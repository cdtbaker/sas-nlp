package no.uib.cipr.matrix.sparse;
import no.uib.cipr.matrix.Matrix;
import no.uib.cipr.matrix.NotConvergedException;
import no.uib.cipr.matrix.Vector;
/** 
 * BiCG solver. BiCG solves the unsymmetric linear system <code>Ax = b</code>
 * using the Preconditioned BiConjugate Gradient method.
 * @author Templates
 */
public class BiCG extends AbstractIterativeSolver {
  /** 
 * Vectors for use in the iterative solution process
 */
  private Vector z, p, q, r, ztilde, ptilde, qtilde, rtilde;
  /** 
 * Constructor for BiCG. Uses the given vector as template for creating
 * scratch vectors. Typically, the solution or the right hand side vector
 * can be passed, and the template is not modified
 * @param templateVector to use as template for the work vectors needed in the
 * solution process
 */
  public BiCG(  Vector template){
    z=template.copy();
    p=template.copy();
    q=template.copy();
    r=template.copy();
    ztilde=template.copy();
    ptilde=template.copy();
    qtilde=template.copy();
    rtilde=template.copy();
  }
  public Vector solve(  Matrix A,  Vector b,  Vector x) throws IterativeSolverNotConvergedException {
    checkSizes(A,b,x);
    double rho_1=1, rho_2=1, alpha=1, beta=1;
    A.multAdd(-1,x,r.set(b));
    rtilde.set(r);
    for (iter.setFirst(); !iter.converged(r,x); iter.next()) {
      M.apply(r,z);
      M.transApply(rtilde,ztilde);
      rho_1=z.dot(rtilde);
      if (rho_1 == 0.)       throw new IterativeSolverNotConvergedException(NotConvergedException.Reason.Breakdown,"rho",iter);
      if (iter.isFirst()) {
        p.set(z);
        ptilde.set(ztilde);
      }
 else {
        beta=rho_1 / rho_2;
        p.scale(beta).add(z);
        ptilde.scale(beta).add(ztilde);
      }
      A.mult(p,q);
      A.transMult(ptilde,qtilde);
      alpha=rho_1 / ptilde.dot(q);
      x.add(alpha,p);
      r.add(-alpha,q);
      rtilde.add(-alpha,qtilde);
      rho_2=rho_1;
    }
    return x;
  }
}
