package no.uib.cipr.matrix.sparse;
import no.uib.cipr.matrix.Vector;
import no.uib.cipr.matrix.Vector.Norm;
/** 
 * Monitors the iterative solution process for convergence and divergence. Can
 * also report the current progress.
 */
public interface IterationMonitor {
  /** 
 * Resets the iteration
 */
  void setFirst();
  /** 
 * Returns true for the first iteration
 */
  boolean isFirst();
  /** 
 * Increases iteration counter
 */
  void next();
  /** 
 * Number of iterations performed
 */
  int iterations();
  /** 
 * Returns current residual
 */
  double residual();
  /** 
 * Checks for convergence
 * @param rResidual-vector
 * @param xState-vector
 * @return True if converged
 */
  boolean converged(  Vector r,  Vector x) throws IterativeSolverNotConvergedException ;
  /** 
 * Checks for convergence
 * @param rResidual-norm
 * @param xState-vector
 * @return True if converged
 */
  boolean converged(  double r,  Vector x) throws IterativeSolverNotConvergedException ;
  /** 
 * Checks for convergence
 * @param rResidual-norm
 * @return True if converged
 */
  boolean converged(  double r) throws IterativeSolverNotConvergedException ;
  /** 
 * Checks for convergence
 * @param rResidual-vector
 * @return True if converged
 */
  boolean converged(  Vector r) throws IterativeSolverNotConvergedException ;
  /** 
 * Sets new iteration reporter
 */
  void setIterationReporter(  IterationReporter monitor);
  /** 
 * Returns current iteration reporter
 */
  IterationReporter getIterationReporter();
  /** 
 * Sets the vector-norm to calculate with
 */
  void setNormType(  Norm normType);
  /** 
 * Returns the vector-norm in use
 */
  Norm getNormType();
}
