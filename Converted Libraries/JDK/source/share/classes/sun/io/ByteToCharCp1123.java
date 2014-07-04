package sun.io;
import sun.nio.cs.ext.IBM1123;
/** 
 * A table to convert to Cp1123 to Unicode
 * @author  ConverterGenerator tool
 */
public class ByteToCharCp1123 extends ByteToCharSingleByte {
  private final static IBM1123 nioCoder=new IBM1123();
  public String getCharacterEncoding(){
    return "Cp1123";
  }
  public ByteToCharCp1123(){
    super.byteToCharTable=nioCoder.getDecoderSingleByteMappings();
  }
}
