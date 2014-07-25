package org.ojalgo.concurrent;
import java.util.concurrent.ForkJoinTask;
import org.ojalgo.OjAlgoUtils;
/** 
 * @author apete
 */
public abstract class DivideAndConquer extends Object {
abstract class Task extends ForkJoinTask<Void> {
    Task(){
      super();
    }
    @Override public final Void getRawResult(){
      return null;
    }
    @Override protected final void setRawResult(    final Void value){
    }
  }
  static int INITIAL=OjAlgoUtils.ENVIRONMENT.threads;
  static int adjustThreshold(  final int threshold,  final int count){
    return Math.max(1,(threshold * threshold) / count);
  }
  static boolean shouldDivideFurther(  final int count,  final int threshold,  final int workers){
    return (count > threshold) && (workers > 1);
  }
  public DivideAndConquer(){
    super();
  }
  /** 
 * Asynchronous execution - start it, and forget about it.
 * @param first The first index, in a range, to include.
 * @param limit The first index NOT to include - last (excl.) index in a range.
 */
  public final void execute(  final int first,  final int limit,  final int threshold){
    DaemonPoolExecutor.INSTANCE.execute(new Task(){
      @Override protected boolean exec(){
        DivideAndConquer.this.divide(first,limit,DivideAndConquer.adjustThreshold(threshold,limit - first),INITIAL);
        return true;
      }
    }
);
  }
  /** 
 * Synchronous execution - wait until it's finished.
 * @param first The first index, in a range, to include.
 * @param limit The first index NOT to include - last (excl.) index in a range.
 */
  public final void invoke(  final int first,  final int limit,  final int threshold){
    DaemonPoolExecutor.INSTANCE.invoke(new Task(){
      @Override protected boolean exec(){
        DivideAndConquer.this.divide(first,limit,DivideAndConquer.adjustThreshold(threshold,limit - first),INITIAL);
        return true;
      }
    }
);
  }
  protected abstract void conquer(  final int first,  final int limit);
  protected final void divide(  final int first,  final int limit,  final int threshold,  final int workers){
    final int tmpCount=limit - first;
    if (DivideAndConquer.shouldDivideFurther(tmpCount,threshold,workers)) {
      final int tmpSplit=first + (tmpCount / 2);
      final int tmpWorkers=workers / 2;
      final Task tmpForkedTask=new Task(){
        @Override protected boolean exec(){
          DivideAndConquer.this.divide(first,tmpSplit,threshold,tmpWorkers);
          return true;
        }
      }
;
      tmpForkedTask.fork();
      this.divide(tmpSplit,limit,threshold,tmpWorkers);
      tmpForkedTask.join();
    }
 else {
      this.conquer(first,limit);
    }
  }
}
