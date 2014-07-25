package org.apache.commons.math3.geometry.partitioning;
import org.apache.commons.math3.geometry.Space;
/** 
 * Class holding boundary attributes.
 * <p>This class is used for the attributes associated with the
 * nodes of region boundary shell trees returned by the {@link Region#getTree Region.getTree}. It contains the
 * parts of the node cut sub-hyperplane that belong to the
 * boundary.</p>
 * <p>This class is a simple placeholder, it does not provide any
 * processing methods.</p>
 * @param<S>
 *  Type of the space.
 * @see Region#getTree
 * @version $Id: BoundaryAttribute.java 1416643 2012-12-03 19:37:14Z tn $
 * @since 3.0
 */
public class BoundaryAttribute<S extends Space> {
  /** 
 * Part of the node cut sub-hyperplane that belongs to the
 * boundary and has the outside of the region on the plus side of
 * its underlying hyperplane (may be null).
 */
  private final SubHyperplane<S> plusOutside;
  /** 
 * Part of the node cut sub-hyperplane that belongs to the
 * boundary and has the inside of the region on the plus side of
 * its underlying hyperplane (may be null).
 */
  private final SubHyperplane<S> plusInside;
  /** 
 * Simple constructor.
 * @param plusOutside part of the node cut sub-hyperplane that
 * belongs to the boundary and has the outside of the region on
 * the plus side of its underlying hyperplane (may be null)
 * @param plusInside part of the node cut sub-hyperplane that
 * belongs to the boundary and has the inside of the region on the
 * plus side of its underlying hyperplane (may be null)
 */
  public BoundaryAttribute(  final SubHyperplane<S> plusOutside,  final SubHyperplane<S> plusInside){
    this.plusOutside=plusOutside;
    this.plusInside=plusInside;
  }
  /** 
 * Get the part of the node cut sub-hyperplane that belongs to the
 * boundary and has the outside of the region on the plus side of
 * its underlying hyperplane.
 * @return part of the node cut sub-hyperplane that belongs to the
 * boundary and has the outside of the region on the plus side of
 * its underlying hyperplane
 */
  public SubHyperplane<S> getPlusOutside(){
    return plusOutside;
  }
  /** 
 * Get the part of the node cut sub-hyperplane that belongs to the
 * boundary and has the inside of the region on the plus side of
 * its underlying hyperplane.
 * @return part of the node cut sub-hyperplane that belongs to the
 * boundary and has the inside of the region on the plus side of
 * its underlying hyperplane
 */
  public SubHyperplane<S> getPlusInside(){
    return plusInside;
  }
}
