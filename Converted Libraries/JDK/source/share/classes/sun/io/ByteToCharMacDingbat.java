package sun.io;
import sun.nio.cs.ext.MacDingbat;
/** 
 * A table to convert to MacDingbat to Unicode
 * @author  ConverterGenerator tool
 */
public class ByteToCharMacDingbat extends ByteToCharSingleByte {
  private final static MacDingbat nioCoder=new MacDingbat();
  public String getCharacterEncoding(){
    return "MacDingbat";
  }
  public ByteToCharMacDingbat(){
    super.byteToCharTable=nioCoder.getDecoderSingleByteMappings();
  }
}
