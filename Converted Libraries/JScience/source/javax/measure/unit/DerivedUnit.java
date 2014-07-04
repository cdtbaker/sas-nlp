package javax.measure.unit;
import javax.measure.quantity.Quantity;
/** 
 * <p> This class identifies the units created by combining or transforming
 * other units.</p>
 * @author  <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 3.1, April 22, 2006
 */
public abstract class DerivedUnit<Q extends Quantity> extends Unit<Q> {
  /** 
 * Default constructor.
 */
  protected DerivedUnit(){
  }
}
