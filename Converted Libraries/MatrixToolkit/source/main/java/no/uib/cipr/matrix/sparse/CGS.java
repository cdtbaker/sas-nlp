package no.uib.cipr.matrix.sparse;
import no.uib.cipr.matrix.Matrix;
import no.uib.cipr.matrix.NotConvergedException;
import no.uib.cipr.matrix.Vector;
/** 
 * Conjugate Gradients squared solver. CGS solves the unsymmetric linear system
 * <code>Ax = b</code> using the Conjugate Gradient Squared method
 * @author Templates
 */
public class CGS extends AbstractIterativeSolver {
  /** 
 * Vectors for use in the iterative solution process
 */
  private Vector p, q, u, phat, qhat, vhat, uhat, sum, r, rtilde;
  /** 
 * Constructor for CGS. Uses the given vector as template for creating
 * scratch vectors. Typically, the solution or the right hand side vector
 * can be passed, and the template is not modified
 * @param templateVector to use as template for the work vectors needed in the
 * solution process
 */
  public CGS(  Vector template){
    p=template.copy();
    q=template.copy();
    u=template.copy();
    phat=template.copy();
    qhat=template.copy();
    vhat=template.copy();
    uhat=template.copy();
    sum=template.copy();
    r=template.copy();
    rtilde=template.copy();
  }
  public Vector solve(  Matrix A,  Vector b,  Vector x) throws IterativeSolverNotConvergedException {
    checkSizes(A,b,x);
    double rho_1=0, rho_2=0, alpha=0, beta=0;
    A.multAdd(-1,x,r.set(b));
    rtilde.set(r);
    for (iter.setFirst(); !iter.converged(r,x); iter.next()) {
      rho_1=rtilde.dot(r);
      if (rho_1 == 0)       throw new IterativeSolverNotConvergedException(NotConvergedException.Reason.Breakdown,"rho",iter);
      if (iter.isFirst()) {
        u.set(r);
        p.set(u);
      }
 else {
        beta=rho_1 / rho_2;
        u.set(r).add(beta,q);
        sum.set(q).add(beta,p);
        p.set(u).add(beta,sum);
      }
      M.apply(p,phat);
      A.mult(phat,vhat);
      alpha=rho_1 / rtilde.dot(vhat);
      q.set(-alpha,vhat).add(u);
      M.apply(sum.set(u).add(q),uhat);
      x.add(alpha,uhat);
      A.mult(uhat,qhat);
      r.add(-alpha,qhat);
      rho_2=rho_1;
    }
    return x;
  }
}
