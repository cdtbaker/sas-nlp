package org.apache.commons.math3.optim.linear;
import org.apache.commons.math3.exception.MathIllegalStateException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
/** 
 * This class represents exceptions thrown by optimizers when a solution escapes to infinity.
 * @version $Id: UnboundedSolutionException.java 1435539 2013-01-19 13:27:24Z tn $
 * @since 2.0
 */
public class UnboundedSolutionException extends MathIllegalStateException {
  /** 
 * Serializable version identifier. 
 */
  private static final long serialVersionUID=940539497277290619L;
  /** 
 * Simple constructor using a default message.
 */
  public UnboundedSolutionException(){
    super(LocalizedFormats.UNBOUNDED_SOLUTION);
  }
}