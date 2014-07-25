package org.ojalgo.function.polynomial;
import java.util.List;
import org.ojalgo.access.Access1D;
import org.ojalgo.function.Function.Differentiable;
import org.ojalgo.function.Function.Integratable;
import org.ojalgo.function.UnaryFunction;
import org.ojalgo.series.NumberSeries;
public interface PolynomialFunction<N extends Number> extends UnaryFunction<N>, Access1D<N>, Differentiable<N,PolynomialFunction<N>>, Integratable<N,PolynomialFunction<N>> {
  int degree();
  void estimate(  Access1D<?> x,  Access1D<?> y);
  void estimate(  List<? extends Number> x,  List<? extends Number> y);
  void estimate(  NumberSeries<?> samples);
  void set(  Access1D<?> coefficients);
  void set(  final int aPower,  final double aCoefficient);
  void set(  final int aPower,  final N aCoefficient);
}
