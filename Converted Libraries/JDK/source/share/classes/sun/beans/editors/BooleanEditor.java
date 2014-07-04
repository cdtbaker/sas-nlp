package sun.beans.editors;
import java.beans.*;
public class BooleanEditor extends PropertyEditorSupport {
  public String getJavaInitializationString(){
    Object value=getValue();
    return (value != null) ? value.toString() : "null";
  }
  public String getAsText(){
    Object value=getValue();
    return (value instanceof Boolean) ? getValidName((Boolean)value) : null;
  }
  public void setAsText(  String text) throws java.lang.IllegalArgumentException {
    if (text == null) {
      setValue(null);
    }
 else     if (isValidName(true,text)) {
      setValue(Boolean.TRUE);
    }
 else     if (isValidName(false,text)) {
      setValue(Boolean.FALSE);
    }
 else {
      throw new java.lang.IllegalArgumentException(text);
    }
  }
  public String[] getTags(){
    return new String[]{getValidName(true),getValidName(false)};
  }
  private String getValidName(  boolean value){
    return value ? "True" : "False";
  }
  private boolean isValidName(  boolean value,  String name){
    return getValidName(value).equalsIgnoreCase(name);
  }
}
