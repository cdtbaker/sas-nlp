package org.jscience.mathematics.function;
/** 
 * <p> Thrown when a function operation cannot be performed.</p>
 * @author  <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 1.0, January 24, 2004
 */
public class FunctionException extends RuntimeException {
  /** 
 * Constructs a {@link FunctionException} with no detail message.
 */
  public FunctionException(){
    super();
  }
  /** 
 * Constructs a {@link FunctionException} with the specified message.
 * @param message the message.
 */
  public FunctionException(  String message){
    super(message);
  }
  private static final long serialVersionUID=1L;
}
