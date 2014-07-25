package org.apache.commons.math3.optimization.linear;
import org.apache.commons.math3.exception.MathIllegalStateException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
/** 
 * This class represents exceptions thrown by optimizers when no solution fulfills the constraints.
 * @version $Id: NoFeasibleSolutionException.java 1422230 2012-12-15 12:11:13Z erans $
 * @deprecated As of 3.1 (to be removed in 4.0).
 * @since 2.0
 */
@Deprecated public class NoFeasibleSolutionException extends MathIllegalStateException {
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
