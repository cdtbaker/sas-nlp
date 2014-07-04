package edu.umd.cs.piccolox.util;
import java.awt.geom.AffineTransform;
/** 
 * Minimal interface that a changeable sequence of points must provide.
 */
public interface MutablePoints extends Points {
  /** 
 * Sets the coordinates for the point at the given index.
 * @param i index of point
 * @param x x component of the point's coordinates
 * @param y y component of the point's coordinates
 */
  void setPoint(  int i,  double x,  double y);
  /** 
 * Inserts a point at the specified position.
 * @param pos position at which to insert the point
 * @param x x component of the point's coordinates
 * @param y y component of the point's coordinates
 */
  void addPoint(  int pos,  double x,  double y);
  /** 
 * Removes a subsequence of points.
 * @param pos position to start removing points
 * @param num number of points to remove
 */
  void removePoints(  int pos,  int num);
  /** 
 * Modifies all points by applying the transform to them.
 * @param t transformto apply to the points
 */
  void transformPoints(  AffineTransform t);
}
