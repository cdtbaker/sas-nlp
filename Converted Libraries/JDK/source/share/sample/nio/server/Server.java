import java.io.*;
import java.net.*;
import java.nio.channels.*;
import java.security.*;
import javax.net.ssl.*;
/** 
 * The main server base class.
 * <P>
 * This class is responsible for setting up most of the server state
 * before the actual server subclasses take over.
 * @author Mark Reinhold
 * @author Brad R. Wetmore
 */
public abstract class Server {
  ServerSocketChannel ssc;
  SSLContext sslContext=null;
  static private int PORT=8000;
  static private int BACKLOG=1024;
  static private boolean SECURE=false;
  Server(  int port,  int backlog,  boolean secure) throws Exception {
    if (secure) {
      createSSLContext();
    }
    ssc=ServerSocketChannel.open();
    ssc.socket().setReuseAddress(true);
    ssc.socket().bind(new InetSocketAddress(port),backlog);
  }
  private void createSSLContext() throws Exception {
    char[] passphrase="passphrase".toCharArray();
    KeyStore ks=KeyStore.getInstance("JKS");
    ks.load(new FileInputStream("testkeys"),passphrase);
    KeyManagerFactory kmf=KeyManagerFactory.getInstance("SunX509");
    kmf.init(ks,passphrase);
    TrustManagerFactory tmf=TrustManagerFactory.getInstance("SunX509");
    tmf.init(ks);
    sslContext=SSLContext.getInstance("TLS");
    sslContext.init(kmf.getKeyManagers(),tmf.getTrustManagers(),null);
  }
  abstract void runServer() throws Exception ;
  static private void usage(){
    System.out.println("Usage:  Server <type> [options]\n" + "     type:\n" + "             B1      Blocking/Single-threaded Server\n"+ "             BN      Blocking/Multi-threaded Server\n"+ "             BP      Blocking/Pooled-Thread Server\n"+ "             N1      Nonblocking/Single-threaded Server\n"+ "             N2      Nonblocking/Dual-threaded Server\n"+ "\n"+ "     options:\n"+ "             -port port              port number\n"+ "                 default:  " + PORT + "\n"+ "             -backlog backlog        backlog\n"+ "                 default:  "+ BACKLOG+ "\n"+ "             -secure                 encrypt with SSL/TLS");
    System.exit(1);
  }
  static private Server createServer(  String args[]) throws Exception {
    if (args.length < 1) {
      usage();
    }
    int port=PORT;
    int backlog=BACKLOG;
    boolean secure=SECURE;
    for (int i=1; i < args.length; i++) {
      if (args[i].equals("-port")) {
        checkArgs(i,args.length);
        port=Integer.valueOf(args[++i]);
      }
 else       if (args[i].equals("-backlog")) {
        checkArgs(i,args.length);
        backlog=Integer.valueOf(args[++i]);
      }
 else       if (args[i].equals("-secure")) {
        secure=true;
      }
 else {
        usage();
      }
    }
    Server server=null;
    if (args[0].equals("B1")) {
      server=new B1(port,backlog,secure);
    }
 else     if (args[0].equals("BN")) {
      server=new BN(port,backlog,secure);
    }
 else     if (args[0].equals("BP")) {
      server=new BP(port,backlog,secure);
    }
 else     if (args[0].equals("N1")) {
      server=new N1(port,backlog,secure);
    }
 else     if (args[0].equals("N2")) {
      server=new N2(port,backlog,secure);
    }
    return server;
  }
  static private void checkArgs(  int i,  int len){
    if ((i + 1) >= len) {
      usage();
    }
  }
  static public void main(  String args[]) throws Exception {
    Server server=createServer(args);
    if (server == null) {
      usage();
    }
    System.out.println("Server started.");
    server.runServer();
  }
}
