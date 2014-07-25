package org.ojalgo.series;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.SortedMap;
import org.ojalgo.ProgrammingError;
import org.ojalgo.access.Access1D;
import org.ojalgo.array.ArrayUtils;
import org.ojalgo.constant.PrimitiveMath;
import org.ojalgo.function.UnaryFunction;
public class NumberSeries<N extends Number & Comparable<N>> extends AbstractSeries<N,N,NumberSeries<N>> implements UnaryFunction<N> {
  public NumberSeries(){
    super();
  }
  public NumberSeries(  final Comparator<? super N> someC){
    super(someC);
  }
  public NumberSeries(  final Map<? extends N,? extends N> someM){
    super(someM);
  }
  public NumberSeries(  final SortedMap<N,? extends N> someM){
    super(someM);
  }
  public Access1D<N> accessKeys(){
    return ArrayUtils.wrapAccess1D(new ArrayList<N>(this.keySet()));
  }
  public Access1D<N> accessValues(){
    return ArrayUtils.wrapAccess1D(new ArrayList<N>(this.values()));
  }
  public double invoke(  final double arg){
    ProgrammingError.throwForIllegalInvocation();
    return PrimitiveMath.NaN;
  }
  public N invoke(  final N arg){
    return this.get(arg);
  }
}
