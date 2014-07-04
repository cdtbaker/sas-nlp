package org.apache.commons.math3.optim.linear;
import java.util.Set;
import java.util.HashSet;
import java.util.Collection;
import java.util.Collections;
import org.apache.commons.math3.optim.OptimizationData;
/** 
 * Class that represents a set of {@link LinearConstraint linear constraints}.
 * @version $Id: LinearConstraintSet.java 1435539 2013-01-19 13:27:24Z tn $
 * @since 3.1
 */
public class LinearConstraintSet implements OptimizationData {
  /** 
 * Set of constraints. 
 */
  private final Set<LinearConstraint> linearConstraints=new HashSet<LinearConstraint>();
  /** 
 * Creates a set containing the given constraints.
 * @param constraints Constraints.
 */
  public LinearConstraintSet(  LinearConstraint... constraints){
    for (    LinearConstraint c : constraints) {
      linearConstraints.add(c);
    }
  }
  /** 
 * Creates a set containing the given constraints.
 * @param constraints Constraints.
 */
  public LinearConstraintSet(  Collection<LinearConstraint> constraints){
    linearConstraints.addAll(constraints);
  }
  /** 
 * Gets the set of linear constraints.
 * @return the constraints.
 */
  public Collection<LinearConstraint> getConstraints(){
    return Collections.unmodifiableSet(linearConstraints);
  }
}
