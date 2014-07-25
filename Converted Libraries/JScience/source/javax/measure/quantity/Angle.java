package javax.measure.quantity;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;
/** 
 * This interface represents the figure formed by two lines diverging from a 
 * common point. The system unit for this quantity is "rad" (radian).
 * This quantity is dimensionless.
 * @author  <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 1.0, January 14, 2006
 */
public interface Angle extends Dimensionless {
  /** 
 * Holds the SI unit (Système International d'Unités) for this quantity.
 */
  public final static Unit<Angle> UNIT=SI.RADIAN;
}
