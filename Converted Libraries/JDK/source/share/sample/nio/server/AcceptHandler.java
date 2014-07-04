import java.io.*;
import java.nio.channels.*;
import javax.net.ssl.*;
/** 
 * A single threaded Handler that performs accepts SocketChannels and
 * registers the Channels with the read/write Selector.
 * @author Mark Reinhold
 * @author Brad R. Wetmore
 */
class AcceptHandler implements Handler {
  private ServerSocketChannel channel;
  private Dispatcher dsp;
  private SSLContext sslContext;
  AcceptHandler(  ServerSocketChannel ssc,  Dispatcher dsp,  SSLContext sslContext){
    channel=ssc;
    this.dsp=dsp;
    this.sslContext=sslContext;
  }
  public void handle(  SelectionKey sk) throws IOException {
    if (!sk.isAcceptable())     return;
    SocketChannel sc=channel.accept();
    if (sc == null) {
      return;
    }
    ChannelIO cio=(sslContext != null ? ChannelIOSecure.getInstance(sc,false,sslContext) : ChannelIO.getInstance(sc,false));
    RequestHandler rh=new RequestHandler(cio);
    dsp.register(cio.getSocketChannel(),SelectionKey.OP_READ,rh);
  }
}
