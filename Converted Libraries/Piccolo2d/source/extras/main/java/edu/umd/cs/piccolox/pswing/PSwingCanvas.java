package edu.umd.cs.piccolox.pswing;
import java.awt.Dimension;
import javax.swing.JComponent;
import javax.swing.RepaintManager;
import edu.umd.cs.piccolo.PCanvas;
/** 
 * The <b>PSwingCanvas</b> is a PCanvas that can display Swing components with
 * the PSwing adapter.
 * @author Benjamin B. Bederson
 * @author Sam R. Reid
 * @author Lance E. Good
 */
public class PSwingCanvas extends PCanvas {
  private static final long serialVersionUID=1L;
  /** 
 * Key used to store the "Swing Wrapper" as an attribute of the PSwing node. 
 */
  public static final String SWING_WRAPPER_KEY="Swing Wrapper";
  private final ChildWrapper swingWrapper;
  /** 
 * Construct a new PSwingCanvas.
 */
  public PSwingCanvas(){
    swingWrapper=new ChildWrapper();
    add(swingWrapper);
    initRepaintManager();
    new PSwingEventHandler(this,getCamera()).setActive(true);
  }
  private void initRepaintManager(){
    final RepaintManager repaintManager=RepaintManager.currentManager(this);
    if (!(repaintManager instanceof PSwingRepaintManager)) {
      RepaintManager.setCurrentManager(new PSwingRepaintManager());
    }
  }
  JComponent getSwingWrapper(){
    return swingWrapper;
  }
  void addPSwing(  final PSwing pSwing){
    swingWrapper.add(pSwing.getComponent());
  }
  void removePSwing(  final PSwing pSwing){
    swingWrapper.remove(pSwing.getComponent());
  }
  /** 
 * JComponent wrapper for a PSwingCanvas. Used by PSwingRepaintManager.
 */
static class ChildWrapper extends JComponent {
    /** 
 */
    private static final long serialVersionUID=1L;
    /** 
 * Create a new JComponent wrapper for the specified PSwingCanvas.
 */
    public ChildWrapper(){
      setSize(new Dimension(0,0));
      setPreferredSize(new Dimension(0,0));
      putClientProperty(SWING_WRAPPER_KEY,SWING_WRAPPER_KEY);
    }
  }
}
