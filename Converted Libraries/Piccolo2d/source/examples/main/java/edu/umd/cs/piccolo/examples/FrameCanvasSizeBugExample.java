package edu.umd.cs.piccolo.examples;
import java.awt.Color;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.PFrame;
/** 
 * This example demonstrates a bug with setting the size
 * of a PFrame.  See http://code.google.com/p/piccolo2d/issues/detail?id=141.
 */
public class FrameCanvasSizeBugExample extends PFrame {
  /** 
 * Default serial version UID. 
 */
  private static final long serialVersionUID=1L;
  /** 
 * Create a new frame canvas size bug example.
 */
  public FrameCanvasSizeBugExample(){
    this(null);
  }
  /** 
 * Create a new frame canvas size bug example for the specified canvas.
 * @param canvas canvas for this frame canvas size bug example
 */
  public FrameCanvasSizeBugExample(  final PCanvas canvas){
    super("FrameCanvasSizeBugExample",false,canvas);
  }
  /** 
 * {@inheritDoc} 
 */
  public void initialize(){
    PText label=new PText("Note white at border S and E\nIt goes away when frame is resized");
    label.setOffset(200,340);
    getCanvas().getLayer().addChild(label);
    getCanvas().setBackground(Color.PINK);
    getCanvas().setOpaque(true);
    setSize(410,410);
  }
  /** 
 * Main.
 * @param args command line arguments, ignored
 */
  public static void main(  final String[] args){
    new FrameCanvasSizeBugExample();
  }
}
