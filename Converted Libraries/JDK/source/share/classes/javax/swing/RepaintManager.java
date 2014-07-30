package javax.swing;
import java.awt.*;
import java.awt.event.*;
import java.awt.peer.ComponentPeer;
import java.awt.peer.ContainerPeer;
import java.awt.image.VolatileImage;
import java.security.AccessController;
import java.util.*;
import java.applet.*;
import sun.awt.AWTAccessor;
import sun.awt.AppContext;
import sun.awt.DisplayChangedListener;
import sun.awt.SunToolkit;
import sun.java2d.SunGraphicsEnvironment;
import sun.security.action.GetPropertyAction;
import com.sun.java.swing.SwingUtilities3;
/** 
 * This class manages repaint requests, allowing the number
 * of repaints to be minimized, for example by collapsing multiple
 * requests into a single repaint for members of a component tree.
 * <p>
 * As of 1.6 <code>RepaintManager</code> handles repaint requests
 * for Swing's top level components (<code>JApplet</code>,
 * <code>JWindow</code>, <code>JFrame</code> and <code>JDialog</code>).
 * Any calls to <code>repaint</code> on one of these will call into the
 * appropriate <code>addDirtyRegion</code> method.
 * @author Arnaud Weber
 */
public class RepaintManager {
  /** 
 * Whether or not the RepaintManager should handle paint requests
 * for top levels.
 */
  static final boolean HANDLE_TOP_LEVEL_PAINT;
  private static final short BUFFER_STRATEGY_NOT_SPECIFIED=0;
  private static final short BUFFER_STRATEGY_SPECIFIED_ON=1;
  private static final short BUFFER_STRATEGY_SPECIFIED_OFF=2;
  private static final short BUFFER_STRATEGY_TYPE;
  /** 
 * Maps from GraphicsConfiguration to VolatileImage.
 */
  private Map<GraphicsConfiguration,VolatileImage> volatileMap=new HashMap<GraphicsConfiguration,VolatileImage>(1);
  private Map<Container,Rectangle> hwDirtyComponents;
  private Map<Component,Rectangle> dirtyComponents;
  private Map<Component,Rectangle> tmpDirtyComponents;
  private java.util.List<Component> invalidComponents;
  private java.util.List<Runnable> runnableList;
  boolean doubleBufferingEnabled=true;
  private Dimension doubleBufferMaxSize;
  DoubleBufferInfo standardDoubleBuffer;
  /** 
 * Object responsible for hanlding core paint functionality.
 */
  private PaintManager paintManager;
  private static final Object repaintManagerKey=RepaintManager.class;
  static boolean volatileImageBufferEnabled=true;
  /** 
 * Value of the system property awt.nativeDoubleBuffering.
 */
  private static boolean nativeDoubleBuffering;
  private static final int VOLATILE_LOOP_MAX=2;
  /** 
 * Number of <code>beginPaint</code> that have been invoked.
 */
  private int paintDepth=0;
  /** 
 * Type of buffer strategy to use.  Will be one of the BUFFER_STRATEGY_
 * constants.
 */
  private short bufferStrategyType;
  /** 
 * True if we're in the process of painting the dirty regions.  This is
 * set to true in <code>paintDirtyRegions</code>.
 */
  private boolean painting;
  /** 
 * If the PaintManager calls into repaintRoot during painting this field
 * will be set to the root.
 */
  private JComponent repaintRoot;
  /** 
 * The Thread that has initiated painting.  If null it
 * indicates painting is not currently in progress.
 */
  private Thread paintThread;
  /** 
 * Runnable used to process all repaint/revalidate requests.
 */
  private final ProcessingRunnable processingRunnable;
static {
    volatileImageBufferEnabled="true".equals(AccessController.doPrivileged(new GetPropertyAction("swing.volatileImageBufferEnabled","true")));
    boolean headless=GraphicsEnvironment.isHeadless();
    if (volatileImageBufferEnabled && headless) {
      volatileImageBufferEnabled=false;
    }
    nativeDoubleBuffering="true".equals(AccessController.doPrivileged(new GetPropertyAction("awt.nativeDoubleBuffering")));
    String bs=AccessController.doPrivileged(new GetPropertyAction("swing.bufferPerWindow"));
    if (headless) {
      BUFFER_STRATEGY_TYPE=BUFFER_STRATEGY_SPECIFIED_OFF;
    }
 else     if (bs == null) {
      BUFFER_STRATEGY_TYPE=BUFFER_STRATEGY_NOT_SPECIFIED;
    }
 else     if ("true".equals(bs)) {
      BUFFER_STRATEGY_TYPE=BUFFER_STRATEGY_SPECIFIED_ON;
    }
 else {
      BUFFER_STRATEGY_TYPE=BUFFER_STRATEGY_SPECIFIED_OFF;
    }
    HANDLE_TOP_LEVEL_PAINT="true".equals(AccessController.doPrivileged(new GetPropertyAction("swing.handleTopLevelPaint","true")));
    GraphicsEnvironment ge=GraphicsEnvironment.getLocalGraphicsEnvironment();
    if (ge instanceof SunGraphicsEnvironment) {
      ((SunGraphicsEnvironment)ge).addDisplayChangedListener(new DisplayChangedHandler());
    }
  }
  /** 
 * Return the RepaintManager for the calling thread given a Component.
 * @param c a Component -- unused in the default implementation, but could
 * be used by an overridden version to return a different RepaintManager
 * depending on the Component
 * @return the RepaintManager object
 */
  public static RepaintManager currentManager(  Component c){
    return currentManager(AppContext.getAppContext());
  }
  /** 
 * Returns the RepaintManager for the specified AppContext.  If
 * a RepaintManager has not been created for the specified
 * AppContext this will return null.
 */
  static RepaintManager currentManager(  AppContext appContext){
    RepaintManager rm=(RepaintManager)appContext.get(repaintManagerKey);
    if (rm == null) {
      rm=new RepaintManager(BUFFER_STRATEGY_TYPE);
      appContext.put(repaintManagerKey,rm);
    }
    return rm;
  }
  /** 
 * Return the RepaintManager for the calling thread given a JComponent.
 * <p>
 * Note: This method exists for backward binary compatibility with earlier
 * versions of the Swing library. It simply returns the result returned by{@link #currentManager(Component)}.
 * @param c a JComponent -- unused
 * @return the RepaintManager object
 */
  public static RepaintManager currentManager(  JComponent c){
    return currentManager((Component)c);
  }
  /** 
 * Set the RepaintManager that should be used for the calling
 * thread. <b>aRepaintManager</b> will become the current RepaintManager
 * for the calling thread's thread group.
 * @param aRepaintManager  the RepaintManager object to use
 */
  public static void setCurrentManager(  RepaintManager aRepaintManager){
    if (aRepaintManager != null) {
      SwingUtilities.appContextPut(repaintManagerKey,aRepaintManager);
    }
 else {
      SwingUtilities.appContextRemove(repaintManagerKey);
    }
  }
  /** 
 * Create a new RepaintManager instance. You rarely call this constructor.
 * directly. To get the default RepaintManager, use
 * RepaintManager.currentManager(JComponent) (normally "this").
 */
  public RepaintManager(){
    this(BUFFER_STRATEGY_SPECIFIED_OFF);
  }
  private RepaintManager(  short bufferStrategyType){
    doubleBufferingEnabled=!nativeDoubleBuffering;
synchronized (this) {
      dirtyComponents=new IdentityHashMap<Component,Rectangle>();
      tmpDirtyComponents=new IdentityHashMap<Component,Rectangle>();
      this.bufferStrategyType=bufferStrategyType;
      hwDirtyComponents=new IdentityHashMap<Container,Rectangle>();
    }
    processingRunnable=new ProcessingRunnable();
  }
  private void displayChanged(){
    clearImages();
  }
  /** 
 * Mark the component as in need of layout and queue a runnable
 * for the event dispatching thread that will validate the components
 * first isValidateRoot() ancestor.
 * @see JComponent#isValidateRoot
 * @see #removeInvalidComponent
 */
  public synchronized void addInvalidComponent(  JComponent invalidComponent){
    RepaintManager delegate=getDelegate(invalidComponent);
    if (delegate != null) {
      delegate.addInvalidComponent(invalidComponent);
      return;
    }
    Component validateRoot=SwingUtilities.getValidateRoot(invalidComponent,true);
    if (validateRoot == null) {
      return;
    }
    if (invalidComponents == null) {
      invalidComponents=new ArrayList<Component>();
    }
 else {
      int n=invalidComponents.size();
      for (int i=0; i < n; i++) {
        if (validateRoot == invalidComponents.get(i)) {
          return;
        }
      }
    }
    invalidComponents.add(validateRoot);
    scheduleProcessingRunnable();
  }
  /** 
 * Remove a component from the list of invalid components.
 * @see #addInvalidComponent
 */
  public synchronized void removeInvalidComponent(  JComponent component){
    RepaintManager delegate=getDelegate(component);
    if (delegate != null) {
      delegate.removeInvalidComponent(component);
      return;
    }
    if (invalidComponents != null) {
      int index=invalidComponents.indexOf(component);
      if (index != -1) {
        invalidComponents.remove(index);
      }
    }
  }
  /** 
 * Add a component in the list of components that should be refreshed.
 * If <i>c</i> already has a dirty region, the rectangle <i>(x,y,w,h)</i>
 * will be unioned with the region that should be redrawn.
 * @see JComponent#repaint
 */
  private void addDirtyRegion0(  Container c,  int x,  int y,  int w,  int h){
    if ((w <= 0) || (h <= 0) || (c == null)) {
      return;
    }
    if ((c.getWidth() <= 0) || (c.getHeight() <= 0)) {
      return;
    }
    if (extendDirtyRegion(c,x,y,w,h)) {
      return;
    }
    Component root=null;
    for (Container p=c; p != null; p=p.getParent()) {
      if (!p.isVisible() || (p.getPeer() == null)) {
        return;
      }
      if ((p instanceof Window) || (p instanceof Applet)) {
        if (p instanceof Frame && (((Frame)p).getExtendedState() & Frame.ICONIFIED) == Frame.ICONIFIED) {
          return;
        }
        root=p;
        break;
      }
    }
    if (root == null)     return;
synchronized (this) {
      if (extendDirtyRegion(c,x,y,w,h)) {
        return;
      }
      dirtyComponents.put(c,new Rectangle(x,y,w,h));
    }
    scheduleProcessingRunnable();
  }
  /** 
 * Add a component in the list of components that should be refreshed.
 * If <i>c</i> already has a dirty region, the rectangle <i>(x,y,w,h)</i>
 * will be unioned with the region that should be redrawn.
 * @param c Component to repaint, null results in nothing happening.
 * @param x X coordinate of the region to repaint
 * @param y Y coordinate of the region to repaint
 * @param w Width of the region to repaint
 * @param h Height of the region to repaint
 * @see JComponent#repaint
 */
  public void addDirtyRegion(  JComponent c,  int x,  int y,  int w,  int h){
    RepaintManager delegate=getDelegate(c);
    if (delegate != null) {
      delegate.addDirtyRegion(c,x,y,w,h);
      return;
    }
    addDirtyRegion0(c,x,y,w,h);
  }
  /** 
 * Adds <code>window</code> to the list of <code>Component</code>s that
 * need to be repainted.
 * @param window Window to repaint, null results in nothing happening.
 * @param x X coordinate of the region to repaint
 * @param y Y coordinate of the region to repaint
 * @param w Width of the region to repaint
 * @param h Height of the region to repaint
 * @see JFrame#repaint
 * @see JWindow#repaint
 * @see JDialog#repaint
 * @since 1.6
 */
  public void addDirtyRegion(  Window window,  int x,  int y,  int w,  int h){
    addDirtyRegion0(window,x,y,w,h);
  }
  /** 
 * Adds <code>applet</code> to the list of <code>Component</code>s that
 * need to be repainted.
 * @param applet Applet to repaint, null results in nothing happening.
 * @param x X coordinate of the region to repaint
 * @param y Y coordinate of the region to repaint
 * @param w Width of the region to repaint
 * @param h Height of the region to repaint
 * @see JApplet#repaint
 * @since 1.6
 */
  public void addDirtyRegion(  Applet applet,  int x,  int y,  int w,  int h){
    addDirtyRegion0(applet,x,y,w,h);
  }
  void scheduleHeavyWeightPaints(){
    Map<Container,Rectangle> hws;
synchronized (this) {
      if (hwDirtyComponents.size() == 0) {
        return;
      }
      hws=hwDirtyComponents;
      hwDirtyComponents=new IdentityHashMap<Container,Rectangle>();
    }
    for (    Container hw : hws.keySet()) {
      Rectangle dirty=hws.get(hw);
      if (hw instanceof Window) {
        addDirtyRegion((Window)hw,dirty.x,dirty.y,dirty.width,dirty.height);
      }
 else       if (hw instanceof Applet) {
        addDirtyRegion((Applet)hw,dirty.x,dirty.y,dirty.width,dirty.height);
      }
 else {
        addDirtyRegion0(hw,dirty.x,dirty.y,dirty.width,dirty.height);
      }
    }
  }
  void nativeAddDirtyRegion(  AppContext appContext,  Container c,  int x,  int y,  int w,  int h){
    if (w > 0 && h > 0) {
synchronized (this) {
        Rectangle dirty=hwDirtyComponents.get(c);
        if (dirty == null) {
          hwDirtyComponents.put(c,new Rectangle(x,y,w,h));
        }
 else {
          hwDirtyComponents.put(c,SwingUtilities.computeUnion(x,y,w,h,dirty));
        }
      }
      scheduleProcessingRunnable(appContext);
    }
  }
  void nativeQueueSurfaceDataRunnable(  AppContext appContext,  Component c,  Runnable r){
synchronized (this) {
      if (runnableList == null) {
        runnableList=new LinkedList<Runnable>();
      }
      runnableList.add(r);
    }
    scheduleProcessingRunnable(appContext);
  }
  /** 
 * Extends the dirty region for the specified component to include
 * the new region.
 * @return false if <code>c</code> is not yet marked dirty.
 */
  private synchronized boolean extendDirtyRegion(  Component c,  int x,  int y,  int w,  int h){
    Rectangle r=dirtyComponents.get(c);
    if (r != null) {
      SwingUtilities.computeUnion(x,y,w,h,r);
      return true;
    }
    return false;
  }
  /** 
 * Return the current dirty region for a component.
 * Return an empty rectangle if the component is not
 * dirty.
 */
  public Rectangle getDirtyRegion(  JComponent aComponent){
    RepaintManager delegate=getDelegate(aComponent);
    if (delegate != null) {
      return delegate.getDirtyRegion(aComponent);
    }
    Rectangle r;
synchronized (this) {
      r=dirtyComponents.get(aComponent);
    }
    if (r == null)     return new Rectangle(0,0,0,0);
 else     return new Rectangle(r);
  }
  /** 
 * Mark a component completely dirty. <b>aComponent</b> will be
 * completely painted during the next paintDirtyRegions() call.
 */
  public void markCompletelyDirty(  JComponent aComponent){
    RepaintManager delegate=getDelegate(aComponent);
    if (delegate != null) {
      delegate.markCompletelyDirty(aComponent);
      return;
    }
    addDirtyRegion(aComponent,0,0,Integer.MAX_VALUE,Integer.MAX_VALUE);
  }
  /** 
 * Mark a component completely clean. <b>aComponent</b> will not
 * get painted during the next paintDirtyRegions() call.
 */
  public void markCompletelyClean(  JComponent aComponent){
    RepaintManager delegate=getDelegate(aComponent);
    if (delegate != null) {
      delegate.markCompletelyClean(aComponent);
      return;
    }
synchronized (this) {
      dirtyComponents.remove(aComponent);
    }
  }
  /** 
 * Convenience method that returns true if <b>aComponent</b> will be completely
 * painted during the next paintDirtyRegions(). If computing dirty regions is
 * expensive for your component, use this method and avoid computing dirty region
 * if it return true.
 */
  public boolean isCompletelyDirty(  JComponent aComponent){
    RepaintManager delegate=getDelegate(aComponent);
    if (delegate != null) {
      return delegate.isCompletelyDirty(aComponent);
    }
    Rectangle r;
    r=getDirtyRegion(aComponent);
    if (r.width == Integer.MAX_VALUE && r.height == Integer.MAX_VALUE)     return true;
 else     return false;
  }
  /** 
 * Validate all of the components that have been marked invalid.
 * @see #addInvalidComponent
 */
  public void validateInvalidComponents(){
    java.util.List<Component> ic;
synchronized (this) {
      if (invalidComponents == null) {
        return;
      }
      ic=invalidComponents;
      invalidComponents=null;
    }
    int n=ic.size();
    for (int i=0; i < n; i++) {
      ic.get(i).validate();
    }
  }
  /** 
 * This is invoked to process paint requests.  It's needed
 * for backward compatability in so far as RepaintManager would previously
 * not see paint requests for top levels, so, we have to make sure
 * a subclass correctly paints any dirty top levels.
 */
  private void prePaintDirtyRegions(){
    Map<Component,Rectangle> dirtyComponents;
    java.util.List<Runnable> runnableList;
synchronized (this) {
      dirtyComponents=this.dirtyComponents;
      runnableList=this.runnableList;
      this.runnableList=null;
    }
    if (runnableList != null) {
      for (      Runnable runnable : runnableList) {
        runnable.run();
      }
    }
    paintDirtyRegions();
    if (dirtyComponents.size() > 0) {
      paintDirtyRegions(dirtyComponents);
    }
  }
  private void updateWindows(  Map<Component,Rectangle> dirtyComponents){
    Toolkit toolkit=Toolkit.getDefaultToolkit();
    if (!(toolkit instanceof SunToolkit && ((SunToolkit)toolkit).needUpdateWindow())) {
      return;
    }
    Set<Window> windows=new HashSet<Window>();
    Set<Component> dirtyComps=dirtyComponents.keySet();
    for (Iterator<Component> it=dirtyComps.iterator(); it.hasNext(); ) {
      Component dirty=it.next();
      Window window=dirty instanceof Window ? (Window)dirty : SwingUtilities.getWindowAncestor(dirty);
      if (window != null && !window.isOpaque()) {
        windows.add(window);
      }
    }
    for (    Window window : windows) {
      AWTAccessor.getWindowAccessor().updateWindow(window);
    }
  }
  boolean isPainting(){
    return painting;
  }
  /** 
 * Paint all of the components that have been marked dirty.
 * @see #addDirtyRegion
 */
  public void paintDirtyRegions(){
synchronized (this) {
      Map<Component,Rectangle> tmp=tmpDirtyComponents;
      tmpDirtyComponents=dirtyComponents;
      dirtyComponents=tmp;
      dirtyComponents.clear();
    }
    paintDirtyRegions(tmpDirtyComponents);
  }
  private void paintDirtyRegions(  Map<Component,Rectangle> tmpDirtyComponents){
    int i, count;
    java.util.List<Component> roots;
    Component dirtyComponent;
    count=tmpDirtyComponents.size();
    if (count == 0) {
      return;
    }
    Rectangle rect;
    int localBoundsX=0;
    int localBoundsY=0;
    int localBoundsH;
    int localBoundsW;
    Enumeration keys;
    roots=new ArrayList<Component>(count);
    for (    Component dirty : tmpDirtyComponents.keySet()) {
      collectDirtyComponents(tmpDirtyComponents,dirty,roots);
    }
    count=roots.size();
    painting=true;
    try {
      for (i=0; i < count; i++) {
        dirtyComponent=roots.get(i);
        rect=tmpDirtyComponents.get(dirtyComponent);
        localBoundsH=dirtyComponent.getHeight();
        localBoundsW=dirtyComponent.getWidth();
        SwingUtilities.computeIntersection(localBoundsX,localBoundsY,localBoundsW,localBoundsH,rect);
        if (dirtyComponent instanceof JComponent) {
          ((JComponent)dirtyComponent).paintImmediately(rect.x,rect.y,rect.width,rect.height);
        }
 else         if (dirtyComponent.isShowing()) {
          Graphics g=JComponent.safelyGetGraphics(dirtyComponent,dirtyComponent);
          if (g != null) {
            g.setClip(rect.x,rect.y,rect.width,rect.height);
            try {
              dirtyComponent.paint(g);
            }
  finally {
              g.dispose();
            }
          }
        }
        if (repaintRoot != null) {
          adjustRoots(repaintRoot,roots,i + 1);
          count=roots.size();
          paintManager.isRepaintingRoot=true;
          repaintRoot.paintImmediately(0,0,repaintRoot.getWidth(),repaintRoot.getHeight());
          paintManager.isRepaintingRoot=false;
          repaintRoot=null;
        }
      }
    }
  finally {
      painting=false;
    }
    updateWindows(tmpDirtyComponents);
    tmpDirtyComponents.clear();
  }
  /** 
 * Removes any components from roots that are children of
 * root.
 */
  private void adjustRoots(  JComponent root,  java.util.List<Component> roots,  int index){
    for (int i=roots.size() - 1; i >= index; i--) {
      Component c=roots.get(i);
      for (; ; ) {
        if (c == root || c == null || !(c instanceof JComponent)) {
          break;
        }
        c=c.getParent();
      }
      if (c == root) {
        roots.remove(i);
      }
    }
  }
  Rectangle tmp=new Rectangle();
  void collectDirtyComponents(  Map<Component,Rectangle> dirtyComponents,  Component dirtyComponent,  java.util.List<Component> roots){
    int dx, dy, rootDx, rootDy;
    Component component, rootDirtyComponent, parent;
    Rectangle cBounds;
    component=rootDirtyComponent=dirtyComponent;
    int x=dirtyComponent.getX();
    int y=dirtyComponent.getY();
    int w=dirtyComponent.getWidth();
    int h=dirtyComponent.getHeight();
    dx=rootDx=0;
    dy=rootDy=0;
    tmp.setBounds(dirtyComponents.get(dirtyComponent));
    SwingUtilities.computeIntersection(0,0,w,h,tmp);
    if (tmp.isEmpty()) {
      return;
    }
    for (; ; ) {
      if (!(component instanceof JComponent))       break;
      parent=component.getParent();
      if (parent == null)       break;
      component=parent;
      dx+=x;
      dy+=y;
      tmp.setLocation(tmp.x + x,tmp.y + y);
      x=component.getX();
      y=component.getY();
      w=component.getWidth();
      h=component.getHeight();
      tmp=SwingUtilities.computeIntersection(0,0,w,h,tmp);
      if (tmp.isEmpty()) {
        return;
      }
      if (dirtyComponents.get(component) != null) {
        rootDirtyComponent=component;
        rootDx=dx;
        rootDy=dy;
      }
    }
    if (dirtyComponent != rootDirtyComponent) {
      Rectangle r;
      tmp.setLocation(tmp.x + rootDx - dx,tmp.y + rootDy - dy);
      r=dirtyComponents.get(rootDirtyComponent);
      SwingUtilities.computeUnion(tmp.x,tmp.y,tmp.width,tmp.height,r);
    }
    if (!roots.contains(rootDirtyComponent))     roots.add(rootDirtyComponent);
  }
  /** 
 * Returns a string that displays and identifies this
 * object's properties.
 * @return a String representation of this object
 */
  public synchronized String toString(){
    StringBuffer sb=new StringBuffer();
    if (dirtyComponents != null)     sb.append("" + dirtyComponents);
    return sb.toString();
  }
  /** 
 * Return the offscreen buffer that should be used as a double buffer with
 * the component <code>c</code>.
 * By default there is a double buffer per RepaintManager.
 * The buffer might be smaller than <code>(proposedWidth,proposedHeight)</code>
 * This happens when the maximum double buffer size as been set for the receiving
 * repaint manager.
 */
  public Image getOffscreenBuffer(  Component c,  int proposedWidth,  int proposedHeight){
    RepaintManager delegate=getDelegate(c);
    if (delegate != null) {
      return delegate.getOffscreenBuffer(c,proposedWidth,proposedHeight);
    }
    return _getOffscreenBuffer(c,proposedWidth,proposedHeight);
  }
  /** 
 * Return a volatile offscreen buffer that should be used as a
 * double buffer with the specified component <code>c</code>.
 * The image returned will be an instance of VolatileImage, or null
 * if a VolatileImage object could not be instantiated.
 * This buffer might be smaller than <code>(proposedWidth,proposedHeight)</code>.
 * This happens when the maximum double buffer size has been set for this
 * repaint manager.
 * @see java.awt.image.VolatileImage
 * @since 1.4
 */
  public Image getVolatileOffscreenBuffer(  Component c,  int proposedWidth,  int proposedHeight){
    RepaintManager delegate=getDelegate(c);
    if (delegate != null) {
      return delegate.getVolatileOffscreenBuffer(c,proposedWidth,proposedHeight);
    }
    Window w=(c instanceof Window) ? (Window)c : SwingUtilities.getWindowAncestor(c);
    if (!w.isOpaque()) {
      Toolkit tk=Toolkit.getDefaultToolkit();
      if ((tk instanceof SunToolkit) && (((SunToolkit)tk).needUpdateWindow())) {
        return null;
      }
    }
    GraphicsConfiguration config=c.getGraphicsConfiguration();
    if (config == null) {
      config=GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
    }
    Dimension maxSize=getDoubleBufferMaximumSize();
    int width=proposedWidth < 1 ? 1 : (proposedWidth > maxSize.width ? maxSize.width : proposedWidth);
    int height=proposedHeight < 1 ? 1 : (proposedHeight > maxSize.height ? maxSize.height : proposedHeight);
    VolatileImage image=volatileMap.get(config);
    if (image == null || image.getWidth() < width || image.getHeight() < height) {
      if (image != null) {
        image.flush();
      }
      image=config.createCompatibleVolatileImage(width,height);
      volatileMap.put(config,image);
    }
    return image;
  }
  private Image _getOffscreenBuffer(  Component c,  int proposedWidth,  int proposedHeight){
    Dimension maxSize=getDoubleBufferMaximumSize();
    DoubleBufferInfo doubleBuffer;
    int width, height;
    Window w=(c instanceof Window) ? (Window)c : SwingUtilities.getWindowAncestor(c);
    if (!w.isOpaque()) {
      Toolkit tk=Toolkit.getDefaultToolkit();
      if ((tk instanceof SunToolkit) && (((SunToolkit)tk).needUpdateWindow())) {
        return null;
      }
    }
    if (standardDoubleBuffer == null) {
      standardDoubleBuffer=new DoubleBufferInfo();
    }
    doubleBuffer=standardDoubleBuffer;
    width=proposedWidth < 1 ? 1 : (proposedWidth > maxSize.width ? maxSize.width : proposedWidth);
    height=proposedHeight < 1 ? 1 : (proposedHeight > maxSize.height ? maxSize.height : proposedHeight);
    if (doubleBuffer.needsReset || (doubleBuffer.image != null && (doubleBuffer.size.width < width || doubleBuffer.size.height < height))) {
      doubleBuffer.needsReset=false;
      if (doubleBuffer.image != null) {
        doubleBuffer.image.flush();
        doubleBuffer.image=null;
      }
      width=Math.max(doubleBuffer.size.width,width);
      height=Math.max(doubleBuffer.size.height,height);
    }
    Image result=doubleBuffer.image;
    if (doubleBuffer.image == null) {
      result=c.createImage(width,height);
      doubleBuffer.size=new Dimension(width,height);
      if (c instanceof JComponent) {
        ((JComponent)c).setCreatedDoubleBuffer(true);
        doubleBuffer.image=result;
      }
    }
    return result;
  }
  /** 
 * Set the maximum double buffer size. 
 */
  public void setDoubleBufferMaximumSize(  Dimension d){
    doubleBufferMaxSize=d;
    if (doubleBufferMaxSize == null) {
      clearImages();
    }
 else {
      clearImages(d.width,d.height);
    }
  }
  private void clearImages(){
    clearImages(0,0);
  }
  private void clearImages(  int width,  int height){
    if (standardDoubleBuffer != null && standardDoubleBuffer.image != null) {
      if (standardDoubleBuffer.image.getWidth(null) > width || standardDoubleBuffer.image.getHeight(null) > height) {
        standardDoubleBuffer.image.flush();
        standardDoubleBuffer.image=null;
      }
    }
    Iterator gcs=volatileMap.keySet().iterator();
    while (gcs.hasNext()) {
      GraphicsConfiguration gc=(GraphicsConfiguration)gcs.next();
      VolatileImage image=volatileMap.get(gc);
      if (image.getWidth() > width || image.getHeight() > height) {
        image.flush();
        gcs.remove();
      }
    }
  }
  /** 
 * Returns the maximum double buffer size.
 * @return a Dimension object representing the maximum size
 */
  public Dimension getDoubleBufferMaximumSize(){
    if (doubleBufferMaxSize == null) {
      try {
        Rectangle virtualBounds=new Rectangle();
        GraphicsEnvironment ge=GraphicsEnvironment.getLocalGraphicsEnvironment();
        for (        GraphicsDevice gd : ge.getScreenDevices()) {
          GraphicsConfiguration gc=gd.getDefaultConfiguration();
          virtualBounds=virtualBounds.union(gc.getBounds());
        }
        doubleBufferMaxSize=new Dimension(virtualBounds.width,virtualBounds.height);
      }
 catch (      HeadlessException e) {
        doubleBufferMaxSize=new Dimension(Integer.MAX_VALUE,Integer.MAX_VALUE);
      }
    }
    return doubleBufferMaxSize;
  }
  /** 
 * Enables or disables double buffering in this RepaintManager.
 * CAUTION: The default value for this property is set for optimal
 * paint performance on the given platform and it is not recommended
 * that programs modify this property directly.
 * @param aFlag  true to activate double buffering
 * @see #isDoubleBufferingEnabled
 */
  public void setDoubleBufferingEnabled(  boolean aFlag){
    doubleBufferingEnabled=aFlag;
    PaintManager paintManager=getPaintManager();
    if (!aFlag && paintManager.getClass() != PaintManager.class) {
      setPaintManager(new PaintManager());
    }
  }
  /** 
 * Returns true if this RepaintManager is double buffered.
 * The default value for this property may vary from platform
 * to platform.  On platforms where native double buffering
 * is supported in the AWT, the default value will be <code>false</code>
 * to avoid unnecessary buffering in Swing.
 * On platforms where native double buffering is not supported,
 * the default value will be <code>true</code>.
 * @return true if this object is double buffered
 */
  public boolean isDoubleBufferingEnabled(){
    return doubleBufferingEnabled;
  }
  /** 
 * This resets the double buffer. Actually, it marks the double buffer
 * as invalid, the double buffer will then be recreated on the next
 * invocation of getOffscreenBuffer.
 */
  void resetDoubleBuffer(){
    if (standardDoubleBuffer != null) {
      standardDoubleBuffer.needsReset=true;
    }
  }
  /** 
 * This resets the volatile double buffer.
 */
  void resetVolatileDoubleBuffer(  GraphicsConfiguration gc){
    Image image=volatileMap.remove(gc);
    if (image != null) {
      image.flush();
    }
  }
  /** 
 * Returns true if we should use the <code>Image</code> returned
 * from <code>getVolatileOffscreenBuffer</code> to do double buffering.
 */
  boolean useVolatileDoubleBuffer(){
    return volatileImageBufferEnabled;
  }
  /** 
 * Returns true if the current thread is the thread painting.  This
 * will return false if no threads are painting.
 */
  private synchronized boolean isPaintingThread(){
    return (Thread.currentThread() == paintThread);
  }
  /** 
 * Paints a region of a component
 * @param paintingComponent Component to paint
 * @param bufferComponent Component to obtain buffer for
 * @param g Graphics to paint to
 * @param x X-coordinate
 * @param y Y-coordinate
 * @param w Width
 * @param h Height
 */
  void paint(  JComponent paintingComponent,  JComponent bufferComponent,  Graphics g,  int x,  int y,  int w,  int h){
    PaintManager paintManager=getPaintManager();
    if (!isPaintingThread()) {
      if (paintManager.getClass() != PaintManager.class) {
        paintManager=new PaintManager();
        paintManager.repaintManager=this;
      }
    }
    if (!paintManager.paint(paintingComponent,bufferComponent,g,x,y,w,h)) {
      g.setClip(x,y,w,h);
      paintingComponent.paintToOffscreen(g,x,y,w,h,x + w,y + h);
    }
  }
  /** 
 * Does a copy area on the specified region.
 * @param clip Whether or not the copyArea needs to be clipped to the
 * Component's bounds.
 */
  void copyArea(  JComponent c,  Graphics g,  int x,  int y,  int w,  int h,  int deltaX,  int deltaY,  boolean clip){
    getPaintManager().copyArea(c,g,x,y,w,h,deltaX,deltaY,clip);
  }
  /** 
 * Invoked prior to any paint/copyArea method calls.  This will
 * be followed by an invocation of <code>endPaint</code>.
 * <b>WARNING</b>: Callers of this method need to wrap the call
 * in a <code>try/finally</code>, otherwise if an exception is thrown
 * during the course of painting the RepaintManager may
 * be left in a state in which the screen is not updated, eg:
 * <pre>
 * repaintManager.beginPaint();
 * try {
 * repaintManager.paint(...);
 * } finally {
 * repaintManager.endPaint();
 * }
 * </pre>
 */
  void beginPaint(){
    boolean multiThreadedPaint=false;
    int paintDepth;
    Thread currentThread=Thread.currentThread();
synchronized (this) {
      paintDepth=this.paintDepth;
      if (paintThread == null || currentThread == paintThread) {
        paintThread=currentThread;
        this.paintDepth++;
      }
 else {
        multiThreadedPaint=true;
      }
    }
    if (!multiThreadedPaint && paintDepth == 0) {
      getPaintManager().beginPaint();
    }
  }
  /** 
 * Invoked after <code>beginPaint</code> has been invoked.
 */
  void endPaint(){
    if (isPaintingThread()) {
      PaintManager paintManager=null;
synchronized (this) {
        if (--paintDepth == 0) {
          paintManager=getPaintManager();
        }
      }
      if (paintManager != null) {
        paintManager.endPaint();
synchronized (this) {
          paintThread=null;
        }
      }
    }
  }
  /** 
 * If possible this will show a previously rendered portion of
 * a Component.  If successful, this will return true, otherwise false.
 * <p>
 * WARNING: This method is invoked from the native toolkit thread, be
 * very careful as to what methods this invokes!
 */
  boolean show(  Container c,  int x,  int y,  int w,  int h){
    return getPaintManager().show(c,x,y,w,h);
  }
  /** 
 * Invoked when the doubleBuffered or useTrueDoubleBuffering
 * properties of a JRootPane change.  This may come in on any thread.
 */
  void doubleBufferingChanged(  JRootPane rootPane){
    getPaintManager().doubleBufferingChanged(rootPane);
  }
  /** 
 * Sets the <code>PaintManager</code> that is used to handle all
 * double buffered painting.
 * @param paintManager The PaintManager to use.  Passing in null indicates
 * the fallback PaintManager should be used.
 */
  void setPaintManager(  PaintManager paintManager){
    if (paintManager == null) {
      paintManager=new PaintManager();
    }
    PaintManager oldPaintManager;
synchronized (this) {
      oldPaintManager=this.paintManager;
      this.paintManager=paintManager;
      paintManager.repaintManager=this;
    }
    if (oldPaintManager != null) {
      oldPaintManager.dispose();
    }
  }
  private synchronized PaintManager getPaintManager(){
    if (paintManager == null) {
      PaintManager paintManager=null;
      if (doubleBufferingEnabled && !nativeDoubleBuffering) {
switch (bufferStrategyType) {
case BUFFER_STRATEGY_NOT_SPECIFIED:
          Toolkit tk=Toolkit.getDefaultToolkit();
        if (tk instanceof SunToolkit) {
          SunToolkit stk=(SunToolkit)tk;
          if (stk.useBufferPerWindow()) {
            paintManager=new BufferStrategyPaintManager();
          }
        }
      break;
case BUFFER_STRATEGY_SPECIFIED_ON:
    paintManager=new BufferStrategyPaintManager();
  break;
default :
break;
}
}
setPaintManager(paintManager);
}
return paintManager;
}
private void scheduleProcessingRunnable(){
scheduleProcessingRunnable(AppContext.getAppContext());
}
private void scheduleProcessingRunnable(AppContext context){
if (processingRunnable.markPending()) {
Toolkit tk=Toolkit.getDefaultToolkit();
if (tk instanceof SunToolkit) {
SunToolkit.getSystemEventQueueImplPP(context).postEvent(new InvocationEvent(Toolkit.getDefaultToolkit(),processingRunnable));
}
 else {
Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(new InvocationEvent(Toolkit.getDefaultToolkit(),processingRunnable));
}
}
}
/** 
 * PaintManager is used to handle all double buffered painting for
 * Swing.  Subclasses should call back into the JComponent method
 * <code>paintToOffscreen</code> to handle the actual painting.
 */
static class PaintManager {
/** 
 * RepaintManager the PaintManager has been installed on.
 */
protected RepaintManager repaintManager;
boolean isRepaintingRoot;
/** 
 * Paints a region of a component
 * @param paintingComponent Component to paint
 * @param bufferComponent Component to obtain buffer for
 * @param g Graphics to paint to
 * @param x X-coordinate
 * @param y Y-coordinate
 * @param w Width
 * @param h Height
 * @return true if painting was successful.
 */
public boolean paint(JComponent paintingComponent,JComponent bufferComponent,Graphics g,int x,int y,int w,int h){
boolean paintCompleted=false;
Image offscreen;
if (repaintManager.useVolatileDoubleBuffer() && (offscreen=getValidImage(repaintManager.getVolatileOffscreenBuffer(bufferComponent,w,h))) != null) {
VolatileImage vImage=(java.awt.image.VolatileImage)offscreen;
GraphicsConfiguration gc=bufferComponent.getGraphicsConfiguration();
for (int i=0; !paintCompleted && i < RepaintManager.VOLATILE_LOOP_MAX; i++) {
if (vImage.validate(gc) == VolatileImage.IMAGE_INCOMPATIBLE) {
repaintManager.resetVolatileDoubleBuffer(gc);
offscreen=repaintManager.getVolatileOffscreenBuffer(bufferComponent,w,h);
vImage=(java.awt.image.VolatileImage)offscreen;
}
paintDoubleBuffered(paintingComponent,vImage,g,x,y,w,h);
paintCompleted=!vImage.contentsLost();
}
}
if (!paintCompleted && (offscreen=getValidImage(repaintManager.getOffscreenBuffer(bufferComponent,w,h))) != null) {
paintDoubleBuffered(paintingComponent,offscreen,g,x,y,w,h);
paintCompleted=true;
}
return paintCompleted;
}
/** 
 * Does a copy area on the specified region.
 */
public void copyArea(JComponent c,Graphics g,int x,int y,int w,int h,int deltaX,int deltaY,boolean clip){
g.copyArea(x,y,w,h,deltaX,deltaY);
}
/** 
 * Invoked prior to any calls to paint or copyArea.
 */
public void beginPaint(){
}
/** 
 * Invoked to indicate painting has been completed.
 */
public void endPaint(){
}
/** 
 * Shows a region of a previously rendered component.  This
 * will return true if successful, false otherwise.  The default
 * implementation returns false.
 */
public boolean show(Container c,int x,int y,int w,int h){
return false;
}
/** 
 * Invoked when the doubleBuffered or useTrueDoubleBuffering
 * properties of a JRootPane change.  This may come in on any thread.
 */
public void doubleBufferingChanged(JRootPane rootPane){
}
/** 
 * Paints a portion of a component to an offscreen buffer.
 */
protected void paintDoubleBuffered(JComponent c,Image image,Graphics g,int clipX,int clipY,int clipW,int clipH){
Graphics osg=image.getGraphics();
int bw=Math.min(clipW,image.getWidth(null));
int bh=Math.min(clipH,image.getHeight(null));
int x, y, maxx, maxy;
try {
for (x=clipX, maxx=clipX + clipW; x < maxx; x+=bw) {
for (y=clipY, maxy=clipY + clipH; y < maxy; y+=bh) {
osg.translate(-x,-y);
osg.setClip(x,y,bw,bh);
c.paintToOffscreen(osg,x,y,bw,bh,maxx,maxy);
g.setClip(x,y,bw,bh);
g.drawImage(image,x,y,c);
osg.translate(x,y);
}
}
}
  finally {
osg.dispose();
}
}
/** 
 * If <code>image</code> is non-null with a positive size it
 * is returned, otherwise null is returned.
 */
private Image getValidImage(Image image){
if (image != null && image.getWidth(null) > 0 && image.getHeight(null) > 0) {
return image;
}
return null;
}
/** 
 * Schedules a repaint for the specified component.  This differs
 * from <code>root.repaint</code> in that if the RepaintManager is
 * currently processing paint requests it'll process this request
 * with the current set of requests.
 */
protected void repaintRoot(JComponent root){
assert (repaintManager.repaintRoot == null);
if (repaintManager.painting) {
repaintManager.repaintRoot=root;
}
 else {
root.repaint();
}
}
/** 
 * Returns true if the component being painted is the root component
 * that was previously passed to <code>repaintRoot</code>.
 */
protected boolean isRepaintingRoot(){
return isRepaintingRoot;
}
/** 
 * Cleans up any state.  After invoked the PaintManager will no
 * longer be used anymore.
 */
protected void dispose(){
}
}
private class DoubleBufferInfo {
public Image image;
public Dimension size;
public boolean needsReset=false;
}
/** 
 * Listener installed to detect display changes. When display changes,
 * schedules a callback to notify all RepaintManagers of the display
 * changes. Only one DisplayChangedHandler is ever installed. The
 * singleton instance will schedule notification for all AppContexts.
 */
private static final class DisplayChangedHandler implements DisplayChangedListener {
public void displayChanged(){
scheduleDisplayChanges();
}
public void paletteChanged(){
}
private void scheduleDisplayChanges(){
for (Object c : AppContext.getAppContexts()) {
AppContext context=(AppContext)c;
synchronized (context) {
if (!context.isDisposed()) {
EventQueue eventQueue=(EventQueue)context.get(AppContext.EVENT_QUEUE_KEY);
if (eventQueue != null) {
  eventQueue.postEvent(new InvocationEvent(Toolkit.getDefaultToolkit(),new DisplayChangedRunnable()));
}
}
}
}
}
}
private static final class DisplayChangedRunnable implements Runnable {
public void run(){
RepaintManager.currentManager((JComponent)null).displayChanged();
}
}
/** 
 * Runnable used to process all repaint/revalidate requests.
 */
private final class ProcessingRunnable implements Runnable {
private boolean pending;
/** 
 * Marks this processing runnable as pending. If this was not
 * already marked as pending, true is returned.
 */
public synchronized boolean markPending(){
if (!pending) {
pending=true;
return true;
}
return false;
}
public void run(){
synchronized (this) {
pending=false;
}
scheduleHeavyWeightPaints();
validateInvalidComponents();
prePaintDirtyRegions();
}
}
private RepaintManager getDelegate(Component c){
RepaintManager delegate=SwingUtilities3.getDelegateRepaintManager(c);
if (this == delegate) {
delegate=null;
}
return delegate;
}
}