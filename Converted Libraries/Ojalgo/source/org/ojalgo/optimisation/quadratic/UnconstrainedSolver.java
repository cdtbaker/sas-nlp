package org.ojalgo.optimisation.quadratic;
import static org.ojalgo.constant.PrimitiveMath.*;
import org.ojalgo.matrix.decomposition.Cholesky;
import org.ojalgo.matrix.decomposition.CholeskyDecomposition;
import org.ojalgo.matrix.decomposition.DecompositionStore;
import org.ojalgo.matrix.decomposition.Eigenvalue;
import org.ojalgo.matrix.decomposition.EigenvalueDecomposition;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.optimisation.ExpressionsBasedModel;
import org.ojalgo.optimisation.Optimisation;
/** 
 * When the KKT matrix is nonsingular, there is a unique optimal primal-dual pair (x,v).
 * If the KKT matrix is singular, but the KKT system is solvable, any solution yields an optimal pair (x,v).
 * If the KKT system is not solvable, the quadratic optimization problem is unbounded below or infeasible.
 * @author apete
 */
final class UnconstrainedSolver extends QuadraticSolver {
  private final Cholesky<Double> myCholesky=CholeskyDecomposition.makePrimitive();
  private final Eigenvalue<Double> myEigenvalue=EigenvalueDecomposition.makePrimitive(true);
  UnconstrainedSolver(  final ExpressionsBasedModel aModel,  final Optimisation.Options solverOptions,  final QuadraticSolver.Builder matrices){
    super(aModel,solverOptions,matrices);
  }
  @Override protected boolean initialise(  final Result kickStart){
    myCholesky.reset();
    myEigenvalue.reset();
    return true;
  }
  @Override protected boolean needsAnotherIteration(){
    return this.countIterations() < 1;
  }
  @Override protected void performIteration(){
    final MatrixStore<Double> tmpBody=this.getQ();
    final MatrixStore<Double> tmpRhs=this.getC();
    final DecompositionStore<Double> tmpX=this.getX();
    if (!myCholesky.isComputed()) {
      myCholesky.compute(tmpBody,false);
    }
    if (myCholesky.isSolvable()) {
      myCholesky.solve(tmpRhs,tmpX);
      this.setState(State.DISTINCT);
    }
 else {
      if (!myEigenvalue.isComputed()) {
        myEigenvalue.compute(tmpBody);
      }
      if (myEigenvalue.isSolvable()) {
        myEigenvalue.solve(tmpRhs,tmpX);
        this.setState(State.OPTIMAL);
      }
 else {
        this.resetX();
        this.setState(State.UNBOUNDED);
      }
    }
  }
  @Override protected boolean validate(){
    boolean retVal=true;
    this.setState(State.VALID);
    final MatrixStore<Double> tmpQ=this.getQ();
    myCholesky.compute(tmpQ,true);
    if (!myCholesky.isSPD()) {
      myEigenvalue.compute(tmpQ,true);
      final MatrixStore<Double> tmpD=myEigenvalue.getD();
      for (int ij=0; retVal && (ij < tmpD.getMinDim()); ij++) {
        if (tmpD.doubleValue(ij,ij) < ZERO) {
          retVal=false;
          this.setState(State.INVALID);
        }
      }
    }
    return retVal;
  }
}
