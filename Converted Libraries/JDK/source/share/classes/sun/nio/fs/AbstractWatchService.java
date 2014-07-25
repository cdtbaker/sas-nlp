package sun.nio.fs;
import java.nio.file.*;
import java.util.concurrent.*;
import java.io.IOException;
/** 
 * Base implementation class for watch services.
 */
abstract class AbstractWatchService implements WatchService {
  private final LinkedBlockingDeque<WatchKey> pendingKeys=new LinkedBlockingDeque<WatchKey>();
  private final WatchKey CLOSE_KEY=new AbstractWatchKey(null,null){
    @Override public boolean isValid(){
      return true;
    }
    @Override public void cancel(){
    }
  }
;
  private volatile boolean closed;
  private final Object closeLock=new Object();
  protected AbstractWatchService(){
  }
  /** 
 * Register the given object with this watch service
 */
  abstract WatchKey register(  Path path,  WatchEvent.Kind<?>[] events,  WatchEvent.Modifier... modifers) throws IOException ;
  final void enqueueKey(  WatchKey key){
    pendingKeys.offer(key);
  }
  /** 
 * Throws ClosedWatchServiceException if watch service is closed
 */
  private void checkOpen(){
    if (closed)     throw new ClosedWatchServiceException();
  }
  /** 
 * Checks the key isn't the special CLOSE_KEY used to unblock threads when
 * the watch service is closed.
 */
  private void checkKey(  WatchKey key){
    if (key == CLOSE_KEY) {
      enqueueKey(key);
    }
    checkOpen();
  }
  @Override public final WatchKey poll(){
    checkOpen();
    WatchKey key=pendingKeys.poll();
    checkKey(key);
    return key;
  }
  @Override public final WatchKey poll(  long timeout,  TimeUnit unit) throws InterruptedException {
    checkOpen();
    WatchKey key=pendingKeys.poll(timeout,unit);
    checkKey(key);
    return key;
  }
  @Override public final WatchKey take() throws InterruptedException {
    checkOpen();
    WatchKey key=pendingKeys.take();
    checkKey(key);
    return key;
  }
  /** 
 * Tells whether or not this watch service is open.
 */
  final boolean isOpen(){
    return !closed;
  }
  /** 
 * Retrieves the object upon which the close method synchronizes.
 */
  final Object closeLock(){
    return closeLock;
  }
  /** 
 * Closes this watch service. This method is invoked by the close
 * method to perform the actual work of closing the watch service.
 */
  abstract void implClose() throws IOException ;
  @Override public final void close() throws IOException {
synchronized (closeLock) {
      if (closed)       return;
      closed=true;
      implClose();
      pendingKeys.clear();
      pendingKeys.offer(CLOSE_KEY);
    }
  }
}
