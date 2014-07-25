package com.sun.jdi.event;
import com.sun.jdi.*;
/** 
 * Notification of a field access in the target VM. Field modifications
 * are not considered field accesses.
 * @see EventQueue
 * @see VirtualMachine
 * @author Robert Field
 * @since  1.3
 */
public interface AccessWatchpointEvent extends WatchpointEvent {
}
