package cern.colt.function;
/** 
 * Interface that represents a procedure object: a procedure that takes 
 * two arguments and does not return a value.
 */
public interface IntIntProcedure {
  /** 
 * Applies a procedure to two arguments.
 * Optionally can return a boolean flag to inform the object calling the procedure.
 * <p>Example: forEach() methods often use procedure objects.
 * To signal to a forEach() method whether iteration should continue normally or terminate (because for example a matching element has been found),
 * a procedure can return <tt>false</tt> to indicate termination and <tt>true</tt> to indicate continuation.
 * @param first   first argument passed to the procedure.
 * @param second   second argument passed to the procedure.
 * @return a flag  to inform the object calling the procedure.
 */
  abstract public boolean apply(  int first,  int second);
}
