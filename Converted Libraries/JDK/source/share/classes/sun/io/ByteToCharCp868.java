package sun.io;
import sun.nio.cs.ext.IBM868;
/** 
 * A table to convert to Cp868 to Unicode
 * @author  ConverterGenerator tool
 */
public class ByteToCharCp868 extends ByteToCharSingleByte {
  private final static IBM868 nioCoder=new IBM868();
  public String getCharacterEncoding(){
    return "Cp868";
  }
  public ByteToCharCp868(){
    super.byteToCharTable=nioCoder.getDecoderSingleByteMappings();
  }
}
