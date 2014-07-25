package edu.umd.cs.piccolo.event;
import java.util.EventListener;
/** 
 * <b>PInputEventListener</b> defines the most basic interface for objects that
 * want to listen to PNodes for input events. This interface is very simple so
 * that others may extend Piccolo's input management system. If you are just
 * using Piccolo's default input management system then you will most often use
 * PBasicInputEventHandler to register with a node for input events.
 * <P>
 * @see PBasicInputEventHandler
 * @version 1.0
 * @author Jesse Grosjean
 */
public interface PInputEventListener extends EventListener {
  /** 
 * Called whenever an event is emitted. Used to notify listeners that an
 * event is available for proecessing.
 * @param event event that was emitted
 * @param type type of event
 */
  void processEvent(  PInputEvent event,  int type);
}
