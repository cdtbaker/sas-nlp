package sun.nio.ch;
import java.util.concurrent.*;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;
import sun.security.action.GetIntegerAction;
/** 
 * Encapsulates a thread pool associated with a channel group.
 */
public class ThreadPool {
  private static final String DEFAULT_THREAD_POOL_THREAD_FACTORY="java.nio.channels.DefaultThreadPool.threadFactory";
  private static final String DEFAULT_THREAD_POOL_INITIAL_SIZE="java.nio.channels.DefaultThreadPool.initialSize";
  private static final ThreadFactory defaultThreadFactory=new ThreadFactory(){
    @Override public Thread newThread(    Runnable r){
      Thread t=new Thread(r);
      t.setDaemon(true);
      return t;
    }
  }
;
  private final ExecutorService executor;
  private final boolean isFixed;
  private final int poolSize;
  private ThreadPool(  ExecutorService executor,  boolean isFixed,  int poolSize){
    this.executor=executor;
    this.isFixed=isFixed;
    this.poolSize=poolSize;
  }
  ExecutorService executor(){
    return executor;
  }
  boolean isFixedThreadPool(){
    return isFixed;
  }
  int poolSize(){
    return poolSize;
  }
  static ThreadFactory defaultThreadFactory(){
    return defaultThreadFactory;
  }
private static class DefaultThreadPoolHolder {
    final static ThreadPool defaultThreadPool=createDefault();
  }
  static ThreadPool getDefault(){
    return DefaultThreadPoolHolder.defaultThreadPool;
  }
  static ThreadPool createDefault(){
    int initialSize=getDefaultThreadPoolInitialSize();
    if (initialSize < 0)     initialSize=Runtime.getRuntime().availableProcessors();
    ThreadFactory threadFactory=getDefaultThreadPoolThreadFactory();
    if (threadFactory == null)     threadFactory=defaultThreadFactory;
    ExecutorService executor=new ThreadPoolExecutor(0,Integer.MAX_VALUE,Long.MAX_VALUE,TimeUnit.MILLISECONDS,new SynchronousQueue<Runnable>(),threadFactory);
    return new ThreadPool(executor,false,initialSize);
  }
  static ThreadPool create(  int nThreads,  ThreadFactory factory){
    if (nThreads <= 0)     throw new IllegalArgumentException("'nThreads' must be > 0");
    ExecutorService executor=Executors.newFixedThreadPool(nThreads,factory);
    return new ThreadPool(executor,true,nThreads);
  }
  public static ThreadPool wrap(  ExecutorService executor,  int initialSize){
    if (executor == null)     throw new NullPointerException("'executor' is null");
    if (executor instanceof ThreadPoolExecutor) {
      int max=((ThreadPoolExecutor)executor).getMaximumPoolSize();
      if (max == Integer.MAX_VALUE) {
        if (initialSize < 0) {
          initialSize=Runtime.getRuntime().availableProcessors();
        }
 else {
          initialSize=0;
        }
      }
    }
 else {
      if (initialSize < 0)       initialSize=0;
    }
    return new ThreadPool(executor,false,initialSize);
  }
  private static int getDefaultThreadPoolInitialSize(){
    String propValue=AccessController.doPrivileged(new GetPropertyAction(DEFAULT_THREAD_POOL_INITIAL_SIZE));
    if (propValue != null) {
      try {
        return Integer.parseInt(propValue);
      }
 catch (      NumberFormatException x) {
        throw new Error("Value of property '" + DEFAULT_THREAD_POOL_INITIAL_SIZE + "' is invalid: "+ x);
      }
    }
    return -1;
  }
  private static ThreadFactory getDefaultThreadPoolThreadFactory(){
    String propValue=AccessController.doPrivileged(new GetPropertyAction(DEFAULT_THREAD_POOL_THREAD_FACTORY));
    if (propValue != null) {
      try {
        Class<?> c=Class.forName(propValue,true,ClassLoader.getSystemClassLoader());
        return ((ThreadFactory)c.newInstance());
      }
 catch (      ClassNotFoundException x) {
        throw new Error(x);
      }
catch (      InstantiationException x) {
        throw new Error(x);
      }
catch (      IllegalAccessException x) {
        throw new Error(x);
      }
    }
    return null;
  }
}
