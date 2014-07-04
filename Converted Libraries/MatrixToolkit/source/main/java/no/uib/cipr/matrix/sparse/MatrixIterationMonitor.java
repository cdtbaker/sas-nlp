package no.uib.cipr.matrix.sparse;
import no.uib.cipr.matrix.NotConvergedException;
import no.uib.cipr.matrix.Vector;
/** 
 * Iteration monitor based on matrix norms. Extends the default linear iteration
 * object to compare with the norm of the system matrix and the right hand side.
 * Can often be a better convergence criteria than the default, but requires the
 * computation of the matrix norm.
 */
public class MatrixIterationMonitor extends DefaultIterationMonitor {
  /** 
 * Norm of the system matrix
 */
  private double normA;
  /** 
 * Norm of the right hand side
 */
  private double normb;
  /** 
 * Constructor for MatrixIterationMonitor
 * @param normANorm of the matrix A
 * @param normbNorm of the vector b
 * @param maxIterMaximum number of iterations
 * @param rtolRelative convergence tolerance (to initial residual)
 * @param atolAbsolute convergence tolerance
 * @param dtolRelative divergence tolerance (to initial residual)
 */
  public MatrixIterationMonitor(  double normA,  double normb,  int maxIter,  double rtol,  double atol,  double dtol){
    this.normA=normA;
    this.normb=normb;
    this.maxIter=maxIter;
    this.rtol=rtol;
    this.atol=atol;
    this.dtol=dtol;
  }
  /** 
 * Constructor for MatrixIterationMonitor. Default is 100000 iterations at
 * most, relative tolerance of 1e-5, absolute tolerance of 1e-50 and a
 * divergence tolerance of 1e+5.
 */
  public MatrixIterationMonitor(  double normA,  double normb){
    this.normA=normA;
    this.normb=normb;
  }
  /** 
 * Sets the norm of the system matrix
 * @param normANorm of the matrix A
 */
  public void setMatrixNorm(  double normA){
    this.normA=normA;
  }
  /** 
 * Sets the norm of the right hand side vector
 * @param normbNorm of the vector b
 */
  public void setVectorNorm(  double normb){
    this.normb=normb;
  }
  @Override protected boolean convergedI(  double r,  Vector x) throws IterativeSolverNotConvergedException {
    if (isFirst())     initR=r;
    if (r < Math.max(rtol * (normA * x.norm(normType) + normb),atol))     return true;
    if (r > dtol * initR)     throw new IterativeSolverNotConvergedException(NotConvergedException.Reason.Divergence,this);
    if (iter >= maxIter)     throw new IterativeSolverNotConvergedException(NotConvergedException.Reason.Iterations,this);
    if (Double.isNaN(r))     throw new IterativeSolverNotConvergedException(NotConvergedException.Reason.Divergence,this);
    return false;
  }
  @Override protected boolean convergedI(  double r){
    throw new UnsupportedOperationException();
  }
}
