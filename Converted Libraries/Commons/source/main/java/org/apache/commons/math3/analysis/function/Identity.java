package org.apache.commons.math3.analysis.function;
import org.apache.commons.math3.analysis.DifferentiableUnivariateFunction;
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction;
/** 
 * Identity function.
 * @since 3.0
 * @version $Id: Identity.java 1424087 2012-12-19 20:32:50Z luc $
 */
public class Identity implements UnivariateDifferentiableFunction, DifferentiableUnivariateFunction {
  /** 
 * {@inheritDoc} 
 */
  public double value(  double x){
    return x;
  }
  /** 
 * {@inheritDoc}
 * @deprecated as of 3.1, replaced by {@link #value(DerivativeStructure)}
 */
  @Deprecated public DifferentiableUnivariateFunction derivative(){
    return new Constant(1);
  }
  /** 
 * {@inheritDoc}
 * @since 3.1
 */
  public DerivativeStructure value(  final DerivativeStructure t){
    return t;
  }
}
