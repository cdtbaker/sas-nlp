package sun.beans.editors;
import java.beans.*;
public class DoubleEditor extends NumberEditor {
  public void setAsText(  String text) throws IllegalArgumentException {
    setValue((text == null) ? null : Double.valueOf(text));
  }
}
