package sun.io;
import sun.nio.cs.MS1254;
/** 
 * A table to convert Cp1254 to Unicode
 * @author  ConverterGenerator tool
 */
public class ByteToCharCp1254 extends ByteToCharSingleByte {
  private final static MS1254 nioCoder=new MS1254();
  public String getCharacterEncoding(){
    return "Cp1254";
  }
  public ByteToCharCp1254(){
    super.byteToCharTable=nioCoder.getDecoderSingleByteMappings();
  }
}
