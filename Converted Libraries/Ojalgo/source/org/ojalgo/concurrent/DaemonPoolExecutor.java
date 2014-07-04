package org.ojalgo.concurrent;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ForkJoinPool;
import org.ojalgo.OjAlgoUtils;
import org.ojalgo.type.IntCount;
public final class DaemonPoolExecutor extends ForkJoinPool {
  public static final DaemonPoolExecutor INSTANCE=new DaemonPoolExecutor(OjAlgoUtils.ENVIRONMENT.threads);
  private DaemonPoolExecutor(){
    super();
  }
  private DaemonPoolExecutor(  final int parallelism){
    super(parallelism);
  }
  private DaemonPoolExecutor(  final int parallelism,  final ForkJoinWorkerThreadFactory factory,  final UncaughtExceptionHandler handler,  final boolean asyncMode){
    super(parallelism,factory,handler,asyncMode);
  }
  public IntCount countActiveDaemons(){
    return new IntCount(this.getActiveThreadCount());
  }
  public IntCount countExistingDaemons(){
    return new IntCount(this.getPoolSize());
  }
  public IntCount countIdleDaemons(){
    return new IntCount(this.getPoolSize() - this.getActiveThreadCount());
  }
  public boolean isDaemonAvailable(){
    return this.getPoolSize() > this.getActiveThreadCount();
  }
}
