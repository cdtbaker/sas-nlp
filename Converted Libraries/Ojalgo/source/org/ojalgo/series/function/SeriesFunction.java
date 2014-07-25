package org.ojalgo.series.function;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.ojalgo.access.Access1D;
import org.ojalgo.series.BasicSeries;
/** 
 * A function that maps from a (collection of) series and one or more keys to a
 * series of numbers. The interpretation of the input series data and the output
 * series is completely free.
 * @author apete
 * @param<K>
 *  The series key type
 */
public abstract class SeriesFunction<K extends Comparable<K>> {
  private final Map<String,? extends BasicSeries<K,? extends Number>> myData;
  @SuppressWarnings("unused") private SeriesFunction(){
    super();
    myData=null;
  }
  protected SeriesFunction(  final BasicSeries<K,? extends Number> data){
    super();
    myData=Collections.singletonMap(data.getName(),data);
  }
  protected SeriesFunction(  final Map<String,? extends BasicSeries<K,? extends Number>> data){
    super();
    myData=data;
  }
  /** 
 * @param key One or more time series keys
 * @return A map with one entry per series. Each entry/series has the same
 * number of elements as there were input keys.
 */
  public abstract Map<String,Access1D<?>> invoke(  K... key);
  protected List<String> getAllSeriesNames(){
    return new ArrayList<String>(myData.keySet());
  }
  protected BasicSeries<K,? extends Number> getSeries(  final String name){
    return myData.get(name);
  }
}
