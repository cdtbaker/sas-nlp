package edu.umd.cs.piccolo.examples;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragSequenceEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.PFrame;
/** 
 * Create a simple graph with some random nodes and connected edges. An event
 * handler allows users to drag nodes around, keeping the edges connected.
 * ported from .NET GraphEditorExample by Sun Hongmei.
 */
public class GraphEditorExample extends PFrame {
  /** 
 */
  private static final long serialVersionUID=1L;
  public GraphEditorExample(){
    this(null);
  }
  public GraphEditorExample(  final PCanvas aCanvas){
    super("GraphEditorExample",false,aCanvas);
  }
  public void initialize(){
    final int numNodes=50;
    final int numEdges=50;
    final PLayer nodeLayer=getCanvas().getLayer();
    final PLayer edgeLayer=new PLayer();
    getCanvas().getCamera().addLayer(0,edgeLayer);
    final Random rnd=new Random();
    ArrayList tmp;
    for (int i=0; i < numNodes; i++) {
      final float x=(float)(300. * rnd.nextDouble());
      final float y=(float)(400. * rnd.nextDouble());
      final PPath path=PPath.createEllipse(x,y,20,20);
      tmp=new ArrayList();
      path.addAttribute("edges",tmp);
      nodeLayer.addChild(path);
    }
    for (int i=0; i < numEdges; i++) {
      final int n1=rnd.nextInt(numNodes);
      final int n2=rnd.nextInt(numNodes);
      final PNode node1=nodeLayer.getChild(n1);
      final PNode node2=nodeLayer.getChild(n2);
      final Point2D.Double bound1=(Point2D.Double)node1.getBounds().getCenter2D();
      final Point2D.Double bound2=(Point2D.Double)node2.getBounds().getCenter2D();
      final PPath edge=new PPath();
      edge.moveTo((float)bound1.getX(),(float)bound1.getY());
      edge.lineTo((float)bound2.getX(),(float)bound2.getY());
      tmp=(ArrayList)node1.getAttribute("edges");
      tmp.add(edge);
      tmp=(ArrayList)node2.getAttribute("edges");
      tmp.add(edge);
      tmp=new ArrayList();
      tmp.add(node1);
      tmp.add(node2);
      edge.addAttribute("nodes",tmp);
      edgeLayer.addChild(edge);
    }
    nodeLayer.addInputEventListener(new NodeDragHandler());
  }
  public static void main(  final String[] args){
    new GraphEditorExample();
  }
class NodeDragHandler extends PDragSequenceEventHandler {
    public NodeDragHandler(){
      getEventFilter().setMarksAcceptedEventsAsHandled(true);
    }
    public void mouseEntered(    final PInputEvent e){
      if (e.getButton() == 0) {
        e.getPickedNode().setPaint(Color.red);
      }
    }
    public void mouseExited(    final PInputEvent e){
      if (e.getButton() == 0) {
        e.getPickedNode().setPaint(Color.white);
      }
    }
    public void drag(    final PInputEvent e){
      final PNode node=e.getPickedNode();
      node.translate(e.getDelta().width,e.getDelta().height);
      final ArrayList edges=(ArrayList)e.getPickedNode().getAttribute("edges");
      int i;
      for (i=0; i < edges.size(); i++) {
        final PPath edge=(PPath)edges.get(i);
        final ArrayList nodes=(ArrayList)edge.getAttribute("nodes");
        final PNode node1=(PNode)nodes.get(0);
        final PNode node2=(PNode)nodes.get(1);
        edge.reset();
        final Point2D.Double bound1=(Point2D.Double)node1.getFullBounds().getCenter2D();
        final Point2D.Double bound2=(Point2D.Double)node2.getFullBounds().getCenter2D();
        edge.moveTo((float)bound1.getX(),(float)bound1.getY());
        edge.lineTo((float)bound2.getX(),(float)bound2.getY());
      }
    }
  }
}
