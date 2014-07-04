package edu.umd.cs.piccolo.examples;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.PRoot;
import edu.umd.cs.piccolo.event.PDragSequenceEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PPaintContext;
import edu.umd.cs.piccolox.PFrame;
/** 
 * Example of drawing an infinite grid, and providing support for snap to grid.
 */
public class GridExample extends PFrame {
  /** 
 */
  private static final long serialVersionUID=1L;
  protected Line2D gridLine=new Line2D.Double();
  protected Stroke gridStroke=new BasicStroke(1);
  protected Color gridPaint=Color.BLACK;
  protected double gridSpacing=20;
  public GridExample(){
    this(null);
  }
  public GridExample(  final PCanvas aCanvas){
    super("GridExample",false,aCanvas);
  }
  public void initialize(){
    final PRoot root=getCanvas().getRoot();
    final PCamera camera=getCanvas().getCamera();
    final PLayer gridLayer=new PLayer(){
      /** 
 */
      private static final long serialVersionUID=1L;
      protected void paint(      final PPaintContext paintContext){
        final double bx=getX() - getX() % gridSpacing - gridSpacing;
        final double by=getY() - getY() % gridSpacing - gridSpacing;
        final double rightBorder=getX() + getWidth() + gridSpacing;
        final double bottomBorder=getY() + getHeight() + gridSpacing;
        final Graphics2D g2=paintContext.getGraphics();
        final Rectangle2D clip=paintContext.getLocalClip();
        g2.setStroke(gridStroke);
        g2.setPaint(gridPaint);
        for (double x=bx; x < rightBorder; x+=gridSpacing) {
          gridLine.setLine(x,by,x,bottomBorder);
          if (clip.intersectsLine(gridLine)) {
            g2.draw(gridLine);
          }
        }
        for (double y=by; y < bottomBorder; y+=gridSpacing) {
          gridLine.setLine(bx,y,rightBorder,y);
          if (clip.intersectsLine(gridLine)) {
            g2.draw(gridLine);
          }
        }
      }
    }
;
    root.removeChild(camera.getLayer(0));
    camera.removeLayer(0);
    root.addChild(gridLayer);
    camera.addLayer(gridLayer);
    camera.addPropertyChangeListener(PNode.PROPERTY_BOUNDS,new PropertyChangeListener(){
      public void propertyChange(      final PropertyChangeEvent evt){
        gridLayer.setBounds(camera.getViewBounds());
      }
    }
);
    camera.addPropertyChangeListener(PCamera.PROPERTY_VIEW_TRANSFORM,new PropertyChangeListener(){
      public void propertyChange(      final PropertyChangeEvent evt){
        gridLayer.setBounds(camera.getViewBounds());
      }
    }
);
    gridLayer.setBounds(camera.getViewBounds());
    final PNode n=new PNode();
    n.setPaint(Color.BLUE);
    n.setBounds(0,0,100,80);
    getCanvas().getLayer().addChild(n);
    getCanvas().removeInputEventListener(getCanvas().getPanEventHandler());
    getCanvas().addInputEventListener(new PDragSequenceEventHandler(){
      protected PNode draggedNode;
      protected Point2D nodeStartPosition;
      protected boolean shouldStartDragInteraction(      final PInputEvent event){
        if (super.shouldStartDragInteraction(event)) {
          return event.getPickedNode() != event.getTopCamera() && !(event.getPickedNode() instanceof PLayer);
        }
        return false;
      }
      protected void startDrag(      final PInputEvent event){
        super.startDrag(event);
        draggedNode=event.getPickedNode();
        draggedNode.moveToFront();
        nodeStartPosition=draggedNode.getOffset();
      }
      protected void drag(      final PInputEvent event){
        super.drag(event);
        final Point2D start=getCanvas().getCamera().localToView((Point2D)getMousePressedCanvasPoint().clone());
        final Point2D current=event.getPositionRelativeTo(getCanvas().getLayer());
        final Point2D dest=new Point2D.Double();
        dest.setLocation(nodeStartPosition.getX() + current.getX() - start.getX(),nodeStartPosition.getY() + current.getY() - start.getY());
        dest.setLocation(dest.getX() - dest.getX() % gridSpacing,dest.getY() - dest.getY() % gridSpacing);
        draggedNode.setOffset(dest.getX(),dest.getY());
      }
    }
);
  }
  public static void main(  final String[] args){
    new GridExample();
  }
}
