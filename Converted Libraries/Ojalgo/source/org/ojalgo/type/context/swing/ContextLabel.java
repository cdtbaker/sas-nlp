package org.ojalgo.type.context.swing;
import javax.swing.Icon;
import javax.swing.JLabel;
import org.ojalgo.ProgrammingError;
import org.ojalgo.type.context.TypeContext;
/** 
 * @author apete
 */
public class ContextLabel<T> extends JLabel {
  private final TypeContext<T> myContext;
  public ContextLabel(  final T aModelObject,  final TypeContext<T> aContext){
    super(aContext.format(aModelObject));
    myContext=aContext;
  }
  public ContextLabel(  final TypeContext<T> aContext){
    super();
    myContext=aContext;
  }
  @SuppressWarnings("unused") private ContextLabel(){
    super();
    myContext=null;
    ProgrammingError.throwForIllegalInvocation();
  }
  @SuppressWarnings("unused") private ContextLabel(  final Icon someImage){
    super(someImage);
    myContext=null;
    ProgrammingError.throwForIllegalInvocation();
  }
  @SuppressWarnings("unused") private ContextLabel(  final Icon someImage,  final int someHorizontalAlignment){
    super(someImage,someHorizontalAlignment);
    myContext=null;
    ProgrammingError.throwForIllegalInvocation();
  }
  @SuppressWarnings("unused") private ContextLabel(  final String someText){
    super(someText);
    myContext=null;
    ProgrammingError.throwForIllegalInvocation();
  }
  @SuppressWarnings("unused") private ContextLabel(  final String someText,  final Icon someIcon,  final int someHorizontalAlignment){
    super(someText,someIcon,someHorizontalAlignment);
    myContext=null;
    ProgrammingError.throwForIllegalInvocation();
  }
  @SuppressWarnings("unused") private ContextLabel(  final String someText,  final int someHorizontalAlignment){
    super(someText,someHorizontalAlignment);
    myContext=null;
    ProgrammingError.throwForIllegalInvocation();
  }
  public final T getModelObject(){
    return myContext.parse(this.getText());
  }
  public final void setModelObject(  final T aModelObject){
    this.setText(myContext.format(aModelObject));
  }
}
