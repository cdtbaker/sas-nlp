package cern.colt.matrix.bench;
import cern.colt.matrix.DoubleMatrix2D;
abstract class Double2DProcedure implements TimerProcedure {
  public DoubleMatrix2D A;
  public DoubleMatrix2D B;
  public DoubleMatrix2D C;
  public DoubleMatrix2D D;
  /** 
 * The number of operations a single call to "apply" involves.
 */
  public double operations(){
    return A.rows() * A.columns() / 1.0E6;
  }
  /** 
 * Sets the matrices to operate upon.
 */
  public void setParameters(  DoubleMatrix2D A,  DoubleMatrix2D B){
    this.A=A;
    this.B=B;
    this.C=A.copy();
    this.D=B.copy();
  }
}
