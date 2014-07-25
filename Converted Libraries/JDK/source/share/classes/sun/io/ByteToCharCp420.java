package sun.io;
import sun.nio.cs.ext.IBM420;
/** 
 * A table to convert to Cp420 to Unicode
 * @author  ConverterGenerator tool
 */
public class ByteToCharCp420 extends ByteToCharSingleByte {
  private final static IBM420 nioCoder=new IBM420();
  public String getCharacterEncoding(){
    return "Cp420";
  }
  public ByteToCharCp420(){
    super.byteToCharTable=nioCoder.getDecoderSingleByteMappings();
  }
}
