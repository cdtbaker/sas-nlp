package sun.io;
import sun.nio.cs.ISO_8859_2;
/** 
 * A table to convert ISO8859_2 to Unicode
 * @author  ConverterGenerator tool
 */
public class ByteToCharISO8859_2 extends ByteToCharSingleByte {
  private final static ISO_8859_2 nioCoder=new ISO_8859_2();
  public String getCharacterEncoding(){
    return "ISO8859_2";
  }
  public ByteToCharISO8859_2(){
    super.byteToCharTable=nioCoder.getDecoderSingleByteMappings();
  }
}
