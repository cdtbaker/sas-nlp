package sun.nio.ch;
import java.nio.channels.*;
import java.util.concurrent.*;
import java.security.AccessController;
import sun.security.action.GetIntegerAction;
/** 
 * Defines static methods to invoke a completion handler or arbitrary task.
 */
class Invoker {
  private Invoker(){
  }
  private static final int maxHandlerInvokeCount=AccessController.doPrivileged(new GetIntegerAction("sun.nio.ch.maxCompletionHandlersOnStack",16));
static class GroupAndInvokeCount {
    private final AsynchronousChannelGroupImpl group;
    private int handlerInvokeCount;
    GroupAndInvokeCount(    AsynchronousChannelGroupImpl group){
      this.group=group;
    }
    AsynchronousChannelGroupImpl group(){
      return group;
    }
    int invokeCount(){
      return handlerInvokeCount;
    }
    void setInvokeCount(    int value){
      handlerInvokeCount=value;
    }
    void resetInvokeCount(){
      handlerInvokeCount=0;
    }
    void incrementInvokeCount(){
      handlerInvokeCount++;
    }
  }
  private static final ThreadLocal<GroupAndInvokeCount> myGroupAndInvokeCount=new ThreadLocal<GroupAndInvokeCount>(){
    @Override protected GroupAndInvokeCount initialValue(){
      return null;
    }
  }
;
  /** 
 * Binds this thread to the given group
 */
  static void bindToGroup(  AsynchronousChannelGroupImpl group){
    myGroupAndInvokeCount.set(new GroupAndInvokeCount(group));
  }
  /** 
 * Returns the GroupAndInvokeCount object for this thread.
 */
  static GroupAndInvokeCount getGroupAndInvokeCount(){
    return myGroupAndInvokeCount.get();
  }
  /** 
 * Returns true if the current thread is in a channel group's thread pool
 */
  static boolean isBoundToAnyGroup(){
    return myGroupAndInvokeCount.get() != null;
  }
  /** 
 * Returns true if the current thread is in the given channel's thread
 * pool and we haven't exceeded the maximum number of handler frames on
 * the stack.
 */
  static boolean mayInvokeDirect(  GroupAndInvokeCount myGroupAndInvokeCount,  AsynchronousChannelGroupImpl group){
    if ((myGroupAndInvokeCount != null) && (myGroupAndInvokeCount.group() == group) && (myGroupAndInvokeCount.invokeCount() < maxHandlerInvokeCount)) {
      return true;
    }
    return false;
  }
  /** 
 * Invoke handler without checking the thread identity or number of handlers
 * on the thread stack.
 */
  static <V,A>void invokeUnchecked(  CompletionHandler<V,? super A> handler,  A attachment,  V value,  Throwable exc){
    if (exc == null) {
      handler.completed(value,attachment);
    }
 else {
      handler.failed(exc,attachment);
    }
    Thread.interrupted();
  }
  /** 
 * Invoke handler assuming thread identity already checked
 */
  static <V,A>void invokeDirect(  GroupAndInvokeCount myGroupAndInvokeCount,  CompletionHandler<V,? super A> handler,  A attachment,  V result,  Throwable exc){
    myGroupAndInvokeCount.incrementInvokeCount();
    Invoker.invokeUnchecked(handler,attachment,result,exc);
  }
  /** 
 * Invokes the handler. If the current thread is in the channel group's
 * thread pool then the handler is invoked directly, otherwise it is
 * invoked indirectly.
 */
  static <V,A>void invoke(  AsynchronousChannel channel,  CompletionHandler<V,? super A> handler,  A attachment,  V result,  Throwable exc){
    boolean invokeDirect=false;
    boolean identityOkay=false;
    GroupAndInvokeCount thisGroupAndInvokeCount=myGroupAndInvokeCount.get();
    if (thisGroupAndInvokeCount != null) {
      if ((thisGroupAndInvokeCount.group() == ((Groupable)channel).group()))       identityOkay=true;
      if (identityOkay && (thisGroupAndInvokeCount.invokeCount() < maxHandlerInvokeCount)) {
        invokeDirect=true;
      }
    }
    if (invokeDirect) {
      invokeDirect(thisGroupAndInvokeCount,handler,attachment,result,exc);
    }
 else {
      try {
        invokeIndirectly(channel,handler,attachment,result,exc);
      }
 catch (      RejectedExecutionException ree) {
        if (identityOkay) {
          invokeDirect(thisGroupAndInvokeCount,handler,attachment,result,exc);
        }
 else {
          throw new ShutdownChannelGroupException();
        }
      }
    }
  }
  /** 
 * Invokes the handler indirectly via the channel group's thread pool.
 */
  static <V,A>void invokeIndirectly(  AsynchronousChannel channel,  final CompletionHandler<V,? super A> handler,  final A attachment,  final V result,  final Throwable exc){
    try {
      ((Groupable)channel).group().executeOnPooledThread(new Runnable(){
        public void run(){
          GroupAndInvokeCount thisGroupAndInvokeCount=myGroupAndInvokeCount.get();
          if (thisGroupAndInvokeCount != null)           thisGroupAndInvokeCount.setInvokeCount(1);
          invokeUnchecked(handler,attachment,result,exc);
        }
      }
);
    }
 catch (    RejectedExecutionException ree) {
      throw new ShutdownChannelGroupException();
    }
  }
  /** 
 * Invokes the handler "indirectly" in the given Executor
 */
  static <V,A>void invokeIndirectly(  final CompletionHandler<V,? super A> handler,  final A attachment,  final V value,  final Throwable exc,  Executor executor){
    try {
      executor.execute(new Runnable(){
        public void run(){
          invokeUnchecked(handler,attachment,value,exc);
        }
      }
);
    }
 catch (    RejectedExecutionException ree) {
      throw new ShutdownChannelGroupException();
    }
  }
  /** 
 * Invokes the given task on the thread pool associated with the given
 * channel. If the current thread is in the thread pool then the task is
 * invoked directly.
 */
  static void invokeOnThreadInThreadPool(  Groupable channel,  Runnable task){
    boolean invokeDirect;
    GroupAndInvokeCount thisGroupAndInvokeCount=myGroupAndInvokeCount.get();
    AsynchronousChannelGroupImpl targetGroup=channel.group();
    if (thisGroupAndInvokeCount == null) {
      invokeDirect=false;
    }
 else {
      invokeDirect=(thisGroupAndInvokeCount.group == targetGroup);
    }
    try {
      if (invokeDirect) {
        task.run();
      }
 else {
        targetGroup.executeOnPooledThread(task);
      }
    }
 catch (    RejectedExecutionException ree) {
      throw new ShutdownChannelGroupException();
    }
  }
  /** 
 * Invoke handler with completed result. This method does not check the
 * thread identity or the number of handlers on the thread stack.
 */
  static <V,A>void invokeUnchecked(  PendingFuture<V,A> future){
    assert future.isDone();
    CompletionHandler<V,? super A> handler=future.handler();
    if (handler != null) {
      invokeUnchecked(handler,future.attachment(),future.value(),future.exception());
    }
  }
  /** 
 * Invoke handler with completed result. If the current thread is in the
 * channel group's thread pool then the handler is invoked directly,
 * otherwise it is invoked indirectly.
 */
  static <V,A>void invoke(  PendingFuture<V,A> future){
    assert future.isDone();
    CompletionHandler<V,? super A> handler=future.handler();
    if (handler != null) {
      invoke(future.channel(),handler,future.attachment(),future.value(),future.exception());
    }
  }
  /** 
 * Invoke handler with completed result. The handler is invoked indirectly,
 * via the channel group's thread pool.
 */
  static <V,A>void invokeIndirectly(  PendingFuture<V,A> future){
    assert future.isDone();
    CompletionHandler<V,? super A> handler=future.handler();
    if (handler != null) {
      invokeIndirectly(future.channel(),handler,future.attachment(),future.value(),future.exception());
    }
  }
}