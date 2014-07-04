package edu.umd.cs.piccolo.examples;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.PFrame;
/** 
 * This example shows how to use setTriggerTime to synchronize the start and end
 * of two activities.
 */
public class WaitForActivitiesExample extends PFrame {
  /** 
 */
  private static final long serialVersionUID=1L;
  public WaitForActivitiesExample(){
    this(null);
  }
  public WaitForActivitiesExample(  final PCanvas aCanvas){
    super("WaitForActivitiesExample",false,aCanvas);
  }
  public void initialize(){
    final PLayer layer=getCanvas().getLayer();
    final PNode a=PPath.createRectangle(0,0,100,80);
    final PNode b=PPath.createRectangle(0,0,100,80);
    layer.addChild(a);
    layer.addChild(b);
    final PActivity a1=a.animateToPositionScaleRotation(200,200,1,0,5000);
    final PActivity a2=b.animateToPositionScaleRotation(200,200,1,0,5000);
    a2.startAfter(a1);
  }
  public static void main(  final String[] args){
    new WaitForActivitiesExample();
  }
}
