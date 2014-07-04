package edu.umd.cs.piccolo.examples;
import java.awt.Color;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.PFrame;
import edu.umd.cs.piccolox.nodes.PClip;
/** 
 * Quick example of how to use a clip.
 */
public class ClipExample extends PFrame {
  /** 
 */
  private static final long serialVersionUID=1L;
  public ClipExample(){
    this(null);
  }
  public ClipExample(  final PCanvas aCanvas){
    super("ClipExample",false,aCanvas);
  }
  public void initialize(){
    final PClip clip=new PClip();
    clip.setPathToEllipse(0,0,100,100);
    clip.setPaint(Color.red);
    clip.addChild(PPath.createRectangle(20,20,100,50));
    getCanvas().getLayer().addChild(clip);
    getCanvas().removeInputEventListener(getCanvas().getPanEventHandler());
    getCanvas().addInputEventListener(new PDragEventHandler());
  }
  public static void main(  final String[] args){
    new ClipExample();
  }
}
