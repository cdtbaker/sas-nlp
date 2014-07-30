package sun.io;
import sun.nio.cs.ext.MacGreek;
/** 
 * Tables and data to convert Unicode to MacGreek
 * @author  ConverterGenerator tool
 */
public class CharToByteMacGreek extends CharToByteSingleByte {
  private final static MacGreek nioCoder=new MacGreek();
  public String getCharacterEncoding(){
    return "MacGreek";
  }
  public CharToByteMacGreek(){
    super.mask1=0xFF00;
    super.mask2=0x00FF;
    super.shift=8;
    super.index1=nioCoder.getEncoderIndex1();
    super.index2=nioCoder.getEncoderIndex2();
  }
}