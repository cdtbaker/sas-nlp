package org.apache.commons.math3.optim.nonlinear.vector.jacobian;
import java.util.ArrayList;
import org.apache.commons.math3.analysis.MultivariateVectorFunction;
import org.apache.commons.math3.analysis.MultivariateMatrixFunction;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.apache.commons.math3.optim.nonlinear.vector.ModelFunction;
import org.apache.commons.math3.optim.nonlinear.vector.ModelFunctionJacobian;
/** 
 * Class that models a straight line defined as {@code y = a x + b}.
 * The parameters of problem are:
 * <ul>
 * <li>{@code a}</li>
 * <li>{@code b}</li>
 * </ul>
 * The model functions are:
 * <ul>
 * <li>for each pair (a, b), the y-coordinate of the line.</li>
 * </ul>
 */
class StraightLineProblem {
  /** 
 * Cloud of points assumed to be fitted by a straight line. 
 */
  private final ArrayList<double[]> points;
  /** 
 * Error (on the y-coordinate of the points). 
 */
  private final double sigma;
  /** 
 * @param error Assumed error for the y-coordinate.
 */
  public StraightLineProblem(  double error){
    points=new ArrayList<double[]>();
    sigma=error;
  }
  public void addPoint(  double px,  double py){
    points.add(new double[]{px,py});
  }
  /** 
 * @return the list of x-coordinates.
 */
  public double[] x(){
    final double[] v=new double[points.size()];
    for (int i=0; i < points.size(); i++) {
      final double[] p=points.get(i);
      v[i]=p[0];
    }
    return v;
  }
  /** 
 * @return the list of y-coordinates.
 */
  public double[] y(){
    final double[] v=new double[points.size()];
    for (int i=0; i < points.size(); i++) {
      final double[] p=points.get(i);
      v[i]=p[1];
    }
    return v;
  }
  public double[] target(){
    return y();
  }
  public double[] weight(){
    final double weight=1 / (sigma * sigma);
    final double[] w=new double[points.size()];
    for (int i=0; i < points.size(); i++) {
      w[i]=weight;
    }
    return w;
  }
  public ModelFunction getModelFunction(){
    return new ModelFunction(new MultivariateVectorFunction(){
      public double[] value(      double[] params){
        final Model line=new Model(params[0],params[1]);
        final double[] model=new double[points.size()];
        for (int i=0; i < points.size(); i++) {
          final double[] p=points.get(i);
          model[i]=line.value(p[0]);
        }
        return model;
      }
    }
);
  }
  public ModelFunctionJacobian getModelFunctionJacobian(){
    return new ModelFunctionJacobian(new MultivariateMatrixFunction(){
      public double[][] value(      double[] point){
        return jacobian(point);
      }
    }
);
  }
  /** 
 * Directly solve the linear problem, using the {@link SimpleRegression}class.
 */
  public double[] solve(){
    final SimpleRegression regress=new SimpleRegression(true);
    for (    double[] d : points) {
      regress.addData(d[0],d[1]);
    }
    final double[] result={regress.getSlope(),regress.getIntercept()};
    return result;
  }
  private double[][] jacobian(  double[] params){
    final double[][] jacobian=new double[points.size()][2];
    for (int i=0; i < points.size(); i++) {
      final double[] p=points.get(i);
      jacobian[i][0]=p[0];
      jacobian[i][1]=1;
    }
    return jacobian;
  }
  /** 
 * Linear function.
 */
public static class Model implements UnivariateFunction {
    final double a;
    final double b;
    public Model(    double a,    double b){
      this.a=a;
      this.b=b;
    }
    public double value(    double x){
      return a * x + b;
    }
  }
}
