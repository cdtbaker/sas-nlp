package org.ojalgo.matrix.decomposition;
import java.math.BigDecimal;
import org.ojalgo.access.Access2D;
import org.ojalgo.array.Array1D;
import org.ojalgo.array.SimpleArray;
import org.ojalgo.constant.PrimitiveMath;
import org.ojalgo.matrix.MatrixUtils;
import org.ojalgo.matrix.store.BigDenseStore;
import org.ojalgo.matrix.store.ComplexDenseStore;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.matrix.store.PrimitiveDenseStore;
import org.ojalgo.scalar.ComplexNumber;
import org.ojalgo.type.context.NumberContext;
/** 
 * Orginalet, sedan ett tag Based on SVDnew2, but with transposing so that calculations are always made on a matrix that
 * "isAspectRationNormal". Based on SVDnew5, but with Rotation replaced by the new alternative.
 * @author apete
 */
abstract class SVDnew32<N extends Number & Comparable<N>> extends SingularValueDecomposition<N> {
static final class Big extends SVDnew32<BigDecimal> {
    Big(){
      super(BigDenseStore.FACTORY,new BidiagonalDecomposition.Big());
    }
  }
static final class Complex extends SVDnew32<ComplexNumber> {
    Complex(){
      super(ComplexDenseStore.FACTORY,new BidiagonalDecomposition.Complex());
    }
  }
static final class Primitive extends SVDnew32<Double> {
    Primitive(){
      super(PrimitiveDenseStore.FACTORY,new BidiagonalDecomposition.Primitive());
    }
  }
  static void doCase1(  final double[] s,  final double[] e,  final int p,  final int k,  final DecompositionStore<?> aQ2){
    double f=e[p - 2];
    e[p - 2]=PrimitiveMath.ZERO;
    double t;
    double cs;
    double sn;
    for (int j=p - 2; j >= k; j--) {
      t=Math.hypot(s[j],f);
      cs=s[j] / t;
      sn=f / t;
      s[j]=t;
      if (j != k) {
        f=-sn * e[j - 1];
        e[j - 1]=cs * e[j - 1];
      }
      if (aQ2 != null) {
        aQ2.rotateRight(p - 1,j,cs,sn);
      }
    }
  }
  static void doCase2(  final double[] s,  final double[] e,  final int p,  final int k,  final DecompositionStore<?> aQ1){
    double f=e[k - 1];
    e[k - 1]=PrimitiveMath.ZERO;
    double t;
    double cs;
    double sn;
    for (int j=k; j < p; j++) {
      t=Math.hypot(s[j],f);
      cs=s[j] / t;
      sn=f / t;
      s[j]=t;
      f=-sn * e[j];
      e[j]=cs * e[j];
      if (aQ1 != null) {
        aQ1.rotateRight(k - 1,j,cs,sn);
      }
    }
  }
  static void doCase3(  final double[] s,  final double[] e,  final int p,  final int k,  final DecompositionStore<?> aQ1,  final DecompositionStore<?> aQ2){
    final int indPm1=p - 1;
    final int indPm2=p - 2;
    final double scale=Math.max(Math.max(Math.max(Math.max(Math.abs(s[indPm1]),Math.abs(s[indPm2])),Math.abs(e[indPm2])),Math.abs(s[k])),Math.abs(e[k]));
    final double sPm1=s[indPm1] / scale;
    final double sPm2=s[indPm2] / scale;
    final double ePm2=e[indPm2] / scale;
    final double sK=s[k] / scale;
    final double eK=e[k] / scale;
    final double b=(((sPm2 + sPm1) * (sPm2 - sPm1)) + (ePm2 * ePm2)) / PrimitiveMath.TWO;
    final double c=(sPm1 * ePm2) * (sPm1 * ePm2);
    double shift=Math.sqrt((b * b) + c);
    if (b < PrimitiveMath.ZERO) {
      shift=-shift;
    }
    shift=c / (b + shift);
    double f=((sK + sPm1) * (sK - sPm1)) + shift;
    double g=sK * eK;
    double t;
    double cs;
    double sn;
    for (int j=k; j < indPm1; j++) {
      t=Math.hypot(f,g);
      cs=f / t;
      sn=g / t;
      if (j != k) {
        e[j - 1]=t;
      }
      f=(cs * s[j]) + (sn * e[j]);
      e[j]=(cs * e[j]) - (sn * s[j]);
      g=sn * s[j + 1];
      s[j + 1]=cs * s[j + 1];
      if (aQ2 != null) {
        aQ2.rotateRight(j + 1,j,cs,sn);
      }
      t=Math.hypot(f,g);
      cs=f / t;
      sn=g / t;
      s[j]=t;
      f=(cs * e[j]) + (sn * s[j + 1]);
      s[j + 1]=(-sn * e[j]) + (cs * s[j + 1]);
      g=sn * e[j + 1];
      e[j + 1]=cs * e[j + 1];
      if (aQ1 != null) {
        aQ1.rotateRight(j + 1,j,cs,sn);
      }
    }
    e[indPm2]=f;
  }
  static void doCase4(  final double[] s,  final int k,  final DecompositionStore<?> aQ1,  final DecompositionStore<?> aQ2){
    final int tmpDiagDim=s.length;
    final double tmpSk=s[k];
    if (tmpSk < PrimitiveMath.ZERO) {
      s[k]=-tmpSk;
      if (aQ2 != null) {
        aQ2.negateColumn(k);
      }
    }
 else     if (tmpSk == PrimitiveMath.ZERO) {
      s[k]=PrimitiveMath.ZERO;
    }
    int tmpK=k;
    while (tmpK < (tmpDiagDim - 1)) {
      if (s[tmpK] >= s[tmpK + 1]) {
        break;
      }
      final double t=s[tmpK];
      s[tmpK]=s[tmpK + 1];
      s[tmpK + 1]=t;
      if (aQ1 != null) {
        aQ1.exchangeColumns(tmpK + 1,tmpK);
      }
      if (aQ2 != null) {
        aQ2.exchangeColumns(tmpK + 1,tmpK);
      }
      tmpK++;
    }
  }
  static Array1D<Double> toDiagonal(  final DiagonalAccess<?> bidiagonal,  final DecompositionStore<?> aQ1,  final DecompositionStore<?> aQ2){
    final int tmpDiagDim=bidiagonal.mainDiagonal.length;
    final double[] s=bidiagonal.mainDiagonal.toRawCopy();
    final double[] e=new double[tmpDiagDim];
    final int tmpOffLength=bidiagonal.superdiagonal.length;
    for (int i=0; i < tmpOffLength; i++) {
      e[i]=bidiagonal.superdiagonal.doubleValue(i);
    }
    int kase;
    int k;
    int p=tmpDiagDim;
    while (p > 0) {
      kase=0;
      k=0;
      for (k=p - 2; k >= -1; k--) {
        if (k == -1) {
          break;
        }
        if (Math.abs(e[k]) <= (PrimitiveMath.TINY + (PrimitiveMath.MACHINE_DOUBLE_ERROR * (Math.abs(s[k]) + Math.abs(s[k + 1]))))) {
          e[k]=PrimitiveMath.ZERO;
          break;
        }
      }
      if (k == (p - 2)) {
        kase=4;
      }
 else {
        int ks;
        for (ks=p - 1; ks >= k; ks--) {
          if (ks == k) {
            break;
          }
          final double t=(ks != p ? Math.abs(e[ks]) : PrimitiveMath.ZERO) + (ks != (k + 1) ? Math.abs(e[ks - 1]) : PrimitiveMath.ZERO);
          if (Math.abs(s[ks]) <= (PrimitiveMath.TINY + (PrimitiveMath.MACHINE_DOUBLE_ERROR * t))) {
            s[ks]=PrimitiveMath.ZERO;
            break;
          }
        }
        if (ks == k) {
          kase=3;
        }
 else         if (ks == (p - 1)) {
          kase=1;
        }
 else {
          kase=2;
          k=ks;
        }
      }
      k++;
switch (kase) {
case 1:
        SVDnew32.doCase1(s,e,p,k,aQ2);
      break;
case 2:
    SVDnew32.doCase2(s,e,p,k,aQ1);
  break;
case 3:
SVDnew32.doCase3(s,e,p,k,aQ1,aQ2);
break;
case 4:
SVDnew32.doCase4(s,k,aQ1,aQ2);
p--;
break;
default :
throw new IllegalStateException();
}
}
return Array1D.PRIMITIVE.wrap(SimpleArray.wrapPrimitive(s));
}
protected SVDnew32(final DecompositionStore.Factory<N,? extends DecompositionStore<N>> aFactory,final BidiagonalDecomposition<N> aBidiagonal){
super(aFactory,aBidiagonal);
}
public boolean equals(final MatrixStore<N> other,final NumberContext context){
return MatrixUtils.equals(other,this,NumberContext.getGeneral(6));
}
public boolean isOrdered(){
return true;
}
public boolean isSolvable(){
return this.isComputed();
}
@Override public final MatrixStore<N> solve(final Access2D<N> rhs){
return this.getInverse().multiplyRight(rhs);
}
@Override protected boolean doCompute(final Access2D<?> aMtrx,final boolean singularValuesOnly,final boolean fullSize){
this.computeBidiagonal(aMtrx,fullSize);
final DiagonalAccess<N> tmpBidiagonal=this.getBidiagonalAccessD();
final DecompositionStore<N> tmpQ1=singularValuesOnly ? null : this.getBidiagonalQ1();
final DecompositionStore<N> tmpQ2=singularValuesOnly ? null : this.getBidiagonalQ2();
final Array1D<Double> tmpDiagonal=SVDnew32.toDiagonal(tmpBidiagonal,tmpQ1,tmpQ2);
this.setSingularValues(tmpDiagonal);
return this.computed(true);
}
@Override protected final MatrixStore<N> makeD(){
return this.wrap(new DiagonalAccess<Double>(this.getSingularValues(),null,null,PrimitiveMath.ZERO));
}
@Override protected final MatrixStore<N> makeQ1(){
return this.getBidiagonalQ1();
}
@Override protected final MatrixStore<N> makeQ2(){
return this.getBidiagonalQ2();
}
@Override protected final Array1D<Double> makeSingularValues(){
throw new IllegalStateException("Should never have to be called!");
}
}
