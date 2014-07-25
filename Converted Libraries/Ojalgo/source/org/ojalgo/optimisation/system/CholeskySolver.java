package org.ojalgo.optimisation.system;
import org.ojalgo.access.Access2D;
import org.ojalgo.matrix.MatrixUtils;
import org.ojalgo.matrix.decomposition.Cholesky;
import org.ojalgo.matrix.decomposition.CholeskyDecomposition;
import org.ojalgo.matrix.store.MatrixStore;
public final class CholeskySolver extends DecompositionSolver<Cholesky<Double>> {
  public CholeskySolver(){
    super(CholeskyDecomposition.makePrimitive());
  }
  private CholeskySolver(  final Cholesky<Double> decomposition){
    super(decomposition);
  }
  @Override protected boolean validate(  final MatrixStore<Double> body){
    boolean retVal=body.countRows() == body.countColumns();
    retVal=retVal && MatrixUtils.isHermitian((Access2D<?>)body);
    retVal=retVal && this.getDecomposition().compute(body,true);
    return retVal;
  }
}
