package javax.measure.quantity;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;
/** 
 * This interface represents the number of times a specified phenomenon occurs
 * within a specified interval. The system unit for this quantity is "Hz"
 * (Hertz).
 * @author  <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 1.0, January 14, 2006
 */
public interface Frequency extends Quantity {
  /** 
 * Holds the SI unit (Système International d'Unités) for this quantity.
 */
  public final static Unit<Frequency> UNIT=SI.HERTZ;
}
