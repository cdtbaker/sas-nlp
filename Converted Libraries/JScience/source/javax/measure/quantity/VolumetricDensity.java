package javax.measure.quantity;
import javax.measure.unit.ProductUnit;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;
/** 
 * This interface represents a mass per unit volume of a substance under
 * specified conditions of pressure and temperature. The system unit for
 * this quantity is "kg/m³" (kilogram per cubic meter).
 * @author  <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 1.0, January 14, 2006
 */
public interface VolumetricDensity extends Quantity {
  /** 
 * Holds the SI unit (Système International d'Unités) for this quantity.
 */
  public final static Unit<VolumetricDensity> UNIT=new ProductUnit<VolumetricDensity>(SI.KILOGRAM.divide(SI.METRE.pow(3)));
}
