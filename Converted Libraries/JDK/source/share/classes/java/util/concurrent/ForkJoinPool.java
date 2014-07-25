package java.util.concurrent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;
/** 
 * An {@link ExecutorService} for running {@link ForkJoinTask}s.
 * A {@code ForkJoinPool} provides the entry point for submissions
 * from non-{@code ForkJoinTask} clients, as well as management and
 * monitoring operations.
 * <p>A {@code ForkJoinPool} differs from other kinds of {@link ExecutorService} mainly by virtue of employing
 * <em>work-stealing</em>: all threads in the pool attempt to find and
 * execute subtasks created by other active tasks (eventually blocking
 * waiting for work if none exist). This enables efficient processing
 * when most tasks spawn other subtasks (as do most {@codeForkJoinTask}s). When setting <em>asyncMode</em> to true in
 * constructors, {@code ForkJoinPool}s may also be appropriate for use
 * with event-style tasks that are never joined.
 * <p>A {@code ForkJoinPool} is constructed with a given target
 * parallelism level; by default, equal to the number of available
 * processors. The pool attempts to maintain enough active (or
 * available) threads by dynamically adding, suspending, or resuming
 * internal worker threads, even if some tasks are stalled waiting to
 * join others. However, no such adjustments are guaranteed in the
 * face of blocked IO or other unmanaged synchronization. The nested{@link ManagedBlocker} interface enables extension of the kinds of
 * synchronization accommodated.
 * <p>In addition to execution and lifecycle control methods, this
 * class provides status check methods (for example{@link #getStealCount}) that are intended to aid in developing,
 * tuning, and monitoring fork/join applications. Also, method{@link #toString} returns indications of pool state in a
 * convenient form for informal monitoring.
 * <p> As is the case with other ExecutorServices, there are three
 * main task execution methods summarized in the following
 * table. These are designed to be used by clients not already engaged
 * in fork/join computations in the current pool.  The main forms of
 * these methods accept instances of {@code ForkJoinTask}, but
 * overloaded forms also allow mixed execution of plain {@codeRunnable}- or {@code Callable}- based activities as well.  However,
 * tasks that are already executing in a pool should normally
 * <em>NOT</em> use these pool execution methods, but instead use the
 * within-computation forms listed in the table.
 * <table BORDER CELLPADDING=3 CELLSPACING=1>
 * <tr>
 * <td></td>
 * <td ALIGN=CENTER> <b>Call from non-fork/join clients</b></td>
 * <td ALIGN=CENTER> <b>Call from within fork/join computations</b></td>
 * </tr>
 * <tr>
 * <td> <b>Arrange async execution</td>
 * <td> {@link #execute(ForkJoinTask)}</td>
 * <td> {@link ForkJoinTask#fork}</td>
 * </tr>
 * <tr>
 * <td> <b>Await and obtain result</td>
 * <td> {@link #invoke(ForkJoinTask)}</td>
 * <td> {@link ForkJoinTask#invoke}</td>
 * </tr>
 * <tr>
 * <td> <b>Arrange exec and obtain Future</td>
 * <td> {@link #submit(ForkJoinTask)}</td>
 * <td> {@link ForkJoinTask#fork} (ForkJoinTasks <em>are</em> Futures)</td>
 * </tr>
 * </table>
 * <p><b>Sample Usage.</b> Normally a single {@code ForkJoinPool} is
 * used for all parallel task execution in a program or subsystem.
 * Otherwise, use would not usually outweigh the construction and
 * bookkeeping overhead of creating a large set of threads. For
 * example, a common pool could be used for the {@code SortTasks}illustrated in {@link RecursiveAction}. Because {@codeForkJoinPool} uses threads in {@linkplain java.lang.Thread#isDaemondaemon} mode, there is typically no need to explicitly {@link #shutdown} such a pool upon program exit.
 * <pre>
 * static final ForkJoinPool mainPool = new ForkJoinPool();
 * ...
 * public void sort(long[] array) {
 * mainPool.invoke(new SortTask(array, 0, array.length));
 * }
 * </pre>
 * <p><b>Implementation notes</b>: This implementation restricts the
 * maximum number of running threads to 32767. Attempts to create
 * pools with greater than the maximum number result in{@code IllegalArgumentException}.
 * <p>This implementation rejects submitted tasks (that is, by throwing{@link RejectedExecutionException}) only when the pool is shut down
 * or internal resources have been exhausted.
 * @since 1.7
 * @author Doug Lea
 */
