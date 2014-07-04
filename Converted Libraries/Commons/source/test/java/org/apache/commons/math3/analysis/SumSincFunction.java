package org.apache.commons.math3.analysis;
import org.apache.commons.math3.analysis.function.Sinc;
/** 
 * Auxiliary class for testing optimizers.
 * @version $Id$
 */
public class SumSincFunction implements MultivariateFunction {
  private static final UnivariateFunction sinc=new Sinc();
  /** 
 * Factor that will multiply each term of the sum.
 */
  private final double factor;
  /** 
 * @param factor Factor that will multiply each term of the sum.
 */
  public SumSincFunction(  double factor){
    this.factor=factor;
  }
  /** 
 * @param point Argument.
 * @return the value of this function at point {@code x}.
 */
  public double value(  double[] point){
    double sum=0;
    for (int i=0, max=point.length; i < max; i++) {
      final double x=point[i];
      final double v=sinc.value(x);
      sum+=v;
    }
    return factor * sum;
  }
}
