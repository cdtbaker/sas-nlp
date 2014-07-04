package org.ojalgo.type;
import java.util.Timer;
import java.util.TimerTask;
public abstract class TypeCache<T> {
  private transient volatile T myCachedObject;
  private volatile boolean myDirty;
  private static final Timer TIMER=new Timer("TypeCache-Daemon",true);
  public TypeCache(  final long aPurgeIntervalMeassure,  final CalendarDateUnit aPurgeIntervalUnit){
    super();
    TIMER.schedule(new TimerTask(){
      @Override public void run(){
        if (TypeCache.this.isDirty()) {
          TypeCache.this.flushCache();
        }
 else {
          TypeCache.this.makeDirty();
        }
      }
    }
,0L,aPurgeIntervalMeassure * aPurgeIntervalUnit.size());
  }
  @SuppressWarnings("unused") private TypeCache(){
    this(8L,CalendarDateUnit.HOUR);
  }
  public synchronized final void flushCache(){
    myCachedObject=null;
  }
  public synchronized final T getCachedObject(){
    if ((myCachedObject == null) || myDirty) {
      myCachedObject=this.recreateCache();
      myDirty=false;
    }
    return myCachedObject;
  }
  public synchronized final boolean isCacheSet(){
    return myCachedObject != null;
  }
  public synchronized final boolean isDirty(){
    return myDirty;
  }
  public synchronized final void makeDirty(){
    myDirty=true;
  }
  protected abstract T recreateCache();
}
