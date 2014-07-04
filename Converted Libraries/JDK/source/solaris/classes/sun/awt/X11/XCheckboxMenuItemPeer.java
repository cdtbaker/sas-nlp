package sun.awt.X11;
import java.awt.*;
import java.awt.peer.*;
import java.awt.event.*;
import java.lang.reflect.Field;
import sun.awt.SunToolkit;
class XCheckboxMenuItemPeer extends XMenuItemPeer implements CheckboxMenuItemPeer {
  /** 
 * Data members
 */
  private final static Field f_state;
static {
    f_state=SunToolkit.getField(CheckboxMenuItem.class,"state");
  }
  /** 
 * Construction
 */
  XCheckboxMenuItemPeer(  CheckboxMenuItem target){
    super(target);
  }
  /** 
 * Implementaion of interface methods
 */
  public void setState(  boolean t){
    repaintIfShowing();
  }
  /** 
 * Access to target's fields
 */
  boolean getTargetState(){
    MenuItem target=getTarget();
    if (target == null) {
      return false;
    }
    try {
      return f_state.getBoolean(target);
    }
 catch (    IllegalAccessException e) {
      e.printStackTrace();
    }
    return false;
  }
  /** 
 * Toggles state and generates ItemEvent
 */
  void action(  final long when){
    XToolkit.executeOnEventHandlerThread((CheckboxMenuItem)getTarget(),new Runnable(){
      public void run(){
        doToggleState(when);
      }
    }
);
  }
  /** 
 * Private
 */
  private void doToggleState(  long when){
    CheckboxMenuItem cb=(CheckboxMenuItem)getTarget();
    boolean newState=!getTargetState();
    cb.setState(newState);
    ItemEvent e=new ItemEvent(cb,ItemEvent.ITEM_STATE_CHANGED,getTargetLabel(),getTargetState() ? ItemEvent.SELECTED : ItemEvent.DESELECTED);
    XWindow.postEventStatic(e);
  }
}
