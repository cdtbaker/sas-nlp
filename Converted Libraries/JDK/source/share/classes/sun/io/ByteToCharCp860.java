package sun.io;
import sun.nio.cs.ext.IBM860;
/** 
 * A table to convert to Cp860 to Unicode
 * @author  ConverterGenerator tool
 */
public class ByteToCharCp860 extends ByteToCharSingleByte {
  private final static IBM860 nioCoder=new IBM860();
  public String getCharacterEncoding(){
    return "Cp860";
  }
  public ByteToCharCp860(){
    super.byteToCharTable=nioCoder.getDecoderSingleByteMappings();
  }
}
