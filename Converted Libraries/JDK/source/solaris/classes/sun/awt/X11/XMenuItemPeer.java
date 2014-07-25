package sun.awt.X11;
import java.awt.*;
import java.awt.peer.*;
import java.awt.event.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import sun.awt.SunToolkit;
public class XMenuItemPeer implements MenuItemPeer {
  /** 
 * Window that this item belongs to.
 */
  private XBaseMenuWindow container;
  /** 
 * Target MenuItem. Note that 'target' member
 * in XWindow is required for dispatching events.
 * This member is only used for accessing its fields
 * and firing ActionEvent & ItemEvent
 */
  private MenuItem target;
  /** 
 * Rectange occupied by menu item in container's
 * coordinates. Filled by map(...) function from
 * XBaseMenuWindow.map()
 */
  private Rectangle bounds;
  /** 
 * Point in container's coordinate system used as
 * origin by drawText.
 */
  private Point textOrigin;
  private final static int SEPARATOR_WIDTH=20;
  private final static int SEPARATOR_HEIGHT=5;
  private final static Field f_enabled;
  private final static Field f_label;
  private final static Field f_shortcut;
  private final static Method m_getFont;
  private final static Method m_isItemEnabled;
  private final static Method m_getActionCommand;
static {
    f_enabled=SunToolkit.getField(MenuItem.class,"enabled");
    f_label=SunToolkit.getField(MenuItem.class,"label");
    f_shortcut=SunToolkit.getField(MenuItem.class,"shortcut");
    m_getFont=SunToolkit.getMethod(MenuComponent.class,"getFont_NoClientCode",null);
    m_getActionCommand=SunToolkit.getMethod(MenuItem.class,"getActionCommandImpl",null);
    m_isItemEnabled=SunToolkit.getMethod(MenuItem.class,"isItemEnabled",null);
  }
  /** 
 * Text metrics are filled in calcTextMetrics function
 * and reset in resetTextMetrics function. Text metrics
 * contain calculated dimensions of various components of
 * menu item.
 */
  private TextMetrics textMetrics;
static class TextMetrics implements Cloneable {
    private Dimension textDimension;
    private int shortcutWidth;
    private int textBaseline;
    TextMetrics(    Dimension textDimension,    int shortcutWidth,    int textBaseline){
      this.textDimension=textDimension;
      this.shortcutWidth=shortcutWidth;
      this.textBaseline=textBaseline;
    }
    public Object clone(){
      try {
        return super.clone();
      }
 catch (      CloneNotSupportedException ex) {
        throw new InternalError();
      }
    }
    Dimension getTextDimension(){
      return this.textDimension;
    }
    int getShortcutWidth(){
      return this.shortcutWidth;
    }
    int getTextBaseline(){
      return this.textBaseline;
    }
  }
  /** 
 * Construction
 */
  XMenuItemPeer(  MenuItem target){
    this.target=target;
  }
  /** 
 * Implementaion of interface methods
 */
  public void dispose(){
  }
  public void setFont(  Font font){
    resetTextMetrics();
    repaintIfShowing();
  }
  public void setLabel(  String label){
    resetTextMetrics();
    repaintIfShowing();
  }
  public void setEnabled(  boolean enabled){
    repaintIfShowing();
  }
  /** 
 * DEPRECATED:  Replaced by setEnabled(boolean).
 * @see java.awt.peer.MenuItemPeer
 */
  public void enable(){
    setEnabled(true);
  }
  /** 
 * DEPRECATED:  Replaced by setEnabled(boolean).
 * @see java.awt.peer.MenuItemPeer
 */
  public void disable(){
    setEnabled(false);
  }
  /** 
 * Access to target's fields
 */
  MenuItem getTarget(){
    return this.target;
  }
  Font getTargetFont(){
    if (target == null) {
      return XWindow.getDefaultFont();
    }
    try {
      return (Font)m_getFont.invoke(target,new Object[0]);
    }
 catch (    IllegalAccessException e) {
      e.printStackTrace();
    }
catch (    InvocationTargetException e) {
      e.printStackTrace();
    }
    return XWindow.getDefaultFont();
  }
  String getTargetLabel(){
    if (target == null) {
      return "";
    }
    try {
      String label=(String)f_label.get(target);
      return (label == null) ? "" : label;
    }
 catch (    IllegalAccessException e) {
      e.printStackTrace();
    }
    return "";
  }
  boolean isTargetEnabled(){
    if (target == null) {
      return false;
    }
    try {
      return f_enabled.getBoolean(target);
    }
 catch (    IllegalAccessException e) {
      e.printStackTrace();
    }
    return false;
  }
  /** 
 * Returns true if item and all its parents are enabled
 * This function is used to fix
 * 6184485: Popup menu is not disabled on XToolkit even when calling setEnabled (false)
 */
  boolean isTargetItemEnabled(){
    if (target == null) {
      return false;
    }
    try {
      return ((Boolean)m_isItemEnabled.invoke(target,new Object[0])).booleanValue();
    }
 catch (    IllegalAccessException e) {
      e.printStackTrace();
    }
catch (    InvocationTargetException e) {
      e.printStackTrace();
    }
    return false;
  }
  String getTargetActionCommand(){
    if (target == null) {
      return "";
    }
    try {
      return (String)m_getActionCommand.invoke(target,(Object[])null);
    }
 catch (    IllegalAccessException e) {
      e.printStackTrace();
    }
catch (    InvocationTargetException e) {
      e.printStackTrace();
    }
    return "";
  }
  MenuShortcut getTargetShortcut(){
    if (target == null) {
      return null;
    }
    try {
      return (MenuShortcut)f_shortcut.get(target);
    }
 catch (    IllegalAccessException e) {
      e.printStackTrace();
    }
    return null;
  }
  String getShortcutText(){
    if (container == null) {
      return null;
    }
    if (container.getRootMenuWindow() instanceof XPopupMenuPeer) {
      return null;
    }
    MenuShortcut sc=getTargetShortcut();
    return (sc == null) ? null : sc.toString();
  }
  /** 
 * This function is called when filling item vectors
 * in XMenuWindow & XMenuBar. We need it because peers
 * are created earlier than windows.
 * @param container the window that this item belongs to.
 */
  void setContainer(  XBaseMenuWindow container){
synchronized (XBaseMenuWindow.getMenuTreeLock()) {
      this.container=container;
    }
  }
  /** 
 * returns the window that this item belongs to
 */
  XBaseMenuWindow getContainer(){
    return this.container;
  }
  /** 
 * This function should be overriden simply to
 * return false in inherited classes.
 */
  boolean isSeparator(){
    boolean r=(getTargetLabel().equals("-"));
    return r;
  }
  /** 
 * Returns true if container exists and is showing
 */
  boolean isContainerShowing(){
    if (container == null) {
      return false;
    }
    return container.isShowing();
  }
  /** 
 * Repaints item if it is showing
 */
  void repaintIfShowing(){
    if (isContainerShowing()) {
      container.postPaintEvent();
    }
  }
  /** 
 * This function is invoked when the user clicks
 * on menu item.
 * @param when the timestamp of action event
 */
  void action(  long when){
    if (!isSeparator() && isTargetItemEnabled()) {
      XWindow.postEventStatic(new ActionEvent(target,ActionEvent.ACTION_PERFORMED,getTargetActionCommand(),when,0));
    }
  }
  /** 
 * Returns text metrics of menu item.
 * This function does not use any locks
 * and is guaranteed to return some value
 * (possibly actual, possibly expired)
 */
  TextMetrics getTextMetrics(){
    TextMetrics textMetrics=this.textMetrics;
    if (textMetrics == null) {
      textMetrics=calcTextMetrics();
      this.textMetrics=textMetrics;
    }
    return textMetrics;
  }
  /** 
 * Returns width of item's shortcut label,
 * 0 if item has no shortcut.
 * The height of shortcut can be deternimed
 * from text dimensions.
 * This function does not use any locks
 * and is guaranteed to return some value
 * (possibly actual, possibly expired)
 */
  TextMetrics calcTextMetrics(){
    if (container == null) {
      return null;
    }
    if (isSeparator()) {
      return new TextMetrics(new Dimension(SEPARATOR_WIDTH,SEPARATOR_HEIGHT),0,0);
    }
    Graphics g=container.getGraphics();
    if (g == null) {
      return null;
    }
    try {
      g.setFont(getTargetFont());
      FontMetrics fm=g.getFontMetrics();
      String str=getTargetLabel();
      int width=fm.stringWidth(str);
      int height=fm.getHeight();
      Dimension textDimension=new Dimension(width,height);
      int textBaseline=fm.getHeight() - fm.getAscent();
      String sc=getShortcutText();
      int shortcutWidth=(sc == null) ? 0 : fm.stringWidth(sc);
      return new TextMetrics(textDimension,shortcutWidth,textBaseline);
    }
  finally {
      g.dispose();
    }
  }
  void resetTextMetrics(){
    textMetrics=null;
    if (container != null) {
      container.updateSize();
    }
  }
  /** 
 * Sets mapping of item to window.
 * @param bounds bounds of item in container's coordinates
 * @param textOrigin point for drawString in container's coordinates
 * @see XBaseMenuWindow.map()
 */
  void map(  Rectangle bounds,  Point textOrigin){
    this.bounds=bounds;
    this.textOrigin=textOrigin;
  }
  /** 
 * returns bounds of item that were previously set by map() function
 */
  Rectangle getBounds(){
    return bounds;
  }
  /** 
 * returns origin of item's text that was previously set by map() function
 */
  Point getTextOrigin(){
    return textOrigin;
  }
}
