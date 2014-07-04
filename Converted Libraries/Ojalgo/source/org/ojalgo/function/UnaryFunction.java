package org.ojalgo.function;
public interface UnaryFunction<N extends Number> extends Function<N> {
  double invoke(  double arg);
  N invoke(  N arg);
}
