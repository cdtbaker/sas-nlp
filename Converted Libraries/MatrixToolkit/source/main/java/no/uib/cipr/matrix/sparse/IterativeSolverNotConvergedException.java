package no.uib.cipr.matrix.sparse;
import no.uib.cipr.matrix.NotConvergedException;
/** 
 * Exception for lack of convergence in a linear problem. Contains the final
 * computed residual.
 */
public class IterativeSolverNotConvergedException extends NotConvergedException {
  private static final long serialVersionUID=5354102050137093202L;
  /** 
 * Iteration count when this exception was thrown
 */
  private int iterations;
  /** 
 * Final residual
 */
  private double r;
  /** 
 * Constructor for IterativeSolverNotConvergedException
 * @param reasonReason for this exception
 * @param messageA more detailed message
 * @param iterAssociated iteration monitor, for extracting residual and
 * iteration number
 */
  public IterativeSolverNotConvergedException(  Reason reason,  String message,  IterationMonitor iter){
    super(reason,message);
    this.r=iter.residual();
    this.iterations=iter.iterations();
  }
  /** 
 * Constructor for IterativeSolverNotConvergedException
 * @param reasonReason for this exception
 * @param iterAssociated iteration monitor, for extracting residual and
 * iteration number
 */
  public IterativeSolverNotConvergedException(  Reason reason,  IterationMonitor iter){
    super(reason);
    this.r=iter.residual();
    this.iterations=iter.iterations();
  }
  /** 
 * Returns final computed residual
 */
  public double getResidual(){
    return r;
  }
  /** 
 * Gets the number of iterations used when this exception was thrown
 */
  public int getIterations(){
    return iterations;
  }
}
