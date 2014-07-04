package org.apache.commons.math3.transform;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.NonMonotonicSequenceException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
/** 
 * Interface for one-dimensional data sets transformations producing real results.
 * <p>
 * Such transforms include {@link FastSineTransformer sine transform},{@link FastCosineTransformer cosine transform} or {@link FastHadamardTransformer Hadamard transform}. {@link FastFourierTransformerFourier transform} is of a different kind and does not implement this
 * interface since it produces {@link org.apache.commons.math3.complex.Complex}results instead of real ones.
 * @version $Id: RealTransformer.java 1385310 2012-09-16 16:32:10Z tn $
 * @since 2.0
 */
public interface RealTransformer {
  /** 
 * Returns the (forward, inverse) transform of the specified real data set.
 * @param f the real data array to be transformed (signal)
 * @param type the type of transform (forward, inverse) to be performed
 * @return the real transformed array (spectrum)
 * @throws MathIllegalArgumentException if the array cannot be transformed
 * with the given type (this may be for example due to array size, which is
 * constrained in some transforms)
 */
  double[] transform(  double[] f,  TransformType type) throws MathIllegalArgumentException ;
  /** 
 * Returns the (forward, inverse) transform of the specified real function,
 * sampled on the specified interval.
 * @param f the function to be sampled and transformed
 * @param min the (inclusive) lower bound for the interval
 * @param max the (exclusive) upper bound for the interval
 * @param n the number of sample points
 * @param type the type of transform (forward, inverse) to be performed
 * @return the real transformed array
 * @throws NonMonotonicSequenceException if the lower bound is greater than, or equal to the upper bound
 * @throws NotStrictlyPositiveException if the number of sample points is negative
 * @throws MathIllegalArgumentException if the sample cannot be transformed
 * with the given type (this may be for example due to sample size, which is
 * constrained in some transforms)
 */
  double[] transform(  UnivariateFunction f,  double min,  double max,  int n,  TransformType type) throws NonMonotonicSequenceException, NotStrictlyPositiveException, MathIllegalArgumentException ;
}
