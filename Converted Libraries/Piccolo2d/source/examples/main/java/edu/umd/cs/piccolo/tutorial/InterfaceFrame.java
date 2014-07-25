package edu.umd.cs.piccolo.tutorial;
import java.awt.Color;
import java.awt.Graphics2D;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPaintContext;
import edu.umd.cs.piccolox.PFrame;
public class InterfaceFrame extends PFrame {
  /** 
 */
  private static final long serialVersionUID=1L;
  public void initialize(){
    getCanvas().setPanEventHandler(null);
    getCanvas().addInputEventListener(new PDragEventHandler());
    final PNode aNode=new PNode();
    aNode.setBounds(0,0,100,80);
    aNode.setPaint(Color.RED);
    final PLayer layer=getCanvas().getLayer();
    layer.addChild(aNode);
    final PNode anotherNode=new PNode();
    anotherNode.setBounds(0,0,100,80);
    anotherNode.setPaint(Color.YELLOW);
    aNode.addChild(anotherNode);
    aNode.setBounds(-10,-10,200,110);
    aNode.translate(100,100);
    aNode.scale(1.5f);
    aNode.rotate(45);
    layer.addChild(PPath.createEllipse(0,0,100,100));
    layer.addChild(PPath.createRectangle(0,100,100,100));
    layer.addChild(new PText("Hello World"));
    final PImage image=new PImage(layer.toImage(300,300,null));
    layer.addChild(image);
    final PNode myCompositeFace=PPath.createRectangle(0,0,100,80);
    final PNode eye1=PPath.createEllipse(0,0,20,20);
    eye1.setPaint(Color.YELLOW);
    final PNode eye2=(PNode)eye1.clone();
    final PNode mouth=PPath.createRectangle(0,0,40,20);
    mouth.setPaint(Color.BLACK);
    myCompositeFace.addChild(eye1);
    myCompositeFace.addChild(eye2);
    myCompositeFace.addChild(mouth);
    myCompositeFace.setChildrenPickable(false);
    eye2.translate(25,0);
    mouth.translate(0,30);
    final PBounds b=myCompositeFace.getUnionOfChildrenBounds(null);
    b.inset(-5,-5);
    myCompositeFace.setBounds(b);
    myCompositeFace.scale(1.5);
    layer.addChild(myCompositeFace);
    final ToggleShape ts=new ToggleShape();
    ts.setPaint(Color.ORANGE);
    layer.addChild(ts);
  }
class ToggleShape extends PPath {
    private static final long serialVersionUID=1L;
    private boolean isPressed=false;
    public ToggleShape(){
      setPathToEllipse(0,0,100,80);
      addInputEventListener(new PBasicInputEventHandler(){
        public void mousePressed(        final PInputEvent event){
          super.mousePressed(event);
          isPressed=true;
          repaint();
        }
        public void mouseReleased(        final PInputEvent event){
          super.mouseReleased(event);
          isPressed=false;
          repaint();
        }
      }
);
    }
    protected void paint(    final PPaintContext paintContext){
      if (isPressed) {
        final Graphics2D g2=paintContext.getGraphics();
        g2.setPaint(getPaint());
        g2.fill(getBoundsReference());
      }
 else {
        super.paint(paintContext);
      }
    }
  }
  public static void main(  final String[] args){
    new InterfaceFrame();
  }
}
