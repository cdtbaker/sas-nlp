package org.ojalgo.optimisation.system;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.matrix.store.PhysicalStore;
import org.ojalgo.matrix.store.PrimitiveDenseStore;
import org.ojalgo.optimisation.Optimisation;
public final class ConjugateGradientSolver extends OptimisationSystem {
  public ConjugateGradientSolver(){
    super();
  }
  @Override public Optimisation.Result solve(  final MatrixStore<Double> body,  final MatrixStore<Double> rhs){
    final PhysicalStore<Double> retVal=PrimitiveDenseStore.FACTORY.makeZero(rhs.countRows(),rhs.countColumns());
    final PhysicalStore<Double> tmpResidual=PrimitiveDenseStore.FACTORY.copy(rhs);
    final MatrixStore<Double> tmpTranspRes=tmpResidual.builder().transpose().build();
    final PhysicalStore<Double> tmpDirection=PrimitiveDenseStore.FACTORY.copy(tmpResidual);
    final MatrixStore<Double> tmpTranspDir=tmpDirection.builder().transpose().build();
    double tmpAlpha;
    final double tmpBeta;
    tmpAlpha=tmpResidual.multiplyLeft(tmpTranspRes).doubleValue(0L) / body.multiplyLeft(tmpTranspDir).multiplyRight(tmpDirection).doubleValue(0L);
    retVal.maxpy(tmpAlpha,tmpDirection);
    tmpResidual.maxpy(-tmpAlpha,body.multiplyRight(tmpDirection));
    return null;
  }
}
