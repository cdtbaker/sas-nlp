package sun.io;
import sun.nio.cs.ext.IBM1124;
/** 
 * A table to convert to Cp1124 to Unicode
 * @author  ConverterGenerator tool
 */
public class ByteToCharCp1124 extends ByteToCharSingleByte {
  private final static IBM1124 nioCoder=new IBM1124();
  public String getCharacterEncoding(){
    return "Cp1124";
  }
  public ByteToCharCp1124(){
    super.byteToCharTable=nioCoder.getDecoderSingleByteMappings();
  }
}