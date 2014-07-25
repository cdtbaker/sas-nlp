package no.uib.cipr.matrix;
/** 
 * Unit upper triangular dense matrix. Almost the same as the{@link no.uib.cipr.matrix.UpperTriangDenseMatrix UpperTriangDenseMatrix},
 * but additionally assumes the main diagonal to be all ones. However it does
 * not access it, so it may be actually be different.
 */
public class UnitUpperTriangDenseMatrix extends UpperTriangDenseMatrix {
  /** 
 * Constructor for UnitUpperTriangDenseMatrix
 * @param nSize of the matrix. Since the matrix must be square, this
 * equals both the number of rows and columns
 */
  public UnitUpperTriangDenseMatrix(  int n){
    super(n,Diag.Unit);
  }
  /** 
 * Constructor for UnitUpperTriangDenseMatrix. Matrix is copied from the
 * supplied matrix
 * @param AMatrix to copy from. Only the strictly upper triangular part
 * is copied
 */
  public UnitUpperTriangDenseMatrix(  Matrix A){
    this(A,true);
  }
  /** 
 * Constructor for UnitUpperTriangDenseMatrix. Matrix is copied from the
 * supplied matrix
 * @param AMatrix to copy from. Only the strictly upper triangular part
 * is copied
 * @param deepIf true, <code>A</code> is copied, else a shallow copy is
 * made and the matrices share underlying storage. For this,
 * <code>A</code> must be a dense matrix
 */
  public UnitUpperTriangDenseMatrix(  Matrix A,  boolean deep){
    super(A,deep,Diag.Unit);
  }
  @Override public void add(  int row,  int column,  double value){
    if (column == row)     throw new IllegalArgumentException("column == row");
    super.add(row,column,value);
  }
  @Override public double get(  int row,  int column){
    if (row == column)     return 1;
    return super.get(row,column);
  }
  @Override public void set(  int row,  int column,  double value){
    if (column == row)     throw new IllegalArgumentException("column == row");
    super.set(row,column,value);
  }
  @Override void copy(  Matrix A){
    for (    MatrixEntry e : A)     if (e.row() < e.column())     set(e.row(),e.column(),e.get());
  }
  @Override public UnitUpperTriangDenseMatrix copy(){
    return new UnitUpperTriangDenseMatrix(this);
  }
  @Override public Matrix zero(){
    throw new UnsupportedOperationException();
  }
}
