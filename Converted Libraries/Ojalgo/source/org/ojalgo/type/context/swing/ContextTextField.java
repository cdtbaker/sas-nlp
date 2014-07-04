package org.ojalgo.type.context.swing;
import java.beans.PropertyChangeListener;
import java.text.Format;
import java.text.ParseException;
import javax.swing.JFormattedTextField;
import javax.swing.SwingConstants;
import org.ojalgo.ProgrammingError;
import org.ojalgo.type.context.TypeContext;
/** 
 * @author apete
 */
public class ContextTextField<T> extends JFormattedTextField {
private static final class ContextFormatter<T> extends AbstractFormatter {
    private final TypeContext<T> myContext;
    public ContextFormatter(    final TypeContext<T> aContext){
      super();
      myContext=aContext;
    }
    @SuppressWarnings("unused") private ContextFormatter(){
      this(null);
    }
    @Override public Object stringToValue(    final String aString) throws ParseException {
      return myContext.parse(aString);
    }
    @SuppressWarnings("unchecked") @Override public String valueToString(    final Object anObject) throws ParseException {
      return myContext.format((T)anObject);
    }
  }
  private static final String VALUE="value";
  private final TypeContext<T> myContext;
  public ContextTextField(  final T aModelObject,  final TypeContext<T> aContext){
    this(aContext);
    this.setModelObject(aModelObject);
  }
  public ContextTextField(  final TypeContext<T> aContext){
    super();
    myContext=aContext;
    this.setHorizontalAlignment(SwingConstants.RIGHT);
  }
  @SuppressWarnings("unused") private ContextTextField(){
    super();
    myContext=null;
    ProgrammingError.throwForIllegalInvocation();
  }
  @SuppressWarnings("unused") private ContextTextField(  final AbstractFormatter someFormatter){
    super(someFormatter);
    myContext=null;
    ProgrammingError.throwForIllegalInvocation();
  }
  @SuppressWarnings("unused") private ContextTextField(  final AbstractFormatterFactory someFactory){
    super(someFactory);
    myContext=null;
    ProgrammingError.throwForIllegalInvocation();
  }
  @SuppressWarnings("unused") private ContextTextField(  final AbstractFormatterFactory someFactory,  final Object someCurrentValue){
    super(someFactory,someCurrentValue);
    myContext=null;
    ProgrammingError.throwForIllegalInvocation();
  }
  @SuppressWarnings("unused") private ContextTextField(  final Format someFormat){
    super(someFormat);
    myContext=null;
    ProgrammingError.throwForIllegalInvocation();
  }
  @SuppressWarnings("unused") private ContextTextField(  final int someColumns){
    super(someColumns);
    myContext=null;
    ProgrammingError.throwForIllegalInvocation();
  }
  @SuppressWarnings("unused") private ContextTextField(  final Object someValue){
    super(someValue);
    myContext=null;
    ProgrammingError.throwForIllegalInvocation();
  }
  @SuppressWarnings("unused") private ContextTextField(  final String someText){
    super(someText);
    myContext=null;
    ProgrammingError.throwForIllegalInvocation();
  }
  public final void addValueChangeListener(  final PropertyChangeListener aListener){
    this.addPropertyChangeListener(VALUE,aListener);
  }
  public final void fireValueChange(){
    this.firePropertyChange(VALUE,this.getModelObject(),this.getModelObject());
  }
  @Override public AbstractFormatterFactory getFormatterFactory(){
    return new AbstractFormatterFactory(){
      @Override public AbstractFormatter getFormatter(      final JFormattedTextField aTextField){
        return new ContextFormatter<T>(myContext);
      }
    }
;
  }
  @SuppressWarnings("unchecked") public final T getModelObject(){
    return (T)this.getValue();
  }
  public final void setModelObject(  final T aModelObject){
    this.setValue(aModelObject);
  }
}
