package org.apache.commons.math3.analysis.solvers;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.exception.NoBracketingException;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
/** 
 * Implements the <em>Secant</em> method for root-finding (approximating a
 * zero of a univariate real function). The solution that is maintained is
 * not bracketed, and as such convergence is not guaranteed.
 * <p>Implementation based on the following article: M. Dowell and P. Jarratt,
 * <em>A modified regula falsi method for computing the root of an
 * equation</em>, BIT Numerical Mathematics, volume 11, number 2,
 * pages 168-174, Springer, 1971.</p>
 * <p>Note that since release 3.0 this class implements the actual
 * <em>Secant</em> algorithm, and not a modified one. As such, the 3.0 version
 * is not backwards compatible with previous versions. To use an algorithm
 * similar to the pre-3.0 releases, use the{@link IllinoisSolver <em>Illinois</em>} algorithm or the{@link PegasusSolver <em>Pegasus</em>} algorithm.</p>
 * @version $Id: SecantSolver.java 1379560 2012-08-31 19:40:30Z erans $
 */
public class SecantSolver extends AbstractUnivariateSolver {
  /** 
 * Default absolute accuracy. 
 */
  protected static final double DEFAULT_ABSOLUTE_ACCURACY=1e-6;
  /** 
 * Construct a solver with default accuracy (1e-6). 
 */
  public SecantSolver(){
    super(DEFAULT_ABSOLUTE_ACCURACY);
  }
  /** 
 * Construct a solver.
 * @param absoluteAccuracy absolute accuracy
 */
  public SecantSolver(  final double absoluteAccuracy){
    super(absoluteAccuracy);
  }
  /** 
 * Construct a solver.
 * @param relativeAccuracy relative accuracy
 * @param absoluteAccuracy absolute accuracy
 */
  public SecantSolver(  final double relativeAccuracy,  final double absoluteAccuracy){
    super(relativeAccuracy,absoluteAccuracy);
  }
  /** 
 * {@inheritDoc} 
 */
  @Override protected final double doSolve() throws TooManyEvaluationsException, NoBracketingException {
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
    while (true) {
      final double x=x1 - ((f1 * (x1 - x0)) / (f1 - f0));
      final double fx=computeObjectiveValue(x);
      if (fx == 0.0) {
        return x;
      }
      x0=x1;
      f0=f1;
      x1=x;
      f1=fx;
      if (FastMath.abs(f1) <= ftol) {
        return x1;
      }
      if (FastMath.abs(x1 - x0) < FastMath.max(rtol * FastMath.abs(x1),atol)) {
        return x1;
      }
    }
  }
}
