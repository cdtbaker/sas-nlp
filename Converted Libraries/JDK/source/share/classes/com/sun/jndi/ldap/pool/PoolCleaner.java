package com.sun.jndi.ldap.pool;
/** 
 * Thread that wakes up periodically and closes expired, unused connections.
 * @author Rosanna Lee
 */
final public class PoolCleaner extends Thread {
  final private Pool[] pools;
  final private long period;
  /** 
 * @param period ms to wait between cleaning
 * @param pools non-null array of Pools to clean
 */
  public PoolCleaner(  long period,  Pool[] pools){
    super();
    this.period=period;
    this.pools=(Pool[])pools.clone();
    setDaemon(true);
  }
  public void run(){
    long threshold;
    while (true) {
synchronized (this) {
        try {
          wait(period);
        }
 catch (        InterruptedException ignore) {
        }
        threshold=System.currentTimeMillis() - period;
        for (int i=0; i < pools.length; i++) {
          if (pools[i] != null) {
            pools[i].expire(threshold);
          }
        }
      }
    }
  }
}
