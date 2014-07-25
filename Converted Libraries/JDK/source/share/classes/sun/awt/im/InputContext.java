package sun.awt.im;
import java.awt.AWTEvent;
import java.awt.AWTKeyStroke;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.InputEvent;
import java.awt.event.InputMethodEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.im.InputMethodRequests;
import java.awt.im.spi.InputMethod;
import java.lang.Character.Subset;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import sun.util.logging.PlatformLogger;
import sun.awt.SunToolkit;
/** 
 * This InputContext class contains parts of the implementation of
 * java.text.im.InputContext. These parts have been moved
 * here to avoid exposing protected members that are needed by the
 * subclass InputMethodContext.
 * @see java.awt.im.InputContext
 * @author JavaSoft Asia/Pacific
 */
public class InputContext extends java.awt.im.InputContext implements ComponentListener, WindowListener {
  private static final PlatformLogger log=PlatformLogger.getLogger("sun.awt.im.InputContext");
  private InputMethodLocator inputMethodLocator;
  private InputMethod inputMethod;
  private boolean inputMethodCreationFailed;
  private HashMap usedInputMethods;
  private Component currentClientComponent;
  private Component awtFocussedComponent;
  private boolean isInputMethodActive;
  private Subset[] characterSubsets=null;
  private boolean compositionAreaHidden=false;
  private static InputContext inputMethodWindowContext;
  private static InputMethod previousInputMethod=null;
  private boolean clientWindowNotificationEnabled=false;
  private Window clientWindowListened;
  private Rectangle clientWindowLocation=null;
  private HashMap perInputMethodState;
  private static AWTKeyStroke inputMethodSelectionKey;
  private static boolean inputMethodSelectionKeyInitialized=false;
  private static final String inputMethodSelectionKeyPath="/java/awt/im/selectionKey";
  private static final String inputMethodSelectionKeyCodeName="keyCode";
  private static final String inputMethodSelectionKeyModifiersName="modifiers";
  /** 
 * Constructs an InputContext.
 */
  protected InputContext(){
    InputMethodManager imm=InputMethodManager.getInstance();
synchronized (InputContext.class) {
      if (!inputMethodSelectionKeyInitialized) {
        inputMethodSelectionKeyInitialized=true;
        if (imm.hasMultipleInputMethods()) {
          initializeInputMethodSelectionKey();
        }
      }
    }
    selectInputMethod(imm.getDefaultKeyboardLocale());
  }
  /** 
 * @see java.awt.im.InputContext#selectInputMethod
 * @exception NullPointerException when the locale is null.
 */
  public synchronized boolean selectInputMethod(  Locale locale){
    if (locale == null) {
      throw new NullPointerException();
    }
    if (inputMethod != null) {
      if (inputMethod.setLocale(locale)) {
        return true;
      }
    }
 else     if (inputMethodLocator != null) {
      if (inputMethodLocator.isLocaleAvailable(locale)) {
        inputMethodLocator=inputMethodLocator.deriveLocator(locale);
        return true;
      }
    }
    InputMethodLocator newLocator=InputMethodManager.getInstance().findInputMethod(locale);
    if (newLocator != null) {
      changeInputMethod(newLocator);
      return true;
    }
    if (inputMethod == null && inputMethodLocator != null) {
      inputMethod=getInputMethod();
      if (inputMethod != null) {
        return inputMethod.setLocale(locale);
      }
    }
    return false;
  }
  /** 
 * @see java.awt.im.InputContext#getLocale
 */
  public Locale getLocale(){
    if (inputMethod != null) {
      return inputMethod.getLocale();
    }
 else     if (inputMethodLocator != null) {
      return inputMethodLocator.getLocale();
    }
 else {
      return null;
    }
  }
  /** 
 * @see java.awt.im.InputContext#setCharacterSubsets
 */
  public void setCharacterSubsets(  Subset[] subsets){
    if (subsets == null) {
      characterSubsets=null;
    }
 else {
      characterSubsets=new Subset[subsets.length];
      System.arraycopy(subsets,0,characterSubsets,0,characterSubsets.length);
    }
    if (inputMethod != null) {
      inputMethod.setCharacterSubsets(subsets);
    }
  }
  /** 
 * @see java.awt.im.InputContext#reconvert
 * @since 1.3
 * @exception UnsupportedOperationException when input method is null
 */
  public synchronized void reconvert(){
    InputMethod inputMethod=getInputMethod();
    if (inputMethod == null) {
      throw new UnsupportedOperationException();
    }
    inputMethod.reconvert();
  }
  /** 
 * @see java.awt.im.InputContext#dispatchEvent
 */
  public void dispatchEvent(  AWTEvent event){
    if (event instanceof InputMethodEvent) {
      return;
    }
    if (event instanceof FocusEvent) {
      Component opposite=((FocusEvent)event).getOppositeComponent();
      if ((opposite != null) && (getComponentWindow(opposite) instanceof InputMethodWindow) && (opposite.getInputContext() == this)) {
        return;
      }
    }
    InputMethod inputMethod=getInputMethod();
    int id=event.getID();
switch (id) {
case FocusEvent.FOCUS_GAINED:
      focusGained((Component)event.getSource());
    break;
case FocusEvent.FOCUS_LOST:
  focusLost((Component)event.getSource(),((FocusEvent)event).isTemporary());
break;
case KeyEvent.KEY_PRESSED:
if (checkInputMethodSelectionKey((KeyEvent)event)) {
InputMethodManager.getInstance().notifyChangeRequestByHotKey((Component)event.getSource());
break;
}
default :
if ((inputMethod != null) && (event instanceof InputEvent)) {
inputMethod.dispatchEvent(event);
}
}
}
/** 
 * Handles focus gained events for any component that's using
 * this input context.
 * These events are generated by AWT when the keyboard focus
 * moves to a component.
 * Besides actual client components, the source components
 * may also be the composition area or any component in an
 * input method window.
 * <p>
 * When handling the focus event for a client component, this
 * method checks whether the input context was previously
 * active for a different client component, and if so, calls
 * endComposition for the previous client component.
 * @param source the component gaining the focus
 */
private void focusGained(Component source){
synchronized (source.getTreeLock()) {
synchronized (this) {
if ("sun.awt.im.CompositionArea".equals(source.getClass().getName())) {
}
 else if (getComponentWindow(source) instanceof InputMethodWindow) {
}
 else {
if (!source.isDisplayable()) {
return;
}
if (inputMethod != null) {
if (currentClientComponent != null && currentClientComponent != source) {
  if (!isInputMethodActive) {
    activateInputMethod(false);
  }
  endComposition();
  deactivateInputMethod(false);
}
}
currentClientComponent=source;
}
awtFocussedComponent=source;
if (inputMethod instanceof InputMethodAdapter) {
((InputMethodAdapter)inputMethod).setAWTFocussedComponent(source);
}
if (!isInputMethodActive) {
activateInputMethod(true);
}
InputMethodContext inputContext=((InputMethodContext)this);
if (!inputContext.isCompositionAreaVisible()) {
InputMethodRequests req=source.getInputMethodRequests();
if (req != null && inputContext.useBelowTheSpotInput()) {
inputContext.setCompositionAreaUndecorated(true);
}
 else {
inputContext.setCompositionAreaUndecorated(false);
}
}
if (compositionAreaHidden == true) {
((InputMethodContext)this).setCompositionAreaVisible(true);
compositionAreaHidden=false;
}
}
}
}
/** 
 * Activates the current input method of this input context, and grabs
 * the composition area for use by this input context.
 * If updateCompositionArea is true, the text in the composition area
 * is updated (set to false if the text is going to change immediately
 * to avoid screen flicker).
 */
private void activateInputMethod(boolean updateCompositionArea){
if (inputMethodWindowContext != null && inputMethodWindowContext != this && inputMethodWindowContext.inputMethodLocator != null && !inputMethodWindowContext.inputMethodLocator.sameInputMethod(inputMethodLocator) && inputMethodWindowContext.inputMethod != null) {
inputMethodWindowContext.inputMethod.hideWindows();
}
inputMethodWindowContext=this;
if (inputMethod != null) {
if (previousInputMethod != inputMethod && previousInputMethod instanceof InputMethodAdapter) {
((InputMethodAdapter)previousInputMethod).stopListening();
}
previousInputMethod=null;
if (log.isLoggable(PlatformLogger.FINE)) log.fine("Current client component " + currentClientComponent);
if (inputMethod instanceof InputMethodAdapter) {
((InputMethodAdapter)inputMethod).setClientComponent(currentClientComponent);
}
inputMethod.activate();
isInputMethodActive=true;
if (perInputMethodState != null) {
Boolean state=(Boolean)perInputMethodState.remove(inputMethod);
if (state != null) {
clientWindowNotificationEnabled=state.booleanValue();
}
}
if (clientWindowNotificationEnabled) {
if (!addedClientWindowListeners()) {
addClientWindowListeners();
}
synchronized (this) {
if (clientWindowListened != null) {
notifyClientWindowChange(clientWindowListened);
}
}
}
 else {
if (addedClientWindowListeners()) {
removeClientWindowListeners();
}
}
}
InputMethodManager.getInstance().setInputContext(this);
((InputMethodContext)this).grabCompositionArea(updateCompositionArea);
}
static Window getComponentWindow(Component component){
while (true) {
if (component == null) {
return null;
}
 else if (component instanceof Window) {
return (Window)component;
}
 else {
component=component.getParent();
}
}
}
/** 
 * Handles focus lost events for any component that's using
 * this input context.
 * These events are generated by AWT when the keyboard focus
 * moves away from a component.
 * Besides actual client components, the source components
 * may also be the composition area or any component in an
 * input method window.
 * @param source the component losing the focus
 * @isTemporary whether the focus change is temporary
 */
private void focusLost(Component source,boolean isTemporary){
synchronized (source.getTreeLock()) {
synchronized (this) {
if (isInputMethodActive) {
deactivateInputMethod(isTemporary);
}
awtFocussedComponent=null;
if (inputMethod instanceof InputMethodAdapter) {
((InputMethodAdapter)inputMethod).setAWTFocussedComponent(null);
}
InputMethodContext inputContext=((InputMethodContext)this);
if (inputContext.isCompositionAreaVisible()) {
inputContext.setCompositionAreaVisible(false);
compositionAreaHidden=true;
}
}
}
}
/** 
 * Checks the key event is the input method selection key or not.
 */
private boolean checkInputMethodSelectionKey(KeyEvent event){
if (inputMethodSelectionKey != null) {
AWTKeyStroke aKeyStroke=AWTKeyStroke.getAWTKeyStrokeForEvent(event);
return inputMethodSelectionKey.equals(aKeyStroke);
}
 else {
return false;
}
}
private void deactivateInputMethod(boolean isTemporary){
InputMethodManager.getInstance().setInputContext(null);
if (inputMethod != null) {
isInputMethodActive=false;
inputMethod.deactivate(isTemporary);
previousInputMethod=inputMethod;
}
}
/** 
 * Switches from the current input method to the one described by newLocator.
 * The current input method, if any, is asked to end composition, deactivated,
 * and saved for future use. The newLocator is made the current locator. If
 * the input context is active, an input method instance for the new locator
 * is obtained; otherwise this is deferred until required.
 */
synchronized void changeInputMethod(InputMethodLocator newLocator){
if (inputMethodLocator == null) {
inputMethodLocator=newLocator;
inputMethodCreationFailed=false;
return;
}
if (inputMethodLocator.sameInputMethod(newLocator)) {
Locale newLocale=newLocator.getLocale();
if (newLocale != null && inputMethodLocator.getLocale() != newLocale) {
if (inputMethod != null) {
inputMethod.setLocale(newLocale);
}
inputMethodLocator=newLocator;
}
return;
}
Locale savedLocale=inputMethodLocator.getLocale();
boolean wasInputMethodActive=isInputMethodActive;
boolean wasCompositionEnabledSupported=false;
boolean wasCompositionEnabled=false;
if (inputMethod != null) {
try {
wasCompositionEnabled=inputMethod.isCompositionEnabled();
wasCompositionEnabledSupported=true;
}
 catch (UnsupportedOperationException e) {
}
if (currentClientComponent != null) {
if (!isInputMethodActive) {
activateInputMethod(false);
}
endComposition();
deactivateInputMethod(false);
if (inputMethod instanceof InputMethodAdapter) {
((InputMethodAdapter)inputMethod).setClientComponent(null);
}
}
savedLocale=inputMethod.getLocale();
if (usedInputMethods == null) {
usedInputMethods=new HashMap(5);
}
if (perInputMethodState == null) {
perInputMethodState=new HashMap(5);
}
usedInputMethods.put(inputMethodLocator.deriveLocator(null),inputMethod);
perInputMethodState.put(inputMethod,Boolean.valueOf(clientWindowNotificationEnabled));
enableClientWindowNotification(inputMethod,false);
if (this == inputMethodWindowContext) {
inputMethod.hideWindows();
inputMethodWindowContext=null;
}
inputMethodLocator=null;
inputMethod=null;
inputMethodCreationFailed=false;
}
if (newLocator.getLocale() == null && savedLocale != null && newLocator.isLocaleAvailable(savedLocale)) {
newLocator=newLocator.deriveLocator(savedLocale);
}
inputMethodLocator=newLocator;
inputMethodCreationFailed=false;
if (wasInputMethodActive) {
inputMethod=getInputMethodInstance();
if (inputMethod instanceof InputMethodAdapter) {
((InputMethodAdapter)inputMethod).setAWTFocussedComponent(awtFocussedComponent);
}
activateInputMethod(true);
}
if (wasCompositionEnabledSupported) {
inputMethod=getInputMethod();
if (inputMethod != null) {
try {
inputMethod.setCompositionEnabled(wasCompositionEnabled);
}
 catch (UnsupportedOperationException e) {
}
}
}
}
/** 
 * Returns the client component.
 */
Component getClientComponent(){
return currentClientComponent;
}
/** 
 * @see java.awt.im.InputContext#removeNotify
 * @exception NullPointerException when the component is null.
 */
public synchronized void removeNotify(Component component){
if (component == null) {
throw new NullPointerException();
}
if (inputMethod == null) {
if (component == currentClientComponent) {
currentClientComponent=null;
}
return;
}
if (component == awtFocussedComponent) {
focusLost(component,false);
}
if (component == currentClientComponent) {
if (isInputMethodActive) {
deactivateInputMethod(false);
}
inputMethod.removeNotify();
if (clientWindowNotificationEnabled && addedClientWindowListeners()) {
removeClientWindowListeners();
}
currentClientComponent=null;
if (inputMethod instanceof InputMethodAdapter) {
((InputMethodAdapter)inputMethod).setClientComponent(null);
}
if (EventQueue.isDispatchThread()) {
((InputMethodContext)this).releaseCompositionArea();
}
 else {
EventQueue.invokeLater(new Runnable(){
public void run(){
((InputMethodContext)InputContext.this).releaseCompositionArea();
}
}
);
}
}
}
/** 
 * @see java.awt.im.InputContext#dispose
 * @exception IllegalStateException when the currentClientComponent is not null
 */
public synchronized void dispose(){
if (currentClientComponent != null) {
throw new IllegalStateException("Can't dispose InputContext while it's active");
}
if (inputMethod != null) {
if (this == inputMethodWindowContext) {
inputMethod.hideWindows();
inputMethodWindowContext=null;
}
if (inputMethod == previousInputMethod) {
previousInputMethod=null;
}
if (clientWindowNotificationEnabled) {
if (addedClientWindowListeners()) {
removeClientWindowListeners();
}
clientWindowNotificationEnabled=false;
}
inputMethod.dispose();
if (clientWindowNotificationEnabled) {
enableClientWindowNotification(inputMethod,false);
}
inputMethod=null;
}
inputMethodLocator=null;
if (usedInputMethods != null && !usedInputMethods.isEmpty()) {
Iterator iterator=usedInputMethods.values().iterator();
usedInputMethods=null;
while (iterator.hasNext()) {
((InputMethod)iterator.next()).dispose();
}
}
clientWindowNotificationEnabled=false;
clientWindowListened=null;
perInputMethodState=null;
}
/** 
 * @see java.awt.im.InputContext#getInputMethodControlObject
 */
public synchronized Object getInputMethodControlObject(){
InputMethod inputMethod=getInputMethod();
if (inputMethod != null) {
return inputMethod.getControlObject();
}
 else {
return null;
}
}
/** 
 * @see java.awt.im.InputContext#setCompositionEnabled(boolean)
 * @exception UnsupportedOperationException when input method is null
 */
public void setCompositionEnabled(boolean enable){
InputMethod inputMethod=getInputMethod();
if (inputMethod == null) {
throw new UnsupportedOperationException();
}
inputMethod.setCompositionEnabled(enable);
}
/** 
 * @see java.awt.im.InputContext#isCompositionEnabled
 * @exception UnsupportedOperationException when input method is null
 */
public boolean isCompositionEnabled(){
InputMethod inputMethod=getInputMethod();
if (inputMethod == null) {
throw new UnsupportedOperationException();
}
return inputMethod.isCompositionEnabled();
}
/** 
 * @return a string with information about the current input method.
 * @exception UnsupportedOperationException when input method is null
 */
public String getInputMethodInfo(){
InputMethod inputMethod=getInputMethod();
if (inputMethod == null) {
throw new UnsupportedOperationException("Null input method");
}
String inputMethodInfo=null;
if (inputMethod instanceof InputMethodAdapter) {
inputMethodInfo=((InputMethodAdapter)inputMethod).getNativeInputMethodInfo();
}
if (inputMethodInfo == null && inputMethodLocator != null) {
inputMethodInfo=inputMethodLocator.getDescriptor().getInputMethodDisplayName(getLocale(),SunToolkit.getStartupLocale());
}
if (inputMethodInfo != null && !inputMethodInfo.equals("")) {
return inputMethodInfo;
}
return inputMethod.toString() + "-" + inputMethod.getLocale().toString();
}
/** 
 * Turns off the native IM. The native IM is diabled when
 * the deactive method of InputMethod is called. It is
 * delayed until the active method is called on a different
 * peer component. This method is provided to explicitly disable
 * the native IM.
 */
public void disableNativeIM(){
InputMethod inputMethod=getInputMethod();
if (inputMethod != null && inputMethod instanceof InputMethodAdapter) {
((InputMethodAdapter)inputMethod).disableInputMethod();
}
}
private synchronized InputMethod getInputMethod(){
if (inputMethod != null) {
return inputMethod;
}
if (inputMethodCreationFailed) {
return null;
}
inputMethod=getInputMethodInstance();
return inputMethod;
}
/** 
 * Returns an instance of the input method described by
 * the current input method locator. This may be an input
 * method that was previously used and switched out of,
 * or a new instance. The locale, character subsets, and
 * input method context of the input method are set.
 * The inputMethodCreationFailed field is set to true if the
 * instantiation failed.
 * @return an InputMethod instance
 * @see java.awt.im.spi.InputMethod#setInputMethodContext
 * @see java.awt.im.spi.InputMethod#setLocale
 * @see java.awt.im.spi.InputMethod#setCharacterSubsets
 */
private InputMethod getInputMethodInstance(){
InputMethodLocator locator=inputMethodLocator;
if (locator == null) {
inputMethodCreationFailed=true;
return null;
}
Locale locale=locator.getLocale();
InputMethod inputMethodInstance=null;
if (usedInputMethods != null) {
inputMethodInstance=(InputMethod)usedInputMethods.remove(locator.deriveLocator(null));
if (inputMethodInstance != null) {
if (locale != null) {
inputMethodInstance.setLocale(locale);
}
inputMethodInstance.setCharacterSubsets(characterSubsets);
Boolean state=(Boolean)perInputMethodState.remove(inputMethodInstance);
if (state != null) {
enableClientWindowNotification(inputMethodInstance,state.booleanValue());
}
((InputMethodContext)this).setInputMethodSupportsBelowTheSpot((!(inputMethodInstance instanceof InputMethodAdapter)) || ((InputMethodAdapter)inputMethodInstance).supportsBelowTheSpot());
return inputMethodInstance;
}
}
try {
inputMethodInstance=locator.getDescriptor().createInputMethod();
if (locale != null) {
inputMethodInstance.setLocale(locale);
}
inputMethodInstance.setInputMethodContext((InputMethodContext)this);
inputMethodInstance.setCharacterSubsets(characterSubsets);
}
 catch (Exception e) {
logCreationFailed(e);
inputMethodCreationFailed=true;
if (inputMethodInstance != null) {
inputMethodInstance=null;
}
}
catch (LinkageError e) {
logCreationFailed(e);
inputMethodCreationFailed=true;
}
((InputMethodContext)this).setInputMethodSupportsBelowTheSpot((!(inputMethodInstance instanceof InputMethodAdapter)) || ((InputMethodAdapter)inputMethodInstance).supportsBelowTheSpot());
return inputMethodInstance;
}
private void logCreationFailed(Throwable throwable){
String errorTextFormat=Toolkit.getProperty("AWT.InputMethodCreationFailed","Could not create {0}. Reason: {1}");
Object[] args={inputMethodLocator.getDescriptor().getInputMethodDisplayName(null,Locale.getDefault()),throwable.getLocalizedMessage()};
MessageFormat mf=new MessageFormat(errorTextFormat);
PlatformLogger logger=PlatformLogger.getLogger("sun.awt.im");
logger.config(mf.format(args));
}
InputMethodLocator getInputMethodLocator(){
if (inputMethod != null) {
return inputMethodLocator.deriveLocator(inputMethod.getLocale());
}
return inputMethodLocator;
}
/** 
 * @see java.awt.im.InputContext#endComposition
 */
public synchronized void endComposition(){
if (inputMethod != null) {
inputMethod.endComposition();
}
}
/** 
 * @see java.awt.im.spi.InputMethodContext#enableClientWindowNotification
 */
synchronized void enableClientWindowNotification(InputMethod requester,boolean enable){
if (requester != inputMethod) {
if (perInputMethodState == null) {
perInputMethodState=new HashMap(5);
}
perInputMethodState.put(requester,Boolean.valueOf(enable));
return;
}
if (clientWindowNotificationEnabled != enable) {
clientWindowLocation=null;
clientWindowNotificationEnabled=enable;
}
if (clientWindowNotificationEnabled) {
if (!addedClientWindowListeners()) {
addClientWindowListeners();
}
if (clientWindowListened != null) {
clientWindowLocation=null;
notifyClientWindowChange(clientWindowListened);
}
}
 else {
if (addedClientWindowListeners()) {
removeClientWindowListeners();
}
}
}
private synchronized void notifyClientWindowChange(Window window){
if (inputMethod == null) {
return;
}
if (!window.isVisible() || ((window instanceof Frame) && ((Frame)window).getState() == Frame.ICONIFIED)) {
clientWindowLocation=null;
inputMethod.notifyClientWindowChange(null);
return;
}
Rectangle location=window.getBounds();
if (clientWindowLocation == null || !clientWindowLocation.equals(location)) {
clientWindowLocation=location;
inputMethod.notifyClientWindowChange(clientWindowLocation);
}
}
private synchronized void addClientWindowListeners(){
Component client=getClientComponent();
if (client == null) {
return;
}
Window window=getComponentWindow(client);
if (window == null) {
return;
}
window.addComponentListener(this);
window.addWindowListener(this);
clientWindowListened=window;
}
private synchronized void removeClientWindowListeners(){
clientWindowListened.removeComponentListener(this);
clientWindowListened.removeWindowListener(this);
clientWindowListened=null;
}
/** 
 * Returns true if listeners have been set up for client window
 * change notification.
 */
private boolean addedClientWindowListeners(){
return clientWindowListened != null;
}
public void componentResized(ComponentEvent e){
notifyClientWindowChange((Window)e.getComponent());
}
public void componentMoved(ComponentEvent e){
notifyClientWindowChange((Window)e.getComponent());
}
public void componentShown(ComponentEvent e){
notifyClientWindowChange((Window)e.getComponent());
}
public void componentHidden(ComponentEvent e){
notifyClientWindowChange((Window)e.getComponent());
}
public void windowOpened(WindowEvent e){
}
public void windowClosing(WindowEvent e){
}
public void windowClosed(WindowEvent e){
}
public void windowIconified(WindowEvent e){
notifyClientWindowChange(e.getWindow());
}
public void windowDeiconified(WindowEvent e){
notifyClientWindowChange(e.getWindow());
}
public void windowActivated(WindowEvent e){
}
public void windowDeactivated(WindowEvent e){
}
/** 
 * Initializes the input method selection key definition in preference trees
 */
private void initializeInputMethodSelectionKey(){
AccessController.doPrivileged(new PrivilegedAction(){
public Object run(){
Preferences root=Preferences.userRoot();
inputMethodSelectionKey=getInputMethodSelectionKeyStroke(root);
if (inputMethodSelectionKey == null) {
root=Preferences.systemRoot();
inputMethodSelectionKey=getInputMethodSelectionKeyStroke(root);
}
return null;
}
}
);
}
private AWTKeyStroke getInputMethodSelectionKeyStroke(Preferences root){
try {
if (root.nodeExists(inputMethodSelectionKeyPath)) {
Preferences node=root.node(inputMethodSelectionKeyPath);
int keyCode=node.getInt(inputMethodSelectionKeyCodeName,KeyEvent.VK_UNDEFINED);
if (keyCode != KeyEvent.VK_UNDEFINED) {
int modifiers=node.getInt(inputMethodSelectionKeyModifiersName,0);
return AWTKeyStroke.getAWTKeyStroke(keyCode,modifiers);
}
}
}
 catch (BackingStoreException bse) {
}
return null;
}
}
