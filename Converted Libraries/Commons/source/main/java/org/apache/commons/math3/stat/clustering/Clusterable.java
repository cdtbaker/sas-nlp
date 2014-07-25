package org.apache.commons.math3.stat.clustering;
import java.util.Collection;
/** 
 * Interface for points that can be clustered together.
 * @param<T>
 *  the type of point that can be clustered
 * @version $Id: Clusterable.java 1461871 2013-03-27 22:01:25Z tn $
 * @since 2.0
 * @deprecated As of 3.2 (to be removed in 4.0),
 * use {@link org.apache.commons.math3.ml.clustering.Clusterable} instead
 */
@Deprecated public interface Clusterable<T> {
  /** 
 * Returns the distance from the given point.
 * @param p the point to compute the distance from
 * @return the distance from the given point
 */
  double distanceFrom(  T p);
  /** 
 * Returns the centroid of the given Collection of points.
 * @param p the Collection of points to compute the centroid of
 * @return the centroid of the given Collection of Points
 */
  T centroidOf(  Collection<T> p);
}
