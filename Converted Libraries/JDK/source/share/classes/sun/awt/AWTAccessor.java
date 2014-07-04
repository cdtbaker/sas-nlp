package sun.awt;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import sun.misc.Unsafe;
import java.awt.peer.ComponentPeer;
import java.security.AccessController;
import java.security.AccessControlContext;
/** 
 * The AWTAccessor utility class.
 * The main purpose of this class is to enable accessing
 * private and package-private fields of classes from
 * different classes/packages. See sun.misc.SharedSecretes
 * for another example.
 */
public final class AWTAccessor {
  private static final Unsafe unsafe=Unsafe.getUnsafe();
  private AWTAccessor(){
  }
public interface ComponentAccessor {
    void setBackgroundEraseDisabled(    Component comp,    boolean disabled);
    boolean getBackgroundEraseDisabled(    Component comp);
    Rectangle getBounds(    Component comp);
    void setMixingCutoutShape(    Component comp,    Shape shape);
    /** 
 * Sets GraphicsConfiguration value for the component.
 */
    void setGraphicsConfiguration(    Component comp,    GraphicsConfiguration gc);
    boolean requestFocus(    Component comp,    CausedFocusEvent.Cause cause);
    boolean canBeFocusOwner(    Component comp);
    /** 
 * Returns whether the component is visible without invoking
 * any client code.
 */
    boolean isVisible(    Component comp);
    /** 
 * Sets the RequestFocusController.
 */
    void setRequestFocusController(    RequestFocusController requestController);
    /** 
 * Returns the appContext of the component.
 */
    AppContext getAppContext(    Component comp);
    /** 
 * Sets the appContext of the component.
 */
    void setAppContext(    Component comp,    AppContext appContext);
    /** 
 * Returns the parent of the component.
 */
    Container getParent(    Component comp);
    /** 
 * Sets the parent of the component to the specified parent.
 */
    void setParent(    Component comp,    Container parent);
    /** 
 * Resizes the component to the specified width and height.
 */
    void setSize(    Component comp,    int width,    int height);
    /** 
 * Returns the location of the component.
 */
    Point getLocation(    Component comp);
    /** 
 * Moves the component to the new location.
 */
    void setLocation(    Component comp,    int x,    int y);
    /** 
 * Determines whether this component is enabled.
 */
    boolean isEnabled(    Component comp);
    /** 
 * Determines whether this component is displayable.
 */
    boolean isDisplayable(    Component comp);
    /** 
 * Gets the cursor set in the component.
 */
    Cursor getCursor(    Component comp);
    /** 
 * Returns the peer of the component.
 */
    ComponentPeer getPeer(    Component comp);
    /** 
 * Sets the peer of the component to the specified peer.
 */
    void setPeer(    Component comp,    ComponentPeer peer);
    /** 
 * Determines whether this component is lightweight.
 */
    boolean isLightweight(    Component comp);
    /** 
 * Returns whether or not paint messages received from
 * the operating system should be ignored.
 */
    boolean getIgnoreRepaint(    Component comp);
    /** 
 * Returns the width of the component.
 */
    int getWidth(    Component comp);
    /** 
 * Returns the height of the component.
 */
    int getHeight(    Component comp);
    /** 
 * Returns the x coordinate of the component.
 */
    int getX(    Component comp);
    /** 
 * Returns the y coordinate of the component.
 */
    int getY(    Component comp);
    /** 
 * Gets the foreground color of this component.
 */
    Color getForeground(    Component comp);
    /** 
 * Gets the background color of this component.
 */
    Color getBackground(    Component comp);
    /** 
 * Sets the background of this component to the specified color.
 */
    void setBackground(    Component comp,    Color background);
    /** 
 * Gets the font of the component.
 */
    Font getFont(    Component comp);
    /** 
 * Processes events occurring on this component.
 */
    void processEvent(    Component comp,    AWTEvent e);
    AccessControlContext getAccessControlContext(    Component comp);
  }
public interface ContainerAccessor {
    /** 
 * Validates the container unconditionally.
 */
    void validateUnconditionally(    Container cont);
  }
public interface WindowAccessor {
    float getOpacity(    Window window);
    void setOpacity(    Window window,    float opacity);
    Shape getShape(    Window window);
    void setShape(    Window window,    Shape shape);
    void setOpaque(    Window window,    boolean isOpaque);
    void updateWindow(    Window window);
    /** 
 * Get the size of the security warning.
 */
    Dimension getSecurityWarningSize(    Window w);
    /** 
 * Set the size of the security warning.
 */
    void setSecurityWarningSize(    Window w,    int width,    int height);
    /** 
 * Set the position of the security warning.
 */
    void setSecurityWarningPosition(    Window w,    Point2D point,    float alignmentX,    float alignmentY);
    /** 
 * Request to recalculate the new position of the security warning for
 * the given window size/location as reported by the native system.
 */
    Point2D calculateSecurityWarningPosition(    Window window,    double x,    double y,    double w,    double h);
    /** 
 * Sets the synchronous status of focus requests on lightweight
 * components in the specified window to the specified value.
 */
    void setLWRequestStatus(    Window changed,    boolean status);
    /** 
 * Indicates whether this window should receive focus on subsequently
 * being shown, or being moved to the front.
 */
    boolean isAutoRequestFocus(    Window w);
    /** 
 * Indicates whether the specified window is an utility window for TrayIcon.
 */
    boolean isTrayIconWindow(    Window w);
    /** 
 * Marks the specified window as an utility window for TrayIcon.
 */
    void setTrayIconWindow(    Window w,    boolean isTrayIconWindow);
  }
public interface AWTEventAccessor {
    /** 
 * Marks the event as posted.
 */
    void setPosted(    AWTEvent ev);
    /** 
 * Sets the flag on this AWTEvent indicating that it was
 * generated by the system.
 */
    void setSystemGenerated(    AWTEvent ev);
    /** 
 * Indicates whether this AWTEvent was generated by the system.
 */
    boolean isSystemGenerated(    AWTEvent ev);
    AccessControlContext getAccessControlContext(    AWTEvent ev);
  }
public interface InputEventAccessor {
    int[] getButtonDownMasks();
  }
public interface FrameAccessor {
    void setExtendedState(    Frame frame,    int state);
    int getExtendedState(    Frame frame);
    Rectangle getMaximizedBounds(    Frame frame);
  }
public interface KeyboardFocusManagerAccessor {
    int shouldNativelyFocusHeavyweight(    Component heavyweight,    Component descendant,    boolean temporary,    boolean focusedWindowChangeAllowed,    long time,    CausedFocusEvent.Cause cause);
    boolean processSynchronousLightweightTransfer(    Component heavyweight,    Component descendant,    boolean temporary,    boolean focusedWindowChangeAllowed,    long time);
    void removeLastFocusRequest(    Component heavyweight);
    void setMostRecentFocusOwner(    Window window,    Component component);
  }
public interface MenuComponentAccessor {
    /** 
 * Returns the appContext of the menu component.
 */
    AppContext getAppContext(    MenuComponent menuComp);
    /** 
 * Sets the appContext of the menu component.
 */
    void setAppContext(    MenuComponent menuComp,    AppContext appContext);
    /** 
 * Returns the menu container of the menu component
 */
    MenuContainer getParent(    MenuComponent menuComp);
  }
public interface EventQueueAccessor {
    Thread getDispatchThread(    EventQueue eventQueue);
    public boolean isDispatchThreadImpl(    EventQueue eventQueue);
  }
public interface PopupMenuAccessor {
    boolean isTrayIconPopup(    PopupMenu popupMenu);
  }
public interface FileDialogAccessor {
    void setFiles(    FileDialog fileDialog,    String directory,    String files[]);
    void setFile(    FileDialog fileDialog,    String file);
    void setDirectory(    FileDialog fileDialog,    String directory);
    boolean isMultipleMode(    FileDialog fileDialog);
  }
  private static ComponentAccessor componentAccessor;
  private static ContainerAccessor containerAccessor;
  private static WindowAccessor windowAccessor;
  private static AWTEventAccessor awtEventAccessor;
  private static InputEventAccessor inputEventAccessor;
  private static FrameAccessor frameAccessor;
  private static KeyboardFocusManagerAccessor kfmAccessor;
  private static MenuComponentAccessor menuComponentAccessor;
  private static EventQueueAccessor eventQueueAccessor;
  private static PopupMenuAccessor popupMenuAccessor;
  private static FileDialogAccessor fileDialogAccessor;
  public static void setComponentAccessor(  ComponentAccessor ca){
    componentAccessor=ca;
  }
  public static ComponentAccessor getComponentAccessor(){
    if (componentAccessor == null) {
      unsafe.ensureClassInitialized(Component.class);
    }
    return componentAccessor;
  }
  public static void setContainerAccessor(  ContainerAccessor ca){
    containerAccessor=ca;
  }
  public static ContainerAccessor getContainerAccessor(){
    if (containerAccessor == null) {
      unsafe.ensureClassInitialized(Container.class);
    }
    return containerAccessor;
  }
  public static void setWindowAccessor(  WindowAccessor wa){
    windowAccessor=wa;
  }
  public static WindowAccessor getWindowAccessor(){
    if (windowAccessor == null) {
      unsafe.ensureClassInitialized(Window.class);
    }
    return windowAccessor;
  }
  public static void setAWTEventAccessor(  AWTEventAccessor aea){
    awtEventAccessor=aea;
  }
  public static AWTEventAccessor getAWTEventAccessor(){
    if (awtEventAccessor == null) {
      unsafe.ensureClassInitialized(AWTEvent.class);
    }
    return awtEventAccessor;
  }
  public static void setInputEventAccessor(  InputEventAccessor iea){
    inputEventAccessor=iea;
  }
  public static InputEventAccessor getInputEventAccessor(){
    if (inputEventAccessor == null) {
      unsafe.ensureClassInitialized(InputEvent.class);
    }
    return inputEventAccessor;
  }
  public static void setFrameAccessor(  FrameAccessor fa){
    frameAccessor=fa;
  }
  public static FrameAccessor getFrameAccessor(){
    if (frameAccessor == null) {
      unsafe.ensureClassInitialized(Frame.class);
    }
    return frameAccessor;
  }
  public static void setKeyboardFocusManagerAccessor(  KeyboardFocusManagerAccessor kfma){
    kfmAccessor=kfma;
  }
  public static KeyboardFocusManagerAccessor getKeyboardFocusManagerAccessor(){
    if (kfmAccessor == null) {
      unsafe.ensureClassInitialized(KeyboardFocusManager.class);
    }
    return kfmAccessor;
  }
  public static void setMenuComponentAccessor(  MenuComponentAccessor mca){
    menuComponentAccessor=mca;
  }
  public static MenuComponentAccessor getMenuComponentAccessor(){
    if (menuComponentAccessor == null) {
      unsafe.ensureClassInitialized(MenuComponent.class);
    }
    return menuComponentAccessor;
  }
  public static void setEventQueueAccessor(  EventQueueAccessor eqa){
    eventQueueAccessor=eqa;
  }
  public static EventQueueAccessor getEventQueueAccessor(){
    if (eventQueueAccessor == null) {
      unsafe.ensureClassInitialized(EventQueue.class);
    }
    return eventQueueAccessor;
  }
  public static void setPopupMenuAccessor(  PopupMenuAccessor pma){
    popupMenuAccessor=pma;
  }
  public static PopupMenuAccessor getPopupMenuAccessor(){
    if (popupMenuAccessor == null) {
      unsafe.ensureClassInitialized(PopupMenu.class);
    }
    return popupMenuAccessor;
  }
  public static void setFileDialogAccessor(  FileDialogAccessor fda){
    fileDialogAccessor=fda;
  }
  public static FileDialogAccessor getFileDialogAccessor(){
    if (fileDialogAccessor == null) {
      unsafe.ensureClassInitialized(FileDialog.class);
    }
    return fileDialogAccessor;
  }
}
