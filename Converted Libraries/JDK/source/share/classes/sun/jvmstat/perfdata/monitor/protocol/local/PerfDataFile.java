package sun.jvmstat.perfdata.monitor.protocol.local;
import java.io.File;
import java.io.FilenameFilter;
/** 
 * Class to provide translations from the local Vm Identifier
 * name space into the file system name space and vice-versa.
 * <p>
 * Provides a factory for creating a File object to the backing
 * store file for instrumentation shared memory region for a JVM
 * identified by its Local Java Virtual Machine Identifier, or
 * <em>lvmid</em>.
 * @author Brian Doherty
 * @since 1.5
 * @see java.io.File
 */
public class PerfDataFile {
  private PerfDataFile(){
  }
  /** 
 * The name of the of the system dependent temporary directory
 */
  public static final String tmpDirName;
  /** 
 * The file name prefix for PerfData shared memory files.
 * <p>
 * This prefix must be kept in sync with the prefix used by the JVM.
 */
  public static final String dirNamePrefix="hsperfdata_";
  /** 
 * The directory name pattern for the user directories.
 */
  public static final String userDirNamePattern="hsperfdata_\\S*";
  /** 
 * The file name pattern for PerfData shared memory files.
 * <p>
 * This pattern must be kept in synch with the file name pattern
 * used by the 1.4.2 and later HotSpot JVM.
 */
  public static final String fileNamePattern="^[0-9]+$";
  /** 
 * The file name pattern for 1.4.1 PerfData shared memory files.
 * <p>
 * This pattern must be kept in synch with the file name pattern
 * used by the 1.4.1 HotSpot JVM.
 */
  public static final String tmpFileNamePattern="^hsperfdata_[0-9]+(_[1-2]+)?$";
  /** 
 * Get a File object for the instrumentation backing store file
 * for the JVM identified by the given local Vm Identifier.
 * <p>
 * This method looks for the most up to date backing store file for
 * the given <tt>lvmid</tt>. It will search all the user specific
 * directories in the temporary directory for the host operating
 * system, which may be influenced by platform specific environment
 * variables.
 * @param lvmid  the local Java Virtual Machine Identifier for the target
 * @return File - a File object to the backing store file for the named
 * shared memory region of the target JVM.
 * @see java.io.File
 * @see #getTempDirectory()
 */
  public static File getFile(  int lvmid){
    if (lvmid == 0) {
      return null;
    }
    File tmpDir=new File(tmpDirName);
    String[] files=tmpDir.list(new FilenameFilter(){
      public boolean accept(      File dir,      String name){
        if (!name.startsWith(dirNamePrefix)) {
          return false;
        }
        File candidate=new File(dir,name);
        return ((candidate.isDirectory() || candidate.isFile()) && candidate.canRead());
      }
    }
);
    long newestTime=0;
    File newest=null;
    for (int i=0; i < files.length; i++) {
      File f=new File(tmpDirName + files[i]);
      File candidate=null;
      if (f.exists() && f.isDirectory()) {
        String name=Integer.toString(lvmid);
        candidate=new File(f.getName(),name);
      }
 else       if (f.exists() && f.isFile()) {
        candidate=f;
      }
 else {
        candidate=f;
      }
      if (candidate.exists() && candidate.isFile() && candidate.canRead()) {
        long modTime=candidate.lastModified();
        if (modTime >= newestTime) {
          newestTime=modTime;
          newest=candidate;
        }
      }
    }
    return newest;
  }
  /** 
 * Return the File object for the backing store file for the specified Java
 * Virtual Machine.
 * <p>
 * This method looks for the most up to date backing store file for
 * the JVM identified by the given user name and lvmid. The directory
 * searched is the temporary directory for the host operating system,
 * which may be influenced by environment variables.
 * @param user   the user name
 * @param lvmid  the local Java Virtual Machine Identifier for the target
 * @return File - a File object to the backing store file for the named
 * shared memory region of the target JVM.
 * @see java.io.File
 * @see #getTempDirectory()
 */
  public static File getFile(  String user,  int lvmid){
    if (lvmid == 0) {
      return null;
    }
    String basename=getTempDirectory(user) + Integer.toString(lvmid);
    File f=new File(basename);
    if (f.exists() && f.isFile() && f.canRead()) {
      return f;
    }
    long newestTime=0;
    File newest=null;
    for (int i=0; i < 2; i++) {
      if (i == 0) {
        basename=getTempDirectory() + Integer.toString(lvmid);
      }
 else {
        basename=getTempDirectory() + Integer.toString(lvmid) + Integer.toString(i);
      }
      f=new File(basename);
      if (f.exists() && f.isFile() && f.canRead()) {
        long modTime=f.lastModified();
        if (modTime >= newestTime) {
          newestTime=modTime;
          newest=f;
        }
      }
    }
    return newest;
  }
  /** 
 * Method to extract a local Java Virtual Machine Identifier from the
 * file name of the given File object.
 * @param file A File object representing the name of a
 * shared memory region for a target JVM
 * @return int - the local Java Virtual Machine Identifier for the target
 * associated with the file
 * @throws java.lang.IllegalArgumentException Thrown if the file name
 * does not conform to the expected pattern
 */
  public static int getLocalVmId(  File file){
    int lvmid=0;
    try {
      return Integer.parseInt(file.getName());
    }
 catch (    NumberFormatException e) {
    }
    String name=file.getName();
    if (name.startsWith(dirNamePrefix)) {
      int first=name.indexOf('_');
      int last=name.lastIndexOf('_');
      try {
        if (first == last) {
          return Integer.parseInt(name.substring(first + 1));
        }
 else {
          return Integer.parseInt(name.substring(first + 1,last));
        }
      }
 catch (      NumberFormatException e) {
      }
    }
    throw new IllegalArgumentException("file name does not match pattern");
  }
  /** 
 * Return the name of the temporary directory being searched for
 * HotSpot PerfData backing store files.
 * <p>
 * This method generally returns the value of the java.io.tmpdir
 * property. However, on some platforms it may return a different
 * directory, as the JVM implementation may store the PerfData backing
 * store files in a different directory for performance reasons.
 * @return String - the name of the temporary directory.
 */
  public static String getTempDirectory(){
    return tmpDirName;
  }
  /** 
 * Return the name of the temporary directory to be searched
 * for HotSpot PerfData backing store files for a given user.
 * <p>
 * This method generally returns the name of a subdirectory of
 * the directory indicated in the java.io.tmpdir property. However,
 * on some platforms it may return a different directory, as the
 * JVM implementation may store the PerfData backing store files
 * in a different directory for performance reasons.
 * @return String - the name of the temporary directory.
 */
  public static String getTempDirectory(  String user){
    return tmpDirName + dirNamePrefix + user+ File.separator;
  }
static {
    String tmpdir=System.getProperty("java.io.tmpdir");
    if (tmpdir.compareTo("/var/tmp/") == 0) {
      tmpdir="/tmp/";
    }
    if (tmpdir.lastIndexOf(File.separator) != (tmpdir.length() - 1)) {
      tmpdir=tmpdir + File.separator;
    }
    tmpDirName=tmpdir;
  }
}
