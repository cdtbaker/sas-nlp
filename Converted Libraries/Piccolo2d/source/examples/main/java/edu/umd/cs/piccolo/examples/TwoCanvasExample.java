package edu.umd.cs.piccolo.examples;
import java.awt.Color;
import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.PRoot;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.PFrame;
import edu.umd.cs.piccolox.handles.PBoundsHandle;
public class TwoCanvasExample extends PFrame {
  /** 
 */
  private static final long serialVersionUID=1L;
  public TwoCanvasExample(){
    this(null);
  }
  public TwoCanvasExample(  final PCanvas aCanvas){
    super("TwoCanvasExample",false,aCanvas);
  }
  public void initialize(){
    final PRoot root=getCanvas().getRoot();
    final PLayer layer=getCanvas().getLayer();
    final PNode n=PPath.createRectangle(0,0,100,80);
    final PNode sticky=PPath.createRectangle(0,0,50,50);
    PBoundsHandle.addBoundsHandlesTo(n);
    sticky.setPaint(Color.YELLOW);
    PBoundsHandle.addBoundsHandlesTo(sticky);
    layer.addChild(n);
    getCanvas().getCamera().addChild(sticky);
    final PCamera otherCamera=new PCamera();
    otherCamera.addLayer(layer);
    root.addChild(otherCamera);
    final PCanvas other=new PCanvas();
    other.setCamera(otherCamera);
    final PFrame result=new PFrame("TwoCanvasExample",false,other);
    result.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    result.setLocation(500,100);
  }
  public static void main(  final String[] args){
    new TwoCanvasExample();
  }
}
