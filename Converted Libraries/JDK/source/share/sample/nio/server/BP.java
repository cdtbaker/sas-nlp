import java.nio.channels.*;
import java.util.concurrent.*;
/** 
 * A multi-threaded server which creates a pool of threads for use
 * by the server.  The Thread pool decides how to schedule those threads.
 * @author Mark Reinhold
 * @author Brad R. Wetmore
 */
public class BP extends Server {
  private static final int POOL_MULTIPLE=4;
  BP(  int port,  int backlog,  boolean secure) throws Exception {
    super(port,backlog,secure);
  }
  void runServer() throws Exception {
    ExecutorService xec=Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * POOL_MULTIPLE);
    for (; ; ) {
      SocketChannel sc=ssc.accept();
      ChannelIO cio=(sslContext != null ? ChannelIOSecure.getInstance(sc,true,sslContext) : ChannelIO.getInstance(sc,true));
      RequestServicer svc=new RequestServicer(cio);
      xec.execute(svc);
    }
  }
}
