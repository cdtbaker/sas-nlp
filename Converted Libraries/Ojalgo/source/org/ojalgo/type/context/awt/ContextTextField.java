package org.ojalgo.type.context.awt;
import java.awt.HeadlessException;
import java.awt.TextField;
import org.ojalgo.ProgrammingError;
import org.ojalgo.type.context.TypeContext;
/** 
 * @author apete
 */
public class ContextTextField<T> extends TextField {
  private final TypeContext<T> myContext;
  public ContextTextField(  final T aModelObject,  final TypeContext<T> aContext){
    super(aContext.format(aModelObject));
    myContext=aContext;
  }
  public ContextTextField(  final TypeContext<T> aContext){
    super();
    myContext=aContext;
  }
  @SuppressWarnings("unused") private ContextTextField() throws HeadlessException {
    super();
    myContext=null;
    ProgrammingError.throwForIllegalInvocation();
  }
  @SuppressWarnings("unused") private ContextTextField(  final int someColumns) throws HeadlessException {
    super(someColumns);
    myContext=null;
    ProgrammingError.throwForIllegalInvocation();
  }
  @SuppressWarnings("unused") private ContextTextField(  final String someText) throws HeadlessException {
    super(someText);
    myContext=null;
    ProgrammingError.throwForIllegalInvocation();
  }
  @SuppressWarnings("unused") private ContextTextField(  final String someText,  final int someColumns) throws HeadlessException {
    super(someText,someColumns);
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
