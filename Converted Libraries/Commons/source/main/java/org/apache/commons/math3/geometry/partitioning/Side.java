package org.apache.commons.math3.geometry.partitioning;
/** 
 * Enumerate representing the location of an element with respect to an{@link Hyperplane hyperplane} of a space.
 * @version $Id: Side.java 1416643 2012-12-03 19:37:14Z tn $
 * @since 3.0
 */
public enum Side {/** 
 * Code for the plus side of the hyperplane. 
 */
PLUS, /** 
 * Code for the minus side of the hyperplane. 
 */
MINUS, /** 
 * Code for elements crossing the hyperplane from plus to minus side. 
 */
BOTH, /** 
 * Code for the hyperplane itself. 
 */
HYPER}
