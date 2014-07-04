package sun.io;
import sun.nio.cs.ext.MacUkraine;
/** 
 * A table to convert to MacUkraine to Unicode
 * @author  ConverterGenerator tool
 */
public class ByteToCharMacUkraine extends ByteToCharSingleByte {
  private final static MacUkraine nioCoder=new MacUkraine();
  public String getCharacterEncoding(){
    return "MacUkraine";
  }
  public ByteToCharMacUkraine(){
    super.byteToCharTable=nioCoder.getDecoderSingleByteMappings();
  }
}
