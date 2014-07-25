package no.uib.cipr.matrix;
/** 
 * Unit lower triangular packed matrix. Same storage as{@link no.uib.cipr.matrix.LowerTriangPackMatrix LowerTriangPackMatrix}, but
 * the main diagonal is assumed to be all ones.
 */
public class UnitLowerTriangPackMatrix extends LowerTriangPackMatrix {
  /** 
 * Constructor for UnitLowerTriangPackMatrix
 * @param nSize of the matrix. Since the matrix must be square, this
 * equals both the number of rows and columns
 */
  public UnitLowerTriangPackMatrix(  int n){
    super(n,Diag.Unit);
  }
  /** 
 * Constructor for UnitLowerTriangPackMatrix
 * @param AMatrix to copy contents from. Only the entries of the relevant
 * part are copied
 */
  public UnitLowerTriangPackMatrix(  Matrix A){
    this(A,true);
  }
  /** 
 * Constructor for UnitLowerTriangPackMatrix
 * @param AMatrix to copy contents from. Only the entries of the relevant
 * part are copied
 * @param deepTrue if the copy is deep, else false (giving a shallow copy).
 * For shallow copies, <code>A</code> must be a packed matrix
 */
  public UnitLowerTriangPackMatrix(  Matrix A,  boolean deep){
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
  @Override public UnitLowerTriangPackMatrix copy(){
    return new UnitLowerTriangPackMatrix(this);
  }
}
