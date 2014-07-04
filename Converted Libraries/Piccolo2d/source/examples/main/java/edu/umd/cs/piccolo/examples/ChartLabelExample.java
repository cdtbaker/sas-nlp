package edu.umd.cs.piccolo.examples;
import java.awt.Color;
import java.awt.geom.Point2D;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.event.PDragSequenceEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.PFrame;
/** 
 * This example shows how to create a vertical and a horizontal bar which can
 * move with your graph and always stays on view.
 * @author Tao
 */
public class ChartLabelExample extends PFrame {
  /** 
 */
  private static final long serialVersionUID=1L;
  final int nodeHeight=15;
  final int nodeWidth=30;
  PLayer rowBarLayer;
  PLayer colBarLayer;
  public ChartLabelExample(){
    this(null);
  }
  public ChartLabelExample(  final PCanvas aCanvas){
    super("ChartLabelExample",false,aCanvas);
  }
  public void initialize(){
    rowBarLayer=new PLayer();
    colBarLayer=new PLayer();
    for (int i=0; i < 10; i++) {
      PText p=new PText("Row " + i);
      p.setX(0);
      p.setY(nodeHeight * i + nodeHeight);
      p.setPaint(Color.white);
      colBarLayer.addChild(p);
      p=new PText("Col " + i);
      p.setX(nodeWidth * i + nodeWidth);
      p.setY(0);
      p.setPaint(Color.white);
      rowBarLayer.addChild(p);
    }
    getCanvas().getCamera().addChild(rowBarLayer);
    getCanvas().getCamera().addChild(colBarLayer);
    for (int i=0; i < 10; i++) {
      for (int j=0; j < 10; j++) {
        final PPath path=PPath.createRectangle(nodeWidth * j + nodeWidth,nodeHeight * i + nodeHeight,nodeWidth - 1,nodeHeight - 1);
        getCanvas().getLayer().addChild(path);
      }
    }
    getCanvas().addInputEventListener(new PDragSequenceEventHandler(){
      Point2D oldP, newP;
      public void mousePressed(      final PInputEvent aEvent){
        oldP=getCanvas().getCamera().getViewBounds().getCenter2D();
      }
      public void mouseReleased(      final PInputEvent aEvent){
        newP=getCanvas().getCamera().getViewBounds().getCenter2D();
        colBarLayer.translate(0,(oldP.getY() - newP.getY()) / getCanvas().getLayer().getScale());
        rowBarLayer.translate((oldP.getX() - newP.getX()) / getCanvas().getLayer().getScale(),0);
      }
    }
);
  }
  public static void main(  final String[] args){
    new ChartLabelExample();
  }
}
