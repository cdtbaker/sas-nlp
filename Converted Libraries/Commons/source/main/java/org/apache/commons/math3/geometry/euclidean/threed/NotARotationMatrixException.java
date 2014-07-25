package org.apache.commons.math3.geometry.euclidean.threed;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.util.Localizable;
/** 
 * This class represents exceptions thrown while building rotations
 * from matrices.
 * @version $Id: NotARotationMatrixException.java 1416643 2012-12-03 19:37:14Z tn $
 * @since 1.2
 */
public class NotARotationMatrixException extends MathIllegalArgumentException {
  /** 
 * Serializable version identifier 
 */
  private static final long serialVersionUID=5647178478658937642L;
  /** 
 * Simple constructor.
 * Build an exception by translating and formating a message
 * @param specifier format specifier (to be translated)
 * @param parts to insert in the format (no translation)
 * @since 2.2
 */
  public NotARotationMatrixException(  Localizable specifier,  Object... parts){
    super(specifier,parts);
  }
}
