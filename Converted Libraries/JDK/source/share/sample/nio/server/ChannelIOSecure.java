import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import javax.net.ssl.*;
import javax.net.ssl.SSLEngineResult.*;
/** 
 * A helper class which performs I/O using the SSLEngine API.
 * <P>
 * Each connection has a SocketChannel and a SSLEngine that is
 * used through the lifetime of the Channel.  We allocate byte buffers
 * for use as the outbound and inbound network buffers.
 * <PRE>
 * Application Data
 * src      requestBB
 * |           ^
 * |     |     |
 * v     |     |
 * +----+-----|-----+----+
 * |          |          |
 * |       SSL|Engine    |
 * wrap()  |          |          |  unwrap()
 * | OUTBOUND | INBOUND  |
 * |          |          |
 * +----+-----|-----+----+
 * |     |     ^
 * |     |     |
 * v           |
 * outNetBB     inNetBB
 * Net data
 * </PRE>
 * These buffers handle all of the intermediary data for the SSL
 * connection.  To make things easy, we'll require outNetBB be
 * completely flushed before trying to wrap any more data, but we
 * could certainly remove that restriction by using larger buffers.
 * <P>
 * There are many, many ways to handle compute and I/O strategies.
 * What follows is a relatively simple one.  The reader is encouraged
 * to develop the strategy that best fits the application.
 * <P>
 * In most of the non-blocking operations in this class, we let the
 * Selector tell us when we're ready to attempt an I/O operation (by the
 * application repeatedly calling our methods).  Another option would be
 * to attempt the operation and return from the method when no forward
 * progress can be made.
 * <P>
 * There's lots of room for enhancements and improvement in this example.
 * <P>
 * We're checking for SSL/TLS end-of-stream truncation attacks via
 * sslEngine.closeInbound().  When you reach the end of a input stream
 * via a read() returning -1 or an IOException, we call
 * sslEngine.closeInbound() to signal to the sslEngine that no more
 * input will be available.  If the peer's close_notify message has not
 * yet been received, this could indicate a trucation attack, in which
 * an attacker is trying to prematurely close the connection.   The
 * closeInbound() will throw an exception if this condition were
 * present.
 * @author Brad R. Wetmore
 * @author Mark Reinhold
 */
