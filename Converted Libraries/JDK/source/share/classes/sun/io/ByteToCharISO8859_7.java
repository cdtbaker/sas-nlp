package sun.io;
import sun.nio.cs.ISO_8859_7;
/** 
 * A table to convert ISO8859_7 to Unicode
 * @author  ConverterGenerator tool
 */
public class ByteToCharISO8859_7 extends ByteToCharSingleByte {
  private final static ISO_8859_7 nioCoder=new ISO_8859_7();
  public String getCharacterEncoding(){
    return "ISO8859_7";
  }
  public ByteToCharISO8859_7(){
    super.byteToCharTable=nioCoder.getDecoderSingleByteMappings();
  }
}
