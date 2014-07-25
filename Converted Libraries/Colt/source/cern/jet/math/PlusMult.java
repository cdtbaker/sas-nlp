package cern.jet.math;
/** 
 * Only for performance tuning of compute intensive linear algebraic computations.
 * Constructs functions that return one of
 * <ul>
 * <li><tt>a + b*constant</tt>
 * <li><tt>a - b*constant</tt>
 * <li><tt>a + b/constant</tt>
 * <li><tt>a - b/constant</tt>
 * </ul> 
 * <tt>a</tt> and <tt>b</tt> are variables, <tt>constant</tt> is fixed, but for performance reasons publicly accessible.
 * Intended to be passed to <tt>matrix.assign(otherMatrix,function)</tt> methods.
 */
public final class PlusMult implements cern.colt.function.DoubleDoubleFunction {
  /** 
 * Public read/write access to avoid frequent object construction.
 */
  public double multiplicator;
  /** 
 * Insert the method's description here.
 * Creation date: (8/10/99 19:12:09)
 */
  protected PlusMult(  final double multiplicator){
    this.multiplicator=multiplicator;
  }
  /** 
 * Returns the result of the function evaluation.
 */
  public final double apply(  double a,  double b){
    return a + b * multiplicator;
  }
  /** 
 * <tt>a - b/constant</tt>.
 */
  public static PlusMult minusDiv(  final double constant){
    return new PlusMult(-1 / constant);
  }
  /** 
 * <tt>a - b*constant</tt>.
 */
  public static PlusMult minusMult(  final double constant){
    return new PlusMult(-constant);
  }
  /** 
 * <tt>a + b/constant</tt>.
 */
  public static PlusMult plusDiv(  final double constant){
    return new PlusMult(1 / constant);
  }
  /** 
 * <tt>a + b*constant</tt>.
 */
  public static PlusMult plusMult(  final double constant){
    return new PlusMult(constant);
  }
}
