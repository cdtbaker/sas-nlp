package org.ojalgo.series.function;
import java.util.Map;
import org.ojalgo.series.BasicSeries;
/** 
 * An extrapolator produces new data points to existing series.
 * @author apete
 * @param<K>
 */
public abstract class SeriesExtrapolator<K extends Comparable<K>> extends SeriesFunction<K> {
  protected SeriesExtrapolator(  final BasicSeries<K,? extends Number> data){
    super(data);
  }
  protected SeriesExtrapolator(  final Map<String,? extends BasicSeries<K,? extends Number>> data){
    super(data);
  }
}
