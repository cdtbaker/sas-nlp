package edu.umd.cs.piccolox.swt;
import org.eclipse.swt.widgets.Display;
/** 
 * The SWTTimerQueue is a queue of timers. It has been implemented as a linked
 * list of SWTTimer objects.
 * @author Lance Good
 */
public class SWTTimerQueue implements Runnable {
  private static SWTTimerQueue instance;
  private final Display display;
  private SWTTimer firstTimer;
  private boolean running;
  /** 
 * Creates a timer queue that will be attached the the provided display.
 * It's Timers are expected to modify only this display, or none.
 * @param display the display that will get updated by this queue's timers.
 */
  public SWTTimerQueue(  final Display display){
    this.display=display;
    start();
  }
  /** 
 * Returns the singleton instance of the SWTTimerQueue. Take note that even
 * when called with different displays it will always return the same result
 * as the first call.
 * @param display display to associate with this Timer Queue's Activities
 * @return singleton instance of SWTTimerQueue
 */
  public static SWTTimerQueue sharedInstance(  final Display display){
    if (instance == null) {
      instance=new SWTTimerQueue(display);
    }
    return instance;
  }
  /** 
 * Starts the timer queue. If it is already running, a RuntimeException will
 * be thrown.
 */
  synchronized void start(){
    if (running) {
      throw new RuntimeException("Can't start a TimerQueue that is already running");
    }
    Display.getDefault().asyncExec(new Runnable(){
      public void run(){
        final Thread timerThread=new Thread(SWTTimerQueue.this,"TimerQueue");
        timerThread.setDaemon(true);
        timerThread.setPriority(Thread.NORM_PRIORITY);
        timerThread.start();
      }
    }
);
    running=true;
  }
  /** 
 * Stops the TimerQueue Thread.
 */
  synchronized void stop(){
    running=false;
    notifyAll();
  }
  /** 
 * Adds the provided timer to the queue of scheduled timers.
 * @param timer timer to add
 * @param expirationTime time at which the timer is to be stopped and
 * removed from the queue. Given in unix time.
 */
  synchronized void addTimer(  final SWTTimer timer,  final long expirationTime){
    if (!timer.isRunning()) {
      insertTimer(timer,expirationTime);
      timer.setExpirationTime(expirationTime);
      timer.setRunning(true);
      notifyAll();
    }
  }
  /** 
 * Insert the Timer into the queue in the order they will expire. If
 * multiple timers are set to expire at the same time, it will insert it
 * after the last one; that way they expire in the order they came in.
 * @param timer timer to insert into the queue
 * @param expirationTime time in UNIX time at which the new timer should
 * expire
 */
  private void insertTimer(  final SWTTimer timer,  final long expirationTime){
    SWTTimer previousTimer=findLastTimerExpiringBefore(expirationTime);
    if (previousTimer == null) {
      firstTimer=timer;
    }
 else {
      timer.setNextTimer(previousTimer.getNextTimer());
      previousTimer.setNextTimer(timer);
    }
  }
  /** 
 * Finds the last timer that will expire before or at the given expiration
 * time. If there are multiple timers expiring at the same time, the last
 * one in the queue will be returned.
 * @param expirationTime expiration to compare against timers in the queue
 * @return last timer that will expire before or at the given expiration
 * time
 */
  private SWTTimer findLastTimerExpiringBefore(  final long expirationTime){
    SWTTimer previousTimer=null;
    SWTTimer nextTimer=firstTimer;
    while (nextTimer != null && nextTimer.getExpirationTime() > expirationTime) {
      previousTimer=nextTimer;
      nextTimer=nextTimer.getNextTimer();
    }
    return previousTimer;
  }
  /** 
 * Removes the provided timer from the Timer Queue. If it is not found, then
 * nothing happens.
 * @param timer timer to remove from the queue
 */
  synchronized void removeTimer(  final SWTTimer timer){
    if (!timer.isRunning()) {
      return;
    }
    if (timer == firstTimer) {
      firstTimer=timer.getNextTimer();
    }
 else {
      SWTTimer previousTimer=findLastTimerBefore(timer);
      if (previousTimer != null) {
        previousTimer.setNextTimer(timer.getNextTimer());
      }
    }
    timer.setExpirationTime(0);
    timer.setNextTimer(null);
    timer.setRunning(false);
  }
  /** 
 * Finds the timer that immediately precedes the provided timer in the
 * queue.
 * @param timer to search for
 * @return timer immediately preceding found timer, or null if not found
 */
  private SWTTimer findLastTimerBefore(  final SWTTimer timer){
    SWTTimer previousTimer=null;
    SWTTimer currentTimer=firstTimer;
    while (currentTimer != null) {
      if (currentTimer == timer) {
        return previousTimer;
      }
      previousTimer=currentTimer;
      currentTimer=currentTimer.getNextTimer();
    }
    return null;
  }
  /** 
 * Returns true if this timer queue contains the given timer.
 * @param timer timer being checked
 * @return true if timer is scheduled in this queue
 */
  synchronized boolean containsTimer(  final SWTTimer timer){
    return timer.running;
  }
  /** 
 * If there are a ton of timers, this method may never return. It loops
 * checking to see if the head of the Timer list has expired. If it has, it
 * posts the Timer and reschedules it if necessary.
 * @return how long the app can take before it should invoke this method
 * again.
 */
  private synchronized long postExpiredTimers(){
    SWTTimer timer;
    long currentTime;
    long timeToWait;
    do {
      timer=firstTimer;
      if (timer == null) {
        return 0;
      }
      currentTime=System.currentTimeMillis();
      timeToWait=timer.getExpirationTime() - currentTime;
      if (timeToWait <= 0) {
        try {
          timer.postOverride();
        }
 catch (        final SecurityException e) {
          throw new RuntimeException("Could not post event",e);
        }
        removeTimer(timer);
        if (timer.isRepeats()) {
          addTimer(timer,currentTime + timer.getDelay());
        }
        try {
          wait(1);
        }
 catch (        final InterruptedException e) {
        }
      }
    }
 while (timeToWait <= 0);
    return timeToWait;
  }
  /** 
 * Dispatches work to timers until the queue is told to stop running.
 */
  public synchronized void run(){
    long timeToWait;
    try {
      while (running) {
        timeToWait=postExpiredTimers();
        try {
          wait(timeToWait);
        }
 catch (        final InterruptedException e) {
        }
      }
    }
 catch (    final ThreadDeath td) {
      running=false;
      SWTTimer timer=firstTimer;
      while (timer != null) {
        timer.cancelEventOverride();
        timer=timer.getNextTimer();
      }
      display.asyncExec(new SWTTimerQueueRestart(display));
      throw td;
    }
  }
  /** 
 * Generates a string handy for debugging the contents of the timer queue.
 * @return String representation of the queue for use in debugging
 */
  public synchronized String toString(){
    StringBuffer buf;
    SWTTimer nextTimer;
    buf=new StringBuffer();
    buf.append("TimerQueue (");
    nextTimer=firstTimer;
    while (nextTimer != null) {
      buf.append(nextTimer.toString());
      nextTimer=nextTimer.getNextTimer();
      if (nextTimer != null) {
        buf.append(", ");
      }
    }
    buf.append(")");
    return buf.toString();
  }
  /** 
 * Runnable that will message the shared instance of the Timer Queue to
 * restart.
 */
protected static class SWTTimerQueueRestart implements Runnable {
    /** 
 * Tracks whether a restart has been attempted. 
 */
    private boolean attemptedStart;
    private final Display display;
    /** 
 * Constructs a QueueRestart Runnable that will message the Timer Queue
 * to Restart.
 * @param display display associated with the SWTTimerQueue
 */
    public SWTTimerQueueRestart(    final Display display){
      this.display=display;
    }
    /** 
 * Attempts to restart the queue associated with the display.
 */
    public synchronized void run(){
      if (attemptedStart) {
        return;
      }
      final SWTTimerQueue q=SWTTimerQueue.sharedInstance(display);
synchronized (q) {
        if (!q.running) {
          q.start();
        }
      }
      attemptedStart=true;
    }
  }
}
