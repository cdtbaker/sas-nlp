package org.apache.commons.math3.optimization;
/** 
 * This interface is mainly intended to enforce the internal coherence of
 * Commons-Math. Users of the API are advised to base their code on
 * the following interfaces:
 * <ul>
 * <li>{@link org.apache.commons.math3.optimization.MultivariateOptimizer}</li>
 * <li>{@link org.apache.commons.math3.optimization.MultivariateDifferentiableOptimizer}</li>
 * <li>{@link org.apache.commons.math3.optimization.MultivariateDifferentiableVectorOptimizer}</li>
 * <li>{@link org.apache.commons.math3.optimization.univariate.UnivariateOptimizer}</li>
 * </ul>
 * @param<PAIR>
 *  Type of the point/objective pair.
 * @version $Id: BaseOptimizer.java 1422230 2012-12-15 12:11:13Z erans $
 * @deprecated As of 3.1 (to be removed in 4.0).
 * @since 3.0
 */
@Deprecated public interface BaseOptimizer<PAIR> {
  /** 
 * Get the maximal number of function evaluations.
 * @return the maximal number of function evaluations.
 */
  int getMaxEvaluations();
  /** 
 * Get the number of evaluations of the objective function.
 * The number of evaluations corresponds to the last call to the{@code optimize} method. It is 0 if the method has not been
 * called yet.
 * @return the number of evaluations of the objective function.
 */
  int getEvaluations();
  /** 
 * Get the convergence checker.
 * @return the object used to check for convergence.
 */
  ConvergenceChecker<PAIR> getConvergenceChecker();
}
