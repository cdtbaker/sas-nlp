package org.ojalgo.type.context.awt;
import java.awt.Label;
import org.ojalgo.ProgrammingError;
import org.ojalgo.type.context.TypeContext;
/** 
 * @author apete
 */
public class ContextLabel<T> extends Label {
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
  @SuppressWarnings("unused") private ContextLabel(  final String someText){
    super(someText);
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
