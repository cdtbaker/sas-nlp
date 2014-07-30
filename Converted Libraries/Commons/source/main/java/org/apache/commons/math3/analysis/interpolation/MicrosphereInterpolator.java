package org.apache.commons.math3.analysis.interpolation;
import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.random.UnitSphereRandomVectorGenerator;
/** 
 * Interpolator that implements the algorithm described in
 * <em>William Dudziak</em>'s
 * <a href="http://www.dudziak.com/microsphere.pdf">MS thesis</a>.
 * @since 2.1
 * @version $Id: MicrosphereInterpolator.java 1379904 2012-09-01 23:54:52Z erans $
 */
public class MicrosphereInterpolator implements MultivariateInterpolator {
  /** 
 * Default number of surface elements that composes the microsphere.
 */
  public static final int DEFAULT_MICROSPHERE_ELEMENTS=2000;
  /** 
 * Default exponent used the weights calculation.
 */
  public static final int DEFAULT_BRIGHTNESS_EXPONENT=2;
  /** 
 * Number of surface elements of the microsphere.
 */
  private final int microsphereElements;
  /** 
 * Exponent used in the power law that computes the weights of the
 * sample data.
 */
  private final int brightnessExponent;
  /** 
 * Create a microsphere interpolator with default settings.
 * Calling this constructor is equivalent to call {@link #MicrosphereInterpolator(int,int)MicrosphereInterpolator(MicrosphereInterpolator.DEFAULT_MICROSPHERE_ELEMENTS,
 * MicrosphereInterpolator.DEFAULT_BRIGHTNESS_EXPONENT)}.
 */
  public MicrosphereInterpolator(){
    this(DEFAULT_MICROSPHERE_ELEMENTS,DEFAULT_BRIGHTNESS_EXPONENT);
  }
  /** 
 * Create a microsphere interpolator.
 * @param elements Number of surface elements of the microsphere.
 * @param exponent Exponent used in the power law that computes the
 * weights (distance dimming factor) of the sample data.
 * @throws NotPositiveException if {@code exponent < 0}.
 * @throws NotStrictlyPositiveException if {@code elements <= 0}.
 */
  public MicrosphereInterpolator(  final int elements,  final int exponent) throws NotPositiveException, NotStrictlyPositiveException {
    if (exponent < 0) {
      throw new NotPositiveException(exponent);
    }
    if (elements <= 0) {
      throw new NotStrictlyPositiveException(elements);
    }
    microsphereElements=elements;
    brightnessExponent=exponent;
  }
  /** 
 * {@inheritDoc}
 */
  public MultivariateFunction interpolate(  final double[][] xval,  final double[] yval) throws DimensionMismatchException, NoDataException, NullArgumentException {
    final UnitSphereRandomVectorGenerator rand=new UnitSphereRandomVectorGenerator(xval[0].length);
    return new MicrosphereInterpolatingFunction(xval,yval,brightnessExponent,microsphereElements,rand);
  }
}