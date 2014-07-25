package org.apache.commons.math3.optimization;
import java.io.Serializable;
/** 
 * Goal type for an optimization problem.
 * @version $Id: GoalType.java 1422230 2012-12-15 12:11:13Z erans $
 * @deprecated As of 3.1 (to be removed in 4.0).
 * @since 2.0
 */
@Deprecated public enum GoalType implements Serializable {/** 
 * Maximization goal. 
 */
MAXIMIZE, /** 
 * Minimization goal. 
 */
MINIMIZE}
