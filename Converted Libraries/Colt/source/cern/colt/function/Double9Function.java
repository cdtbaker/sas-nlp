package cern.colt.function;
/** 
 * Interface that represents a function object: a function that takes 
 * 9 arguments and returns a single value.
 */
public interface Double9Function {
  /** 
 * Applies a function to nine arguments.
 * @return the result of the function.
 */
  abstract public double apply(  double a00,  double a01,  double a02,  double a10,  double a11,  double a12,  double a20,  double a21,  double a22);
}
