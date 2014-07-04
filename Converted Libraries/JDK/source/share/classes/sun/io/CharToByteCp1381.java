package sun.io;
import sun.nio.cs.ext.*;
public class CharToByteCp1381 extends CharToByteDBCS_ASCII {
  public String getCharacterEncoding(){
    return "Cp1381";
  }
  public CharToByteCp1381(){
    super((DoubleByte.Encoder)new IBM1381().newEncoder());
  }
}
