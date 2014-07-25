package org.ojalgo.random;
import static org.ojalgo.constant.PrimitiveMath.*;
import org.ojalgo.access.Access1D;
import org.ojalgo.access.Access2D;
import org.ojalgo.array.Array1D;
import org.ojalgo.array.Array2D;
public final class Normal1D extends RandomNumber1D {
  static Access2D<?> correlations(  final Access2D<?> covariances){
    final int tmpDim=covariances.getRowDim();
    final Array2D<Double> retVal=Array2D.PRIMITIVE.makeZero(tmpDim,tmpDim);
    final Array1D<Double> tmpStdDev=Array1D.PRIMITIVE.makeZero(tmpDim);
    for (int ij=0; ij < tmpDim; ij++) {
      tmpStdDev.set(ij,Math.sqrt(covariances.doubleValue(ij,ij)));
    }
    double tmpCorrelation;
    for (int j=0; j < tmpDim; j++) {
      retVal.set(j,j,ONE);
      for (int i=j + 1; i < tmpDim; i++) {
        tmpCorrelation=covariances.doubleValue(i,j) / (tmpStdDev.doubleValue(i) * tmpStdDev.doubleValue(j));
        retVal.set(i,j,tmpCorrelation);
        retVal.set(j,i,tmpCorrelation);
      }
    }
    return retVal;
  }
  private final Array1D<Double> myLocations;
  private final Array1D<Double> myScales;
  public Normal1D(  final Access1D<?> locations,  final Access2D<?> covariances){
    super(Normal1D.correlations(covariances));
    final int tmpDim=covariances.getRowDim();
    myLocations=Array1D.PRIMITIVE.copy(locations);
    myScales=Array1D.PRIMITIVE.makeZero(tmpDim);
    for (int ij=0; ij < tmpDim; ij++) {
      myScales.set(ij,Math.sqrt(covariances.doubleValue(ij,ij)));
    }
  }
  private Normal1D(  final Access2D<?> correlations){
    super(correlations);
    myLocations=null;
    myScales=null;
  }
  public Array1D<Double> doubleValue(){
    final Array1D<Double> retVal=this.random().nextGaussian();
    for (int i=0; i < retVal.length; i++) {
      retVal.set(i,myLocations.doubleValue(i) + (myScales.doubleValue(i) * retVal.doubleValue(i)));
    }
    return retVal;
  }
  @Override public Array1D<Double> getExpected(){
    return myLocations;
  }
  @Override public Array1D<Double> getStandardDeviation(){
    return myScales;
  }
}
