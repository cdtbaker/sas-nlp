package org.ojalgo.series;
import java.awt.Color;
import java.util.Collection;
import java.util.SortedMap;
import org.ojalgo.function.BinaryFunction;
import org.ojalgo.function.ParameterFunction;
import org.ojalgo.function.UnaryFunction;
import org.ojalgo.series.primitive.DataSeries;
import org.ojalgo.type.keyvalue.KeyValue;
/** 
 * A BasicSeries is a {@linkplain SortedMap} with:
 * <ul>
 * <li>Sligthly restricted type parameters</li>
 * <li>The option to set a name and colour</li>
 * <li>A few additional methods to help access and modify series entries</li>
 * </ul>
 * @author apete
 */
public interface BasicSeries<K extends Comparable<K>,V extends Number> extends SortedMap<K,V> {
  BasicSeries<K,V> colour(  Color aPaint);
  V firstValue();
  Color getColour();
  DataSeries getDataSeries();
  String getName();
  double[] getPrimitiveValues();
  V lastValue();
  void modify(  BasicSeries<K,V> aLeftArg,  BinaryFunction<V> aFunc);
  void modify(  BinaryFunction<V> aFunc,  BasicSeries<K,V> aRightArg);
  void modify(  BinaryFunction<V> aFunc,  V aRightArg);
  void modify(  ParameterFunction<V> aFunc,  int aParam);
  void modify(  UnaryFunction<V> aFunc);
  void modify(  V aLeftArg,  BinaryFunction<V> aFunc);
  BasicSeries<K,V> name(  String aName);
  void putAll(  Collection<? extends KeyValue<? extends K,? extends V>> data);
}
