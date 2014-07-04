package org.ojalgo.series.primitive;
import org.ojalgo.type.CalendarDate;
public abstract class PrimitiveTimeSeries extends PrimitiveSeries {
  private final PrimitiveSeries myValueSeries;
  protected PrimitiveTimeSeries(  final PrimitiveSeries aValueSeries){
    super();
    myValueSeries=aValueSeries;
  }
  public abstract CalendarDate first();
  public abstract long getAverageStepSize();
  public final PrimitiveSeries getValueSeries(){
    return myValueSeries;
  }
  public abstract long[] keys();
  public abstract CalendarDate last();
  public final int size(){
    return myValueSeries.size();
  }
  @Override public final double value(  final int index){
    return myValueSeries.value(index);
  }
}
