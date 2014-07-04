package edu.umd.cs.piccolo.examples;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import edu.umd.cs.piccolo.PCanvas;
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
/** 
 * This example shows how to create and manipulate nodes.
 */
public class NodeExample extends PFrame {
  /** 
 */
  private static final long serialVersionUID=1L;
  boolean fIsPressed=false;
  public NodeExample(){
    this(null);
  }
  public NodeExample(  final PCanvas aCanvas){
    super("NodeExample",false,aCanvas);
  }
  public void initialize(){
    nodeDemo();
    createNodeUsingExistingClasses();
    subclassExistingClasses();
    composeOtherNodes();
    createCustomNode();
    getCanvas().removeInputEventListener(getCanvas().getPanEventHandler());
    getCanvas().addInputEventListener(new PDragEventHandler());
  }
  public void nodeDemo(){
    final PLayer layer=getCanvas().getLayer();
    final PNode aNode=PPath.createRectangle(0,0,100,80);
    layer.addChild(aNode);
    aNode.setPaint(Color.red);
    aNode.addChild(PPath.createRectangle(0,0,100,80));
    aNode.setBounds(-10,-10,200,110);
    aNode.translate(100,100);
    aNode.scale(1.5);
    aNode.rotate(45);
    aNode.setTransparency(0.75f);
    final PNode aCopy=(PNode)aNode.clone();
    aNode.setChildrenPickable(false);
    aNode.setPaint(Color.GREEN);
    aNode.setTransparency(1.0f);
    layer.addChild(aCopy);
    aCopy.setOffset(0,0);
    aCopy.rotate(-45);
  }
  public void createNodeUsingExistingClasses(){
    final PLayer layer=getCanvas().getLayer();
    layer.addChild(PPath.createEllipse(0,0,100,100));
    layer.addChild(PPath.createRectangle(0,100,100,100));
    layer.addChild(new PText("Hello World"));
    final PImage image=new PImage(layer.toImage(300,300,null));
    layer.addChild(image);
  }
  public void subclassExistingClasses(){
    final PNode n=new PPath(new Ellipse2D.Float(0,0,100,80)){
      /** 
 */
      private static final long serialVersionUID=1L;
      public void paint(      final PPaintContext aPaintContext){
        if (fIsPressed) {
          final Graphics2D g2=aPaintContext.getGraphics();
          g2.setPaint(getPaint());
          g2.fill(getBoundsReference());
        }
 else {
          super.paint(aPaintContext);
        }
      }
    }
;
    n.addInputEventListener(new PBasicInputEventHandler(){
      public void mousePressed(      final PInputEvent aEvent){
        super.mousePressed(aEvent);
        fIsPressed=true;
        n.invalidatePaint();
      }
      public void mouseReleased(      final PInputEvent aEvent){
        super.mousePressed(aEvent);
        fIsPressed=false;
        n.invalidatePaint();
      }
    }
);
    n.setPaint(Color.ORANGE);
    getCanvas().getLayer().addChild(n);
  }
  public void composeOtherNodes(){
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
    myCompositeFace.setBounds(b.inset(-5,-5));
    myCompositeFace.scale(1.5);
    getCanvas().getLayer().addChild(myCompositeFace);
  }
  public void createCustomNode(){
    final PNode n=new PNode(){
      /** 
 */
      private static final long serialVersionUID=1L;
      public void paint(      final PPaintContext aPaintContext){
        final double bx=getX();
        final double by=getY();
        final double rightBorder=bx + getWidth();
        final double bottomBorder=by + getHeight();
        final Line2D line=new Line2D.Double();
        final Graphics2D g2=aPaintContext.getGraphics();
        g2.setStroke(new BasicStroke(0));
        g2.setPaint(getPaint());
        for (double x=bx; x < rightBorder; x+=5) {
          line.setLine(x,by,x,bottomBorder);
          g2.draw(line);
        }
        for (double y=by; y < bottomBorder; y+=5) {
          line.setLine(bx,y,rightBorder,y);
          g2.draw(line);
        }
      }
    }
;
    n.setBounds(0,0,100,80);
    n.setPaint(Color.black);
    getCanvas().getLayer().addChild(n);
  }
  public static void main(  final String[] args){
    new NodeExample();
  }
}
