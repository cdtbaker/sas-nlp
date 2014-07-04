package org.ojalgo.function.polynomial;
import org.ojalgo.access.Access1D;
import org.ojalgo.array.Array1D;
import org.ojalgo.matrix.decomposition.QR;
import org.ojalgo.matrix.decomposition.QRDecomposition;
import org.ojalgo.matrix.store.ComplexDenseStore;
import org.ojalgo.matrix.store.PhysicalStore;
import org.ojalgo.scalar.ComplexNumber;
import org.ojalgo.type.TypeUtils;
public class ComplexPolynomial extends AbstractPolynomial<ComplexNumber> {
  public ComplexPolynomial(  final int aDegree){
    super(Array1D.COMPLEX.makeZero(aDegree + 1));
  }
  ComplexPolynomial(  final Array1D<ComplexNumber> someCoefficients){
    super(someCoefficients);
  }
  public void estimate(  final Access1D<?> x,  final Access1D<?> y){
    final int tmpRowDim=Math.min(x.size(),y.size());
    final int tmpColDim=this.size();
    final PhysicalStore<ComplexNumber> tmpBody=ComplexDenseStore.FACTORY.makeZero(tmpRowDim,tmpColDim);
    final PhysicalStore<ComplexNumber> tmpRHS=ComplexDenseStore.FACTORY.makeZero(tmpRowDim,1);
    for (int i=0; i < tmpRowDim; i++) {
      ComplexNumber tmpX=ComplexNumber.ONE;
      final ComplexNumber tmpXfactor=TypeUtils.toComplexNumber(x.get(i));
      final ComplexNumber tmpY=TypeUtils.toComplexNumber(y.get(i));
      for (int j=0; j < tmpColDim; j++) {
        tmpBody.set(i,j,tmpX);
        tmpX=tmpX.multiply(tmpXfactor);
      }
      tmpRHS.set(i,0,tmpY);
    }
    final QR<ComplexNumber> tmpQR=QRDecomposition.makeComplex();
    tmpQR.compute(tmpBody);
    this.set(tmpQR.solve(tmpRHS));
  }
  public ComplexNumber integrate(  final ComplexNumber aFromPoint,  final ComplexNumber aToPoint){
    final PolynomialFunction<ComplexNumber> tmpPrim=this.buildPrimitive();
    final ComplexNumber tmpFromVal=tmpPrim.invoke(aFromPoint);
    final ComplexNumber tmpToVal=tmpPrim.invoke(aToPoint);
    return tmpToVal.subtract(tmpFromVal);
  }
  public ComplexNumber invoke(  final ComplexNumber arg){
    int tmpPower=this.degree();
    ComplexNumber retVal=this.get(tmpPower);
    while (--tmpPower >= 0) {
      retVal=this.get(tmpPower).add(arg.multiply(retVal));
    }
    return retVal;
  }
  public void set(  final Access1D<?> someCoefficient){
    final int tmpLimit=Math.min(this.size(),someCoefficient.size());
    for (int p=0; p < tmpLimit; p++) {
      this.set(p,TypeUtils.toComplexNumber(someCoefficient.get(p)));
    }
  }
  @Override protected ComplexNumber getDerivativeFactor(  final int aPower){
    final int tmpNextIndex=aPower + 1;
    return this.get(tmpNextIndex).multiply(tmpNextIndex);
  }
  @Override protected ComplexNumber getPrimitiveFactor(  final int aPower){
    if (aPower <= 0) {
      return ComplexNumber.ZERO;
    }
 else {
      return this.get(aPower - 1).divide(aPower);
    }
  }
  @Override protected AbstractPolynomial<ComplexNumber> makeInstance(  final int aSize){
    return new ComplexPolynomial(Array1D.COMPLEX.makeZero(aSize));
  }
}
