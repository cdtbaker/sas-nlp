package com.sun.jdi.event;
import com.sun.jdi.*;
/** 
 * Notification of a field modification in the
 * target VM.
 * @see EventQueue
 * @see VirtualMachine
 * @see com.sun.jdi.request.ModificationWatchpointRequest
 * @author Robert Field
 * @since  1.3
 */
public interface ModificationWatchpointEvent extends WatchpointEvent {
  /** 
 * Value that will be assigned to the field when the instruction
 * completes.
 */
  Value valueToBe();
}
