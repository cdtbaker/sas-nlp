package org.ojalgo.function.polynomial;
import org.ojalgo.access.Access1D;
import org.ojalgo.array.Array1D;
import org.ojalgo.constant.PrimitiveMath;
import org.ojalgo.matrix.decomposition.QR;
import org.ojalgo.matrix.decomposition.QRDecomposition;
import org.ojalgo.matrix.store.PhysicalStore;
import org.ojalgo.matrix.store.PrimitiveDenseStore;
public class PrimitivePolynomial extends AbstractPolynomial<Double> {
  public PrimitivePolynomial(  final int aDegree){
    super(Array1D.PRIMITIVE.makeZero(aDegree + 1));
  }
  PrimitivePolynomial(  final Array1D<Double> someCoefficients){
    super(someCoefficients);
  }
  public void estimate(  final Access1D<?> x,  final Access1D<?> y){
    final int tmpRowDim=Math.min(x.size(),y.size());
    final int tmpColDim=this.size();
    final PhysicalStore<Double> tmpBody=PrimitiveDenseStore.FACTORY.makeZero(tmpRowDim,tmpColDim);
    final PhysicalStore<Double> tmpRHS=PrimitiveDenseStore.FACTORY.makeZero(tmpRowDim,1);
    for (int i=0; i < tmpRowDim; i++) {
      double tmpX=PrimitiveMath.ONE;
      final double tmpXfactor=x.doubleValue(i);
      final double tmpY=y.doubleValue(i);
      for (int j=0; j < tmpColDim; j++) {
        tmpBody.set(i,j,tmpX);
        tmpX*=tmpXfactor;
      }
      tmpRHS.set(i,0,tmpY);
    }
    final QR<Double> tmpQR=QRDecomposition.makePrimitive();
    tmpQR.compute(tmpBody);
    this.set(tmpQR.solve(tmpRHS));
  }
  public Double integrate(  final Double aFromPoint,  final Double aToPoint){
    final PolynomialFunction<Double> tmpPrim=this.buildPrimitive();
    final double tmpFromVal=tmpPrim.invoke(aFromPoint.doubleValue());
    final double tmpToVal=tmpPrim.invoke(aToPoint.doubleValue());
    return tmpToVal - tmpFromVal;
  }
  public Double invoke(  final Double arg){
    return this.invoke(arg.doubleValue());
  }
  public void set(  final Access1D<?> someCoefficient){
    final int tmpLimit=Math.min(this.size(),someCoefficient.size());
    for (int p=0; p < tmpLimit; p++) {
      this.set(p,someCoefficient.doubleValue(p));
    }
  }
  @Override protected Double getDerivativeFactor(  final int aPower){
    final int tmpNextIndex=aPower + 1;
    return tmpNextIndex * this.doubleValue(tmpNextIndex);
  }
  @Override protected Double getPrimitiveFactor(  final int aPower){
    if (aPower <= 0) {
      return PrimitiveMath.ZERO;
    }
 else {
      return this.doubleValue(aPower - 1) / aPower;
    }
  }
  @Override protected AbstractPolynomial<Double> makeInstance(  final int aSize){
    return new PrimitivePolynomial(Array1D.PRIMITIVE.makeZero(aSize));
  }
}
