package org.ojalgo.series.function;
import java.util.Map;
import org.ojalgo.access.Access1D;
import org.ojalgo.series.BasicSeries;
import org.ojalgo.series.CalendarDateSeries;
import org.ojalgo.series.CoordinationSet;
import org.ojalgo.type.CalendarDate;
import org.ojalgo.type.CalendarDateDuration;
import org.ojalgo.type.CalendarDateUnit;
/** 
 * A forecaster is restricted to {@linkplain CalendarDate} keys and is intended
 * to predict something related to future keys/dates.
 * @author apete
 */
public abstract class SeriesForecaster extends SeriesFunction<CalendarDate> {
  private final CalendarDate myLastKey;
  private final CalendarDateUnit myResolution;
  private SeriesForecaster(  final BasicSeries<CalendarDate,? extends Number> data){
    super(data);
    myLastKey=null;
    myResolution=null;
  }
  private SeriesForecaster(  final Map<String,? extends BasicSeries<CalendarDate,? extends Number>> data){
    super(data);
    myLastKey=null;
    myResolution=null;
  }
  protected SeriesForecaster(  final CalendarDateSeries<? extends Number> data){
    super(data);
    myLastKey=data.lastKey();
    myResolution=data.getResolution();
  }
  protected SeriesForecaster(  final CoordinationSet<? extends Number> coordinatedHistoricalData){
    super(coordinatedHistoricalData);
    myLastKey=coordinatedHistoricalData.getEarliestLastKey();
    myResolution=coordinatedHistoricalData.getResolution();
  }
  @Override public Map<String,Access1D<?>> invoke(  final CalendarDate... key){
    final CalendarDate tmpLastKey=this.getLastKey();
    final CalendarDateUnit tmpResolution=this.getResolution();
    final CalendarDateDuration[] tmpHorizon=new CalendarDateDuration[key.length];
    for (int h=0; h < tmpHorizon.length; h++) {
      final double tmpMeassure=tmpResolution.count(tmpLastKey.millis,key[h].millis);
      tmpHorizon[h]=new CalendarDateDuration(tmpMeassure,tmpResolution);
    }
    return this.invoke(tmpHorizon);
  }
  public abstract Map<String,Access1D<?>> invoke(  CalendarDateDuration... horizon);
  protected final CalendarDate getLastKey(){
    return myLastKey;
  }
  protected final CalendarDateUnit getResolution(){
    return myResolution;
  }
}
