package no.uib.cipr.matrix.sparse;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Matrix;
import no.uib.cipr.matrix.Vector;
/** 
 * Diagonal preconditioner. Uses the inverse of the diagonal as preconditioner
 */
public class DiagonalPreconditioner implements Preconditioner {
  /** 
 * This contains the inverse of the diagonal
 */
  private double[] invdiag;
  /** 
 * Constructor for DiagonalPreconditioner
 * @param nProblem size (number of rows)
 */
  public DiagonalPreconditioner(  int n){
    invdiag=new double[n];
  }
  public Vector apply(  Vector b,  Vector x){
    if (!(x instanceof DenseVector) || !(b instanceof DenseVector))     throw new IllegalArgumentException("Vector must be DenseVectors");
    double[] xd=((DenseVector)x).getData();
    double[] bd=((DenseVector)b).getData();
    for (int i=0; i < invdiag.length; ++i)     xd[i]=bd[i] * invdiag[i];
    return x;
  }
  public Vector transApply(  Vector b,  Vector x){
    return apply(b,x);
  }
  public void setMatrix(  Matrix A){
    if (A.numRows() != invdiag.length)     throw new IllegalArgumentException("Matrix size differs from preconditioner size");
    for (int i=0; i < invdiag.length; ++i) {
      invdiag[i]=A.get(i,i);
      if (invdiag[i] == 0)       throw new RuntimeException("Zero diagonal on row " + (i + 1));
 else       invdiag[i]=1 / invdiag[i];
    }
  }
}
