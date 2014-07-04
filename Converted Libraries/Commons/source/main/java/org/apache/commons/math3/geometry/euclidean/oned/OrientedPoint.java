package org.apache.commons.math3.geometry.euclidean.oned;
import org.apache.commons.math3.geometry.Vector;
import org.apache.commons.math3.geometry.partitioning.Hyperplane;
/** 
 * This class represents a 1D oriented hyperplane.
 * <p>An hyperplane in 1D is a simple point, its orientation being a
 * boolean.</p>
 * <p>Instances of this class are guaranteed to be immutable.</p>
 * @version $Id: OrientedPoint.java 1416643 2012-12-03 19:37:14Z tn $
 * @since 3.0
 */
public class OrientedPoint implements Hyperplane<Euclidean1D> {
  /** 
 * Vector location. 
 */
  private Vector1D location;
  /** 
 * Orientation. 
 */
  private boolean direct;
  /** 
 * Simple constructor.
 * @param location location of the hyperplane
 * @param direct if true, the plus side of the hyperplane is towards
 * abscissas greater than {@code location}
 */
  public OrientedPoint(  final Vector1D location,  final boolean direct){
    this.location=location;
    this.direct=direct;
  }
  /** 
 * Copy the instance.
 * <p>Since instances are immutable, this method directly returns
 * the instance.</p>
 * @return the instance itself
 */
  public OrientedPoint copySelf(){
    return this;
  }
  /** 
 * {@inheritDoc} 
 */
  public double getOffset(  final Vector<Euclidean1D> point){
    final double delta=((Vector1D)point).getX() - location.getX();
    return direct ? delta : -delta;
  }
  /** 
 * Build a region covering the whole hyperplane.
 * <p>Since this class represent zero dimension spaces which does
 * not have lower dimension sub-spaces, this method returns a dummy
 * implementation of a {@link org.apache.commons.math3.geometry.partitioning.SubHyperplane SubHyperplane}.
 * This implementation is only used to allow the {@link org.apache.commons.math3.geometry.partitioning.SubHyperplaneSubHyperplane} class implementation to work properly, it should
 * <em>not</em> be used otherwise.</p>
 * @return a dummy sub hyperplane
 */
  public SubOrientedPoint wholeHyperplane(){
    return new SubOrientedPoint(this,null);
  }
  /** 
 * Build a region covering the whole space.
 * @return a region containing the instance (really an {@link IntervalsSet IntervalsSet} instance)
 */
  public IntervalsSet wholeSpace(){
    return new IntervalsSet();
  }
  /** 
 * {@inheritDoc} 
 */
  public boolean sameOrientationAs(  final Hyperplane<Euclidean1D> other){
    return !(direct ^ ((OrientedPoint)other).direct);
  }
  /** 
 * Get the hyperplane location on the real line.
 * @return the hyperplane location
 */
  public Vector1D getLocation(){
    return location;
  }
  /** 
 * Check if the hyperplane orientation is direct.
 * @return true if the plus side of the hyperplane is towards
 * abscissae greater than hyperplane location
 */
  public boolean isDirect(){
    return direct;
  }
  /** 
 * Revert the instance.
 */
  public void revertSelf(){
    direct=!direct;
  }
}
