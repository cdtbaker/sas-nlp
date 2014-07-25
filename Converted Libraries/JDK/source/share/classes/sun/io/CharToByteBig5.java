package sun.io;
import sun.nio.cs.ext.DoubleByte;
import sun.nio.cs.ext.Big5;
/** 
 * Tables and data to convert Unicode to Big5
 */
public class CharToByteBig5 extends CharToByteDBCS_ASCII {
  private static DoubleByte.Encoder enc=(DoubleByte.Encoder)new Big5().newEncoder();
  public String getCharacterEncoding(){
    return "Big5";
  }
  public CharToByteBig5(){
    super(enc);
  }
}
