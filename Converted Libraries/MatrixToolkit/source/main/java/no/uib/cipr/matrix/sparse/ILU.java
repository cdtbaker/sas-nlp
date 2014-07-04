package no.uib.cipr.matrix.sparse;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Matrix;
import no.uib.cipr.matrix.Vector;
/** 
 * ILU(0) preconditioner using a compressed row matrix as internal storage
 */
public class ILU implements Preconditioner {
  /** 
 * Factorisation matrix
 */
  private final CompRowMatrix LU;
  /** 
 * The L and U factors
 */
  private Matrix L, U;
  /** 
 * Temporary vector for solving the factorised system
 */
  private final Vector y;
  /** 
 * Sets up the ILU preconditioner
 * @param LUMatrix to use internally. For best performance, its non-zero
 * pattern must conform to that of the system matrix
 */
  public ILU(  CompRowMatrix LU){
    if (!LU.isSquare())     throw new IllegalArgumentException("ILU only applies to square matrices");
    this.LU=LU;
    int n=LU.numRows();
    y=new DenseVector(n);
  }
  public Vector apply(  Vector b,  Vector x){
    L.solve(b,y);
    return U.solve(y,x);
  }
  public Vector transApply(  Vector b,  Vector x){
    U.transSolve(b,y);
    return L.transSolve(y,x);
  }
  public void setMatrix(  Matrix A){
    LU.set(A);
    factor();
  }
  private void factor(){
    int n=LU.numRows();
    int[] colind=LU.getColumnIndices();
    int[] rowptr=LU.getRowPointers();
    double[] data=LU.getData();
    int[] diagind=findDiagonalIndices(n,colind,rowptr);
    for (int k=1; k < n; ++k)     for (int i=rowptr[k]; i < diagind[k]; ++i) {
      int index=colind[i];
      double LUii=data[diagind[index]];
      if (LUii == 0)       throw new RuntimeException("Zero pivot encountered on row " + (i + 1) + " during ILU process");
      double LUki=(data[i]/=LUii);
      for (int j=diagind[index] + 1, l=rowptr[k] + 1; j < rowptr[index + 1]; ++j) {
        while (l < rowptr[k + 1] && colind[l] < colind[j])         l++;
        if (colind[l] == colind[j])         data[l]-=LUki * data[j];
      }
    }
    L=new UnitLowerCompRowMatrix(LU,diagind);
    U=new UpperCompRowMatrix(LU,diagind);
  }
  private int[] findDiagonalIndices(  int m,  int[] colind,  int[] rowptr){
    int[] diagind=new int[m];
    for (int k=0; k < m; ++k) {
      diagind[k]=Arrays.binarySearch(colind,k,rowptr[k],rowptr[k + 1]);
      if (diagind[k] < 0)       throw new RuntimeException("Missing diagonal entry on row " + (k + 1));
    }
    return diagind;
  }
}
