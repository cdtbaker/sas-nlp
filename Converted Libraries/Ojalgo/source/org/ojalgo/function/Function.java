package org.ojalgo.function;
public interface Function<N extends Number> {
public static interface Differentiable<N extends Number,F extends Function<N>> extends Function<N> {
    F buildDerivative();
  }
public static interface Integratable<N extends Number,F extends Function<N>> extends Function<N> {
    F buildPrimitive();
    N integrate(    N aFromPoint,    N aToPoint);
  }
}
