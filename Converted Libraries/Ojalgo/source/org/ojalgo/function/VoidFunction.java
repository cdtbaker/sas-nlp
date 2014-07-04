package org.ojalgo.function;
public interface VoidFunction<N extends Number> extends Function<N> {
  void invoke(  double arg);
  void invoke(  N arg);
}
