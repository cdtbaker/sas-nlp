package sun.io;
import sun.nio.cs.ext.MS1258;
/** 
 * A table to convert Cp1258 to Unicode
 * @author  ConverterGenerator tool
 */
public class ByteToCharCp1258 extends ByteToCharSingleByte {
  private final static MS1258 nioCoder=new MS1258();
  public String getCharacterEncoding(){
    return "Cp1258";
  }
  public ByteToCharCp1258(){
    super.byteToCharTable=nioCoder.getDecoderSingleByteMappings();
  }
}
