package org.ejml.alg.dense.linsol.lu;
import org.ejml.alg.dense.decomposition.lu.LUDecompositionBase;
import org.ejml.data.DenseMatrix64F;
/** 
 * For each column in the B matrix it makes a copy, which is then solved for and
 * writen into X.  By making a copy of the column cpu cache issues are reduced.
 * @author Peter Abeles
 */
public class LinearSolverLu extends LinearSolverLuBase {
  boolean doImprove=false;
  public LinearSolverLu(  LUDecompositionBase decomp){
    super(decomp);
  }
  public LinearSolverLu(  LUDecompositionBase decomp,  boolean doImprove){
    super(decomp);
    this.doImprove=doImprove;
  }
  @Override public void solve(  DenseMatrix64F b,  DenseMatrix64F x){
    if (b.numCols != x.numCols && b.numRows != numCols && x.numRows != numCols) {
      throw new IllegalArgumentException("Unexpected matrix size");
    }
    int numCols=b.numCols;
    double dataB[]=b.data;
    double dataX[]=x.data;
    double[] vv=decomp._getVV();
    for (int j=0; j < numCols; j++) {
      int index=j;
      for (int i=0; i < this.numCols; i++, index+=numCols)       vv[i]=dataB[index];
      decomp._solveVectorInternal(vv);
      index=j;
      for (int i=0; i < this.numCols; i++, index+=numCols)       dataX[index]=vv[i];
    }
    if (doImprove) {
      improveSol(b,x);
    }
  }
}
