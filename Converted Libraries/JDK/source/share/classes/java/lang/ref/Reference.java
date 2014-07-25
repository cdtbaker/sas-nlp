package java.lang.ref;
import sun.misc.Cleaner;
/** 
 * Abstract base class for reference objects.  This class defines the
 * operations common to all reference objects.  Because reference objects are
 * implemented in close cooperation with the garbage collector, this class may
 * not be subclassed directly.
 * @author   Mark Reinhold
 * @since    1.2
 */
public abstract class Reference<T> {
  private T referent;
  ReferenceQueue<? super T> queue;
  Reference next;
  transient private Reference<T> discovered;
static private class Lock {
  }
  private static Lock lock=new Lock();
  private static Reference pending=null;
private static class ReferenceHandler extends Thread {
    ReferenceHandler(    ThreadGroup g,    String name){
      super(g,name);
    }
    public void run(){
      for (; ; ) {
        Reference r;
synchronized (lock) {
          if (pending != null) {
            r=pending;
            Reference rn=r.next;
            pending=(rn == r) ? null : rn;
            r.next=r;
          }
 else {
            try {
              lock.wait();
            }
 catch (            InterruptedException x) {
            }
            continue;
          }
        }
        if (r instanceof Cleaner) {
          ((Cleaner)r).clean();
          continue;
        }
        ReferenceQueue q=r.queue;
        if (q != ReferenceQueue.NULL)         q.enqueue(r);
      }
    }
  }
static {
    ThreadGroup tg=Thread.currentThread().getThreadGroup();
    for (ThreadGroup tgn=tg; tgn != null; tg=tgn, tgn=tg.getParent())     ;
    Thread handler=new ReferenceHandler(tg,"Reference Handler");
    handler.setPriority(Thread.MAX_PRIORITY);
    handler.setDaemon(true);
    handler.start();
  }
  /** 
 * Returns this reference object's referent.  If this reference object has
 * been cleared, either by the program or by the garbage collector, then
 * this method returns <code>null</code>.
 * @return   The object to which this reference refers, or
 * <code>null</code> if this reference object has been cleared
 */
  public T get(){
    return this.referent;
  }
  /** 
 * Clears this reference object.  Invoking this method will not cause this
 * object to be enqueued.
 * <p> This method is invoked only by Java code; when the garbage collector
 * clears references it does so directly, without invoking this method.
 */
  public void clear(){
    this.referent=null;
  }
  /** 
 * Tells whether or not this reference object has been enqueued, either by
 * the program or by the garbage collector.  If this reference object was
 * not registered with a queue when it was created, then this method will
 * always return <code>false</code>.
 * @return   <code>true</code> if and only if this reference object has
 * been enqueued
 */
  public boolean isEnqueued(){
synchronized (this) {
      return (this.queue != ReferenceQueue.NULL) && (this.next != null);
    }
  }
  /** 
 * Adds this reference object to the queue with which it is registered,
 * if any.
 * <p> This method is invoked only by Java code; when the garbage collector
 * enqueues references it does so directly, without invoking this method.
 * @return   <code>true</code> if this reference object was successfully
 * enqueued; <code>false</code> if it was already enqueued or if
 * it was not registered with a queue when it was created
 */
  public boolean enqueue(){
    return this.queue.enqueue(this);
  }
  Reference(  T referent){
    this(referent,null);
  }
  Reference(  T referent,  ReferenceQueue<? super T> queue){
    this.referent=referent;
    this.queue=(queue == null) ? ReferenceQueue.NULL : queue;
  }
}
