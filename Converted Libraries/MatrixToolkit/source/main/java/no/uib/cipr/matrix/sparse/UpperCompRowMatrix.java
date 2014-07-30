package no.uib.cipr.matrix.sparse;
import no.uib.cipr.matrix.AbstractMatrix;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Vector;
/** 
 * Upper triangular CRS matrix. Only used for triangular solves
 */
class UpperCompRowMatrix extends AbstractMatrix {
  private int[] rowptr;
  private int[] colind;
  private double[] data;
  private int[] diagind;
  public UpperCompRowMatrix(  CompRowMatrix LU,  int[] diagind){
    super(LU);
    rowptr=LU.getRowPointers();
    colind=LU.getColumnIndices();
    data=LU.getData();
    this.diagind=diagind;
  }
  @Override public Vector solve(  Vector b,  Vector x){
    if (!(b instanceof DenseVector) || !(x instanceof DenseVector))     return super.solve(b,x);
    double[] bd=((DenseVector)b).getData();
    double[] xd=((DenseVector)x).getData();
    for (int i=numRows - 1; i >= 0; --i) {
      double sum=0;
      for (int j=diagind[i] + 1; j < rowptr[i + 1]; ++j)       sum+=data[j] * xd[colind[j]];
      xd[i]=(bd[i] - sum) / data[diagind[i]];
    }
    return x;
  }
  @Override public Vector transSolve(  Vector b,  Vector x){
    if (!(x instanceof DenseVector))     return super.transSolve(b,x);
    x.set(b);
    double[] xd=((DenseVector)x).getData();
    for (int i=0; i < numRows; ++i) {
      xd[i]/=data[diagind[i]];
      for (int j=diagind[i] + 1; j < rowptr[i + 1]; ++j)       xd[colind[j]]-=data[j] * xd[i];
    }
    return x;
  }
}