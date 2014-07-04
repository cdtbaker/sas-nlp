package edu.umd.cs.piccolo.util;
import junit.framework.TestCase;
import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
/** 
 * Unit test for PPickPath.
 */
public class PPickPathTest extends TestCase {
  public PPickPathTest(  final String name){
    super(name);
  }
  public void testPick(){
    final PCanvas canvas=new PCanvas();
    final PCamera camera=canvas.getCamera();
    final PLayer layer=canvas.getLayer();
    camera.setBounds(0,0,100,100);
    final PNode a=PPath.createRectangle(0,0,100,100);
    final PNode b=PPath.createRectangle(0,0,100,100);
    final PNode c=PPath.createRectangle(0,0,100,100);
    layer.addChild(a);
    layer.addChild(b);
    layer.addChild(c);
    final PPickPath pickPath=camera.pick(50,50,2);
    assertTrue(pickPath.getPickedNode() == c);
    assertTrue(pickPath.nextPickedNode() == b);
    assertTrue(pickPath.nextPickedNode() == a);
    assertTrue(pickPath.nextPickedNode() == camera);
    assertTrue(pickPath.nextPickedNode() == null);
    assertTrue(pickPath.nextPickedNode() == null);
  }
}
