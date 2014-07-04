package edu.umd.cs.piccolo.examples;
import java.awt.Color;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.PFrame;
/** 
 * This example shows how a node can get the keyboard focus.
 */
public class KeyEventFocusExample extends PFrame {
  /** 
 */
  private static final long serialVersionUID=1L;
  public KeyEventFocusExample(){
    this(null);
  }
  public KeyEventFocusExample(  final PCanvas aCanvas){
    super("KeyEventFocusExample",false,aCanvas);
  }
  public void initialize(){
    final PCanvas canvas=getCanvas();
    final PNode nodeGreen=PPath.createRectangle(0,0,100,100);
    final PNode nodeRed=PPath.createRectangle(0,0,100,100);
    nodeRed.translate(200,0);
    nodeGreen.setPaint(Color.green);
    nodeRed.setPaint(Color.red);
    canvas.getLayer().addChild(nodeGreen);
    canvas.getLayer().addChild(nodeRed);
    nodeGreen.addInputEventListener(new PBasicInputEventHandler(){
      public void keyPressed(      final PInputEvent event){
        System.out.println("green keypressed");
      }
      public void mousePressed(      final PInputEvent event){
        event.getInputManager().setKeyboardFocus(event.getPath());
        System.out.println("green mousepressed");
      }
      public void keyboardFocusGained(      final PInputEvent event){
        System.out.println("green focus gained");
      }
      public void keyboardFocusLost(      final PInputEvent event){
        System.out.println("green focus lost");
      }
    }
);
    nodeRed.addInputEventListener(new PBasicInputEventHandler(){
      public void keyPressed(      final PInputEvent event){
        System.out.println("red keypressed");
      }
      public void mousePressed(      final PInputEvent event){
        event.getInputManager().setKeyboardFocus(event.getPath());
        System.out.println("red mousepressed");
      }
      public void keyboardFocusGained(      final PInputEvent event){
        System.out.println("red focus gained");
      }
      public void keyboardFocusLost(      final PInputEvent event){
        System.out.println("red focus lost");
      }
    }
);
  }
  public static void main(  final String[] args){
    new KeyEventFocusExample();
  }
}
