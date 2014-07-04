package org.ojalgo.random;
public interface DiscreteDistribution extends Distribution {
  /** 
 * Probability density function
 */
  double getProbability(  int aVal);
}