class ChannelIOSecure extends ChannelIO {
  private SSLEngine sslEngine=null;
  private int appBBSize;
  private int netBBSize;
  private ByteBuffer inNetBB;
  private ByteBuffer outNetBB;
  private static ByteBuffer hsBB=ByteBuffer.allocate(0);
  private ByteBuffer fileChannelBB=null;
  private HandshakeStatus initialHSStatus;
  private boolean initialHSComplete;
  private boolean shutdown=false;
  protected ChannelIOSecure(  SocketChannel sc,  boolean blocking,  SSLContext sslc) throws IOException {
    super(sc,blocking);
    sslEngine=sslc.createSSLEngine();
    sslEngine.setUseClientMode(false);
    initialHSStatus=HandshakeStatus.NEED_UNWRAP;
    initialHSComplete=false;
    netBBSize=sslEngine.getSession().getPacketBufferSize();
    inNetBB=ByteBuffer.allocate(netBBSize);
    outNetBB=ByteBuffer.allocate(netBBSize);
    outNetBB.position(0);
    outNetBB.limit(0);
  }
  static ChannelIOSecure getInstance(  SocketChannel sc,  boolean blocking,  SSLContext sslc) throws IOException {
    ChannelIOSecure cio=new ChannelIOSecure(sc,blocking,sslc);
    cio.appBBSize=cio.sslEngine.getSession().getApplicationBufferSize();
    cio.requestBB=ByteBuffer.allocate(cio.appBBSize);
    return cio;
  }
  protected void resizeRequestBB(){
    resizeRequestBB(appBBSize);
  }
  private void resizeResponseBB(){
    ByteBuffer bb=ByteBuffer.allocate(netBBSize);
    inNetBB.flip();
    bb.put(inNetBB);
    inNetBB=bb;
  }
  private boolean tryFlush(  ByteBuffer bb) throws IOException {
    super.write(bb);
    return !bb.hasRemaining();
  }
  boolean doHandshake() throws IOException {
    return doHandshake(null);
  }
  boolean doHandshake(  SelectionKey sk) throws IOException {
    SSLEngineResult result;
    if (initialHSComplete) {
      return initialHSComplete;
    }
    if (outNetBB.hasRemaining()) {
      if (!tryFlush(outNetBB)) {
        return false;
      }
switch (initialHSStatus) {
case FINISHED:
        initialHSComplete=true;
case NEED_UNWRAP:
      if (sk != null) {
        sk.interestOps(SelectionKey.OP_READ);
      }
    break;
}
return initialHSComplete;
}
switch (initialHSStatus) {
case NEED_UNWRAP:
if (sc.read(inNetBB) == -1) {
  sslEngine.closeInbound();
  return initialHSComplete;
}
needIO: while (initialHSStatus == HandshakeStatus.NEED_UNWRAP) {
resizeRequestBB();
inNetBB.flip();
result=sslEngine.unwrap(inNetBB,requestBB);
inNetBB.compact();
initialHSStatus=result.getHandshakeStatus();
switch (result.getStatus()) {
case OK:
switch (initialHSStatus) {
case NOT_HANDSHAKING:
    throw new IOException("Not handshaking during initial handshake");
case NEED_TASK:
  initialHSStatus=doTasks();
break;
case FINISHED:
initialHSComplete=true;
break needIO;
}
break;
case BUFFER_UNDERFLOW:
netBBSize=sslEngine.getSession().getPacketBufferSize();
if (netBBSize > inNetBB.capacity()) {
resizeResponseBB();
}
if (sk != null) {
sk.interestOps(SelectionKey.OP_READ);
}
break needIO;
case BUFFER_OVERFLOW:
appBBSize=sslEngine.getSession().getApplicationBufferSize();
break;
default :
throw new IOException("Received" + result.getStatus() + "during initial handshaking");
}
}
if (initialHSStatus != HandshakeStatus.NEED_WRAP) {
break;
}
case NEED_WRAP:
outNetBB.clear();
result=sslEngine.wrap(hsBB,outNetBB);
outNetBB.flip();
initialHSStatus=result.getHandshakeStatus();
switch (result.getStatus()) {
case OK:
if (initialHSStatus == HandshakeStatus.NEED_TASK) {
initialHSStatus=doTasks();
}
if (sk != null) {
sk.interestOps(SelectionKey.OP_WRITE);
}
break;
default :
throw new IOException("Received" + result.getStatus() + "during initial handshaking");
}
break;
default :
throw new RuntimeException("Invalid Handshaking State" + initialHSStatus);
}
return initialHSComplete;
}
private SSLEngineResult.HandshakeStatus doTasks(){
Runnable runnable;
while ((runnable=sslEngine.getDelegatedTask()) != null) {
runnable.run();
}
return sslEngine.getHandshakeStatus();
}
int read() throws IOException {
SSLEngineResult result;
if (!initialHSComplete) {
throw new IllegalStateException();
}
int pos=requestBB.position();
if (sc.read(inNetBB) == -1) {
sslEngine.closeInbound();
return -1;
}
do {
resizeRequestBB();
inNetBB.flip();
result=sslEngine.unwrap(inNetBB,requestBB);
inNetBB.compact();
switch (result.getStatus()) {
case BUFFER_OVERFLOW:
appBBSize=sslEngine.getSession().getApplicationBufferSize();
break;
case BUFFER_UNDERFLOW:
netBBSize=sslEngine.getSession().getPacketBufferSize();
if (netBBSize > inNetBB.capacity()) {
resizeResponseBB();
break;
}
case OK:
if (result.getHandshakeStatus() == HandshakeStatus.NEED_TASK) {
doTasks();
}
break;
default :
throw new IOException("sslEngine error during data read: " + result.getStatus());
}
}
 while ((inNetBB.position() != 0) && result.getStatus() != Status.BUFFER_UNDERFLOW);
return (requestBB.position() - pos);
}
int write(ByteBuffer src) throws IOException {
if (!initialHSComplete) {
throw new IllegalStateException();
}
return doWrite(src);
}
private int doWrite(ByteBuffer src) throws IOException {
int retValue=0;
if (outNetBB.hasRemaining() && !tryFlush(outNetBB)) {
return retValue;
}
outNetBB.clear();
SSLEngineResult result=sslEngine.wrap(src,outNetBB);
retValue=result.bytesConsumed();
outNetBB.flip();
switch (result.getStatus()) {
case OK:
if (result.getHandshakeStatus() == HandshakeStatus.NEED_TASK) {
doTasks();
}
break;
default :
throw new IOException("sslEngine error during data write: " + result.getStatus());
}
if (outNetBB.hasRemaining()) {
tryFlush(outNetBB);
}
return retValue;
}
long transferTo(FileChannel fc,long pos,long len) throws IOException {
if (!initialHSComplete) {
throw new IllegalStateException();
}
if (fileChannelBB == null) {
fileChannelBB=ByteBuffer.allocate(appBBSize);
fileChannelBB.limit(0);
}
fileChannelBB.compact();
int fileRead=fc.read(fileChannelBB);
fileChannelBB.flip();
doWrite(fileChannelBB);
return fileRead;
}
boolean dataFlush() throws IOException {
boolean fileFlushed=true;
if ((fileChannelBB != null) && fileChannelBB.hasRemaining()) {
doWrite(fileChannelBB);
fileFlushed=!fileChannelBB.hasRemaining();
}
 else if (outNetBB.hasRemaining()) {
tryFlush(outNetBB);
}
return (fileFlushed && !outNetBB.hasRemaining());
}
boolean shutdown() throws IOException {
if (!shutdown) {
sslEngine.closeOutbound();
shutdown=true;
}
if (outNetBB.hasRemaining() && tryFlush(outNetBB)) {
return false;
}
outNetBB.clear();
SSLEngineResult result=sslEngine.wrap(hsBB,outNetBB);
if (result.getStatus() != Status.CLOSED) {
throw new SSLException("Improper close state");
}
outNetBB.flip();
if (outNetBB.hasRemaining()) {
tryFlush(outNetBB);
}
return (!outNetBB.hasRemaining() && (result.getHandshakeStatus() != HandshakeStatus.NEED_WRAP));
}
}
