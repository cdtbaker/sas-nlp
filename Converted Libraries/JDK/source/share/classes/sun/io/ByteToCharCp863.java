package sun.io;
import sun.nio.cs.ext.IBM863;
/** 
 * A table to convert to Cp863 to Unicode
 * @author  ConverterGenerator tool
 */
public class ByteToCharCp863 extends ByteToCharSingleByte {
  private final static IBM863 nioCoder=new IBM863();
  public String getCharacterEncoding(){
    return "Cp863";
  }
  public ByteToCharCp863(){
    super.byteToCharTable=nioCoder.getDecoderSingleByteMappings();
  }
}
