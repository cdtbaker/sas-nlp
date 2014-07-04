package sun.io;
import sun.nio.cs.ext.IBM1149;
/** 
 * Tables and data to convert Unicode to Cp1149
 * @author  ConverterGenerator tool
 */
public class CharToByteCp1149 extends CharToByteSingleByte {
  private final static IBM1149 nioCoder=new IBM1149();
  public String getCharacterEncoding(){
    return "Cp1149";
  }
  public CharToByteCp1149(){
    super.mask1=0xFF00;
    super.mask2=0x00FF;
    super.shift=8;
    super.index1=nioCoder.getEncoderIndex1();
    super.index2=nioCoder.getEncoderIndex2();
  }
}
