import java.nio.channels.*;
/** 
 * A non-blocking/single-threaded server.  All accept() and
 * read()/write() operations are performed by a single thread, but only
 * after being selected for those operations by a Selector.
 * @author Mark Reinhold
 * @author Brad R. Wetmore
 */
public class N1 extends Server {
  N1(  int port,  int backlog,  boolean secure) throws Exception {
    super(port,backlog,secure);
    ssc.configureBlocking(false);
  }
  void runServer() throws Exception {
    Dispatcher d=new Dispatcher1();
    d.register(ssc,SelectionKey.OP_ACCEPT,new AcceptHandler(ssc,d,sslContext));
    d.run();
  }
}
