package org.ejml.alg.dense.linsol;
import org.ejml.data.ReshapeMatrix64F;
import org.ejml.factory.LinearSolver;
/** 
 * Ensures that any linear solver it is wrapped around will never modify
 * the input matrices.
 * @author Peter Abeles
 */
@SuppressWarnings({"unchecked"}) public class LinearSolverSafe<T extends ReshapeMatrix64F> implements LinearSolver<T> {
  private LinearSolver<T> alg;
  private T A;
  private T B;
  /** 
 * @param alg The solver it is wrapped around.
 */
  public LinearSolverSafe(  LinearSolver<T> alg){
    this.alg=alg;
  }
  @Override public boolean setA(  T A){
    if (alg.modifiesA()) {
      if (this.A == null) {
        this.A=(T)A.copy();
      }
 else {
        if (this.A.numRows != A.numRows || this.A.numCols != A.numCols) {
          this.A.reshape(A.numRows,A.numCols,false);
        }
        this.A.set(A);
      }
      return alg.setA(this.A);
    }
    return alg.setA(A);
  }
  @Override public double quality(){
    return alg.quality();
  }
  @Override public void solve(  T B,  T X){
    if (alg.modifiesB()) {
      if (this.B == null) {
        this.B=(T)B.copy();
      }
 else {
        if (this.B.numRows != B.numRows || this.B.numCols != B.numCols) {
          this.B.reshape(A.numRows,B.numCols,false);
        }
        this.B.set(B);
      }
      B=this.B;
    }
    alg.solve(B,X);
  }
  @Override public void invert(  T A_inv){
    alg.invert(A_inv);
  }
  @Override public boolean modifiesA(){
    return false;
  }
  @Override public boolean modifiesB(){
    return false;
  }
}
