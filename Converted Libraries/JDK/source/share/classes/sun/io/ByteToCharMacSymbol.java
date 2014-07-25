package sun.io;
import sun.nio.cs.ext.MacSymbol;
/** 
 * A table to convert to MacSymbol to Unicode
 * @author  ConverterGenerator tool
 */
public class ByteToCharMacSymbol extends ByteToCharSingleByte {
  private final static MacSymbol nioCoder=new MacSymbol();
  public String getCharacterEncoding(){
    return "MacSymbol";
  }
  public ByteToCharMacSymbol(){
    super.byteToCharTable=nioCoder.getDecoderSingleByteMappings();
  }
}
