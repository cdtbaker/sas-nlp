package edu.umd.cs.piccolox.util;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
/** 
 * Interface for a sequence of points.
 */
public interface Points {
  /** 
 * Returns the number of points in the sequence.
 * @return number of points in the sequence
 */
  int getPointCount();
  /** 
 * Returns the x component of the point at the given index.
 * @param i index of desired point
 * @return x component of point
 */
  double getX(  int i);
  /** 
 * Returns the y component of the point at the given index.
 * @param i index of desired point
 * @return y component of point
 */
  double getY(  int i);
  /** 
 * Returns a point representation of the coordinates at the given index.
 * @param i index of desired point
 * @param dst output parameter into which the point's details will be
 * populated, if null a new one will be created.
 * @return a point representation of the coordinates at the given index
 */
  Point2D getPoint(  int i,  Point2D dst);
  /** 
 * Returns the bounds of all the points taken as a whole.
 * @param dst output parameter to store bounds into, if null a new rectangle
 * will be created
 * @return rectangle containing the bounds
 */
  Rectangle2D getBounds(  Rectangle2D dst);
}
