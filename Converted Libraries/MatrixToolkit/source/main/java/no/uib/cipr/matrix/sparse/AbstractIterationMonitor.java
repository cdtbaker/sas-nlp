package no.uib.cipr.matrix.sparse;
import no.uib.cipr.matrix.Vector;
import no.uib.cipr.matrix.Vector.Norm;
/** 
 * Partial implementation of an iteration reporter
 */
public abstract class AbstractIterationMonitor implements IterationMonitor {
  /** 
 * Iteration number
 */
  protected int iter;
  /** 
 * Vector-norm
 */
  protected Norm normType;
  /** 
 * Iteration reporter
 */
  protected IterationReporter reporter;
  /** 
 * Current residual
 */
  protected double residual;
  /** 
 * Constructor for AbstractIterationMonitor. Default norm is the 2-norm with
 * no iteration reporting.
 */
  public AbstractIterationMonitor(){
    normType=Norm.Two;
    reporter=new NoIterationReporter();
  }
  public void setFirst(){
    iter=0;
  }
  public boolean isFirst(){
    return iter == 0;
  }
  public void next(){
    iter++;
  }
  public int iterations(){
    return iter;
  }
  public boolean converged(  Vector r,  Vector x) throws IterativeSolverNotConvergedException {
    return converged(r.norm(normType),x);
  }
  public boolean converged(  double r,  Vector x) throws IterativeSolverNotConvergedException {
    reporter.monitor(r,x,iter);
    this.residual=r;
    return convergedI(r,x);
  }
  public boolean converged(  double r) throws IterativeSolverNotConvergedException {
    reporter.monitor(r,iter);
    this.residual=r;
    return convergedI(r);
  }
  protected abstract boolean convergedI(  double r,  Vector x) throws IterativeSolverNotConvergedException ;
  protected abstract boolean convergedI(  double r) throws IterativeSolverNotConvergedException ;
  public boolean converged(  Vector r) throws IterativeSolverNotConvergedException {
    return converged(r.norm(normType));
  }
  public Norm getNormType(){
    return normType;
  }
  public void setNormType(  Norm normType){
    this.normType=normType;
  }
  public IterationReporter getIterationReporter(){
    return reporter;
  }
  public void setIterationReporter(  IterationReporter monitor){
    this.reporter=monitor;
  }
  public double residual(){
    return residual;
  }
}
