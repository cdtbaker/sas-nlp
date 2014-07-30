package javax.swing;
import java.awt.*;
import sun.awt.ModalExclude;
import sun.awt.SunToolkit;
/** 
 * Popups are used to display a <code>Component</code> to the user, typically
 * on top of all the other <code>Component</code>s in a particular containment
 * hierarchy. <code>Popup</code>s have a very small life cycle. Once you
 * have obtained a <code>Popup</code>, and hidden it (invoked the
 * <code>hide</code> method), you should no longer
 * invoke any methods on it. This allows the <code>PopupFactory</code> to cache
 * <code>Popup</code>s for later use.
 * <p>
 * The general contract is that if you need to change the size of the
 * <code>Component</code>, or location of the <code>Popup</code>, you should
 * obtain a new <code>Popup</code>.
 * <p>
 * <code>Popup</code> does not descend from <code>Component</code>, rather
 * implementations of <code>Popup</code> are responsible for creating
 * and maintaining their own <code>Component</code>s to render the
 * requested <code>Component</code> to the user.
 * <p>
 * You typically do not explicitly create an instance of <code>Popup</code>,
 * instead obtain one from a <code>PopupFactory</code>.
 * @see PopupFactory
 * @since 1.4
 */
public class Popup {
  /** 
 * The Component representing the Popup.
 */
  private Component component;
  /** 
 * Creates a <code>Popup</code> for the Component <code>owner</code>
 * containing the Component <code>contents</code>. <code>owner</code>
 * is used to determine which <code>Window</code> the new
 * <code>Popup</code> will parent the <code>Component</code> the
 * <code>Popup</code> creates to.
 * A null <code>owner</code> implies there is no valid parent.
 * <code>x</code> and
 * <code>y</code> specify the preferred initial location to place
 * the <code>Popup</code> at. Based on screen size, or other paramaters,
 * the <code>Popup</code> may not display at <code>x</code> and
 * <code>y</code>.
 * @param owner    Component mouse coordinates are relative to, may be null
 * @param contents Contents of the Popup
 * @param x        Initial x screen coordinate
 * @param y        Initial y screen coordinate
 * @exception IllegalArgumentException if contents is null
 */
  protected Popup(  Component owner,  Component contents,  int x,  int y){
    this();
    if (contents == null) {
      throw new IllegalArgumentException("Contents must be non-null");
    }
    reset(owner,contents,x,y);
  }
  /** 
 * Creates a <code>Popup</code>. This is provided for subclasses.
 */
  protected Popup(){
  }
  /** 
 * Makes the <code>Popup</code> visible. If the <code>Popup</code> is
 * currently visible, this has no effect.
 */
  public void show(){
    Component component=getComponent();
    if (component != null) {
      component.show();
    }
  }
  /** 
 * Hides and disposes of the <code>Popup</code>. Once a <code>Popup</code>
 * has been disposed you should no longer invoke methods on it. A
 * <code>dispose</code>d <code>Popup</code> may be reclaimed and later used
 * based on the <code>PopupFactory</code>. As such, if you invoke methods
 * on a <code>disposed</code> <code>Popup</code>, indeterminate
 * behavior will result.
 */
  public void hide(){
    Component component=getComponent();
    if (component instanceof JWindow) {
      component.hide();
      ((JWindow)component).getContentPane().removeAll();
    }
    dispose();
  }
  /** 
 * Frees any resources the <code>Popup</code> may be holding onto.
 */
  void dispose(){
    Component component=getComponent();
    Window window=SwingUtilities.getWindowAncestor(component);
    if (component instanceof JWindow) {
      ((Window)component).dispose();
      component=null;
    }
    if (window instanceof DefaultFrame) {
      window.dispose();
    }
  }
  /** 
 * Resets the <code>Popup</code> to an initial state.
 */
  void reset(  Component owner,  Component contents,  int ownerX,  int ownerY){
    if (getComponent() == null) {
      component=createComponent(owner);
    }
    Component c=getComponent();
    if (c instanceof JWindow) {
      JWindow component=(JWindow)getComponent();
      component.setLocation(ownerX,ownerY);
      component.getContentPane().add(contents,BorderLayout.CENTER);
      component.invalidate();
      component.validate();
      if (component.isVisible()) {
        pack();
      }
    }
  }
  /** 
 * Causes the <code>Popup</code> to be sized to fit the preferred size
 * of the <code>Component</code> it contains.
 */
  void pack(){
    Component component=getComponent();
    if (component instanceof Window) {
      ((Window)component).pack();
    }
  }
  /** 
 * Returns the <code>Window</code> to use as the parent of the
 * <code>Window</code> created for the <code>Popup</code>. This creates
 * a new <code>DefaultFrame</code>, if necessary.
 */
  private Window getParentWindow(  Component owner){
    Window window=null;
    if (owner instanceof Window) {
      window=(Window)owner;
    }
 else     if (owner != null) {
      window=SwingUtilities.getWindowAncestor(owner);
    }
    if (window == null) {
      window=new DefaultFrame();
    }
    return window;
  }
  /** 
 * Creates the Component to use as the parent of the <code>Popup</code>.
 * The default implementation creates a <code>Window</code>, subclasses
 * should override.
 */
  Component createComponent(  Component owner){
    if (GraphicsEnvironment.isHeadless()) {
      return null;
    }
    return new HeavyWeightWindow(getParentWindow(owner));
  }
  /** 
 * Returns the <code>Component</code> returned from
 * <code>createComponent</code> that will hold the <code>Popup</code>.
 */
  Component getComponent(){
    return component;
  }
  /** 
 * Component used to house window.
 */
static class HeavyWeightWindow extends JWindow implements ModalExclude {
    HeavyWeightWindow(    Window parent){
      super(parent);
      setFocusableWindowState(false);
      setType(Window.Type.POPUP);
      getRootPane().setUseTrueDoubleBuffering(false);
      try {
        setAlwaysOnTop(true);
      }
 catch (      SecurityException se) {
      }
    }
    public void update(    Graphics g){
      paint(g);
    }
    public void show(){
      this.pack();
      if (getWidth() > 0 && getHeight() > 0) {
        super.show();
      }
    }
  }
  /** 
 * Used if no valid Window ancestor of the supplied owner is found.
 * <p>
 * PopupFactory uses this as a way to know when the Popup shouldn't
 * be cached based on the Window.
 */
static class DefaultFrame extends Frame {
  }
}