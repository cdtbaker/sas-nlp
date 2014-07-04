package javax.measure.quantity;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;
/** 
 * This interface represents an electric charge. The system unit for this
 * quantity is "C" (Coulomb).
 * @author  <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 1.0, January 14, 2006
 */
public interface ElectricCharge extends Quantity {
  /** 
 * Holds the SI unit (Système International d'Unités) for this quantity.
 */
  public final static Unit<ElectricCharge> UNIT=SI.COULOMB;
}
