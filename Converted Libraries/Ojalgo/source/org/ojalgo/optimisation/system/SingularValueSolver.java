package org.ojalgo.optimisation.system;
import org.ojalgo.matrix.decomposition.SingularValue;
import org.ojalgo.matrix.decomposition.SingularValueDecomposition;
import org.ojalgo.matrix.store.MatrixStore;
public final class SingularValueSolver extends DecompositionSolver<SingularValue<Double>> {
  public SingularValueSolver(){
    super(SingularValueDecomposition.makePrimitive());
  }
  private SingularValueSolver(  final SingularValue<Double> decomposition){
    super(decomposition);
  }
  @Override protected boolean validate(  final MatrixStore<Double> body){
    return true;
  }
}
