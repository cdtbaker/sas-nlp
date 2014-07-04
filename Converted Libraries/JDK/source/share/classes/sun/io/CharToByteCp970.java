package sun.io;
import sun.nio.cs.ext.*;
public class CharToByteCp970 extends CharToByteDBCS_ASCII {
  public String getCharacterEncoding(){
    return "Cp970";
  }
  public CharToByteCp970(){
    super((DoubleByte.Encoder)new IBM970().newEncoder());
  }
}
