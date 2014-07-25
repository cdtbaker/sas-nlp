package org.ojalgo.series.primitive;
final class QuotientsSeries extends PrimitiveSeries {
  private final PrimitiveSeries mySeries;
  private final int myPeriod;
  QuotientsSeries(  final PrimitiveSeries aSeries){
    super();
    mySeries=aSeries;
    myPeriod=1;
  }
  QuotientsSeries(  final PrimitiveSeries aSeries,  final int aPeriod){
    super();
    mySeries=aSeries;
    myPeriod=aPeriod;
  }
  public final int size(){
    return mySeries.size() - myPeriod;
  }
  @Override public final double value(  final int index){
    return mySeries.value(index + myPeriod) / mySeries.value(index);
  }
}
