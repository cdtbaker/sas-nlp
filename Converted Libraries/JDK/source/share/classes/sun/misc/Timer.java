package sun.misc;
/** 
 * A Timer object is used by algorithms that require timed events.
 * For example, in an animation loop, a timer would help in
 * determining when to change frames.
 * A timer has an interval which determines when it "ticks";
 * that is, a timer delays for the specified interval and then
 * it calls the owner's tick() method.
 * Here's an example of creating a timer with a 5 sec interval:
 * <pre>
 * class Main implements Timeable {
 * public void tick(Timer timer) {
 * System.out.println("tick");
 * }
 * public static void main(String args[]) {
 * (new Timer(this, 5000)).cont();
 * }
 * }
 * </pre>
 * A timer can be stopped, continued, or reset at any time.
 * A timer's state is not stopped while it's calling the
 * owner's tick() method.
 * A timer can be regular or irregular.  If in regular mode,
 * a timer ticks at the specified interval, regardless of
 * how long the owner's tick() method takes.  While the timer
 * is running, no ticks are ever discarded.  That means that if
 * the owner's tick() method takes longer than the interval,
 * the ticks that would have occurred are delivered immediately.
 * In irregular mode, a timer starts delaying for exactly
 * the specified interval only after the tick() method returns.
 * Synchronization issues: do not hold the timer's monitor
 * while calling any of the Timer operations below otherwise
 * the Timer class will deadlock.
 * @author     Patrick Chan
 */
