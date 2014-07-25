package edu.umd.cs.piccolo.examples;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolo.util.PPaintContext;
import edu.umd.cs.piccolox.PFrame;
import edu.umd.cs.piccolox.handles.PHandle;
import edu.umd.cs.piccolox.util.PLocator;
/** 
 * This shows how to create a simple node that has two handles and can be used
 * for specifying angles. The nodes UI desing isn't very exciting, but the
 * example shows one way to create a custom node with custom handles.
 */
public class AngleNodeExample extends PFrame {
  /** 
 */
  private static final long serialVersionUID=1L;
  public AngleNodeExample(){
    this(null);
  }
  public AngleNodeExample(  final PCanvas aCanvas){
    super("AngleNodeExample",false,aCanvas);
  }
  public void initialize(){
    final PCanvas c=getCanvas();
    final PLayer l=c.getLayer();
    l.addChild(new AngleNode());
  }
  public static void main(  final String[] args){
    new AngleNodeExample();
  }
public static class AngleNode extends PNode {
    /** 
 */
    private static final long serialVersionUID=1L;
    protected Point2D.Double pointOne;
    protected Point2D.Double pointTwo;
    protected Stroke stroke;
    public AngleNode(){
      pointOne=new Point2D.Double(100,0);
      pointTwo=new Point2D.Double(0,100);
      stroke=new BasicStroke(1,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND);
      setPaint(Color.BLACK);
      updateBounds();
      addHandles();
    }
    public void addHandles(){
      PLocator l=new PLocator(){
        /** 
 */
        private static final long serialVersionUID=1L;
        public double locateX(){
          return pointOne.getX();
        }
        public double locateY(){
          return pointOne.getY();
        }
      }
;
      PHandle h=new PHandle(l){
        /** 
 */
        private static final long serialVersionUID=1L;
        public void dragHandle(        final PDimension aLocalDimension,        final PInputEvent aEvent){
          localToParent(aLocalDimension);
          pointOne.setLocation(pointOne.getX() + aLocalDimension.getWidth(),pointOne.getY() + aLocalDimension.getHeight());
          updateBounds();
          relocateHandle();
        }
      }
;
      addChild(h);
      l=new PLocator(){
        /** 
 */
        private static final long serialVersionUID=1L;
        public double locateX(){
          return pointTwo.getX();
        }
        public double locateY(){
          return pointTwo.getY();
        }
      }
;
      h=new PHandle(l){
        /** 
 */
        private static final long serialVersionUID=1L;
        public void dragHandle(        final PDimension aLocalDimension,        final PInputEvent aEvent){
          localToParent(aLocalDimension);
          pointTwo.setLocation(pointTwo.getX() + aLocalDimension.getWidth(),pointTwo.getY() + aLocalDimension.getHeight());
          updateBounds();
          relocateHandle();
        }
      }
;
      addChild(h);
    }
    protected void paint(    final PPaintContext paintContext){
      final Graphics2D g2=paintContext.getGraphics();
      g2.setStroke(stroke);
      g2.setPaint(getPaint());
      g2.draw(getAnglePath());
    }
    protected void updateBounds(){
      final GeneralPath p=getAnglePath();
      final Rectangle2D b=stroke.createStrokedShape(p).getBounds2D();
      super.setBounds(b.getX(),b.getY(),b.getWidth(),b.getHeight());
    }
    public GeneralPath getAnglePath(){
      final GeneralPath p=new GeneralPath();
      p.moveTo((float)pointOne.getX(),(float)pointOne.getY());
      p.lineTo(0,0);
      p.lineTo((float)pointTwo.getX(),(float)pointTwo.getY());
      return p;
    }
    public boolean setBounds(    final double x,    final double y,    final double width,    final double height){
      return false;
    }
  }
}
