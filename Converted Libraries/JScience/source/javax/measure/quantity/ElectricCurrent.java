package javax.measure.quantity;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;
/** 
 * This interface represents the amount of electric charge flowing past
 * a specified circuit point per unit time. The system unit for
 * this quantity is "A" (Ampere).
 * @author  <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 1.0, January 14, 2006
 */
public interface ElectricCurrent extends Quantity {
  /** 
 * Holds the SI unit (Système International d'Unités) for this quantity.
 */
  public final static Unit<ElectricCurrent> UNIT=SI.AMPERE;
}
