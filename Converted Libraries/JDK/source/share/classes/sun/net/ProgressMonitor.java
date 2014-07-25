package sun.net;
import java.util.ArrayList;
import java.util.Iterator;
import java.net.URL;
/** 
 * ProgressMonitor is a class for monitoring progress in network input stream.
 * @author Stanley Man-Kit Ho
 */
public class ProgressMonitor {
  /** 
 * Return default ProgressMonitor.
 */
  public static synchronized ProgressMonitor getDefault(){
    return pm;
  }
  /** 
 * Change default ProgressMonitor implementation.
 */
  public static synchronized void setDefault(  ProgressMonitor m){
    if (m != null)     pm=m;
  }
  /** 
 * Change progress metering policy.
 */
  public static synchronized void setMeteringPolicy(  ProgressMeteringPolicy policy){
    if (policy != null)     meteringPolicy=policy;
  }
  /** 
 * Return a snapshot of the ProgressSource list
 */
  public ArrayList<ProgressSource> getProgressSources(){
    ArrayList<ProgressSource> snapshot=new ArrayList<ProgressSource>();
    try {
synchronized (progressSourceList) {
        for (Iterator<ProgressSource> iter=progressSourceList.iterator(); iter.hasNext(); ) {
          ProgressSource pi=iter.next();
          snapshot.add((ProgressSource)pi.clone());
        }
      }
    }
 catch (    CloneNotSupportedException e) {
      e.printStackTrace();
    }
    return snapshot;
  }
  /** 
 * Return update notification threshold
 */
  public synchronized int getProgressUpdateThreshold(){
    return meteringPolicy.getProgressUpdateThreshold();
  }
  /** 
 * Return true if metering should be turned on
 * for a particular URL input stream.
 */
  public boolean shouldMeterInput(  URL url,  String method){
    return meteringPolicy.shouldMeterInput(url,method);
  }
  /** 
 * Register progress source when progress is began.
 */
  public void registerSource(  ProgressSource pi){
synchronized (progressSourceList) {
      if (progressSourceList.contains(pi))       return;
      progressSourceList.add(pi);
    }
    if (progressListenerList.size() > 0) {
      ArrayList<ProgressListener> listeners=new ArrayList<ProgressListener>();
synchronized (progressListenerList) {
        for (Iterator<ProgressListener> iter=progressListenerList.iterator(); iter.hasNext(); ) {
          listeners.add(iter.next());
        }
      }
      for (Iterator<ProgressListener> iter=listeners.iterator(); iter.hasNext(); ) {
        ProgressListener pl=iter.next();
        ProgressEvent pe=new ProgressEvent(pi,pi.getURL(),pi.getMethod(),pi.getContentType(),pi.getState(),pi.getProgress(),pi.getExpected());
        pl.progressStart(pe);
      }
    }
  }
  /** 
 * Unregister progress source when progress is finished.
 */
  public void unregisterSource(  ProgressSource pi){
synchronized (progressSourceList) {
      if (progressSourceList.contains(pi) == false)       return;
      pi.close();
      progressSourceList.remove(pi);
    }
    if (progressListenerList.size() > 0) {
      ArrayList<ProgressListener> listeners=new ArrayList<ProgressListener>();
synchronized (progressListenerList) {
        for (Iterator<ProgressListener> iter=progressListenerList.iterator(); iter.hasNext(); ) {
          listeners.add(iter.next());
        }
      }
      for (Iterator<ProgressListener> iter=listeners.iterator(); iter.hasNext(); ) {
        ProgressListener pl=iter.next();
        ProgressEvent pe=new ProgressEvent(pi,pi.getURL(),pi.getMethod(),pi.getContentType(),pi.getState(),pi.getProgress(),pi.getExpected());
        pl.progressFinish(pe);
      }
    }
  }
  /** 
 * Progress source is updated.
 */
  public void updateProgress(  ProgressSource pi){
synchronized (progressSourceList) {
      if (progressSourceList.contains(pi) == false)       return;
    }
    if (progressListenerList.size() > 0) {
      ArrayList<ProgressListener> listeners=new ArrayList<ProgressListener>();
synchronized (progressListenerList) {
        for (Iterator<ProgressListener> iter=progressListenerList.iterator(); iter.hasNext(); ) {
          listeners.add(iter.next());
        }
      }
      for (Iterator<ProgressListener> iter=listeners.iterator(); iter.hasNext(); ) {
        ProgressListener pl=iter.next();
        ProgressEvent pe=new ProgressEvent(pi,pi.getURL(),pi.getMethod(),pi.getContentType(),pi.getState(),pi.getProgress(),pi.getExpected());
        pl.progressUpdate(pe);
      }
    }
  }
  /** 
 * Add progress listener in progress monitor.
 */
  public void addProgressListener(  ProgressListener l){
synchronized (progressListenerList) {
      progressListenerList.add(l);
    }
  }
  /** 
 * Remove progress listener from progress monitor.
 */
  public void removeProgressListener(  ProgressListener l){
synchronized (progressListenerList) {
      progressListenerList.remove(l);
    }
  }
  private static ProgressMeteringPolicy meteringPolicy=new DefaultProgressMeteringPolicy();
  private static ProgressMonitor pm=new ProgressMonitor();
  private ArrayList<ProgressSource> progressSourceList=new ArrayList<ProgressSource>();
  private ArrayList<ProgressListener> progressListenerList=new ArrayList<ProgressListener>();
}
/** 
 * Default progress metering policy.
 */
class DefaultProgressMeteringPolicy implements ProgressMeteringPolicy {
  /** 
 * Return true if metering should be turned on for a particular network input stream.
 */
  public boolean shouldMeterInput(  URL url,  String method){
    return false;
  }
  /** 
 * Return update notification threshold.
 */
  public int getProgressUpdateThreshold(){
    return 8192;
  }
}
