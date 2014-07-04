package javax.swing.plaf.basic;
import javax.swing.ComboBoxEditor;
import javax.swing.JTextField;
import javax.swing.border.Border;
import java.awt.Component;
import java.awt.event.*;
import java.lang.reflect.Method;
/** 
 * The default editor for editable combo boxes. The editor is implemented as a JTextField.
 * @author Arnaud Weber
 * @author Mark Davidson
 */
public class BasicComboBoxEditor implements ComboBoxEditor, FocusListener {
  protected JTextField editor;
  private Object oldValue;
  public BasicComboBoxEditor(){
    editor=createEditorComponent();
  }
  public Component getEditorComponent(){
    return editor;
  }
  /** 
 * Creates the internal editor component. Override this to provide
 * a custom implementation.
 * @return a new editor component
 * @since 1.6
 */
  protected JTextField createEditorComponent(){
    JTextField editor=new BorderlessTextField("",9);
    editor.setBorder(null);
    return editor;
  }
  /** 
 * Sets the item that should be edited.
 * @param anObject the displayed value of the editor
 */
  public void setItem(  Object anObject){
    String text;
    if (anObject != null) {
      text=anObject.toString();
      oldValue=anObject;
    }
 else {
      text="";
    }
    if (!text.equals(editor.getText())) {
      editor.setText(text);
    }
  }
  public Object getItem(){
    Object newValue=editor.getText();
    if (oldValue != null && !(oldValue instanceof String)) {
      if (newValue.equals(oldValue.toString())) {
        return oldValue;
      }
 else {
        Class<?> cls=oldValue.getClass();
        try {
          Method method=cls.getMethod("valueOf",new Class[]{String.class});
          newValue=method.invoke(oldValue,new Object[]{editor.getText()});
        }
 catch (        Exception ex) {
        }
      }
    }
    return newValue;
  }
  public void selectAll(){
    editor.selectAll();
    editor.requestFocus();
  }
  public void focusGained(  FocusEvent e){
  }
  public void focusLost(  FocusEvent e){
  }
  public void addActionListener(  ActionListener l){
    editor.addActionListener(l);
  }
  public void removeActionListener(  ActionListener l){
    editor.removeActionListener(l);
  }
static class BorderlessTextField extends JTextField {
    public BorderlessTextField(    String value,    int n){
      super(value,n);
    }
    public void setText(    String s){
      if (getText().equals(s)) {
        return;
      }
      super.setText(s);
    }
    public void setBorder(    Border b){
      if (!(b instanceof UIResource)) {
        super.setBorder(b);
      }
    }
  }
  /** 
 * A subclass of BasicComboBoxEditor that implements UIResource.
 * BasicComboBoxEditor doesn't implement UIResource
 * directly so that applications can safely override the
 * cellRenderer property with BasicListCellRenderer subclasses.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases. The current serialization support is
 * appropriate for short term storage or RMI between applications running
 * the same version of Swing.  As of 1.4, support for long term storage
 * of all JavaBeans<sup><font size="-2">TM</font></sup>
 * has been added to the <code>java.beans</code> package.
 * Please see {@link java.beans.XMLEncoder}.
 */
public static class UIResource extends BasicComboBoxEditor implements javax.swing.plaf.UIResource {
  }
}
