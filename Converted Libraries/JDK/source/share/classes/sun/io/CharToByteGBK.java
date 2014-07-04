package sun.io;
import sun.nio.cs.ext.*;
public class CharToByteGBK extends CharToByteDBCS_ASCII {
  private static DoubleByte.Encoder enc=(DoubleByte.Encoder)new GBK().newEncoder();
  public String getCharacterEncoding(){
    return "GBK";
  }
  public CharToByteGBK(){
    super(enc);
  }
}
