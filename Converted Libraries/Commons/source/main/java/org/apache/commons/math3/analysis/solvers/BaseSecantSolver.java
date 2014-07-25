package org.apache.commons.math3.analysis.solvers;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.exception.ConvergenceException;
import org.apache.commons.math3.exception.MathInternalError;
/** 
 * Base class for all bracketing <em>Secant</em>-based methods for root-finding
 * (approximating a zero of a univariate real function).
 * <p>Implementation of the {@link RegulaFalsiSolver <em>Regula Falsi</em>} and{@link IllinoisSolver <em>Illinois</em>} methods is based on the
 * following article: M. Dowell and P. Jarratt,
 * <em>A modified regula falsi method for computing the root of an
 * equation</em>, BIT Numerical Mathematics, volume 11, number 2,
 * pages 168-174, Springer, 1971.</p>
 * <p>Implementation of the {@link PegasusSolver <em>Pegasus</em>} method is
 * based on the following article: M. Dowell and P. Jarratt,
 * <em>The "Pegasus" method for computing the root of an equation</em>,
 * BIT Numerical Mathematics, volume 12, number 4, pages 503-508, Springer,
 * 1972.</p>
 * <p>The {@link SecantSolver <em>Secant</em>} method is <em>not</em> a
 * bracketing method, so it is not implemented here. It has a separate
 * implementation.</p>
 * @since 3.0
 * @version $Id: BaseSecantSolver.java 1455194 2013-03-11 15:45:54Z luc $
 */
public abstract class BaseSecantSolver extends AbstractUnivariateSolver implements BracketedUnivariateSolver<UnivariateFunction> {
  /** 
 * Default absolute accuracy. 
 */
  protected static final double DEFAULT_ABSOLUTE_ACCURACY=1e-6;
  /** 
 * The kinds of solutions that the algorithm may accept. 
 */
  private AllowedSolution allowed;
  /** 
 * The <em>Secant</em>-based root-finding method to use. 
 */
  private final Method method;
  /** 
 * Construct a solver.
 * @param absoluteAccuracy Absolute accuracy.
 * @param method <em>Secant</em>-based root-finding method to use.
 */
  protected BaseSecantSolver(  final double absoluteAccuracy,  final Method method){
    super(absoluteAccuracy);
    this.allowed=AllowedSolution.ANY_SIDE;
    this.method=method;
  }
  /** 
 * Construct a solver.
 * @param relativeAccuracy Relative accuracy.
 * @param absoluteAccuracy Absolute accuracy.
 * @param method <em>Secant</em>-based root-finding method to use.
 */
  protected BaseSecantSolver(  final double relativeAccuracy,  final double absoluteAccuracy,  final Method method){
    super(relativeAccuracy,absoluteAccuracy);
    this.allowed=AllowedSolution.ANY_SIDE;
    this.method=method;
  }
  /** 
 * Construct a solver.
 * @param relativeAccuracy Maximum relative error.
 * @param absoluteAccuracy Maximum absolute error.
 * @param functionValueAccuracy Maximum function value error.
 * @param method <em>Secant</em>-based root-finding method to use
 */
  protected BaseSecantSolver(  final double relativeAccuracy,  final double absoluteAccuracy,  final double functionValueAccuracy,  final Method method){
    super(relativeAccuracy,absoluteAccuracy,functionValueAccuracy);
    this.allowed=AllowedSolution.ANY_SIDE;
    this.method=method;
  }
  /** 
 * {@inheritDoc} 
 */
  public double solve(  final int maxEval,  final UnivariateFunction f,  final double min,  final double max,  final AllowedSolution allowedSolution){
    return solve(maxEval,f,min,max,min + 0.5 * (max - min),allowedSolution);
  }
  /** 
 * {@inheritDoc} 
 */
  public double solve(  final int maxEval,  final UnivariateFunction f,  final double min,  final double max,  final double startValue,  final AllowedSolution allowedSolution){
    this.allowed=allowedSolution;
    return super.solve(maxEval,f,min,max,startValue);
  }
  /** 
 * {@inheritDoc} 
 */
  @Override public double solve(  final int maxEval,  final UnivariateFunction f,  final double min,  final double max,  final double startValue){
    return solve(maxEval,f,min,max,startValue,AllowedSolution.ANY_SIDE);
  }
  /** 
 * {@inheritDoc}
 * @throws ConvergenceException if the algorithm failed due to finite
 * precision.
 */
  @Override protected final double doSolve() throws ConvergenceException {
    double x0=getMin();
    double x1=getMax();
    double f0=computeObjectiveValue(x0);
    double f1=computeObjectiveValue(x1);
    if (f0 == 0.0) {
      return x0;
    }
    if (f1 == 0.0) {
      return x1;
    }
    verifyBracketing(x0,x1);
    final double ftol=getFunctionValueAccuracy();
    final double atol=getAbsoluteAccuracy();
    final double rtol=getRelativeAccuracy();
    boolean inverted=false;
    while (true) {
      final double x=x1 - ((f1 * (x1 - x0)) / (f1 - f0));
      final double fx=computeObjectiveValue(x);
      if (fx == 0.0) {
        return x;
      }
      if (f1 * fx < 0) {
        x0=x1;
        f0=f1;
        inverted=!inverted;
      }
 else {
switch (method) {
case ILLINOIS:
          f0*=0.5;
        break;
case PEGASUS:
      f0*=f1 / (f1 + fx);
    break;
case REGULA_FALSI:
  if (x == x1) {
    throw new ConvergenceException();
  }
break;
default :
throw new MathInternalError();
}
}
x1=x;
f1=fx;
if (FastMath.abs(f1) <= ftol) {
switch (allowed) {
case ANY_SIDE:
return x1;
case LEFT_SIDE:
if (inverted) {
return x1;
}
break;
case RIGHT_SIDE:
if (!inverted) {
return x1;
}
break;
case BELOW_SIDE:
if (f1 <= 0) {
return x1;
}
break;
case ABOVE_SIDE:
if (f1 >= 0) {
return x1;
}
break;
default :
throw new MathInternalError();
}
}
if (FastMath.abs(x1 - x0) < FastMath.max(rtol * FastMath.abs(x1),atol)) {
switch (allowed) {
case ANY_SIDE:
return x1;
case LEFT_SIDE:
return inverted ? x1 : x0;
case RIGHT_SIDE:
return inverted ? x0 : x1;
case BELOW_SIDE:
return (f1 <= 0) ? x1 : x0;
case ABOVE_SIDE:
return (f1 >= 0) ? x1 : x0;
default :
throw new MathInternalError();
}
}
}
}
/** 
 * <em>Secant</em>-based root-finding methods. 
 */
protected enum Method {/** 
 * The {@link RegulaFalsiSolver <em>Regula Falsi</em>} or
 * <em>False Position</em> method.
 */
REGULA_FALSI, /** 
 * The {@link IllinoisSolver <em>Illinois</em>} method. 
 */
ILLINOIS, /** 
 * The {@link PegasusSolver <em>Pegasus</em>} method. 
 */
PEGASUS}
}
