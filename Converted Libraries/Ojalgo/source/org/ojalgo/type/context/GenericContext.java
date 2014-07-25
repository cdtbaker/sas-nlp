package org.ojalgo.type.context;
import java.text.Format;
/** 
 * @author apete
 */
public final class GenericContext<T> extends FormatContext<T> {
  private final TypeContext<T> myDelegate;
  public GenericContext(  final Format format){
    super(format);
    myDelegate=null;
  }
  @SuppressWarnings("unchecked") GenericContext(  final TypeContext<?> delegate,  final Format format){
    super(format);
    myDelegate=(TypeContext<T>)delegate;
  }
  public T enforce(  final T object){
    if (myDelegate != null) {
      return myDelegate.enforce(object);
    }
 else {
      return this.parse(this.format(object));
    }
  }
  @Override protected void configureFormat(  final Format format,  final Object object){
  }
  @Override protected String handleFormatException(  final Format format,  final Object object){
    return myDelegate.format(object);
  }
  @Override protected T handleParseException(  final Format format,  final String string){
    return myDelegate.parse(string);
  }
}
