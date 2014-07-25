package sun.misc;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.SortedSet;
import java.util.TreeSet;
/** 
 * Support for garbage-collection latency requests.
 * @author   Mark Reinhold
 * @since    1.2
 */
public class GC {
  private GC(){
  }
  private static final long NO_TARGET=Long.MAX_VALUE;
  private static long latencyTarget=NO_TARGET;
  private static Thread daemon=null;
private static class LatencyLock extends Object {
  }
  private static Object lock=new LatencyLock();
  /** 
 * Returns the maximum <em>object-inspection age</em>, which is the number
 * of real-time milliseconds that have elapsed since the
 * least-recently-inspected heap object was last inspected by the garbage
 * collector.
 * <p> For simple stop-the-world collectors this value is just the time
 * since the most recent collection.  For generational collectors it is the
 * time since the oldest generation was most recently collected.  Other
 * collectors are free to return a pessimistic estimate of the elapsed
 * time, or simply the time since the last full collection was performed.
 * <p> Note that in the presence of reference objects, a given object that
 * is no longer strongly reachable may have to be inspected multiple times
 * before it can be reclaimed.
 */
  public static native long maxObjectInspectionAge();
private static class Daemon extends Thread {
    public void run(){
      for (; ; ) {
        long l;
synchronized (lock) {
          l=latencyTarget;
          if (l == NO_TARGET) {
            GC.daemon=null;
            return;
          }
          long d=maxObjectInspectionAge();
          if (d >= l) {
            System.gc();
            d=0;
          }
          try {
            lock.wait(l - d);
          }
 catch (          InterruptedException x) {
            continue;
          }
        }
      }
    }
    private Daemon(    ThreadGroup tg){
      super(tg,"GC Daemon");
    }
    public static void create(){
      PrivilegedAction<Void> pa=new PrivilegedAction<Void>(){
        public Void run(){
          ThreadGroup tg=Thread.currentThread().getThreadGroup();
          for (ThreadGroup tgn=tg; tgn != null; tg=tgn, tgn=tg.getParent())           ;
          Daemon d=new Daemon(tg);
          d.setDaemon(true);
          d.setPriority(Thread.MIN_PRIORITY + 1);
          d.start();
          GC.daemon=d;
          return null;
        }
      }
;
      AccessController.doPrivileged(pa);
    }
  }
  private static void setLatencyTarget(  long ms){
    latencyTarget=ms;
    if (daemon == null) {
      Daemon.create();
    }
 else {
      lock.notify();
    }
  }
  /** 
 * Represents an active garbage-collection latency request.  Instances of
 * this class are created by the <code>{@link #requestLatency}</code>
 * method.  Given a request, the only interesting operation is that of
 * cancellation.
 */
public static class LatencyRequest implements Comparable<LatencyRequest> {
    private static long counter=0;
    private static SortedSet<LatencyRequest> requests=null;
    private static void adjustLatencyIfNeeded(){
      if ((requests == null) || requests.isEmpty()) {
        if (latencyTarget != NO_TARGET) {
          setLatencyTarget(NO_TARGET);
        }
      }
 else {
        LatencyRequest r=requests.first();
        if (r.latency != latencyTarget) {
          setLatencyTarget(r.latency);
        }
      }
    }
    private long latency;
    private long id;
    private LatencyRequest(    long ms){
      if (ms <= 0) {
        throw new IllegalArgumentException("Non-positive latency: " + ms);
      }
      this.latency=ms;
synchronized (lock) {
        this.id=++counter;
        if (requests == null) {
          requests=new TreeSet<LatencyRequest>();
        }
        requests.add(this);
        adjustLatencyIfNeeded();
      }
    }
    /** 
 * Cancels this latency request.
 * @throws IllegalStateExceptionIf this request has already been cancelled
 */
    public void cancel(){
synchronized (lock) {
        if (this.latency == NO_TARGET) {
          throw new IllegalStateException("Request already" + " cancelled");
        }
        if (!requests.remove(this)) {
          throw new InternalError("Latency request " + this + " not found");
        }
        if (requests.isEmpty())         requests=null;
        this.latency=NO_TARGET;
        adjustLatencyIfNeeded();
      }
    }
    public int compareTo(    LatencyRequest r){
      long d=this.latency - r.latency;
      if (d == 0)       d=this.id - r.id;
      return (d < 0) ? -1 : ((d > 0) ? +1 : 0);
    }
    public String toString(){
      return (LatencyRequest.class.getName() + "[" + latency+ ","+ id+ "]");
    }
  }
  /** 
 * Makes a new request for a garbage-collection latency of the given
 * number of real-time milliseconds.  A low-priority daemon thread makes a
 * best effort to ensure that the maximum object-inspection age never
 * exceeds the smallest of the currently active requests.
 * @param latencyThe requested latency
 * @throws IllegalArgumentExceptionIf the given <code>latency</code> is non-positive
 */
  public static LatencyRequest requestLatency(  long latency){
    return new LatencyRequest(latency);
  }
  /** 
 * Returns the current smallest garbage-collection latency request, or zero
 * if there are no active requests.
 */
  public static long currentLatencyTarget(){
    long t=latencyTarget;
    return (t == NO_TARGET) ? 0 : t;
  }
}
