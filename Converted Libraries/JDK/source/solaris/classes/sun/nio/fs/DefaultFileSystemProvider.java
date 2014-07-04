package sun.nio.fs;
import java.nio.file.spi.FileSystemProvider;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.security.action.GetPropertyAction;
/** 
 * Creates this platform's default FileSystemProvider.
 */
public class DefaultFileSystemProvider {
  private DefaultFileSystemProvider(){
  }
  @SuppressWarnings("unchecked") private static FileSystemProvider createProvider(  final String cn){
    return AccessController.doPrivileged(new PrivilegedAction<FileSystemProvider>(){
      public FileSystemProvider run(){
        Class<FileSystemProvider> c;
        try {
          c=(Class<FileSystemProvider>)Class.forName(cn,true,null);
        }
 catch (        ClassNotFoundException x) {
          throw new AssertionError(x);
        }
        try {
          return c.newInstance();
        }
 catch (        IllegalAccessException x) {
          throw new AssertionError(x);
        }
catch (        InstantiationException x) {
          throw new AssertionError(x);
        }
      }
    }
);
  }
  /** 
 * Returns the default FileSystemProvider.
 */
  public static FileSystemProvider create(){
    String osname=AccessController.doPrivileged(new GetPropertyAction("os.name"));
    if (osname.equals("SunOS"))     return createProvider("sun.nio.fs.SolarisFileSystemProvider");
    if (osname.equals("Linux"))     return createProvider("sun.nio.fs.LinuxFileSystemProvider");
    throw new AssertionError("Platform not recognized");
  }
}
