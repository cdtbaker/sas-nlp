package sun.security.ssl;
import java.io.InputStream;
import java.io.IOException;
import java.security.MessageDigest;
import javax.net.ssl.SSLException;
/** 
 * InputStream for handshake data, used internally only. Contains the
 * handshake message buffer and methods to parse them.
 * Once a new handshake record arrives, it is buffered in this class until
 * processed by the Handshaker. The buffer may also contain incomplete
 * handshake messages in case the message is split across multiple records.
 * Handshaker.process_record deals with all that. It may also contain
 * handshake messages larger than the default buffer size (e.g. large
 * certificate messages). The buffer is grown dynamically to handle that
 * (see InputRecord.queueHandshake()).
 * Note that the InputRecord used as a buffer here is separate from the
 * AppInStream.r, which is where data from the socket is initially read
 * into. This is because once the initial handshake has been completed,
 * handshake and application data messages may be interleaved arbitrarily
 * and must be processed independently.
 * @author David Brownell
 */
public class HandshakeInStream extends InputStream {
  InputRecord r;
  HandshakeInStream(  HandshakeHash handshakeHash){
    r=new InputRecord();
    r.setHandshakeHash(handshakeHash);
  }
  public int available(){
    return r.available();
  }
  public int read() throws IOException {
    int n=r.read();
    if (n == -1) {
      throw new SSLException("Unexpected end of handshake data");
    }
    return n;
  }
  public int read(  byte b[],  int off,  int len) throws IOException {
    int n=r.read(b,off,len);
    if (n != len) {
      throw new SSLException("Unexpected end of handshake data");
    }
    return n;
  }
  public long skip(  long n) throws IOException {
    return r.skip(n);
  }
  public void mark(  int readlimit){
    r.mark(readlimit);
  }
  public void reset(){
    r.reset();
  }
  public boolean markSupported(){
    return true;
  }
  void incomingRecord(  InputRecord in) throws IOException {
    r.queueHandshake(in);
  }
  void digestNow(){
    r.doHashes();
  }
  void ignore(  int n){
    r.ignore(n);
  }
  int getInt8() throws IOException {
    return read();
  }
  int getInt16() throws IOException {
    return (getInt8() << 8) | getInt8();
  }
  int getInt24() throws IOException {
    return (getInt8() << 16) | (getInt8() << 8) | getInt8();
  }
  int getInt32() throws IOException {
    return (getInt8() << 24) | (getInt8() << 16) | (getInt8() << 8)| getInt8();
  }
  byte[] getBytes8() throws IOException {
    int len=getInt8();
    byte b[]=new byte[len];
    read(b,0,len);
    return b;
  }
  public byte[] getBytes16() throws IOException {
    int len=getInt16();
    byte b[]=new byte[len];
    read(b,0,len);
    return b;
  }
  byte[] getBytes24() throws IOException {
    int len=getInt24();
    byte b[]=new byte[len];
    read(b,0,len);
    return b;
  }
}
