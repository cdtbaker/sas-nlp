package sun.io;
import sun.nio.cs.ext.IBM1112;
/** 
 * A table to convert to Cp1112 to Unicode
 * @author  ConverterGenerator tool
 */
public class ByteToCharCp1112 extends ByteToCharSingleByte {
  private final static IBM1112 nioCoder=new IBM1112();
  public String getCharacterEncoding(){
    return "Cp1112";
  }
  public ByteToCharCp1112(){
    super.byteToCharTable=nioCoder.getDecoderSingleByteMappings();
  }
}
