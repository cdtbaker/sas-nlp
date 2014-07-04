package org.ojalgo.series.primitive;
final class PrunedSeries extends PrimitiveSeries {
  private final PrimitiveSeries mySeries;
  private final int myShift;
  PrunedSeries(  final PrimitiveSeries aSeries){
    super();
    mySeries=aSeries;
    myShift=0;
  }
  PrunedSeries(  final PrimitiveSeries aSeries,  final int aShift){
    super();
    mySeries=aSeries;
    myShift=aShift;
  }
  public final int size(){
    return mySeries.size() - Math.abs(myShift);
  }
  @Override public final double value(  final int index){
    return mySeries.value(index - Math.min(myShift,0));
  }
}
