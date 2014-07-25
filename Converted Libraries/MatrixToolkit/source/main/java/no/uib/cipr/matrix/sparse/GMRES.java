package no.uib.cipr.matrix.sparse;
import no.uib.cipr.matrix.DenseMatrix;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.GivensRotation;
import no.uib.cipr.matrix.Matrix;
import no.uib.cipr.matrix.UpperTriangDenseMatrix;
import no.uib.cipr.matrix.Vector;
import no.uib.cipr.matrix.Vector.Norm;
/** 
 * GMRES solver. GMRES solves the unsymmetric linear system <code>Ax = b</code>
 * using the Generalized Minimum Residual method. The GMRES iteration is
 * restarted after a given number of iterations. By default it is restarted
 * after 30 iterations.
 * @author Templates
 */
public class GMRES extends AbstractIterativeSolver {
  /** 
 * After this many iterations, the GMRES will be restarted.
 */
  private int restart;
  /** 
 * Vectors for use in the iterative solution process
 */
  private Vector w, u, r;
  /** 
 * Vectors spanning the subspace
 */
  private Vector[] v;
  /** 
 * Restart vector
 */
  private DenseVector s;
  /** 
 * Hessenberg matrix
 */
  private DenseMatrix H;
  /** 
 * Givens rotations for the QR factorization
 */
  private GivensRotation[] rotation;
  /** 
 * Constructor for GMRES. Uses the given vector as template for creating
 * scratch vectors. Typically, the solution or the right hand side vector
 * can be passed, and the template is not modified. The iteration is
 * restarted every 30 iterations
 * @param templateVector to use as template for the work vectors needed in the
 * solution process
 */
  public GMRES(  Vector template){
    this(template,30);
  }
  /** 
 * Constructor for GMRES. Uses the given vector as template for creating
 * scratch vectors. Typically, the solution or the right hand side vector
 * can be passed, and the template is not modified
 * @param templateVector to use as template for the work vectors needed in the
 * solution process
 * @param restartGMRES iteration is restarted after this number of iterations
 */
  public GMRES(  Vector template,  int restart){
    w=template.copy();
    u=template.copy();
    r=template.copy();
    setRestart(restart);
  }
  /** 
 * Sets the restart parameter
 * @param restartGMRES iteration is restarted after this number of iterations
 */
  public void setRestart(  int restart){
    this.restart=restart;
    if (restart <= 0)     throw new IllegalArgumentException("restart must be a positive integer");
    s=new DenseVector(restart + 1);
    H=new DenseMatrix(restart + 1,restart);
    rotation=new GivensRotation[restart + 1];
    v=new Vector[restart + 1];
    for (int i=0; i < v.length; ++i)     v[i]=r.copy().zero();
  }
  public Vector solve(  Matrix A,  Vector b,  Vector x) throws IterativeSolverNotConvergedException {
    checkSizes(A,b,x);
    A.multAdd(-1,x,u.set(b));
    M.apply(u,r);
    double normr=r.norm(Norm.Two);
    M.apply(b,u);
    for (iter.setFirst(); !iter.converged(r,x); iter.next()) {
      v[0].set(1 / normr,r);
      s.zero().set(0,normr);
      int i=0;
      for (; i < restart && !iter.converged(Math.abs(s.get(i))); i++, iter.next()) {
        A.mult(v[i],u);
        M.apply(u,w);
        for (int k=0; k <= i; k++) {
          H.set(k,i,w.dot(v[k]));
          w.add(-H.get(k,i),v[k]);
        }
        H.set(i + 1,i,w.norm(Norm.Two));
        v[i + 1].set(1. / H.get(i + 1,i),w);
        for (int k=0; k < i; ++k)         rotation[k].apply(H,i,k,k + 1);
        rotation[i]=new GivensRotation(H.get(i,i),H.get(i + 1,i));
        rotation[i].apply(H,i,i,i + 1);
        rotation[i].apply(s,i,i + 1);
      }
      new UpperTriangDenseMatrix(H,i,false).solve(s,s);
      for (int j=0; j < i; j++)       x.add(s.get(j),v[j]);
      A.multAdd(-1,x,u.set(b));
      M.apply(u,r);
      normr=r.norm(Norm.Two);
    }
    return x;
  }
}
