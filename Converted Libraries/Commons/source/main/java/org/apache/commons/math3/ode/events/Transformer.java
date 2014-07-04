package org.apache.commons.math3.ode.events;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.Precision;
/** 
 * Transformer for {@link EventHandler#g(double,double[]) g functions}.
 * @see EventFilter
 * @see FilterType
 * @version $Id: Transformer.java 1458298 2013-03-19 14:09:58Z luc $
 * @since 3.2
 */
enum Transformer {/** 
 * Transformer computing transformed = 0.
 * <p>
 * This transformer is used when we initialize the filter, until we get at
 * least one non-zero value to select the proper transformer.
 * </p>
 */
UNINITIALIZED{
  /** 
 * {@inheritDoc} 
 */
  protected double transformed(  final double g){
    return 0;
  }
}
, /** 
 * Transformer computing transformed = g.
 * <p>
 * When this transformer is applied, the roots of the original function
 * are preserved, with the same {@code increasing/decreasing} status.
 * </p>
 */
PLUS{
  /** 
 * {@inheritDoc} 
 */
  protected double transformed(  final double g){
    return g;
  }
}
, /** 
 * Transformer computing transformed = -g.
 * <p>
 * When this transformer is applied, the roots of the original function
 * are preserved, with reversed {@code increasing/decreasing} status.
 * </p>
 */
MINUS{
  /** 
 * {@inheritDoc} 
 */
  protected double transformed(  final double g){
    return -g;
  }
}
, /** 
 * Transformer computing transformed = min(-{@link Precision#SAFE_MIN}, -g, +g).
 * <p>
 * When this transformer is applied, the transformed function is
 * guaranteed to be always strictly negative (i.e. there are no roots).
 * </p>
 */
MIN{
  /** 
 * {@inheritDoc} 
 */
  protected double transformed(  final double g){
    return FastMath.min(-Precision.SAFE_MIN,FastMath.min(-g,+g));
  }
}
, /** 
 * Transformer computing transformed = max(+{@link Precision#SAFE_MIN}, -g, +g).
 * <p>
 * When this transformer is applied, the transformed function is
 * guaranteed to be always strictly positive (i.e. there are no roots).
 * </p>
 */
MAX{
  /** 
 * {@inheritDoc} 
 */
  protected double transformed(  final double g){
    return FastMath.max(+Precision.SAFE_MIN,FastMath.max(-g,+g));
  }
}
; /** 
 * Transform value of function g.
 * @param g raw value of function g
 * @return transformed value of function g
 */
protected abstract double transformed(double g);
}
