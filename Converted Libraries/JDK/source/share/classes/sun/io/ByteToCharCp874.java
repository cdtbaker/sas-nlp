package sun.io;
import sun.nio.cs.IBM874;
/** 
 * A table to convert to Cp874 to Unicode
 * @author  ConverterGenerator tool
 */
public class ByteToCharCp874 extends ByteToCharSingleByte {
  private final static IBM874 nioCoder=new IBM874();
  public String getCharacterEncoding(){
    return "Cp874";
  }
  public ByteToCharCp874(){
    super.byteToCharTable=nioCoder.getDecoderSingleByteMappings();
  }
}
