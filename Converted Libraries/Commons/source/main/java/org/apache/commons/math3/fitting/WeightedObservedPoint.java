package org.apache.commons.math3.fitting;
import java.io.Serializable;
/** 
 * This class is a simple container for weighted observed point in{@link CurveFitter curve fitting}.
 * <p>Instances of this class are guaranteed to be immutable.</p>
 * @version $Id: WeightedObservedPoint.java 1416643 2012-12-03 19:37:14Z tn $
 * @since 2.0
 */
public class WeightedObservedPoint implements Serializable {
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
 * @param weight Weight of the measurement in the fitting process.
 * @param x Abscissa of the measurement.
 * @param y Ordinate of the measurement.
 */
  public WeightedObservedPoint(  final double weight,  final double x,  final double y){
    this.weight=weight;
    this.x=x;
    this.y=y;
  }
  /** 
 * Gets the weight of the measurement in the fitting process.
 * @return the weight of the measurement in the fitting process.
 */
  public double getWeight(){
    return weight;
  }
  /** 
 * Gets the abscissa of the point.
 * @return the abscissa of the point.
 */
  public double getX(){
    return x;
  }
  /** 
 * Gets the observed value of the function at x.
 * @return the observed value of the function at x.
 */
  public double getY(){
    return y;
  }
}
