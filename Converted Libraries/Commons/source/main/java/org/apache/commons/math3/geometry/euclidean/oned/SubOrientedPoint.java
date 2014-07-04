package org.apache.commons.math3.geometry.euclidean.oned;
import org.apache.commons.math3.geometry.partitioning.AbstractSubHyperplane;
import org.apache.commons.math3.geometry.partitioning.Hyperplane;
import org.apache.commons.math3.geometry.partitioning.Region;
import org.apache.commons.math3.geometry.partitioning.Side;
/** 
 * This class represents sub-hyperplane for {@link OrientedPoint}.
 * <p>An hyperplane in 1D is a simple point, its orientation being a
 * boolean.</p>
 * <p>Instances of this class are guaranteed to be immutable.</p>
 * @version $Id: SubOrientedPoint.java 1416643 2012-12-03 19:37:14Z tn $
 * @since 3.0
 */
public class SubOrientedPoint extends AbstractSubHyperplane<Euclidean1D,Euclidean1D> {
  /** 
 * Simple constructor.
 * @param hyperplane underlying hyperplane
 * @param remainingRegion remaining region of the hyperplane
 */
  public SubOrientedPoint(  final Hyperplane<Euclidean1D> hyperplane,  final Region<Euclidean1D> remainingRegion){
    super(hyperplane,remainingRegion);
  }
  /** 
 * {@inheritDoc} 
 */
  @Override public double getSize(){
    return 0;
  }
  /** 
 * {@inheritDoc} 
 */
  @Override protected AbstractSubHyperplane<Euclidean1D,Euclidean1D> buildNew(  final Hyperplane<Euclidean1D> hyperplane,  final Region<Euclidean1D> remainingRegion){
    return new SubOrientedPoint(hyperplane,remainingRegion);
  }
  /** 
 * {@inheritDoc} 
 */
  @Override public Side side(  final Hyperplane<Euclidean1D> hyperplane){
    final double global=hyperplane.getOffset(((OrientedPoint)getHyperplane()).getLocation());
    return (global < -1.0e-10) ? Side.MINUS : ((global > 1.0e-10) ? Side.PLUS : Side.HYPER);
  }
  /** 
 * {@inheritDoc} 
 */
  @Override public SplitSubHyperplane<Euclidean1D> split(  final Hyperplane<Euclidean1D> hyperplane){
    final double global=hyperplane.getOffset(((OrientedPoint)getHyperplane()).getLocation());
    return (global < -1.0e-10) ? new SplitSubHyperplane<Euclidean1D>(null,this) : new SplitSubHyperplane<Euclidean1D>(this,null);
  }
}
