package sun.io;
import sun.nio.cs.MS1257;
/** 
 * A table to convert Cp1257 to Unicode
 * @author  ConverterGenerator tool
 */
public class ByteToCharCp1257 extends ByteToCharSingleByte {
  private final static MS1257 nioCoder=new MS1257();
  public String getCharacterEncoding(){
    return "Cp1257";
  }
  public ByteToCharCp1257(){
    super.byteToCharTable=nioCoder.getDecoderSingleByteMappings();
  }
}
