package sun.nio.fs;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.nio.file.spi.*;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;
/** 
 * Base implementation of FileSystem for Unix-like implementations.
 */
abstract class UnixFileSystem extends FileSystem {
  private final UnixFileSystemProvider provider;
  private final byte[] defaultDirectory;
  private final boolean needToResolveAgainstDefaultDirectory;
  private final UnixPath rootDirectory;
  UnixFileSystem(  UnixFileSystemProvider provider,  String dir){
    this.provider=provider;
    this.defaultDirectory=UnixPath.normalizeAndCheck(dir).getBytes();
    if (this.defaultDirectory[0] != '/') {
      throw new RuntimeException("default directory must be absolute");
    }
    String propValue=AccessController.doPrivileged(new GetPropertyAction("sun.nio.fs.chdirAllowed","false"));
    boolean chdirAllowed=(propValue.length() == 0) ? true : Boolean.valueOf(propValue);
    if (chdirAllowed) {
      this.needToResolveAgainstDefaultDirectory=true;
    }
 else {
      byte[] cwd=UnixNativeDispatcher.getcwd();
      boolean defaultIsCwd=(cwd.length == defaultDirectory.length);
      if (defaultIsCwd) {
        for (int i=0; i < cwd.length; i++) {
          if (cwd[i] != defaultDirectory[i]) {
            defaultIsCwd=false;
            break;
          }
        }
      }
      this.needToResolveAgainstDefaultDirectory=!defaultIsCwd;
    }
    this.rootDirectory=new UnixPath(this,"/");
  }
  byte[] defaultDirectory(){
    return defaultDirectory;
  }
  boolean needToResolveAgainstDefaultDirectory(){
    return needToResolveAgainstDefaultDirectory;
  }
  UnixPath rootDirectory(){
    return rootDirectory;
  }
  boolean isSolaris(){
    return false;
  }
  static List<String> standardFileAttributeViews(){
    return Arrays.asList("basic","posix","unix","owner");
  }
  @Override public final FileSystemProvider provider(){
    return provider;
  }
  @Override public final String getSeparator(){
    return "/";
  }
  @Override public final boolean isOpen(){
    return true;
  }
  @Override public final boolean isReadOnly(){
    return false;
  }
  @Override public final void close() throws IOException {
    throw new UnsupportedOperationException();
  }
  /** 
 * Copies non-POSIX attributes from the source to target file.
 * Copying a file preserving attributes, or moving a file, will preserve
 * the file owner/group/permissions/timestamps but it does not preserve
 * other non-POSIX attributes. This method is invoked by the
 * copy or move operation to preserve these attributes. It should copy
 * extended attributes, ACLs, or other attributes.
 * @param sfdOpen file descriptor to source file
 * @param tfdOpen file descriptor to target file
 */
  void copyNonPosixAttributes(  int sfd,  int tfd){
  }
  /** 
 * Unix systems only have a single root directory (/)
 */
  @Override public final Iterable<Path> getRootDirectories(){
    final List<Path> allowedList=Collections.unmodifiableList(Arrays.asList((Path)rootDirectory));
    return new Iterable<Path>(){
      public Iterator<Path> iterator(){
        try {
          SecurityManager sm=System.getSecurityManager();
          if (sm != null)           sm.checkRead(rootDirectory.toString());
          return allowedList.iterator();
        }
 catch (        SecurityException x) {
          List<Path> disallowed=Collections.emptyList();
          return disallowed.iterator();
        }
      }
    }
;
  }
  /** 
 * Returns object to iterate over entries in mounttab or equivalent
 */
  abstract Iterable<UnixMountEntry> getMountEntries();
  /** 
 * Returns a FileStore to represent the file system for the given mount
 * mount.
 */
  abstract FileStore getFileStore(  UnixMountEntry entry) throws IOException ;
  /** 
 * Iterator returned by getFileStores method.
 */
private class FileStoreIterator implements Iterator<FileStore> {
    private final Iterator<UnixMountEntry> entries;
    private FileStore next;
    FileStoreIterator(){
      this.entries=getMountEntries().iterator();
    }
    private FileStore readNext(){
      assert Thread.holdsLock(this);
      for (; ; ) {
        if (!entries.hasNext())         return null;
        UnixMountEntry entry=entries.next();
        if (entry.isIgnored())         continue;
        SecurityManager sm=System.getSecurityManager();
        if (sm != null) {
          try {
            sm.checkRead(new String(entry.dir()));
          }
 catch (          SecurityException x) {
            continue;
          }
        }
        try {
          return getFileStore(entry);
        }
 catch (        IOException ignore) {
        }
      }
    }
    @Override public synchronized boolean hasNext(){
      if (next != null)       return true;
      next=readNext();
      return next != null;
    }
    @Override public synchronized FileStore next(){
      if (next == null)       next=readNext();
      if (next == null) {
        throw new NoSuchElementException();
      }
 else {
        FileStore result=next;
        next=null;
        return result;
      }
    }
    @Override public void remove(){
      throw new UnsupportedOperationException();
    }
  }
  @Override public final Iterable<FileStore> getFileStores(){
    SecurityManager sm=System.getSecurityManager();
    if (sm != null) {
      try {
        sm.checkPermission(new RuntimePermission("getFileStoreAttributes"));
      }
 catch (      SecurityException se) {
        return Collections.emptyList();
      }
    }
    return new Iterable<FileStore>(){
      public Iterator<FileStore> iterator(){
        return new FileStoreIterator();
      }
    }
;
  }
  @Override public final Path getPath(  String first,  String... more){
    String path;
    if (more.length == 0) {
      path=first;
    }
 else {
      StringBuilder sb=new StringBuilder();
      sb.append(first);
      for (      String segment : more) {
        if (segment.length() > 0) {
          if (sb.length() > 0)           sb.append('/');
          sb.append(segment);
        }
      }
      path=sb.toString();
    }
    return new UnixPath(this,path);
  }
  @Override public PathMatcher getPathMatcher(  String syntaxAndInput){
    int pos=syntaxAndInput.indexOf(':');
    if (pos <= 0 || pos == syntaxAndInput.length())     throw new IllegalArgumentException();
    String syntax=syntaxAndInput.substring(0,pos);
    String input=syntaxAndInput.substring(pos + 1);
    String expr;
    if (syntax.equals(GLOB_SYNTAX)) {
      expr=Globs.toUnixRegexPattern(input);
    }
 else {
      if (syntax.equals(REGEX_SYNTAX)) {
        expr=input;
      }
 else {
        throw new UnsupportedOperationException("Syntax '" + syntax + "' not recognized");
      }
    }
    final Pattern pattern=Pattern.compile(expr);
    return new PathMatcher(){
      @Override public boolean matches(      Path path){
        return pattern.matcher(path.toString()).matches();
      }
    }
;
  }
  private static final String GLOB_SYNTAX="glob";
  private static final String REGEX_SYNTAX="regex";
  @Override public final UserPrincipalLookupService getUserPrincipalLookupService(){
    return LookupService.instance;
  }
private static class LookupService {
    static final UserPrincipalLookupService instance=new UserPrincipalLookupService(){
      @Override public UserPrincipal lookupPrincipalByName(      String name) throws IOException {
        return UnixUserPrincipals.lookupUser(name);
      }
      @Override public GroupPrincipal lookupPrincipalByGroupName(      String group) throws IOException {
        return UnixUserPrincipals.lookupGroup(group);
      }
    }
;
  }
}
