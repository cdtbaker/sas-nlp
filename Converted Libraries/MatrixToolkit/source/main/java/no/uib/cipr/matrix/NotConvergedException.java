package no.uib.cipr.matrix;
/** 
 * Signals lack of convergence of an iterative process
 */
public class NotConvergedException extends Exception {
  private static final long serialVersionUID=-2305369220010776320L;
  /** 
 * Possible reasons for lack of convergence
 */
  public enum Reason {  /** 
 * Did not converge after a maximum number of iterations
 */
  Iterations,   /** 
 * Divergence detected
 */
  Divergence,   /** 
 * The iterative process detected a breakdown
 */
  Breakdown}
  /** 
 * The reason for this exception
 */
  protected Reason reason;
  /** 
 * Constructor for NotConvergedException
 * @param reasonThe reason for the lack of convergence
 * @param messageA more descriptive explanation
 */
  public NotConvergedException(  Reason reason,  String message){
    super(message);
    this.reason=reason;
  }
  /** 
 * Constructor for NotConvergedException. No message is provided
 * @param reasonThe reason for the lack of convergence
 */
  public NotConvergedException(  Reason reason){
    this.reason=reason;
  }
  /** 
 * Returns the reason for the exception
 */
  public Reason getReason(){
    return reason;
  }
}
