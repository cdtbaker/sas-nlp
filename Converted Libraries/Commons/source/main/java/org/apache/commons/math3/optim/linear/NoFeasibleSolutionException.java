package org.apache.commons.math3.optim.linear;
import org.apache.commons.math3.exception.MathIllegalStateException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
/** 
 * This class represents exceptions thrown by optimizers when no solution fulfills the constraints.
 * @version $Id: NoFeasibleSolutionException.java 1435539 2013-01-19 13:27:24Z tn $
 * @since 2.0
 */
public class NoFeasibleSolutionException extends MathIllegalStateException {
  /** 
 * Serializable version identifier. 
 */
  private static final long serialVersionUID=-3044253632189082760L;
  /** 
 * Simple constructor using a default message.
 */
  public NoFeasibleSolutionException(){
    super(LocalizedFormats.NO_FEASIBLE_SOLUTION);
  }
}
