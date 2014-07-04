package sun.io;
import sun.nio.cs.MS1252;
/** 
 * A table to convert Cp1252 to Unicode
 * @author  ConverterGenerator tool
 */
public class ByteToCharCp1252 extends ByteToCharSingleByte {
  private final static MS1252 nioCoder=new MS1252();
  public String getCharacterEncoding(){
    return "Cp1252";
  }
  public ByteToCharCp1252(){
    super.byteToCharTable=nioCoder.getDecoderSingleByteMappings();
  }
}
