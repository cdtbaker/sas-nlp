package javax.swing;
import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.lang.reflect.*;
import java.lang.ref.WeakReference;
import java.security.AccessController;
import java.util.*;
import com.sun.java.swing.SwingUtilities3;
import sun.awt.SubRegionShowable;
import sun.java2d.SunGraphics2D;
import sun.security.action.GetPropertyAction;
import sun.java2d.pipe.hw.ExtendedBufferCapabilities;
import sun.awt.SunToolkit;
import sun.util.logging.PlatformLogger;
/** 
 * A PaintManager implementation that uses a BufferStrategy for
 * rendering.
 * @author Scott Violet
 */
class BufferStrategyPaintManager extends RepaintManager.PaintManager {
  private static Method COMPONENT_CREATE_BUFFER_STRATEGY_METHOD;
  private static Method COMPONENT_GET_BUFFER_STRATEGY_METHOD;
  private static final PlatformLogger LOGGER=PlatformLogger.getLogger("javax.swing.BufferStrategyPaintManager");
  /** 
 * List of BufferInfos.  We don't use a Map primarily because
 * there are typically only a handful of top level components making
 * a Map overkill.
 */
  private ArrayList<BufferInfo> bufferInfos;
  /** 
 * Indicates <code>beginPaint</code> has been invoked.  This is
 * set to true for the life of beginPaint/endPaint pair.
 */
  private boolean painting;
  /** 
 * Indicates we're in the process of showing.  All painting, on the EDT,
 * is blocked while this is true.
 */
  private boolean showing;
  private int accumulatedX;
  private int accumulatedY;
  private int accumulatedMaxX;
  private int accumulatedMaxY;
  /** 
 * Farthest JComponent ancestor for the current paint/copyArea.
 */
  private JComponent rootJ;
  /** 
 * Parent Applet/Window for the current paint/copyArea
 */
  private Container root;
  /** 
 * Location of component being painted relative to root.
 */
  private int xOffset;
  /** 
 * Location of component being painted relative to root.
 */
  private int yOffset;
  /** 
 * Graphics from the BufferStrategy.
 */
  private Graphics bsg;
  /** 
 * BufferStrategy currently being used.
 */
  private BufferStrategy bufferStrategy;
  /** 
 * BufferInfo corresponding to root.
 */
  private BufferInfo bufferInfo;
  /** 
 * Set to true if the bufferInfo needs to be disposed when current
 * paint loop is done.
 */
  private boolean disposeBufferOnEnd;
  private static Method getGetBufferStrategyMethod(){
    if (COMPONENT_GET_BUFFER_STRATEGY_METHOD == null) {
      getMethods();
    }
    return COMPONENT_GET_BUFFER_STRATEGY_METHOD;
  }
  private static Method getCreateBufferStrategyMethod(){
    if (COMPONENT_CREATE_BUFFER_STRATEGY_METHOD == null) {
      getMethods();
    }
    return COMPONENT_CREATE_BUFFER_STRATEGY_METHOD;
  }
  private static void getMethods(){
    java.security.AccessController.doPrivileged(new java.security.PrivilegedAction<Object>(){
      public Object run(){
        try {
          COMPONENT_CREATE_BUFFER_STRATEGY_METHOD=Component.class.getDeclaredMethod("createBufferStrategy",new Class[]{int.class,BufferCapabilities.class});
          COMPONENT_CREATE_BUFFER_STRATEGY_METHOD.setAccessible(true);
          COMPONENT_GET_BUFFER_STRATEGY_METHOD=Component.class.getDeclaredMethod("getBufferStrategy");
          COMPONENT_GET_BUFFER_STRATEGY_METHOD.setAccessible(true);
        }
 catch (        SecurityException e) {
          assert false;
        }
catch (        NoSuchMethodException nsme) {
          assert false;
        }
        return null;
      }
    }
);
  }
  BufferStrategyPaintManager(){
    bufferInfos=new ArrayList<BufferInfo>(1);
  }
  /** 
 * Cleans up any created BufferStrategies.
 */
  protected void dispose(){
    SwingUtilities.invokeLater(new Runnable(){
      public void run(){
        java.util.List<BufferInfo> bufferInfos;
synchronized (BufferStrategyPaintManager.this) {
          while (showing) {
            try {
              BufferStrategyPaintManager.this.wait();
            }
 catch (            InterruptedException ie) {
            }
          }
          bufferInfos=BufferStrategyPaintManager.this.bufferInfos;
          BufferStrategyPaintManager.this.bufferInfos=null;
        }
        dispose(bufferInfos);
      }
    }
);
  }
  private void dispose(  java.util.List<BufferInfo> bufferInfos){
    if (LOGGER.isLoggable(PlatformLogger.FINER)) {
      LOGGER.finer("BufferStrategyPaintManager disposed",new RuntimeException());
    }
    if (bufferInfos != null) {
      for (      BufferInfo bufferInfo : bufferInfos) {
        bufferInfo.dispose();
      }
    }
  }
  /** 
 * Shows the specified region of the back buffer.  This will return
 * true if successful, false otherwise.  This is invoked on the
 * toolkit thread in response to an expose event.
 */
  public boolean show(  Container c,  int x,  int y,  int w,  int h){
synchronized (this) {
      if (painting) {
        return false;
      }
      showing=true;
    }
    try {
      BufferInfo info=getBufferInfo(c);
      BufferStrategy bufferStrategy;
      if (info != null && info.isInSync() && (bufferStrategy=info.getBufferStrategy(false)) != null) {
        SubRegionShowable bsSubRegion=(SubRegionShowable)bufferStrategy;
        boolean paintAllOnExpose=info.getPaintAllOnExpose();
        info.setPaintAllOnExpose(false);
        if (bsSubRegion.showIfNotLost(x,y,(x + w),(y + h))) {
          return !paintAllOnExpose;
        }
        bufferInfo.setContentsLostDuringExpose(true);
      }
    }
  finally {
synchronized (this) {
        showing=false;
        notifyAll();
      }
    }
    return false;
  }
  public boolean paint(  JComponent paintingComponent,  JComponent bufferComponent,  Graphics g,  int x,  int y,  int w,  int h){
    if (prepare(paintingComponent,true,x,y,w,h)) {
      if ((g instanceof SunGraphics2D) && ((SunGraphics2D)g).getDestination() == root) {
        int cx=((SunGraphics2D)bsg).constrainX;
        int cy=((SunGraphics2D)bsg).constrainY;
        if (cx != 0 || cy != 0) {
          bsg.translate(-cx,-cy);
        }
        ((SunGraphics2D)bsg).constrain(xOffset + cx,yOffset + cy,x + w,y + h);
        bsg.setClip(x,y,w,h);
        paintingComponent.paintToOffscreen(bsg,x,y,w,h,x + w,y + h);
        accumulate(xOffset + x,yOffset + y,w,h);
        return true;
      }
 else {
        bufferInfo.setInSync(false);
      }
    }
    if (LOGGER.isLoggable(PlatformLogger.FINER)) {
      LOGGER.finer("prepare failed");
    }
    return super.paint(paintingComponent,bufferComponent,g,x,y,w,h);
  }
  public void copyArea(  JComponent c,  Graphics g,  int x,  int y,  int w,  int h,  int deltaX,  int deltaY,  boolean clip){
    if (prepare(c,false,0,0,0,0) && bufferInfo.isInSync()) {
      if (clip) {
        Rectangle cBounds=c.getVisibleRect();
        int relX=xOffset + x;
        int relY=yOffset + y;
        bsg.clipRect(xOffset + cBounds.x,yOffset + cBounds.y,cBounds.width,cBounds.height);
        bsg.copyArea(relX,relY,w,h,deltaX,deltaY);
      }
 else {
        bsg.copyArea(xOffset + x,yOffset + y,w,h,deltaX,deltaY);
      }
      accumulate(x + xOffset + deltaX,y + yOffset + deltaY,w,h);
    }
 else {
      if (LOGGER.isLoggable(PlatformLogger.FINER)) {
        LOGGER.finer("copyArea: prepare failed or not in sync");
      }
      if (!flushAccumulatedRegion()) {
        rootJ.repaint();
      }
 else {
        super.copyArea(c,g,x,y,w,h,deltaX,deltaY,clip);
      }
    }
  }
  public void beginPaint(){
synchronized (this) {
      painting=true;
      while (showing) {
        try {
          wait();
        }
 catch (        InterruptedException ie) {
        }
      }
    }
    if (LOGGER.isLoggable(PlatformLogger.FINEST)) {
      LOGGER.finest("beginPaint");
    }
    resetAccumulated();
  }
  public void endPaint(){
    if (LOGGER.isLoggable(PlatformLogger.FINEST)) {
      LOGGER.finest("endPaint: region " + accumulatedX + " "+ accumulatedY+ " "+ accumulatedMaxX+ " "+ accumulatedMaxY);
    }
    if (painting) {
      if (!flushAccumulatedRegion()) {
        if (!isRepaintingRoot()) {
          repaintRoot(rootJ);
        }
 else {
          resetDoubleBufferPerWindow();
          rootJ.repaint();
        }
      }
    }
    BufferInfo toDispose=null;
synchronized (this) {
      painting=false;
      if (disposeBufferOnEnd) {
        disposeBufferOnEnd=false;
        toDispose=bufferInfo;
        bufferInfos.remove(toDispose);
      }
    }
    if (toDispose != null) {
      toDispose.dispose();
    }
  }
  /** 
 * Renders the BufferStrategy to the screen.
 * @return true if successful, false otherwise.
 */
  private boolean flushAccumulatedRegion(){
    boolean success=true;
    if (accumulatedX != Integer.MAX_VALUE) {
      SubRegionShowable bsSubRegion=(SubRegionShowable)bufferStrategy;
      boolean contentsLost=bufferStrategy.contentsLost();
      if (!contentsLost) {
        bsSubRegion.show(accumulatedX,accumulatedY,accumulatedMaxX,accumulatedMaxY);
        contentsLost=bufferStrategy.contentsLost();
      }
      if (contentsLost) {
        if (LOGGER.isLoggable(PlatformLogger.FINER)) {
          LOGGER.finer("endPaint: contents lost");
        }
        bufferInfo.setInSync(false);
        success=false;
      }
    }
    resetAccumulated();
    return success;
  }
  private void resetAccumulated(){
    accumulatedX=Integer.MAX_VALUE;
    accumulatedY=Integer.MAX_VALUE;
    accumulatedMaxX=0;
    accumulatedMaxY=0;
  }
  /** 
 * Invoked when the double buffering or useTrueDoubleBuffering
 * changes for a JRootPane.  If the rootpane is not double
 * buffered, or true double buffering changes we throw out any
 * cache we may have.
 */
  public void doubleBufferingChanged(  final JRootPane rootPane){
    if ((!rootPane.isDoubleBuffered() || !rootPane.getUseTrueDoubleBuffering()) && rootPane.getParent() != null) {
      if (!SwingUtilities.isEventDispatchThread()) {
        Runnable updater=new Runnable(){
          public void run(){
            doubleBufferingChanged0(rootPane);
          }
        }
;
        SwingUtilities.invokeLater(updater);
      }
 else {
        doubleBufferingChanged0(rootPane);
      }
    }
  }
  /** 
 * Does the work for doubleBufferingChanged.
 */
  private void doubleBufferingChanged0(  JRootPane rootPane){
    BufferInfo info;
synchronized (this) {
      while (showing) {
        try {
          wait();
        }
 catch (        InterruptedException ie) {
        }
      }
      info=getBufferInfo(rootPane.getParent());
      if (painting && bufferInfo == info) {
        disposeBufferOnEnd=true;
        info=null;
      }
 else       if (info != null) {
        bufferInfos.remove(info);
      }
    }
    if (info != null) {
      info.dispose();
    }
  }
  /** 
 * Calculates information common to paint/copyArea.
 * @return true if should use buffering per window in painting.
 */
  private boolean prepare(  JComponent c,  boolean isPaint,  int x,  int y,  int w,  int h){
    if (bsg != null) {
      bsg.dispose();
      bsg=null;
    }
    bufferStrategy=null;
    if (fetchRoot(c)) {
      boolean contentsLost=false;
      BufferInfo bufferInfo=getBufferInfo(root);
      if (bufferInfo == null) {
        contentsLost=true;
        bufferInfo=new BufferInfo(root);
        bufferInfos.add(bufferInfo);
        if (LOGGER.isLoggable(PlatformLogger.FINER)) {
          LOGGER.finer("prepare: new BufferInfo: " + root);
        }
      }
      this.bufferInfo=bufferInfo;
      if (!bufferInfo.hasBufferStrategyChanged()) {
        bufferStrategy=bufferInfo.getBufferStrategy(true);
        if (bufferStrategy != null) {
          bsg=bufferStrategy.getDrawGraphics();
          if (bufferStrategy.contentsRestored()) {
            contentsLost=true;
            if (LOGGER.isLoggable(PlatformLogger.FINER)) {
              LOGGER.finer("prepare: contents restored in prepare");
            }
          }
        }
 else {
          return false;
        }
        if (bufferInfo.getContentsLostDuringExpose()) {
          contentsLost=true;
          bufferInfo.setContentsLostDuringExpose(false);
          if (LOGGER.isLoggable(PlatformLogger.FINER)) {
            LOGGER.finer("prepare: contents lost on expose");
          }
        }
        if (isPaint && c == rootJ && x == 0 && y == 0 && c.getWidth() == w && c.getHeight() == h) {
          bufferInfo.setInSync(true);
        }
 else         if (contentsLost) {
          bufferInfo.setInSync(false);
          if (!isRepaintingRoot()) {
            repaintRoot(rootJ);
          }
 else {
            resetDoubleBufferPerWindow();
          }
        }
        return (bufferInfos != null);
      }
    }
    return false;
  }
  private boolean fetchRoot(  JComponent c){
    boolean encounteredHW=false;
    rootJ=c;
    root=c;
    xOffset=yOffset=0;
    while (root != null && (!(root instanceof Window) && !SunToolkit.isInstanceOf(root,"java.applet.Applet"))) {
      xOffset+=root.getX();
      yOffset+=root.getY();
      root=root.getParent();
      if (root != null) {
        if (root instanceof JComponent) {
          rootJ=(JComponent)root;
        }
 else         if (!root.isLightweight()) {
          if (!encounteredHW) {
            encounteredHW=true;
          }
 else {
            return false;
          }
        }
      }
    }
    if ((root instanceof RootPaneContainer) && (rootJ instanceof JRootPane)) {
      if (rootJ.isDoubleBuffered() && ((JRootPane)rootJ).getUseTrueDoubleBuffering()) {
        return true;
      }
    }
    return false;
  }
  /** 
 * Turns off double buffering per window.
 */
  private void resetDoubleBufferPerWindow(){
    if (bufferInfos != null) {
      dispose(bufferInfos);
      bufferInfos=null;
      repaintManager.setPaintManager(null);
    }
  }
  /** 
 * Returns the BufferInfo for the specified root or null if one
 * hasn't been created yet.
 */
  private BufferInfo getBufferInfo(  Container root){
    for (int counter=bufferInfos.size() - 1; counter >= 0; counter--) {
      BufferInfo bufferInfo=bufferInfos.get(counter);
      Container biRoot=bufferInfo.getRoot();
      if (biRoot == null) {
        bufferInfos.remove(counter);
        if (LOGGER.isLoggable(PlatformLogger.FINER)) {
          LOGGER.finer("BufferInfo pruned, root null");
        }
      }
 else       if (biRoot == root) {
        return bufferInfo;
      }
    }
    return null;
  }
  private void accumulate(  int x,  int y,  int w,  int h){
    accumulatedX=Math.min(x,accumulatedX);
    accumulatedY=Math.min(y,accumulatedY);
    accumulatedMaxX=Math.max(accumulatedMaxX,x + w);
    accumulatedMaxY=Math.max(accumulatedMaxY,y + h);
  }
  /** 
 * BufferInfo is used to track the BufferStrategy being used for
 * a particular Component.  In addition to tracking the BufferStrategy
 * it will install a WindowListener and ComponentListener.  When the
 * component is hidden/iconified the buffer is marked as needing to be
 * completely repainted.
 */
private class BufferInfo extends ComponentAdapter implements WindowListener {
    private WeakReference<BufferStrategy> weakBS;
    private WeakReference<Container> root;
    private boolean inSync;
    private boolean contentsLostDuringExpose;
    private boolean paintAllOnExpose;
    public BufferInfo(    Container root){
      this.root=new WeakReference<Container>(root);
      root.addComponentListener(this);
      if (root instanceof Window) {
        ((Window)root).addWindowListener(this);
      }
    }
    public void setPaintAllOnExpose(    boolean paintAllOnExpose){
      this.paintAllOnExpose=paintAllOnExpose;
    }
    public boolean getPaintAllOnExpose(){
      return paintAllOnExpose;
    }
    public void setContentsLostDuringExpose(    boolean value){
      contentsLostDuringExpose=value;
    }
    public boolean getContentsLostDuringExpose(){
      return contentsLostDuringExpose;
    }
    public void setInSync(    boolean inSync){
      this.inSync=inSync;
    }
    /** 
 * Whether or not the contents of the buffer strategy
 * is in sync with the window.  This is set to true when the root
 * pane paints all, and false when contents are lost/restored.
 */
    public boolean isInSync(){
      return inSync;
    }
    /** 
 * Returns the Root (Window or Applet) that this BufferInfo references.
 */
    public Container getRoot(){
      return (root == null) ? null : root.get();
    }
    /** 
 * Returns the BufferStartegy.  This will return null if
 * the BufferStartegy hasn't been created and <code>create</code> is
 * false, or if there is a problem in creating the
 * <code>BufferStartegy</code>.
 * @param create If true, and the BufferStartegy is currently null,
 * one will be created.
 */
    public BufferStrategy getBufferStrategy(    boolean create){
      BufferStrategy bs=(weakBS == null) ? null : weakBS.get();
      if (bs == null && create) {
        bs=createBufferStrategy();
        if (bs != null) {
          weakBS=new WeakReference<BufferStrategy>(bs);
        }
        if (LOGGER.isLoggable(PlatformLogger.FINER)) {
          LOGGER.finer("getBufferStrategy: created bs: " + bs);
        }
      }
      return bs;
    }
    /** 
 * Returns true if the buffer strategy of the component differs
 * from current buffer strategy.
 */
    public boolean hasBufferStrategyChanged(){
      Container root=getRoot();
      if (root != null) {
        BufferStrategy ourBS=null;
        BufferStrategy componentBS=null;
        ourBS=getBufferStrategy(false);
        if (root instanceof Window) {
          componentBS=((Window)root).getBufferStrategy();
        }
 else {
          try {
            componentBS=(BufferStrategy)getGetBufferStrategyMethod().invoke(root);
          }
 catch (          InvocationTargetException ite) {
            assert false;
          }
catch (          IllegalArgumentException iae) {
            assert false;
          }
catch (          IllegalAccessException iae2) {
            assert false;
          }
        }
        if (componentBS != ourBS) {
          if (ourBS != null) {
            ourBS.dispose();
          }
          weakBS=null;
          return true;
        }
      }
      return false;
    }
    /** 
 * Creates the BufferStrategy.  If the appropriate system property
 * has been set we'll try for flip first and then we'll try for
 * blit.
 */
    private BufferStrategy createBufferStrategy(){
      Container root=getRoot();
      if (root == null) {
        return null;
      }
      BufferStrategy bs=null;
      if (SwingUtilities3.isVsyncRequested(root)) {
        bs=createBufferStrategy(root,true);
        if (LOGGER.isLoggable(PlatformLogger.FINER)) {
          LOGGER.finer("createBufferStrategy: using vsynced strategy");
        }
      }
      if (bs == null) {
        bs=createBufferStrategy(root,false);
      }
      if (!(bs instanceof SubRegionShowable)) {
        bs=null;
      }
      return bs;
    }
    private BufferStrategy createBufferStrategy(    Container root,    boolean isVsynced){
      BufferCapabilities caps;
      if (isVsynced) {
        caps=new ExtendedBufferCapabilities(new ImageCapabilities(true),new ImageCapabilities(true),BufferCapabilities.FlipContents.COPIED,ExtendedBufferCapabilities.VSyncType.VSYNC_ON);
      }
 else {
        caps=new BufferCapabilities(new ImageCapabilities(true),new ImageCapabilities(true),null);
      }
      BufferStrategy bs=null;
      if (SunToolkit.isInstanceOf(root,"java.applet.Applet")) {
        try {
          getCreateBufferStrategyMethod().invoke(root,2,caps);
          bs=(BufferStrategy)getGetBufferStrategyMethod().invoke(root);
        }
 catch (        InvocationTargetException ite) {
          if (LOGGER.isLoggable(PlatformLogger.FINER)) {
            LOGGER.finer("createBufferStratety failed",ite);
          }
        }
catch (        IllegalArgumentException iae) {
          assert false;
        }
catch (        IllegalAccessException iae2) {
          assert false;
        }
      }
 else {
        try {
          ((Window)root).createBufferStrategy(2,caps);
          bs=((Window)root).getBufferStrategy();
        }
 catch (        AWTException e) {
          if (LOGGER.isLoggable(PlatformLogger.FINER)) {
            LOGGER.finer("createBufferStratety failed",e);
          }
        }
      }
      return bs;
    }
    /** 
 * Cleans up and removes any references.
 */
    public void dispose(){
      Container root=getRoot();
      if (LOGGER.isLoggable(PlatformLogger.FINER)) {
        LOGGER.finer("disposed BufferInfo for: " + root);
      }
      if (root != null) {
        root.removeComponentListener(this);
        if (root instanceof Window) {
          ((Window)root).removeWindowListener(this);
        }
        BufferStrategy bs=getBufferStrategy(false);
        if (bs != null) {
          bs.dispose();
        }
      }
      this.root=null;
      weakBS=null;
    }
    public void componentHidden(    ComponentEvent e){
      Container root=getRoot();
      if (root != null && root.isVisible()) {
        root.repaint();
      }
 else {
        setPaintAllOnExpose(true);
      }
    }
    public void windowIconified(    WindowEvent e){
      setPaintAllOnExpose(true);
    }
    public void windowClosed(    WindowEvent e){
synchronized (BufferStrategyPaintManager.this) {
        while (showing) {
          try {
            BufferStrategyPaintManager.this.wait();
          }
 catch (          InterruptedException ie) {
          }
        }
        bufferInfos.remove(this);
      }
      dispose();
    }
    public void windowOpened(    WindowEvent e){
    }
    public void windowClosing(    WindowEvent e){
    }
    public void windowDeiconified(    WindowEvent e){
    }
    public void windowActivated(    WindowEvent e){
    }
    public void windowDeactivated(    WindowEvent e){
    }
  }
}
