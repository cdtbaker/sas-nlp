package sun.io;
import sun.nio.cs.ext.MacThai;
/** 
 * A table to convert to MacThai to Unicode
 * @author  ConverterGenerator tool
 */
public class ByteToCharMacThai extends ByteToCharSingleByte {
  private final static MacThai nioCoder=new MacThai();
  public String getCharacterEncoding(){
    return "MacThai";
  }
  public ByteToCharMacThai(){
    super.byteToCharTable=nioCoder.getDecoderSingleByteMappings();
  }
}
