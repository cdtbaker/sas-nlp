package org.ejml.alg.dense.linsol.qr;
import org.ejml.alg.dense.decomposition.TriangularSolver;
import org.ejml.alg.dense.decomposition.qr.QRColPivDecompositionHouseholderColumn;
import org.ejml.alg.dense.decomposition.qr.QrHelperFunctions;
import org.ejml.data.DenseMatrix64F;
/** 
 * <p>
 * Performs a pseudo inverse solver using the {@link QRColPivDecompositionHouseholderColumn} decomposition
 * directly.  For details on how the pseudo inverse is computed see {@link BaseLinearSolverQrp}.
 * </p>
 * @author Peter Abeles
 */
public class LinearSolverQrpHouseCol extends BaseLinearSolverQrp {
  private QRColPivDecompositionHouseholderColumn decomposition;
  private DenseMatrix64F x_basic=new DenseMatrix64F(1,1);
  public LinearSolverQrpHouseCol(  QRColPivDecompositionHouseholderColumn decomposition,  boolean norm2Solution){
    super(decomposition,norm2Solution);
    this.decomposition=decomposition;
  }
  @Override public void solve(  DenseMatrix64F B,  DenseMatrix64F X){
    if (X.numRows != numCols)     throw new IllegalArgumentException("Unexpected dimensions for X");
 else     if (B.numRows != numRows || B.numCols != X.numCols)     throw new IllegalArgumentException("Unexpected dimensions for B");
    int BnumCols=B.numCols;
    int pivots[]=decomposition.getPivots();
    double qr[][]=decomposition.getQR();
    double gammas[]=decomposition.getGammas();
    for (int colB=0; colB < BnumCols; colB++) {
      x_basic.reshape(numRows,1);
      Y.reshape(numRows,1);
      for (int i=0; i < numRows; i++) {
        x_basic.data[i]=B.get(i,colB);
      }
      for (int i=0; i < rank; i++) {
        double u[]=qr[i];
        double vv=u[i];
        u[i]=1;
        QrHelperFunctions.rank1UpdateMultR(x_basic,u,gammas[i],0,i,numRows,Y.data);
        u[i]=vv;
      }
      TriangularSolver.solveU(R11.data,x_basic.data,rank);
      x_basic.reshape(numCols,1,true);
      for (int i=rank; i < numCols; i++)       x_basic.data[i]=0;
      if (norm2Solution && rank < numCols)       upgradeSolution(x_basic);
      for (int i=0; i < numCols; i++) {
        X.set(pivots[i],colB,x_basic.data[i]);
      }
    }
  }
  @Override public boolean modifiesA(){
    return decomposition.inputModified();
  }
  @Override public boolean modifiesB(){
    return false;
  }
}
