package edu.umd.cs.piccolo.examples;
import java.awt.geom.Arc2D;
import java.awt.geom.GeneralPath;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.PFrame;
import edu.umd.cs.piccolox.activities.PPositionPathActivity;
/** 
 * This example shows how create a simple acitivty to animate a node along a
 * general path.
 */
public class PositionPathActivityExample extends PFrame {
  /** 
 */
  private static final long serialVersionUID=1L;
  public PositionPathActivityExample(){
    super();
  }
  public void initialize(){
    final PLayer layer=getCanvas().getLayer();
    final PNode animatedNode=PPath.createRectangle(0,0,100,80);
    layer.addChild(animatedNode);
    final GeneralPath path=new GeneralPath();
    path.moveTo(0,0);
    path.lineTo(300,300);
    path.lineTo(300,0);
    path.append(new Arc2D.Float(0,0,300,300,90,-90,Arc2D.OPEN),true);
    path.closePath();
    final PPath ppath=new PPath(path);
    layer.addChild(ppath);
    final PPositionPathActivity positionPathActivity=new PPositionPathActivity(5000,0,new PPositionPathActivity.Target(){
      public void setPosition(      final double x,      final double y){
        animatedNode.setOffset(x,y);
      }
    }
);
    positionPathActivity.setPositions(path);
    positionPathActivity.setLoopCount(Integer.MAX_VALUE);
    animatedNode.addActivity(positionPathActivity);
  }
  public static void main(  final String[] args){
    new PositionPathActivityExample();
  }
}
