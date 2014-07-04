package org.ojalgo.function.aggregator;
import org.ojalgo.function.VoidFunction;
import org.ojalgo.scalar.Scalar;
public interface AggregatorFunction<N extends Number> extends VoidFunction<N> {
  double doubleValue();
  N getNumber();
  int intValue();
  void merge(  N anArg);
  N merge(  N aResult1,  N aResult2);
  AggregatorFunction<N> reset();
  Scalar<N> toScalar();
}
