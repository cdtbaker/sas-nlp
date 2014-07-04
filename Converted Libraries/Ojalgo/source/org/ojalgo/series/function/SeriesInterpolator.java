package org.ojalgo.series.function;
import java.util.Map;
import org.ojalgo.series.BasicSeries;
/** 
 * An interpolator produces new data points to existing series, inbetween
 * existing keys.
 * @author apete
 * @param<K>
 */
public abstract class SeriesInterpolator<K extends Comparable<K>> extends SeriesExtrapolator<K> {
  protected SeriesInterpolator(  final BasicSeries<K,? extends Number> data){
    super(data);
  }
  protected SeriesInterpolator(  final Map<String,? extends BasicSeries<K,? extends Number>> data){
    super(data);
  }
}
