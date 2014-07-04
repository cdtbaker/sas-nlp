package org.apache.commons.math3.geometry.euclidean.twod;
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.geometry.euclidean.oned.IntervalsSet;
import org.apache.commons.math3.geometry.partitioning.Region;
import org.apache.commons.math3.geometry.partitioning.RegionFactory;
import org.apache.commons.math3.geometry.partitioning.SubHyperplane;
/** 
 * This class represent a tree of nested 2D boundary loops.
 * <p>This class is used for piecewise polygons construction.
 * Polygons are built using the outline edges as
 * representative of boundaries, the orientation of these lines are
 * meaningful. However, we want to allow the user to specify its
 * outline loops without having to take care of this orientation. This
 * class is devoted to correct mis-oriented loops.<p>
 * <p>Orientation is computed assuming the piecewise polygon is finite,
 * i.e. the outermost loops have their exterior side facing points at
 * infinity, and hence are oriented counter-clockwise. The orientation of
 * internal loops is computed as the reverse of the orientation of
 * their immediate surrounding loop.</p>
 * @version $Id: NestedLoops.java 1416643 2012-12-03 19:37:14Z tn $
 * @since 3.0
 */
class NestedLoops {
  /** 
 * Boundary loop. 
 */
  private Vector2D[] loop;
  /** 
 * Surrounded loops. 
 */
  private ArrayList<NestedLoops> surrounded;
  /** 
 * Polygon enclosing a finite region. 
 */
  private Region<Euclidean2D> polygon;
  /** 
 * Indicator for original loop orientation. 
 */
  private boolean originalIsClockwise;
  /** 
 * Simple Constructor.
 * <p>Build an empty tree of nested loops. This instance will become
 * the root node of a complete tree, it is not associated with any
 * loop by itself, the outermost loops are in the root tree child
 * nodes.</p>
 */
  public NestedLoops(){
    surrounded=new ArrayList<NestedLoops>();
  }
  /** 
 * Constructor.
 * <p>Build a tree node with neither parent nor children</p>
 * @param loop boundary loop (will be reversed in place if needed)
 * @exception MathIllegalArgumentException if an outline has an open boundary loop
 */
  private NestedLoops(  final Vector2D[] loop) throws MathIllegalArgumentException {
    if (loop[0] == null) {
      throw new MathIllegalArgumentException(LocalizedFormats.OUTLINE_BOUNDARY_LOOP_OPEN);
    }
    this.loop=loop;
    surrounded=new ArrayList<NestedLoops>();
    final ArrayList<SubHyperplane<Euclidean2D>> edges=new ArrayList<SubHyperplane<Euclidean2D>>();
    Vector2D current=loop[loop.length - 1];
    for (int i=0; i < loop.length; ++i) {
      final Vector2D previous=current;
      current=loop[i];
      final Line line=new Line(previous,current);
      final IntervalsSet region=new IntervalsSet(line.toSubSpace(previous).getX(),line.toSubSpace(current).getX());
      edges.add(new SubLine(line,region));
    }
    polygon=new PolygonsSet(edges);
    if (Double.isInfinite(polygon.getSize())) {
      polygon=new RegionFactory<Euclidean2D>().getComplement(polygon);
      originalIsClockwise=false;
    }
 else {
      originalIsClockwise=true;
    }
  }
  /** 
 * Add a loop in a tree.
 * @param bLoop boundary loop (will be reversed in place if needed)
 * @exception MathIllegalArgumentException if an outline has crossing
 * boundary loops or open boundary loops
 */
  public void add(  final Vector2D[] bLoop) throws MathIllegalArgumentException {
    add(new NestedLoops(bLoop));
  }
  /** 
 * Add a loop in a tree.
 * @param node boundary loop (will be reversed in place if needed)
 * @exception MathIllegalArgumentException if an outline has boundary
 * loops that cross each other
 */
  private void add(  final NestedLoops node) throws MathIllegalArgumentException {
    for (    final NestedLoops child : surrounded) {
      if (child.polygon.contains(node.polygon)) {
        child.add(node);
        return;
      }
    }
    for (final Iterator<NestedLoops> iterator=surrounded.iterator(); iterator.hasNext(); ) {
      final NestedLoops child=iterator.next();
      if (node.polygon.contains(child.polygon)) {
        node.surrounded.add(child);
        iterator.remove();
      }
    }
    RegionFactory<Euclidean2D> factory=new RegionFactory<Euclidean2D>();
    for (    final NestedLoops child : surrounded) {
      if (!factory.intersection(node.polygon,child.polygon).isEmpty()) {
        throw new MathIllegalArgumentException(LocalizedFormats.CROSSING_BOUNDARY_LOOPS);
      }
    }
    surrounded.add(node);
  }
  /** 
 * Correct the orientation of the loops contained in the tree.
 * <p>This is this method that really inverts the loops that where
 * provided through the {@link #add(Vector2D[]) add} method if
 * they are mis-oriented</p>
 */
  public void correctOrientation(){
    for (    NestedLoops child : surrounded) {
      child.setClockWise(true);
    }
  }
  /** 
 * Set the loop orientation.
 * @param clockwise if true, the loop should be set to clockwise
 * orientation
 */
  private void setClockWise(  final boolean clockwise){
    if (originalIsClockwise ^ clockwise) {
      int min=-1;
      int max=loop.length;
      while (++min < --max) {
        final Vector2D tmp=loop[min];
        loop[min]=loop[max];
        loop[max]=tmp;
      }
    }
    for (    final NestedLoops child : surrounded) {
      child.setClockWise(!clockwise);
    }
  }
}
