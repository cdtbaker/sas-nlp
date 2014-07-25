package edu.umd.cs.piccolo.examples;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolox.PFrame;
import edu.umd.cs.piccolox.event.PStyledTextEventHandler;
/** 
 * @author Lance Good
 */
public class TextExample extends PFrame {
  /** 
 */
  private static final long serialVersionUID=1L;
  public TextExample(){
    this(null);
  }
  public TextExample(  final PCanvas aCanvas){
    super("TextExample",false,aCanvas);
  }
  public void initialize(){
    getCanvas().removeInputEventListener(getCanvas().getPanEventHandler());
    final PStyledTextEventHandler textHandler=new PStyledTextEventHandler(getCanvas());
    getCanvas().addInputEventListener(textHandler);
  }
  public static void main(  final String[] args){
    new TextExample();
  }
}
