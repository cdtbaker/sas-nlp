/** 
 * Java interface "Delegate.java" generated from Poseidon for UML.
 * Poseidon for UML is developed by <A HREF="http://www.gentleware.com">Gentleware</A>.
 * Generated with <A HREF="http://jakarta.apache.org/velocity/">velocity</A> template engine.
 */
package com.sun.pept;
import com.sun.pept.presentation.MessageStruct;
import java.util.*;
/** 
 * <p>
 * @author Dr. Harold Carr
 * </p>
 */
public interface Delegate {
  /** 
 * <p>
 * Does ...
 * </p><p>
 * @return a MessageStruct with ...
 * </p>
 */
  public MessageStruct getMessageStruct();
  /** 
 * <p>
 * Does ...
 * </p><p>
 * </p><p>
 * @param message ...
 * </p>
 */
  public void send(  MessageStruct message);
}
