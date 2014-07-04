package sun.management;
import java.io.File;
import java.io.IOException;
public class FileSystemImpl extends FileSystem {
  public boolean supportsFileSecurity(  File f) throws IOException {
    return isSecuritySupported0(f.getAbsolutePath());
  }
  public boolean isAccessUserOnly(  File f) throws IOException {
    String path=f.getAbsolutePath();
    if (!isSecuritySupported0(path)) {
      throw new UnsupportedOperationException("File system does not support file security");
    }
    return isAccessUserOnly0(path);
  }
  static native void init0();
  static native boolean isSecuritySupported0(  String path) throws IOException ;
  static native boolean isAccessUserOnly0(  String path) throws IOException ;
static {
    java.security.AccessController.doPrivileged(new sun.security.action.LoadLibraryAction("management"));
    init0();
  }
}
