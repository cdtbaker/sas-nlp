package cern.jet.math;
/** 
 * Only for performance tuning of compute intensive linear algebraic computations.
 * Constructs functions that return one of
 * <ul>
 * <li><tt>a * constant</tt>
 * <li><tt>a / constant</tt>
 * </ul> 
 * <tt>a</tt> is variable, <tt>constant</tt> is fixed, but for performance reasons publicly accessible.
 * Intended to be passed to <tt>matrix.assign(function)</tt> methods.
 */
public final class Mult implements cern.colt.function.DoubleFunction {
  /** 
 * Public read/write access to avoid frequent object construction.
 */
  public double multiplicator;
  /** 
 * Insert the method's description here.
 * Creation date: (8/10/99 19:12:09)
 */
  protected Mult(  final double multiplicator){
    this.multiplicator=multiplicator;
  }
  /** 
 * Returns the result of the function evaluation.
 */
  public final double apply(  double a){
    return a * multiplicator;
  }
  /** 
 * <tt>a / constant</tt>.
 */
  public static Mult div(  final double constant){
    return mult(1 / constant);
  }
  /** 
 * <tt>a * constant</tt>.
 */
  public static Mult mult(  final double constant){
    return new Mult(constant);
  }
}
