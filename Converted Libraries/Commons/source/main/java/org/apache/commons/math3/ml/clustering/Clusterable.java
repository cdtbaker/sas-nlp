package org.apache.commons.math3.ml.clustering;
/** 
 * Interface for n-dimensional points that can be clustered together.
 * @version $Id: Clusterable.java 1461862 2013-03-27 21:48:10Z tn $
 * @since 3.2
 */
public interface Clusterable {
  /** 
 * Gets the n-dimensional point.
 * @return the point array
 */
  double[] getPoint();
}
