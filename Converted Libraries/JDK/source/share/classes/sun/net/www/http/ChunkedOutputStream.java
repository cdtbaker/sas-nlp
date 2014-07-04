package sun.net.www.http;
import java.io.*;
/** 
 * OutputStream that sends the output to the underlying stream using chunked
 * encoding as specified in RFC 2068.
 */
public class ChunkedOutputStream extends PrintStream {
  static final int DEFAULT_CHUNK_SIZE=4096;
  private static final byte[] CRLF={'\r','\n'};
  private static final int CRLF_SIZE=CRLF.length;
  private static final byte[] FOOTER=CRLF;
  private static final int FOOTER_SIZE=CRLF_SIZE;
  private static final byte[] EMPTY_CHUNK_HEADER=getHeader(0);
  private static final int EMPTY_CHUNK_HEADER_SIZE=getHeaderSize(0);
  private byte buf[];
  private int size;
  private int count;
  private int spaceInCurrentChunk;
  private PrintStream out;
  private int preferredChunkDataSize;
  private int preferedHeaderSize;
  private int preferredChunkGrossSize;
  private byte[] completeHeader;
  private static int getHeaderSize(  int size){
    return (Integer.toHexString(size)).length() + CRLF_SIZE;
  }
  private static byte[] getHeader(  int size){
    try {
      String hexStr=Integer.toHexString(size);
      byte[] hexBytes=hexStr.getBytes("US-ASCII");
      byte[] header=new byte[getHeaderSize(size)];
      for (int i=0; i < hexBytes.length; i++)       header[i]=hexBytes[i];
      header[hexBytes.length]=CRLF[0];
      header[hexBytes.length + 1]=CRLF[1];
      return header;
    }
 catch (    java.io.UnsupportedEncodingException e) {
      throw new InternalError(e.getMessage());
    }
  }
  public ChunkedOutputStream(  PrintStream o){
    this(o,DEFAULT_CHUNK_SIZE);
  }
  public ChunkedOutputStream(  PrintStream o,  int size){
    super(o);
    out=o;
    if (size <= 0) {
      size=DEFAULT_CHUNK_SIZE;
    }
    if (size > 0) {
      int adjusted_size=size - getHeaderSize(size) - FOOTER_SIZE;
      if (getHeaderSize(adjusted_size + 1) < getHeaderSize(size)) {
        adjusted_size++;
      }
      size=adjusted_size;
    }
    if (size > 0) {
      preferredChunkDataSize=size;
    }
 else {
      preferredChunkDataSize=DEFAULT_CHUNK_SIZE - getHeaderSize(DEFAULT_CHUNK_SIZE) - FOOTER_SIZE;
    }
    preferedHeaderSize=getHeaderSize(preferredChunkDataSize);
    preferredChunkGrossSize=preferedHeaderSize + preferredChunkDataSize + FOOTER_SIZE;
    completeHeader=getHeader(preferredChunkDataSize);
    buf=new byte[preferredChunkDataSize + 32];
    reset();
  }
  private void flush(  boolean flushAll){
    if (spaceInCurrentChunk == 0) {
      out.write(buf,0,preferredChunkGrossSize);
      out.flush();
      reset();
    }
 else     if (flushAll) {
      if (size > 0) {
        int adjustedHeaderStartIndex=preferedHeaderSize - getHeaderSize(size);
        System.arraycopy(getHeader(size),0,buf,adjustedHeaderStartIndex,getHeaderSize(size));
        buf[count++]=FOOTER[0];
        buf[count++]=FOOTER[1];
        out.write(buf,adjustedHeaderStartIndex,count - adjustedHeaderStartIndex);
      }
 else {
        out.write(EMPTY_CHUNK_HEADER,0,EMPTY_CHUNK_HEADER_SIZE);
      }
      out.flush();
      reset();
    }
  }
  @Override public boolean checkError(){
    return out.checkError();
  }
  private void ensureOpen(){
    if (out == null)     setError();
  }
  @Override public synchronized void write(  byte b[],  int off,  int len){
    ensureOpen();
    if ((off < 0) || (off > b.length) || (len < 0)|| ((off + len) > b.length)|| ((off + len) < 0)) {
      throw new IndexOutOfBoundsException();
    }
 else     if (len == 0) {
      return;
    }
    int bytesToWrite=len;
    int inputIndex=off;
    do {
      if (bytesToWrite >= spaceInCurrentChunk) {
        for (int i=0; i < completeHeader.length; i++)         buf[i]=completeHeader[i];
        System.arraycopy(b,inputIndex,buf,count,spaceInCurrentChunk);
        inputIndex+=spaceInCurrentChunk;
        bytesToWrite-=spaceInCurrentChunk;
        count+=spaceInCurrentChunk;
        buf[count++]=FOOTER[0];
        buf[count++]=FOOTER[1];
        spaceInCurrentChunk=0;
        flush(false);
        if (checkError()) {
          break;
        }
      }
 else {
        System.arraycopy(b,inputIndex,buf,count,bytesToWrite);
        count+=bytesToWrite;
        size+=bytesToWrite;
        spaceInCurrentChunk-=bytesToWrite;
        bytesToWrite=0;
      }
    }
 while (bytesToWrite > 0);
  }
  @Override public synchronized void write(  int _b){
    byte b[]={(byte)_b};
    write(b,0,1);
  }
  public synchronized void reset(){
    count=preferedHeaderSize;
    size=0;
    spaceInCurrentChunk=preferredChunkDataSize;
  }
  public int size(){
    return size;
  }
  @Override public synchronized void close(){
    ensureOpen();
    if (size > 0) {
      flush(true);
    }
    flush(true);
    out=null;
  }
  @Override public synchronized void flush(){
    ensureOpen();
    if (size > 0) {
      flush(true);
    }
  }
}
