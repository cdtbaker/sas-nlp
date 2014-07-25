package edu.umd.cs.piccolo.examples;
import java.awt.Color;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.PFrame;
import edu.umd.cs.piccolox.nodes.PComposite;
/** 
 * This example shows how to create a composite node. A composite node is a
 * group of nodes that behave as a single node when interacted with.
 */
public class CompositeExample extends PFrame {
  /** 
 */
  private static final long serialVersionUID=1L;
  public CompositeExample(){
    this(null);
  }
  public CompositeExample(  final PCanvas aCanvas){
    super("CompositeExample",false,aCanvas);
  }
  public void initialize(){
    final PComposite composite=new PComposite();
    final PNode circle=PPath.createEllipse(0,0,100,100);
    final PNode rectangle=PPath.createRectangle(50,50,100,100);
    final PNode text=new PText("Hello world!");
    composite.addChild(circle);
    composite.addChild(rectangle);
    composite.addChild(text);
    rectangle.rotate(Math.toRadians(45));
    rectangle.setPaint(Color.RED);
    text.scale(2.0);
    text.setPaint(Color.GREEN);
    getCanvas().getLayer().addChild(composite);
    getCanvas().removeInputEventListener(getCanvas().getPanEventHandler());
    getCanvas().addInputEventListener(new PDragEventHandler());
  }
  public static void main(  final String[] args){
    new CompositeExample();
  }
}
