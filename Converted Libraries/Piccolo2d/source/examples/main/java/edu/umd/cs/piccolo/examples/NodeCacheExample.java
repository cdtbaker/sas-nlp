package edu.umd.cs.piccolo.examples;
import java.awt.BasicStroke;
import java.awt.Color;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.PFrame;
import edu.umd.cs.piccolox.nodes.PNodeCache;
public class NodeCacheExample extends PFrame {
  /** 
 */
  private static final long serialVersionUID=1L;
  public NodeCacheExample(){
    this(null);
  }
  public NodeCacheExample(  final PCanvas aCanvas){
    super("NodeCacheExample",false,aCanvas);
  }
  public void initialize(){
    final PCanvas canvas=getCanvas();
    final PPath circle=PPath.createEllipse(0,0,100,100);
    circle.setStroke(new BasicStroke(10));
    circle.setPaint(Color.YELLOW);
    final PPath rectangle=PPath.createRectangle(-100,-50,100,100);
    rectangle.setStroke(new BasicStroke(15));
    rectangle.setPaint(Color.ORANGE);
    final PNodeCache cache=new PNodeCache();
    cache.addChild(circle);
    cache.addChild(rectangle);
    cache.invalidateCache();
    canvas.getLayer().addChild(cache);
    canvas.removeInputEventListener(canvas.getPanEventHandler());
    canvas.addInputEventListener(new PDragEventHandler());
  }
  public static void main(  final String[] args){
    new NodeCacheExample();
  }
}
