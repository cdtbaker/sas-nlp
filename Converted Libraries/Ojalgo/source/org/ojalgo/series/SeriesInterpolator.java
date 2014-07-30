package org.ojalgo.series;
import java.math.BigDecimal;
import java.util.TreeMap;
import org.ojalgo.constant.PrimitiveMath;
import org.ojalgo.type.CalendarDate;
import org.ojalgo.type.TypeUtils;
import org.ojalgo.type.context.NumberContext;
public class SeriesInterpolator {
  private final NumberContext myContext;
  private CoordinationSet<Double> myCoordinatedSet=null;
  private final TreeMap<BigDecimal,String> myKeys=new TreeMap<BigDecimal,String>();
  private final CoordinationSet<Double> myOriginalSet=new CoordinationSet<Double>();
  public SeriesInterpolator(){
    this(NumberContext.getGeneral(15));
  }
  public SeriesInterpolator(  final NumberContext context){
    super();
    myContext=context;
  }
  public void addSeries(  final Number key,  final CalendarDateSeries<Double> series){
    final BigDecimal tmpKey=TypeUtils.toBigDecimal(key,myContext);
    myKeys.put(tmpKey,series.getName());
    myOriginalSet.put(series);
    myCoordinatedSet=null;
  }
  public CalendarDateSeries<Double> getCombination(  final Number inputKey){
    final BigDecimal tmpInputKey=TypeUtils.toBigDecimal(inputKey,myContext);
    if (myCoordinatedSet == null) {
      myCoordinatedSet=myOriginalSet.prune();
      myCoordinatedSet.complete();
    }
    final CalendarDateSeries<Double> retVal=new CalendarDateSeries<Double>(myCoordinatedSet.getResolution());
    BigDecimal tmpLowerKey=null;
    BigDecimal tmpUpperKey=null;
    for (    final BigDecimal tmpIterKey : myKeys.keySet()) {
      if (tmpIterKey.compareTo(tmpInputKey) != 1) {
        if ((tmpLowerKey == null) || (tmpIterKey.compareTo(tmpLowerKey) == 1)) {
          tmpLowerKey=tmpIterKey;
        }
      }
      if (tmpIterKey.compareTo(tmpInputKey) != -1) {
        if ((tmpUpperKey == null) || (tmpIterKey.compareTo(tmpInputKey) != -1)) {
          tmpUpperKey=tmpIterKey;
        }
      }
    }
    @SuppressWarnings("unchecked") final long[] tmpSeriesKeys=((CalendarDateSeries<Double>)myCoordinatedSet.values().toArray()[0]).getPrimitiveKeys();
    double tmpFactor;
    double[] tmpSeriesValues;
    if ((tmpLowerKey == null) && (tmpUpperKey != null)) {
      tmpFactor=tmpInputKey.doubleValue() / tmpUpperKey.doubleValue();
      tmpSeriesValues=myCoordinatedSet.get(myKeys.get(tmpUpperKey)).getPrimitiveValues();
      for (int i=0; i < tmpSeriesValues.length; i++) {
        tmpSeriesValues[i]*=tmpFactor;
      }
    }
 else     if ((tmpLowerKey != null) && (tmpUpperKey == null)) {
      tmpFactor=tmpInputKey.doubleValue() / tmpLowerKey.doubleValue();
      tmpSeriesValues=myCoordinatedSet.get(myKeys.get(tmpLowerKey)).getPrimitiveValues();
      for (int i=0; i < tmpSeriesValues.length; i++) {
        tmpSeriesValues[i]*=tmpFactor;
      }
    }
 else     if ((tmpLowerKey != null) && (tmpUpperKey != null)) {
      if (tmpLowerKey.equals(tmpUpperKey)) {
        tmpSeriesValues=myCoordinatedSet.get(myKeys.get(tmpLowerKey)).getPrimitiveValues();
      }
 else {
        final double[] tmpLowerValues=myCoordinatedSet.get(myKeys.get(tmpLowerKey)).getPrimitiveValues();
        final double[] tmpUpperValues=myCoordinatedSet.get(myKeys.get(tmpUpperKey)).getPrimitiveValues();
        tmpFactor=(tmpInputKey.doubleValue() - tmpLowerKey.doubleValue()) / (tmpUpperKey.doubleValue() - tmpLowerKey.doubleValue());
        tmpSeriesValues=new double[tmpSeriesKeys.length];
        for (int i=0; i < tmpSeriesValues.length; i++) {
          tmpSeriesValues[i]=(tmpFactor * tmpUpperValues[i]) + ((PrimitiveMath.ONE - tmpFactor) * tmpLowerValues[i]);
        }
      }
    }
 else {
      tmpSeriesValues=new double[tmpSeriesKeys.length];
    }
    for (int i=0; i < tmpSeriesKeys.length; i++) {
      retVal.put(new CalendarDate(tmpSeriesKeys[i]),tmpSeriesValues[i]);
    }
    return retVal;
  }
}