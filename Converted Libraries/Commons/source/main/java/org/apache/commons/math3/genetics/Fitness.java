package org.apache.commons.math3.genetics;
/** 
 * Fitness of a chromosome.
 * @version $Id: Fitness.java 1416643 2012-12-03 19:37:14Z tn $
 * @since 2.0
 */
public interface Fitness {
  /** 
 * Compute the fitness. This is usually very time-consuming, so the value should be cached.
 * @return fitness
 */
  double fitness();
}
