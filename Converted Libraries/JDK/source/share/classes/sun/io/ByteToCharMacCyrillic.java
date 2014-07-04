package sun.io;
import sun.nio.cs.ext.MacCyrillic;
/** 
 * A table to convert to MacCyrillic to Unicode
 * @author  ConverterGenerator tool
 */
public class ByteToCharMacCyrillic extends ByteToCharSingleByte {
  private final static MacCyrillic nioCoder=new MacCyrillic();
  public String getCharacterEncoding(){
    return "MacCyrillic";
  }
  public ByteToCharMacCyrillic(){
    super.byteToCharTable=nioCoder.getDecoderSingleByteMappings();
  }
}
