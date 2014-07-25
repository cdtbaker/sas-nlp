package sun.awt.X11;
import java.awt.Component;
/** 
 * Interface for notifying a client that a scrollbar has changed.
 * @see java.awt.event.AdjustmentEvent
 */
interface XScrollbarClient {
  public void notifyValue(  XScrollbar obj,  int type,  int value,  boolean isAdjusting);
  public Component getEventSource();
  public void repaintScrollbarRequest(  XScrollbar scrollBar);
}
