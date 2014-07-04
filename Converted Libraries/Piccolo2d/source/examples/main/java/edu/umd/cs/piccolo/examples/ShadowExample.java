package edu.umd.cs.piccolo.examples;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.image.BufferedImage;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.PFrame;
import edu.umd.cs.piccolox.nodes.PShadow;
/** 
 * Shadow example.
 */
public final class ShadowExample extends PFrame {
  private static final Color SHADOW_PAINT=new Color(20,20,20,200);
  /** 
 * Default serial version UID. 
 */
  private static final long serialVersionUID=1L;
  /** 
 * Create a new shadow example.
 */
  public ShadowExample(){
    this(null);
  }
  /** 
 * Create a new shadow example with the specified canvas.
 * @param canvas canvas for this shadow example
 */
  public ShadowExample(  final PCanvas canvas){
    super("ShadowExample",false,canvas);
  }
  /** 
 * {@inheritDoc} 
 */
  public void initialize(){
    BufferedImage src=buildRedRectangleImage();
    addHeaderAt("Shadow nodes drawn from an image, with increasing blur radius:",0,0);
    double x=25.0d;
    double y=25.0d;
    for (int blurRadius=4; blurRadius < 28; blurRadius+=4) {
      PImage node=new PImage(src);
      PShadow shadowNode=new PShadow(src,SHADOW_PAINT,blurRadius);
      node.setOffset(x,y);
      shadowNode.setOffset(x - (2 * blurRadius) + 5.0d,y - (2 * blurRadius) + 5.0d);
      getCanvas().getLayer().addChild(shadowNode);
      getCanvas().getLayer().addChild(node);
      x+=125.0d;
      if (x > 300.0d) {
        y+=125.0d;
        x=25.0d;
      }
    }
    addHeaderAt("Shadow nodes drawn from node.toImage():",0,300);
    PPath rectNode=buildRedRectangleNode();
    PShadow rectShadow=new PShadow(rectNode.toImage(),SHADOW_PAINT,8);
    rectShadow.setOffset(25.0d - (2 * 8) + 5.0d,325.0d - (2 * 8) + 5.0d);
    getCanvas().getLayer().addChild(rectShadow);
    getCanvas().getLayer().addChild(rectNode);
    PText textNode=new PText("Shadow Text");
    textNode.setTextPaint(Color.RED);
    textNode.setFont(textNode.getFont().deriveFont(36.0f));
    textNode.setOffset(125.0d,325.0d);
    PShadow textShadow=new PShadow(textNode.toImage(),SHADOW_PAINT,8);
    textShadow.setOffset(125.0d - (2 * 8) + 2.5d,325.0d - (2 * 8) + 2.5d);
    getCanvas().getLayer().addChild(textShadow);
    getCanvas().getLayer().addChild(textNode);
  }
  private PText addHeaderAt(  String labelText,  double x,  double y){
    PText labelNode=new PText(labelText);
    labelNode.setOffset(x,y);
    getCanvas().getLayer().addChild(labelNode);
    return labelNode;
  }
  private BufferedImage buildRedRectangleImage(){
    BufferedImage src=new BufferedImage(75,75,BufferedImage.TYPE_INT_ARGB);
    Graphics2D g=src.createGraphics();
    g.setPaint(Color.RED);
    g.fillRect(0,0,75,75);
    g.dispose();
    return src;
  }
  private PPath buildRedRectangleNode(){
    PPath rectNode=PPath.createRectangle(0.0f,0.0f,75.0f,75.0f);
    rectNode.setPaint(Color.RED);
    rectNode.setStroke(null);
    rectNode.setOffset(25.0d,325.0d);
    return rectNode;
  }
  /** 
 * Main.
 * @param args command line arguments, ignored
 */
  public static void main(  final String[] args){
    new ShadowExample();
  }
}
