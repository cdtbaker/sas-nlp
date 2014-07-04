package org.ojalgo.netio;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
public final class BufferedInputStreamReader extends BufferedReader {
  public BufferedInputStreamReader(  InputStream aStream){
    super(new InputStreamReader(aStream));
  }
  public BufferedInputStreamReader(  InputStream aStream,  Charset aCharset){
    super(new InputStreamReader(aStream,aCharset));
  }
  public BufferedInputStreamReader(  InputStream aStream,  Charset aCharset,  int aSize){
    super(new InputStreamReader(aStream,aCharset),aSize);
  }
  public BufferedInputStreamReader(  InputStream aStream,  CharsetDecoder aDecoder){
    super(new InputStreamReader(aStream,aDecoder));
  }
  public BufferedInputStreamReader(  InputStream aStream,  CharsetDecoder aDecoder,  int aSize){
    super(new InputStreamReader(aStream,aDecoder),aSize);
  }
  public BufferedInputStreamReader(  InputStream aStream,  int aSize){
    super(new InputStreamReader(aStream),aSize);
  }
  public BufferedInputStreamReader(  InputStream aStream,  String aCharsetName) throws UnsupportedEncodingException {
    super(new InputStreamReader(aStream,aCharsetName));
  }
  public BufferedInputStreamReader(  InputStream aStream,  String aCharsetName,  int aSize) throws UnsupportedEncodingException {
    super(new InputStreamReader(aStream,aCharsetName),aSize);
  }
  protected BufferedInputStreamReader(  Reader aReader){
    super(aReader);
  }
  protected BufferedInputStreamReader(  Reader aReader,  int aSize){
    super(aReader,aSize);
  }
}
