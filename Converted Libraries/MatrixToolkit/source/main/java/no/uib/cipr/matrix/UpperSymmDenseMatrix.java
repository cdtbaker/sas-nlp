package no.uib.cipr.matrix;
/** 
 * Upper symmetrix dense matrix. It has the same storage layout as the{@link no.uib.cipr.matrix.DenseMatrix DenseMatrix}, but only refers to
 * elements above or on the main diagonal. The remaining elements are never
 * accessed nor changed, and is known only by symmetry.
 */
public class UpperSymmDenseMatrix extends AbstractSymmDenseMatrix {
  /** 
 * Constructor for UpperSymmDenseMatrix
 * @param nSize of the matrix. Since the matrix must be square, this
 * equals both the number of rows and columns
 */
  public UpperSymmDenseMatrix(  int n){
    super(n,UpLo.Upper);
  }
  /** 
 * Constructor for UpperSymmDenseMatrix
 * @param AMatrix to copy. It must be a square matrix, and only the upper
 * triangular part is copied
 */
  public UpperSymmDenseMatrix(  Matrix A){
    this(A,true);
  }
  /** 
 * Constructor for UpperSymmDenseMatrix
 * @param AMatrix to copy. It must be a square matrix, and only the upper
 * triangular part is copied
 * @param deepIf false, a shallow copy is made. In that case, <code>A</code>
 * must be a dense matrix
 */
  public UpperSymmDenseMatrix(  Matrix A,  boolean deep){
    super(A,deep,UpLo.Upper);
  }
  @Override public void add(  int row,  int column,  double value){
    if (row <= column)     super.add(row,column,value);
  }
  @Override public double get(  int row,  int column){
    if (row > column)     return super.get(column,row);
    return super.get(row,column);
  }
  @Override public void set(  int row,  int column,  double value){
    if (row <= column)     super.set(row,column,value);
  }
  @Override public UpperSymmDenseMatrix copy(){
    return new UpperSymmDenseMatrix(this);
  }
  @Override void copy(  Matrix A){
    for (    MatrixEntry e : A)     if (e.row() <= e.column())     set(e.row(),e.column(),e.get());
  }
}
