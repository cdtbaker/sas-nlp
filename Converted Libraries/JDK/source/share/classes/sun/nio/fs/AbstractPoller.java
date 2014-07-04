package sun.nio.fs;
import java.nio.file.*;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.io.IOException;
import java.util.*;
/** 
 * Base implementation of background poller thread used in watch service
 * implementations. A poller thread waits on events from the file system and
 * also services "requests" from clients to register for new events or cancel
 * existing registrations.
 */
abstract class AbstractPoller implements Runnable {
  private final LinkedList<Request> requestList;
  private boolean shutdown;
  protected AbstractPoller(){
    this.requestList=new LinkedList<Request>();
    this.shutdown=false;
  }
  /** 
 * Starts the poller thread
 */
  public void start(){
    final Runnable thisRunnable=this;
    AccessController.doPrivileged(new PrivilegedAction<Object>(){
      @Override public Object run(){
        Thread thr=new Thread(thisRunnable);
        thr.setDaemon(true);
        thr.start();
        return null;
      }
    }
);
  }
  /** 
 * Wakeup poller thread so that it can service pending requests
 */
  abstract void wakeup() throws IOException ;
  /** 
 * Executed by poller thread to register directory for changes
 */
  abstract Object implRegister(  Path path,  Set<? extends WatchEvent.Kind<?>> events,  WatchEvent.Modifier... modifiers);
  /** 
 * Executed by poller thread to cancel key
 */
  abstract void implCancelKey(  WatchKey key);
  /** 
 * Executed by poller thread to shutdown and cancel all keys
 */
  abstract void implCloseAll();
  /** 
 * Requests, and waits on, poller thread to register given file.
 */
  final WatchKey register(  Path dir,  WatchEvent.Kind<?>[] events,  WatchEvent.Modifier... modifiers) throws IOException {
    if (dir == null)     throw new NullPointerException();
    if (events.length == 0)     throw new IllegalArgumentException("No events to register");
    Set<WatchEvent.Kind<?>> eventSet=new HashSet<>(events.length);
    for (    WatchEvent.Kind<?> event : events) {
      if (event == StandardWatchEventKinds.ENTRY_CREATE || event == StandardWatchEventKinds.ENTRY_MODIFY || event == StandardWatchEventKinds.ENTRY_DELETE) {
        eventSet.add(event);
        continue;
      }
      if (event == StandardWatchEventKinds.OVERFLOW) {
        if (events.length == 1)         throw new IllegalArgumentException("No events to register");
        continue;
      }
      if (event == null)       throw new NullPointerException("An element in event set is 'null'");
      throw new UnsupportedOperationException(event.name());
    }
    return (WatchKey)invoke(RequestType.REGISTER,dir,eventSet,modifiers);
  }
  /** 
 * Cancels, and waits on, poller thread to cancel given key.
 */
  final void cancel(  WatchKey key){
    try {
      invoke(RequestType.CANCEL,key);
    }
 catch (    IOException x) {
      throw new AssertionError(x.getMessage());
    }
  }
  /** 
 * Shutdown poller thread
 */
  final void close() throws IOException {
    invoke(RequestType.CLOSE);
  }
  /** 
 * Types of request that the poller thread must handle
 */
  private static enum RequestType {  REGISTER,   CANCEL,   CLOSE}
  /** 
 * Encapsulates a request (command) to the poller thread.
 */
private static class Request {
    private final RequestType type;
    private final Object[] params;
    private boolean completed=false;
    private Object result=null;
    Request(    RequestType type,    Object... params){
      this.type=type;
      this.params=params;
    }
    RequestType type(){
      return type;
    }
    Object[] parameters(){
      return params;
    }
    void release(    Object result){
synchronized (this) {
        this.completed=true;
        this.result=result;
        notifyAll();
      }
    }
    /** 
 * Await completion of the request. The return value is the result of
 * the request.
 */
    Object awaitResult(){
synchronized (this) {
        while (!completed) {
          try {
            wait();
          }
 catch (          InterruptedException x) {
          }
        }
        return result;
      }
    }
  }
  /** 
 * Enqueues request to poller thread and waits for result
 */
  private Object invoke(  RequestType type,  Object... params) throws IOException {
    Request req=new Request(type,params);
synchronized (requestList) {
      if (shutdown) {
        throw new ClosedWatchServiceException();
      }
      requestList.add(req);
    }
    wakeup();
    Object result=req.awaitResult();
    if (result instanceof RuntimeException)     throw (RuntimeException)result;
    if (result instanceof IOException)     throw (IOException)result;
    return result;
  }
  /** 
 * Invoked by poller thread to process all pending requests
 * @return  true if poller thread should shutdown
 */
  @SuppressWarnings("unchecked") boolean processRequests(){
synchronized (requestList) {
      Request req;
      while ((req=requestList.poll()) != null) {
        if (shutdown) {
          req.release(new ClosedWatchServiceException());
        }
switch (req.type()) {
case REGISTER:
{
            Object[] params=req.parameters();
            Path path=(Path)params[0];
            Set<? extends WatchEvent.Kind<?>> events=(Set<? extends WatchEvent.Kind<?>>)params[1];
            WatchEvent.Modifier[] modifiers=(WatchEvent.Modifier[])params[2];
            req.release(implRegister(path,events,modifiers));
            break;
          }
case CANCEL:
{
          Object[] params=req.parameters();
          WatchKey key=(WatchKey)params[0];
          implCancelKey(key);
          req.release(null);
          break;
        }
case CLOSE:
{
        implCloseAll();
        req.release(null);
        shutdown=true;
        break;
      }
default :
    req.release(new IOException("request not recognized"));
}
}
}
return shutdown;
}
}
