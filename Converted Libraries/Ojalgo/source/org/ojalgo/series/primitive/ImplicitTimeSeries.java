package org.ojalgo.series.primitive;
import java.util.Calendar;
import java.util.Date;
import org.ojalgo.type.CalendarDate;
import org.ojalgo.type.CalendarDateUnit;
public final class ImplicitTimeSeries extends PrimitiveTimeSeries {
  private final CalendarDateUnit myResolution;
  private final CalendarDate myFirst;
  public ImplicitTimeSeries(  final Calendar aFirst,  final CalendarDateUnit aResolution,  final PrimitiveSeries aValueSeries){
    super(aValueSeries);
    myFirst=CalendarDate.make(aFirst,aResolution);
    myResolution=aResolution;
  }
  public ImplicitTimeSeries(  final CalendarDate aFirst,  final CalendarDateUnit aResolution,  final PrimitiveSeries aValueSeries){
    super(aValueSeries);
    myFirst=aFirst.filter(aResolution);
    myResolution=aResolution;
  }
  public ImplicitTimeSeries(  final Date aFirst,  final CalendarDateUnit aResolution,  final PrimitiveSeries aValueSeries){
    super(aValueSeries);
    myFirst=CalendarDate.make(aFirst,aResolution);
    myResolution=aResolution;
  }
  @Override public ImplicitTimeSeries add(  final double aValue){
    return new ImplicitTimeSeries(this.first(),this.resolution(),super.add(aValue));
  }
  @Override public final ImplicitTimeSeries add(  final PrimitiveSeries aSeries){
    return new ImplicitTimeSeries(this.first(),this.resolution(),super.add(aSeries));
  }
  @Override public ImplicitTimeSeries copy(){
    return new ImplicitTimeSeries(this.first(),this.resolution(),super.copy());
  }
  @Override public final ImplicitTimeSeries differences(){
    return new ImplicitTimeSeries(this.first(),this.resolution(),super.differences());
  }
  @Override public final ImplicitTimeSeries differences(  final int aPeriod){
    return new ImplicitTimeSeries(this.first(),this.resolution(),super.differences(aPeriod));
  }
  @Override public ImplicitTimeSeries divide(  final double aValue){
    return new ImplicitTimeSeries(this.first(),this.resolution(),super.divide(aValue));
  }
  @Override public final ImplicitTimeSeries divide(  final PrimitiveSeries aSeries){
    return new ImplicitTimeSeries(this.first(),this.resolution(),super.divide(aSeries));
  }
  @Override public PrimitiveSeries exp(){
    return new ImplicitTimeSeries(this.first(),this.resolution(),super.exp());
  }
  @Override public final CalendarDate first(){
    return myFirst;
  }
  @Override public long getAverageStepSize(){
    return myResolution.size();
  }
  @Override public final long[] keys(){
    final long[] retVal=new long[this.size()];
    CalendarDate tmpKey=myFirst;
    retVal[0]=tmpKey.millis;
    for (int t=1; t < retVal.length; t++) {
      tmpKey=tmpKey.step(myResolution);
      retVal[t]=tmpKey.millis;
    }
    return retVal;
  }
  @Override public final CalendarDate last(){
    return myFirst.step(this.size() - 1,myResolution);
  }
  @Override public PrimitiveSeries log(){
    return new ImplicitTimeSeries(this.first(),this.resolution(),super.log());
  }
  @Override public final ImplicitTimeSeries multiply(  final double aFactor){
    return new ImplicitTimeSeries(this.first(),this.resolution(),super.multiply(aFactor));
  }
  @Override public ImplicitTimeSeries multiply(  final PrimitiveSeries aSeries){
    return new ImplicitTimeSeries(this.first(),this.resolution(),super.multiply(aSeries));
  }
  @Override public final ImplicitTimeSeries quotients(){
    return new ImplicitTimeSeries(this.first(),this.resolution(),super.quotients());
  }
  @Override public final ImplicitTimeSeries quotients(  final int aPeriod){
    return new ImplicitTimeSeries(this.first(),this.resolution(),super.quotients(aPeriod));
  }
  public final CalendarDateUnit resolution(){
    return myResolution;
  }
  @Override public final ImplicitTimeSeries runningProduct(  final double initialValue){
    return new ImplicitTimeSeries(this.first(),this.resolution(),super.runningProduct(initialValue));
  }
  @Override public final ImplicitTimeSeries runningSum(  final double initialValue){
    return new ImplicitTimeSeries(this.first(),this.resolution(),super.runningSum(initialValue));
  }
  @Override public ImplicitTimeSeries subtract(  final double aValue){
    return new ImplicitTimeSeries(this.first(),this.resolution(),super.subtract(aValue));
  }
  @Override public ImplicitTimeSeries subtract(  final PrimitiveSeries aSeries){
    return new ImplicitTimeSeries(this.first(),this.resolution(),super.subtract(aSeries));
  }
}
