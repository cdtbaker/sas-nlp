package org.ojalgo.optimisation.system;
import org.ojalgo.access.Access2D;
import org.ojalgo.matrix.MatrixUtils;
import org.ojalgo.matrix.decomposition.Eigenvalue;
import org.ojalgo.matrix.decomposition.EigenvalueDecomposition;
import org.ojalgo.matrix.store.MatrixStore;
public final class EigenvalueSolver extends DecompositionSolver<Eigenvalue<Double>> {
  public EigenvalueSolver(){
    super(EigenvalueDecomposition.makePrimitive(true));
  }
  private EigenvalueSolver(  final Eigenvalue<Double> decomposition){
    super(decomposition);
  }
  @Override protected boolean validate(  final MatrixStore<Double> body){
    boolean retVal=body.countRows() == body.countColumns();
    retVal=retVal && MatrixUtils.isHermitian((Access2D<?>)body);
    return retVal;
  }
}
