package no.uib.cipr.matrix;
/** 
 * Unit lower triangular dense matrix. Almost the same as the{@link no.uib.cipr.matrix.LowerTriangDenseMatrix LowerTriangDenseMatrix},
 * but additionally assumes the main diagonal to be all ones. However it does
 * not access it, so it may be actually be different.
 */
public class UnitLowerTriangDenseMatrix extends LowerTriangDenseMatrix {
  /** 
 * Constructor for UnitLowerTriangDenseMatrix
 * @param nSize of the matrix. Since the matrix must be square, this
 * equals both the number of rows and columns
 */
  public UnitLowerTriangDenseMatrix(  int n){
    super(n,Diag.Unit);
  }
  /** 
 * Constructor for UnitLowerTriangDenseMatrix
 * @param AMatrix to copy from. Only the strictly lower triangular part
 * is copied
 */
  public UnitLowerTriangDenseMatrix(  Matrix A){
    this(A,true);
  }
  /** 
 * Constructor for UnitLowerTriangDenseMatrix
 * @param AMatrix to copy from. Only the strictly lower triangular part
 * is copied
 * @param deepIf true, <code>A</code> is copied, else a shallow copy is
 * made and the matrices share underlying storage. For this,
 * <code>A</code> must be a dense matrix
 */
  public UnitLowerTriangDenseMatrix(  Matrix A,  boolean deep){
    super(A,deep,Diag.Unit);
  }
  @Override public void add(  int row,  int column,  double value){
    if (column == row)     throw new IllegalArgumentException("column == row");
    super.add(row,column,value);
  }
  @Override public double get(  int row,  int column){
    if (column == row)     return 1;
    return super.get(row,column);
  }
  @Override public void set(  int row,  int column,  double value){
    if (column == row)     throw new IllegalArgumentException("column == row");
    super.set(row,column,value);
  }
  @Override void copy(  Matrix A){
    for (    MatrixEntry e : A)     if (e.row() > e.column())     set(e.row(),e.column(),e.get());
  }
  @Override public UnitLowerTriangDenseMatrix copy(){
    return new UnitLowerTriangDenseMatrix(this);
  }
  @Override public Matrix zero(){
    throw new UnsupportedOperationException();
  }
}
