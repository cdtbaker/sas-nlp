package sun.misc;
import java.io.EOFException;
import java.net.URL;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.InputStream;
import java.security.CodeSigner;
import java.util.jar.Manifest;
import java.nio.ByteBuffer;
import java.util.Arrays;
import sun.nio.ByteBuffered;
/** 
 * This class is used to represent a Resource that has been loaded
 * from the class path.
 * @author  David Connelly
 * @since   1.2
 */
public abstract class Resource {
  /** 
 * Returns the name of the Resource.
 */
  public abstract String getName();
  /** 
 * Returns the URL of the Resource.
 */
  public abstract URL getURL();
  /** 
 * Returns the CodeSource URL for the Resource.
 */
  public abstract URL getCodeSourceURL();
  /** 
 * Returns an InputStream for reading the Resource data.
 */
  public abstract InputStream getInputStream() throws IOException ;
  /** 
 * Returns the length of the Resource data, or -1 if unknown.
 */
  public abstract int getContentLength() throws IOException ;
  private InputStream cis;
  private synchronized InputStream cachedInputStream() throws IOException {
    if (cis == null) {
      cis=getInputStream();
    }
    return cis;
  }
  /** 
 * Returns the Resource data as an array of bytes.
 */
  public byte[] getBytes() throws IOException {
    byte[] b;
    InputStream in=cachedInputStream();
    boolean isInterrupted=Thread.interrupted();
    int len;
    for (; ; ) {
      try {
        len=getContentLength();
        break;
      }
 catch (      InterruptedIOException iioe) {
        Thread.interrupted();
        isInterrupted=true;
      }
    }
    try {
      b=new byte[0];
      if (len == -1)       len=Integer.MAX_VALUE;
      int pos=0;
      while (pos < len) {
        int bytesToRead;
        if (pos >= b.length) {
          bytesToRead=Math.min(len - pos,b.length + 1024);
          if (b.length < pos + bytesToRead) {
            b=Arrays.copyOf(b,pos + bytesToRead);
          }
        }
 else {
          bytesToRead=b.length - pos;
        }
        int cc=0;
        try {
          cc=in.read(b,pos,bytesToRead);
        }
 catch (        InterruptedIOException iioe) {
          Thread.interrupted();
          isInterrupted=true;
        }
        if (cc < 0) {
          if (len != Integer.MAX_VALUE) {
            throw new EOFException("Detect premature EOF");
          }
 else {
            if (b.length != pos) {
              b=Arrays.copyOf(b,pos);
            }
            break;
          }
        }
        pos+=cc;
      }
    }
  finally {
      try {
        in.close();
      }
 catch (      InterruptedIOException iioe) {
        isInterrupted=true;
      }
catch (      IOException ignore) {
      }
      if (isInterrupted) {
        Thread.currentThread().interrupt();
      }
    }
    return b;
  }
  /** 
 * Returns the Resource data as a ByteBuffer, but only if the input stream
 * was implemented on top of a ByteBuffer. Return <tt>null</tt> otherwise.
 */
  public ByteBuffer getByteBuffer() throws IOException {
    InputStream in=cachedInputStream();
    if (in instanceof ByteBuffered) {
      return ((ByteBuffered)in).getByteBuffer();
    }
    return null;
  }
  /** 
 * Returns the Manifest for the Resource, or null if none.
 */
  public Manifest getManifest() throws IOException {
    return null;
  }
  /** 
 * Returns theCertificates for the Resource, or null if none.
 */
  public java.security.cert.Certificate[] getCertificates(){
    return null;
  }
  /** 
 * Returns the code signers for the Resource, or null if none.
 */
  public CodeSigner[] getCodeSigners(){
    return null;
  }
}
