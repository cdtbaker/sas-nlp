package no.uib.cipr.matrix.sparse;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Matrix;
import no.uib.cipr.matrix.Vector;
/** 
 * SSOR preconditioner. Uses symmetrical sucessive overrelaxation as a
 * preconditioner. Meant for symmetrical, positive definite matrices. For best
 * performance, omega must be carefully chosen (between 0 and 2).
 */
public class SSOR implements Preconditioner {
  /** 
 * Overrelaxation parameter for the forward sweep
 */
  private double omegaF;
  /** 
 * Overrelaxation parameter for the backwards sweep
 */
  private double omegaR;
  /** 
 * Holds a copy of the matrix A in the compressed row format
 */
  private final CompRowMatrix F;
  /** 
 * Indices to the diagonal entries of the matrix
 */
  private final int[] diagind;
  /** 
 * Temporary vector for holding the half-step state
 */
  private final double[] xx;
  /** 
 * True if the reverse (backward) sweep is to be done. Without this, the
 * method is SOR instead of SSOR
 */
  private final boolean reverse;
  /** 
 * Constructor for SSOR
 * @param FMatrix to use internally. It will not be modified, thus the
 * system matrix may be passed
 * @param reverseTrue to perform a reverse sweep as well as the forward sweep.
 * If false, this preconditioner becomes the SOR method instead
 * @param omegaFOverrelaxation parameter for the forward sweep. Between 0 and
 * 2.
 * @param omegaROverrelaxation parameter for the backwards sweep. Between 0
 * and 2.
 */
  public SSOR(  CompRowMatrix F,  boolean reverse,  double omegaF,  double omegaR){
    if (!F.isSquare())     throw new IllegalArgumentException("SSOR only applies to square matrices");
    this.F=F;
    this.reverse=reverse;
    setOmega(omegaF,omegaR);
    int n=F.numRows();
    diagind=new int[n];
    xx=new double[n];
  }
  /** 
 * Constructor for SSOR. Uses <code>omega=1</code> with a backwards sweep
 * @param FMatrix to use internally. It will not be modified, thus the
 * system matrix may be passed
 */
  public SSOR(  CompRowMatrix F){
    this(F,true,1,1);
  }
  /** 
 * Sets the overrelaxation parameters
 * @param omegaFOverrelaxation parameter for the forward sweep. Between 0 and
 * 2.
 * @param omegaROverrelaxation parameter for the backwards sweep. Between 0
 * and 2.
 */
  public void setOmega(  double omegaF,  double omegaR){
    if (omegaF < 0 || omegaF > 2)     throw new IllegalArgumentException("omegaF must be between 0 and 2");
    if (omegaR < 0 || omegaR > 2)     throw new IllegalArgumentException("omegaR must be between 0 and 2");
    this.omegaF=omegaF;
    this.omegaR=omegaR;
  }
  public void setMatrix(  Matrix A){
    F.set(A);
    int n=F.numRows();
    int[] rowptr=F.getRowPointers();
    int[] colind=F.getColumnIndices();
    for (int k=0; k < n; ++k) {
      diagind[k]=Arrays.binarySearch(colind,k,rowptr[k],rowptr[k + 1]);
      if (diagind[k] < 0)       throw new RuntimeException("Missing diagonal on row " + (k + 1));
    }
  }
  public Vector apply(  Vector b,  Vector x){
    if (!(b instanceof DenseVector) || !(x instanceof DenseVector))     throw new IllegalArgumentException("Vectors must be a DenseVectors");
    int[] rowptr=F.getRowPointers();
    int[] colind=F.getColumnIndices();
    double[] data=F.getData();
    double[] bd=((DenseVector)b).getData();
    double[] xd=((DenseVector)x).getData();
    int n=F.numRows();
    System.arraycopy(xd,0,xx,0,n);
    for (int i=0; i < n; ++i) {
      double sigma=0;
      for (int j=rowptr[i]; j < diagind[i]; ++j)       sigma+=data[j] * xx[colind[j]];
      for (int j=diagind[i] + 1; j < rowptr[i + 1]; ++j)       sigma+=data[j] * xd[colind[j]];
      sigma=(bd[i] - sigma) / data[diagind[i]];
      xx[i]=xd[i] + omegaF * (sigma - xd[i]);
    }
    if (!reverse) {
      System.arraycopy(xx,0,xd,0,n);
      return x;
    }
    for (int i=n - 1; i >= 0; --i) {
      double sigma=0;
      for (int j=rowptr[i]; j < diagind[i]; ++j)       sigma+=data[j] * xx[colind[j]];
      for (int j=diagind[i] + 1; j < rowptr[i + 1]; ++j)       sigma+=data[j] * xd[colind[j]];
      sigma=(bd[i] - sigma) / data[diagind[i]];
      xd[i]=xx[i] + omegaR * (sigma - xx[i]);
    }
    return x;
  }
  public Vector transApply(  Vector b,  Vector x){
    return apply(b,x);
  }
}
