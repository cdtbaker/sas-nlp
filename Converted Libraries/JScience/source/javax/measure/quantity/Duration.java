package javax.measure.quantity;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;
/** 
 * This interface represents a period of existence or persistence. The system
 * unit for this quantity is "s" (second).
 * @author  <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 1.0, January 14, 2006
 */
public interface Duration extends Quantity {
  /** 
 * Holds the SI unit (Système International d'Unités) for this quantity.
 */
  public final static Unit<Duration> UNIT=SI.SECOND;
}
