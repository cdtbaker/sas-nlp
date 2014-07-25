package org.apache.commons.math3.optim.linear;
import org.apache.commons.math3.optim.OptimizationData;
/** 
 * A constraint for a linear optimization problem indicating whether all
 * variables must be restricted to non-negative values.
 * @version $Id: NonNegativeConstraint.java 1435539 2013-01-19 13:27:24Z tn $
 * @since 3.1
 */
public class NonNegativeConstraint implements OptimizationData {
  /** 
 * Whether the variables are all positive. 
 */
  private final boolean isRestricted;
  /** 
 * @param restricted If {@code true}, all the variables must be positive.
 */
  public NonNegativeConstraint(  boolean restricted){
    isRestricted=restricted;
  }
  /** 
 * Indicates whether all the variables must be restricted to non-negative
 * values.
 * @return {@code true} if all the variables must be positive.
 */
  public boolean isRestrictedToNonNegative(){
    return isRestricted;
  }
}
