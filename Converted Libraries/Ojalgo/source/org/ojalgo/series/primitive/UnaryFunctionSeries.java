package org.ojalgo.series.primitive;
import org.ojalgo.function.UnaryFunction;
public final class UnaryFunctionSeries extends PrimitiveSeries {
  private final PrimitiveSeries mySeries;
  private final UnaryFunction<Double> myFunction;
  public UnaryFunctionSeries(  final PrimitiveSeries aSeries,  final UnaryFunction<Double> aFunction){
    super();
    mySeries=aSeries;
    myFunction=aFunction;
  }
  public final int size(){
    return mySeries.size();
  }
  @Override public final double value(  final int index){
    return myFunction.invoke(mySeries.value(index));
  }
}