public class ForkJoinPool extends AbstractExecutorService {
  /** 
 * Factory for creating new {@link ForkJoinWorkerThread}s.
 * A {@code ForkJoinWorkerThreadFactory} must be defined and used
 * for {@code ForkJoinWorkerThread} subclasses that extend base
 * functionality or initialize threads with different contexts.
 */
public static interface ForkJoinWorkerThreadFactory {
    /** 
 * Returns a new worker thread operating in the given pool.
 * @param pool the pool this thread works in
 * @throws NullPointerException if the pool is null
 */
    public ForkJoinWorkerThread newThread(    ForkJoinPool pool);
  }
  /** 
 * Default ForkJoinWorkerThreadFactory implementation; creates a
 * new ForkJoinWorkerThread.
 */
static class DefaultForkJoinWorkerThreadFactory implements ForkJoinWorkerThreadFactory {
    public ForkJoinWorkerThread newThread(    ForkJoinPool pool){
      return new ForkJoinWorkerThread(pool);
    }
  }
  /** 
 * Creates a new ForkJoinWorkerThread. This factory is used unless
 * overridden in ForkJoinPool constructors.
 */
  public static final ForkJoinWorkerThreadFactory defaultForkJoinWorkerThreadFactory;
  /** 
 * Permission required for callers of methods that may start or
 * kill threads.
 */
  private static final RuntimePermission modifyThreadPermission;
  /** 
 * If there is a security manager, makes sure caller has
 * permission to modify threads.
 */
  private static void checkPermission(){
    SecurityManager security=System.getSecurityManager();
    if (security != null)     security.checkPermission(modifyThreadPermission);
  }
  /** 
 * Generator for assigning sequence numbers as pool names.
 */
  private static final AtomicInteger poolNumberGenerator;
  /** 
 * Generator for initial random seeds for worker victim
 * selection. This is used only to create initial seeds. Random
 * steals use a cheaper xorshift generator per steal attempt. We
 * don't expect much contention on seedGenerator, so just use a
 * plain Random.
 */
  static final Random workerSeedGenerator;
  /** 
 * Array holding all worker threads in the pool.  Initialized upon
 * construction. Array size must be a power of two.  Updates and
 * replacements are protected by scanGuard, but the array is
 * always kept in a consistent enough state to be randomly
 * accessed without locking by workers performing work-stealing,
 * as well as other traversal-based methods in this class, so long
 * as reads memory-acquire by first reading ctl. All readers must
 * tolerate that some array slots may be null.
 */
  ForkJoinWorkerThread[] workers;
  /** 
 * Initial size for submission queue array. Must be a power of
 * two.  In many applications, these always stay small so we use a
 * small initial cap.
 */
  private static final int INITIAL_QUEUE_CAPACITY=8;
  /** 
 * Maximum size for submission queue array. Must be a power of two
 * less than or equal to 1 << (31 - width of array entry) to
 * ensure lack of index wraparound, but is capped at a lower
 * value to help users trap runaway computations.
 */
  private static final int MAXIMUM_QUEUE_CAPACITY=1 << 24;
  /** 
 * Array serving as submission queue. Initialized upon construction.
 */
  private ForkJoinTask<?>[] submissionQueue;
  /** 
 * Lock protecting submissions array for addSubmission
 */
  private final ReentrantLock submissionLock;
  /** 
 * Condition for awaitTermination, using submissionLock for
 * convenience.
 */
  private final Condition termination;
  /** 
 * Creation factory for worker threads.
 */
  private final ForkJoinWorkerThreadFactory factory;
  /** 
 * The uncaught exception handler used when any worker abruptly
 * terminates.
 */
  final Thread.UncaughtExceptionHandler ueh;
  /** 
 * Prefix for assigning names to worker threads
 */
  private final String workerNamePrefix;
  /** 
 * Sum of per-thread steal counts, updated only when threads are
 * idle or terminating.
 */
  private volatile long stealCount;
  /** 
 * Main pool control -- a long packed with:
 * AC: Number of active running workers minus target parallelism (16 bits)
 * TC: Number of total workers minus target parallelism (16bits)
 * ST: true if pool is terminating (1 bit)
 * EC: the wait count of top waiting thread (15 bits)
 * ID: ~poolIndex of top of Treiber stack of waiting threads (16 bits)
 * When convenient, we can extract the upper 32 bits of counts and
 * the lower 32 bits of queue state, u = (int)(ctl >>> 32) and e =
 * (int)ctl.  The ec field is never accessed alone, but always
 * together with id and st. The offsets of counts by the target
 * parallelism and the positionings of fields makes it possible to
 * perform the most common checks via sign tests of fields: When
 * ac is negative, there are not enough active workers, when tc is
 * negative, there are not enough total workers, when id is
 * negative, there is at least one waiting worker, and when e is
 * negative, the pool is terminating.  To deal with these possibly
 * negative fields, we use casts in and out of "short" and/or
 * signed shifts to maintain signedness.
 */
  volatile long ctl;
  private static final int AC_SHIFT=48;
  private static final int TC_SHIFT=32;
  private static final int ST_SHIFT=31;
  private static final int EC_SHIFT=16;
  private static final int MAX_ID=0x7fff;
  private static final int SMASK=0xffff;
  private static final int SHORT_SIGN=1 << 15;
  private static final int INT_SIGN=1 << 31;
  private static final long STOP_BIT=0x0001L << ST_SHIFT;
  private static final long AC_MASK=((long)SMASK) << AC_SHIFT;
  private static final long TC_MASK=((long)SMASK) << TC_SHIFT;
  private static final long TC_UNIT=1L << TC_SHIFT;
  private static final long AC_UNIT=1L << AC_SHIFT;
  private static final int UAC_SHIFT=AC_SHIFT - 32;
  private static final int UTC_SHIFT=TC_SHIFT - 32;
  private static final int UAC_MASK=SMASK << UAC_SHIFT;
  private static final int UTC_MASK=SMASK << UTC_SHIFT;
  private static final int UAC_UNIT=1 << UAC_SHIFT;
  private static final int UTC_UNIT=1 << UTC_SHIFT;
  private static final int E_MASK=0x7fffffff;
  private static final int EC_UNIT=1 << EC_SHIFT;
  /** 
 * The target parallelism level.
 */
  final int parallelism;
  /** 
 * Index (mod submission queue length) of next element to take
 * from submission queue. Usage is identical to that for
 * per-worker queues -- see ForkJoinWorkerThread internal
 * documentation.
 */
  volatile int queueBase;
  /** 
 * Index (mod submission queue length) of next element to add
 * in submission queue. Usage is identical to that for
 * per-worker queues -- see ForkJoinWorkerThread internal
 * documentation.
 */
  int queueTop;
  /** 
 * True when shutdown() has been called.
 */
  volatile boolean shutdown;
  /** 
 * True if use local fifo, not default lifo, for local polling
 * Read by, and replicated by ForkJoinWorkerThreads
 */
  final boolean locallyFifo;
  /** 
 * The number of threads in ForkJoinWorkerThreads.helpQuiescePool.
 * When non-zero, suppresses automatic shutdown when active
 * counts become zero.
 */
  volatile int quiescerCount;
  /** 
 * The number of threads blocked in join.
 */
  volatile int blockedCount;
  /** 
 * Counter for worker Thread names (unrelated to their poolIndex)
 */
  private volatile int nextWorkerNumber;
  /** 
 * The index for the next created worker. Accessed under scanGuard.
 */
  private int nextWorkerIndex;
  /** 
 * SeqLock and index masking for updates to workers array.  Locked
 * when SG_UNIT is set. Unlocking clears bit by adding
 * SG_UNIT. Staleness of read-only operations can be checked by
 * comparing scanGuard to value before the reads. The low 16 bits
 * (i.e, anding with SMASK) hold (the smallest power of two
 * covering all worker indices, minus one, and is used to avoid
 * dealing with large numbers of null slots when the workers array
 * is overallocated.
 */
  volatile int scanGuard;
  private static final int SG_UNIT=1 << 16;
  /** 
 * The wakeup interval (in nanoseconds) for a worker waiting for a
 * task when the pool is quiescent to instead try to shrink the
 * number of workers.  The exact value does not matter too
 * much. It must be short enough to release resources during
 * sustained periods of idleness, but not so short that threads
 * are continually re-created.
 */
  private static final long SHRINK_RATE=4L * 1000L * 1000L* 1000L;
  /** 
 * Top-level loop for worker threads: On each step: if the
 * previous step swept through all queues and found no tasks, or
 * there are excess threads, then possibly blocks. Otherwise,
 * scans for and, if found, executes a task. Returns when pool
 * and/or worker terminate.
 * @param w the worker
 */
  final void work(  ForkJoinWorkerThread w){
    boolean swept=false;
    long c;
    while (!w.terminate && (int)(c=ctl) >= 0) {
      int a;
      if (!swept && (a=(int)(c >> AC_SHIFT)) <= 0)       swept=scan(w,a);
 else       if (tryAwaitWork(w,c))       swept=false;
    }
  }
  /** 
 * Wakes up or creates a worker.
 */
  final void signalWork(){
    long c;
    int e, u;
    while ((((e=(int)(c=ctl)) | (u=(int)(c >>> 32))) & (INT_SIGN | SHORT_SIGN)) == (INT_SIGN | SHORT_SIGN) && e >= 0) {
      if (e > 0) {
        int i;
        ForkJoinWorkerThread w;
        ForkJoinWorkerThread[] ws;
        if ((ws=workers) == null || (i=~e & SMASK) >= ws.length || (w=ws[i]) == null)         break;
        long nc=(((long)(w.nextWait & E_MASK)) | ((long)(u + UAC_UNIT) << 32));
        if (w.eventCount == e && UNSAFE.compareAndSwapLong(this,ctlOffset,c,nc)) {
          w.eventCount=(e + EC_UNIT) & E_MASK;
          if (w.parked)           UNSAFE.unpark(w);
          break;
        }
      }
 else       if (UNSAFE.compareAndSwapLong(this,ctlOffset,c,(long)(((u + UTC_UNIT) & UTC_MASK) | ((u + UAC_UNIT) & UAC_MASK)) << 32)) {
        addWorker();
        break;
      }
    }
  }
  /** 
 * Variant of signalWork to help release waiters on rescans.
 * Tries once to release a waiter if active count < 0.
 * @return false if failed due to contention, else true
 */
  private boolean tryReleaseWaiter(){
    long c;
    int e, i;
    ForkJoinWorkerThread w;
    ForkJoinWorkerThread[] ws;
    if ((e=(int)(c=ctl)) > 0 && (int)(c >> AC_SHIFT) < 0 && (ws=workers) != null && (i=~e & SMASK) < ws.length && (w=ws[i]) != null) {
      long nc=((long)(w.nextWait & E_MASK) | ((c + AC_UNIT) & (AC_MASK | TC_MASK)));
      if (w.eventCount != e || !UNSAFE.compareAndSwapLong(this,ctlOffset,c,nc))       return false;
      w.eventCount=(e + EC_UNIT) & E_MASK;
      if (w.parked)       UNSAFE.unpark(w);
    }
    return true;
  }
  /** 
 * Scans for and, if found, executes one task. Scans start at a
 * random index of workers array, and randomly select the first
 * (2*#workers)-1 probes, and then, if all empty, resort to 2
 * circular sweeps, which is necessary to check quiescence. and
 * taking a submission only if no stealable tasks were found.  The
 * steal code inside the loop is a specialized form of
 * ForkJoinWorkerThread.deqTask, followed bookkeeping to support
 * helpJoinTask and signal propagation. The code for submission
 * queues is almost identical. On each steal, the worker completes
 * not only the task, but also all local tasks that this task may
 * have generated. On detecting staleness or contention when
 * trying to take a task, this method returns without finishing
 * sweep, which allows global state rechecks before retry.
 * @param w the worker
 * @param a the number of active workers
 * @return true if swept all queues without finding a task
 */
  private boolean scan(  ForkJoinWorkerThread w,  int a){
    int g=scanGuard;
    int m=(parallelism == 1 - a && blockedCount == 0) ? 0 : g & SMASK;
    ForkJoinWorkerThread[] ws=workers;
    if (ws == null || ws.length <= m)     return false;
    for (int r=w.seed, k=r, j=-(m + m); j <= m + m; ++j) {
      ForkJoinTask<?> t;
      ForkJoinTask<?>[] q;
      int b, i;
      ForkJoinWorkerThread v=ws[k & m];
      if (v != null && (b=v.queueBase) != v.queueTop && (q=v.queue) != null && (i=(q.length - 1) & b) >= 0) {
        long u=(i << ASHIFT) + ABASE;
        if ((t=q[i]) != null && v.queueBase == b && UNSAFE.compareAndSwapObject(q,u,t,null)) {
          int d=(v.queueBase=b + 1) - v.queueTop;
          v.stealHint=w.poolIndex;
          if (d != 0)           signalWork();
          w.execTask(t);
        }
        r^=r << 13;
        r^=r >>> 17;
        w.seed=r ^ (r << 5);
        return false;
      }
 else       if (j < 0) {
        r^=r << 13;
        r^=r >>> 17;
        k=r^=r << 5;
      }
 else       ++k;
    }
    if (scanGuard != g)     return false;
 else {
      ForkJoinTask<?> t;
      ForkJoinTask<?>[] q;
      int b, i;
      if ((b=queueBase) != queueTop && (q=submissionQueue) != null && (i=(q.length - 1) & b) >= 0) {
        long u=(i << ASHIFT) + ABASE;
        if ((t=q[i]) != null && queueBase == b && UNSAFE.compareAndSwapObject(q,u,t,null)) {
          queueBase=b + 1;
          w.execTask(t);
        }
        return false;
      }
      return true;
    }
  }
  /** 
 * Tries to enqueue worker w in wait queue and await change in
 * worker's eventCount.  If the pool is quiescent and there is
 * more than one worker, possibly terminates worker upon exit.
 * Otherwise, before blocking, rescans queues to avoid missed
 * signals.  Upon finding work, releases at least one worker
 * (which may be the current worker). Rescans restart upon
 * detected staleness or failure to release due to
 * contention. Note the unusual conventions about Thread.interrupt
 * here and elsewhere: Because interrupts are used solely to alert
 * threads to check termination, which is checked here anyway, we
 * clear status (using Thread.interrupted) before any call to
 * park, so that park does not immediately return due to status
 * being set via some other unrelated call to interrupt in user
 * code.
 * @param w the calling worker
 * @param c the ctl value on entry
 * @return true if waited or another thread was released upon enq
 */
  private boolean tryAwaitWork(  ForkJoinWorkerThread w,  long c){
    int v=w.eventCount;
    w.nextWait=(int)c;
    long nc=(long)(v & E_MASK) | ((c - AC_UNIT) & (AC_MASK | TC_MASK));
    if (ctl != c || !UNSAFE.compareAndSwapLong(this,ctlOffset,c,nc)) {
      long d=ctl;
      return (int)d != (int)c && ((d - c) & AC_MASK) >= 0L;
    }
    for (int sc=w.stealCount; sc != 0; ) {
      long s=stealCount;
      if (UNSAFE.compareAndSwapLong(this,stealCountOffset,s,s + sc))       sc=w.stealCount=0;
 else       if (w.eventCount != v)       return true;
    }
    if ((!shutdown || !tryTerminate(false)) && (int)c != 0 && parallelism + (int)(nc >> AC_SHIFT) == 0 && blockedCount == 0 && quiescerCount == 0)     idleAwaitWork(w,nc,c,v);
    for (boolean rescanned=false; ; ) {
      if (w.eventCount != v)       return true;
      if (!rescanned) {
        int g=scanGuard, m=g & SMASK;
        ForkJoinWorkerThread[] ws=workers;
        if (ws != null && m < ws.length) {
          rescanned=true;
          for (int i=0; i <= m; ++i) {
            ForkJoinWorkerThread u=ws[i];
            if (u != null) {
              if (u.queueBase != u.queueTop && !tryReleaseWaiter())               rescanned=false;
              if (w.eventCount != v)               return true;
            }
          }
        }
        if (scanGuard != g || (queueBase != queueTop && !tryReleaseWaiter()))         rescanned=false;
        if (!rescanned)         Thread.yield();
 else         Thread.interrupted();
      }
 else {
        w.parked=true;
        if (w.eventCount != v) {
          w.parked=false;
          return true;
        }
        LockSupport.park(this);
        rescanned=w.parked=false;
      }
    }
  }
  /** 
 * If inactivating worker w has caused pool to become
 * quiescent, check for pool termination, and wait for event
 * for up to SHRINK_RATE nanosecs (rescans are unnecessary in
 * this case because quiescence reflects consensus about lack
 * of work). On timeout, if ctl has not changed, terminate the
 * worker. Upon its termination (see deregisterWorker), it may
 * wake up another worker to possibly repeat this process.
 * @param w the calling worker
 * @param currentCtl the ctl value after enqueuing w
 * @param prevCtl the ctl value if w terminated
 * @param v the eventCount w awaits change
 */
  private void idleAwaitWork(  ForkJoinWorkerThread w,  long currentCtl,  long prevCtl,  int v){
    if (w.eventCount == v) {
      if (shutdown)       tryTerminate(false);
      ForkJoinTask.helpExpungeStaleExceptions();
      while (ctl == currentCtl) {
        long startTime=System.nanoTime();
        w.parked=true;
        if (w.eventCount == v)         LockSupport.parkNanos(this,SHRINK_RATE);
        w.parked=false;
        if (w.eventCount != v)         break;
 else         if (System.nanoTime() - startTime < SHRINK_RATE - (SHRINK_RATE / 10))         Thread.interrupted();
 else         if (UNSAFE.compareAndSwapLong(this,ctlOffset,currentCtl,prevCtl)) {
          w.terminate=true;
          w.eventCount=((int)currentCtl + EC_UNIT) & E_MASK;
          break;
        }
      }
    }
  }
  /** 
 * Enqueues the given task in the submissionQueue.  Same idea as
 * ForkJoinWorkerThread.pushTask except for use of submissionLock.
 * @param t the task
 */
  private void addSubmission(  ForkJoinTask<?> t){
    final ReentrantLock lock=this.submissionLock;
    lock.lock();
    try {
      ForkJoinTask<?>[] q;
      int s, m;
      if ((q=submissionQueue) != null) {
        long u=(((s=queueTop) & (m=q.length - 1)) << ASHIFT) + ABASE;
        UNSAFE.putOrderedObject(q,u,t);
        queueTop=s + 1;
        if (s - queueBase == m)         growSubmissionQueue();
      }
    }
  finally {
      lock.unlock();
    }
    signalWork();
  }
  /** 
 * Creates or doubles submissionQueue array.
 * Basically identical to ForkJoinWorkerThread version.
 */
  private void growSubmissionQueue(){
    ForkJoinTask<?>[] oldQ=submissionQueue;
    int size=oldQ != null ? oldQ.length << 1 : INITIAL_QUEUE_CAPACITY;
    if (size > MAXIMUM_QUEUE_CAPACITY)     throw new RejectedExecutionException("Queue capacity exceeded");
    if (size < INITIAL_QUEUE_CAPACITY)     size=INITIAL_QUEUE_CAPACITY;
    ForkJoinTask<?>[] q=submissionQueue=new ForkJoinTask<?>[size];
    int mask=size - 1;
    int top=queueTop;
    int oldMask;
    if (oldQ != null && (oldMask=oldQ.length - 1) >= 0) {
      for (int b=queueBase; b != top; ++b) {
        long u=((b & oldMask) << ASHIFT) + ABASE;
        Object x=UNSAFE.getObjectVolatile(oldQ,u);
        if (x != null && UNSAFE.compareAndSwapObject(oldQ,u,x,null))         UNSAFE.putObjectVolatile(q,((b & mask) << ASHIFT) + ABASE,x);
      }
    }
  }
  /** 
 * Tries to increment blockedCount, decrement active count
 * (sometimes implicitly) and possibly release or create a
 * compensating worker in preparation for blocking. Fails
 * on contention or termination.
 * @return true if the caller can block, else should recheck and retry
 */
  private boolean tryPreBlock(){
    int b=blockedCount;
    if (UNSAFE.compareAndSwapInt(this,blockedCountOffset,b,b + 1)) {
      int pc=parallelism;
      do {
        ForkJoinWorkerThread[] ws;
        ForkJoinWorkerThread w;
        int e, ac, tc, rc, i;
        long c=ctl;
        int u=(int)(c >>> 32);
        if ((e=(int)c) < 0) {
        }
 else         if ((ac=(u >> UAC_SHIFT)) <= 0 && e != 0 && (ws=workers) != null && (i=~e & SMASK) < ws.length && (w=ws[i]) != null) {
          long nc=((long)(w.nextWait & E_MASK) | (c & (AC_MASK | TC_MASK)));
          if (w.eventCount == e && UNSAFE.compareAndSwapLong(this,ctlOffset,c,nc)) {
            w.eventCount=(e + EC_UNIT) & E_MASK;
            if (w.parked)             UNSAFE.unpark(w);
            return true;
          }
        }
 else         if ((tc=(short)(u >>> UTC_SHIFT)) >= 0 && ac + pc > 1) {
          long nc=((c - AC_UNIT) & AC_MASK) | (c & ~AC_MASK);
          if (UNSAFE.compareAndSwapLong(this,ctlOffset,c,nc))           return true;
        }
 else         if (tc + pc < MAX_ID) {
          long nc=((c + TC_UNIT) & TC_MASK) | (c & ~TC_MASK);
          if (UNSAFE.compareAndSwapLong(this,ctlOffset,c,nc)) {
            addWorker();
            return true;
          }
        }
      }
 while (!UNSAFE.compareAndSwapInt(this,blockedCountOffset,b=blockedCount,b - 1));
    }
    return false;
  }
  /** 
 * Decrements blockedCount and increments active count
 */
  private void postBlock(){
    long c;
    do {
    }
 while (!UNSAFE.compareAndSwapLong(this,ctlOffset,c=ctl,c + AC_UNIT));
    int b;
    do {
    }
 while (!UNSAFE.compareAndSwapInt(this,blockedCountOffset,b=blockedCount,b - 1));
  }
  /** 
 * Possibly blocks waiting for the given task to complete, or
 * cancels the task if terminating.  Fails to wait if contended.
 * @param joinMe the task
 */
  final void tryAwaitJoin(  ForkJoinTask<?> joinMe){
    int s;
    Thread.interrupted();
    if (joinMe.status >= 0) {
      if (tryPreBlock()) {
        joinMe.tryAwaitDone(0L);
        postBlock();
      }
 else       if ((ctl & STOP_BIT) != 0L)       joinMe.cancelIgnoringExceptions();
    }
  }
  /** 
 * Possibly blocks the given worker waiting for joinMe to
 * complete or timeout
 * @param joinMe the task
 * @param millis the wait time for underlying Object.wait
 */
  final void timedAwaitJoin(  ForkJoinTask<?> joinMe,  long nanos){
    while (joinMe.status >= 0) {
      Thread.interrupted();
      if ((ctl & STOP_BIT) != 0L) {
        joinMe.cancelIgnoringExceptions();
        break;
      }
      if (tryPreBlock()) {
        long last=System.nanoTime();
        while (joinMe.status >= 0) {
          long millis=TimeUnit.NANOSECONDS.toMillis(nanos);
          if (millis <= 0)           break;
          joinMe.tryAwaitDone(millis);
          if (joinMe.status < 0)           break;
          if ((ctl & STOP_BIT) != 0L) {
            joinMe.cancelIgnoringExceptions();
            break;
          }
          long now=System.nanoTime();
          nanos-=now - last;
          last=now;
        }
        postBlock();
        break;
      }
    }
  }
  /** 
 * If necessary, compensates for blocker, and blocks
 */
  private void awaitBlocker(  ManagedBlocker blocker) throws InterruptedException {
    while (!blocker.isReleasable()) {
      if (tryPreBlock()) {
        try {
          do {
          }
 while (!blocker.isReleasable() && !blocker.block());
        }
  finally {
          postBlock();
        }
        break;
      }
    }
  }
  /** 
 * Tries to create and start a worker; minimally rolls back counts
 * on failure.
 */
  private void addWorker(){
    Throwable ex=null;
    ForkJoinWorkerThread t=null;
    try {
      t=factory.newThread(this);
    }
 catch (    Throwable e) {
      ex=e;
    }
    if (t == null) {
      long c;
      do {
      }
 while (!UNSAFE.compareAndSwapLong(this,ctlOffset,c=ctl,(((c - AC_UNIT) & AC_MASK) | ((c - TC_UNIT) & TC_MASK) | (c & ~(AC_MASK | TC_MASK)))));
      if (!tryTerminate(false) && ex != null && !(Thread.currentThread() instanceof ForkJoinWorkerThread))       UNSAFE.throwException(ex);
    }
 else     t.start();
  }
  /** 
 * Callback from ForkJoinWorkerThread constructor to assign a
 * public name
 */
  final String nextWorkerName(){
    for (int n; ; ) {
      if (UNSAFE.compareAndSwapInt(this,nextWorkerNumberOffset,n=nextWorkerNumber,++n))       return workerNamePrefix + n;
    }
  }
  /** 
 * Callback from ForkJoinWorkerThread constructor to
 * determine its poolIndex and record in workers array.
 * @param w the worker
 * @return the worker's pool index
 */
  final int registerWorker(  ForkJoinWorkerThread w){
    for (int g; ; ) {
      ForkJoinWorkerThread[] ws;
      if (((g=scanGuard) & SG_UNIT) == 0 && UNSAFE.compareAndSwapInt(this,scanGuardOffset,g,g | SG_UNIT)) {
        int k=nextWorkerIndex;
        try {
          if ((ws=workers) != null) {
            int n=ws.length;
            if (k < 0 || k >= n || ws[k] != null) {
              for (k=0; k < n && ws[k] != null; ++k)               ;
              if (k == n)               ws=workers=Arrays.copyOf(ws,n << 1);
            }
            ws[k]=w;
            nextWorkerIndex=k + 1;
            int m=g & SMASK;
            g=(k > m) ? ((m << 1) + 1) & SMASK : g + (SG_UNIT << 1);
          }
        }
  finally {
          scanGuard=g;
        }
        return k;
      }
 else       if ((ws=workers) != null) {
        for (        ForkJoinWorkerThread u : ws) {
          if (u != null && u.queueBase != u.queueTop) {
            if (tryReleaseWaiter())             break;
          }
        }
      }
    }
  }
  /** 
 * Final callback from terminating worker.  Removes record of
 * worker from array, and adjusts counts. If pool is shutting
 * down, tries to complete termination.
 * @param w the worker
 */
  final void deregisterWorker(  ForkJoinWorkerThread w,  Throwable ex){
    int idx=w.poolIndex;
    int sc=w.stealCount;
    int steps=0;
    do {
      long s, c;
      int g;
      if (steps == 0 && ((g=scanGuard) & SG_UNIT) == 0 && UNSAFE.compareAndSwapInt(this,scanGuardOffset,g,g|=SG_UNIT)) {
        ForkJoinWorkerThread[] ws=workers;
        if (ws != null && idx >= 0 && idx < ws.length && ws[idx] == w)         ws[idx]=null;
        nextWorkerIndex=idx;
        scanGuard=g + SG_UNIT;
        steps=1;
      }
      if (steps == 1 && UNSAFE.compareAndSwapLong(this,ctlOffset,c=ctl,(((c - AC_UNIT) & AC_MASK) | ((c - TC_UNIT) & TC_MASK) | (c & ~(AC_MASK | TC_MASK)))))       steps=2;
      if (sc != 0 && UNSAFE.compareAndSwapLong(this,stealCountOffset,s=stealCount,s + sc))       sc=0;
    }
 while (steps != 2 || sc != 0);
    if (!tryTerminate(false)) {
      if (ex != null)       signalWork();
 else       tryReleaseWaiter();
    }
  }
  /** 
 * Possibly initiates and/or completes termination.
 * @param now if true, unconditionally terminate, else only
 * if shutdown and empty queue and no active workers
 * @return true if now terminating or terminated
 */
  private boolean tryTerminate(  boolean now){
    long c;
    while (((c=ctl) & STOP_BIT) == 0) {
      if (!now) {
        if ((int)(c >> AC_SHIFT) != -parallelism)         return false;
        if (!shutdown || blockedCount != 0 || quiescerCount != 0 || queueBase != queueTop) {
          if (ctl == c)           return false;
          continue;
        }
      }
      if (UNSAFE.compareAndSwapLong(this,ctlOffset,c,c | STOP_BIT))       startTerminating();
    }
    if ((short)(c >>> TC_SHIFT) == -parallelism) {
      final ReentrantLock lock=this.submissionLock;
      lock.lock();
      try {
        termination.signalAll();
      }
  finally {
        lock.unlock();
      }
    }
    return true;
  }
  /** 
 * Runs up to three passes through workers: (0) Setting
 * termination status for each worker, followed by wakeups up to
 * queued workers; (1) helping cancel tasks; (2) interrupting
 * lagging threads (likely in external tasks, but possibly also
 * blocked in joins).  Each pass repeats previous steps because of
 * potential lagging thread creation.
 */
  private void startTerminating(){
    cancelSubmissions();
    for (int pass=0; pass < 3; ++pass) {
      ForkJoinWorkerThread[] ws=workers;
      if (ws != null) {
        for (        ForkJoinWorkerThread w : ws) {
          if (w != null) {
            w.terminate=true;
            if (pass > 0) {
              w.cancelTasks();
              if (pass > 1 && !w.isInterrupted()) {
                try {
                  w.interrupt();
                }
 catch (                SecurityException ignore) {
                }
              }
            }
          }
        }
        terminateWaiters();
      }
    }
  }
  /** 
 * Polls and cancels all submissions. Called only during termination.
 */
  private void cancelSubmissions(){
    while (queueBase != queueTop) {
      ForkJoinTask<?> task=pollSubmission();
      if (task != null) {
        try {
          task.cancel(false);
        }
 catch (        Throwable ignore) {
        }
      }
    }
  }
  /** 
 * Tries to set the termination status of waiting workers, and
 * then wakes them up (after which they will terminate).
 */
  private void terminateWaiters(){
    ForkJoinWorkerThread[] ws=workers;
    if (ws != null) {
      ForkJoinWorkerThread w;
      long c;
      int i, e;
      int n=ws.length;
      while ((i=~(e=(int)(c=ctl)) & SMASK) < n && (w=ws[i]) != null && w.eventCount == (e & E_MASK)) {
        if (UNSAFE.compareAndSwapLong(this,ctlOffset,c,(long)(w.nextWait & E_MASK) | ((c + AC_UNIT) & AC_MASK) | (c & (TC_MASK | STOP_BIT)))) {
          w.terminate=true;
          w.eventCount=e + EC_UNIT;
          if (w.parked)           UNSAFE.unpark(w);
        }
      }
    }
  }
  /** 
 * Increment or decrement quiescerCount. Needed only to prevent
 * triggering shutdown if a worker is transiently inactive while
 * checking quiescence.
 * @param delta 1 for increment, -1 for decrement
 */
  final void addQuiescerCount(  int delta){
    int c;
    do {
    }
 while (!UNSAFE.compareAndSwapInt(this,quiescerCountOffset,c=quiescerCount,c + delta));
  }
  /** 
 * Directly increment or decrement active count without
 * queuing. This method is used to transiently assert inactivation
 * while checking quiescence.
 * @param delta 1 for increment, -1 for decrement
 */
  final void addActiveCount(  int delta){
    long d=delta < 0 ? -AC_UNIT : AC_UNIT;
    long c;
    do {
    }
 while (!UNSAFE.compareAndSwapLong(this,ctlOffset,c=ctl,((c + d) & AC_MASK) | (c & ~AC_MASK)));
  }
  /** 
 * Returns the approximate (non-atomic) number of idle threads per
 * active thread.
 */
  final int idlePerActive(){
    int p=parallelism;
    int a=p + (int)(ctl >> AC_SHIFT);
    return (a > (p>>>=1) ? 0 : a > (p>>>=1) ? 1 : a > (p>>>=1) ? 2 : a > (p>>>=1) ? 4 : 8);
  }
  /** 
 * Creates a {@code ForkJoinPool} with parallelism equal to {@link java.lang.Runtime#availableProcessors}, using the {@linkplain #defaultForkJoinWorkerThreadFactory default thread factory},
 * no UncaughtExceptionHandler, and non-async LIFO processing mode.
 * @throws SecurityException if a security manager exists and
 * the caller is not permitted to modify threads
 * because it does not hold {@link java.lang.RuntimePermission}{@code ("modifyThread")}
 */
  public ForkJoinPool(){
    this(Runtime.getRuntime().availableProcessors(),defaultForkJoinWorkerThreadFactory,null,false);
  }
  /** 
 * Creates a {@code ForkJoinPool} with the indicated parallelism
 * level, the {@linkplain #defaultForkJoinWorkerThreadFactory default thread factory},
 * no UncaughtExceptionHandler, and non-async LIFO processing mode.
 * @param parallelism the parallelism level
 * @throws IllegalArgumentException if parallelism less than or
 * equal to zero, or greater than implementation limit
 * @throws SecurityException if a security manager exists and
 * the caller is not permitted to modify threads
 * because it does not hold {@link java.lang.RuntimePermission}{@code ("modifyThread")}
 */
  public ForkJoinPool(  int parallelism){
    this(parallelism,defaultForkJoinWorkerThreadFactory,null,false);
  }
  /** 
 * Creates a {@code ForkJoinPool} with the given parameters.
 * @param parallelism the parallelism level. For default value,
 * use {@link java.lang.Runtime#availableProcessors}.
 * @param factory the factory for creating new threads. For default value,
 * use {@link #defaultForkJoinWorkerThreadFactory}.
 * @param handler the handler for internal worker threads that
 * terminate due to unrecoverable errors encountered while executing
 * tasks. For default value, use {@code null}.
 * @param asyncMode if true,
 * establishes local first-in-first-out scheduling mode for forked
 * tasks that are never joined. This mode may be more appropriate
 * than default locally stack-based mode in applications in which
 * worker threads only process event-style asynchronous tasks.
 * For default value, use {@code false}.
 * @throws IllegalArgumentException if parallelism less than or
 * equal to zero, or greater than implementation limit
 * @throws NullPointerException if the factory is null
 * @throws SecurityException if a security manager exists and
 * the caller is not permitted to modify threads
 * because it does not hold {@link java.lang.RuntimePermission}{@code ("modifyThread")}
 */
  public ForkJoinPool(  int parallelism,  ForkJoinWorkerThreadFactory factory,  Thread.UncaughtExceptionHandler handler,  boolean asyncMode){
    checkPermission();
    if (factory == null)     throw new NullPointerException();
    if (parallelism <= 0 || parallelism > MAX_ID)     throw new IllegalArgumentException();
    this.parallelism=parallelism;
    this.factory=factory;
    this.ueh=handler;
    this.locallyFifo=asyncMode;
    long np=(long)(-parallelism);
    this.ctl=((np << AC_SHIFT) & AC_MASK) | ((np << TC_SHIFT) & TC_MASK);
    this.submissionQueue=new ForkJoinTask<?>[INITIAL_QUEUE_CAPACITY];
    int n=parallelism << 1;
    if (n >= MAX_ID)     n=MAX_ID;
 else {
      n|=n >>> 1;
      n|=n >>> 2;
      n|=n >>> 4;
      n|=n >>> 8;
    }
    workers=new ForkJoinWorkerThread[n + 1];
    this.submissionLock=new ReentrantLock();
    this.termination=submissionLock.newCondition();
    StringBuilder sb=new StringBuilder("ForkJoinPool-");
    sb.append(poolNumberGenerator.incrementAndGet());
    sb.append("-worker-");
    this.workerNamePrefix=sb.toString();
  }
  /** 
 * Performs the given task, returning its result upon completion.
 * If the computation encounters an unchecked Exception or Error,
 * it is rethrown as the outcome of this invocation.  Rethrown
 * exceptions behave in the same way as regular exceptions, but,
 * when possible, contain stack traces (as displayed for example
 * using {@code ex.printStackTrace()}) of both the current thread
 * as well as the thread actually encountering the exception;
 * minimally only the latter.
 * @param task the task
 * @return the task's result
 * @throws NullPointerException if the task is null
 * @throws RejectedExecutionException if the task cannot be
 * scheduled for execution
 */
  public <T>T invoke(  ForkJoinTask<T> task){
    Thread t=Thread.currentThread();
    if (task == null)     throw new NullPointerException();
    if (shutdown)     throw new RejectedExecutionException();
    if ((t instanceof ForkJoinWorkerThread) && ((ForkJoinWorkerThread)t).pool == this)     return task.invoke();
 else {
      addSubmission(task);
      return task.join();
    }
  }
  /** 
 * Unless terminating, forks task if within an ongoing FJ
 * computation in the current pool, else submits as external task.
 */
  private <T>void forkOrSubmit(  ForkJoinTask<T> task){
    ForkJoinWorkerThread w;
    Thread t=Thread.currentThread();
    if (shutdown)     throw new RejectedExecutionException();
    if ((t instanceof ForkJoinWorkerThread) && (w=(ForkJoinWorkerThread)t).pool == this)     w.pushTask(task);
 else     addSubmission(task);
  }
  /** 
 * Arranges for (asynchronous) execution of the given task.
 * @param task the task
 * @throws NullPointerException if the task is null
 * @throws RejectedExecutionException if the task cannot be
 * scheduled for execution
 */
  public void execute(  ForkJoinTask<?> task){
    if (task == null)     throw new NullPointerException();
    forkOrSubmit(task);
  }
  /** 
 * @throws NullPointerException if the task is null
 * @throws RejectedExecutionException if the task cannot be
 * scheduled for execution
 */
  public void execute(  Runnable task){
    if (task == null)     throw new NullPointerException();
    ForkJoinTask<?> job;
    if (task instanceof ForkJoinTask<?>)     job=(ForkJoinTask<?>)task;
 else     job=ForkJoinTask.adapt(task,null);
    forkOrSubmit(job);
  }
  /** 
 * Submits a ForkJoinTask for execution.
 * @param task the task to submit
 * @return the task
 * @throws NullPointerException if the task is null
 * @throws RejectedExecutionException if the task cannot be
 * scheduled for execution
 */
  public <T>ForkJoinTask<T> submit(  ForkJoinTask<T> task){
    if (task == null)     throw new NullPointerException();
    forkOrSubmit(task);
    return task;
  }
  /** 
 * @throws NullPointerException if the task is null
 * @throws RejectedExecutionException if the task cannot be
 * scheduled for execution
 */
  public <T>ForkJoinTask<T> submit(  Callable<T> task){
    if (task == null)     throw new NullPointerException();
    ForkJoinTask<T> job=ForkJoinTask.adapt(task);
    forkOrSubmit(job);
    return job;
  }
  /** 
 * @throws NullPointerException if the task is null
 * @throws RejectedExecutionException if the task cannot be
 * scheduled for execution
 */
  public <T>ForkJoinTask<T> submit(  Runnable task,  T result){
    if (task == null)     throw new NullPointerException();
    ForkJoinTask<T> job=ForkJoinTask.adapt(task,result);
    forkOrSubmit(job);
    return job;
  }
  /** 
 * @throws NullPointerException if the task is null
 * @throws RejectedExecutionException if the task cannot be
 * scheduled for execution
 */
  public ForkJoinTask<?> submit(  Runnable task){
    if (task == null)     throw new NullPointerException();
    ForkJoinTask<?> job;
    if (task instanceof ForkJoinTask<?>)     job=(ForkJoinTask<?>)task;
 else     job=ForkJoinTask.adapt(task,null);
    forkOrSubmit(job);
    return job;
  }
  /** 
 * @throws NullPointerException       {@inheritDoc}
 * @throws RejectedExecutionException {@inheritDoc}
 */
  public <T>List<Future<T>> invokeAll(  Collection<? extends Callable<T>> tasks){
    ArrayList<ForkJoinTask<T>> forkJoinTasks=new ArrayList<ForkJoinTask<T>>(tasks.size());
    for (    Callable<T> task : tasks)     forkJoinTasks.add(ForkJoinTask.adapt(task));
    invoke(new InvokeAll<T>(forkJoinTasks));
    @SuppressWarnings({"unchecked","rawtypes"}) List<Future<T>> futures=(List<Future<T>>)(List)forkJoinTasks;
    return futures;
  }
static final class InvokeAll<T> extends RecursiveAction {
    final ArrayList<ForkJoinTask<T>> tasks;
    InvokeAll(    ArrayList<ForkJoinTask<T>> tasks){
      this.tasks=tasks;
    }
    public void compute(){
      try {
        invokeAll(tasks);
      }
 catch (      Exception ignore) {
      }
    }
    private static final long serialVersionUID=-7914297376763021607L;
  }
  /** 
 * Returns the factory used for constructing new workers.
 * @return the factory used for constructing new workers
 */
  public ForkJoinWorkerThreadFactory getFactory(){
    return factory;
  }
  /** 
 * Returns the handler for internal worker threads that terminate
 * due to unrecoverable errors encountered while executing tasks.
 * @return the handler, or {@code null} if none
 */
  public Thread.UncaughtExceptionHandler getUncaughtExceptionHandler(){
    return ueh;
  }
  /** 
 * Returns the targeted parallelism level of this pool.
 * @return the targeted parallelism level of this pool
 */
  public int getParallelism(){
    return parallelism;
  }
  /** 
 * Returns the number of worker threads that have started but not
 * yet terminated.  The result returned by this method may differ
 * from {@link #getParallelism} when threads are created to
 * maintain parallelism when others are cooperatively blocked.
 * @return the number of worker threads
 */
  public int getPoolSize(){
    return parallelism + (short)(ctl >>> TC_SHIFT);
  }
  /** 
 * Returns {@code true} if this pool uses local first-in-first-out
 * scheduling mode for forked tasks that are never joined.
 * @return {@code true} if this pool uses async mode
 */
  public boolean getAsyncMode(){
    return locallyFifo;
  }
  /** 
 * Returns an estimate of the number of worker threads that are
 * not blocked waiting to join tasks or for other managed
 * synchronization. This method may overestimate the
 * number of running threads.
 * @return the number of worker threads
 */
  public int getRunningThreadCount(){
    int r=parallelism + (int)(ctl >> AC_SHIFT);
    return (r <= 0) ? 0 : r;
  }
  /** 
 * Returns an estimate of the number of threads that are currently
 * stealing or executing tasks. This method may overestimate the
 * number of active threads.
 * @return the number of active threads
 */
  public int getActiveThreadCount(){
    int r=parallelism + (int)(ctl >> AC_SHIFT) + blockedCount;
    return (r <= 0) ? 0 : r;
  }
  /** 
 * Returns {@code true} if all worker threads are currently idle.
 * An idle worker is one that cannot obtain a task to execute
 * because none are available to steal from other threads, and
 * there are no pending submissions to the pool. This method is
 * conservative; it might not return {@code true} immediately upon
 * idleness of all threads, but will eventually become true if
 * threads remain inactive.
 * @return {@code true} if all threads are currently idle
 */
  public boolean isQuiescent(){
    return parallelism + (int)(ctl >> AC_SHIFT) + blockedCount == 0;
  }
  /** 
 * Returns an estimate of the total number of tasks stolen from
 * one thread's work queue by another. The reported value
 * underestimates the actual total number of steals when the pool
 * is not quiescent. This value may be useful for monitoring and
 * tuning fork/join programs: in general, steal counts should be
 * high enough to keep threads busy, but low enough to avoid
 * overhead and contention across threads.
 * @return the number of steals
 */
  public long getStealCount(){
    return stealCount;
  }
  /** 
 * Returns an estimate of the total number of tasks currently held
 * in queues by worker threads (but not including tasks submitted
 * to the pool that have not begun executing). This value is only
 * an approximation, obtained by iterating across all threads in
 * the pool. This method may be useful for tuning task
 * granularities.
 * @return the number of queued tasks
 */
  public long getQueuedTaskCount(){
    long count=0;
    ForkJoinWorkerThread[] ws;
    if ((short)(ctl >>> TC_SHIFT) > -parallelism && (ws=workers) != null) {
      for (      ForkJoinWorkerThread w : ws)       if (w != null)       count-=w.queueBase - w.queueTop;
    }
    return count;
  }
  /** 
 * Returns an estimate of the number of tasks submitted to this
 * pool that have not yet begun executing.  This method may take
 * time proportional to the number of submissions.
 * @return the number of queued submissions
 */
  public int getQueuedSubmissionCount(){
    return -queueBase + queueTop;
  }
  /** 
 * Returns {@code true} if there are any tasks submitted to this
 * pool that have not yet begun executing.
 * @return {@code true} if there are any queued submissions
 */
  public boolean hasQueuedSubmissions(){
    return queueBase != queueTop;
  }
  /** 
 * Removes and returns the next unexecuted submission if one is
 * available.  This method may be useful in extensions to this
 * class that re-assign work in systems with multiple pools.
 * @return the next submission, or {@code null} if none
 */
  protected ForkJoinTask<?> pollSubmission(){
    ForkJoinTask<?> t;
    ForkJoinTask<?>[] q;
    int b, i;
    while ((b=queueBase) != queueTop && (q=submissionQueue) != null && (i=(q.length - 1) & b) >= 0) {
      long u=(i << ASHIFT) + ABASE;
      if ((t=q[i]) != null && queueBase == b && UNSAFE.compareAndSwapObject(q,u,t,null)) {
        queueBase=b + 1;
        return t;
      }
    }
    return null;
  }
  /** 
 * Removes all available unexecuted submitted and forked tasks
 * from scheduling queues and adds them to the given collection,
 * without altering their execution status. These may include
 * artificially generated or wrapped tasks. This method is
 * designed to be invoked only when the pool is known to be
 * quiescent. Invocations at other times may not remove all
 * tasks. A failure encountered while attempting to add elements
 * to collection {@code c} may result in elements being in
 * neither, either or both collections when the associated
 * exception is thrown.  The behavior of this operation is
 * undefined if the specified collection is modified while the
 * operation is in progress.
 * @param c the collection to transfer elements into
 * @return the number of elements transferred
 */
  protected int drainTasksTo(  Collection<? super ForkJoinTask<?>> c){
    int count=0;
    while (queueBase != queueTop) {
      ForkJoinTask<?> t=pollSubmission();
      if (t != null) {
        c.add(t);
        ++count;
      }
    }
    ForkJoinWorkerThread[] ws;
    if ((short)(ctl >>> TC_SHIFT) > -parallelism && (ws=workers) != null) {
      for (      ForkJoinWorkerThread w : ws)       if (w != null)       count+=w.drainTasksTo(c);
    }
    return count;
  }
  /** 
 * Returns a string identifying this pool, as well as its state,
 * including indications of run state, parallelism level, and
 * worker and task counts.
 * @return a string identifying this pool, as well as its state
 */
  public String toString(){
    long st=getStealCount();
    long qt=getQueuedTaskCount();
    long qs=getQueuedSubmissionCount();
    int pc=parallelism;
    long c=ctl;
    int tc=pc + (short)(c >>> TC_SHIFT);
    int rc=pc + (int)(c >> AC_SHIFT);
    if (rc < 0)     rc=0;
    int ac=rc + blockedCount;
    String level;
    if ((c & STOP_BIT) != 0)     level=(tc == 0) ? "Terminated" : "Terminating";
 else     level=shutdown ? "Shutting down" : "Running";
    return super.toString() + "[" + level+ ", parallelism = "+ pc+ ", size = "+ tc+ ", active = "+ ac+ ", running = "+ rc+ ", steals = "+ st+ ", tasks = "+ qt+ ", submissions = "+ qs+ "]";
  }
  /** 
 * Initiates an orderly shutdown in which previously submitted
 * tasks are executed, but no new tasks will be accepted.
 * Invocation has no additional effect if already shut down.
 * Tasks that are in the process of being submitted concurrently
 * during the course of this method may or may not be rejected.
 * @throws SecurityException if a security manager exists and
 * the caller is not permitted to modify threads
 * because it does not hold {@link java.lang.RuntimePermission}{@code ("modifyThread")}
 */
  public void shutdown(){
    checkPermission();
    shutdown=true;
    tryTerminate(false);
  }
  /** 
 * Attempts to cancel and/or stop all tasks, and reject all
 * subsequently submitted tasks.  Tasks that are in the process of
 * being submitted or executed concurrently during the course of
 * this method may or may not be rejected. This method cancels
 * both existing and unexecuted tasks, in order to permit
 * termination in the presence of task dependencies. So the method
 * always returns an empty list (unlike the case for some other
 * Executors).
 * @return an empty list
 * @throws SecurityException if a security manager exists and
 * the caller is not permitted to modify threads
 * because it does not hold {@link java.lang.RuntimePermission}{@code ("modifyThread")}
 */
  public List<Runnable> shutdownNow(){
    checkPermission();
    shutdown=true;
    tryTerminate(true);
    return Collections.emptyList();
  }
  /** 
 * Returns {@code true} if all tasks have completed following shut down.
 * @return {@code true} if all tasks have completed following shut down
 */
  public boolean isTerminated(){
    long c=ctl;
    return ((c & STOP_BIT) != 0L && (short)(c >>> TC_SHIFT) == -parallelism);
  }
  /** 
 * Returns {@code true} if the process of termination has
 * commenced but not yet completed.  This method may be useful for
 * debugging. A return of {@code true} reported a sufficient
 * period after shutdown may indicate that submitted tasks have
 * ignored or suppressed interruption, or are waiting for IO,
 * causing this executor not to properly terminate. (See the
 * advisory notes for class {@link ForkJoinTask} stating that
 * tasks should not normally entail blocking operations.  But if
 * they do, they must abort them on interrupt.)
 * @return {@code true} if terminating but not yet terminated
 */
  public boolean isTerminating(){
    long c=ctl;
    return ((c & STOP_BIT) != 0L && (short)(c >>> TC_SHIFT) != -parallelism);
  }
  /** 
 * Returns true if terminating or terminated. Used by ForkJoinWorkerThread.
 */
  final boolean isAtLeastTerminating(){
    return (ctl & STOP_BIT) != 0L;
  }
  /** 
 * Returns {@code true} if this pool has been shut down.
 * @return {@code true} if this pool has been shut down
 */
  public boolean isShutdown(){
    return shutdown;
  }
  /** 
 * Blocks until all tasks have completed execution after a shutdown
 * request, or the timeout occurs, or the current thread is
 * interrupted, whichever happens first.
 * @param timeout the maximum time to wait
 * @param unit the time unit of the timeout argument
 * @return {@code true} if this executor terminated and{@code false} if the timeout elapsed before termination
 * @throws InterruptedException if interrupted while waiting
 */
  public boolean awaitTermination(  long timeout,  TimeUnit unit) throws InterruptedException {
    long nanos=unit.toNanos(timeout);
    final ReentrantLock lock=this.submissionLock;
    lock.lock();
    try {
      for (; ; ) {
        if (isTerminated())         return true;
        if (nanos <= 0)         return false;
        nanos=termination.awaitNanos(nanos);
      }
    }
  finally {
      lock.unlock();
    }
  }
  /** 
 * Interface for extending managed parallelism for tasks running
 * in {@link ForkJoinPool}s.
 * <p>A {@code ManagedBlocker} provides two methods.  Method{@code isReleasable} must return {@code true} if blocking is
 * not necessary. Method {@code block} blocks the current thread
 * if necessary (perhaps internally invoking {@code isReleasable}before actually blocking). These actions are performed by any
 * thread invoking {@link ForkJoinPool#managedBlock}.  The
 * unusual methods in this API accommodate synchronizers that may,
 * but don't usually, block for long periods. Similarly, they
 * allow more efficient internal handling of cases in which
 * additional workers may be, but usually are not, needed to
 * ensure sufficient parallelism.  Toward this end,
 * implementations of method {@code isReleasable} must be amenable
 * to repeated invocation.
 * <p>For example, here is a ManagedBlocker based on a
 * ReentrantLock:
 * <pre> {@code}class ManagedLocker implements ManagedBlocker 
 * final ReentrantLock lock;
 * boolean hasLock = false;
 * ManagedLocker(ReentrantLock lock) { this.lock = lock; }
 * public boolean block() {
 * if (!hasLock)
 * lock.lock();
 * return true;
 * }
 * public boolean isReleasable() {
 * return hasLock || (hasLock = lock.tryLock());
 * }
 * }}</pre>
 * <p>Here is a class that possibly blocks waiting for an
 * item on a given queue:
 * <pre> {@code}class QueueTaker<E> implements ManagedBlocker 
 * final BlockingQueue<E> queue;
 * volatile E item = null;
 * QueueTaker(BlockingQueue<E> q) { this.queue = q; }
 * public boolean block() throws InterruptedException {
 * if (item == null)
 * item = queue.take();
 * return true;
 * }
 * public boolean isReleasable() {
 * return item != null || (item = queue.poll()) != null;
 * }
 * public E getItem() { // call after pool.managedBlock completes
 * return item;
 * }
 * }}</pre>
 */
public static interface ManagedBlocker {
    /** 
 * Possibly blocks the current thread, for example waiting for
 * a lock or condition.
 * @return {@code true} if no additional blocking is necessary
 * (i.e., if isReleasable would return true)
 * @throws InterruptedException if interrupted while waiting
 * (the method is not required to do so, but is allowed to)
 */
    boolean block() throws InterruptedException ;
    /** 
 * Returns {@code true} if blocking is unnecessary.
 */
    boolean isReleasable();
  }
  /** 
 * Blocks in accord with the given blocker.  If the current thread
 * is a {@link ForkJoinWorkerThread}, this method possibly
 * arranges for a spare thread to be activated if necessary to
 * ensure sufficient parallelism while the current thread is blocked.
 * <p>If the caller is not a {@link ForkJoinTask}, this method is
 * behaviorally equivalent to
 * <pre> {@codewhile (!blocker.isReleasable())
 * if (blocker.block())
 * return;}</pre>
 * If the caller is a {@code ForkJoinTask}, then the pool may
 * first be expanded to ensure parallelism, and later adjusted.
 * @param blocker the blocker
 * @throws InterruptedException if blocker.block did so
 */
  public static void managedBlock(  ManagedBlocker blocker) throws InterruptedException {
    Thread t=Thread.currentThread();
    if (t instanceof ForkJoinWorkerThread) {
      ForkJoinWorkerThread w=(ForkJoinWorkerThread)t;
      w.pool.awaitBlocker(blocker);
    }
 else {
      do {
      }
 while (!blocker.isReleasable() && !blocker.block());
    }
  }
  protected <T>RunnableFuture<T> newTaskFor(  Runnable runnable,  T value){
    return (RunnableFuture<T>)ForkJoinTask.adapt(runnable,value);
  }
  protected <T>RunnableFuture<T> newTaskFor(  Callable<T> callable){
    return (RunnableFuture<T>)ForkJoinTask.adapt(callable);
  }
  private static final sun.misc.Unsafe UNSAFE;
  private static final long ctlOffset;
  private static final long stealCountOffset;
  private static final long blockedCountOffset;
  private static final long quiescerCountOffset;
  private static final long scanGuardOffset;
  private static final long nextWorkerNumberOffset;
  private static final long ABASE;
  private static final int ASHIFT;
static {
    poolNumberGenerator=new AtomicInteger();
    workerSeedGenerator=new Random();
    modifyThreadPermission=new RuntimePermission("modifyThread");
    defaultForkJoinWorkerThreadFactory=new DefaultForkJoinWorkerThreadFactory();
    int s;
    try {
      UNSAFE=sun.misc.Unsafe.getUnsafe();
      Class k=ForkJoinPool.class;
      ctlOffset=UNSAFE.objectFieldOffset(k.getDeclaredField("ctl"));
      stealCountOffset=UNSAFE.objectFieldOffset(k.getDeclaredField("stealCount"));
      blockedCountOffset=UNSAFE.objectFieldOffset(k.getDeclaredField("blockedCount"));
      quiescerCountOffset=UNSAFE.objectFieldOffset(k.getDeclaredField("quiescerCount"));
      scanGuardOffset=UNSAFE.objectFieldOffset(k.getDeclaredField("scanGuard"));
      nextWorkerNumberOffset=UNSAFE.objectFieldOffset(k.getDeclaredField("nextWorkerNumber"));
      Class a=ForkJoinTask[].class;
      ABASE=UNSAFE.arrayBaseOffset(a);
      s=UNSAFE.arrayIndexScale(a);
    }
 catch (    Exception e) {
      throw new Error(e);
    }
    if ((s & (s - 1)) != 0)     throw new Error("data type scale not a power of two");
    ASHIFT=31 - Integer.numberOfLeadingZeros(s);
  }
}
