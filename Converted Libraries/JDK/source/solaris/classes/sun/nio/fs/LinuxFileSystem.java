package sun.nio.fs;
import java.nio.file.*;
import java.io.IOException;
import java.util.*;
import static sun.nio.fs.LinuxNativeDispatcher.*;
/** 
 * Linux implementation of FileSystem
 */
class LinuxFileSystem extends UnixFileSystem {
  LinuxFileSystem(  UnixFileSystemProvider provider,  String dir){
    super(provider,dir);
  }
  @Override public WatchService newWatchService() throws IOException {
    return new LinuxWatchService(this);
  }
private static class SupportedFileFileAttributeViewsHolder {
    static final Set<String> supportedFileAttributeViews=supportedFileAttributeViews();
    private static Set<String> supportedFileAttributeViews(){
      Set<String> result=new HashSet<>();
      result.addAll(standardFileAttributeViews());
      result.add("dos");
      result.add("user");
      return Collections.unmodifiableSet(result);
    }
  }
  @Override public Set<String> supportedFileAttributeViews(){
    return SupportedFileFileAttributeViewsHolder.supportedFileAttributeViews;
  }
  @Override void copyNonPosixAttributes(  int ofd,  int nfd){
    LinuxUserDefinedFileAttributeView.copyExtendedAttributes(ofd,nfd);
  }
  /** 
 * Returns object to iterate over the mount entries in the given fstab file.
 */
  Iterable<UnixMountEntry> getMountEntries(  String fstab){
    ArrayList<UnixMountEntry> entries=new ArrayList<>();
    try {
      long fp=setmntent(fstab.getBytes(),"r".getBytes());
      try {
        for (; ; ) {
          UnixMountEntry entry=new UnixMountEntry();
          int res=getextmntent(fp,entry);
          if (res < 0)           break;
          entries.add(entry);
        }
      }
  finally {
        endmntent(fp);
      }
    }
 catch (    UnixException x) {
    }
    return entries;
  }
  /** 
 * Returns object to iterate over the mount entries in /etc/mtab
 */
  @Override Iterable<UnixMountEntry> getMountEntries(){
    return getMountEntries("/etc/mtab");
  }
  @Override FileStore getFileStore(  UnixMountEntry entry) throws IOException {
    return new LinuxFileStore(this,entry);
  }
}
