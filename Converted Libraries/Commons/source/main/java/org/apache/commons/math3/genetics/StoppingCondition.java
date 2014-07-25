package org.apache.commons.math3.genetics;
/** 
 * Algorithm used to determine when to stop evolution.
 * @since 2.0
 * @version $Id: StoppingCondition.java 1416643 2012-12-03 19:37:14Z tn $
 */
public interface StoppingCondition {
  /** 
 * Determine whether or not the given population satisfies the stopping condition.
 * @param population the population to test.
 * @return <code>true</code> if this stopping condition is met by the given population,
 * <code>false</code> otherwise.
 */
  boolean isSatisfied(  Population population);
}
