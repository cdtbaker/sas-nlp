package no.uib.cipr.matrix;
/** 
 * Unit upper triangular banded matrix. The same storage as{@link no.uib.cipr.matrix.LowerTriangBandMatrix LowerTriangBandMatrix}, but
 * the main diagonal is assumed to be all ones. It is still allocated, however.
 */
public class UnitUpperTriangBandMatrix extends UpperTriangBandMatrix {
  /** 
 * Constructor for UnitUpperTriangBandMatrix
 * @param nSize of the matrix. Since the matrix must be square, this
 * equals both the number of rows and columns
 * @param kdNumber of bands above the main diagonal (superdiagonals)
 */
  public UnitUpperTriangBandMatrix(  int n,  int kd){
    super(n,kd,Diag.Unit);
  }
  /** 
 * Constructor for UnitUpperTriangBandMatrix
 * @param AMatrix to copy contents from. Only the parts of <code>A</code>
 * that lie within the allocated band are copied over, the rest
 * is ignored (including main diagonal entries)
 * @param kdNumber of bands above the main diagonal (superdiagonals)
 */
  public UnitUpperTriangBandMatrix(  Matrix A,  int kd){
    this(A,kd,true);
  }
  /** 
 * Constructor for UnitUpperTriangBandMatrix
 * @param AMatrix to copy contents from. Only the parts of <code>A</code>
 * that lie within the allocated band are copied over, the rest
 * is ignored (including main diagonal entries)
 * @param kdNumber of bands above the main diagonal (superdiagonals)
 * @param deepTrue for a deep copy. For shallow copies, <code>A</code>
 * must be a banded matrix
 */
  public UnitUpperTriangBandMatrix(  Matrix A,  int kd,  boolean deep){
    super(A,kd,deep,Diag.Unit);
  }
  @Override public void add(  int row,  int column,  double value){
    if (row == column)     throw new IllegalArgumentException("row == column");
    super.add(row,column,value);
  }
  @Override public double get(  int row,  int column){
    if (row == column)     return 1;
    return super.get(row,column);
  }
  @Override public void set(  int row,  int column,  double value){
    if (row == column)     throw new IllegalArgumentException("row == column");
    super.set(row,column,value);
  }
  @Override public UnitUpperTriangBandMatrix copy(){
    return new UnitUpperTriangBandMatrix(this,ku);
  }
  @Override public Matrix zero(){
    throw new UnsupportedOperationException();
  }
  @Override void copy(  Matrix A){
    for (    MatrixEntry e : A)     if (inBand(e.row(),e.column()) && e.row() != e.column())     set(e.row(),e.column(),e.get());
  }
}
