package org.apache.commons.math3.analysis.function;
import org.apache.commons.math3.analysis.DifferentiableUnivariateFunction;
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction;
/** 
 * Constant function.
 * @since 3.0
 * @version $Id: Constant.java 1424087 2012-12-19 20:32:50Z luc $
 */
public class Constant implements UnivariateDifferentiableFunction, DifferentiableUnivariateFunction {
  /** 
 * Constant. 
 */
  private final double c;
  /** 
 * @param c Constant.
 */
  public Constant(  double c){
    this.c=c;
  }
  /** 
 * {@inheritDoc} 
 */
  public double value(  double x){
    return c;
  }
  /** 
 * {@inheritDoc}
 * @deprecated as of 3.1, replaced by {@link #value(DerivativeStructure)}
 */
  @Deprecated public DifferentiableUnivariateFunction derivative(){
    return new Constant(0);
  }
  /** 
 * {@inheritDoc}
 * @since 3.1
 */
  public DerivativeStructure value(  final DerivativeStructure t){
    return new DerivativeStructure(t.getFreeParameters(),t.getOrder(),c);
  }
}
