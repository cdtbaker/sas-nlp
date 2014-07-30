package com.sun.jndi.ldap.sasl;
import javax.security.sasl.Sasl;
import javax.security.sasl.SaslClient;
import javax.security.sasl.SaslException;
import java.io.IOException;
import java.io.EOFException;
import java.io.InputStream;
/** 
 * This class is used by clients of Java SASL that need to create an input stream
 * that uses SaslClient's unwrap() method to decode the SASL buffers
 * sent by the SASL server.
 * Extend from InputStream instead of FilterInputStream because
 * we need to override less methods in InputStream. That is, the
 * behavior of the default implementations in InputStream matches
 * more closely with the behavior we want in SaslInputStream.
 * @author Rosanna Lee
 */
public class SaslInputStream extends InputStream {
  private static final boolean debug=false;
  private byte[] saslBuffer;
  private byte[] lenBuf=new byte[4];
  private byte[] buf=new byte[0];
  private int bufPos=0;
  private InputStream in;
  private SaslClient sc;
  private int recvMaxBufSize=65536;
  SaslInputStream(  SaslClient sc,  InputStream in) throws SaslException {
    super();
    this.in=in;
    this.sc=sc;
    String str=(String)sc.getNegotiatedProperty(Sasl.MAX_BUFFER);
    if (str != null) {
      try {
        recvMaxBufSize=Integer.parseInt(str);
      }
 catch (      NumberFormatException e) {
        throw new SaslException(Sasl.MAX_BUFFER + " property must be numeric string: " + str);
      }
    }
    saslBuffer=new byte[recvMaxBufSize];
  }
  public int read() throws IOException {
    byte[] inBuf=new byte[1];
    int count=read(inBuf,0,1);
    if (count > 0) {
      return inBuf[0];
    }
 else {
      return -1;
    }
  }
  public int read(  byte[] inBuf,  int start,  int count) throws IOException {
    if (bufPos >= buf.length) {
      int actual=fill();
      while (actual == 0) {
        actual=fill();
      }
      if (actual == -1) {
        return -1;
      }
    }
    int avail=buf.length - bufPos;
    if (count > avail) {
      System.arraycopy(buf,bufPos,inBuf,start,avail);
      bufPos=buf.length;
      return avail;
    }
 else {
      System.arraycopy(buf,bufPos,inBuf,start,count);
      bufPos+=count;
      return count;
    }
  }
  /** 
 * Fills the buf with more data by reading a SASL buffer, unwrapping it,
 * and leaving the bytes in buf for read() to return.
 * @return The number of unwrapped bytes available
 */
  private int fill() throws IOException {
    int actual=readFully(lenBuf,4);
    if (actual != 4) {
      return -1;
    }
    int len=networkByteOrderToInt(lenBuf,0,4);
    if (len > recvMaxBufSize) {
      throw new IOException(len + "exceeds the negotiated receive buffer size limit:" + recvMaxBufSize);
    }
    if (debug) {
      System.err.println("reading " + len + " bytes from network");
    }
    actual=readFully(saslBuffer,len);
    if (actual != len) {
      throw new EOFException("Expecting to read " + len + " bytes but got "+ actual+ " bytes before EOF");
    }
    buf=sc.unwrap(saslBuffer,0,len);
    bufPos=0;
    return buf.length;
  }
  /** 
 * Read requested number of bytes before returning.
 * @return The number of bytes actually read; -1 if none read
 */
  private int readFully(  byte[] inBuf,  int total) throws IOException {
    int count, pos=0;
    if (debug) {
      System.err.println("readFully " + total + " from "+ in);
    }
    while (total > 0) {
      count=in.read(inBuf,pos,total);
      if (debug) {
        System.err.println("readFully read " + count);
      }
      if (count == -1) {
        return (pos == 0 ? -1 : pos);
      }
      pos+=count;
      total-=count;
    }
    return pos;
  }
  public int available() throws IOException {
    return buf.length - bufPos;
  }
  public void close() throws IOException {
    SaslException save=null;
    try {
      sc.dispose();
    }
 catch (    SaslException e) {
      save=e;
    }
    in.close();
    if (save != null) {
      throw save;
    }
  }
  /** 
 * Returns the integer represented by  4 bytes in network byte order.
 */
  private static int networkByteOrderToInt(  byte[] buf,  int start,  int count){
    if (count > 4) {
      throw new IllegalArgumentException("Cannot handle more than 4 bytes");
    }
    int answer=0;
    for (int i=0; i < count; i++) {
      answer<<=8;
      answer|=((int)buf[start + i] & 0xff);
    }
    return answer;
  }
}