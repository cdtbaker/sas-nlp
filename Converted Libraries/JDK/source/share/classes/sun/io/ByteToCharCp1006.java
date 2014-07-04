package sun.io;
import sun.nio.cs.ext.IBM1006;
/** 
 * A table to convert to Cp1006 to Unicode
 * @author  ConverterGenerator tool
 */
public class ByteToCharCp1006 extends ByteToCharSingleByte {
  private final static IBM1006 nioCoder=new IBM1006();
  public String getCharacterEncoding(){
    return "Cp1006";
  }
  public ByteToCharCp1006(){
    super.byteToCharTable=nioCoder.getDecoderSingleByteMappings();
  }
}
