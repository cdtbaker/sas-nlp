package org.apache.commons.math3.optimization.linear;
import org.apache.commons.math3.exception.MathIllegalStateException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
/** 
 * This class represents exceptions thrown by optimizers when a solution escapes to infinity.
 * @version $Id: UnboundedSolutionException.java 1422230 2012-12-15 12:11:13Z erans $
 * @deprecated As of 3.1 (to be removed in 4.0).
 * @since 2.0
 */
@Deprecated public class UnboundedSolutionException extends MathIllegalStateException {
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
