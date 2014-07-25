package javax.measure.quantity;
import javax.measure.unit.ProductUnit;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;
/** 
 * This interface represents the rate of change of angular velocity with respect
 * to time. The system unit for this quantity is "rad/s²" (radian per 
 * square second).
 * @author  <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 1.0, January 14, 2006
 */
public interface AngularAcceleration extends Quantity {
  /** 
 * Holds the SI unit (Système International d'Unités) for this quantity.
 */
  public final static Unit<AngularAcceleration> UNIT=new ProductUnit<AngularAcceleration>(SI.RADIAN.divide(SI.SECOND.pow(2)));
}
