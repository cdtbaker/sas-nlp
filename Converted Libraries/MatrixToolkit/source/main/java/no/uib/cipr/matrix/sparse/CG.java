package no.uib.cipr.matrix.sparse;
import no.uib.cipr.matrix.Matrix;
import no.uib.cipr.matrix.Vector;
/** 
 * Conjugate Gradients solver. CG solves the symmetric positive definite linear
 * system <code>Ax=b</code> using the Conjugate Gradient method.
 * @author Templates
 */
public class CG extends AbstractIterativeSolver {
  /** 
 * Vectors for use in the iterative solution process
 */
  private Vector p, z, q, r;
  /** 
 * Constructor for CG. Uses the given vector as template for creating
 * scratch vectors. Typically, the solution or the right hand side vector
 * can be passed, and the template is not modified
 * @param templateVector to use as template for the work vectors needed in the
 * solution process
 */
  public CG(  Vector template){
    p=template.copy();
    z=template.copy();
    q=template.copy();
    r=template.copy();
  }
  public Vector solve(  Matrix A,  Vector b,  Vector x) throws IterativeSolverNotConvergedException {
    checkSizes(A,b,x);
    double alpha=0, beta=0, rho=0, rho_1=0;
    A.multAdd(-1,x,r.set(b));
    for (iter.setFirst(); !iter.converged(r,x); iter.next()) {
      M.apply(r,z);
      rho=r.dot(z);
      if (iter.isFirst())       p.set(z);
 else {
        beta=rho / rho_1;
        p.scale(beta).add(z);
      }
      A.mult(p,q);
      alpha=rho / p.dot(q);
      x.add(alpha,p);
      r.add(-alpha,q);
      rho_1=rho;
    }
    return x;
  }
}
