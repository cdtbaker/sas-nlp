package org.ojalgo.optimisation.system;
import org.ojalgo.matrix.decomposition.LU;
import org.ojalgo.matrix.decomposition.LUDecomposition;
import org.ojalgo.matrix.store.MatrixStore;
public final class LUSolver extends DecompositionSolver<LU<Double>> {
  public LUSolver(){
    super(LUDecomposition.makePrimitive());
  }
  private LUSolver(  final LU<Double> decomposition){
    super(decomposition);
  }
  @Override protected boolean validate(  final MatrixStore<Double> body){
    return true;
  }
}
