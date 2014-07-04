/** 
 * Java interface "MessageDispatcher.java" generated from Poseidon for UML.
 * Poseidon for UML is developed by <A HREF="http://www.gentleware.com">Gentleware</A>.
 * Generated with <A HREF="http://jakarta.apache.org/velocity/">velocity</A> template engine.
 */
package com.sun.pept.protocol;
import com.sun.pept.ept.MessageInfo;
import java.util.*;
/** 
 * <p>
 * @author Dr. Harold Carr
 * </p>
 */
public interface MessageDispatcher {
  /** 
 * <p>
 * Does ...
 * </p><p>
 * </p><p>
 * @param messageInfo ...
 * </p>
 */
  public void send(  MessageInfo messageInfo);
  /** 
 * <p>
 * Does ...
 * </p><p>
 * </p><p>
 * @param messageInfo ...
 * </p>
 */
  public void receive(  MessageInfo messageInfo);
}
