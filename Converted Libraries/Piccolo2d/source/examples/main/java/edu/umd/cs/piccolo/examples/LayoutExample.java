package edu.umd.cs.piccolo.examples;
import java.awt.Color;
import java.util.Iterator;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.PFrame;
import edu.umd.cs.piccolox.handles.PBoundsHandle;
/** 
 * This example shows how to create a node that will automatically layout its
 * children.
 */
public class LayoutExample extends PFrame {
  /** 
 */
  private static final long serialVersionUID=1L;
  public LayoutExample(){
    this(null);
  }
  public LayoutExample(  final PCanvas aCanvas){
    super("LayoutExample",false,aCanvas);
  }
  public void initialize(){
    final PNode layoutNode=new PNode(){
      /** 
 */
      private static final long serialVersionUID=1L;
      public void layoutChildren(){
        double xOffset=0;
        final double yOffset=0;
        final Iterator i=getChildrenIterator();
        while (i.hasNext()) {
          final PNode each=(PNode)i.next();
          each.setOffset(xOffset - each.getX(),yOffset);
          xOffset+=each.getWidth();
        }
      }
    }
;
    layoutNode.setPaint(Color.red);
    for (int i=0; i < 1000; i++) {
      final PNode each=PPath.createRectangle(0,0,100,80);
      layoutNode.addChild(each);
    }
    PBoundsHandle.addBoundsHandlesTo(layoutNode.getChild(0));
    getCanvas().getLayer().addChild(layoutNode);
  }
  public static void main(  final String[] args){
    new LayoutExample();
  }
}
