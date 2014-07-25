package org.apache.commons.math3.optimization.general;
/** 
 * This interface represents a preconditioner for differentiable scalar
 * objective function optimizers.
 * @version $Id: Preconditioner.java 1422230 2012-12-15 12:11:13Z erans $
 * @deprecated As of 3.1 (to be removed in 4.0).
 * @since 2.0
 */
@Deprecated public interface Preconditioner {
  /** 
 * Precondition a search direction.
 * <p>
 * The returned preconditioned search direction must be computed fast or
 * the algorithm performances will drop drastically. A classical approach
 * is to compute only the diagonal elements of the hessian and to divide
 * the raw search direction by these elements if they are all positive.
 * If at least one of them is negative, it is safer to return a clone of
 * the raw search direction as if the hessian was the identity matrix. The
 * rationale for this simplified choice is that a negative diagonal element
 * means the current point is far from the optimum and preconditioning will
 * not be efficient anyway in this case.
 * </p>
 * @param point current point at which the search direction was computed
 * @param r raw search direction (i.e. opposite of the gradient)
 * @return approximation of H<sup>-1</sup>r where H is the objective function hessian
 */
  double[] precondition(  double[] point,  double[] r);
}
