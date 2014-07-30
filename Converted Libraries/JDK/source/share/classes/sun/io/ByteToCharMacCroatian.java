package sun.io;
import sun.nio.cs.ext.MacCroatian;
/** 
 * A table to convert to MacCroatian to Unicode
 * @author  ConverterGenerator tool
 */
public class ByteToCharMacCroatian extends ByteToCharSingleByte {
  private final static MacCroatian nioCoder=new MacCroatian();
  public String getCharacterEncoding(){
    return "MacCroatian";
  }
  public ByteToCharMacCroatian(){
    super.byteToCharTable=nioCoder.getDecoderSingleByteMappings();
  }
}