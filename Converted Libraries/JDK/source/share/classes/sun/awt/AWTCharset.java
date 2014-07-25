package sun.awt;
import java.nio.CharBuffer;
import java.nio.ByteBuffer;
import java.nio.charset.*;
public class AWTCharset extends Charset {
  protected Charset awtCs;
  protected Charset javaCs;
  public AWTCharset(  String awtCsName,  Charset javaCs){
    super(awtCsName,null);
    this.javaCs=javaCs;
    this.awtCs=this;
  }
  public boolean contains(  Charset cs){
    if (javaCs == null)     return false;
    return javaCs.contains(cs);
  }
  public CharsetEncoder newEncoder(){
    if (javaCs == null)     throw new Error("Encoder is not supported by this Charset");
    return new Encoder(javaCs.newEncoder());
  }
  public CharsetDecoder newDecoder(){
    if (javaCs == null)     throw new Error("Decoder is not supported by this Charset");
    return new Decoder(javaCs.newDecoder());
  }
public class Encoder extends CharsetEncoder {
    protected CharsetEncoder enc;
    protected Encoder(){
      this(javaCs.newEncoder());
    }
    protected Encoder(    CharsetEncoder enc){
      super(awtCs,enc.averageBytesPerChar(),enc.maxBytesPerChar());
      this.enc=enc;
    }
    public boolean canEncode(    char c){
      return enc.canEncode(c);
    }
    public boolean canEncode(    CharSequence cs){
      return enc.canEncode(cs);
    }
    protected CoderResult encodeLoop(    CharBuffer src,    ByteBuffer dst){
      return enc.encode(src,dst,true);
    }
    protected CoderResult implFlush(    ByteBuffer out){
      return enc.flush(out);
    }
    protected void implReset(){
      enc.reset();
    }
    protected void implReplaceWith(    byte[] newReplacement){
      if (enc != null)       enc.replaceWith(newReplacement);
    }
    protected void implOnMalformedInput(    CodingErrorAction newAction){
      enc.onMalformedInput(newAction);
    }
    protected void implOnUnmappableCharacter(    CodingErrorAction newAction){
      enc.onUnmappableCharacter(newAction);
    }
    public boolean isLegalReplacement(    byte[] repl){
      return true;
    }
  }
public class Decoder extends CharsetDecoder {
    protected CharsetDecoder dec;
    private String nr;
    protected Decoder(){
      this(javaCs.newDecoder());
    }
    protected Decoder(    CharsetDecoder dec){
      super(awtCs,dec.averageCharsPerByte(),dec.maxCharsPerByte());
      this.dec=dec;
    }
    protected CoderResult decodeLoop(    ByteBuffer src,    CharBuffer dst){
      return dec.decode(src,dst,true);
    }
    ByteBuffer fbb=ByteBuffer.allocate(0);
    protected CoderResult implFlush(    CharBuffer out){
      dec.decode(fbb,out,true);
      return dec.flush(out);
    }
    protected void implReset(){
      dec.reset();
    }
    protected void implReplaceWith(    String newReplacement){
      if (dec != null)       dec.replaceWith(newReplacement);
    }
    protected void implOnMalformedInput(    CodingErrorAction newAction){
      dec.onMalformedInput(newAction);
    }
    protected void implOnUnmappableCharacter(    CodingErrorAction newAction){
      dec.onUnmappableCharacter(newAction);
    }
  }
}
