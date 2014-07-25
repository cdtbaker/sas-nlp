package sun.io;
import sun.nio.cs.ext.IBM922;
/** 
 * A table to convert to Cp922 to Unicode
 * @author  ConverterGenerator tool
 */
public class ByteToCharCp922 extends ByteToCharSingleByte {
  private final static IBM922 nioCoder=new IBM922();
  public String getCharacterEncoding(){
    return "Cp922";
  }
  public ByteToCharCp922(){
    super.byteToCharTable=nioCoder.getDecoderSingleByteMappings();
  }
}
