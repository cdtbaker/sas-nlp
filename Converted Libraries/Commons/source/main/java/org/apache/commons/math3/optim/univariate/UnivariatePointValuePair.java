package org.apache.commons.math3.optim.univariate;
import java.io.Serializable;
/** 
 * This class holds a point and the value of an objective function at this
 * point.
 * This is a simple immutable container.
 * @version $Id: UnivariatePointValuePair.java 1435539 2013-01-19 13:27:24Z tn $
 * @since 3.0
 */
public class UnivariatePointValuePair implements Serializable {
  /** 
 * Serializable version identifier. 
 */
  private static final long serialVersionUID=1003888396256744753L;
  /** 
 * Point. 
 */
  private final double point;
  /** 
 * Value of the objective function at the point. 
 */
  private final double value;
  /** 
 * Build a point/objective function value pair.
 * @param point Point.
 * @param value Value of an objective function at the point
 */
  public UnivariatePointValuePair(  final double point,  final double value){
    this.point=point;
    this.value=value;
  }
  /** 
 * Get the point.
 * @return the point.
 */
  public double getPoint(){
    return point;
  }
  /** 
 * Get the value of the objective function.
 * @return the stored value of the objective function.
 */
  public double getValue(){
    return value;
  }
}
