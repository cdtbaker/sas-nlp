package org.ojalgo.function.aggregator;
import java.math.BigDecimal;
import org.ojalgo.scalar.ComplexNumber;
import org.ojalgo.scalar.RationalNumber;
public enum Aggregator {CARDINALITY, LARGEST, MAXIMUM, MINIMUM, NORM1, NORM2, PRODUCT, PRODUCT2, SMALLEST, SUM, SUM2; public final AggregatorFunction<BigDecimal> getBigFunction(){
switch (this) {
case CARDINALITY:
    return BigAggregator.CARDINALITY.get().reset();
case LARGEST:
  return BigAggregator.LARGEST.get().reset();
case MAXIMUM:
return BigAggregator.MAX.get().reset();
case MINIMUM:
return BigAggregator.MIN.get().reset();
case NORM1:
return BigAggregator.NORM1.get().reset();
case NORM2:
return BigAggregator.NORM2.get().reset();
case PRODUCT:
return BigAggregator.PRODUCT.get().reset();
case PRODUCT2:
return BigAggregator.PRODUCT2.get().reset();
case SMALLEST:
return BigAggregator.SMALLEST.get().reset();
case SUM:
return BigAggregator.SUM.get().reset();
case SUM2:
return BigAggregator.SUM2.get().reset();
default :
return null;
}
}
public final AggregatorFunction<ComplexNumber> getComplexFunction(){
switch (this) {
case CARDINALITY:
return ComplexAggregator.CARDINALITY.get().reset();
case LARGEST:
return ComplexAggregator.LARGEST.get().reset();
case MAXIMUM:
return ComplexAggregator.MAX.get().reset();
case MINIMUM:
return ComplexAggregator.MIN.get().reset();
case NORM1:
return ComplexAggregator.NORM1.get().reset();
case NORM2:
return ComplexAggregator.NORM2.get().reset();
case PRODUCT:
return ComplexAggregator.PRODUCT.get().reset();
case PRODUCT2:
return ComplexAggregator.PRODUCT2.get().reset();
case SMALLEST:
return ComplexAggregator.SMALLEST.get().reset();
case SUM:
return ComplexAggregator.SUM.get().reset();
case SUM2:
return ComplexAggregator.SUM2.get().reset();
default :
return null;
}
}
@SuppressWarnings("unchecked") public final <N extends Number>AggregatorFunction<N> getFunction(final Class<?> aType){
if (double.class.isAssignableFrom(aType) || Double.class.isAssignableFrom(aType)) {
return (AggregatorFunction<N>)this.getPrimitiveFunction();
}
 else if (ComplexNumber.class.isAssignableFrom(aType)) {
return (AggregatorFunction<N>)this.getComplexFunction();
}
 else if (BigDecimal.class.isAssignableFrom(aType)) {
return (AggregatorFunction<N>)this.getBigFunction();
}
 else if (RationalNumber.class.isAssignableFrom(aType)) {
return (AggregatorFunction<N>)this.getRationalFunction();
}
 else {
return null;
}
}
public final AggregatorFunction<Double> getPrimitiveFunction(){
switch (this) {
case CARDINALITY:
return PrimitiveAggregator.CARDINALITY.get().reset();
case LARGEST:
return PrimitiveAggregator.LARGEST.get().reset();
case MAXIMUM:
return PrimitiveAggregator.MAX.get().reset();
case MINIMUM:
return PrimitiveAggregator.MIN.get().reset();
case NORM1:
return PrimitiveAggregator.NORM1.get().reset();
case NORM2:
return PrimitiveAggregator.NORM2.get().reset();
case PRODUCT:
return PrimitiveAggregator.PRODUCT.get().reset();
case PRODUCT2:
return PrimitiveAggregator.PRODUCT2.get().reset();
case SMALLEST:
return PrimitiveAggregator.SMALLEST.get().reset();
case SUM:
return PrimitiveAggregator.SUM.get().reset();
case SUM2:
return PrimitiveAggregator.SUM2.get().reset();
default :
return null;
}
}
public final AggregatorFunction<RationalNumber> getRationalFunction(){
switch (this) {
case CARDINALITY:
return RationalAggregator.CARDINALITY.get().reset();
case LARGEST:
return RationalAggregator.LARGEST.get().reset();
case MAXIMUM:
return RationalAggregator.MAX.get().reset();
case MINIMUM:
return RationalAggregator.MIN.get().reset();
case NORM1:
return RationalAggregator.NORM1.get().reset();
case NORM2:
return RationalAggregator.NORM2.get().reset();
case PRODUCT:
return RationalAggregator.PRODUCT.get().reset();
case PRODUCT2:
return RationalAggregator.PRODUCT2.get().reset();
case SMALLEST:
return RationalAggregator.SMALLEST.get().reset();
case SUM:
return RationalAggregator.SUM.get().reset();
case SUM2:
return RationalAggregator.SUM2.get().reset();
default :
return null;
}
}
}
