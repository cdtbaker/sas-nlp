package org.apache.commons.math3.analysis.function;
import java.util.Arrays;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NonMonotonicSequenceException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.util.MathArrays;
/** 
 * <a href="http://en.wikipedia.org/wiki/Step_function">
 * Step function</a>.
 * @since 3.0
 * @version $Id: StepFunction.java 1455194 2013-03-11 15:45:54Z luc $
 */
public class StepFunction implements UnivariateFunction {
  /** 
 * Abscissae. 
 */
  private final double[] abscissa;
  /** 
 * Ordinates. 
 */
  private final double[] ordinate;
  /** 
 * Builds a step function from a list of arguments and the corresponding
 * values. Specifically, returns the function h(x) defined by <pre><code>
 * h(x) = y[0] for all x < x[1]
 * y[1] for x[1] <= x < x[2]
 * ...
 * y[y.length - 1] for x >= x[x.length - 1]
 * </code></pre>
 * The value of {@code x[0]} is ignored, but it must be strictly less than{@code x[1]}.
 * @param x Domain values where the function changes value.
 * @param y Values of the function.
 * @throws NonMonotonicSequenceExceptionif the {@code x} array is not sorted in strictly increasing order.
 * @throws NullArgumentException if {@code x} or {@code y} are {@code null}.
 * @throws NoDataException if {@code x} or {@code y} are zero-length.
 * @throws DimensionMismatchException if {@code x} and {@code y} do not
 * have the same length.
 */
  public StepFunction(  double[] x,  double[] y) throws NullArgumentException, NoDataException, DimensionMismatchException, NonMonotonicSequenceException {
    if (x == null || y == null) {
      throw new NullArgumentException();
    }
    if (x.length == 0 || y.length == 0) {
      throw new NoDataException();
    }
    if (y.length != x.length) {
      throw new DimensionMismatchException(y.length,x.length);
    }
    MathArrays.checkOrder(x);
    abscissa=MathArrays.copyOf(x);
    ordinate=MathArrays.copyOf(y);
  }
  /** 
 * {@inheritDoc} 
 */
  public double value(  double x){
    int index=Arrays.binarySearch(abscissa,x);
    double fx=0;
    if (index < -1) {
      fx=ordinate[-index - 2];
    }
 else     if (index >= 0) {
      fx=ordinate[index];
    }
 else {
      fx=ordinate[0];
    }
    return fx;
  }
}
