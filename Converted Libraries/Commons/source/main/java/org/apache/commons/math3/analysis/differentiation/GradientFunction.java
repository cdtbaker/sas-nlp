package org.apache.commons.math3.analysis.differentiation;
import org.apache.commons.math3.analysis.MultivariateVectorFunction;
/** 
 * Class representing the gradient of a multivariate function.
 * <p>
 * The vectorial components of the function represent the derivatives
 * with respect to each function parameters.
 * </p>
 * @version $Id: GradientFunction.java 1455194 2013-03-11 15:45:54Z luc $
 * @since 3.1
 */
public class GradientFunction implements MultivariateVectorFunction {
  /** 
 * Underlying real-valued function. 
 */
  private final MultivariateDifferentiableFunction f;
  /** 
 * Simple constructor.
 * @param f underlying real-valued function
 */
  public GradientFunction(  final MultivariateDifferentiableFunction f){
    this.f=f;
  }
  /** 
 * {@inheritDoc} 
 */
  public double[] value(  double[] point){
    final DerivativeStructure[] dsX=new DerivativeStructure[point.length];
    for (int i=0; i < point.length; ++i) {
      dsX[i]=new DerivativeStructure(point.length,1,i,point[i]);
    }
    final DerivativeStructure dsY=f.value(dsX);
    final double[] y=new double[point.length];
    final int[] orders=new int[point.length];
    for (int i=0; i < point.length; ++i) {
      orders[i]=1;
      y[i]=dsY.getPartialDerivative(orders);
      orders[i]=0;
    }
    return y;
  }
}