public class Timer {
  /** 
 * This is the owner of the timer.  Its tick method is
 * called when the timer ticks.
 */
  public Timeable owner;
  long interval;
  long sleepUntil;
  long remainingTime;
  boolean regular;
  boolean stopped;
  Timer next;
  static TimerThread timerThread=null;
  /** 
 * Creates a timer object that is owned by 'owner' and
 * with the interval 'interval' milliseconds.  The new timer
 * object is stopped and is regular.  getRemainingTime()
 * return 'interval' at this point.  getStopTime() returns
 * the time this object was created.
 * @param owner    owner of the timer object
 * @param interval interval of the timer in milliseconds
 */
  public Timer(  Timeable owner,  long interval){
    this.owner=owner;
    this.interval=interval;
    remainingTime=interval;
    regular=true;
    sleepUntil=System.currentTimeMillis();
    stopped=true;
synchronized (getClass()) {
      if (timerThread == null) {
        timerThread=new TimerThread();
      }
    }
  }
  /** 
 * Returns true if this timer is stopped.
 */
  public synchronized boolean isStopped(){
    return stopped;
  }
  /** 
 * Stops the timer.  The amount of time the timer has already
 * delayed is saved so if the timer is continued, it will only
 * delay for the amount of time remaining.
 * Note that even after stopping a timer, one more tick may
 * still occur.
 * This method is MT-safe; i.e. it is synchronized but for
 * implementation reasons, the synchronized modifier cannot
 * be included in the method declaration.
 */
  public void stop(){
    long now=System.currentTimeMillis();
synchronized (timerThread) {
synchronized (this) {
        if (!stopped) {
          TimerThread.dequeue(this);
          remainingTime=Math.max(0,sleepUntil - now);
          sleepUntil=now;
          stopped=true;
        }
      }
    }
  }
  /** 
 * Continue the timer.  The next tick will come at getRemainingTime()
 * milliseconds later.  If the timer is not stopped, this
 * call will be a no-op.
 * This method is MT-safe; i.e. it is synchronized but for
 * implementation reasons, the synchronized modifier cannot
 * be included in the method declaration.
 */
  public void cont(){
synchronized (timerThread) {
synchronized (this) {
        if (stopped) {
          sleepUntil=Math.max(sleepUntil + 1,System.currentTimeMillis() + remainingTime);
          TimerThread.enqueue(this);
          stopped=false;
        }
      }
    }
  }
  /** 
 * Resets the timer's remaining time to the timer's interval.
 * If the timer's running state is not altered.
 */
  public void reset(){
synchronized (timerThread) {
synchronized (this) {
        setRemainingTime(interval);
      }
    }
  }
  /** 
 * Returns the time at which the timer was last stopped.  The
 * return value is valid only if the timer is stopped.
 */
  public synchronized long getStopTime(){
    return sleepUntil;
  }
  /** 
 * Returns the timer's interval.
 */
  public synchronized long getInterval(){
    return interval;
  }
  /** 
 * Changes the timer's interval.  The new interval setting
 * does not take effect until after the next tick.
 * This method does not alter the remaining time or the
 * running state of the timer.
 * @param interval new interval of the timer in milliseconds
 */
  public synchronized void setInterval(  long interval){
    this.interval=interval;
  }
  /** 
 * Returns the remaining time before the timer's next tick.
 * The return value is valid only if timer is stopped.
 */
  public synchronized long getRemainingTime(){
    return remainingTime;
  }
  /** 
 * Sets the remaining time before the timer's next tick.
 * This method does not alter the timer's running state.
 * This method is MT-safe; i.e. it is synchronized but for
 * implementation reasons, the synchronized modifier cannot
 * be included in the method declaration.
 * @param time new remaining time in milliseconds.
 */
  public void setRemainingTime(  long time){
synchronized (timerThread) {
synchronized (this) {
        if (stopped) {
          remainingTime=time;
        }
 else {
          stop();
          remainingTime=time;
          cont();
        }
      }
    }
  }
  /** 
 * In regular mode, a timer ticks at the specified interval,
 * regardless of how long the owner's tick() method takes.
 * While the timer is running, no ticks are ever discarded.
 * That means that if the owner's tick() method takes longer
 * than the interval, the ticks that would have occurred are
 * delivered immediately.
 * In irregular mode, a timer starts delaying for exactly
 * the specified interval only after the tick() method returns.
 */
  public synchronized void setRegular(  boolean regular){
    this.regular=regular;
  }
  protected Thread getTimerThread(){
    return TimerThread.timerThread;
  }
}
class TimerThread extends Thread {
  public static boolean debug=false;
  static TimerThread timerThread;
  static boolean notified=false;
  protected TimerThread(){
    super("TimerThread");
    timerThread=this;
    start();
  }
  public synchronized void run(){
    while (true) {
      long delay;
      while (timerQueue == null) {
        try {
          wait();
        }
 catch (        InterruptedException ex) {
        }
      }
      notified=false;
      delay=timerQueue.sleepUntil - System.currentTimeMillis();
      if (delay > 0) {
        try {
          wait(delay);
        }
 catch (        InterruptedException ex) {
        }
      }
      if (!notified) {
        Timer timer=timerQueue;
        timerQueue=timerQueue.next;
        TimerTickThread thr=TimerTickThread.call(timer,timer.sleepUntil);
        if (debug) {
          long delta=(System.currentTimeMillis() - timer.sleepUntil);
          System.out.println("tick(" + thr.getName() + ","+ timer.interval+ ","+ delta+ ")");
          if (delta > 250) {
            System.out.println("*** BIG DELAY ***");
          }
        }
      }
    }
  }
  static Timer timerQueue=null;
  static protected void enqueue(  Timer timer){
    Timer prev=null;
    Timer cur=timerQueue;
    if (cur == null || timer.sleepUntil <= cur.sleepUntil) {
      timer.next=timerQueue;
      timerQueue=timer;
      notified=true;
      timerThread.notify();
    }
 else {
      do {
        prev=cur;
        cur=cur.next;
      }
 while (cur != null && timer.sleepUntil > cur.sleepUntil);
      timer.next=cur;
      prev.next=timer;
    }
    if (debug) {
      long now=System.currentTimeMillis();
      System.out.print(Thread.currentThread().getName() + ": enqueue " + timer.interval+ ": ");
      cur=timerQueue;
      while (cur != null) {
        long delta=cur.sleepUntil - now;
        System.out.print(cur.interval + "(" + delta+ ") ");
        cur=cur.next;
      }
      System.out.println();
    }
  }
  static protected boolean dequeue(  Timer timer){
    Timer prev=null;
    Timer cur=timerQueue;
    while (cur != null && cur != timer) {
      prev=cur;
      cur=cur.next;
    }
    if (cur == null) {
      if (debug) {
        System.out.println(Thread.currentThread().getName() + ": dequeue " + timer.interval+ ": no-op");
      }
      return false;
    }
    if (prev == null) {
      timerQueue=timer.next;
      notified=true;
      timerThread.notify();
    }
 else {
      prev.next=timer.next;
    }
    timer.next=null;
    if (debug) {
      long now=System.currentTimeMillis();
      System.out.print(Thread.currentThread().getName() + ": dequeue " + timer.interval+ ": ");
      cur=timerQueue;
      while (cur != null) {
        long delta=cur.sleepUntil - now;
        System.out.print(cur.interval + "(" + delta+ ") ");
        cur=cur.next;
      }
      System.out.println();
    }
    return true;
  }
  protected static void requeue(  Timer timer){
    if (!timer.stopped) {
      long now=System.currentTimeMillis();
      if (timer.regular) {
        timer.sleepUntil+=timer.interval;
      }
 else {
        timer.sleepUntil=now + timer.interval;
      }
      enqueue(timer);
    }
 else     if (debug) {
      System.out.println(Thread.currentThread().getName() + ": requeue " + timer.interval+ ": no-op");
    }
  }
}
class TimerTickThread extends Thread {
  static final int MAX_POOL_SIZE=3;
  static int curPoolSize=0;
  static TimerTickThread pool=null;
  TimerTickThread next=null;
  Timer timer;
  long lastSleepUntil;
  protected static synchronized TimerTickThread call(  Timer timer,  long sleepUntil){
    TimerTickThread thread=pool;
    if (thread == null) {
      thread=new TimerTickThread();
      thread.timer=timer;
      thread.lastSleepUntil=sleepUntil;
      thread.start();
    }
 else {
      pool=pool.next;
      thread.timer=timer;
      thread.lastSleepUntil=sleepUntil;
synchronized (thread) {
        thread.notify();
      }
    }
    return thread;
  }
  private boolean returnToPool(){
synchronized (getClass()) {
      if (curPoolSize >= MAX_POOL_SIZE) {
        return false;
      }
      next=pool;
      pool=this;
      curPoolSize++;
      timer=null;
    }
    while (timer == null) {
synchronized (this) {
        try {
          wait();
        }
 catch (        InterruptedException ex) {
        }
      }
    }
synchronized (getClass()) {
      curPoolSize--;
    }
    return true;
  }
  public void run(){
    do {
      timer.owner.tick(timer);
synchronized (TimerThread.timerThread) {
synchronized (timer) {
          if (lastSleepUntil == timer.sleepUntil) {
            TimerThread.requeue(timer);
          }
        }
      }
    }
 while (returnToPool());
  }
}
