package sun.awt;
import java.awt.IllegalComponentStateException;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.WeakHashMap;
import sun.util.logging.PlatformLogger;
/** 
 * This class is used to aid in keeping track of DisplayChangedListeners and
 * notifying them when a display change has taken place. DisplayChangedListeners
 * are notified when the display's bit depth is changed, or when a top-level
 * window has been dragged onto another screen.
 * It is safe for a DisplayChangedListener to be added while the list is being
 * iterated.
 * The displayChanged() call is propagated after some occurrence (either
 * due to user action or some other application) causes the display mode
 * (e.g., depth or resolution) to change.  All heavyweight components need
 * to know when this happens because they need to create new surfaceData
 * objects based on the new depth.
 * displayChanged() is also called on Windows when they are moved from one
 * screen to another on a system equipped with multiple displays.
 */
public class SunDisplayChanger {
  private static final PlatformLogger log=PlatformLogger.getLogger("sun.awt.multiscreen.SunDisplayChanger");
  private Map listeners=Collections.synchronizedMap(new WeakHashMap(1));
  public SunDisplayChanger(){
  }
  public void add(  DisplayChangedListener theListener){
    if (log.isLoggable(PlatformLogger.FINE)) {
      if (theListener == null) {
        log.fine("Assertion (theListener != null) failed");
      }
    }
    if (log.isLoggable(PlatformLogger.FINER)) {
      log.finer("Adding listener: " + theListener);
    }
    listeners.put(theListener,null);
  }
  public void remove(  DisplayChangedListener theListener){
    if (log.isLoggable(PlatformLogger.FINE)) {
      if (theListener == null) {
        log.fine("Assertion (theListener != null) failed");
      }
    }
    if (log.isLoggable(PlatformLogger.FINER)) {
      log.finer("Removing listener: " + theListener);
    }
    listeners.remove(theListener);
  }
  public void notifyListeners(){
    if (log.isLoggable(PlatformLogger.FINEST)) {
      log.finest("notifyListeners");
    }
    HashMap listClone;
    Set cloneSet;
synchronized (listeners) {
      listClone=new HashMap(listeners);
    }
    cloneSet=listClone.keySet();
    Iterator itr=cloneSet.iterator();
    while (itr.hasNext()) {
      DisplayChangedListener current=(DisplayChangedListener)itr.next();
      try {
        if (log.isLoggable(PlatformLogger.FINEST)) {
          log.finest("displayChanged for listener: " + current);
        }
        current.displayChanged();
      }
 catch (      IllegalComponentStateException e) {
        listeners.remove(current);
      }
    }
  }
  public void notifyPaletteChanged(){
    if (log.isLoggable(PlatformLogger.FINEST)) {
      log.finest("notifyPaletteChanged");
    }
    HashMap listClone;
    Set cloneSet;
synchronized (listeners) {
      listClone=new HashMap(listeners);
    }
    cloneSet=listClone.keySet();
    Iterator itr=cloneSet.iterator();
    while (itr.hasNext()) {
      DisplayChangedListener current=(DisplayChangedListener)itr.next();
      try {
        if (log.isLoggable(PlatformLogger.FINEST)) {
          log.finest("paletteChanged for listener: " + current);
        }
        current.paletteChanged();
      }
 catch (      IllegalComponentStateException e) {
        listeners.remove(current);
      }
    }
  }
}
