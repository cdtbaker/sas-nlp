package org.ojalgo.function.polynomial;
import java.math.BigDecimal;
import org.ojalgo.access.Access1D;
import org.ojalgo.array.Array1D;
import org.ojalgo.constant.BigMath;
import org.ojalgo.matrix.decomposition.QR;
import org.ojalgo.matrix.decomposition.QRDecomposition;
import org.ojalgo.matrix.store.BigDenseStore;
import org.ojalgo.matrix.store.PhysicalStore;
import org.ojalgo.scalar.RationalNumber;
import org.ojalgo.type.TypeUtils;
public class RationalPolynomial extends AbstractPolynomial<RationalNumber> {
  public RationalPolynomial(  final int aDegree){
    super(Array1D.RATIONAL.makeZero(aDegree + 1));
  }
  RationalPolynomial(  final Array1D<RationalNumber> someCoefficients){
    super(someCoefficients);
  }
  public void estimate(  final Access1D<?> x,  final Access1D<?> y){
    final int tmpRowDim=Math.min(x.size(),y.size());
    final int tmpColDim=this.size();
    final PhysicalStore<BigDecimal> tmpBody=BigDenseStore.FACTORY.makeZero(tmpRowDim,tmpColDim);
    final PhysicalStore<BigDecimal> tmpRHS=BigDenseStore.FACTORY.makeZero(tmpRowDim,1);
    for (int i=0; i < tmpRowDim; i++) {
      BigDecimal tmpX=BigMath.ONE;
      final BigDecimal tmpXfactor=TypeUtils.toBigDecimal(x.get(i));
      final BigDecimal tmpY=TypeUtils.toBigDecimal(y.get(i));
      for (int j=0; j < tmpColDim; j++) {
        tmpBody.set(i,j,tmpX);
        tmpX=tmpX.multiply(tmpXfactor);
      }
      tmpRHS.set(i,0,tmpY);
    }
    final QR<BigDecimal> tmpQR=QRDecomposition.makeBig();
    tmpQR.compute(tmpBody);
    this.set(tmpQR.solve(tmpRHS));
  }
  public RationalNumber integrate(  final RationalNumber aFromPoint,  final RationalNumber aToPoint){
    final PolynomialFunction<RationalNumber> tmpPrim=this.buildPrimitive();
    final RationalNumber tmpFromVal=tmpPrim.invoke(aFromPoint);
    final RationalNumber tmpToVal=tmpPrim.invoke(aToPoint);
    return tmpToVal.subtract(tmpFromVal);
  }
  public RationalNumber invoke(  final RationalNumber arg){
    int tmpPower=this.degree();
    RationalNumber retVal=this.get(tmpPower);
    while (--tmpPower >= 0) {
      retVal=this.get(tmpPower).add(arg.multiply(retVal));
    }
    return retVal;
  }
  public void set(  final Access1D<?> someCoefficient){
    final int tmpLimit=Math.min(this.size(),someCoefficient.size());
    for (int p=0; p < tmpLimit; p++) {
      this.set(p,TypeUtils.toRationalNumber(someCoefficient.get(p)));
    }
  }
  @Override protected RationalNumber getDerivativeFactor(  final int aPower){
    final int tmpNextIndex=aPower + 1;
    return this.get(tmpNextIndex).multiply(tmpNextIndex);
  }
  @Override protected RationalNumber getPrimitiveFactor(  final int aPower){
    if (aPower <= 0) {
      return RationalNumber.ZERO;
    }
 else {
      return this.get(aPower - 1).divide(aPower);
    }
  }
  @Override protected AbstractPolynomial<RationalNumber> makeInstance(  final int aSize){
    return new RationalPolynomial(Array1D.RATIONAL.makeZero(aSize));
  }
}
