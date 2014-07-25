package no.uib.cipr.matrix.sparse;
import no.uib.cipr.matrix.Matrix;
import no.uib.cipr.matrix.Vector;
/** 
 * Preconditioner interface. Before a preconditioner is used,
 * <code>setMatrix</code> must be called
 */
public interface Preconditioner {
  /** 
 * Solves the approximate problem with the given right hand side. Result is
 * stored in given solution vector
 * @param bRight hand side of problem
 * @param xResult is stored here
 * @return x
 */
  Vector apply(  Vector b,  Vector x);
  /** 
 * Solves the approximate transpose problem with the given right hand side.
 * Result is stored in given solution vector
 * @param bRight hand side of problem
 * @param xResult is stored here
 * @return x
 */
  Vector transApply(  Vector b,  Vector x);
  /** 
 * Sets the operator matrix for the preconditioner. This method must be
 * called before a preconditioner is used by an iterative solver
 * @param AMatrix to setup the preconditioner for. Not modified
 */
  void setMatrix(  Matrix A);
}
