package no.uib.cipr.matrix;
/** 
 * Lower symmetric packed matrix. Same storage as{@link no.uib.cipr.matrix.LowerTriangPackMatrix LowerTriangPackMatrix}, but
 * the upper triangular part is known by symmetry.
 */
public class LowerSymmPackMatrix extends AbstractSymmPackMatrix {
  /** 
 * Constructor for LowerSymmPackMatrix
 * @param nSize of the matrix. Since the matrix must be square, this
 * equals both the number of rows and columns
 */
  public LowerSymmPackMatrix(  int n){
    super(n,UpLo.Lower);
  }
  /** 
 * Constructor for LowerSymmPackMatrix
 * @param AMatrix to copy contents from. Only the entries of the relevant
 * part are copied
 */
  public LowerSymmPackMatrix(  Matrix A){
    this(A,true);
  }
  /** 
 * Constructor for LowerSymmPackMatrix
 * @param AMatrix to copy contents from. Only the entries of the relevant
 * part are copied
 * @param deepTrue if the copy is deep, else false (giving a shallow copy).
 * For shallow copies, <code>A</code> must be a packed matrix
 */
  public LowerSymmPackMatrix(  Matrix A,  boolean deep){
    super(A,deep,UpLo.Lower);
  }
  @Override public void add(  int row,  int column,  double value){
    if (column <= row)     data[getIndex(row,column)]+=value;
  }
  @Override public void set(  int row,  int column,  double value){
    if (column <= row)     data[getIndex(row,column)]=value;
  }
  @Override public double get(  int row,  int column){
    if (column <= row)     return data[getIndex(row,column)];
    return data[getIndex(column,row)];
  }
  /** 
 * Checks the row and column indices, and returns the linear data index
 */
  int getIndex(  int row,  int column){
    check(row,column);
    return row + (2 * n - (column + 1)) * column / 2;
  }
  @Override void copy(  Matrix A){
    for (    MatrixEntry e : A)     if (e.row() >= e.column())     set(e.row(),e.column(),e.get());
  }
  @Override public LowerSymmPackMatrix copy(){
    return new LowerSymmPackMatrix(this);
  }
}
