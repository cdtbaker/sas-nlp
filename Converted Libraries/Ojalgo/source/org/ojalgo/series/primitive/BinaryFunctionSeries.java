package org.ojalgo.series.primitive;
import org.ojalgo.function.BinaryFunction;
public final class BinaryFunctionSeries extends PrimitiveSeries {
  private final BinaryFunction<Double> myFunction;
  private final PrimitiveSeries myLeftSeries;
  private final PrimitiveSeries myRightSeries;
  public BinaryFunctionSeries(  final PrimitiveSeries aLeftSeries,  final BinaryFunction<Double> aFunction,  final PrimitiveSeries aRightSeries){
    super();
    myLeftSeries=aLeftSeries;
    myRightSeries=aRightSeries;
    myFunction=aFunction;
  }
  public final int size(){
    return Math.min(myLeftSeries.size(),myRightSeries.size());
  }
  @Override public final double value(  final int index){
    return myFunction.invoke(myLeftSeries.value(index),myRightSeries.value(index));
  }
}
