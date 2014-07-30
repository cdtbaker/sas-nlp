package javax.swing;
import java.awt.LayoutManager;
import java.awt.Component;
import java.awt.Container;
import java.awt.Rectangle;
import java.awt.Point;
import java.awt.Dimension;
import java.awt.Insets;
import java.io.Serializable;
/** 
 * The default layout manager for <code>JViewport</code>.
 * <code>ViewportLayout</code> defines
 * a policy for layout that should be useful for most applications.
 * The viewport makes its view the same size as the viewport,
 * however it will not make the view smaller than its minimum size.
 * As the viewport grows the view is kept bottom justified until
 * the entire view is visible, subsequently the view is kept top
 * justified.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases. The current serialization support is
 * appropriate for short term storage or RMI between applications running
 * the same version of Swing.  As of 1.4, support for long term storage
 * of all JavaBeans<sup><font size="-2">TM</font></sup>
 * has been added to the <code>java.beans</code> package.
 * Please see {@link java.beans.XMLEncoder}.
 * @author Hans Muller
 */
public class ViewportLayout implements LayoutManager, Serializable {
  static ViewportLayout SHARED_INSTANCE=new ViewportLayout();
  /** 
 * Adds the specified component to the layout. Not used by this class.
 * @param name the name of the component
 * @param c the the component to be added
 */
  public void addLayoutComponent(  String name,  Component c){
  }
  /** 
 * Removes the specified component from the layout. Not used by
 * this class.
 * @param c the component to remove
 */
  public void removeLayoutComponent(  Component c){
  }
  /** 
 * Returns the preferred dimensions for this layout given the components
 * in the specified target container.
 * @param parent the component which needs to be laid out
 * @return a <code>Dimension</code> object containing the
 * preferred dimensions
 * @see #minimumLayoutSize
 */
  public Dimension preferredLayoutSize(  Container parent){
    Component view=((JViewport)parent).getView();
    if (view == null) {
      return new Dimension(0,0);
    }
 else     if (view instanceof Scrollable) {
      return ((Scrollable)view).getPreferredScrollableViewportSize();
    }
 else {
      return view.getPreferredSize();
    }
  }
  /** 
 * Returns the minimum dimensions needed to layout the components
 * contained in the specified target container.
 * @param parent the component which needs to be laid out
 * @return a <code>Dimension</code> object containing the minimum
 * dimensions
 * @see #preferredLayoutSize
 */
  public Dimension minimumLayoutSize(  Container parent){
    return new Dimension(4,4);
  }
  /** 
 * Called by the AWT when the specified container needs to be laid out.
 * @param parent  the container to lay out
 * @exception AWTError  if the target isn't the container specified to the
 * <code>BoxLayout</code> constructor
 */
  public void layoutContainer(  Container parent){
    JViewport vp=(JViewport)parent;
    Component view=vp.getView();
    Scrollable scrollableView=null;
    if (view == null) {
      return;
    }
 else     if (view instanceof Scrollable) {
      scrollableView=(Scrollable)view;
    }
    Insets insets=vp.getInsets();
    Dimension viewPrefSize=view.getPreferredSize();
    Dimension vpSize=vp.getSize();
    Dimension extentSize=vp.toViewCoordinates(vpSize);
    Dimension viewSize=new Dimension(viewPrefSize);
    if (scrollableView != null) {
      if (scrollableView.getScrollableTracksViewportWidth()) {
        viewSize.width=vpSize.width;
      }
      if (scrollableView.getScrollableTracksViewportHeight()) {
        viewSize.height=vpSize.height;
      }
    }
    Point viewPosition=vp.getViewPosition();
    if (scrollableView == null || vp.getParent() == null || vp.getParent().getComponentOrientation().isLeftToRight()) {
      if ((viewPosition.x + extentSize.width) > viewSize.width) {
        viewPosition.x=Math.max(0,viewSize.width - extentSize.width);
      }
    }
 else {
      if (extentSize.width > viewSize.width) {
        viewPosition.x=viewSize.width - extentSize.width;
      }
 else {
        viewPosition.x=Math.max(0,Math.min(viewSize.width - extentSize.width,viewPosition.x));
      }
    }
    if ((viewPosition.y + extentSize.height) > viewSize.height) {
      viewPosition.y=Math.max(0,viewSize.height - extentSize.height);
    }
    if (scrollableView == null) {
      if ((viewPosition.x == 0) && (vpSize.width > viewPrefSize.width)) {
        viewSize.width=vpSize.width;
      }
      if ((viewPosition.y == 0) && (vpSize.height > viewPrefSize.height)) {
        viewSize.height=vpSize.height;
      }
    }
    vp.setViewPosition(viewPosition);
    vp.setViewSize(viewSize);
  }
}