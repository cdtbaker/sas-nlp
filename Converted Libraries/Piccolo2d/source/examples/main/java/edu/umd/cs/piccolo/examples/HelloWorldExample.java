package edu.umd.cs.piccolo.examples;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.PFrame;
public class HelloWorldExample extends PFrame {
  /** 
 */
  private static final long serialVersionUID=1L;
  public HelloWorldExample(){
    this(null);
  }
  public HelloWorldExample(  final PCanvas aCanvas){
    super("HelloWorldExample",false,aCanvas);
  }
  public void initialize(){
    final PText text=new PText("Hello World");
    getCanvas().getLayer().addChild(text);
  }
  public static void main(  final String[] args){
    new HelloWorldExample();
  }
}
