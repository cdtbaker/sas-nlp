package no.uib.cipr.matrix.sparse;
import java.util.Arrays;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Matrix;
import no.uib.cipr.matrix.Vector;
/** 
 * Incomplete Cholesky preconditioner without fill-in using a compressed row
 * matrix as internal storage
 */
public class ICC implements Preconditioner {
  /** 
 * Factorisation matrix
 */
  private final CompRowMatrix R;
  /** 
 * Triangular view onto R for solution purposes
 */
  private Matrix Rt;
  /** 
 * Temporary vector for solving the factorised system
 */
  private final Vector y;
  /** 
 * Sets up the ICC preconditioner
 * @param RMatrix to use internally. For best performance, its non-zero
 * pattern must conform to that of the system matrix
 */
  public ICC(  CompRowMatrix R){
    if (!R.isSquare())     throw new IllegalArgumentException("ICC only applies to square matrices");
    this.R=R;
    int n=R.numRows();
    y=new DenseVector(n);
  }
  public Vector apply(  Vector b,  Vector x){
    Rt.transSolve(b,y);
    return Rt.solve(y,x);
  }
  public Vector transApply(  Vector b,  Vector x){
    return apply(b,x);
  }
  public void setMatrix(  Matrix A){
    R.set(A);
    factor();
  }
  private void factor(){
    int n=R.numRows();
    int[] colind=R.getColumnIndices();
    int[] rowptr=R.getRowPointers();
    double[] data=R.getData();
    double[] Rk=new double[n];
    int[] diagind=findDiagonalIndices(n,colind,rowptr);
    for (int k=0; k < n; ++k) {
      Arrays.fill(Rk,0);
      for (int i=rowptr[k]; i < rowptr[k + 1]; ++i)       Rk[colind[i]]=data[i];
      for (int i=0; i < k; ++i) {
        double Rii=data[diagind[i]];
        if (Rii == 0)         throw new RuntimeException("Zero pivot encountered on row " + (i + 1) + " during ICC process");
        double Rki=Rk[i] / Rii;
        if (Rki == 0)         continue;
        for (int j=diagind[i] + 1; j < rowptr[i + 1]; ++j)         Rk[colind[j]]-=Rki * data[j];
      }
      if (Rk[k] == 0)       throw new RuntimeException("Zero diagonal entry encountered on row " + (k + 1) + " during ICC process");
      double sqRkk=Math.sqrt(Rk[k]);
      for (int i=diagind[k]; i < rowptr[k + 1]; ++i)       data[i]=Rk[colind[i]] / sqRkk;
    }
    Rt=new UpperCompRowMatrix(R,diagind);
  }
  private int[] findDiagonalIndices(  int m,  int[] colind,  int[] rowptr){
    int[] diagind=new int[m];
    for (int k=0; k < m; ++k) {
      diagind[k]=no.uib.cipr.matrix.sparse.Arrays.binarySearch(colind,k,rowptr[k],rowptr[k + 1]);
      if (diagind[k] < 0)       throw new RuntimeException("Missing diagonal entry on row " + (k + 1));
    }
    return diagind;
  }
}
