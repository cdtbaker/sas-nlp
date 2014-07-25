package sun.io;
import sun.nio.cs.ext.IBM838;
/** 
 * A table to convert to Cp838 to Unicode
 * @author  ConverterGenerator tool
 */
public class ByteToCharCp838 extends ByteToCharSingleByte {
  private final static IBM838 nioCoder=new IBM838();
  public String getCharacterEncoding(){
    return "Cp838";
  }
  public ByteToCharCp838(){
    super.byteToCharTable=nioCoder.getDecoderSingleByteMappings();
  }
}
