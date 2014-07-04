import java.io.*;
import java.nio.channels.*;
/** 
 * A Blocking/Multi-threaded Server which creates a new thread for each
 * connection.  This is not efficient for large numbers of connections.
 * @author Mark Reinhold
 * @author Brad R. Wetmore
 */
public class BN extends Server {
  BN(  int port,  int backlog,  boolean secure) throws Exception {
    super(port,backlog,secure);
  }
  void runServer() throws IOException {
    for (; ; ) {
      SocketChannel sc=ssc.accept();
      ChannelIO cio=(sslContext != null ? ChannelIOSecure.getInstance(sc,true,sslContext) : ChannelIO.getInstance(sc,true));
      RequestServicer svc=new RequestServicer(cio);
      Thread th=new Thread(svc);
      th.start();
    }
  }
}
