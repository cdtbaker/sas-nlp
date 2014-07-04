package org.apache.commons.math3.analysis;
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction;
/** 
 * Auxiliary class for testing solvers.
 * @version $Id: QuinticFunction.java 1383441 2012-09-11 14:56:39Z luc $
 */
public class QuinticFunction implements UnivariateDifferentiableFunction {
  public double value(  double x){
    return (x - 1) * (x - 0.5) * x* (x + 0.5)* (x + 1);
  }
  public DerivativeStructure value(  DerivativeStructure t){
    return t.subtract(1).multiply(t.subtract(0.5)).multiply(t).multiply(t.add(0.5)).multiply(t.add(1));
  }
}
