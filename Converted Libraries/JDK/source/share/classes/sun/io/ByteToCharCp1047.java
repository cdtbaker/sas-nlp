package sun.io;
import sun.nio.cs.ext.IBM1047;
/** 
 * A table to convert to Cp1047 to Unicode
 * @author  ConverterGenerator tool
 */
public class ByteToCharCp1047 extends ByteToCharSingleByte {
  private final static IBM1047 nioCoder=new IBM1047();
  public String getCharacterEncoding(){
    return "Cp1047";
  }
  public ByteToCharCp1047(){
    super.byteToCharTable=nioCoder.getDecoderSingleByteMappings();
  }
}
