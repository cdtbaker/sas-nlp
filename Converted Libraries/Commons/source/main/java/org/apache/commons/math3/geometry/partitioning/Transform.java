package org.apache.commons.math3.geometry.partitioning;
import org.apache.commons.math3.geometry.Vector;
import org.apache.commons.math3.geometry.Space;
/** 
 * This interface represents an inversible affine transform in a space.
 * <p>Inversible affine transform include for example scalings,
 * translations, rotations.</p>
 * <p>Transforms are dimension-specific. The consistency rules between
 * the three {@code apply} methods are the following ones for a
 * transformed defined for dimension D:</p>
 * <ul>
 * <li>
 * the transform can be applied to a point in the
 * D-dimension space using its {@link #apply(Vector)}method
 * </li>
 * <li>
 * the transform can be applied to a (D-1)-dimension
 * hyperplane in the D-dimension space using its{@link #apply(Hyperplane)} method
 * </li>
 * <li>
 * the transform can be applied to a (D-2)-dimension
 * sub-hyperplane in a (D-1)-dimension hyperplane using
 * its {@link #apply(SubHyperplane,Hyperplane,Hyperplane)}method
 * </li>
 * </ul>
 * @param<S>
 *  Type of the embedding space.
 * @param<T>
 *  Type of the embedded sub-space.
 * @version $Id: Transform.java 1416643 2012-12-03 19:37:14Z tn $
 * @since 3.0
 */
public interface Transform<S extends Space,T extends Space> {
  /** 
 * Transform a point of a space.
 * @param point point to transform
 * @return a new object representing the transformed point
 */
  Vector<S> apply(  Vector<S> point);
  /** 
 * Transform an hyperplane of a space.
 * @param hyperplane hyperplane to transform
 * @return a new object representing the transformed hyperplane
 */
  Hyperplane<S> apply(  Hyperplane<S> hyperplane);
  /** 
 * Transform a sub-hyperplane embedded in an hyperplane.
 * @param sub sub-hyperplane to transform
 * @param original hyperplane in which the sub-hyperplane is
 * defined (this is the original hyperplane, the transform has
 * <em>not</em> been applied to it)
 * @param transformed hyperplane in which the sub-hyperplane is
 * defined (this is the transformed hyperplane, the transform
 * <em>has</em> been applied to it)
 * @return a new object representing the transformed sub-hyperplane
 */
  SubHyperplane<T> apply(  SubHyperplane<T> sub,  Hyperplane<S> original,  Hyperplane<S> transformed);
}
