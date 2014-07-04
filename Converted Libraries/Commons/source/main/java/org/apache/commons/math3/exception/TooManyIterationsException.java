package org.apache.commons.math3.exception;
import org.apache.commons.math3.exception.util.LocalizedFormats;
/** 
 * Exception to be thrown when the maximal number of iterations is exceeded.
 * @since 3.1
 * @version $Id$
 */
public class TooManyIterationsException extends MaxCountExceededException {
  /** 
 * Serializable version Id. 
 */
  private static final long serialVersionUID=20121211L;
  /** 
 * Construct the exception.
 * @param max Maximum number of evaluations.
 */
  public TooManyIterationsException(  Number max){
    super(max);
    getContext().addMessage(LocalizedFormats.ITERATIONS);
  }
}
