package org.ojalgo.random;
import java.util.Random;
import org.ojalgo.access.Access2D;
import org.ojalgo.array.Array1D;
import org.ojalgo.matrix.decomposition.Cholesky;
import org.ojalgo.matrix.decomposition.CholeskyDecomposition;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.matrix.store.PrimitiveDenseStore;
import org.ojalgo.type.Alternator;
public class Random1D {
  public final int length;
  private final Alternator<Random> myAlternator=RandomNumber.makeRandomAlternator();
  private final MatrixStore<Double> myCholeskiedCorrelations;
  public Random1D(  final Access2D<?> aCorrelationsMatrix){
    super();
    final Cholesky<Double> tmpCholesky=CholeskyDecomposition.makePrimitive();
    tmpCholesky.compute(aCorrelationsMatrix);
    myCholeskiedCorrelations=tmpCholesky.getL();
    tmpCholesky.reset();
    length=myCholeskiedCorrelations.getMinDim();
  }
  /** 
 * If the variables are uncorrelated.
 */
  public Random1D(  final int aLength){
    super();
    myCholeskiedCorrelations=null;
    length=aLength;
  }
  @SuppressWarnings("unused") private Random1D(){
    this(null);
  }
  /** 
 * An array of correlated random numbers, provided that you gave a
 * correlations matrix to the constructor.
 */
  public Array1D<Double> nextDouble(){
    final PrimitiveDenseStore tmpUncorrelated=PrimitiveDenseStore.FACTORY.makeZero(length,1);
    for (int i=0; i < length; i++) {
      tmpUncorrelated.set(i,0,this.random().nextDouble());
    }
    if (myCholeskiedCorrelations != null) {
      return ((PrimitiveDenseStore)tmpUncorrelated.multiplyLeft(myCholeskiedCorrelations)).asList();
    }
 else {
      return tmpUncorrelated.asList();
    }
  }
  /** 
 * An array of correlated random numbers, provided that you gave a
 * correlations matrix to the constructor.
 */
  public Array1D<Double> nextGaussian(){
    final PrimitiveDenseStore tmpUncorrelated=PrimitiveDenseStore.FACTORY.makeZero(length,1);
    for (int i=0; i < length; i++) {
      tmpUncorrelated.set(i,0,this.random().nextGaussian());
    }
    if (myCholeskiedCorrelations != null) {
      return ((PrimitiveDenseStore)tmpUncorrelated.multiplyLeft(myCholeskiedCorrelations)).asList();
    }
 else {
      return tmpUncorrelated.asList();
    }
  }
  public int size(){
    return length;
  }
  protected Random random(){
    return myAlternator.get();
  }
}
