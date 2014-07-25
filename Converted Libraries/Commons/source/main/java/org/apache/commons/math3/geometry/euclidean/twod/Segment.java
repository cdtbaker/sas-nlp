package org.apache.commons.math3.geometry.euclidean.twod;
import org.apache.commons.math3.util.FastMath;
/** 
 * Simple container for a two-points segment.
 * @version $Id: Segment.java 1422195 2012-12-15 06:45:18Z psteitz $
 * @since 3.0
 */
public class Segment {
  /** 
 * Start point of the segment. 
 */
  private final Vector2D start;
  /** 
 * End point of the segments. 
 */
  private final Vector2D end;
  /** 
 * Line containing the segment. 
 */
  private final Line line;
  /** 
 * Build a segment.
 * @param start start point of the segment
 * @param end end point of the segment
 * @param line line containing the segment
 */
  public Segment(  final Vector2D start,  final Vector2D end,  final Line line){
    this.start=start;
    this.end=end;
    this.line=line;
  }
  /** 
 * Get the start point of the segment.
 * @return start point of the segment
 */
  public Vector2D getStart(){
    return start;
  }
  /** 
 * Get the end point of the segment.
 * @return end point of the segment
 */
  public Vector2D getEnd(){
    return end;
  }
  /** 
 * Get the line containing the segment.
 * @return line containing the segment
 */
  public Line getLine(){
    return line;
  }
  /** 
 * Calculates the shortest distance from a point to this line segment.
 * <p>
 * If the perpendicular extension from the point to the line does not
 * cross in the bounds of the line segment, the shortest distance to
 * the two end points will be returned.
 * </p>
 * Algorithm adapted from:
 * <a href="http://www.codeguru.com/forum/printthread.php?s=cc8cf0596231f9a7dba4da6e77c29db3&t=194400&pp=15&page=1">
 * Thread @ Codeguru</a>
 * @param p to check
 * @return distance between the instance and the point
 * @since 3.1
 */
  public double distance(  final Vector2D p){
    final double deltaX=end.getX() - start.getX();
    final double deltaY=end.getY() - start.getY();
    final double r=((p.getX() - start.getX()) * deltaX + (p.getY() - start.getY()) * deltaY) / (deltaX * deltaX + deltaY * deltaY);
    if (r < 0 || r > 1) {
      final double dist1=getStart().distance(p);
      final double dist2=getEnd().distance(p);
      return FastMath.min(dist1,dist2);
    }
 else {
      final double px=start.getX() + r * deltaX;
      final double py=start.getY() + r * deltaY;
      final Vector2D interPt=new Vector2D(px,py);
      return interPt.distance(p);
    }
  }
}
