package no.uib.cipr.matrix;
/** 
 * Upper triangular dense matrix. It has the same storage layout as the{@link no.uib.cipr.matrix.DenseMatrix DenseMatrix}, but only refers to
 * elements above or on the main diagonal. The remaining elements are assumed to
 * be zero, but since they are never accessed, they need not be.
 */
public class UpperTriangDenseMatrix extends AbstractTriangDenseMatrix {
  /** 
 * Constructor for UpperTriangDenseMatrix
 * @param nSize of the matrix. Since the matrix must be square, this
 * equals both the number of rows and columns
 */
  public UpperTriangDenseMatrix(  int n){
    super(n,UpLo.Upper,Diag.NonUnit);
  }
  /** 
 * Constructor for UpperTriangDenseMatrix
 * @param nSize of the matrix. Since the matrix must be square, this
 * equals both the number of rows and columns
 */
  UpperTriangDenseMatrix(  int n,  Diag diag){
    super(n,UpLo.Upper,diag);
  }
  /** 
 * Constructor for UpperTriangDenseMatrix
 * @param AMatrix to copy from. Only the upper triangular part is copied
 */
  public UpperTriangDenseMatrix(  Matrix A){
    this(A,Math.min(A.numRows(),A.numColumns()));
  }
  /** 
 * Constructor for UpperTriangDenseMatrix
 * @param AMatrix to copy from. Only the upper triangular part is copied
 * @param deepTrue for deep copy, false for reference (in which case
 * <code>A</code> must be a dense matrix)
 */
  public UpperTriangDenseMatrix(  Matrix A,  boolean deep){
    this(A,Math.min(A.numRows(),A.numColumns()),deep);
  }
  /** 
 * Constructor for UpperTriangDenseMatrix
 * @param AMatrix to copy from. Only the upper triangular part is copied
 * @param deepTrue for deep copy, false for reference (in which case
 * <code>A</code> must be a dense matrix)
 */
  UpperTriangDenseMatrix(  Matrix A,  boolean deep,  Diag diag){
    this(A,Math.min(A.numRows(),A.numColumns()),deep,diag);
  }
  /** 
 * Constructor for UpperTriangDenseMatrix
 * @param AMatrix to copy from. Only the upper triangular part is copied
 * @param kSize of matrix to refer.
 * <code>k&lt;min(numRows,numColumns)</code>
 */
  public UpperTriangDenseMatrix(  Matrix A,  int k){
    this(A,k,true);
  }
  /** 
 * Constructor for UpperTriangDenseMatrix
 * @param AMatrix to copy from. Only the upper triangular part is copied
 * @param kSize of matrix to refer.
 * <code>k&lt;min(numRows,numColumns)</code>
 * @param deepTrue for deep copy, false for reference (in which case
 * <code>A</code> must be a dense matrix)
 */
  public UpperTriangDenseMatrix(  Matrix A,  int k,  boolean deep){
    super(A,k,deep,UpLo.Upper,Diag.NonUnit);
  }
  /** 
 * Constructor for UpperTriangDenseMatrix
 * @param AMatrix to copy from. Only the upper triangular part is copied
 * @param kSize of matrix to refer.
 * <code>k&lt;min(numRows,numColumns)</code>
 * @param deepTrue for deep copy, false for reference (in which case
 * <code>A</code> must be a dense matrix)
 */
  UpperTriangDenseMatrix(  Matrix A,  int k,  boolean deep,  Diag diag){
    super(A,k,deep,UpLo.Upper,diag);
  }
  @Override public void add(  int row,  int column,  double value){
    if (row > column)     throw new IllegalArgumentException("row > column");
    super.add(row,column,value);
  }
  @Override public double get(  int row,  int column){
    if (row > column)     return 0;
    return super.get(row,column);
  }
  @Override public void set(  int row,  int column,  double value){
    if (row > column)     throw new IllegalArgumentException("row > column");
    super.set(row,column,value);
  }
  @Override void copy(  Matrix A){
    for (    MatrixEntry e : A)     if (e.row() <= e.column())     set(e.row(),e.column(),e.get());
  }
  @Override public Matrix set(  Matrix A){
    zero();
    copy(A);
    return this;
  }
  @Override public UpperTriangDenseMatrix copy(){
    return new UpperTriangDenseMatrix(this);
  }
}
