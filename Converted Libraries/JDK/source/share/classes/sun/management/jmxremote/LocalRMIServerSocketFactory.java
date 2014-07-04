package sun.management.jmxremote;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.rmi.server.RMIServerSocketFactory;
import java.util.Enumeration;
/** 
 * This RMI server socket factory creates server sockets that
 * will only accept connection requests from clients running
 * on the host where the RMI remote objects have been exported.
 */
public final class LocalRMIServerSocketFactory implements RMIServerSocketFactory {
  /** 
 * Creates a server socket that only accepts connection requests from
 * clients running on the host where the RMI remote objects have been
 * exported.
 */
  public ServerSocket createServerSocket(  int port) throws IOException {
    return new ServerSocket(port){
      @Override public Socket accept() throws IOException {
        final Socket socket=super.accept();
        final InetAddress remoteAddr=socket.getInetAddress();
        final String msg="The server sockets created using the " + "LocalRMIServerSocketFactory only accept connections " + "from clients running on the host where the RMI "+ "remote objects have been exported.";
        if (remoteAddr == null) {
          String details="";
          if (socket.isClosed()) {
            details=" Socket is closed.";
          }
 else           if (!socket.isConnected()) {
            details=" Socket is not connected";
          }
          try {
            socket.close();
          }
 catch (          Exception ok) {
          }
          throw new IOException(msg + " Couldn't determine client address." + details);
        }
 else         if (remoteAddr.isLoopbackAddress()) {
          return socket;
        }
        Enumeration<NetworkInterface> nis;
        try {
          nis=NetworkInterface.getNetworkInterfaces();
        }
 catch (        SocketException e) {
          try {
            socket.close();
          }
 catch (          IOException ioe) {
          }
          throw new IOException(msg,e);
        }
        while (nis.hasMoreElements()) {
          NetworkInterface ni=nis.nextElement();
          Enumeration<InetAddress> addrs=ni.getInetAddresses();
          while (addrs.hasMoreElements()) {
            InetAddress localAddr=addrs.nextElement();
            if (localAddr.equals(remoteAddr)) {
              return socket;
            }
          }
        }
        try {
          socket.close();
        }
 catch (        IOException ioe) {
        }
        throw new IOException(msg);
      }
    }
;
  }
  /** 
 * Two LocalRMIServerSocketFactory objects
 * are equal if they are of the same type.
 */
  @Override public boolean equals(  Object obj){
    return (obj instanceof LocalRMIServerSocketFactory);
  }
  /** 
 * Returns a hash code value for this LocalRMIServerSocketFactory.
 */
  @Override public int hashCode(){
    return getClass().hashCode();
  }
}
