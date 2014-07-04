package org.apache.commons.math3.ode.nonstiff;
import org.apache.commons.math3.ode.sampling.StepInterpolator;
/** 
 * This class implements a linear interpolator for step.
 * <p>This interpolator computes dense output inside the last
 * step computed. The interpolation equation is consistent with the
 * integration scheme :
 * <ul>
 * <li>Using reference point at step start:<br>
 * y(t<sub>n</sub> + &theta; h) = y (t<sub>n</sub>) + &theta; h y'
 * </li>
 * <li>Using reference point at step end:<br>
 * y(t<sub>n</sub> + &theta; h) = y (t<sub>n</sub> + h) - (1-&theta;) h y'
 * </li>
 * </ul>
 * </p>
 * where &theta; belongs to [0 ; 1] and where y' is the evaluation of
 * the derivatives already computed during the step.</p>
 * @see EulerIntegrator
 * @version $Id: EulerStepInterpolator.java 1416643 2012-12-03 19:37:14Z tn $
 * @since 1.2
 */
class EulerStepInterpolator extends RungeKuttaStepInterpolator {
  /** 
 * Serializable version identifier. 
 */
  private static final long serialVersionUID=20111120L;
  /** 
 * Simple constructor.
 * This constructor builds an instance that is not usable yet, the{@link org.apache.commons.math3.ode.sampling.AbstractStepInterpolator#reinitialize}method should be called before using the instance in order to
 * initialize the internal arrays. This constructor is used only
 * in order to delay the initialization in some cases. The {@link RungeKuttaIntegrator} class uses the prototyping design pattern
 * to create the step interpolators by cloning an uninitialized model
 * and later initializing the copy.
 */
  public EulerStepInterpolator(){
  }
  /** 
 * Copy constructor.
 * @param interpolator interpolator to copy from. The copy is a deep
 * copy: its arrays are separated from the original arrays of the
 * instance
 */
  public EulerStepInterpolator(  final EulerStepInterpolator interpolator){
    super(interpolator);
  }
  /** 
 * {@inheritDoc} 
 */
  @Override protected StepInterpolator doCopy(){
    return new EulerStepInterpolator(this);
  }
  /** 
 * {@inheritDoc} 
 */
  @Override protected void computeInterpolatedStateAndDerivatives(  final double theta,  final double oneMinusThetaH){
    if ((previousState != null) && (theta <= 0.5)) {
      for (int i=0; i < interpolatedState.length; ++i) {
        interpolatedState[i]=previousState[i] + theta * h * yDotK[0][i];
      }
      System.arraycopy(yDotK[0],0,interpolatedDerivatives,0,interpolatedDerivatives.length);
    }
 else {
      for (int i=0; i < interpolatedState.length; ++i) {
        interpolatedState[i]=currentState[i] - oneMinusThetaH * yDotK[0][i];
      }
      System.arraycopy(yDotK[0],0,interpolatedDerivatives,0,interpolatedDerivatives.length);
    }
  }
}
