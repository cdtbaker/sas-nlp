package org.ojalgo.random;
/** 
 * Distribution
 * @author apete
 */
public interface Distribution {
  double getExpected();
  double getStandardDeviation();
  double getVariance();
}
