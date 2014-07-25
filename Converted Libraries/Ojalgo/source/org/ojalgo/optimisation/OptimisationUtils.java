package org.ojalgo.optimisation;
import static org.ojalgo.constant.PrimitiveMath.*;
import org.ojalgo.function.aggregator.AggregatorFunction;
import org.ojalgo.type.context.NumberContext;
public abstract class OptimisationUtils {
  static final NumberContext DISPLAY=NumberContext.getGeneral(6);
  static int getAdjustmentFactorExponent(  final AggregatorFunction<?> aLargestAggr,  final AggregatorFunction<?> aSmallestAggr){
    final double tmpLargestValue=aLargestAggr.doubleValue();
    final double tmpSmallestValue=aSmallestAggr.doubleValue();
    final double tmpLargestExp=tmpLargestValue >= IS_ZERO ? Math.log10(tmpLargestValue) : ZERO;
    final double tmpSmallestExp=tmpSmallestValue >= IS_ZERO ? Math.log10(tmpSmallestValue) : -TWELFTH;
    return (int)Math.rint(Math.max((tmpLargestExp + tmpSmallestExp) / (-TWO),(-SIX) - tmpSmallestExp));
  }
  private OptimisationUtils(){
    super();
  }
}
