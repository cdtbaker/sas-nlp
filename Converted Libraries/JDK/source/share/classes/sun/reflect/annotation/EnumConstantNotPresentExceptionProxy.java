package sun.reflect.annotation;
import java.lang.annotation.*;
/** 
 * ExceptionProxy for EnumConstantNotPresentException.
 * @author  Josh Bloch
 * @since   1.5
 */
public class EnumConstantNotPresentExceptionProxy extends ExceptionProxy {
  Class<? extends Enum> enumType;
  String constName;
  public EnumConstantNotPresentExceptionProxy(  Class<? extends Enum> enumType,  String constName){
    this.enumType=enumType;
    this.constName=constName;
  }
  protected RuntimeException generateException(){
    return new EnumConstantNotPresentException(enumType,constName);
  }
}
