package org.apache.commons.math3.optim.nonlinear.scalar;
import org.apache.commons.math3.optim.OptimizationData;
/** 
 * Goal type for an optimization problem (minimization or maximization of
 * a scalar function.
 * @version $Id: GoalType.java 1435539 2013-01-19 13:27:24Z tn $
 * @since 2.0
 */
public enum GoalType implements OptimizationData {/** 
 * Maximization. 
 */
MAXIMIZE, /** 
 * Minimization. 
 */
MINIMIZE}
