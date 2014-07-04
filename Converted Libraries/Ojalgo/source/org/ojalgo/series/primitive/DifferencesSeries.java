package org.ojalgo.series.primitive;
final class DifferencesSeries extends PrimitiveSeries {
  private final PrimitiveSeries mySeries;
  private final int myPeriod;
  DifferencesSeries(  final PrimitiveSeries aSeries){
    super();
    mySeries=aSeries;
    myPeriod=1;
  }
  DifferencesSeries(  final PrimitiveSeries aSeries,  final int aPeriod){
    super();
    mySeries=aSeries;
    myPeriod=aPeriod;
  }
  public final int size(){
    return mySeries.size() - myPeriod;
  }
  @Override public final double value(  final int index){
    return mySeries.value(index + myPeriod) - mySeries.value(index);
  }
}
