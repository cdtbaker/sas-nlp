package sun.tools.jar;
import java.io.IOException;
public class JarException extends IOException {
  public JarException(){
    super();
  }
  public JarException(  String s){
    super(s);
  }
}
