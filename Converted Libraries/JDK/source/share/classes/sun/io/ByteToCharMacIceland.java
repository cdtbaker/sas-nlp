package sun.io;
import sun.nio.cs.ext.MacIceland;
/** 
 * A table to convert to MacIceland to Unicode
 * @author  ConverterGenerator tool
 */
public class ByteToCharMacIceland extends ByteToCharSingleByte {
  private final static MacIceland nioCoder=new MacIceland();
  public String getCharacterEncoding(){
    return "MacIceland";
  }
  public ByteToCharMacIceland(){
    super.byteToCharTable=nioCoder.getDecoderSingleByteMappings();
  }
}
