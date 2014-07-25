package org.ojalgo.optimisation.system;
import org.ojalgo.access.Access2D;
import org.ojalgo.matrix.decomposition.DecompositionStore;
import org.ojalgo.matrix.decomposition.MatrixDecomposition;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.optimisation.Optimisation;
abstract class DecompositionSolver<D extends MatrixDecomposition<Double>> extends OptimisationSystem {
  private final D myDecomposition;
  @SuppressWarnings("unused") private DecompositionSolver(){
    this(null);
  }
  protected DecompositionSolver(  final D decomposition){
    super();
    myDecomposition=decomposition;
  }
  public boolean compute(  final Access2D<?> body){
    myDecomposition.compute(body);
    return myDecomposition.isSolvable();
  }
  public boolean isComputed(){
    return myDecomposition.isComputed();
  }
  public boolean isSolvable(){
    return myDecomposition.isSolvable();
  }
  public DecompositionStore<Double> preallocate(  final Access2D<Double> templateBody,  final Access2D<Double> templateRHS){
    return myDecomposition.preallocate(templateBody,templateRHS);
  }
  public Optimisation.Result solve(  final MatrixStore<Double> rhs,  final DecompositionStore<Double> preallocated){
    if (!myDecomposition.isComputed()) {
      throw new IllegalStateException("Need to call compute(body) first!");
    }
 else {
      if (myDecomposition.isSolvable()) {
        return new Optimisation.Result(Optimisation.State.FEASIBLE,myDecomposition.solve(rhs,preallocated));
      }
 else {
        return new Optimisation.Result(Optimisation.State.FAILED,rhs);
      }
    }
  }
  @Override public Optimisation.Result solve(  final MatrixStore<Double> body,  final MatrixStore<Double> rhs){
    return this.solve(body,rhs,this.preallocate(body,rhs));
  }
  public Optimisation.Result solve(  final MatrixStore<Double> body,  final MatrixStore<Double> rhs,  final DecompositionStore<Double> preallocated){
    this.compute(body);
    return this.solve(rhs,preallocated);
  }
  public boolean validate(  final MatrixStore<Double> body,  final MatrixStore<Double> rhs){
    return (body.countRows() == rhs.countRows()) && this.validate(body);
  }
  protected final D getDecomposition(){
    return myDecomposition;
  }
  /** 
 * Should test that the equation system body meets the requirements for the{@linkplain MatrixDecomposition#compute(Access2D)} method for the
 * specific matrix decomposition.
 */
  protected abstract boolean validate(  MatrixStore<Double> body);
}
