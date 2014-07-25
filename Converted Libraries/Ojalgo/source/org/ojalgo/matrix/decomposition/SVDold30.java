package org.ojalgo.matrix.decomposition;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.ojalgo.ProgrammingError;
import org.ojalgo.access.Access2D;
import org.ojalgo.array.Array1D;
import org.ojalgo.array.Array2D;
import org.ojalgo.concurrent.DaemonPoolExecutor;
import org.ojalgo.constant.BigMath;
import org.ojalgo.constant.PrimitiveMath;
import org.ojalgo.function.BigFunction;
import org.ojalgo.function.ComplexFunction;
import org.ojalgo.function.PrimitiveFunction;
import org.ojalgo.matrix.MatrixUtils;
import org.ojalgo.matrix.store.BigDenseStore;
import org.ojalgo.matrix.store.ComplexDenseStore;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.matrix.store.PhysicalStore;
import org.ojalgo.matrix.store.PrimitiveDenseStore;
import org.ojalgo.matrix.transformation.Rotation;
import org.ojalgo.scalar.ComplexNumber;
import org.ojalgo.scalar.Scalar;
import org.ojalgo.type.TypeUtils;
import org.ojalgo.type.context.NumberContext;
/** 
 * Samma som orginalet, but without QR. Instead Householder directly. Wasn't faster. Try going directly to bidiagonal
 * instead. Based SVDold2, but with GenericRotaion replaced with Rotation.
 * @author apete
 */
