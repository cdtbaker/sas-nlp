package edu.umd.cs.piccolo.examples;
import java.awt.BasicStroke;
import java.awt.Color;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.PFrame;
import edu.umd.cs.piccolox.util.PFixedWidthStroke;
/** 
 * Stroke example.
 */
public final class StrokeExample extends PFrame {
  /** 
 */
  private static final long serialVersionUID=1L;
  /** 
 * Create a new stroke example.
 */
  public StrokeExample(){
    this(null);
  }
  /** 
 * Create a new stroke example with the specified canvas.
 * @param canvas canvas
 */
  public StrokeExample(  final PCanvas canvas){
    super("StrokeExample",false,canvas);
  }
  /** 
 * {@inheritDoc} 
 */
  public void initialize(){
    final PText label=new PText("Stroke Example");
    label.setFont(label.getFont().deriveFont(24.0f));
    label.offset(20.0d,20.0d);
    final PPath rect=PPath.createRectangle(50.0f,50.0f,300.0f,300.0f);
    rect.setStroke(new BasicStroke(4.0f));
    rect.setStrokePaint(new Color(80,80,80));
    final PText fixedWidthLabel=new PText("PFixedWidthStrokes");
    fixedWidthLabel.setTextPaint(new Color(80,0,0));
    fixedWidthLabel.offset(100.0d,80.0d);
    final PPath fixedWidthRect0=PPath.createRectangle(100.0f,100.0f,200.0f,50.0f);
    fixedWidthRect0.setStroke(new PFixedWidthStroke(2.0f));
    fixedWidthRect0.setStrokePaint(new Color(60,60,60));
    final PPath fixedWidthRect1=PPath.createRectangle(100.0f,175.0f,200.0f,50.0f);
    fixedWidthRect1.setStroke(new PFixedWidthStroke(1.5f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_MITER,10.0f));
    fixedWidthRect1.setStrokePaint(new Color(40,40,40));
    final PPath fixedWidthRect2=PPath.createRectangle(100.0f,250.0f,200.0f,50.0f);
    fixedWidthRect2.setStroke(new PFixedWidthStroke(1.0f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_MITER,10.0f,new float[]{2.0f,3.0f,4.0f},1.0f));
    fixedWidthRect2.setStrokePaint(new Color(20,20,20));
    getCanvas().getLayer().addChild(label);
    getCanvas().getLayer().addChild(rect);
    getCanvas().getLayer().addChild(fixedWidthLabel);
    getCanvas().getLayer().addChild(fixedWidthRect0);
    getCanvas().getLayer().addChild(fixedWidthRect1);
    getCanvas().getLayer().addChild(fixedWidthRect2);
  }
  /** 
 * Main.
 * @param args command line arguments
 */
  public static void main(  final String[] args){
    new StrokeExample();
  }
}
