package org.apache.commons.math3.ode.sampling;
/** 
 * {@link StepNormalizer Step normalizer} bounds settings. They influence
 * whether the underlying fixed step size step handler is called for the first
 * and last points. Note that if the last point coincides with a normalized
 * point, then the underlying fixed step size step handler is always called,
 * regardless of these settings.
 * @see StepNormalizer
 * @see StepNormalizerMode
 * @version $Id: StepNormalizerBounds.java 1416643 2012-12-03 19:37:14Z tn $
 * @since 3.0
 */
public enum StepNormalizerBounds {/** 
 * Do not include the first and last points. 
 */
NEITHER(false,false), /** 
 * Include the first point, but not the last point. 
 */
FIRST(true,false), /** 
 * Include the last point, but not the first point. 
 */
LAST(false,true), /** 
 * Include both the first and last points. 
 */
BOTH(true,true); /** 
 * Whether the first point should be passed to the underlying fixed
 * step size step handler.
 */
private final boolean first;
/** 
 * Whether the last point should be passed to the underlying fixed
 * step size step handler.
 */
private final boolean last;
/** 
 * Simple constructor.
 * @param first Whether the first point should be passed to the
 * underlying fixed step size step handler.
 * @param last Whether the last point should be passed to the
 * underlying fixed step size step handler.
 */
private StepNormalizerBounds(final boolean first,final boolean last){
  this.first=first;
  this.last=last;
}
/** 
 * Returns a value indicating whether the first point should be passed
 * to the underlying fixed step size step handler.
 * @return value indicating whether the first point should be passed
 * to the underlying fixed step size step handler.
 */
public boolean firstIncluded(){
  return first;
}
/** 
 * Returns a value indicating whether the last point should be passed
 * to the underlying fixed step size step handler.
 * @return value indicating whether the last point should be passed
 * to the underlying fixed step size step handler.
 */
public boolean lastIncluded(){
  return last;
}
}
