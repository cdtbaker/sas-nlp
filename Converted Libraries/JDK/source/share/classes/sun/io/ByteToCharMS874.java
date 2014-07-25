package sun.io;
import sun.nio.cs.ext.MS874;
/** 
 * A table to convert MS874 to Unicode
 * @author  ConverterGenerator tool
 */
public class ByteToCharMS874 extends ByteToCharSingleByte {
  private final static MS874 nioCoder=new MS874();
  public String getCharacterEncoding(){
    return "MS874";
  }
  public ByteToCharMS874(){
    super.byteToCharTable=nioCoder.getDecoderSingleByteMappings();
  }
}
