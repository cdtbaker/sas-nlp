package javax.measure.quantity;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;
/** 
 * This interface represents the effective (or "equivalent") dose of radiation
 * received by a human or some other living organism. The system unit for
 * this quantity is "Sv" (Sievert).
 * @author  <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 1.0, January 14, 2006
 */
public interface RadiationDoseEffective extends Quantity {
  /** 
 * Holds the SI unit (Système International d'Unités) for this quantity.
 */
  public final static Unit<RadiationDoseEffective> UNIT=SI.SIEVERT;
}
