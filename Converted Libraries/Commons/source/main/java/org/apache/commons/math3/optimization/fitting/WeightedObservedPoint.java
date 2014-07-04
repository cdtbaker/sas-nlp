package org.apache.commons.math3.optimization.fitting;
import java.io.Serializable;
/** 
 * This class is a simple container for weighted observed point in{@link CurveFitter curve fitting}.
 * <p>Instances of this class are guaranteed to be immutable.</p>
 * @version $Id: WeightedObservedPoint.java 1422230 2012-12-15 12:11:13Z erans $
 * @deprecated As of 3.1 (to be removed in 4.0).
 * @since 2.0
 */
@Deprecated public class WeightedObservedPoint implements Serializable {
  /** 
 * Serializable version id. 
 */
  private static final long serialVersionUID=5306874947404636157L;
  /** 
 * Weight of the measurement in the fitting process. 
 */
  private final double weight;
  /** 
 * Abscissa of the point. 
 */
  private final double x;
  /** 
 * Observed value of the function at x. 
 */
  private final double y;
  /** 
 * Simple constructor.
 * @param weight weight of the measurement in the fitting process
 * @param x abscissa of the measurement
 * @param y ordinate of the measurement
 */
  public WeightedObservedPoint(  final double weight,  final double x,  final double y){
    this.weight=weight;
    this.x=x;
    this.y=y;
  }
  /** 
 * Get the weight of the measurement in the fitting process.
 * @return weight of the measurement in the fitting process
 */
  public double getWeight(){
    return weight;
  }
  /** 
 * Get the abscissa of the point.
 * @return abscissa of the point
 */
  public double getX(){
    return x;
  }
  /** 
 * Get the observed value of the function at x.
 * @return observed value of the function at x
 */
  public double getY(){
    return y;
  }
}
