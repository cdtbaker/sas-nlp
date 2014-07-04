package no.uib.cipr.matrix;
/** 
 * Lower symmetric dense matrix. It has the same storage layout as the{@link no.uib.cipr.matrix.DenseMatrix DenseMatrix}, but only refers to
 * elements below or on the main diagonal. The remaining elements are never
 * accessed nor changed, and is known only by symmetry.
 */
public class LowerSymmDenseMatrix extends AbstractSymmDenseMatrix {
  /** 
 * Constructor for LowerSymmDenseMatrix
 * @param nSize of the matrix. Since the matrix must be square, this
 * equals both the number of rows and columns
 */
  public LowerSymmDenseMatrix(  int n){
    super(n,UpLo.Lower);
  }
  /** 
 * Constructor for LowerSymmDenseMatrix
 * @param AMatrix to copy. It must be a square matrix, and only the lower
 * triangular part is copied
 */
  public LowerSymmDenseMatrix(  Matrix A){
    this(A,true);
  }
  /** 
 * Constructor for LowerSymmDenseMatrix
 * @param AMatrix to copy. It must be a square matrix, and only the lower
 * triangular part is copied
 * @param deepIf false, a shallow copy is made. In that case, <code>A</code>
 * must be a dense matrix
 */
  public LowerSymmDenseMatrix(  Matrix A,  boolean deep){
    super(A,deep,UpLo.Lower);
  }
  @Override public void add(  int row,  int column,  double value){
    if (column <= row)     super.add(row,column,value);
  }
  @Override public double get(  int row,  int column){
    if (column > row)     return super.get(column,row);
    return super.get(row,column);
  }
  @Override public void set(  int row,  int column,  double value){
    if (column <= row)     super.set(row,column,value);
  }
  @Override public LowerSymmDenseMatrix copy(){
    return new LowerSymmDenseMatrix(this);
  }
  @Override void copy(  Matrix A){
    for (    MatrixEntry e : A)     if (e.row() >= e.column())     set(e.row(),e.column(),e.get());
  }
}
