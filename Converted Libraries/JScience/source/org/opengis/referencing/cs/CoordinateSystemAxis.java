package org.opengis.referencing.cs;
import javax.measure.unit.Unit;
import org.opengis.referencing.IdentifiedObject;
/** 
 * Definition of a coordinate system axis.
 * See <A HREF="package-summary.html#AxisNames">axis name constraints</A>.
 * @version <A HREF="http://portal.opengeospatial.org/files/?artifact_id=6716">Abstract specification 2.0</A>
 * @author Martin Desruisseaux (IRD)
 * @since GeoAPI 1.0
 * @see CoordinateSystem
 * @see Unit
 */
public interface CoordinateSystemAxis extends IdentifiedObject {
  /** 
 * The abbreviation used for this coordinate system axes. This abbreviation is also
 * used to identify the ordinates in coordinate tuple. Examples are "<var>X</var>"
 * and "<var>Y</var>".
 * @return The coordinate system axis abbreviation.
 */
  String getAbbreviation();
  /** 
 * Direction of this coordinate system axis. In the case of Cartesian projected
 * coordinates, this is the direction of this coordinate system axis locally.
 * Examples:{@linkplain AxisDirection#NORTH north} or {@linkplain AxisDirection#SOUTH south},{@linkplain AxisDirection#EAST  east}  or {@linkplain AxisDirection#WEST  west},{@linkplain AxisDirection#UP    up}    or {@linkplain AxisDirection#DOWN  down}.
 * Within any set of coordinate system axes, only one of each pair of terms
 * can be used. For earth-fixed coordinate reference systems, this direction is often
 * approximate and intended to provide a human interpretable meaning to the axis. When a
 * geodetic datum is used, the precise directions of the axes may therefore vary slightly
 * from this approximate direction.
 * Note that an {@link org.opengis.referencing.crs.EngineeringCRS} often requires
 * specific descriptions of the directions of its coordinate system axes.
 * @return The coordinate system axis direction.
 */
  AxisDirection getDirection();
  /** 
 * The unit of measure used for this coordinate system axis. The value of this
 * coordinate in a coordinate tuple shall be recorded using this unit of measure,
 * whenever those coordinates use a coordinate reference system that uses a
 * coordinate system that uses this axis.
 * @return  The coordinate system axis unit.
 */
  Unit<?> getUnit();
}
