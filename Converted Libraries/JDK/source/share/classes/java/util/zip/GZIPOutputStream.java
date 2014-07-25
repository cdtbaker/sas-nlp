package java.util.zip;
import java.io.OutputStream;
import java.io.IOException;
/** 
 * This class implements a stream filter for writing compressed data in
 * the GZIP file format.
 * @author      David Connelly
 */
public class GZIPOutputStream extends DeflaterOutputStream {
  /** 
 * CRC-32 of uncompressed data.
 */
  protected CRC32 crc=new CRC32();
  private final static int GZIP_MAGIC=0x8b1f;
  private final static int TRAILER_SIZE=8;
  /** 
 * Creates a new output stream with the specified buffer size.
 * <p>The new output stream instance is created as if by invoking
 * the 3-argument constructor GZIPOutputStream(out, size, false).
 * @param out the output stream
 * @param size the output buffer size
 * @exception IOException If an I/O error has occurred.
 * @exception IllegalArgumentException if size is <= 0
 */
  public GZIPOutputStream(  OutputStream out,  int size) throws IOException {
    this(out,size,false);
  }
  /** 
 * Creates a new output stream with the specified buffer size and
 * flush mode.
 * @param out the output stream
 * @param size the output buffer size
 * @param syncFlushif {@code true} invocation of the inherited{@link DeflaterOutputStream#flush() flush()} method of
 * this instance flushes the compressor with flush mode{@link Deflater#SYNC_FLUSH} before flushing the output
 * stream, otherwise only flushes the output stream
 * @exception IOException If an I/O error has occurred.
 * @exception IllegalArgumentException if size is <= 0
 * @since 1.7
 */
  public GZIPOutputStream(  OutputStream out,  int size,  boolean syncFlush) throws IOException {
    super(out,new Deflater(Deflater.DEFAULT_COMPRESSION,true),size,syncFlush);
    usesDefaultDeflater=true;
    writeHeader();
    crc.reset();
  }
  /** 
 * Creates a new output stream with a default buffer size.
 * <p>The new output stream instance is created as if by invoking
 * the 2-argument constructor GZIPOutputStream(out, false).
 * @param out the output stream
 * @exception IOException If an I/O error has occurred.
 */
  public GZIPOutputStream(  OutputStream out) throws IOException {
    this(out,512,false);
  }
  /** 
 * Creates a new output stream with a default buffer size and
 * the specified flush mode.
 * @param out the output stream
 * @param syncFlushif {@code true} invocation of the inherited{@link DeflaterOutputStream#flush() flush()} method of
 * this instance flushes the compressor with flush mode{@link Deflater#SYNC_FLUSH} before flushing the output
 * stream, otherwise only flushes the output stream
 * @exception IOException If an I/O error has occurred.
 * @since 1.7
 */
  public GZIPOutputStream(  OutputStream out,  boolean syncFlush) throws IOException {
    this(out,512,syncFlush);
  }
  /** 
 * Writes array of bytes to the compressed output stream. This method
 * will block until all the bytes are written.
 * @param buf the data to be written
 * @param off the start offset of the data
 * @param len the length of the data
 * @exception IOException If an I/O error has occurred.
 */
  public synchronized void write(  byte[] buf,  int off,  int len) throws IOException {
    super.write(buf,off,len);
    crc.update(buf,off,len);
  }
  /** 
 * Finishes writing compressed data to the output stream without closing
 * the underlying stream. Use this method when applying multiple filters
 * in succession to the same output stream.
 * @exception IOException if an I/O error has occurred
 */
  public void finish() throws IOException {
    if (!def.finished()) {
      def.finish();
      while (!def.finished()) {
        int len=def.deflate(buf,0,buf.length);
        if (def.finished() && len <= buf.length - TRAILER_SIZE) {
          writeTrailer(buf,len);
          len=len + TRAILER_SIZE;
          out.write(buf,0,len);
          return;
        }
        if (len > 0)         out.write(buf,0,len);
      }
      byte[] trailer=new byte[TRAILER_SIZE];
      writeTrailer(trailer,0);
      out.write(trailer);
    }
  }
  private void writeHeader() throws IOException {
    out.write(new byte[]{(byte)GZIP_MAGIC,(byte)(GZIP_MAGIC >> 8),Deflater.DEFLATED,0,0,0,0,0,0,0});
  }
  private void writeTrailer(  byte[] buf,  int offset) throws IOException {
    writeInt((int)crc.getValue(),buf,offset);
    writeInt(def.getTotalIn(),buf,offset + 4);
  }
  private void writeInt(  int i,  byte[] buf,  int offset) throws IOException {
    writeShort(i & 0xffff,buf,offset);
    writeShort((i >> 16) & 0xffff,buf,offset + 2);
  }
  private void writeShort(  int s,  byte[] buf,  int offset) throws IOException {
    buf[offset]=(byte)(s & 0xff);
    buf[offset + 1]=(byte)((s >> 8) & 0xff);
  }
}
