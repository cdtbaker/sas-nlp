package edu.umd.cs.piccolo.examples;
import java.awt.Color;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.PFrame;
public class PositionExample extends PFrame {
  /** 
 */
  private static final long serialVersionUID=1L;
  public PositionExample(){
    this(null);
  }
  public PositionExample(  final PCanvas aCanvas){
    super("PositionExample",false,aCanvas);
  }
  public void initialize(){
    final PNode n1=PPath.createRectangle(0,0,100,80);
    final PNode n2=PPath.createRectangle(0,0,100,80);
    getCanvas().getLayer().addChild(n1);
    getCanvas().getLayer().addChild(n2);
    n2.scale(2.0);
    n2.rotate(Math.toRadians(90));
    n2.scale(0.5);
    n2.setPaint(Color.red);
    n1.offset(100,0);
    n2.offset(100,0);
  }
  public static void main(  final String[] args){
    new PositionExample();
  }
}
