package org.apache.commons.math3.geometry;
import java.io.Serializable;
import org.apache.commons.math3.exception.MathUnsupportedOperationException;
/** 
 * This interface represents a generic space, with affine and vectorial counterparts.
 * @version $Id: Space.java 1416643 2012-12-03 19:37:14Z tn $
 * @see Vector
 * @since 3.0
 */
public interface Space extends Serializable {
  /** 
 * Get the dimension of the space.
 * @return dimension of the space
 */
  int getDimension();
  /** 
 * Get the n-1 dimension subspace of this space.
 * @return n-1 dimension sub-space of this space
 * @see #getDimension()
 * @exception MathUnsupportedOperationException for dimension-1 spaces
 * which do not have sub-spaces
 */
  Space getSubSpace() throws MathUnsupportedOperationException ;
}
