package cern.colt.function;
/** 
 * Interface that represents a function object: a function that takes three arguments.
 */
public interface IntIntDoubleFunction {
  /** 
 * Applies a function to three arguments.
 * @param first   first argument passed to the function.
 * @param second  second argument passed to the function.
 * @param third   third argument passed to the function.
 * @return the result of the function.
 */
  abstract public double apply(  int first,  int second,  double third);
}
