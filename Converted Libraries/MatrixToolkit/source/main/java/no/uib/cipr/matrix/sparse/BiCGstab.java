package no.uib.cipr.matrix.sparse;
import no.uib.cipr.matrix.Matrix;
import no.uib.cipr.matrix.NotConvergedException;
import no.uib.cipr.matrix.Vector;
/** 
 * BiCG stablized solver. BiCGstab solves the unsymmetric linear system
 * <code>Ax = b</code> using the Preconditioned BiConjugate Gradient
 * Stabilized method
 * @author Templates
 */
public class BiCGstab extends AbstractIterativeSolver {
  /** 
 * Vectors for use in the iterative solution process
 */
  private Vector p, s, phat, shat, t, v, temp, r, rtilde;
  /** 
 * Constructor for BiCGstab. Uses the given vector as template for creating
 * scratch vectors. Typically, the solution or the right hand side vector
 * can be passed, and the template is not modified
 * @param templateVector to use as template for the work vectors needed in the
 * solution process
 */
  public BiCGstab(  Vector template){
    p=template.copy();
    s=template.copy();
    phat=template.copy();
    shat=template.copy();
    t=template.copy();
    v=template.copy();
    temp=template.copy();
    r=template.copy();
    rtilde=template.copy();
  }
  public Vector solve(  Matrix A,  Vector b,  Vector x) throws IterativeSolverNotConvergedException {
    checkSizes(A,b,x);
    double rho_1=1, rho_2=1, alpha=1, beta=1, omega=1;
    A.multAdd(-1,x,r.set(b));
    rtilde.set(r);
    for (iter.setFirst(); !iter.converged(r,x); iter.next()) {
      rho_1=rtilde.dot(r);
      if (rho_1 == 0)       throw new IterativeSolverNotConvergedException(NotConvergedException.Reason.Breakdown,"rho",iter);
      if (omega == 0)       throw new IterativeSolverNotConvergedException(NotConvergedException.Reason.Breakdown,"omega",iter);
      if (iter.isFirst())       p.set(r);
 else {
        beta=(rho_1 / rho_2) * (alpha / omega);
        temp.set(-omega,v).add(p);
        p.set(r).add(beta,temp);
      }
      M.apply(p,phat);
      A.mult(phat,v);
      alpha=rho_1 / rtilde.dot(v);
      s.set(r).add(-alpha,v);
      x.add(alpha,phat);
      if (iter.converged(s,x))       return x;
      M.apply(s,shat);
      A.mult(shat,t);
      omega=t.dot(s) / t.dot(t);
      x.add(omega,shat);
      r.set(s).add(-omega,t);
      rho_2=rho_1;
    }
    return x;
  }
}
