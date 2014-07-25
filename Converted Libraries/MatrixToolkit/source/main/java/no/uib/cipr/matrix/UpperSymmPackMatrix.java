package no.uib.cipr.matrix;
/** 
 * Upper symmetric packed matrix. Same storage as{@link no.uib.cipr.matrix.UpperTriangPackMatrix UpperTriangPackMatrix}, but
 * the lower triangular part is known by symmetry.
 */
public class UpperSymmPackMatrix extends AbstractSymmPackMatrix {
  /** 
 * Constructor for UpperSymmPackMatrix
 * @param nSize of the matrix. Since the matrix must be square, this
 * equals both the number of rows and columns
 */
  public UpperSymmPackMatrix(  int n){
    super(n,UpLo.Upper);
  }
  /** 
 * Constructor for UpperSymmPackMatrix
 * @param AMatrix to copy contents from. Only the entries of the relevant
 * part are copied
 */
  public UpperSymmPackMatrix(  Matrix A){
    this(A,true);
  }
  /** 
 * Constructor for UpperSymmPackMatrix
 * @param AMatrix to copy contents from. Only the entries of the relevant
 * part are copied
 * @param deepTrue if the copy is deep, else false (giving a shallow copy).
 * For shallow copies, <code>A</code> must be a packed matrix
 */
  public UpperSymmPackMatrix(  Matrix A,  boolean deep){
    super(A,deep,UpLo.Upper);
  }
  @Override public void add(  int row,  int column,  double value){
    if (row <= column)     data[getIndex(row,column)]+=value;
  }
  @Override public void set(  int row,  int column,  double value){
    if (row <= column)     data[getIndex(row,column)]=value;
  }
  @Override public double get(  int row,  int column){
    if (row <= column)     return data[getIndex(row,column)];
    return data[getIndex(column,row)];
  }
  /** 
 * Checks the row and column indices, and returns the linear data index
 */
  int getIndex(  int row,  int column){
    check(row,column);
    return row + (column + 1) * column / 2;
  }
  @Override void copy(  Matrix A){
    for (    MatrixEntry e : A)     if (e.row() <= e.column())     set(e.row(),e.column(),e.get());
  }
  @Override public UpperSymmPackMatrix copy(){
    return new UpperSymmPackMatrix(this);
  }
}
