package org.apache.commons.math3.geometry.euclidean.threed;
import org.apache.commons.math3.geometry.euclidean.oned.Vector1D;
import org.apache.commons.math3.geometry.euclidean.twod.Euclidean2D;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.geometry.euclidean.twod.PolygonsSet;
import org.apache.commons.math3.geometry.partitioning.AbstractSubHyperplane;
import org.apache.commons.math3.geometry.partitioning.BSPTree;
import org.apache.commons.math3.geometry.partitioning.Hyperplane;
import org.apache.commons.math3.geometry.partitioning.Region;
import org.apache.commons.math3.geometry.partitioning.Side;
import org.apache.commons.math3.geometry.partitioning.SubHyperplane;
/** 
 * This class represents a sub-hyperplane for {@link Plane}.
 * @version $Id: SubPlane.java 1416643 2012-12-03 19:37:14Z tn $
 * @since 3.0
 */
public class SubPlane extends AbstractSubHyperplane<Euclidean3D,Euclidean2D> {
  /** 
 * Simple constructor.
 * @param hyperplane underlying hyperplane
 * @param remainingRegion remaining region of the hyperplane
 */
  public SubPlane(  final Hyperplane<Euclidean3D> hyperplane,  final Region<Euclidean2D> remainingRegion){
    super(hyperplane,remainingRegion);
  }
  /** 
 * {@inheritDoc} 
 */
  @Override protected AbstractSubHyperplane<Euclidean3D,Euclidean2D> buildNew(  final Hyperplane<Euclidean3D> hyperplane,  final Region<Euclidean2D> remainingRegion){
    return new SubPlane(hyperplane,remainingRegion);
  }
  /** 
 * {@inheritDoc} 
 */
  @Override public Side side(  Hyperplane<Euclidean3D> hyperplane){
    final Plane otherPlane=(Plane)hyperplane;
    final Plane thisPlane=(Plane)getHyperplane();
    final Line inter=otherPlane.intersection(thisPlane);
    if (inter == null) {
      final double global=otherPlane.getOffset(thisPlane);
      return (global < -1.0e-10) ? Side.MINUS : ((global > 1.0e-10) ? Side.PLUS : Side.HYPER);
    }
    Vector2D p=thisPlane.toSubSpace(inter.toSpace(Vector1D.ZERO));
    Vector2D q=thisPlane.toSubSpace(inter.toSpace(Vector1D.ONE));
    Vector3D crossP=Vector3D.crossProduct(inter.getDirection(),thisPlane.getNormal());
    if (crossP.dotProduct(otherPlane.getNormal()) < 0) {
      final Vector2D tmp=p;
      p=q;
      q=tmp;
    }
    final org.apache.commons.math3.geometry.euclidean.twod.Line line2D=new org.apache.commons.math3.geometry.euclidean.twod.Line(p,q);
    return getRemainingRegion().side(line2D);
  }
  /** 
 * Split the instance in two parts by an hyperplane.
 * @param hyperplane splitting hyperplane
 * @return an object containing both the part of the instance
 * on the plus side of the instance and the part of the
 * instance on the minus side of the instance
 */
  @Override public SplitSubHyperplane<Euclidean3D> split(  Hyperplane<Euclidean3D> hyperplane){
    final Plane otherPlane=(Plane)hyperplane;
    final Plane thisPlane=(Plane)getHyperplane();
    final Line inter=otherPlane.intersection(thisPlane);
    if (inter == null) {
      final double global=otherPlane.getOffset(thisPlane);
      return (global < -1.0e-10) ? new SplitSubHyperplane<Euclidean3D>(null,this) : new SplitSubHyperplane<Euclidean3D>(this,null);
    }
    Vector2D p=thisPlane.toSubSpace(inter.toSpace(Vector1D.ZERO));
    Vector2D q=thisPlane.toSubSpace(inter.toSpace(Vector1D.ONE));
    Vector3D crossP=Vector3D.crossProduct(inter.getDirection(),thisPlane.getNormal());
    if (crossP.dotProduct(otherPlane.getNormal()) < 0) {
      final Vector2D tmp=p;
      p=q;
      q=tmp;
    }
    final SubHyperplane<Euclidean2D> l2DMinus=new org.apache.commons.math3.geometry.euclidean.twod.Line(p,q).wholeHyperplane();
    final SubHyperplane<Euclidean2D> l2DPlus=new org.apache.commons.math3.geometry.euclidean.twod.Line(q,p).wholeHyperplane();
    final BSPTree<Euclidean2D> splitTree=getRemainingRegion().getTree(false).split(l2DMinus);
    final BSPTree<Euclidean2D> plusTree=getRemainingRegion().isEmpty(splitTree.getPlus()) ? new BSPTree<Euclidean2D>(Boolean.FALSE) : new BSPTree<Euclidean2D>(l2DPlus,new BSPTree<Euclidean2D>(Boolean.FALSE),splitTree.getPlus(),null);
    final BSPTree<Euclidean2D> minusTree=getRemainingRegion().isEmpty(splitTree.getMinus()) ? new BSPTree<Euclidean2D>(Boolean.FALSE) : new BSPTree<Euclidean2D>(l2DMinus,new BSPTree<Euclidean2D>(Boolean.FALSE),splitTree.getMinus(),null);
    return new SplitSubHyperplane<Euclidean3D>(new SubPlane(thisPlane.copySelf(),new PolygonsSet(plusTree)),new SubPlane(thisPlane.copySelf(),new PolygonsSet(minusTree)));
  }
}
