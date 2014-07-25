package no.uib.cipr.matrix;
/** 
 * Upper triangular packed matrix. In contrast with{@link no.uib.cipr.matrix.LowerTriangDenseMatrix LowerTriangDenseMatrix},
 * this matrix exploits the sparsity by only storing about half the matrix. As
 * such, the triangular matrix
 * <p>
 * <table border="1">
 * <tr>
 * <td>a<sub>11</sub></td>
 * <td>a<sub>12</sub></td>
 * <td>a<sub>13</sub></td>
 * <td>a<sub>14</sub></td>
 * </tr>
 * <tr>
 * <td>&nbsp;</td>
 * <td>a<sub>22</sub></td>
 * <td>a<sub>23</sub></td>
 * <td>a<sub>24</sub></td>
 * </tr>
 * <tr>
 * <td>&nbsp;</td>
 * <td>&nbsp;</td>
 * <td>a<sub>33</sub></td>
 * <td>a<sub>34</sub></td>
 * </tr>
 * <tr>
 * <td>&nbsp;</td>
 * <td>&nbsp;</td>
 * <td>&nbsp;</td>
 * <td>a<sub>44</sub></td>
 * </tr>
 * </table>
 * </p>
 * <p>
 * is packed as follows:
 * </p>
 * <p>
 * <table border="1">
 * <tr>
 * <td>a<sub>11</sub></td>
 * <td>a<sub>12</sub></td>
 * <td>a<sub>22</sub></td>
 * <td>a<sub>13</sub></td>
 * <td>a<sub>23</sub></td>
 * <td>a<sub>33</sub></td>
 * <td>a<sub>14</sub></td>
 * <td>a<sub>24</sub></td>
 * <td>a<sub>34</sub></td>
 * <td>a<sub>44</sub></td>
 * </tr>
 * </table>
 * </p>
 */
public class UpperTriangPackMatrix extends AbstractTriangPackMatrix {
  /** 
 * Constructor for UpperTriangPackMatrix
 * @param nSize of the matrix. Since the matrix must be square, this
 * equals both the number of rows and columns
 */
  public UpperTriangPackMatrix(  int n){
    super(n,UpLo.Upper,Diag.NonUnit);
  }
  /** 
 * Constructor for UpperTriangPackMatrix
 * @param nSize of the matrix. Since the matrix must be square, this
 * equals both the number of rows and columns
 */
  UpperTriangPackMatrix(  int n,  Diag diag){
    super(n,UpLo.Upper,diag);
  }
  /** 
 * Constructor for UpperTriangPackMatrix
 * @param AMatrix to copy contents from. Only the entries of the relevant
 * part are copied
 */
  public UpperTriangPackMatrix(  Matrix A){
    this(A,true);
  }
  /** 
 * Constructor for UpperTriangPackMatrix
 * @param AMatrix to copy contents from. Only the entries of the relevant
 * part are copied
 * @param deepTrue if the copy is deep, else false (giving a shallow copy).
 * For shallow copies, <code>A</code> must be a packed matrix
 */
  public UpperTriangPackMatrix(  Matrix A,  boolean deep){
    super(A,deep,UpLo.Upper,Diag.NonUnit);
  }
  /** 
 * Constructor for UpperTriangPackMatrix
 * @param AMatrix to copy contents from. Only the entries of the relevant
 * part are copied
 * @param deepTrue if the copy is deep, else false (giving a shallow copy).
 * For shallow copies, <code>A</code> must be a packed matrix
 */
  UpperTriangPackMatrix(  Matrix A,  boolean deep,  Diag diag){
    super(A,deep,UpLo.Upper,diag);
  }
  @Override public void add(  int row,  int column,  double value){
    if (row > column)     throw new IllegalArgumentException("row > column");
    data[getIndex(row,column)]+=value;
  }
  @Override public void set(  int row,  int column,  double value){
    if (row > column)     throw new IllegalArgumentException("row > column");
    data[getIndex(row,column)]=value;
  }
  @Override public double get(  int row,  int column){
    if (row > column)     return 0;
    return data[getIndex(row,column)];
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
  @Override public UpperTriangPackMatrix copy(){
    return new UpperTriangPackMatrix(this);
  }
}
