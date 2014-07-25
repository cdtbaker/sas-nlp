package no.uib.cipr.matrix.sparse;
import no.uib.cipr.matrix.Matrix;
import no.uib.cipr.matrix.Vector;
/** 
 * Iterative linear solver. Solves <code>Ax=b</code> for <code>x</code>,
 * and it supports preconditioning and convergence monitoring.
 */
public interface IterativeSolver {
  /** 
 * Solves the given problem, writing result into the vector.
 * @param AMatrix of the problem
 * @param bRight hand side
 * @param xSolution is stored here. Also used as initial guess
 * @return The solution vector x
 */
  Vector solve(  Matrix A,  Vector b,  Vector x) throws IterativeSolverNotConvergedException ;
  /** 
 * Sets preconditioner
 * @param MPreconditioner to use
 */
  void setPreconditioner(  Preconditioner M);
  /** 
 * Gets preconditioner
 * @return Current preconditioner
 */
  Preconditioner getPreconditioner();
  /** 
 * Sets iteration monitor
 * @param iterIteration monitor
 */
  void setIterationMonitor(  IterationMonitor iter);
  /** 
 * Gets the iteration monitor
 * @return Current iteration monitor
 */
  IterationMonitor getIterationMonitor();
}
