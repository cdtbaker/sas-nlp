package cern.colt.function;
/** 
 * Interface that represents a function object: a function that takes 
 * two arguments and returns a single value.
 */
public interface DoubleDoubleFunction {
  /** 
 * Applies a function to two arguments.
 * @param x   the first argument passed to the function.
 * @param y   the second argument passed to the function.
 * @return the result of the function.
 */
  abstract public double apply(  double x,  double y);
}
