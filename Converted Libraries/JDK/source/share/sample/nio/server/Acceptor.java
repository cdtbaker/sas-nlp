import java.io.*;
import java.nio.channels.*;
import javax.net.ssl.*;
/** 
 * A Runnable class which sits in a loop accepting SocketChannels,
 * then registers the Channels with the read/write Selector.
 * @author Mark Reinhold
 * @author Brad R. Wetmore
 */
class Acceptor implements Runnable {
  private ServerSocketChannel ssc;
  private Dispatcher d;
  private SSLContext sslContext;
  Acceptor(  ServerSocketChannel ssc,  Dispatcher d,  SSLContext sslContext){
    this.ssc=ssc;
    this.d=d;
    this.sslContext=sslContext;
  }
  public void run(){
    for (; ; ) {
      try {
        SocketChannel sc=ssc.accept();
        ChannelIO cio=(sslContext != null ? ChannelIOSecure.getInstance(sc,false,sslContext) : ChannelIO.getInstance(sc,false));
        RequestHandler rh=new RequestHandler(cio);
        d.register(cio.getSocketChannel(),SelectionKey.OP_READ,rh);
      }
 catch (      IOException x) {
        x.printStackTrace();
        break;
      }
    }
  }
}
