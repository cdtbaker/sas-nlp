package org.ojalgo.series.primitive;
import org.ojalgo.access.Access1D;
final class AccessSeries extends PrimitiveSeries {
  private final Access1D<?> myValues;
  AccessSeries(  final Access1D<?> aValues){
    super();
    myValues=aValues;
  }
  public final int size(){
    return myValues.size();
  }
  @Override public final double value(  final int index){
    return myValues.doubleValue(index);
  }
}
