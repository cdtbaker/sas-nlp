package sun.awt;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CharsetDecoder;
public class HKSCS extends sun.nio.cs.ext.MS950_HKSCS_XP {
  public HKSCS(){
    super();
  }
  public boolean contains(  Charset cs){
    return (cs instanceof HKSCS);
  }
}
