package org.apache.commons.math3.exception;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.exception.util.ExceptionContext;
import org.apache.commons.math3.exception.util.ExceptionContextProvider;
/** 
 * Base class for all unsupported features.
 * It is used for all the exceptions that have the semantics of the standard{@link UnsupportedOperationException}, but must also provide a localized
 * message.
 * @since 2.2
 * @version $Id: MathUnsupportedOperationException.java 1364378 2012-07-22 17:42:38Z tn $
 */
public class MathUnsupportedOperationException extends UnsupportedOperationException implements ExceptionContextProvider {
  /** 
 * Serializable version Id. 
 */
  private static final long serialVersionUID=-6024911025449780478L;
  /** 
 * Context. 
 */
  private final ExceptionContext context;
  /** 
 * Default constructor.
 */
  public MathUnsupportedOperationException(){
    this(LocalizedFormats.UNSUPPORTED_OPERATION);
  }
  /** 
 * @param pattern Message pattern providing the specific context of
 * the error.
 * @param args Arguments.
 */
  public MathUnsupportedOperationException(  Localizable pattern,  Object... args){
    context=new ExceptionContext(this);
    context.addMessage(pattern,args);
  }
  /** 
 * {@inheritDoc} 
 */
  public ExceptionContext getContext(){
    return context;
  }
  /** 
 * {@inheritDoc} 
 */
  @Override public String getMessage(){
    return context.getMessage();
  }
  /** 
 * {@inheritDoc} 
 */
  @Override public String getLocalizedMessage(){
    return context.getLocalizedMessage();
  }
}
