package org.jscience.mathematics.vector;
/** 
 * Signals that an operation is performed upon vectors or matrices whose
 * dimensions disagree.
 * @author <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 3.0, February 13, 2006
 */
public class DimensionException extends RuntimeException {
  /** 
 * Constructs a dimension exception with no detail message.
 */
  public DimensionException(){
    super();
  }
  /** 
 * Constructs a dimension exception with the specified message.
 * @param message the error message.
 */
  public DimensionException(  String message){
    super(message);
  }
  private static final long serialVersionUID=1L;
}
