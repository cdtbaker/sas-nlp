package org.ojalgo.matrix.store.operation;
import static org.ojalgo.constant.PrimitiveMath.*;
import java.math.BigDecimal;
import org.ojalgo.concurrent.DivideAndConquer;
import org.ojalgo.constant.BigMath;
import org.ojalgo.constant.PrimitiveMath;
import org.ojalgo.function.BigFunction;
import org.ojalgo.function.ComplexFunction;
import org.ojalgo.matrix.transformation.Householder;
import org.ojalgo.scalar.ComplexNumber;
/** 
 * Performs Householder transformation from both sides simultaneously
 * assuming that [A] is hermitian (square symmetric) [A] = [A]<sup>H</sup>.
 * Will only read from and write to the lower/left triangular part of [A].
 * @author apete
 */
public final class HouseholderHermitian extends MatrixOperation {
  public static int THRESHOLD=64;
  public static void invoke(  final BigDecimal[] aData,  final Householder.Big aHouseholder,  final BigDecimal[] aWorker){
    final BigDecimal[] tmpVector=aHouseholder.vector;
    final int tmpFirst=aHouseholder.first;
    final int tmpLength=tmpVector.length;
    final BigDecimal tmpBeta=aHouseholder.beta;
    final int tmpCount=tmpLength - tmpFirst;
    if (tmpCount > THRESHOLD) {
      final DivideAndConquer tmpConqurer=new DivideAndConquer(){
        @Override protected void conquer(        final int aFirst,        final int aLimit){
          MultiplyHermitianAndVector.invoke(aWorker,aFirst,aLimit,aData,tmpVector,tmpFirst);
        }
      }
;
      tmpConqurer.invoke(tmpFirst,tmpLength,THRESHOLD);
    }
 else {
      MultiplyHermitianAndVector.invoke(aWorker,tmpFirst,tmpLength,aData,tmpVector,tmpFirst);
    }
    BigDecimal tmpVal=BigMath.ZERO;
    for (int c=tmpFirst; c < tmpLength; c++) {
      tmpVal=tmpVal.add(tmpVector[c].multiply(aWorker[c]));
    }
    tmpVal=BigFunction.DIVIDE.invoke(tmpVal.multiply(tmpBeta),BigMath.TWO);
    for (int c=tmpFirst; c < tmpLength; c++) {
      aWorker[c]=tmpBeta.multiply(aWorker[c].subtract(tmpVal.multiply(tmpVector[c])));
    }
    if (tmpCount > THRESHOLD) {
      final DivideAndConquer tmpConqurer=new DivideAndConquer(){
        @Override protected void conquer(        final int aFirst,        final int aLimit){
          HermitianRank2Update.invoke(aData,aFirst,aLimit,tmpVector,aWorker);
        }
      }
;
      tmpConqurer.invoke(tmpFirst,tmpLength,THRESHOLD);
    }
 else {
      HermitianRank2Update.invoke(aData,tmpFirst,tmpLength,tmpVector,aWorker);
    }
  }
  public static void invoke(  final ComplexNumber[] aData,  final Householder.Complex aHouseholder,  final ComplexNumber[] aWorker){
    final ComplexNumber[] tmpVector=aHouseholder.vector;
    final int tmpFirst=aHouseholder.first;
    final int tmpLength=tmpVector.length;
    final ComplexNumber tmpBeta=aHouseholder.beta;
    final int tmpCount=tmpLength - tmpFirst;
    if (tmpCount > THRESHOLD) {
      final DivideAndConquer tmpConqurer=new DivideAndConquer(){
        @Override protected void conquer(        final int aFirst,        final int aLimit){
          MultiplyHermitianAndVector.invoke(aWorker,aFirst,aLimit,aData,tmpVector,tmpFirst);
        }
      }
;
      tmpConqurer.invoke(tmpFirst,tmpLength,THRESHOLD);
    }
 else {
      MultiplyHermitianAndVector.invoke(aWorker,tmpFirst,tmpLength,aData,tmpVector,tmpFirst);
    }
    ComplexNumber tmpVal=ComplexNumber.ZERO;
    for (int c=tmpFirst; c < tmpLength; c++) {
      tmpVal=tmpVal.add(tmpVector[c].conjugate().multiply(aWorker[c]));
    }
    tmpVal=ComplexFunction.DIVIDE.invoke(tmpVal.multiply(tmpBeta),ComplexNumber.TWO);
    for (int c=tmpFirst; c < tmpLength; c++) {
      aWorker[c]=tmpBeta.multiply(aWorker[c].subtract(tmpVal.multiply(tmpVector[c])));
    }
    if (tmpCount > THRESHOLD) {
      final DivideAndConquer tmpConqurer=new DivideAndConquer(){
        @Override protected void conquer(        final int aFirst,        final int aLimit){
          HermitianRank2Update.invoke(aData,aFirst,aLimit,tmpVector,aWorker);
        }
      }
;
      tmpConqurer.invoke(tmpFirst,tmpLength,THRESHOLD);
    }
 else {
      HermitianRank2Update.invoke(aData,tmpFirst,tmpLength,tmpVector,aWorker);
    }
  }
  public static void invoke(  final double[] aData,  final Householder.Primitive aHouseholder,  final double[] aWorker){
    final double[] tmpVector=aHouseholder.vector;
    final int tmpFirst=aHouseholder.first;
    final int tmpLength=tmpVector.length;
    final double tmpBeta=aHouseholder.beta;
    final int tmpCount=tmpLength - tmpFirst;
    if (tmpCount > THRESHOLD) {
      final DivideAndConquer tmpConqurer=new DivideAndConquer(){
        @Override protected void conquer(        final int aFirst,        final int aLimit){
          MultiplyHermitianAndVector.invoke(aWorker,aFirst,aLimit,aData,tmpVector,tmpFirst);
        }
      }
;
      tmpConqurer.invoke(tmpFirst,tmpLength,THRESHOLD);
    }
 else {
      MultiplyHermitianAndVector.invoke(aWorker,tmpFirst,tmpLength,aData,tmpVector,tmpFirst);
    }
    double tmpVal=ZERO;
    for (int c=tmpFirst; c < tmpLength; c++) {
      tmpVal+=tmpVector[c] * aWorker[c];
    }
    tmpVal*=(tmpBeta / TWO);
    for (int c=tmpFirst; c < tmpLength; c++) {
      aWorker[c]=tmpBeta * (aWorker[c] - (tmpVal * tmpVector[c]));
    }
    if (tmpCount > THRESHOLD) {
      final DivideAndConquer tmpConqurer=new DivideAndConquer(){
        @Override protected void conquer(        final int aFirst,        final int aLimit){
          HermitianRank2Update.invoke(aData,aFirst,aLimit,tmpVector,aWorker);
        }
      }
;
      tmpConqurer.invoke(tmpFirst,tmpLength,THRESHOLD);
    }
 else {
      HermitianRank2Update.invoke(aData,tmpFirst,tmpLength,tmpVector,aWorker);
    }
  }
  public static void tred2j(  final double[] z,  final double[] d,  final double[] e,  final boolean yesvecs){
    final int n=d.length;
    double scale;
    double h;
    double f;
    double g;
    double hh;
    final int tmpRowDim=n;
    final int tmpLast=n - 1;
    for (int i=0; i < n; i++) {
      d[i]=z[i + (tmpRowDim * tmpLast)];
    }
    for (int i=tmpLast; i > 0; i--) {
      final int l=i - 1;
      h=scale=PrimitiveMath.ZERO;
      for (int k=0; k < i; k++) {
        scale+=Math.abs(d[k]);
      }
      if (scale == PrimitiveMath.ZERO) {
        e[i]=d[l];
        for (int j=0; j < i; j++) {
          d[j]=z[l + (tmpRowDim * j)];
          z[i + (tmpRowDim * j)]=PrimitiveMath.ZERO;
          z[j + (tmpRowDim * i)]=PrimitiveMath.ZERO;
        }
      }
 else {
        for (int k=0; k < i; k++) {
          d[k]/=scale;
          h+=d[k] * d[k];
        }
        f=d[l];
        g=Math.sqrt(h);
        if (f > 0) {
          g=-g;
        }
        e[i]=scale * g;
        h-=f * g;
        d[l]=f - g;
        for (int j=0; j < i; j++) {
          e[j]=PrimitiveMath.ZERO;
        }
        for (int j=0; j < i; j++) {
          f=d[j];
          z[j + (tmpRowDim * i)]=f;
          g=e[j] + (z[j + (tmpRowDim * j)] * f);
          for (int k=j + 1; k <= l; k++) {
            g+=z[k + (tmpRowDim * j)] * d[k];
            e[k]+=z[k + (tmpRowDim * j)] * f;
          }
          e[j]=g;
        }
        f=PrimitiveMath.ZERO;
        for (int j=0; j < i; j++) {
          e[j]/=h;
          f+=e[j] * d[j];
        }
        hh=f / (h + h);
        for (int j=0; j < i; j++) {
          e[j]-=hh * d[j];
        }
        for (int j=0; j < i; j++) {
          f=d[j];
          g=e[j];
          for (int k=j; k <= l; k++) {
            z[k + (tmpRowDim * j)]-=((f * e[k]) + (g * d[k]));
          }
          d[j]=z[l + (tmpRowDim * j)];
          z[i + (tmpRowDim * j)]=PrimitiveMath.ZERO;
        }
      }
      d[i]=h;
    }
    if (yesvecs) {
      for (int i=0; i < tmpLast; i++) {
        final int l=i + 1;
        z[tmpLast + (tmpRowDim * i)]=z[i + (tmpRowDim * i)];
        z[i + (tmpRowDim * i)]=PrimitiveMath.ONE;
        h=d[l];
        if (h != PrimitiveMath.ZERO) {
          for (int k=0; k <= i; k++) {
            d[k]=z[k + (tmpRowDim * l)] / h;
          }
          for (int j=0; j <= i; j++) {
            g=PrimitiveMath.ZERO;
            for (int k=0; k <= i; k++) {
              g+=z[k + (tmpRowDim * l)] * z[k + (tmpRowDim * j)];
            }
            for (int k=0; k <= i; k++) {
              z[k + (tmpRowDim * j)]-=g * d[k];
            }
          }
        }
        for (int k=0; k <= i; k++) {
          z[k + (tmpRowDim * l)]=PrimitiveMath.ZERO;
        }
      }
      for (int j=0; j < n; j++) {
        d[j]=z[tmpLast + (tmpRowDim * j)];
        z[tmpLast + (tmpRowDim * j)]=PrimitiveMath.ZERO;
      }
      z[tmpLast + (tmpRowDim * tmpLast)]=PrimitiveMath.ONE;
      e[0]=PrimitiveMath.ZERO;
    }
  }
  public static void tred2nr(  final double[] z,  final double[] d,  final double[] e,  final boolean yesvecs){
    final int n=d.length;
    int l;
    final int tmpRowDim=n;
    double scale;
    double h;
    double hh;
    double g;
    double f;
    for (int i=n - 1; i > 0; i--) {
      l=i - 1;
      scale=PrimitiveMath.ZERO;
      h=PrimitiveMath.ZERO;
      if (l > 0) {
        for (int k=0; k < i; k++) {
          scale+=Math.abs(z[i + (k * tmpRowDim)]);
        }
        if (scale == PrimitiveMath.ZERO) {
          e[i]=z[i + (l * tmpRowDim)];
        }
 else {
          for (int k=0; k < i; k++) {
            z[i + (k * tmpRowDim)]/=scale;
            h+=z[i + (k * tmpRowDim)] * z[i + (k * tmpRowDim)];
          }
          f=z[i + (l * tmpRowDim)];
          g=(f >= PrimitiveMath.ZERO) ? -Math.sqrt(h) : Math.sqrt(h);
          e[i]=scale * g;
          h-=f * g;
          z[i + (l * tmpRowDim)]=f - g;
          f=PrimitiveMath.ZERO;
          for (int j=0; j < i; j++) {
            if (yesvecs) {
              z[j + (i * tmpRowDim)]=z[i + (j * tmpRowDim)] / h;
            }
            g=PrimitiveMath.ZERO;
            for (int k=0; k < (j + 1); k++) {
              g+=z[j + (k * tmpRowDim)] * z[i + (k * tmpRowDim)];
            }
            for (int k=j + 1; k < i; k++) {
              g+=z[k + (j * tmpRowDim)] * z[i + (k * tmpRowDim)];
            }
            e[j]=g / h;
            f+=e[j] * z[i + (j * tmpRowDim)];
          }
          hh=f / (h + h);
          for (int j=0; j < i; j++) {
            f=z[i + (j * tmpRowDim)];
            e[j]=g=e[j] - (hh * f);
            for (int k=0; k < (j + 1); k++) {
              z[j + (k * tmpRowDim)]-=((f * e[k]) + (g * z[i + (k * tmpRowDim)]));
            }
          }
        }
      }
 else {
        e[i]=z[i + (l * tmpRowDim)];
      }
      d[i]=h;
    }
    if (yesvecs) {
      d[0]=PrimitiveMath.ZERO;
    }
    e[0]=PrimitiveMath.ZERO;
    for (int i=0; i < n; i++) {
      if (yesvecs) {
        if (d[i] != PrimitiveMath.ZERO) {
          for (int j=0; j < i; j++) {
            g=PrimitiveMath.ZERO;
            for (int k=0; k < i; k++) {
              g+=z[i + (k * tmpRowDim)] * z[k + (j * tmpRowDim)];
            }
            for (int k=0; k < i; k++) {
              z[k + (j * tmpRowDim)]-=g * z[k + (i * tmpRowDim)];
            }
          }
        }
        d[i]=z[i + (i * tmpRowDim)];
        z[i + (i * tmpRowDim)]=PrimitiveMath.ONE;
        for (int j=0; j < i; j++) {
          z[i + (j * tmpRowDim)]=PrimitiveMath.ZERO;
          z[j + (i * tmpRowDim)]=PrimitiveMath.ZERO;
        }
      }
 else {
        d[i]=z[i + (i * tmpRowDim)];
      }
    }
  }
  private HouseholderHermitian(){
    super();
  }
}
