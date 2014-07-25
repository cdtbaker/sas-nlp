package org.jscience.physics.model;
import javax.measure.converter.RationalConverter;
import javax.measure.converter.UnitConverter;
import javax.measure.unit.BaseUnit;
import javax.measure.unit.Dimension;
import javax.measure.unit.SI;
/** 
 * This class represents the quantum model.
 * @author  <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 3.0, February 13, 2006
 */
public final class QuantumModel extends PhysicalModel {
  /** 
 * Holds the single instance of this class.
 */
  final static QuantumModel INSTANCE=new QuantumModel();
  /** 
 * Holds the meter to time transform.
 */
  private static RationalConverter METRE_TO_TIME=new RationalConverter(1,299792458);
  /** 
 * Selects the relativistic model as the current model.
 */
  public static void select(){
    throw new UnsupportedOperationException("Not implemented");
  }
  public Dimension getDimension(  BaseUnit<?> unit){
    if (unit.equals(SI.METRE))     return Dimension.TIME;
    return Dimension.Model.STANDARD.getDimension(unit);
  }
  public UnitConverter getTransform(  BaseUnit<?> unit){
    if (unit.equals(SI.METRE))     return METRE_TO_TIME;
    return Dimension.Model.STANDARD.getTransform(unit);
  }
}
