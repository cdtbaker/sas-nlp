package cern.colt.function;
/** 
 * Interface that represents a function object: a function that takes two arguments.
 */
public interface IntDoubleFunction {
  /** 
 * Applies a function to two arguments.
 * @param first   first argument passed to the function.
 * @param second  second argument passed to the function.
 * @return the result of the function.
 */
  abstract public double apply(  int first,  double second);
}
