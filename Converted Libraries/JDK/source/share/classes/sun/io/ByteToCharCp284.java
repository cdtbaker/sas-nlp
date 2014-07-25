package sun.io;
import sun.nio.cs.ext.IBM284;
/** 
 * A table to convert to Cp284 to Unicode
 * @author  ConverterGenerator tool
 */
public class ByteToCharCp284 extends ByteToCharSingleByte {
  private final static IBM284 nioCoder=new IBM284();
  public String getCharacterEncoding(){
    return "Cp284";
  }
  public ByteToCharCp284(){
    super.byteToCharTable=nioCoder.getDecoderSingleByteMappings();
  }
}
