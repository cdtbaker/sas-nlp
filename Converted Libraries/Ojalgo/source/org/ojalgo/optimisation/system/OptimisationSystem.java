package org.ojalgo.optimisation.system;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.optimisation.Optimisation;
public abstract class OptimisationSystem {
  protected OptimisationSystem(){
    super();
  }
  public abstract Optimisation.Result solve(  MatrixStore<Double> body,  MatrixStore<Double> rhs);
}
