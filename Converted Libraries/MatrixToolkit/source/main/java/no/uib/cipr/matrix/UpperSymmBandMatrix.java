package no.uib.cipr.matrix;
/** 
 * Upper symmetrical banded matrix. The same storage as{@link no.uib.cipr.matrix.BandMatrix BandMatrix}, but without subdiagonals.
 * Lower part of the matrix is implictly known by symmetry
 */
public class UpperSymmBandMatrix extends AbstractSymmBandMatrix {
  /** 
 * Constructor for UpperSymmBandMatrix
 * @param nSize of the matrix. Since the matrix must be square, this
 * equals both the number of rows and columns
 * @param kdNumber of bands off the main diagonal (off diagonals)
 */
  public UpperSymmBandMatrix(  int n,  int kd){
    super(n,0,kd,UpLo.Upper);
  }
  /** 
 * Constructor for UpperSymmBandMatrix
 * @param AMatrix to copy contents from. Only the parts of <code>A</code>
 * that lie within the allocated band are copied over, the rest
 * is ignored
 * @param kdNumber of bands off the main diagonal (off diagonals)
 */
  public UpperSymmBandMatrix(  Matrix A,  int kd){
    this(A,kd,true);
  }
  /** 
 * Constructor for UpperSymmBandMatrix
 * @param AMatrix to copy contents from. Only the parts of <code>A</code>
 * that lie within the allocated band are copied over, the rest
 * is ignored
 * @param kdNumber of bands off the main diagonal (off diagonals)
 * @param deepTrue for a deep copy. For shallow copies, <code>A</code>
 * must be a banded matrix
 */
  public UpperSymmBandMatrix(  Matrix A,  int kd,  boolean deep){
    super(A,0,kd,deep,UpLo.Upper);
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
  @Override public UpperSymmBandMatrix copy(){
    return new UpperSymmBandMatrix(this,kd);
  }
}
