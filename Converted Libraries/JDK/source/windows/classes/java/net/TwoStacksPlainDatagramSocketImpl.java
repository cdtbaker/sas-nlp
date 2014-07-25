package java.net;
import java.io.IOException;
import java.io.FileDescriptor;
import sun.net.ResourceManager;
/** 
 * This class defines the plain DatagramSocketImpl that is used for all
 * Windows versions lower than Vista. It adds support for IPv6 on
 * these platforms where available.
 * For backward compatibility windows platforms that do not have IPv6
 * support also use this implementation, and fd1 gets set to null
 * during socket creation.
 * @author Chris Hegarty
 */
class TwoStacksPlainDatagramSocketImpl extends AbstractPlainDatagramSocketImpl {
  private FileDescriptor fd1;
  private InetAddress anyLocalBoundAddr=null;
  private int fduse=-1;
  private int lastfd=-1;
static {
    init();
  }
  protected synchronized void create() throws SocketException {
    fd1=new FileDescriptor();
    super.create();
  }
  protected synchronized void bind(  int lport,  InetAddress laddr) throws SocketException {
    super.bind(lport,laddr);
    if (laddr.isAnyLocalAddress()) {
      anyLocalBoundAddr=laddr;
    }
  }
  protected synchronized void receive(  DatagramPacket p) throws IOException {
    try {
      receive0(p);
    }
  finally {
      fduse=-1;
    }
  }
  public Object getOption(  int optID) throws SocketException {
    if (isClosed()) {
      throw new SocketException("Socket Closed");
    }
    if (optID == SO_BINDADDR) {
      if (fd != null && fd1 != null) {
        return anyLocalBoundAddr;
      }
      return socketGetOption(optID);
    }
 else     return super.getOption(optID);
  }
  protected boolean isClosed(){
    return (fd == null && fd1 == null) ? true : false;
  }
  protected void close(){
    if (fd != null || fd1 != null) {
      datagramSocketClose();
      ResourceManager.afterUdpClose();
      fd=null;
      fd1=null;
    }
  }
  protected synchronized native void bind0(  int lport,  InetAddress laddr) throws SocketException ;
  protected native void send(  DatagramPacket p) throws IOException ;
  protected synchronized native int peek(  InetAddress i) throws IOException ;
  protected synchronized native int peekData(  DatagramPacket p) throws IOException ;
  protected synchronized native void receive0(  DatagramPacket p) throws IOException ;
  protected native void setTimeToLive(  int ttl) throws IOException ;
  protected native int getTimeToLive() throws IOException ;
  protected native void setTTL(  byte ttl) throws IOException ;
  protected native byte getTTL() throws IOException ;
  protected native void join(  InetAddress inetaddr,  NetworkInterface netIf) throws IOException ;
  protected native void leave(  InetAddress inetaddr,  NetworkInterface netIf) throws IOException ;
  protected native void datagramSocketCreate() throws SocketException ;
  protected native void datagramSocketClose();
  protected native void socketSetOption(  int opt,  Object val) throws SocketException ;
  protected native Object socketGetOption(  int opt) throws SocketException ;
  protected native void connect0(  InetAddress address,  int port) throws SocketException ;
  protected native void disconnect0(  int family);
  /** 
 * Perform class load-time initializations.
 */
  private native static void init();
}
