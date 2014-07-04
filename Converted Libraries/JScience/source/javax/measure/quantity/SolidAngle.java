package javax.measure.quantity;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;
/** 
 * This interface represents the angle formed by three or more planes intersecting
 * at a common point. The system unit for this quantity is "sr" (steradian).
 * This quantity is dimensionless.
 * @author  <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 1.0, January 14, 2006
 */
public interface SolidAngle extends Dimensionless {
  /** 
 * Holds the SI unit (Système International d'Unités) for this quantity.
 */
  public final static Unit<SolidAngle> UNIT=SI.STERADIAN;
}
