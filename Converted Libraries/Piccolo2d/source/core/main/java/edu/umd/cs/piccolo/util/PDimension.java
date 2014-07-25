package edu.umd.cs.piccolo.util;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.io.Serializable;
/** 
 * <b>PDimension</b> this class should be removed once a concrete Dimension2D
 * that supports doubles is added to java.
 * <P>
 * @version 1.0
 * @author Jesse Grosjean
 */
public class PDimension extends Dimension2D implements Serializable {
  /** 
 * Allows for future serialization code to understand versioned binary
 * formats.
 */
  private static final long serialVersionUID=1L;
  /** 
 * The width of the dimension. 
 */
  public double width;
  /** 
 * The height of the dimension. 
 */
  public double height;
  /** 
 * Returns a dimension with no width or height.
 */
  public PDimension(){
    super();
  }
  /** 
 * Copies the provided dimension.
 * @param aDimension dimension to copy
 */
  public PDimension(  final Dimension2D aDimension){
    this(aDimension.getWidth(),aDimension.getHeight());
  }
  /** 
 * Creates a dimension with the provided dimensions.
 * @param aWidth desired width
 * @param aHeight desired height
 */
  public PDimension(  final double aWidth,  final double aHeight){
    super();
    width=aWidth;
    height=aHeight;
  }
  /** 
 * Creates a dimension that's the size of a rectangel with the points
 * provided as opposite corners.
 * @param p1 first point on rectangle
 * @param p2 point diagonally across from p1
 */
  public PDimension(  final Point2D p1,  final Point2D p2){
    width=p2.getX() - p1.getX();
    height=p2.getY() - p1.getY();
  }
  /** 
 * Returns the height of the dimension.
 * @return height height of the dimension
 */
  public double getHeight(){
    return height;
  }
  /** 
 * Returns the width of the dimension.
 * @return width width of the dimension
 */
  public double getWidth(){
    return width;
  }
  /** 
 * Resizes the dimension to have the dimensions provided.
 * @param aWidth desired width
 * @param aHeight desired height
 */
  public void setSize(  final double aWidth,  final double aHeight){
    width=aWidth;
    height=aHeight;
  }
  /** 
 * Returns a string representation of this dimension object.
 * @return string representation of this dimension object.
 */
  public String toString(){
    final StringBuffer result=new StringBuffer();
    result.append(super.toString().replaceAll(".*\\.",""));
    result.append('[');
    result.append("width=");
    result.append(width);
    result.append(",height=");
    result.append(height);
    result.append(']');
    return result.toString();
  }
}
