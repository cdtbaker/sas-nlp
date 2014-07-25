package org.ojalgo.series.primitive;
import static org.ojalgo.function.PrimitiveFunction.*;
import java.util.Arrays;
import java.util.Iterator;
import org.ojalgo.access.Access1D;
import org.ojalgo.access.Iterator1D;
import org.ojalgo.array.Array1D;
import org.ojalgo.array.SimpleArray;
public abstract class PrimitiveSeries implements Access1D<Double> {
  public static PrimitiveSeries copy(  final Access1D<?> aBase){
    return new AccessSeries(Array1D.PRIMITIVE.copy(aBase));
  }
  public static PrimitiveSeries wrap(  final Access1D<?> aBase){
    return new AccessSeries(aBase);
  }
  protected PrimitiveSeries(){
    super();
  }
  public PrimitiveSeries add(  final double aValue){
    return new UnaryFunctionSeries(this,ADD.second(aValue));
  }
  public PrimitiveSeries add(  final PrimitiveSeries aSeries){
    return new BinaryFunctionSeries(this,ADD,aSeries);
  }
  public PrimitiveSeries copy(){
    return this.toDataSeries();
  }
  public long count(){
    return this.size();
  }
  /** 
 * period == 1
 */
  public PrimitiveSeries differences(){
    return new DifferencesSeries(this,1);
  }
  public PrimitiveSeries differences(  final int aPeriod){
    return new DifferencesSeries(this,aPeriod);
  }
  public PrimitiveSeries divide(  final double aValue){
    return new UnaryFunctionSeries(this,DIVIDE.second(aValue));
  }
  public PrimitiveSeries divide(  final PrimitiveSeries aSeries){
    return new BinaryFunctionSeries(this,DIVIDE,aSeries);
  }
  public final double doubleValue(  final long anInd){
    return this.value((int)anInd);
  }
  public PrimitiveSeries exp(){
    return new UnaryFunctionSeries(this,EXP);
  }
  public final Double get(  final int anInd){
    return this.value(anInd);
  }
  public final Double get(  final long index){
    return this.value((int)index);
  }
  public final Iterator<Double> iterator(){
    return new Iterator1D<Double>(this);
  }
  public PrimitiveSeries log(){
    return new UnaryFunctionSeries(this,LOG);
  }
  public PrimitiveSeries multiply(  final double aValue){
    return new UnaryFunctionSeries(this,MULTIPLY.second(aValue));
  }
  public PrimitiveSeries multiply(  final PrimitiveSeries aSeries){
    return new BinaryFunctionSeries(this,MULTIPLY,aSeries);
  }
  /** 
 * A positive valued shift will prune that many elements off the head of the series. A negative valued shift will
 * prune that many elements off the tail of the series.
 * @param aShift
 * @return
 */
  public PrimitiveSeries prune(  final int aShift){
    return new PrunedSeries(this,aShift);
  }
  /** 
 * period == 1
 */
  public PrimitiveSeries quotients(){
    return new QuotientsSeries(this,1);
  }
  public PrimitiveSeries quotients(  final int aPeriod){
    return new QuotientsSeries(this,aPeriod);
  }
  public PrimitiveSeries runningProduct(  final double initialValue){
    final int tmpNewSize=this.size() + 1;
    final double[] tmpValues=new double[tmpNewSize];
    double tmpAggrVal=tmpValues[0]=initialValue;
    for (int i=1; i < tmpNewSize; i++) {
      tmpValues[i]=tmpAggrVal*=this.value(i - 1);
    }
    return DataSeries.wrap(tmpValues);
  }
  public PrimitiveSeries runningSum(  final double initialValue){
    final int tmpNewSize=this.size() + 1;
    final double[] tmpValues=new double[tmpNewSize];
    double tmpAggrVal=tmpValues[0]=initialValue;
    for (int i=1; i < tmpNewSize; i++) {
      tmpValues[i]=tmpAggrVal+=this.value(i - 1);
    }
    return DataSeries.wrap(tmpValues);
  }
  public PrimitiveSeries sample(  final int interval){
    if (interval <= 1) {
      throw new IllegalArgumentException();
    }
 else {
      final int tmpSampleSize=this.size() / interval;
      final int tmpLastIndex=this.size() - 1;
      final SimpleArray.Primitive tmpValues=SimpleArray.makePrimitive(tmpSampleSize);
      for (int i=0; i < tmpSampleSize; i++) {
        tmpValues.set(i,tmpLastIndex - (i * interval));
      }
      return PrimitiveSeries.wrap(this);
    }
  }
  public PrimitiveSeries subtract(  final double aValue){
    return new UnaryFunctionSeries(this,SUBTRACT.second(aValue));
  }
  public PrimitiveSeries subtract(  final PrimitiveSeries aSeries){
    return new BinaryFunctionSeries(this,SUBTRACT,aSeries);
  }
  public final DataSeries toDataSeries(){
    return DataSeries.wrap(this.values());
  }
  @Override public String toString(){
    return "PrimitiveSeries [values()=" + Arrays.toString(this.values()) + "]";
  }
  public abstract double value(  final int index);
  public final double[] values(){
    final int tmpSize=this.size();
    final double[] retVal=new double[tmpSize];
    for (int i=0; i < tmpSize; i++) {
      retVal[i]=this.value(i);
    }
    return retVal;
  }
}
