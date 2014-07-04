package org.apache.commons.math3.geometry.euclidean.threed;
import org.apache.commons.math3.exception.MathIllegalStateException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
/** 
 * This class represents exceptions thrown while extractiong Cardan
 * or Euler angles from a rotation.
 * @version $Id: CardanEulerSingularityException.java 1416643 2012-12-03 19:37:14Z tn $
 * @since 1.2
 */
public class CardanEulerSingularityException extends MathIllegalStateException {
  /** 
 * Serializable version identifier 
 */
  private static final long serialVersionUID=-1360952845582206770L;
  /** 
 * Simple constructor.
 * build an exception with a default message.
 * @param isCardan if true, the rotation is related to Cardan angles,
 * if false it is related to EulerAngles
 */
  public CardanEulerSingularityException(  boolean isCardan){
    super(isCardan ? LocalizedFormats.CARDAN_ANGLES_SINGULARITY : LocalizedFormats.EULER_ANGLES_SINGULARITY);
  }
}
