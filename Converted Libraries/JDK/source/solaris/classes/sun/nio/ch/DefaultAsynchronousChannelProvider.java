package sun.nio.ch;
import java.nio.channels.spi.AsynchronousChannelProvider;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;
/** 
 * Creates this platform's default asynchronous channel provider
 */
public class DefaultAsynchronousChannelProvider {
  /** 
 * Prevent instantiation.
 */
  private DefaultAsynchronousChannelProvider(){
  }
  /** 
 * Returns the default AsynchronousChannelProvider.
 */
  public static AsynchronousChannelProvider create(){
    String osname=AccessController.doPrivileged(new GetPropertyAction("os.name"));
    if (osname.equals("SunOS"))     return new SolarisAsynchronousChannelProvider();
    if (osname.equals("Linux"))     return new LinuxAsynchronousChannelProvider();
    throw new InternalError("platform not recognized");
  }
}
