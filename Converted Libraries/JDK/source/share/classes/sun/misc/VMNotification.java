package sun.misc;
/** 
 * @deprecated 
 */
@Deprecated public interface VMNotification {
  void newAllocState(  int oldState,  int newState,  boolean threadsSuspended);
}
