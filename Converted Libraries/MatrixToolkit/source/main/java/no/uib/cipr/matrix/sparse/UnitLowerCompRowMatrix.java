package no.uib.cipr.matrix.sparse;
import no.uib.cipr.matrix.AbstractMatrix;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Vector;
/** 
 * Unit lower triangular CRS matrix. Only used for triangular solves
 */
class UnitLowerCompRowMatrix extends AbstractMatrix {
  private int[] rowptr;
  private int[] colind;
  private double[] data;
  private int[] diagind;
  public UnitLowerCompRowMatrix(  CompRowMatrix LU,  int[] diagind){
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
    for (int i=0; i < numRows; ++i) {
      double sum=0;
      for (int j=rowptr[i]; j < diagind[i]; ++j)       sum+=data[j] * xd[colind[j]];
      xd[i]=bd[i] - sum;
    }
    return x;
  }
  @Override public Vector transSolve(  Vector b,  Vector x){
    if (!(x instanceof DenseVector))     return super.transSolve(b,x);
    x.set(b);
    double[] xd=((DenseVector)x).getData();
    for (int i=numRows - 1; i >= 0; --i)     for (int j=rowptr[i]; j < diagind[i]; ++j)     xd[colind[j]]-=data[j] * xd[i];
    return x;
  }
}
