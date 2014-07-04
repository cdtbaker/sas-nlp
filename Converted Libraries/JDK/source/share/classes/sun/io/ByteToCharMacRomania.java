package sun.io;
import sun.nio.cs.ext.MacRomania;
/** 
 * A table to convert to MacRomania to Unicode
 * @author  ConverterGenerator tool
 */
public class ByteToCharMacRomania extends ByteToCharSingleByte {
  private final static MacRomania nioCoder=new MacRomania();
  public String getCharacterEncoding(){
    return "MacRomania";
  }
  public ByteToCharMacRomania(){
    super.byteToCharTable=nioCoder.getDecoderSingleByteMappings();
  }
}
