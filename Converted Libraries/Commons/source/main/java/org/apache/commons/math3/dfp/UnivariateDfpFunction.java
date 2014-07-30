package org.apache.commons.math3.dfp;
/** 
 * An interface representing a univariate {@link Dfp} function.
 * @version $Id: UnivariateDfpFunction.java 1416643 2012-12-03 19:37:14Z tn $
 */
public interface UnivariateDfpFunction {
  /** 
 * Compute the value of the function.
 * @param x Point at which the function value should be computed.
 * @return the value.
 * @throws IllegalArgumentException when the activated method itself can
 * ascertain that preconditions, specified in the API expressed at the
 * level of the activated method, have been violated.  In the vast
 * majority of cases where Commons-Math throws IllegalArgumentException,
 * it is the result of argument checking of actual parameters immediately
 * passed to a method.
 */
  Dfp value(  Dfp x);
}