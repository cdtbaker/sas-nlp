package org.ejml.alg.dense.linsol.qr;
import org.ejml.alg.dense.decomposition.qr.QRDecompositionHouseholderColumn;
import org.ejml.alg.dense.decomposition.qr.QrUpdate;
import org.ejml.alg.dense.linsol.AdjustableLinearSolver;
import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.CommonOps;
/** 
 * A solver for QR decomposition that can efficiently modify the previous decomposition when
 * data is added or removed.
 * @author Peter Abeles
 */
public class AdjLinearSolverQr extends LinearSolverQr implements AdjustableLinearSolver {
  private QrUpdate update;
  private DenseMatrix64F A;
  public AdjLinearSolverQr(){
    super(new QRDecompositionHouseholderColumn());
  }
  @Override public void setMaxSize(  int maxRows,  int maxCols){
    maxRows+=5;
    super.setMaxSize(maxRows,maxCols);
    update=new QrUpdate(maxRows,maxCols,true);
    A=new DenseMatrix64F(maxRows,maxCols);
  }
  /** 
 * Compute the A matrix from the Q and R matrices.
 * @return The A matrix.
 */
  @Override public DenseMatrix64F getA(){
    if (A.data.length < numRows * numCols) {
      A=new DenseMatrix64F(numRows,numCols);
    }
    A.reshape(numRows,numCols,false);
    CommonOps.mult(Q,R,A);
    return A;
  }
  @Override public boolean addRowToA(  double[] A_row,  int rowIndex){
    if (numRows + 1 > maxRows) {
      int grow=maxRows / 10;
      if (grow < 1)       grow=1;
      maxRows=numRows + grow;
      Q.reshape(maxRows,maxRows,true);
      R.reshape(maxRows,maxCols,true);
    }
    update.addRow(Q,R,A_row,rowIndex,true);
    numRows++;
    return true;
  }
  @Override public boolean removeRowFromA(  int index){
    update.deleteRow(Q,R,index,true);
    numRows--;
    return true;
  }
}
