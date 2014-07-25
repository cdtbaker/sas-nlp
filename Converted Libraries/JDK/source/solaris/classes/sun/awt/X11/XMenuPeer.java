package sun.awt.X11;
import java.awt.*;
import java.awt.peer.*;
import java.lang.reflect.Field;
import java.util.Vector;
import sun.util.logging.PlatformLogger;
import sun.awt.SunToolkit;
public class XMenuPeer extends XMenuItemPeer implements MenuPeer {
  /** 
 * Data members
 */
  private static PlatformLogger log=PlatformLogger.getLogger("sun.awt.X11.XMenuPeer");
  /** 
 * Window that correspond to this menu
 */
  XMenuWindow menuWindow;
  private final static Field f_items;
static {
    f_items=SunToolkit.getField(Menu.class,"items");
  }
  /** 
 * Construction
 */
  XMenuPeer(  Menu target){
    super(target);
  }
  /** 
 * This function is called when menu is bound
 * to its container window. Creates submenu window
 * that fills its items vector while construction
 */
  void setContainer(  XBaseMenuWindow container){
    super.setContainer(container);
    menuWindow=new XMenuWindow(this);
  }
  /** 
 * Disposes menu window if needed
 */
  public void dispose(){
    if (menuWindow != null) {
      menuWindow.dispose();
    }
    super.dispose();
  }
  /** 
 * Resets text metrics for this item, for its menu window
 * and for all descendant menu windows
 */
  public void setFont(  Font font){
    resetTextMetrics();
    XMenuWindow menuWindow=getMenuWindow();
    if (menuWindow != null) {
      menuWindow.setItemsFont(font);
    }
    repaintIfShowing();
  }
  /** 
 * addSeparator routines are not used
 * in peers. Shared code invokes addItem("-")
 * for adding separators
 */
  public void addSeparator(){
    if (log.isLoggable(PlatformLogger.FINER))     log.finer("addSeparator is not implemented");
  }
  public void addItem(  MenuItem item){
    XMenuWindow menuWindow=getMenuWindow();
    if (menuWindow != null) {
      menuWindow.addItem(item);
    }
 else {
      if (log.isLoggable(PlatformLogger.FINE)) {
        log.fine("Attempt to use XMenuWindowPeer without window");
      }
    }
  }
  public void delItem(  int index){
    XMenuWindow menuWindow=getMenuWindow();
    if (menuWindow != null) {
      menuWindow.delItem(index);
    }
 else {
      if (log.isLoggable(PlatformLogger.FINE)) {
        log.fine("Attempt to use XMenuWindowPeer without window");
      }
    }
  }
  /** 
 * Access to target's fields
 */
  Vector getTargetItems(){
    try {
      return (Vector)f_items.get(getTarget());
    }
 catch (    IllegalAccessException iae) {
      iae.printStackTrace();
      return null;
    }
  }
  /** 
 * Overriden behaviour
 */
  boolean isSeparator(){
    return false;
  }
  String getShortcutText(){
    return null;
  }
  /** 
 * Returns menu window of this menu or null
 * it this menu has no container and so its
 * window can't be created.
 */
  XMenuWindow getMenuWindow(){
    return menuWindow;
  }
}
