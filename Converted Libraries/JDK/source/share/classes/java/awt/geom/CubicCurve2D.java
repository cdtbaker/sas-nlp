package java.awt.geom;
import java.awt.Shape;
import java.awt.Rectangle;
import java.util.Arrays;
import java.io.Serializable;
import sun.awt.geom.Curve;
import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.ulp;
/** 
 * The <code>CubicCurve2D</code> class defines a cubic parametric curve
 * segment in {@code (x,y)} coordinate space.
 * <p>
 * This class is only the abstract superclass for all objects which
 * store a 2D cubic curve segment.
 * The actual storage representation of the coordinates is left to
 * the subclass.
 * @author      Jim Graham
 * @since 1.2
 */
public abstract class CubicCurve2D implements Shape, Cloneable {
  /** 
 * A cubic parametric curve segment specified with{@code float} coordinates.
 * @since 1.2
 */
public static class Float extends CubicCurve2D implements Serializable {
    /** 
 * The X coordinate of the start point
 * of the cubic curve segment.
 * @since 1.2
 * @serial
 */
    public float x1;
    /** 
 * The Y coordinate of the start point
 * of the cubic curve segment.
 * @since 1.2
 * @serial
 */
    public float y1;
    /** 
 * The X coordinate of the first control point
 * of the cubic curve segment.
 * @since 1.2
 * @serial
 */
    public float ctrlx1;
    /** 
 * The Y coordinate of the first control point
 * of the cubic curve segment.
 * @since 1.2
 * @serial
 */
    public float ctrly1;
    /** 
 * The X coordinate of the second control point
 * of the cubic curve segment.
 * @since 1.2
 * @serial
 */
    public float ctrlx2;
    /** 
 * The Y coordinate of the second control point
 * of the cubic curve segment.
 * @since 1.2
 * @serial
 */
    public float ctrly2;
    /** 
 * The X coordinate of the end point
 * of the cubic curve segment.
 * @since 1.2
 * @serial
 */
    public float x2;
    /** 
 * The Y coordinate of the end point
 * of the cubic curve segment.
 * @since 1.2
 * @serial
 */
    public float y2;
    /** 
 * Constructs and initializes a CubicCurve with coordinates
 * (0, 0, 0, 0, 0, 0, 0, 0).
 * @since 1.2
 */
    public Float(){
    }
    /** 
 * Constructs and initializes a {@code CubicCurve2D} from
 * the specified {@code float} coordinates.
 * @param x1 the X coordinate for the start point
 * of the resulting {@code CubicCurve2D}
 * @param y1 the Y coordinate for the start point
 * of the resulting {@code CubicCurve2D}
 * @param ctrlx1 the X coordinate for the first control point
 * of the resulting {@code CubicCurve2D}
 * @param ctrly1 the Y coordinate for the first control point
 * of the resulting {@code CubicCurve2D}
 * @param ctrlx2 the X coordinate for the second control point
 * of the resulting {@code CubicCurve2D}
 * @param ctrly2 the Y coordinate for the second control point
 * of the resulting {@code CubicCurve2D}
 * @param x2 the X coordinate for the end point
 * of the resulting {@code CubicCurve2D}
 * @param y2 the Y coordinate for the end point
 * of the resulting {@code CubicCurve2D}
 * @since 1.2
 */
    public Float(    float x1,    float y1,    float ctrlx1,    float ctrly1,    float ctrlx2,    float ctrly2,    float x2,    float y2){
      setCurve(x1,y1,ctrlx1,ctrly1,ctrlx2,ctrly2,x2,y2);
    }
    /** 
 * {@inheritDoc}
 * @since 1.2
 */
    public double getX1(){
      return (double)x1;
    }
    /** 
 * {@inheritDoc}
 * @since 1.2
 */
    public double getY1(){
      return (double)y1;
    }
    /** 
 * {@inheritDoc}
 * @since 1.2
 */
    public Point2D getP1(){
      return new Point2D.Float(x1,y1);
    }
    /** 
 * {@inheritDoc}
 * @since 1.2
 */
    public double getCtrlX1(){
      return (double)ctrlx1;
    }
    /** 
 * {@inheritDoc}
 * @since 1.2
 */
    public double getCtrlY1(){
      return (double)ctrly1;
    }
    /** 
 * {@inheritDoc}
 * @since 1.2
 */
    public Point2D getCtrlP1(){
      return new Point2D.Float(ctrlx1,ctrly1);
    }
    /** 
 * {@inheritDoc}
 * @since 1.2
 */
    public double getCtrlX2(){
      return (double)ctrlx2;
    }
    /** 
 * {@inheritDoc}
 * @since 1.2
 */
    public double getCtrlY2(){
      return (double)ctrly2;
    }
    /** 
 * {@inheritDoc}
 * @since 1.2
 */
    public Point2D getCtrlP2(){
      return new Point2D.Float(ctrlx2,ctrly2);
    }
    /** 
 * {@inheritDoc}
 * @since 1.2
 */
    public double getX2(){
      return (double)x2;
    }
    /** 
 * {@inheritDoc}
 * @since 1.2
 */
    public double getY2(){
      return (double)y2;
    }
    /** 
 * {@inheritDoc}
 * @since 1.2
 */
    public Point2D getP2(){
      return new Point2D.Float(x2,y2);
    }
    /** 
 * {@inheritDoc}
 * @since 1.2
 */
    public void setCurve(    double x1,    double y1,    double ctrlx1,    double ctrly1,    double ctrlx2,    double ctrly2,    double x2,    double y2){
      this.x1=(float)x1;
      this.y1=(float)y1;
      this.ctrlx1=(float)ctrlx1;
      this.ctrly1=(float)ctrly1;
      this.ctrlx2=(float)ctrlx2;
      this.ctrly2=(float)ctrly2;
      this.x2=(float)x2;
      this.y2=(float)y2;
    }
    /** 
 * Sets the location of the end points and control points
 * of this curve to the specified {@code float} coordinates.
 * @param x1 the X coordinate used to set the start point
 * of this {@code CubicCurve2D}
 * @param y1 the Y coordinate used to set the start point
 * of this {@code CubicCurve2D}
 * @param ctrlx1 the X coordinate used to set the first control point
 * of this {@code CubicCurve2D}
 * @param ctrly1 the Y coordinate used to set the first control point
 * of this {@code CubicCurve2D}
 * @param ctrlx2 the X coordinate used to set the second control point
 * of this {@code CubicCurve2D}
 * @param ctrly2 the Y coordinate used to set the second control point
 * of this {@code CubicCurve2D}
 * @param x2 the X coordinate used to set the end point
 * of this {@code CubicCurve2D}
 * @param y2 the Y coordinate used to set the end point
 * of this {@code CubicCurve2D}
 * @since 1.2
 */
    public void setCurve(    float x1,    float y1,    float ctrlx1,    float ctrly1,    float ctrlx2,    float ctrly2,    float x2,    float y2){
      this.x1=x1;
      this.y1=y1;
      this.ctrlx1=ctrlx1;
      this.ctrly1=ctrly1;
      this.ctrlx2=ctrlx2;
      this.ctrly2=ctrly2;
      this.x2=x2;
      this.y2=y2;
    }
    /** 
 * {@inheritDoc}
 * @since 1.2
 */
    public Rectangle2D getBounds2D(){
      float left=Math.min(Math.min(x1,x2),Math.min(ctrlx1,ctrlx2));
      float top=Math.min(Math.min(y1,y2),Math.min(ctrly1,ctrly2));
      float right=Math.max(Math.max(x1,x2),Math.max(ctrlx1,ctrlx2));
      float bottom=Math.max(Math.max(y1,y2),Math.max(ctrly1,ctrly2));
      return new Rectangle2D.Float(left,top,right - left,bottom - top);
    }
    private static final long serialVersionUID=-1272015596714244385L;
  }
  /** 
 * A cubic parametric curve segment specified with{@code double} coordinates.
 * @since 1.2
 */
public static class Double extends CubicCurve2D implements Serializable {
    /** 
 * The X coordinate of the start point
 * of the cubic curve segment.
 * @since 1.2
 * @serial
 */
    public double x1;
    /** 
 * The Y coordinate of the start point
 * of the cubic curve segment.
 * @since 1.2
 * @serial
 */
    public double y1;
    /** 
 * The X coordinate of the first control point
 * of the cubic curve segment.
 * @since 1.2
 * @serial
 */
    public double ctrlx1;
    /** 
 * The Y coordinate of the first control point
 * of the cubic curve segment.
 * @since 1.2
 * @serial
 */
    public double ctrly1;
    /** 
 * The X coordinate of the second control point
 * of the cubic curve segment.
 * @since 1.2
 * @serial
 */
    public double ctrlx2;
    /** 
 * The Y coordinate of the second control point
 * of the cubic curve segment.
 * @since 1.2
 * @serial
 */
    public double ctrly2;
    /** 
 * The X coordinate of the end point
 * of the cubic curve segment.
 * @since 1.2
 * @serial
 */
    public double x2;
    /** 
 * The Y coordinate of the end point
 * of the cubic curve segment.
 * @since 1.2
 * @serial
 */
    public double y2;
    /** 
 * Constructs and initializes a CubicCurve with coordinates
 * (0, 0, 0, 0, 0, 0, 0, 0).
 * @since 1.2
 */
    public Double(){
    }
    /** 
 * Constructs and initializes a {@code CubicCurve2D} from
 * the specified {@code double} coordinates.
 * @param x1 the X coordinate for the start point
 * of the resulting {@code CubicCurve2D}
 * @param y1 the Y coordinate for the start point
 * of the resulting {@code CubicCurve2D}
 * @param ctrlx1 the X coordinate for the first control point
 * of the resulting {@code CubicCurve2D}
 * @param ctrly1 the Y coordinate for the first control point
 * of the resulting {@code CubicCurve2D}
 * @param ctrlx2 the X coordinate for the second control point
 * of the resulting {@code CubicCurve2D}
 * @param ctrly2 the Y coordinate for the second control point
 * of the resulting {@code CubicCurve2D}
 * @param x2 the X coordinate for the end point
 * of the resulting {@code CubicCurve2D}
 * @param y2 the Y coordinate for the end point
 * of the resulting {@code CubicCurve2D}
 * @since 1.2
 */
    public Double(    double x1,    double y1,    double ctrlx1,    double ctrly1,    double ctrlx2,    double ctrly2,    double x2,    double y2){
      setCurve(x1,y1,ctrlx1,ctrly1,ctrlx2,ctrly2,x2,y2);
    }
    /** 
 * {@inheritDoc}
 * @since 1.2
 */
    public double getX1(){
      return x1;
    }
    /** 
 * {@inheritDoc}
 * @since 1.2
 */
    public double getY1(){
      return y1;
    }
    /** 
 * {@inheritDoc}
 * @since 1.2
 */
    public Point2D getP1(){
      return new Point2D.Double(x1,y1);
    }
    /** 
 * {@inheritDoc}
 * @since 1.2
 */
    public double getCtrlX1(){
      return ctrlx1;
    }
    /** 
 * {@inheritDoc}
 * @since 1.2
 */
    public double getCtrlY1(){
      return ctrly1;
    }
    /** 
 * {@inheritDoc}
 * @since 1.2
 */
    public Point2D getCtrlP1(){
      return new Point2D.Double(ctrlx1,ctrly1);
    }
    /** 
 * {@inheritDoc}
 * @since 1.2
 */
    public double getCtrlX2(){
      return ctrlx2;
    }
    /** 
 * {@inheritDoc}
 * @since 1.2
 */
    public double getCtrlY2(){
      return ctrly2;
    }
    /** 
 * {@inheritDoc}
 * @since 1.2
 */
    public Point2D getCtrlP2(){
      return new Point2D.Double(ctrlx2,ctrly2);
    }
    /** 
 * {@inheritDoc}
 * @since 1.2
 */
    public double getX2(){
      return x2;
    }
    /** 
 * {@inheritDoc}
 * @since 1.2
 */
    public double getY2(){
      return y2;
    }
    /** 
 * {@inheritDoc}
 * @since 1.2
 */
    public Point2D getP2(){
      return new Point2D.Double(x2,y2);
    }
    /** 
 * {@inheritDoc}
 * @since 1.2
 */
    public void setCurve(    double x1,    double y1,    double ctrlx1,    double ctrly1,    double ctrlx2,    double ctrly2,    double x2,    double y2){
      this.x1=x1;
      this.y1=y1;
      this.ctrlx1=ctrlx1;
      this.ctrly1=ctrly1;
      this.ctrlx2=ctrlx2;
      this.ctrly2=ctrly2;
      this.x2=x2;
      this.y2=y2;
    }
    /** 
 * {@inheritDoc}
 * @since 1.2
 */
    public Rectangle2D getBounds2D(){
      double left=Math.min(Math.min(x1,x2),Math.min(ctrlx1,ctrlx2));
      double top=Math.min(Math.min(y1,y2),Math.min(ctrly1,ctrly2));
      double right=Math.max(Math.max(x1,x2),Math.max(ctrlx1,ctrlx2));
      double bottom=Math.max(Math.max(y1,y2),Math.max(ctrly1,ctrly2));
      return new Rectangle2D.Double(left,top,right - left,bottom - top);
    }
    private static final long serialVersionUID=-4202960122839707295L;
  }
  /** 
 * This is an abstract class that cannot be instantiated directly.
 * Type-specific implementation subclasses are available for
 * instantiation and provide a number of formats for storing
 * the information necessary to satisfy the various accessor
 * methods below.
 * @see java.awt.geom.CubicCurve2D.Float
 * @see java.awt.geom.CubicCurve2D.Double
 * @since 1.2
 */
  protected CubicCurve2D(){
  }
  /** 
 * Returns the X coordinate of the start point in double precision.
 * @return the X coordinate of the start point of the{@code CubicCurve2D}.
 * @since 1.2
 */
  public abstract double getX1();
  /** 
 * Returns the Y coordinate of the start point in double precision.
 * @return the Y coordinate of the start point of the{@code CubicCurve2D}.
 * @since 1.2
 */
  public abstract double getY1();
  /** 
 * Returns the start point.
 * @return a {@code Point2D} that is the start point of
 * the {@code CubicCurve2D}.
 * @since 1.2
 */
  public abstract Point2D getP1();
  /** 
 * Returns the X coordinate of the first control point in double precision.
 * @return the X coordinate of the first control point of the{@code CubicCurve2D}.
 * @since 1.2
 */
  public abstract double getCtrlX1();
  /** 
 * Returns the Y coordinate of the first control point in double precision.
 * @return the Y coordinate of the first control point of the{@code CubicCurve2D}.
 * @since 1.2
 */
  public abstract double getCtrlY1();
  /** 
 * Returns the first control point.
 * @return a {@code Point2D} that is the first control point of
 * the {@code CubicCurve2D}.
 * @since 1.2
 */
  public abstract Point2D getCtrlP1();
  /** 
 * Returns the X coordinate of the second control point
 * in double precision.
 * @return the X coordinate of the second control point of the{@code CubicCurve2D}.
 * @since 1.2
 */
  public abstract double getCtrlX2();
  /** 
 * Returns the Y coordinate of the second control point
 * in double precision.
 * @return the Y coordinate of the second control point of the{@code CubicCurve2D}.
 * @since 1.2
 */
  public abstract double getCtrlY2();
  /** 
 * Returns the second control point.
 * @return a {@code Point2D} that is the second control point of
 * the {@code CubicCurve2D}.
 * @since 1.2
 */
  public abstract Point2D getCtrlP2();
  /** 
 * Returns the X coordinate of the end point in double precision.
 * @return the X coordinate of the end point of the{@code CubicCurve2D}.
 * @since 1.2
 */
  public abstract double getX2();
  /** 
 * Returns the Y coordinate of the end point in double precision.
 * @return the Y coordinate of the end point of the{@code CubicCurve2D}.
 * @since 1.2
 */
  public abstract double getY2();
  /** 
 * Returns the end point.
 * @return a {@code Point2D} that is the end point of
 * the {@code CubicCurve2D}.
 * @since 1.2
 */
  public abstract Point2D getP2();
  /** 
 * Sets the location of the end points and control points of this curve
 * to the specified double coordinates.
 * @param x1 the X coordinate used to set the start point
 * of this {@code CubicCurve2D}
 * @param y1 the Y coordinate used to set the start point
 * of this {@code CubicCurve2D}
 * @param ctrlx1 the X coordinate used to set the first control point
 * of this {@code CubicCurve2D}
 * @param ctrly1 the Y coordinate used to set the first control point
 * of this {@code CubicCurve2D}
 * @param ctrlx2 the X coordinate used to set the second control point
 * of this {@code CubicCurve2D}
 * @param ctrly2 the Y coordinate used to set the second control point
 * of this {@code CubicCurve2D}
 * @param x2 the X coordinate used to set the end point
 * of this {@code CubicCurve2D}
 * @param y2 the Y coordinate used to set the end point
 * of this {@code CubicCurve2D}
 * @since 1.2
 */
  public abstract void setCurve(  double x1,  double y1,  double ctrlx1,  double ctrly1,  double ctrlx2,  double ctrly2,  double x2,  double y2);
  /** 
 * Sets the location of the end points and control points of this curve
 * to the double coordinates at the specified offset in the specified
 * array.
 * @param coords a double array containing coordinates
 * @param offset the index of <code>coords</code> from which to begin
 * setting the end points and control points of this curve
 * to the coordinates contained in <code>coords</code>
 * @since 1.2
 */
  public void setCurve(  double[] coords,  int offset){
    setCurve(coords[offset + 0],coords[offset + 1],coords[offset + 2],coords[offset + 3],coords[offset + 4],coords[offset + 5],coords[offset + 6],coords[offset + 7]);
  }
  /** 
 * Sets the location of the end points and control points of this curve
 * to the specified <code>Point2D</code> coordinates.
 * @param p1 the first specified <code>Point2D</code> used to set the
 * start point of this curve
 * @param cp1 the second specified <code>Point2D</code> used to set the
 * first control point of this curve
 * @param cp2 the third specified <code>Point2D</code> used to set the
 * second control point of this curve
 * @param p2 the fourth specified <code>Point2D</code> used to set the
 * end point of this curve
 * @since 1.2
 */
  public void setCurve(  Point2D p1,  Point2D cp1,  Point2D cp2,  Point2D p2){
    setCurve(p1.getX(),p1.getY(),cp1.getX(),cp1.getY(),cp2.getX(),cp2.getY(),p2.getX(),p2.getY());
  }
  /** 
 * Sets the location of the end points and control points of this curve
 * to the coordinates of the <code>Point2D</code> objects at the specified
 * offset in the specified array.
 * @param pts an array of <code>Point2D</code> objects
 * @param offset  the index of <code>pts</code> from which to begin setting
 * the end points and control points of this curve to the
 * points contained in <code>pts</code>
 * @since 1.2
 */
  public void setCurve(  Point2D[] pts,  int offset){
    setCurve(pts[offset + 0].getX(),pts[offset + 0].getY(),pts[offset + 1].getX(),pts[offset + 1].getY(),pts[offset + 2].getX(),pts[offset + 2].getY(),pts[offset + 3].getX(),pts[offset + 3].getY());
  }
  /** 
 * Sets the location of the end points and control points of this curve
 * to the same as those in the specified <code>CubicCurve2D</code>.
 * @param c the specified <code>CubicCurve2D</code>
 * @since 1.2
 */
  public void setCurve(  CubicCurve2D c){
    setCurve(c.getX1(),c.getY1(),c.getCtrlX1(),c.getCtrlY1(),c.getCtrlX2(),c.getCtrlY2(),c.getX2(),c.getY2());
  }
  /** 
 * Returns the square of the flatness of the cubic curve specified
 * by the indicated control points. The flatness is the maximum distance
 * of a control point from the line connecting the end points.
 * @param x1 the X coordinate that specifies the start point
 * of a {@code CubicCurve2D}
 * @param y1 the Y coordinate that specifies the start point
 * of a {@code CubicCurve2D}
 * @param ctrlx1 the X coordinate that specifies the first control point
 * of a {@code CubicCurve2D}
 * @param ctrly1 the Y coordinate that specifies the first control point
 * of a {@code CubicCurve2D}
 * @param ctrlx2 the X coordinate that specifies the second control point
 * of a {@code CubicCurve2D}
 * @param ctrly2 the Y coordinate that specifies the second control point
 * of a {@code CubicCurve2D}
 * @param x2 the X coordinate that specifies the end point
 * of a {@code CubicCurve2D}
 * @param y2 the Y coordinate that specifies the end point
 * of a {@code CubicCurve2D}
 * @return the square of the flatness of the {@code CubicCurve2D}represented by the specified coordinates.
 * @since 1.2
 */
  public static double getFlatnessSq(  double x1,  double y1,  double ctrlx1,  double ctrly1,  double ctrlx2,  double ctrly2,  double x2,  double y2){
    return Math.max(Line2D.ptSegDistSq(x1,y1,x2,y2,ctrlx1,ctrly1),Line2D.ptSegDistSq(x1,y1,x2,y2,ctrlx2,ctrly2));
  }
  /** 
 * Returns the flatness of the cubic curve specified
 * by the indicated control points. The flatness is the maximum distance
 * of a control point from the line connecting the end points.
 * @param x1 the X coordinate that specifies the start point
 * of a {@code CubicCurve2D}
 * @param y1 the Y coordinate that specifies the start point
 * of a {@code CubicCurve2D}
 * @param ctrlx1 the X coordinate that specifies the first control point
 * of a {@code CubicCurve2D}
 * @param ctrly1 the Y coordinate that specifies the first control point
 * of a {@code CubicCurve2D}
 * @param ctrlx2 the X coordinate that specifies the second control point
 * of a {@code CubicCurve2D}
 * @param ctrly2 the Y coordinate that specifies the second control point
 * of a {@code CubicCurve2D}
 * @param x2 the X coordinate that specifies the end point
 * of a {@code CubicCurve2D}
 * @param y2 the Y coordinate that specifies the end point
 * of a {@code CubicCurve2D}
 * @return the flatness of the {@code CubicCurve2D}represented by the specified coordinates.
 * @since 1.2
 */
  public static double getFlatness(  double x1,  double y1,  double ctrlx1,  double ctrly1,  double ctrlx2,  double ctrly2,  double x2,  double y2){
    return Math.sqrt(getFlatnessSq(x1,y1,ctrlx1,ctrly1,ctrlx2,ctrly2,x2,y2));
  }
  /** 
 * Returns the square of the flatness of the cubic curve specified
 * by the control points stored in the indicated array at the
 * indicated index. The flatness is the maximum distance
 * of a control point from the line connecting the end points.
 * @param coords an array containing coordinates
 * @param offset the index of <code>coords</code> from which to begin
 * getting the end points and control points of the curve
 * @return the square of the flatness of the <code>CubicCurve2D</code>
 * specified by the coordinates in <code>coords</code> at
 * the specified offset.
 * @since 1.2
 */
  public static double getFlatnessSq(  double coords[],  int offset){
    return getFlatnessSq(coords[offset + 0],coords[offset + 1],coords[offset + 2],coords[offset + 3],coords[offset + 4],coords[offset + 5],coords[offset + 6],coords[offset + 7]);
  }
  /** 
 * Returns the flatness of the cubic curve specified
 * by the control points stored in the indicated array at the
 * indicated index.  The flatness is the maximum distance
 * of a control point from the line connecting the end points.
 * @param coords an array containing coordinates
 * @param offset the index of <code>coords</code> from which to begin
 * getting the end points and control points of the curve
 * @return the flatness of the <code>CubicCurve2D</code>
 * specified by the coordinates in <code>coords</code> at
 * the specified offset.
 * @since 1.2
 */
  public static double getFlatness(  double coords[],  int offset){
    return getFlatness(coords[offset + 0],coords[offset + 1],coords[offset + 2],coords[offset + 3],coords[offset + 4],coords[offset + 5],coords[offset + 6],coords[offset + 7]);
  }
  /** 
 * Returns the square of the flatness of this curve.  The flatness is the
 * maximum distance of a control point from the line connecting the
 * end points.
 * @return the square of the flatness of this curve.
 * @since 1.2
 */
  public double getFlatnessSq(){
    return getFlatnessSq(getX1(),getY1(),getCtrlX1(),getCtrlY1(),getCtrlX2(),getCtrlY2(),getX2(),getY2());
  }
  /** 
 * Returns the flatness of this curve.  The flatness is the
 * maximum distance of a control point from the line connecting the
 * end points.
 * @return the flatness of this curve.
 * @since 1.2
 */
  public double getFlatness(){
    return getFlatness(getX1(),getY1(),getCtrlX1(),getCtrlY1(),getCtrlX2(),getCtrlY2(),getX2(),getY2());
  }
  /** 
 * Subdivides this cubic curve and stores the resulting two
 * subdivided curves into the left and right curve parameters.
 * Either or both of the left and right objects may be the same
 * as this object or null.
 * @param left the cubic curve object for storing for the left or
 * first half of the subdivided curve
 * @param right the cubic curve object for storing for the right or
 * second half of the subdivided curve
 * @since 1.2
 */
  public void subdivide(  CubicCurve2D left,  CubicCurve2D right){
    subdivide(this,left,right);
  }
  /** 
 * Subdivides the cubic curve specified by the <code>src</code> parameter
 * and stores the resulting two subdivided curves into the
 * <code>left</code> and <code>right</code> curve parameters.
 * Either or both of the <code>left</code> and <code>right</code> objects
 * may be the same as the <code>src</code> object or <code>null</code>.
 * @param src the cubic curve to be subdivided
 * @param left the cubic curve object for storing the left or
 * first half of the subdivided curve
 * @param right the cubic curve object for storing the right or
 * second half of the subdivided curve
 * @since 1.2
 */
  public static void subdivide(  CubicCurve2D src,  CubicCurve2D left,  CubicCurve2D right){
    double x1=src.getX1();
    double y1=src.getY1();
    double ctrlx1=src.getCtrlX1();
    double ctrly1=src.getCtrlY1();
    double ctrlx2=src.getCtrlX2();
    double ctrly2=src.getCtrlY2();
    double x2=src.getX2();
    double y2=src.getY2();
    double centerx=(ctrlx1 + ctrlx2) / 2.0;
    double centery=(ctrly1 + ctrly2) / 2.0;
    ctrlx1=(x1 + ctrlx1) / 2.0;
    ctrly1=(y1 + ctrly1) / 2.0;
    ctrlx2=(x2 + ctrlx2) / 2.0;
    ctrly2=(y2 + ctrly2) / 2.0;
    double ctrlx12=(ctrlx1 + centerx) / 2.0;
    double ctrly12=(ctrly1 + centery) / 2.0;
    double ctrlx21=(ctrlx2 + centerx) / 2.0;
    double ctrly21=(ctrly2 + centery) / 2.0;
    centerx=(ctrlx12 + ctrlx21) / 2.0;
    centery=(ctrly12 + ctrly21) / 2.0;
    if (left != null) {
      left.setCurve(x1,y1,ctrlx1,ctrly1,ctrlx12,ctrly12,centerx,centery);
    }
    if (right != null) {
      right.setCurve(centerx,centery,ctrlx21,ctrly21,ctrlx2,ctrly2,x2,y2);
    }
  }
  /** 
 * Subdivides the cubic curve specified by the coordinates
 * stored in the <code>src</code> array at indices <code>srcoff</code>
 * through (<code>srcoff</code>&nbsp;+&nbsp;7) and stores the
 * resulting two subdivided curves into the two result arrays at the
 * corresponding indices.
 * Either or both of the <code>left</code> and <code>right</code>
 * arrays may be <code>null</code> or a reference to the same array
 * as the <code>src</code> array.
 * Note that the last point in the first subdivided curve is the
 * same as the first point in the second subdivided curve. Thus,
 * it is possible to pass the same array for <code>left</code>
 * and <code>right</code> and to use offsets, such as <code>rightoff</code>
 * equals (<code>leftoff</code> + 6), in order
 * to avoid allocating extra storage for this common point.
 * @param src the array holding the coordinates for the source curve
 * @param srcoff the offset into the array of the beginning of the
 * the 6 source coordinates
 * @param left the array for storing the coordinates for the first
 * half of the subdivided curve
 * @param leftoff the offset into the array of the beginning of the
 * the 6 left coordinates
 * @param right the array for storing the coordinates for the second
 * half of the subdivided curve
 * @param rightoff the offset into the array of the beginning of the
 * the 6 right coordinates
 * @since 1.2
 */
  public static void subdivide(  double src[],  int srcoff,  double left[],  int leftoff,  double right[],  int rightoff){
    double x1=src[srcoff + 0];
    double y1=src[srcoff + 1];
    double ctrlx1=src[srcoff + 2];
    double ctrly1=src[srcoff + 3];
    double ctrlx2=src[srcoff + 4];
    double ctrly2=src[srcoff + 5];
    double x2=src[srcoff + 6];
    double y2=src[srcoff + 7];
    if (left != null) {
      left[leftoff + 0]=x1;
      left[leftoff + 1]=y1;
    }
    if (right != null) {
      right[rightoff + 6]=x2;
      right[rightoff + 7]=y2;
    }
    x1=(x1 + ctrlx1) / 2.0;
    y1=(y1 + ctrly1) / 2.0;
    x2=(x2 + ctrlx2) / 2.0;
    y2=(y2 + ctrly2) / 2.0;
    double centerx=(ctrlx1 + ctrlx2) / 2.0;
    double centery=(ctrly1 + ctrly2) / 2.0;
    ctrlx1=(x1 + centerx) / 2.0;
    ctrly1=(y1 + centery) / 2.0;
    ctrlx2=(x2 + centerx) / 2.0;
    ctrly2=(y2 + centery) / 2.0;
    centerx=(ctrlx1 + ctrlx2) / 2.0;
    centery=(ctrly1 + ctrly2) / 2.0;
    if (left != null) {
      left[leftoff + 2]=x1;
      left[leftoff + 3]=y1;
      left[leftoff + 4]=ctrlx1;
      left[leftoff + 5]=ctrly1;
      left[leftoff + 6]=centerx;
      left[leftoff + 7]=centery;
    }
    if (right != null) {
      right[rightoff + 0]=centerx;
      right[rightoff + 1]=centery;
      right[rightoff + 2]=ctrlx2;
      right[rightoff + 3]=ctrly2;
      right[rightoff + 4]=x2;
      right[rightoff + 5]=y2;
    }
  }
  /** 
 * Solves the cubic whose coefficients are in the <code>eqn</code>
 * array and places the non-complex roots back into the same array,
 * returning the number of roots.  The solved cubic is represented
 * by the equation:
 * <pre>
 * eqn = {c, b, a, d}
 * dx^3 + ax^2 + bx + c = 0
 * </pre>
 * A return value of -1 is used to distinguish a constant equation
 * that might be always 0 or never 0 from an equation that has no
 * zeroes.
 * @param eqn an array containing coefficients for a cubic
 * @return the number of roots, or -1 if the equation is a constant.
 * @since 1.2
 */
  public static int solveCubic(  double eqn[]){
    return solveCubic(eqn,eqn);
  }
  /** 
 * Solve the cubic whose coefficients are in the <code>eqn</code>
 * array and place the non-complex roots into the <code>res</code>
 * array, returning the number of roots.
 * The cubic solved is represented by the equation:
 * eqn = {c, b, a, d}
 * dx^3 + ax^2 + bx + c = 0
 * A return value of -1 is used to distinguish a constant equation,
 * which may be always 0 or never 0, from an equation which has no
 * zeroes.
 * @param eqn the specified array of coefficients to use to solve
 * the cubic equation
 * @param res the array that contains the non-complex roots
 * resulting from the solution of the cubic equation
 * @return the number of roots, or -1 if the equation is a constant
 * @since 1.3
 */
  public static int solveCubic(  double eqn[],  double res[]){
    final double d=eqn[3];
    if (d == 0) {
      return QuadCurve2D.solveQuadratic(eqn,res);
    }
    final double A=eqn[2] / d;
    final double B=eqn[1] / d;
    final double C=eqn[0] / d;
    double sq_A=A * A;
    double p=1.0 / 3 * (-1.0 / 3 * sq_A + B);
    double q=1.0 / 2 * (2.0 / 27 * A * sq_A - 1.0 / 3 * A * B + C);
    double cb_p=p * p * p;
    double D=q * q + cb_p;
    final double sub=1.0 / 3 * A;
    int num;
    if (D < 0) {
      double phi=1.0 / 3 * Math.acos(-q / Math.sqrt(-cb_p));
      double t=2 * Math.sqrt(-p);
      if (res == eqn) {
        eqn=Arrays.copyOf(eqn,4);
      }
      res[0]=(t * Math.cos(phi));
      res[1]=(-t * Math.cos(phi + Math.PI / 3));
      res[2]=(-t * Math.cos(phi - Math.PI / 3));
      num=3;
      for (int i=0; i < num; ++i) {
        res[i]-=sub;
      }
    }
 else {
      double sqrt_D=Math.sqrt(D);
      double u=Math.cbrt(sqrt_D - q);
      double v=-Math.cbrt(sqrt_D + q);
      double uv=u + v;
      num=1;
      double err=1200000000 * ulp(abs(uv) + abs(sub));
      if (iszero(D,err) || within(u,v,err)) {
        if (res == eqn) {
          eqn=Arrays.copyOf(eqn,4);
        }
        res[1]=-(uv / 2) - sub;
        num=2;
      }
      res[0]=uv - sub;
    }
    if (num > 1) {
      num=fixRoots(eqn,res,num);
    }
    if (num > 2 && (res[2] == res[1] || res[2] == res[0])) {
      num--;
    }
    if (num > 1 && res[1] == res[0]) {
      res[1]=res[--num];
    }
    return num;
  }
  private static int fixRoots(  double[] eqn,  double[] res,  int num){
    double[] intervals={eqn[1],2 * eqn[2],3 * eqn[3]};
    int critCount=QuadCurve2D.solveQuadratic(intervals,intervals);
    if (critCount == 2 && intervals[0] == intervals[1]) {
      critCount--;
    }
    if (critCount == 2 && intervals[0] > intervals[1]) {
      double tmp=intervals[0];
      intervals[0]=intervals[1];
      intervals[1]=tmp;
    }
    if (num == 3) {
      double xe=getRootUpperBound(eqn);
      double x0=-xe;
      Arrays.sort(res,0,num);
      if (critCount == 2) {
        res[0]=refineRootWithHint(eqn,x0,intervals[0],res[0]);
        res[1]=refineRootWithHint(eqn,intervals[0],intervals[1],res[1]);
        res[2]=refineRootWithHint(eqn,intervals[1],xe,res[2]);
        return 3;
      }
 else       if (critCount == 1) {
        double fxe=eqn[3];
        double fx0=-fxe;
        double x1=intervals[0];
        double fx1=solveEqn(eqn,3,x1);
        if (oppositeSigns(fx0,fx1)) {
          res[0]=bisectRootWithHint(eqn,x0,x1,res[0]);
        }
 else         if (oppositeSigns(fx1,fxe)) {
          res[0]=bisectRootWithHint(eqn,x1,xe,res[2]);
        }
 else {
          res[0]=x1;
        }
      }
 else       if (critCount == 0) {
        res[0]=bisectRootWithHint(eqn,x0,xe,res[1]);
      }
    }
 else     if (num == 2 && critCount == 2) {
      double goodRoot=res[0];
      double badRoot=res[1];
      double x1=intervals[0];
      double x2=intervals[1];
      double x=abs(x1 - goodRoot) > abs(x2 - goodRoot) ? x1 : x2;
      double fx=solveEqn(eqn,3,x);
      if (iszero(fx,10000000 * ulp(x))) {
        double badRootVal=solveEqn(eqn,3,badRoot);
        res[1]=abs(badRootVal) < abs(fx) ? badRoot : x;
        return 2;
      }
    }
    return 1;
  }
  private static double refineRootWithHint(  double[] eqn,  double min,  double max,  double t){
    if (!inInterval(t,min,max)) {
      return t;
    }
    double[] deriv={eqn[1],2 * eqn[2],3 * eqn[3]};
    double origt=t;
    for (int i=0; i < 3; i++) {
      double slope=solveEqn(deriv,2,t);
      double y=solveEqn(eqn,3,t);
      double delta=-(y / slope);
      double newt=t + delta;
      if (slope == 0 || y == 0 || t == newt) {
        break;
      }
      t=newt;
    }
    if (within(t,origt,1000 * ulp(origt)) && inInterval(t,min,max)) {
      return t;
    }
    return origt;
  }
  private static double bisectRootWithHint(  double[] eqn,  double x0,  double xe,  double hint){
    double delta1=Math.min(abs(hint - x0) / 64,0.0625);
    double delta2=Math.min(abs(hint - xe) / 64,0.0625);
    double x02=hint - delta1;
    double xe2=hint + delta2;
    double fx02=solveEqn(eqn,3,x02);
    double fxe2=solveEqn(eqn,3,xe2);
    while (oppositeSigns(fx02,fxe2)) {
      if (x02 >= xe2) {
        return x02;
      }
      x0=x02;
      xe=xe2;
      delta1/=64;
      delta2/=64;
      x02=hint - delta1;
      xe2=hint + delta2;
      fx02=solveEqn(eqn,3,x02);
      fxe2=solveEqn(eqn,3,xe2);
    }
    if (fx02 == 0) {
      return x02;
    }
    if (fxe2 == 0) {
      return xe2;
    }
    return bisectRoot(eqn,x0,xe);
  }
  private static double bisectRoot(  double[] eqn,  double x0,  double xe){
    double fx0=solveEqn(eqn,3,x0);
    double m=x0 + (xe - x0) / 2;
    while (m != x0 && m != xe) {
      double fm=solveEqn(eqn,3,m);
      if (fm == 0) {
        return m;
      }
      if (oppositeSigns(fx0,fm)) {
        xe=m;
      }
 else {
        fx0=fm;
        x0=m;
      }
      m=x0 + (xe - x0) / 2;
    }
    return m;
  }
  private static boolean inInterval(  double t,  double min,  double max){
    return min <= t && t <= max;
  }
  private static boolean within(  double x,  double y,  double err){
    double d=y - x;
    return (d <= err && d >= -err);
  }
  private static boolean iszero(  double x,  double err){
    return within(x,0,err);
  }
  private static boolean oppositeSigns(  double x1,  double x2){
    return (x1 < 0 && x2 > 0) || (x1 > 0 && x2 < 0);
  }
  private static double solveEqn(  double eqn[],  int order,  double t){
    double v=eqn[order];
    while (--order >= 0) {
      v=v * t + eqn[order];
    }
    return v;
  }
  private static double getRootUpperBound(  double[] eqn){
    double d=eqn[3];
    double a=eqn[2];
    double b=eqn[1];
    double c=eqn[0];
    double M=1 + max(max(abs(a),abs(b)),abs(c)) / abs(d);
    M+=ulp(M) + 1;
    return M;
  }
  /** 
 * {@inheritDoc}
 * @since 1.2
 */
  public boolean contains(  double x,  double y){
    if (!(x * 0.0 + y * 0.0 == 0.0)) {
      return false;
    }
    double x1=getX1();
    double y1=getY1();
    double x2=getX2();
    double y2=getY2();
    int crossings=(Curve.pointCrossingsForLine(x,y,x1,y1,x2,y2) + Curve.pointCrossingsForCubic(x,y,x1,y1,getCtrlX1(),getCtrlY1(),getCtrlX2(),getCtrlY2(),x2,y2,0));
    return ((crossings & 1) == 1);
  }
  /** 
 * {@inheritDoc}
 * @since 1.2
 */
  public boolean contains(  Point2D p){
    return contains(p.getX(),p.getY());
  }
  /** 
 * {@inheritDoc}
 * @since 1.2
 */
  public boolean intersects(  double x,  double y,  double w,  double h){
    if (w <= 0 || h <= 0) {
      return false;
    }
    int numCrossings=rectCrossings(x,y,w,h);
    return numCrossings != 0;
  }
  /** 
 * {@inheritDoc}
 * @since 1.2
 */
  public boolean intersects(  Rectangle2D r){
    return intersects(r.getX(),r.getY(),r.getWidth(),r.getHeight());
  }
  /** 
 * {@inheritDoc}
 * @since 1.2
 */
  public boolean contains(  double x,  double y,  double w,  double h){
    if (w <= 0 || h <= 0) {
      return false;
    }
    int numCrossings=rectCrossings(x,y,w,h);
    return !(numCrossings == 0 || numCrossings == Curve.RECT_INTERSECTS);
  }
  private int rectCrossings(  double x,  double y,  double w,  double h){
    int crossings=0;
    if (!(getX1() == getX2() && getY1() == getY2())) {
      crossings=Curve.rectCrossingsForLine(crossings,x,y,x + w,y + h,getX1(),getY1(),getX2(),getY2());
      if (crossings == Curve.RECT_INTERSECTS) {
        return crossings;
      }
    }
    return Curve.rectCrossingsForCubic(crossings,x,y,x + w,y + h,getX2(),getY2(),getCtrlX2(),getCtrlY2(),getCtrlX1(),getCtrlY1(),getX1(),getY1(),0);
  }
  /** 
 * {@inheritDoc}
 * @since 1.2
 */
  public boolean contains(  Rectangle2D r){
    return contains(r.getX(),r.getY(),r.getWidth(),r.getHeight());
  }
  /** 
 * {@inheritDoc}
 * @since 1.2
 */
  public Rectangle getBounds(){
    return getBounds2D().getBounds();
  }
  /** 
 * Returns an iteration object that defines the boundary of the
 * shape.
 * The iterator for this class is not multi-threaded safe,
 * which means that this <code>CubicCurve2D</code> class does not
 * guarantee that modifications to the geometry of this
 * <code>CubicCurve2D</code> object do not affect any iterations of
 * that geometry that are already in process.
 * @param at an optional <code>AffineTransform</code> to be applied to the
 * coordinates as they are returned in the iteration, or <code>null</code>
 * if untransformed coordinates are desired
 * @return    the <code>PathIterator</code> object that returns the
 * geometry of the outline of this <code>CubicCurve2D</code>, one
 * segment at a time.
 * @since 1.2
 */
  public PathIterator getPathIterator(  AffineTransform at){
    return new CubicIterator(this,at);
  }
  /** 
 * Return an iteration object that defines the boundary of the
 * flattened shape.
 * The iterator for this class is not multi-threaded safe,
 * which means that this <code>CubicCurve2D</code> class does not
 * guarantee that modifications to the geometry of this
 * <code>CubicCurve2D</code> object do not affect any iterations of
 * that geometry that are already in process.
 * @param at an optional <code>AffineTransform</code> to be applied to the
 * coordinates as they are returned in the iteration, or <code>null</code>
 * if untransformed coordinates are desired
 * @param flatness the maximum amount that the control points
 * for a given curve can vary from colinear before a subdivided
 * curve is replaced by a straight line connecting the end points
 * @return    the <code>PathIterator</code> object that returns the
 * geometry of the outline of this <code>CubicCurve2D</code>,
 * one segment at a time.
 * @since 1.2
 */
  public PathIterator getPathIterator(  AffineTransform at,  double flatness){
    return new FlatteningPathIterator(getPathIterator(at),flatness);
  }
  /** 
 * Creates a new object of the same class as this object.
 * @return     a clone of this instance.
 * @exception OutOfMemoryError            if there is not enough memory.
 * @see java.lang.Cloneable
 * @since      1.2
 */
  public Object clone(){
    try {
      return super.clone();
    }
 catch (    CloneNotSupportedException e) {
      throw new InternalError();
    }
  }
}
