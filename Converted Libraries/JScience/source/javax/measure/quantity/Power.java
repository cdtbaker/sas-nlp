package javax.measure.quantity;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;
/** 
 * This interface represents the rate at which work is done. The system unit
 * for this quantity is "W" (Watt).
 * @author  <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 1.0, January 14, 2006
 */
public interface Power extends Quantity {
  /** 
 * Holds the SI unit (Système International d'Unités) for this quantity.
 */
  public final static Unit<Power> UNIT=SI.WATT;
}
