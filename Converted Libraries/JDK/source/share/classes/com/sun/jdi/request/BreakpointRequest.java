package com.sun.jdi.request;
import com.sun.jdi.*;
/** 
 * Identifies a {@link Location} in the target VM at which
 * execution should be stopped. When an enabled BreakpointRequest is
 * satisfied, an{@link com.sun.jdi.event.EventSet event set} containing an{@link com.sun.jdi.event.BreakpointEvent BreakpointEvent}will be placed on the{@link com.sun.jdi.event.EventQueue EventQueue} and
 * the application is interrupted. The collection of existing breakpoints is
 * managed by the {@link EventRequestManager}
 * @see Location
 * @see com.sun.jdi.event.BreakpointEvent
 * @see com.sun.jdi.event.EventQueue
 * @see EventRequestManager
 * @author Robert Field
 * @since  1.3
 */
public interface BreakpointRequest extends EventRequest, Locatable {
  /** 
 * Returns the location of the requested breakpoint.
 * @return the {@link Location} where this breakpoint has been set.
 */
  Location location();
  /** 
 * Restricts the events generated by this request to those in
 * the given thread.
 * @param thread the thread to filter on.
 * @throws InvalidRequestStateException if this request is currently
 * enabled or has been deleted.
 * Filters may be added only to disabled requests.
 */
  void addThreadFilter(  ThreadReference thread);
  /** 
 * Restricts the events generated by this request to those in
 * which the currently executing instance is the object
 * specified.
 * <P>
 * Not all targets support this operation.
 * Use {@link VirtualMachine#canUseInstanceFilters()}to determine if the operation is supported.
 * @since 1.4
 * @param instance the object which must be the current instance
 * in order to pass this filter.
 * @throws java.lang.UnsupportedOperationException if
 * the target virtual machine does not support this
 * operation.
 * @throws InvalidRequestStateException if this request is currently
 * enabled or has been deleted.
 * Filters may be added only to disabled requests.
 */
  void addInstanceFilter(  ObjectReference instance);
}
