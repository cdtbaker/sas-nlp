package org.ojalgo.netio;
import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
public final class BufferedOutputStreamWriter extends BufferedWriter {
  public BufferedOutputStreamWriter(  OutputStream aStream){
    super(new OutputStreamWriter(aStream));
  }
  public BufferedOutputStreamWriter(  OutputStream aStream,  Charset aCharset){
    super(new OutputStreamWriter(aStream,aCharset));
  }
  public BufferedOutputStreamWriter(  OutputStream aStream,  Charset aCharset,  int aSize){
    super(new OutputStreamWriter(aStream,aCharset),aSize);
  }
  public BufferedOutputStreamWriter(  OutputStream aStream,  CharsetEncoder anEncoder){
    super(new OutputStreamWriter(aStream,anEncoder));
  }
  public BufferedOutputStreamWriter(  OutputStream aStream,  CharsetEncoder anEncoder,  int aSize){
    super(new OutputStreamWriter(aStream,anEncoder),aSize);
  }
  public BufferedOutputStreamWriter(  OutputStream aStream,  int aSize){
    super(new OutputStreamWriter(aStream),aSize);
  }
  public BufferedOutputStreamWriter(  OutputStream aStream,  String aCharsetName) throws UnsupportedEncodingException {
    super(new OutputStreamWriter(aStream,aCharsetName));
  }
  public BufferedOutputStreamWriter(  OutputStream aStream,  String aCharsetName,  int aSize) throws UnsupportedEncodingException {
    super(new OutputStreamWriter(aStream,aCharsetName),aSize);
  }
  protected BufferedOutputStreamWriter(  Writer aWriter,  int aSize){
    super(aWriter,aSize);
  }
  protected BufferedOutputStreamWriter(  Writer aWriter){
    super(aWriter);
  }
}
