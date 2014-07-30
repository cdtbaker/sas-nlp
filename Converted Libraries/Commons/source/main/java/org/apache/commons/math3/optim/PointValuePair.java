package org.apache.commons.math3.optim;
import java.io.Serializable;
import org.apache.commons.math3.util.Pair;
/** 
 * This class holds a point and the value of an objective function at
 * that point.
 * @see PointVectorValuePair
 * @see org.apache.commons.math3.analysis.MultivariateFunction
 * @version $Id: PointValuePair.java 1435539 2013-01-19 13:27:24Z tn $
 * @since 3.0
 */
public class PointValuePair extends Pair<double[],Double> implements Serializable {
  /** 
 * Serializable UID. 
 */
  private static final long serialVersionUID=20120513L;
  /** 
 * Builds a point/objective function value pair.
 * @param point Point coordinates. This instance will store
 * a copy of the array, not the array passed as argument.
 * @param value Value of the objective function at the point.
 */
  public PointValuePair(  final double[] point,  final double value){
    this(point,value,true);
  }
  /** 
 * Builds a point/objective function value pair.
 * @param point Point coordinates.
 * @param value Value of the objective function at the point.
 * @param copyArray if {@code true}, the input array will be copied,
 * otherwise it will be referenced.
 */
  public PointValuePair(  final double[] point,  final double value,  final boolean copyArray){
    super(copyArray ? ((point == null) ? null : point.clone()) : point,value);
  }
  /** 
 * Gets the point.
 * @return a copy of the stored point.
 */
  public double[] getPoint(){
    final double[] p=getKey();
    return p == null ? null : p.clone();
  }
  /** 
 * Gets a reference to the point.
 * @return a reference to the internal array storing the point.
 */
  public double[] getPointRef(){
    return getKey();
  }
  /** 
 * Replace the instance with a data transfer object for serialization.
 * @return data transfer object that will be serialized
 */
  private Object writeReplace(){
    return new DataTransferObject(getKey(),getValue());
  }
  /** 
 * Internal class used only for serialization. 
 */
private static class DataTransferObject implements Serializable {
    /** 
 * Serializable UID. 
 */
    private static final long serialVersionUID=20120513L;
    /** 
 * Point coordinates.
 * @Serial
 */
    private final double[] point;
    /** 
 * Value of the objective function at the point.
 * @Serial
 */
    private final double value;
    /** 
 * Simple constructor.
 * @param point Point coordinates.
 * @param value Value of the objective function at the point.
 */
    public DataTransferObject(    final double[] point,    final double value){
      this.point=point.clone();
      this.value=value;
    }
    /** 
 * Replace the deserialized data transfer object with a {@link PointValuePair}.
 * @return replacement {@link PointValuePair}
 */
    private Object readResolve(){
      return new PointValuePair(point,value,false);
    }
  }
}