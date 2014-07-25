package sun.io;
import sun.nio.cs.ext.IBM285;
/** 
 * A table to convert to Cp285 to Unicode
 * @author  ConverterGenerator tool
 */
public class ByteToCharCp285 extends ByteToCharSingleByte {
  private final static IBM285 nioCoder=new IBM285();
  public String getCharacterEncoding(){
    return "Cp285";
  }
  public ByteToCharCp285(){
    super.byteToCharTable=nioCoder.getDecoderSingleByteMappings();
  }
}