abstract class SVDold30<N extends Number & Comparable<N>> extends SingularValueDecomposition<N> {
static final class Big extends SVDold30<BigDecimal> {
    Big(){
      super(BigDenseStore.FACTORY,new BidiagonalDecomposition.Big());
    }
    @Override protected Rotation<BigDecimal>[] rotations(    final PhysicalStore<BigDecimal> aStore,    final int aLowInd,    final int aHighInd,    final Rotation<BigDecimal>[] retVal){
      final BigDecimal a00=aStore.get(aLowInd,aLowInd);
      final BigDecimal a01=aStore.get(aLowInd,aHighInd);
      final BigDecimal a10=aStore.get(aHighInd,aLowInd);
      final BigDecimal a11=aStore.get(aHighInd,aHighInd);
      final BigDecimal x=a00.add(a11);
      final BigDecimal y=a10.subtract(a01);
      BigDecimal t;
      final BigDecimal cg;
      final BigDecimal sg;
      if (y.signum() == 0) {
        cg=BigFunction.SIGNUM.invoke(x);
        sg=BigMath.ZERO;
      }
 else       if (x.signum() == 0) {
        sg=BigFunction.SIGNUM.invoke(y);
        cg=BigMath.ZERO;
      }
 else       if (y.abs().compareTo(x.abs()) == 1) {
        t=BigFunction.DIVIDE.invoke(x,y);
        sg=BigFunction.DIVIDE.invoke(BigFunction.SIGNUM.invoke(y),BigFunction.SQRT1PX2.invoke(t));
        cg=sg.multiply(t);
      }
 else {
        t=BigFunction.DIVIDE.invoke(y,x);
        cg=BigFunction.DIVIDE.invoke(BigFunction.SIGNUM.invoke(x),BigFunction.SQRT1PX2.invoke(t));
        sg=cg.multiply(t);
      }
      final BigDecimal b00=cg.multiply(a00).add(sg.multiply(a10));
      final BigDecimal b11=cg.multiply(a11).subtract(sg.multiply(a01));
      final BigDecimal b2=cg.multiply(a01.add(a10)).add(sg.multiply(a11.subtract(a00)));
      t=BigFunction.DIVIDE.invoke(b11.subtract(b00),b2);
      t=BigFunction.DIVIDE.invoke(BigFunction.SIGNUM.invoke(t),BigFunction.SQRT1PX2.invoke(t).add(t.abs()));
      final BigDecimal cj=BigFunction.DIVIDE.invoke(BigMath.ONE,BigFunction.SQRT1PX2.invoke(t));
      final BigDecimal sj=cj.multiply(t);
      retVal[1]=new Rotation.Big(aLowInd,aHighInd,cj,sj);
      retVal[0]=new Rotation.Big(aLowInd,aHighInd,cj.multiply(cg).add(sj.multiply(sg)),cj.multiply(sg).subtract(sj.multiply(cg)));
      return retVal;
    }
  }
static final class Complex extends SVDold30<ComplexNumber> {
    Complex(){
      super(ComplexDenseStore.FACTORY,new BidiagonalDecomposition.Complex());
    }
    @Override protected Rotation<ComplexNumber>[] rotations(    final PhysicalStore<ComplexNumber> aStore,    final int aLowInd,    final int aHighInd,    final Rotation<ComplexNumber>[] retVal){
      final ComplexNumber a00=aStore.get(aLowInd,aLowInd);
      final ComplexNumber a01=aStore.get(aLowInd,aHighInd);
      final ComplexNumber a10=aStore.get(aHighInd,aLowInd);
      final ComplexNumber a11=aStore.get(aHighInd,aHighInd);
      final ComplexNumber x=a00.add(a11);
      final ComplexNumber y=a10.subtract(a01);
      ComplexNumber t;
      final ComplexNumber cg;
      final ComplexNumber sg;
      if (y.isZero()) {
        cg=x.signum();
        sg=ComplexNumber.ZERO;
      }
 else       if (x.isZero()) {
        sg=y.signum();
        cg=ComplexNumber.ZERO;
      }
 else       if (y.compareTo(x) == 1) {
        t=x.divide(y);
        sg=y.signum().divide(ComplexFunction.SQRT1PX2.invoke(t));
        cg=sg.multiply(t);
      }
 else {
        t=y.divide(x);
        cg=x.signum().divide(ComplexFunction.SQRT1PX2.invoke(t));
        sg=cg.multiply(t);
      }
      final ComplexNumber b00=cg.multiply(a00).add(sg.multiply(a10));
      final ComplexNumber b11=cg.multiply(a11).subtract(sg.multiply(a01));
      final ComplexNumber b2=cg.multiply(a01.add(a10)).add(sg.multiply(a11.subtract(a00)));
      t=b11.subtract(b00).divide(b2);
      t=t.signum().divide(ComplexFunction.SQRT1PX2.invoke(t).add(t.norm()));
      final ComplexNumber cj=ComplexFunction.SQRT1PX2.invoke(t).invert();
      final ComplexNumber sj=cj.multiply(t);
      retVal[1]=new Rotation.Complex(aLowInd,aHighInd,cj,sj);
      retVal[0]=new Rotation.Complex(aLowInd,aHighInd,cj.multiply(cg).add(sj.multiply(sg)),cj.multiply(sg).subtract(sj.multiply(cg)));
      return retVal;
    }
  }
static final class Primitive extends SVDold30<Double> {
    Primitive(){
      super(PrimitiveDenseStore.FACTORY,new BidiagonalDecomposition.Primitive());
    }
    @Override protected Rotation<Double>[] rotations(    final PhysicalStore<Double> aStore,    final int aLowInd,    final int aHighInd,    final Rotation<Double>[] retVal){
      final double a00=aStore.doubleValue(aLowInd,aLowInd);
      final double a01=aStore.doubleValue(aLowInd,aHighInd);
      final double a10=aStore.doubleValue(aHighInd,aLowInd);
      final double a11=aStore.doubleValue(aHighInd,aHighInd);
      final double x=a00 + a11;
      final double y=a10 - a01;
      double t;
      final double cg;
      final double sg;
      if (TypeUtils.isZero(y)) {
        cg=Math.signum(x);
        sg=PrimitiveMath.ZERO;
      }
 else       if (TypeUtils.isZero(x)) {
        sg=Math.signum(y);
        cg=PrimitiveMath.ZERO;
      }
 else       if (Math.abs(y) > Math.abs(x)) {
        t=x / y;
        sg=Math.signum(y) / PrimitiveFunction.SQRT1PX2.invoke(t);
        cg=sg * t;
      }
 else {
        t=y / x;
        cg=Math.signum(x) / PrimitiveFunction.SQRT1PX2.invoke(t);
        sg=cg * t;
      }
      final double b00=(cg * a00) + (sg * a10);
      final double b11=(cg * a11) - (sg * a01);
      final double b2=(cg * (a01 + a10)) + (sg * (a11 - a00));
      t=(b11 - b00) / b2;
      t=Math.signum(t) / (PrimitiveFunction.SQRT1PX2.invoke(t) + Math.abs(t));
      final double cj=PrimitiveMath.ONE / PrimitiveFunction.SQRT1PX2.invoke(t);
      final double sj=cj * t;
      retVal[1]=new Rotation.Primitive(aLowInd,aHighInd,cj,sj);
      retVal[0]=new Rotation.Primitive(aLowInd,aHighInd,((cj * cg) + (sj * sg)),((cj * sg) - (sj * cg)));
      return retVal;
    }
  }
  private Future<PhysicalStore<N>> myFutureQ1;
  private Future<PhysicalStore<N>> myFutureQ2;
  private final List<Rotation<N>> myQ1Rotations=new ArrayList<Rotation<N>>();
  private final List<Rotation<N>> myQ2Rotations=new ArrayList<Rotation<N>>();
  protected SVDold30(  final DecompositionStore.Factory<N,? extends DecompositionStore<N>> aFactory,  final BidiagonalDecomposition<N> aBidiagonal){
    super(aFactory,aBidiagonal);
  }
  public final boolean equals(  final MatrixStore<N> aStore,  final NumberContext context){
    return MatrixUtils.equals(aStore,this,context);
  }
  public boolean isOrdered(){
    return false;
  }
  public boolean isSolvable(){
    return this.isComputed();
  }
  @Override public final void reset(){
    super.reset();
    myQ1Rotations.clear();
    myQ2Rotations.clear();
    myFutureQ1=null;
    myFutureQ2=null;
  }
  @Override public final MatrixStore<N> solve(  final Access2D<N> rhs){
    return this.getInverse().multiplyRight(rhs);
  }
  @Override @SuppressWarnings("unchecked") protected final boolean doCompute(  final Access2D<?> aStore,  final boolean singularValuesOnly,  final boolean fullSize){
    final int tmpMinDim=Math.min(aStore.getRowDim(),aStore.getColDim());
    this.computeBidiagonal(aStore,fullSize);
    final DecompositionStore<N> tmpSimilar=this.copy(this.getBidiagonalAccessD());
    this.setD(tmpSimilar);
    this.setSingularValues(Array1D.PRIMITIVE.makeZero(tmpMinDim));
    Rotation<N>[] tmpRotations=new Rotation[2];
    final N tmpZero=this.getStaticZero();
    boolean tmpNotAllZeros=true;
    for (int l=0; tmpNotAllZeros && (l < tmpMinDim); l++) {
      tmpNotAllZeros=false;
      int i;
      for (int i0=1; i0 < tmpMinDim; i0++) {
        for (int j=0; j < (tmpMinDim - i0); j++) {
          i=i0 + j;
          if (!tmpSimilar.isZero(i,j) || !tmpSimilar.isZero(j,i)) {
            tmpNotAllZeros=true;
            tmpRotations=this.rotations(tmpSimilar,j,i,tmpRotations);
            tmpSimilar.transformLeft(tmpRotations[0]);
            tmpSimilar.transformRight(tmpRotations[1]);
            myQ1Rotations.add(tmpRotations[0].invert());
            myQ2Rotations.add(tmpRotations[1]);
          }
          tmpSimilar.set(i,j,tmpZero);
          tmpSimilar.set(j,i,tmpZero);
        }
      }
    }
    double tmpSingularValue;
    for (int ij=0; ij < tmpMinDim; ij++) {
      if (tmpSimilar.isZero(ij,ij)) {
        tmpSingularValue=PrimitiveMath.ZERO;
      }
 else       if (tmpSimilar.isAbsolute(ij,ij)) {
        tmpSingularValue=tmpSimilar.doubleValue(ij,ij);
      }
 else {
        final Scalar<N> tmpDiagSclr=tmpSimilar.toScalar(ij,ij);
        final N tmpSignum=tmpDiagSclr.signum().getNumber();
        tmpSingularValue=tmpDiagSclr.divide(tmpSignum).norm();
        tmpSimilar.set(ij,ij,tmpSingularValue);
        myQ2Rotations.add(this.makeRotation(ij,ij,tmpSignum,tmpSignum));
      }
      this.getSingularValues().set(ij,tmpSingularValue);
    }
    this.getSingularValues().sortDescending();
    myFutureQ1=DaemonPoolExecutor.INSTANCE.submit(new Callable<PhysicalStore<N>>(){
      public PhysicalStore<N> call() throws Exception {
        final PhysicalStore<N> retVal=SVDold30.this.getBidiagonalQ1();
        final List<Rotation<N>> tmpRotations=myQ1Rotations;
        final int tmpLimit=tmpRotations.size();
        for (int r=0; r < tmpLimit; r++) {
          retVal.transformRight(tmpRotations.get(r));
        }
        return retVal;
      }
    }
);
    myFutureQ2=DaemonPoolExecutor.INSTANCE.submit(new Callable<PhysicalStore<N>>(){
      public PhysicalStore<N> call() throws Exception {
        final PhysicalStore<N> retVal=SVDold30.this.getBidiagonalQ2();
        final List<Rotation<N>> tmpRotations=myQ2Rotations;
        final int tmpLimit=tmpRotations.size();
        for (int r=0; r < tmpLimit; r++) {
          retVal.transformRight(tmpRotations.get(r));
        }
        return retVal;
      }
    }
);
    return this.computed(true);
  }
  protected final DiagonalAccess<N> extractSimilar(  final PhysicalStore<N> aStore,  final boolean aNormalAspectRatio){
    final Array2D<N> tmpArray2D=((DecompositionStore<N>)aStore).asArray2D();
    final Array1D<N> tmpMain=tmpArray2D.sliceDiagonal(0,0);
    if (aNormalAspectRatio) {
      final Array1D<N> tmpSuper=tmpArray2D.sliceDiagonal(0,1);
      return new DiagonalAccess<N>(tmpMain,tmpSuper,null,this.getStaticZero());
    }
 else {
      final Array1D<N> tmpSub=tmpArray2D.sliceDiagonal(1,0);
      return new DiagonalAccess<N>(tmpMain,null,tmpSub,this.getStaticZero());
    }
  }
  @Override protected final MatrixStore<N> makeD(){
    return null;
  }
  @Override protected final MatrixStore<N> makeQ1(){
    try {
      return myFutureQ1.get();
    }
 catch (    final InterruptedException anException) {
      throw new ProgrammingError(anException.getMessage());
    }
catch (    final ExecutionException anException) {
      throw new ProgrammingError(anException.getMessage());
    }
  }
  @Override protected final MatrixStore<N> makeQ2(){
    try {
      return myFutureQ2.get();
    }
 catch (    final InterruptedException anException) {
      throw new ProgrammingError(anException.getMessage());
    }
catch (    final ExecutionException anException) {
      throw new ProgrammingError(anException.getMessage());
    }
  }
  @Override protected Array1D<Double> makeSingularValues(){
    return null;
  }
  protected abstract Rotation<N>[] rotations(  PhysicalStore<N> aStore,  int aLowInd,  int aHighInd,  Rotation<N>[] retVal);
}
