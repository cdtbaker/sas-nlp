package sun.io;
import sun.nio.cs.ext.IBM864;
/** 
 * A table to convert to Cp864 to Unicode
 * @author  ConverterGenerator tool
 */
public class ByteToCharCp864 extends ByteToCharSingleByte {
  private final static IBM864 nioCoder=new IBM864();
  public String getCharacterEncoding(){
    return "Cp864";
  }
  public ByteToCharCp864(){
    super.byteToCharTable=nioCoder.getDecoderSingleByteMappings();
  }
}
