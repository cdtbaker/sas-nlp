package org.apache.commons.math3.analysis.function;
import org.apache.commons.math3.analysis.FunctionUtils;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.DifferentiableUnivariateFunction;
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction;
import org.apache.commons.math3.util.FastMath;
/** 
 * Power function.
 * @since 3.0
 * @version $Id: Power.java 1383441 2012-09-11 14:56:39Z luc $
 */
public class Power implements UnivariateDifferentiableFunction, DifferentiableUnivariateFunction {
  /** 
 * Power. 
 */
  private final double p;
  /** 
 * @param p Power.
 */
  public Power(  double p){
    this.p=p;
  }
  /** 
 * {@inheritDoc} 
 */
  public double value(  double x){
    return FastMath.pow(x,p);
  }
  /** 
 * {@inheritDoc}
 * @deprecated as of 3.1, replaced by {@link #value(DerivativeStructure)}
 */
  @Deprecated public UnivariateFunction derivative(){
    return FunctionUtils.toDifferentiableUnivariateFunction(this).derivative();
  }
  /** 
 * {@inheritDoc}
 * @since 3.1
 */
  public DerivativeStructure value(  final DerivativeStructure t){
    return t.pow(p);
  }
}
