package no.uib.cipr.matrix.sparse;
import no.uib.cipr.matrix.Matrix;
import no.uib.cipr.matrix.Vector;
/** 
 * Iterative Refinement. IR solves the unsymmetric linear system
 * <code>Ax = b</code> using Iterative Refinement (preconditioned Richardson
 * iteration).
 * @author Templates
 */
public class IR extends AbstractIterativeSolver {
  /** 
 * Vectors for use in the iterative solution process
 */
  private Vector z, r;
  /** 
 * Constructor for IR. Uses the given vector as template for creating
 * scratch vectors. Typically, the solution or the right hand side vector
 * can be passed, and the template is not modified
 * @param templateVector to use as template for the work vectors needed in the
 * solution process
 */
  public IR(  Vector template){
    z=template.copy();
    r=template.copy();
  }
  public Vector solve(  Matrix A,  Vector b,  Vector x) throws IterativeSolverNotConvergedException {
    checkSizes(A,b,x);
    A.multAdd(-1,x,r.set(b));
    for (iter.setFirst(); !iter.converged(r,x); iter.next()) {
      M.apply(r,z);
      x.add(z);
      A.multAdd(-1,x,r.set(b));
    }
    return x;
  }
}
