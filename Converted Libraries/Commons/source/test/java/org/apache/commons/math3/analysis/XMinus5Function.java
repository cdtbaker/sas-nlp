package org.apache.commons.math3.analysis;
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction;
/** 
 * Auxiliary class for testing solvers.
 * @version $Id$
 */
public class XMinus5Function implements UnivariateDifferentiableFunction {
  public double value(  double x){
    return x - 5;
  }
  public DerivativeStructure value(  DerivativeStructure t){
    return t.subtract(5);
  }
}
