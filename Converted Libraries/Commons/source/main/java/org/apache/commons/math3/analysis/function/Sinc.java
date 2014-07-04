package org.apache.commons.math3.analysis.function;
import org.apache.commons.math3.analysis.DifferentiableUnivariateFunction;
import org.apache.commons.math3.analysis.FunctionUtils;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.util.FastMath;
/** 
 * <a href="http://en.wikipedia.org/wiki/Sinc_function">Sinc</a> function,
 * defined by
 * <pre><code>
 * sinc(x) = 1            if x = 0,
 * sin(x) / x   otherwise.
 * </code></pre>
 * @since 3.0
 * @version $Id: Sinc.java 1455194 2013-03-11 15:45:54Z luc $
 */
public class Sinc implements UnivariateDifferentiableFunction, DifferentiableUnivariateFunction {
  /** 
 * Value below which the computations are done using Taylor series.
 * <p>
 * The Taylor series for sinc even order derivatives are:
 * <pre>
 * d^(2n)sinc/dx^(2n)     = Sum_(k>=0) (-1)^(n+k) / ((2k)!(2n+2k+1)) x^(2k)
 * = (-1)^n     [ 1/(2n+1) - x^2/(4n+6) + x^4/(48n+120) - x^6/(1440n+5040) + O(x^8) ]
 * </pre>
 * </p>
 * <p>
 * The Taylor series for sinc odd order derivatives are:
 * <pre>
 * d^(2n+1)sinc/dx^(2n+1) = Sum_(k>=0) (-1)^(n+k+1) / ((2k+1)!(2n+2k+3)) x^(2k+1)
 * = (-1)^(n+1) [ x/(2n+3) - x^3/(12n+30) + x^5/(240n+840) - x^7/(10080n+45360) + O(x^9) ]
 * </pre>
 * </p>
 * <p>
 * So the ratio of the fourth term with respect to the first term
 * is always smaller than x^6/720, for all derivative orders.
 * This implies that neglecting this term and using only the first three terms induces
 * a relative error bounded by x^6/720. The SHORTCUT value is chosen such that this
 * relative error is below double precision accuracy when |x| <= SHORTCUT.
 * </p>
 */
  private static final double SHORTCUT=6.0e-3;
  /** 
 * For normalized sinc function. 
 */
  private final boolean normalized;
  /** 
 * The sinc function, {@code sin(x) / x}.
 */
  public Sinc(){
    this(false);
  }
  /** 
 * Instantiates the sinc function.
 * @param normalized If {@code true}, the function is
 * <code> sin(&pi;x) / &pi;x</code>, otherwise {@code sin(x) / x}.
 */
  public Sinc(  boolean normalized){
    this.normalized=normalized;
  }
  /** 
 * {@inheritDoc} 
 */
  public double value(  final double x){
    final double scaledX=normalized ? FastMath.PI * x : x;
    if (FastMath.abs(scaledX) <= SHORTCUT) {
      final double scaledX2=scaledX * scaledX;
      return ((scaledX2 - 20) * scaledX2 + 120) / 120;
    }
 else {
      return FastMath.sin(scaledX) / scaledX;
    }
  }
  /** 
 * {@inheritDoc}
 * @deprecated as of 3.1, replaced by {@link #value(DerivativeStructure)}
 */
  @Deprecated public UnivariateFunction derivative(){
    return FunctionUtils.toDifferentiableUnivariateFunction(this).derivative();
  }
  /** 
 * {@inheritDoc}
 * @since 3.1
 */
  public DerivativeStructure value(  final DerivativeStructure t) throws DimensionMismatchException {
    final double scaledX=(normalized ? FastMath.PI : 1) * t.getValue();
    final double scaledX2=scaledX * scaledX;
    double[] f=new double[t.getOrder() + 1];
    if (FastMath.abs(scaledX) <= SHORTCUT) {
      for (int i=0; i < f.length; ++i) {
        final int k=i / 2;
        if ((i & 0x1) == 0) {
          f[i]=(((k & 0x1) == 0) ? 1 : -1) * (1.0 / (i + 1) - scaledX2 * (1.0 / (2 * i + 6) - scaledX2 / (24 * i + 120)));
        }
 else {
          f[i]=(((k & 0x1) == 0) ? -scaledX : scaledX) * (1.0 / (i + 2) - scaledX2 * (1.0 / (6 * i + 24) - scaledX2 / (120 * i + 720)));
        }
      }
    }
 else {
      final double inv=1 / scaledX;
      final double cos=FastMath.cos(scaledX);
      final double sin=FastMath.sin(scaledX);
      f[0]=inv * sin;
      final double[] sc=new double[f.length];
      sc[0]=1;
      double coeff=inv;
      for (int n=1; n < f.length; ++n) {
        double s=0;
        double c=0;
        final int kStart;
        if ((n & 0x1) == 0) {
          sc[n]=0;
          kStart=n;
        }
 else {
          sc[n]=sc[n - 1];
          c=sc[n];
          kStart=n - 1;
        }
        for (int k=kStart; k > 1; k-=2) {
          sc[k]=(k - n) * sc[k] - sc[k - 1];
          s=s * scaledX2 + sc[k];
          sc[k - 1]=(k - 1 - n) * sc[k - 1] + sc[k - 2];
          c=c * scaledX2 + sc[k - 1];
        }
        sc[0]*=-n;
        s=s * scaledX2 + sc[0];
        coeff*=inv;
        f[n]=coeff * (s * sin + c * scaledX * cos);
      }
    }
    if (normalized) {
      double scale=FastMath.PI;
      for (int i=1; i < f.length; ++i) {
        f[i]*=scale;
        scale*=FastMath.PI;
      }
    }
    return t.compose(f);
  }
}
