package javax.measure.quantity;
import javax.measure.unit.Unit;
/** 
 * This interface represents a dimensionless quantity.
 * @author  <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 1.0, January 14, 2006
 */
public interface Dimensionless extends Quantity {
  /** 
 * Holds the SI unit (Système International d'Unités) for this quantity.
 */
  public final static Unit<Dimensionless> UNIT=Unit.ONE;
}
