package javax.measure.unit;
import javax.measure.converter.UnitConverter;
import javax.measure.quantity.Quantity;
/** 
 * <p> This class represents the units used in expressions to distinguish
 * between quantities of a different nature but of the same dimensions.</p>
 * <p> Instances of this class are created through the {@link Unit#alternate(String)} method.</p>
 * @author  <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 4.2, August 26, 2007
 */
public final class AlternateUnit<Q extends Quantity> extends DerivedUnit<Q> {
  /** 
 * Holds the symbol.
 */
  private final String _symbol;
  /** 
 * Holds the parent unit (a system unit).
 */
  private final Unit<?> _parent;
  /** 
 * Creates an alternate unit for the specified unit identified by the 
 * specified symbol. 
 * @param symbol the symbol for this alternate unit.
 * @param parent the system unit from which this alternate unit is
 * derived.
 * @throws UnsupportedOperationException if the source is not 
 * a standard unit.
 * @throws IllegalArgumentException if the specified symbol is 
 * associated to a different unit.
 */
  AlternateUnit(  String symbol,  Unit<?> parent){
    if (!parent.isStandardUnit())     throw new UnsupportedOperationException(this + " is not a standard unit");
    _symbol=symbol;
    _parent=parent;
synchronized (Unit.SYMBOL_TO_UNIT) {
      Unit<?> unit=Unit.SYMBOL_TO_UNIT.get(symbol);
      if (unit == null) {
        Unit.SYMBOL_TO_UNIT.put(symbol,this);
        return;
      }
      if (unit instanceof AlternateUnit) {
        AlternateUnit<?> existingUnit=(AlternateUnit<?>)unit;
        if (symbol.equals(existingUnit._symbol) && _parent.equals(existingUnit._parent))         return;
      }
      throw new IllegalArgumentException("Symbol " + symbol + " is associated to a different unit");
    }
  }
  /** 
 * Returns the symbol for this alternate unit.
 * @return this alternate unit symbol.
 */
  public final String getSymbol(){
    return _symbol;
  }
  /** 
 * Returns the parent unit from which this alternate unit is derived 
 * (a system unit itself).
 * @return the parent of the alternate unit.
 */
  @SuppressWarnings("unchecked") public final Unit<? super Q> getParent(){
    return (Unit<? super Q>)_parent;
  }
  @Override public final Unit<? super Q> getStandardUnit(){
    return this;
  }
  @Override public final UnitConverter toStandardUnit(){
    return UnitConverter.IDENTITY;
  }
  /** 
 * Indicates if this alternate unit is considered equals to the specified 
 * object (both are alternate units with equal symbol, equal base units
 * and equal converter to base units).
 * @param that the object to compare for equality.
 * @return <code>true</code> if <code>this</code> and <code>that</code>
 * are considered equals; <code>false</code>otherwise. 
 */
  public boolean equals(  Object that){
    if (this == that)     return true;
    if (!(that instanceof AlternateUnit))     return false;
    AlternateUnit<?> thatUnit=(AlternateUnit<?>)that;
    return this._symbol.equals(thatUnit._symbol);
  }
  public int hashCode(){
    return _symbol.hashCode();
  }
  private static final long serialVersionUID=1L;
}
