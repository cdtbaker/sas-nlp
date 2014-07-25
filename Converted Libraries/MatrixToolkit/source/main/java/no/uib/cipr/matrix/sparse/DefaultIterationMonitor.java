package no.uib.cipr.matrix.sparse;
import no.uib.cipr.matrix.NotConvergedException;
import no.uib.cipr.matrix.Vector;
/** 
 * Default iteration monitor. This tester checks declares convergence if the
 * absolute value of the residual norm is sufficiently small, or if the relative
 * decrease is small. Divergence is decleared if too many iterations are spent,
 * or the residual has grown too much. NaNs will also cause divergence to be
 * flagged.
 */
public class DefaultIterationMonitor extends AbstractIterationMonitor {
  /** 
 * Initial residual
 */
  double initR;
  /** 
 * Relative tolerance
 */
  double rtol;
  /** 
 * Absolute tolerance
 */
  double atol;
  /** 
 * Divergence tolerance
 */
  double dtol;
  /** 
 * Maximum number of iterations
 */
  int maxIter;
  /** 
 * Constructor for DefaultIterationMonitor
 * @param maxIterMaximum number of iterations
 * @param rtolRelative convergence tolerance (to initial residual)
 * @param atolAbsolute convergence tolerance
 * @param dtolRelative divergence tolerance (to initial residual)
 */
  public DefaultIterationMonitor(  int maxIter,  double rtol,  double atol,  double dtol){
    this.maxIter=maxIter;
    this.rtol=rtol;
    this.atol=atol;
    this.dtol=dtol;
  }
  /** 
 * Constructor for DefaultIterationMonitor. Default is 100000 iterations at
 * most, relative tolerance of 1e-5, absolute tolerance of 1e-50 and a
 * divergence tolerance of 1e+5.
 */
  public DefaultIterationMonitor(){
    this.maxIter=100000;
    this.rtol=1e-5;
    this.atol=1e-50;
    this.dtol=1e+5;
  }
  /** 
 * Sets maximum number of iterations to permit
 * @param maxIterMaximum number of iterations
 */
  public void setMaxIterations(  int maxIter){
    this.maxIter=maxIter;
  }
  /** 
 * Sets the relative tolerance
 * @param rtolRelative convergence tolerance (to initial residual)
 */
  public void setRelativeTolerance(  double rtol){
    this.rtol=rtol;
  }
  /** 
 * Sets the absolute tolerance
 * @param atolAbsolute convergence tolerance
 */
  public void setAbsoluteTolerance(  double atol){
    this.atol=atol;
  }
  /** 
 * Sets the divergence tolerance
 * @param dtolRelative divergence tolerance (to initial residual)
 */
  public void setDivergenceTolerance(  double dtol){
    this.dtol=dtol;
  }
  @Override protected boolean convergedI(  double r) throws IterativeSolverNotConvergedException {
    if (isFirst())     initR=r;
    if (r < Math.max(rtol * initR,atol))     return true;
    if (r > dtol * initR)     throw new IterativeSolverNotConvergedException(NotConvergedException.Reason.Divergence,this);
    if (iter >= maxIter)     throw new IterativeSolverNotConvergedException(NotConvergedException.Reason.Iterations,this);
    if (Double.isNaN(r))     throw new IterativeSolverNotConvergedException(NotConvergedException.Reason.Divergence,this);
    return false;
  }
  @Override protected boolean convergedI(  double r,  Vector x) throws IterativeSolverNotConvergedException {
    return convergedI(r);
  }
}
