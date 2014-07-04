package org.apache.commons.math3.analysis.differentiation;
import org.apache.commons.math3.analysis.MultivariateMatrixFunction;
/** 
 * Class representing the Jacobian of a multivariate vector function.
 * <p>
 * The rows iterate on the model functions while the columns iterate on the parameters; thus,
 * the numbers of rows is equal to the dimension of the underlying function vector
 * value and the number of columns is equal to the number of free parameters of
 * the underlying function.
 * </p>
 * @version $Id: JacobianFunction.java 1455194 2013-03-11 15:45:54Z luc $
 * @since 3.1
 */
public class JacobianFunction implements MultivariateMatrixFunction {
  /** 
 * Underlying vector-valued function. 
 */
  private final MultivariateDifferentiableVectorFunction f;
  /** 
 * Simple constructor.
 * @param f underlying vector-valued function
 */
  public JacobianFunction(  final MultivariateDifferentiableVectorFunction f){
    this.f=f;
  }
  /** 
 * {@inheritDoc} 
 */
  public double[][] value(  double[] point){
    final DerivativeStructure[] dsX=new DerivativeStructure[point.length];
    for (int i=0; i < point.length; ++i) {
      dsX[i]=new DerivativeStructure(point.length,1,i,point[i]);
    }
    final DerivativeStructure[] dsY=f.value(dsX);
    final double[][] y=new double[dsY.length][point.length];
    final int[] orders=new int[point.length];
    for (int i=0; i < dsY.length; ++i) {
      for (int j=0; j < point.length; ++j) {
        orders[j]=1;
        y[i][j]=dsY[i].getPartialDerivative(orders);
        orders[j]=0;
      }
    }
    return y;
  }
}
