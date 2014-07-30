package sun.security.ssl;
import java.io.*;
/** 
 * InputStream for application data as returned by SSLSocket.getInputStream().
 * It uses an InputRecord as internal buffer that is refilled on demand
 * whenever it runs out of data.
 * @author David Brownell
 */
class AppInputStream extends InputStream {
  private final static byte[] SKIP_ARRAY=new byte[1024];
  private SSLSocketImpl c;
  InputRecord r;
  private final byte[] oneByte=new byte[1];
  AppInputStream(  SSLSocketImpl conn){
    r=new InputRecord();
    c=conn;
  }
  /** 
 * Return the minimum number of bytes that can be read without blocking.
 * Currently not synchronized.
 */
  public int available() throws IOException {
    if (c.checkEOF() || (r.isAppDataValid() == false)) {
      return 0;
    }
    return r.available();
  }
  /** 
 * Read a single byte, returning -1 on non-fault EOF status.
 */
  public synchronized int read() throws IOException {
    int n=read(oneByte,0,1);
    if (n <= 0) {
      return -1;
    }
    return oneByte[0] & 0xff;
  }
  /** 
 * Read up to "len" bytes into this buffer, starting at "off".
 * If the layer above needs more data, it asks for more, so we
 * are responsible only for blocking to fill at most one buffer,
 * and returning "-1" on non-fault EOF status.
 */
  public synchronized int read(  byte b[],  int off,  int len) throws IOException {
    if (b == null) {
      throw new NullPointerException();
    }
 else     if (off < 0 || len < 0 || len > b.length - off) {
      throw new IndexOutOfBoundsException();
    }
 else     if (len == 0) {
      return 0;
    }
    if (c.checkEOF()) {
      return -1;
    }
    try {
      while (r.available() == 0) {
        c.readDataRecord(r);
        if (c.checkEOF()) {
          return -1;
        }
      }
      int howmany=Math.min(len,r.available());
      howmany=r.read(b,off,howmany);
      return howmany;
    }
 catch (    Exception e) {
      c.handleException(e);
      return -1;
    }
  }
  /** 
 * Skip n bytes. This implementation is somewhat less efficient
 * than possible, but not badly so (redundant copy). We reuse
 * the read() code to keep things simpler. Note that SKIP_ARRAY
 * is static and may garbled by concurrent use, but we are not interested
 * in the data anyway.
 */
  public synchronized long skip(  long n) throws IOException {
    long skipped=0;
    while (n > 0) {
      int len=(int)Math.min(n,SKIP_ARRAY.length);
      int r=read(SKIP_ARRAY,0,len);
      if (r <= 0) {
        break;
      }
      n-=r;
      skipped+=r;
    }
    return skipped;
  }
  public void close() throws IOException {
    c.close();
  }
}