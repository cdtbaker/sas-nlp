package org.apache.commons.math3.exception;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.ExceptionContext;
import org.apache.commons.math3.exception.util.ExceptionContextProvider;
/** 
 * As of release 4.0, all exceptions thrown by the Commons Math code (except{@link NullArgumentException}) inherit from this class.
 * In most cases, this class should not be instantiated directly: it should
 * serve as a base class for implementing exception classes that describe a
 * specific "problem".
 * @since 3.1
 * @version $Id: MathRuntimeException.java 1416643 2012-12-03 19:37:14Z tn $
 */
public class MathRuntimeException extends RuntimeException implements ExceptionContextProvider {
  /** 
 * Serializable version Id. 
 */
  private static final long serialVersionUID=20120926L;
  /** 
 * Context. 
 */
  private final ExceptionContext context;
  /** 
 * @param pattern Message pattern explaining the cause of the error.
 * @param args Arguments.
 */
  public MathRuntimeException(  Localizable pattern,  Object... args){
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
