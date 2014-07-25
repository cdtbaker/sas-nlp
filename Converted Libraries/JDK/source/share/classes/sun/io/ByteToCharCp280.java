package sun.io;
import sun.nio.cs.ext.IBM280;
/** 
 * A table to convert to Cp280 to Unicode
 * @author  ConverterGenerator tool
 */
public class ByteToCharCp280 extends ByteToCharSingleByte {
  private final static IBM280 nioCoder=new IBM280();
  public String getCharacterEncoding(){
    return "Cp280";
  }
  public ByteToCharCp280(){
    super.byteToCharTable=nioCoder.getDecoderSingleByteMappings();
  }
}
