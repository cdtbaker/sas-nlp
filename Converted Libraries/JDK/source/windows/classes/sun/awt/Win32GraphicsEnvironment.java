package sun.awt;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.peer.ComponentPeer;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import sun.awt.DisplayChangedListener;
import sun.awt.SunDisplayChanger;
import sun.awt.windows.WPrinterJob;
import sun.awt.windows.WToolkit;
import sun.java2d.SunGraphicsEnvironment;
import sun.java2d.SurfaceManagerFactory;
import sun.java2d.WindowsSurfaceManagerFactory;
import sun.java2d.d3d.D3DGraphicsDevice;
import sun.java2d.windows.WindowsFlags;
/** 
 * This is an implementation of a GraphicsEnvironment object for the
 * default local GraphicsEnvironment used by the Java Runtime Environment
 * for Windows.
 * @see GraphicsDevice
 * @see GraphicsConfiguration
 */
public class Win32GraphicsEnvironment extends SunGraphicsEnvironment {
static {
    WToolkit.loadLibraries();
    WindowsFlags.initFlags();
    initDisplayWrapper();
    SurfaceManagerFactory.setInstance(new WindowsSurfaceManagerFactory());
  }
  /** 
 * Initializes native components of the graphics environment.  This
 * includes everything from the native GraphicsDevice elements to
 * the DirectX rendering layer.
 */
  private static native void initDisplay();
  private static boolean displayInitialized;
  public static void initDisplayWrapper(){
    if (!displayInitialized) {
      displayInitialized=true;
      initDisplay();
    }
  }
  public Win32GraphicsEnvironment(){
  }
  protected native int getNumScreens();
  protected native int getDefaultScreen();
  public GraphicsDevice getDefaultScreenDevice(){
    return getScreenDevices()[getDefaultScreen()];
  }
  /** 
 * Returns the number of pixels per logical inch along the screen width.
 * In a system with multiple display monitors, this value is the same for
 * all monitors.
 * @returns number of pixels per logical inch in X direction
 */
  public native int getXResolution();
  /** 
 * Returns the number of pixels per logical inch along the screen height.
 * In a system with multiple display monitors, this value is the same for
 * all monitors.
 * @returns number of pixels per logical inch in Y direction
 */
  public native int getYResolution();
  private ArrayList<WeakReference<Win32GraphicsDevice>> oldDevices;
  @Override public void displayChanged(){
    GraphicsDevice newDevices[]=new GraphicsDevice[getNumScreens()];
    GraphicsDevice oldScreens[]=screens;
    if (oldScreens != null) {
      for (int i=0; i < oldScreens.length; i++) {
        if (!(screens[i] instanceof Win32GraphicsDevice)) {
          assert (false) : oldScreens[i];
          continue;
        }
        Win32GraphicsDevice gd=(Win32GraphicsDevice)oldScreens[i];
        if (!gd.isValid()) {
          if (oldDevices == null) {
            oldDevices=new ArrayList<WeakReference<Win32GraphicsDevice>>();
          }
          oldDevices.add(new WeakReference<Win32GraphicsDevice>(gd));
        }
 else         if (i < newDevices.length) {
          newDevices[i]=gd;
        }
      }
      oldScreens=null;
    }
    for (int i=0; i < newDevices.length; i++) {
      if (newDevices[i] == null) {
        newDevices[i]=makeScreenDevice(i);
      }
    }
    screens=newDevices;
    for (    GraphicsDevice gd : screens) {
      if (gd instanceof DisplayChangedListener) {
        ((DisplayChangedListener)gd).displayChanged();
      }
    }
    if (oldDevices != null) {
      int defScreen=getDefaultScreen();
      for (ListIterator<WeakReference<Win32GraphicsDevice>> it=oldDevices.listIterator(); it.hasNext(); ) {
        Win32GraphicsDevice gd=it.next().get();
        if (gd != null) {
          gd.invalidate(defScreen);
          gd.displayChanged();
        }
 else {
          it.remove();
        }
      }
    }
    WToolkit.resetGC();
    displayChanger.notifyListeners();
  }
  protected GraphicsDevice makeScreenDevice(  int screennum){
    GraphicsDevice device=null;
    if (WindowsFlags.isD3DEnabled()) {
      device=D3DGraphicsDevice.createDevice(screennum);
    }
    if (device == null) {
      device=new Win32GraphicsDevice(screennum);
    }
    return device;
  }
  public boolean isDisplayLocal(){
    return true;
  }
  @Override public boolean isFlipStrategyPreferred(  ComponentPeer peer){
    GraphicsConfiguration gc;
    if (peer != null && (gc=peer.getGraphicsConfiguration()) != null) {
      GraphicsDevice gd=gc.getDevice();
      if (gd instanceof D3DGraphicsDevice) {
        return ((D3DGraphicsDevice)gd).isD3DEnabledOnDevice();
      }
    }
    return false;
  }
  private static volatile boolean isDWMCompositionEnabled;
  /** 
 * Returns true if dwm composition is currently enabled, false otherwise.
 * @return true if dwm composition is enabled, false otherwise
 */
  public static boolean isDWMCompositionEnabled(){
    return isDWMCompositionEnabled;
  }
  /** 
 * Called from the native code when DWM composition state changed.
 * May be called multiple times during the lifetime of the application.
 * REMIND: we may want to create a listener mechanism for this.
 * Note: called on the Toolkit thread, no user code or locks are allowed.
 * @param enabled indicates the state of dwm composition
 */
  private static void dwmCompositionChanged(  boolean enabled){
    isDWMCompositionEnabled=enabled;
  }
  /** 
 * Used to find out if the OS is Windows Vista or later.
 * @return {@code true} if the OS is Vista or later, {@code false} otherwise
 */
  public static native boolean isVistaOS();
}
