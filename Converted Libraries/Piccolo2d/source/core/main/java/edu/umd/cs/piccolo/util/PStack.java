package edu.umd.cs.piccolo.util;
import java.util.ArrayList;
/** 
 * <b>PStack</b> this class should be removed when a non thread safe stack is
 * added to the java class libraries.
 * <p>
 * @version 1.0
 * @author Jesse Grosjean
 */
public class PStack extends ArrayList {
  /** 
 * Allows for future serialization code to understand versioned binary
 * formats.
 */
  private static final long serialVersionUID=1L;
  /** 
 * Creates an empty stack.
 */
  public PStack(){
  }
  /** 
 * Pushes the provided object onto the top of the stack.
 * @param o object to add to the stack
 */
  public void push(  final Object o){
    add(o);
  }
  /** 
 * Returns  topmost element on the stack, or null if stack is empty.
 * @return topmost element on the stack, or null if empty
 */
  public Object peek(){
    final int s=size();
    if (s == 0) {
      return null;
    }
 else {
      return get(s - 1);
    }
  }
  /** 
 * Removes top element on the stack and returns it.
 * @return topmost element on stack.
 */
  public Object pop(){
    return remove(size() - 1);
  }
}
