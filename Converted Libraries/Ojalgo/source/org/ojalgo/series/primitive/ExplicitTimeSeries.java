package org.ojalgo.series.primitive;
import org.ojalgo.type.CalendarDate;
public final class ExplicitTimeSeries extends PrimitiveTimeSeries {
  private final long[] myTimes;
  public ExplicitTimeSeries(  final long[] someTimes,  final PrimitiveSeries aValueSeries){
    super(aValueSeries);
    myTimes=someTimes;
  }
  @Override public ExplicitTimeSeries add(  final double aValue){
    return new ExplicitTimeSeries(this.keys(),super.add(aValue));
  }
  @Override public final ExplicitTimeSeries add(  final PrimitiveSeries aSeries){
    return new ExplicitTimeSeries(this.keys(),super.add(aSeries));
  }
  @Override public ExplicitTimeSeries copy(){
    return new ExplicitTimeSeries(this.keys(),super.copy());
  }
  @Override public final ExplicitTimeSeries differences(){
    return new ExplicitTimeSeries(this.keys(),super.differences());
  }
  @Override public final ExplicitTimeSeries differences(  final int aPeriod){
    return new ExplicitTimeSeries(this.keys(),super.differences(aPeriod));
  }
  @Override public ExplicitTimeSeries divide(  final double aValue){
    return new ExplicitTimeSeries(this.keys(),super.divide(aValue));
  }
  @Override public final ExplicitTimeSeries divide(  final PrimitiveSeries aSeries){
    return new ExplicitTimeSeries(this.keys(),super.divide(aSeries));
  }
  @Override public PrimitiveSeries exp(){
    return new ExplicitTimeSeries(this.keys(),super.exp());
  }
  @Override public final CalendarDate first(){
    return new CalendarDate(myTimes[0]);
  }
  @Override public long getAverageStepSize(){
    final int tmpIndexOfLast=myTimes.length - 1;
    return (myTimes[tmpIndexOfLast] - myTimes[0]) / tmpIndexOfLast;
  }
  public final long key(  final int index){
    return myTimes[index];
  }
  @Override public final long[] keys(){
    return myTimes;
  }
  @Override public final CalendarDate last(){
    return new CalendarDate(myTimes[myTimes.length - 1]);
  }
  @Override public PrimitiveSeries log(){
    return new ExplicitTimeSeries(this.keys(),super.log());
  }
  @Override public final ExplicitTimeSeries multiply(  final double aFactor){
    return new ExplicitTimeSeries(this.keys(),super.multiply(aFactor));
  }
  @Override public ExplicitTimeSeries multiply(  final PrimitiveSeries aSeries){
    return new ExplicitTimeSeries(this.keys(),super.multiply(aSeries));
  }
  @Override public final ExplicitTimeSeries quotients(){
    return new ExplicitTimeSeries(this.keys(),super.quotients());
  }
  @Override public final ExplicitTimeSeries quotients(  final int aPeriod){
    return new ExplicitTimeSeries(this.keys(),super.quotients(aPeriod));
  }
  @Override public final ExplicitTimeSeries runningProduct(  final double initialValue){
    return new ExplicitTimeSeries(this.keys(),super.runningProduct(initialValue));
  }
  @Override public final ExplicitTimeSeries runningSum(  final double initialValue){
    return new ExplicitTimeSeries(this.keys(),super.runningSum(initialValue));
  }
  @Override public ExplicitTimeSeries subtract(  final double aValue){
    return new ExplicitTimeSeries(this.keys(),super.subtract(aValue));
  }
  @Override public ExplicitTimeSeries subtract(  final PrimitiveSeries aSeries){
    return new ExplicitTimeSeries(this.keys(),super.subtract(aSeries));
  }
}
