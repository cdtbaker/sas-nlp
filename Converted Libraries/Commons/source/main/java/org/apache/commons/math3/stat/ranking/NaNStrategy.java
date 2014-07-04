package org.apache.commons.math3.stat.ranking;
/** 
 * Strategies for handling NaN values in rank transformations.
 * <ul>
 * <li>MINIMAL - NaNs are treated as minimal in the ordering, equivalent to
 * (that is, tied with) <code>Double.NEGATIVE_INFINITY</code>.</li>
 * <li>MAXIMAL - NaNs are treated as maximal in the ordering, equivalent to
 * <code>Double.POSITIVE_INFINITY</code></li>
 * <li>REMOVED - NaNs are removed before the rank transform is applied</li>
 * <li>FIXED - NaNs are left "in place," that is the rank transformation is
 * applied to the other elements in the input array, but the NaN elements
 * are returned unchanged.</li>
 * <li>FAILED - If any NaN is encountered in the input array, an appropriate
 * exception is thrown</li>
 * </ul>
 * @since 2.0
 * @version $Id: NaNStrategy.java 1422313 2012-12-15 18:53:41Z psteitz $
 */
public enum NaNStrategy {/** 
 * NaNs are considered minimal in the ordering 
 */
MINIMAL, /** 
 * NaNs are considered maximal in the ordering 
 */
MAXIMAL, /** 
 * NaNs are removed before computing ranks 
 */
REMOVED, /** 
 * NaNs are left in place 
 */
FIXED, /** 
 * NaNs result in an exception
 * @since 3.1
 */
FAILED}
