package org.ejml.alg.dense.decomposition.bidiagonal;
import org.ejml.alg.dense.decomposition.qr.QrHelperFunctions;
import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.CommonOps;
/** 
 * <p>
 * Performs a {@link org.ejml.alg.dense.decomposition.bidiagonal.BidiagonalDecomposition} using
 * householder reflectors.  This is efficient on wide or square matrices.
 * </p>
 * @author Peter Abeles
 */
public class BidiagonalDecompositionRow implements BidiagonalDecomposition<DenseMatrix64F> {
  private DenseMatrix64F UBV;
  private int m;
  private int n;
  private int min;
  private double gammasU[];
  private double gammasV[];
  private double b[];
  private double u[];
  /** 
 * Creates a decompose that defines the specified amount of memory.
 * @param numElements number of elements in the matrix.
 */
  public BidiagonalDecompositionRow(  int numElements){
    UBV=new DenseMatrix64F(numElements);
    gammasU=new double[numElements];
    gammasV=new double[numElements];
    b=new double[numElements];
    u=new double[numElements];
  }
  public BidiagonalDecompositionRow(){
    this(1);
  }
  /** 
 * Computes the decomposition of the provided matrix.  If no errors are detected then true is returned,
 * false otherwise.
 * @param A  The matrix that is being decomposed.  Not modified.
 * @return If it detects any errors or not.
 */
  @Override public boolean decompose(  DenseMatrix64F A){
    init(A);
    return _decompose();
  }
  /** 
 * Sets up internal data structures and creates a copy of the input matrix.
 * @param A The input matrix.  Not modified.
 */
  protected void init(  DenseMatrix64F A){
    UBV=A;
    m=UBV.numRows;
    n=UBV.numCols;
    min=Math.min(m,n);
    int max=Math.max(m,n);
    if (b.length < max + 1) {
      b=new double[max + 1];
      u=new double[max + 1];
    }
    if (gammasU.length < m) {
      gammasU=new double[m];
    }
    if (gammasV.length < n) {
      gammasV=new double[n];
    }
  }
  /** 
 * The raw UBV matrix that is stored internally.
 * @return UBV matrix.
 */
  public DenseMatrix64F getUBV(){
    return UBV;
  }
  @Override public void getDiagonal(  double[] diag,  double[] off){
    diag[0]=UBV.get(0);
    for (int i=1; i < n; i++) {
      diag[i]=UBV.unsafe_get(i,i);
      off[i - 1]=UBV.unsafe_get(i - 1,i);
    }
  }
  /** 
 * Returns the bidiagonal matrix.
 * @param B If not null the results are stored here, if null a new matrix is created.
 * @return The bidiagonal matrix.
 */
  @Override public DenseMatrix64F getB(  DenseMatrix64F B,  boolean compact){
    B=handleB(B,compact,m,n,min);
    B.set(0,0,UBV.get(0,0));
    for (int i=1; i < min; i++) {
      B.set(i,i,UBV.get(i,i));
      B.set(i - 1,i,UBV.get(i - 1,i));
    }
    if (n > m)     B.set(min - 1,min,UBV.get(min - 1,min));
    return B;
  }
  public static DenseMatrix64F handleB(  DenseMatrix64F B,  boolean compact,  int m,  int n,  int min){
    int w=n > m ? min + 1 : min;
    if (compact) {
      if (B == null) {
        B=new DenseMatrix64F(min,w);
      }
 else {
        B.reshape(min,w,false);
        B.zero();
      }
    }
 else {
      if (B == null) {
        B=new DenseMatrix64F(m,n);
      }
 else {
        B.reshape(m,n,false);
        B.zero();
      }
    }
    return B;
  }
  /** 
 * Returns the orthogonal U matrix.
 * @param U If not null then the results will be stored here.  Otherwise a new matrix will be created.
 * @return The extracted Q matrix.
 */
  @Override public DenseMatrix64F getU(  DenseMatrix64F U,  boolean transpose,  boolean compact){
    U=handleU(U,transpose,compact,m,n,min);
    CommonOps.setIdentity(U);
    for (int i=0; i < m; i++)     u[i]=0;
    for (int j=min - 1; j >= 0; j--) {
      u[j]=1;
      for (int i=j + 1; i < m; i++) {
        u[i]=UBV.get(i,j);
      }
      if (transpose)       QrHelperFunctions.rank1UpdateMultL(U,u,gammasU[j],j,j,m);
 else       QrHelperFunctions.rank1UpdateMultR(U,u,gammasU[j],j,j,m,this.b);
    }
    return U;
  }
  public static DenseMatrix64F handleU(  DenseMatrix64F U,  boolean transpose,  boolean compact,  int m,  int n,  int min){
    if (compact) {
      if (transpose) {
        if (U == null)         U=new DenseMatrix64F(min,m);
 else {
          U.reshape(min,m,false);
        }
      }
 else {
        if (U == null)         U=new DenseMatrix64F(m,min);
 else         U.reshape(m,min,false);
      }
    }
 else {
      if (U == null)       U=new DenseMatrix64F(m,m);
 else       U.reshape(m,m,false);
    }
    return U;
  }
  /** 
 * Returns the orthogonal V matrix.
 * @param V If not null then the results will be stored here.  Otherwise a new matrix will be created.
 * @return The extracted Q matrix.
 */
  @Override public DenseMatrix64F getV(  DenseMatrix64F V,  boolean transpose,  boolean compact){
    V=handleV(V,transpose,compact,m,n,min);
    CommonOps.setIdentity(V);
    for (int j=min - 1; j >= 0; j--) {
      u[j + 1]=1;
      for (int i=j + 2; i < n; i++) {
        u[i]=UBV.get(j,i);
      }
      if (transpose)       QrHelperFunctions.rank1UpdateMultL(V,u,gammasV[j],j + 1,j + 1,n);
 else       QrHelperFunctions.rank1UpdateMultR(V,u,gammasV[j],j + 1,j + 1,n,this.b);
    }
    return V;
  }
  public static DenseMatrix64F handleV(  DenseMatrix64F V,  boolean transpose,  boolean compact,  int m,  int n,  int min){
    int w=n > m ? min + 1 : min;
    if (compact) {
      if (transpose) {
        if (V == null) {
          V=new DenseMatrix64F(w,n);
        }
 else         V.reshape(w,n,false);
      }
 else {
        if (V == null) {
          V=new DenseMatrix64F(n,w);
        }
 else         V.reshape(n,w,false);
      }
    }
 else {
      if (V == null) {
        V=new DenseMatrix64F(n,n);
      }
 else       V.reshape(n,n,false);
    }
    return V;
  }
  /** 
 * Internal function for computing the decomposition.
 */
  private boolean _decompose(){
    for (int k=0; k < min; k++) {
      computeU(k);
      computeV(k);
    }
    return true;
  }
  protected void computeU(  int k){
    double b[]=UBV.data;
    double max=0;
    for (int i=k; i < m; i++) {
      double val=u[i]=b[i * n + k];
      val=Math.abs(val);
      if (val > max)       max=val;
    }
    if (max > 0) {
      double tau=QrHelperFunctions.computeTauAndDivide(k,m,u,max);
      double nu=u[k] + tau;
      QrHelperFunctions.divideElements_Bcol(k + 1,m,n,u,b,k,nu);
      u[k]=1.0;
      double gamma=nu / tau;
      gammasU[k]=gamma;
      QrHelperFunctions.rank1UpdateMultR(UBV,u,gamma,k + 1,k,m,this.b);
      b[k * n + k]=-tau * max;
    }
 else {
      gammasU[k]=0;
    }
  }
  protected void computeV(  int k){
    double b[]=UBV.data;
    int row=k * n;
    double max=QrHelperFunctions.findMax(b,row + k + 1,n - k - 1);
    if (max > 0) {
      double tau=QrHelperFunctions.computeTauAndDivide(k + 1,n,b,row,max);
      double nu=b[row + k + 1] + tau;
      QrHelperFunctions.divideElements_Brow(k + 2,n,u,b,row,nu);
      u[k + 1]=1.0;
      double gamma=nu / tau;
      gammasV[k]=gamma;
      QrHelperFunctions.rank1UpdateMultL(UBV,u,gamma,k + 1,k + 1,n);
      b[row + k + 1]=-tau * max;
    }
 else {
      gammasV[k]=0;
    }
  }
  /** 
 * Returns gammas from the householder operations for the U matrix.
 * @return gammas for householder operations
 */
  public double[] getGammasU(){
    return gammasU;
  }
  /** 
 * Returns gammas from the householder operations for the V matrix.
 * @return gammas for householder operations
 */
  public double[] getGammasV(){
    return gammasV;
  }
  @Override public boolean inputModified(){
    return true;
  }
}
