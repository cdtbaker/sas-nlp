package edu.umd.cs.piccolo.examples;
import java.awt.Color;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.PFrame;
/** 
 * This example shows how create and schedule activities.
 */
public class ActivityExample extends PFrame {
  private static final long serialVersionUID=1L;
  public ActivityExample(){
    this(null);
  }
  public ActivityExample(  final PCanvas aCanvas){
    super("ActivityExample",false,aCanvas);
  }
  public void initialize(){
    final long currentTime=System.currentTimeMillis();
    final PNode aNode=PPath.createRectangle(0,0,100,80);
    final PLayer layer=getCanvas().getLayer();
    layer.addChild(aNode);
    aNode.setOffset(200,200);
    final PActivity flash=new PActivity(-1,500,currentTime + 5000){
      boolean fRed=true;
      protected void activityStep(      final long elapsedTime){
        super.activityStep(elapsedTime);
        if (fRed) {
          aNode.setPaint(Color.red);
        }
 else {
          aNode.setPaint(Color.green);
        }
        fRed=!fRed;
      }
    }
;
    getCanvas().getRoot().addActivity(flash);
    final PActivity a1=aNode.animateToPositionScaleRotation(0,0,0.5,0,5000);
    final PActivity a2=aNode.animateToPositionScaleRotation(100,0,1.5,Math.toRadians(110),5000);
    final PActivity a3=aNode.animateToPositionScaleRotation(200,100,1,0,5000);
    final PActivity a4=aNode.animateToTransparency(0.25f,3000);
    a1.setStartTime(currentTime);
    a2.startAfter(a1);
    a3.startAfter(a2);
    a4.startAfter(a3);
  }
  public static void main(  final String[] args){
    new ActivityExample();
  }
}
