package org.ojalgo.function;
public interface NullaryFunction<N extends Number> extends Function<N> {
  double doubleValue();
  N invoke();
}
