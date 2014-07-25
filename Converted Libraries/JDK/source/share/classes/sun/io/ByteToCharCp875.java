package sun.io;
import sun.nio.cs.ext.IBM875;
/** 
 * A table to convert to Cp875 to Unicode
 * @author  ConverterGenerator tool
 */
public class ByteToCharCp875 extends ByteToCharSingleByte {
  private final static IBM875 nioCoder=new IBM875();
  public String getCharacterEncoding(){
    return "Cp875";
  }
  public ByteToCharCp875(){
    super.byteToCharTable=nioCoder.getDecoderSingleByteMappings();
  }
}
