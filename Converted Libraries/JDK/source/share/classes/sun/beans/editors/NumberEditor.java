package sun.beans.editors;
import java.beans.*;
abstract public class NumberEditor extends PropertyEditorSupport {
  public String getJavaInitializationString(){
    Object value=getValue();
    return (value != null) ? value.toString() : "null";
  }
}
