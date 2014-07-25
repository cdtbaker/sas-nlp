package edu.umd.cs.piccolo.tutorial;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PDragSequenceEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PInputEventFilter;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.PFrame;
public class UserInteraction extends PFrame {
  /** 
 */
  private static final long serialVersionUID=1L;
  public UserInteraction(){
    super();
  }
  public void initialize(){
    getCanvas().setPanEventHandler(null);
    final PBasicInputEventHandler squiggleHandler=new SquiggleHandler(getCanvas());
    getCanvas().addInputEventListener(squiggleHandler);
    final PNode nodeGreen=PPath.createRectangle(0,0,100,100);
    nodeGreen.setPaint(Color.GREEN);
    getCanvas().getLayer().addChild(nodeGreen);
    nodeGreen.addInputEventListener(new PBasicInputEventHandler(){
      public void mousePressed(      final PInputEvent event){
        event.getPickedNode().setPaint(Color.ORANGE);
        event.getInputManager().setKeyboardFocus(event.getPath());
        event.setHandled(true);
      }
      public void mouseDragged(      final PInputEvent event){
        final PNode aNode=event.getPickedNode();
        final PDimension delta=event.getDeltaRelativeTo(aNode);
        aNode.translate(delta.width,delta.height);
        event.setHandled(true);
      }
      public void mouseReleased(      final PInputEvent event){
        event.getPickedNode().setPaint(Color.GREEN);
        event.setHandled(true);
      }
      public void keyPressed(      final PInputEvent event){
        final PNode node=event.getPickedNode();
switch (event.getKeyCode()) {
case KeyEvent.VK_UP:
          node.translate(0,-10f);
        break;
case KeyEvent.VK_DOWN:
      node.translate(0,10f);
    break;
case KeyEvent.VK_LEFT:
  node.translate(-10f,0);
break;
case KeyEvent.VK_RIGHT:
node.translate(10f,0);
break;
}
}
}
);
}
public class SquiggleHandler extends PDragSequenceEventHandler {
protected PCanvas canvas;
protected PPath squiggle;
public SquiggleHandler(final PCanvas aCanvas){
canvas=aCanvas;
setEventFilter(new PInputEventFilter(InputEvent.BUTTON1_MASK));
}
public void startDrag(final PInputEvent e){
super.startDrag(e);
final Point2D p=e.getPosition();
squiggle=new PPath();
squiggle.moveTo((float)p.getX(),(float)p.getY());
squiggle.setStroke(new BasicStroke((float)(1 / e.getCamera().getViewScale())));
canvas.getLayer().addChild(squiggle);
e.getInputManager().setKeyboardFocus(null);
}
public void drag(final PInputEvent e){
super.drag(e);
updateSquiggle(e);
}
public void endDrag(final PInputEvent e){
super.endDrag(e);
updateSquiggle(e);
squiggle=null;
}
public void updateSquiggle(final PInputEvent aEvent){
final Point2D p=aEvent.getPosition();
squiggle.lineTo((float)p.getX(),(float)p.getY());
}
}
public static void main(final String[] args){
new UserInteraction();
}
}
