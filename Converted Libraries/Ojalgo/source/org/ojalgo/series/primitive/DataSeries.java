package org.ojalgo.series.primitive;
import org.ojalgo.access.Access1D;
import org.ojalgo.array.Array1D;
import org.ojalgo.array.SimpleArray;
import org.ojalgo.function.BinaryFunction;
import org.ojalgo.function.ParameterFunction;
import org.ojalgo.function.UnaryFunction;
import org.ojalgo.function.aggregator.AggregatorFunction;
public final class DataSeries extends PrimitiveSeries {
  public static DataSeries copy(  final Access1D<?> aBase){
    return new DataSeries(Array1D.PRIMITIVE.copy(aBase));
  }
  public static DataSeries copy(  final double[] aRaw){
    return new DataSeries(Array1D.PRIMITIVE.copy(aRaw));
  }
  public static DataSeries wrap(  final double[] aRaw){
    return new DataSeries(Array1D.PRIMITIVE.wrap(SimpleArray.wrapPrimitive(aRaw)));
  }
  private final Array1D<Double> myValues;
  private DataSeries(  final Array1D<Double> aValues){
    super();
    myValues=aValues;
  }
  public final void modify(  final BinaryFunction<Double> aFunc,  final Double aNmbr){
    myValues.modifyAll(aFunc,aNmbr);
  }
  public final void modify(  final Double aNmbr,  final BinaryFunction<Double> aFunc){
    myValues.modifyAll(aNmbr,aFunc);
  }
  public final void modify(  final ParameterFunction<Double> aFunc,  final int aParam){
    myValues.modifyAll(aFunc,aParam);
  }
  public final void modify(  final UnaryFunction<Double> aFunc){
    myValues.modifyAll(aFunc);
  }
  public final int size(){
    return myValues.size();
  }
  @Override public final double value(  final int index){
    return myValues.doubleValue(index);
  }
  public final void visit(  final AggregatorFunction<Double> aVisitor){
    myValues.visitAll(aVisitor);
  }
}
