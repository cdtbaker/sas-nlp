package javax.measure.quantity;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;
/** 
 * This interface represents the volume of fluid passing a point in a system
 * per unit of time. The system unit for this quantity is "m³/s" 
 * (cubic meter per second).
 * @author  <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 3.0, March 2, 2006
 * @see <a href="http://en.wikipedia.org/wiki/Rate_of_fluid_flow">
 *      Wikipedia: Volumetric Flow Rate</a>
 */
public interface VolumetricFlowRate extends Quantity {
  /** 
 * Holds the SI unit (Système International d'Unités) for this quantity.
 */
  @SuppressWarnings("unchecked") public final static Unit<VolumetricFlowRate> UNIT=(Unit<VolumetricFlowRate>)SI.METRE.pow(3).divide(SI.SECOND);
}
