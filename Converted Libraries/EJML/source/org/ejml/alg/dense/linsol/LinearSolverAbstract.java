package org.ejml.alg.dense.linsol;
import org.ejml.data.DenseMatrix64F;
import org.ejml.factory.LinearSolver;
/** 
 * <p>
 * An abstract class that provides some common functionality and a default implementation
 * of invert that uses the solve function of the child class.
 * </p>
 * <p>
 * The extending class must explicity call {@link #_setA(org.ejml.data.DenseMatrix64F)}inside of its {@link #setA} function.
 * </p>
 * @author Peter Abeles
 */
public abstract class LinearSolverAbstract implements LinearSolver<DenseMatrix64F> {
  protected DenseMatrix64F A;
  protected int numRows;
  protected int numCols;
  public DenseMatrix64F getA(){
    return A;
  }
  protected void _setA(  DenseMatrix64F A){
    this.A=A;
    this.numRows=A.numRows;
    this.numCols=A.numCols;
  }
  @Override public void invert(  DenseMatrix64F A_inv){
    InvertUsingSolve.invert(this,A,A_inv);
  }
}
