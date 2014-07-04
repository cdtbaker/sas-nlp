package edu.umd.cs.piccolox.pswing;
import java.awt.Component;
import java.util.Vector;
import javax.swing.JComponent;
import javax.swing.RepaintManager;
import edu.umd.cs.piccolo.util.PBounds;
/** 
 * This RepaintManager replaces the default Swing implementation, and is used to
 * repaint dirty regions of PSwing components and make sure the PSwings have
 * the appropriate size.
 * <p>
 * This is an internal class used by Piccolo to support Swing components in
 * Piccolo. This should not be instantiated, though all the public methods of
 * javax.swing.RepaintManager may still be called and perform in the expected
 * manner.
 * </p>
 * <p>
 * PBasicRepaint Manager is an extension of RepaintManager that traps those
 * repaints called by the Swing components that have been added to the PCanvas
 * and passes these repaints to the PSwing rather than up the
 * component hierarchy as usually happens.
 * </p>
 * <p>
 * Also traps invalidate calls made by the Swing components added to the PCanvas
 * to reshape the corresponding PSwing.
 * </p>
 * <p>
 * Also keeps a list of PSwings that are painting. This disables repaint until
 * the component has finished painting. This is to address a problem introduced
 * by Swing's CellRendererPane which is itself a work-around. The problem is
 * that JTable's, JTree's, and JList's cell renderers need to be validated
 * before repaint. Since we have to repaint the entire Swing component hierarchy
 * (in the case of a PSwing), this causes an infinite loop. So we introduce the
 * restriction that no repaints can be triggered by a call to paint.
 * </p>
 * @author Benjamin B. Bederson
 * @author Lance E. Good
 * @author Sam R. Reid
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PSwingRepaintManager extends RepaintManager {
  private final Vector paintingComponents=new Vector();
  /** 
 * Locks repaint for a particular (Swing) component displayed by PCanvas.
 * @param c The component for which the repaint is to be locked
 */
  public void lockRepaint(  final JComponent c){
    paintingComponents.addElement(c);
  }
  /** 
 * Unlocks repaint for a particular (Swing) component displayed by PCanvas.
 * @param c The component for which the repaint is to be unlocked
 */
  public void unlockRepaint(  final JComponent c){
    paintingComponents.remove(c);
  }
  /** 
 * Returns true if repaint is currently locked for a component and false
 * otherwise.
 * @param c The component for which the repaint status is desired
 * @return Whether the component is currently painting
 */
  public boolean isPainting(  final JComponent c){
    return paintingComponents.contains(c);
  }
  /** 
 * This is the method "repaint" now calls in the Swing components.
 * Overridden to capture repaint calls from those Swing components which are
 * being used as Piccolo visual components and to call the Piccolo repaint
 * mechanism rather than the traditional Component hierarchy repaint
 * mechanism. Otherwise, behaves like the superclass.
 * @param component Component to be repainted
 * @param x X coordinate of the dirty region in the component
 * @param y Y coordinate of the dirty region in the component
 * @param width Width of the dirty region in the component
 * @param height Height of the dirty region in the component
 */
  public synchronized void addDirtyRegion(  final JComponent component,  final int x,  final int y,  final int width,  final int height){
    boolean captureRepaint=false;
    JComponent childComponent=null;
    int captureX=x;
    int captureY=y;
    for (Component comp=component; comp != null && comp.isLightweight(); comp=comp.getParent()) {
      if (comp.getParent() instanceof PSwingCanvas.ChildWrapper) {
        captureRepaint=true;
        childComponent=(JComponent)comp;
        break;
      }
 else {
        captureX+=comp.getLocation().getX();
        captureY+=comp.getLocation().getY();
      }
    }
    if (captureRepaint) {
      if (!isPainting(childComponent)) {
        final double repaintW=Math.min(childComponent.getWidth() - captureX,width);
        final double repaintH=Math.min(childComponent.getHeight() - captureY,height);
        getPSwing(childComponent).repaint(new PBounds(captureX,captureY,repaintW,repaintH));
      }
    }
 else {
      super.addDirtyRegion(component,x,y,width,height);
    }
  }
  /** 
 * This is the method "invalidate" calls in the Swing components. Overridden
 * to capture invalidation calls from those Swing components being used as
 * Piccolo visual components and to update Piccolo's visual component
 * wrapper bounds (these are stored separately from the Swing component).
 * Otherwise, behaves like the superclass.
 * @param invalidComponent The Swing component that needs validation
 */
  public synchronized void addInvalidComponent(  final JComponent invalidComponent){
    if (invalidComponent.getParent() == null || !(invalidComponent.getParent() instanceof PSwingCanvas.ChildWrapper)) {
      super.addInvalidComponent(invalidComponent);
    }
 else {
      invalidComponent.validate();
      getPSwing(invalidComponent).updateBounds();
    }
  }
  /** 
 * Obtains the PSwing associated with the specified component.
 * @param component the component for which to return the associated PSwing
 * @return the associated PSwing
 */
  private PSwing getPSwing(  JComponent component){
    return (PSwing)component.getClientProperty(PSwing.PSWING_PROPERTY);
  }
}
