package org.apache.commons.math3.distribution;
import java.io.Serializable;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.solvers.UnivariateSolverUtils;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.RandomDataImpl;
import org.apache.commons.math3.util.FastMath;
/** 
 * Base class for probability distributions on the reals.
 * Default implementations are provided for some of the methods
 * that do not vary from distribution to distribution.
 * @version $Id: AbstractRealDistribution.java 1422195 2012-12-15 06:45:18Z psteitz $
 * @since 3.0
 */
public abstract class AbstractRealDistribution implements RealDistribution, Serializable {
  /** 
 * Default accuracy. 
 */
  public static final double SOLVER_DEFAULT_ABSOLUTE_ACCURACY=1e-6;
  /** 
 * Serializable version identifier 
 */
  private static final long serialVersionUID=-38038050983108802L;
  /** 
 * RandomData instance used to generate samples from the distribution.
 * @deprecated As of 3.1, to be removed in 4.0. Please use the{@link #random} instance variable instead.
 */
  @Deprecated protected RandomDataImpl randomData=new RandomDataImpl();
  /** 
 * RNG instance used to generate samples from the distribution.
 * @since 3.1
 */
  protected final RandomGenerator random;
  /** 
 * Solver absolute accuracy for inverse cumulative computation 
 */
  private double solverAbsoluteAccuracy=SOLVER_DEFAULT_ABSOLUTE_ACCURACY;
  /** 
 * @deprecated As of 3.1, to be removed in 4.0. Please use{@link #AbstractRealDistribution(RandomGenerator)} instead.
 */
  @Deprecated protected AbstractRealDistribution(){
    random=null;
  }
  /** 
 * @param rng Random number generator.
 * @since 3.1
 */
  protected AbstractRealDistribution(  RandomGenerator rng){
    random=rng;
  }
  /** 
 * {@inheritDoc}The default implementation uses the identity
 * <p>{@code P(x0 < X <= x1) = P(X <= x1) - P(X <= x0)}</p>
 * @deprecated As of 3.1 (to be removed in 4.0). Please use{@link #probability(double,double)} instead.
 */
  @Deprecated public double cumulativeProbability(  double x0,  double x1) throws NumberIsTooLargeException {
    return probability(x0,x1);
  }
  /** 
 * For a random variable {@code X} whose values are distributed according
 * to this distribution, this method returns {@code P(x0 < X <= x1)}.
 * @param x0 Lower bound (excluded).
 * @param x1 Upper bound (included).
 * @return the probability that a random variable with this distribution
 * takes a value between {@code x0} and {@code x1}, excluding the lower
 * and including the upper endpoint.
 * @throws NumberIsTooLargeException if {@code x0 > x1}.
 * The default implementation uses the identity{@code P(x0 < X <= x1) = P(X <= x1) - P(X <= x0)}
 * @since 3.1
 */
  public double probability(  double x0,  double x1){
    if (x0 > x1) {
      throw new NumberIsTooLargeException(LocalizedFormats.LOWER_ENDPOINT_ABOVE_UPPER_ENDPOINT,x0,x1,true);
    }
    return cumulativeProbability(x1) - cumulativeProbability(x0);
  }
  /** 
 * {@inheritDoc}The default implementation returns
 * <ul>
 * <li>{@link #getSupportLowerBound()} for {@code p = 0},</li>
 * <li>{@link #getSupportUpperBound()} for {@code p = 1}.</li>
 * </ul>
 */
  public double inverseCumulativeProbability(  final double p) throws OutOfRangeException {
    if (p < 0.0 || p > 1.0) {
      throw new OutOfRangeException(p,0,1);
    }
    double lowerBound=getSupportLowerBound();
    if (p == 0.0) {
      return lowerBound;
    }
    double upperBound=getSupportUpperBound();
    if (p == 1.0) {
      return upperBound;
    }
    final double mu=getNumericalMean();
    final double sig=FastMath.sqrt(getNumericalVariance());
    final boolean chebyshevApplies;
    chebyshevApplies=!(Double.isInfinite(mu) || Double.isNaN(mu) || Double.isInfinite(sig)|| Double.isNaN(sig));
    if (lowerBound == Double.NEGATIVE_INFINITY) {
      if (chebyshevApplies) {
        lowerBound=mu - sig * FastMath.sqrt((1. - p) / p);
      }
 else {
        lowerBound=-1.0;
        while (cumulativeProbability(lowerBound) >= p) {
          lowerBound*=2.0;
        }
      }
    }
    if (upperBound == Double.POSITIVE_INFINITY) {
      if (chebyshevApplies) {
        upperBound=mu + sig * FastMath.sqrt(p / (1. - p));
      }
 else {
        upperBound=1.0;
        while (cumulativeProbability(upperBound) < p) {
          upperBound*=2.0;
        }
      }
    }
    final UnivariateFunction toSolve=new UnivariateFunction(){
      public double value(      final double x){
        return cumulativeProbability(x) - p;
      }
    }
;
    double x=UnivariateSolverUtils.solve(toSolve,lowerBound,upperBound,getSolverAbsoluteAccuracy());
    if (!isSupportConnected()) {
      final double dx=getSolverAbsoluteAccuracy();
      if (x - dx >= getSupportLowerBound()) {
        double px=cumulativeProbability(x);
        if (cumulativeProbability(x - dx) == px) {
          upperBound=x;
          while (upperBound - lowerBound > dx) {
            final double midPoint=0.5 * (lowerBound + upperBound);
            if (cumulativeProbability(midPoint) < px) {
              lowerBound=midPoint;
            }
 else {
              upperBound=midPoint;
            }
          }
          return upperBound;
        }
      }
    }
    return x;
  }
  /** 
 * Returns the solver absolute accuracy for inverse cumulative computation.
 * You can override this method in order to use a Brent solver with an
 * absolute accuracy different from the default.
 * @return the maximum absolute error in inverse cumulative probability estimates
 */
  protected double getSolverAbsoluteAccuracy(){
    return solverAbsoluteAccuracy;
  }
  /** 
 * {@inheritDoc} 
 */
  public void reseedRandomGenerator(  long seed){
    random.setSeed(seed);
    randomData.reSeed(seed);
  }
  /** 
 * {@inheritDoc}The default implementation uses the
 * <a href="http://en.wikipedia.org/wiki/Inverse_transform_sampling">
 * inversion method.
 * </a>
 */
  public double sample(){
    return inverseCumulativeProbability(random.nextDouble());
  }
  /** 
 * {@inheritDoc}The default implementation generates the sample by calling{@link #sample()} in a loop.
 */
  public double[] sample(  int sampleSize){
    if (sampleSize <= 0) {
      throw new NotStrictlyPositiveException(LocalizedFormats.NUMBER_OF_SAMPLES,sampleSize);
    }
    double[] out=new double[sampleSize];
    for (int i=0; i < sampleSize; i++) {
      out[i]=sample();
    }
    return out;
  }
  /** 
 * {@inheritDoc}
 * @return zero.
 * @since 3.1
 */
  public double probability(  double x){
    return 0d;
  }
}
