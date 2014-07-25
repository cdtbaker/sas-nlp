package javax.measure.quantity;
import javax.measure.unit.ProductUnit;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;
/** 
 * This interface represents the diffusion of momentum. 
 * The system unit for this quantity is "m²/s".
 * @author  <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 3.0, March 2, 2006
 * @see <a href="http://en.wikipedia.org/wiki/Viscosity">
 *      Wikipedia: Viscosity</a>
 */
public interface KinematicViscosity extends Quantity {
  /** 
 * Holds the SI unit (Système International d'Unités) for this quantity.
 */
  public final static Unit<KinematicViscosity> UNIT=new ProductUnit<KinematicViscosity>(SI.METRE.pow(2).divide(SI.SECOND));
}
