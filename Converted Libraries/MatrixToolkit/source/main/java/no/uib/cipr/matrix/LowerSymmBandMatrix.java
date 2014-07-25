package no.uib.cipr.matrix;
/** 
 * Lower symmetrical banded matrix. The same storage as{@link no.uib.cipr.matrix.BandMatrix BandMatrix}, but without
 * superdiagonals. Upper part of the matrix is implictly known by symmetry
 */
public class LowerSymmBandMatrix extends AbstractSymmBandMatrix {
  /** 
 * Constructor for LowerSymmBandMatrix
 * @param nSize of the matrix. Since the matrix must be square, this
 * equals both the number of rows and columns
 * @param kdNumber of bands off the main diagonal (off diagonals)
 */
  public LowerSymmBandMatrix(  int n,  int kd){
    super(n,kd,0,UpLo.Lower);
  }
  /** 
 * Constructor for LowerSymmBandMatrix
 * @param AMatrix to copy contents from. Only the parts of <code>A</code>
 * that lie within the allocated band are copied over, the rest
 * is ignored
 * @param kdNumber of bands off the main diagonal (off diagonals)
 */
  public LowerSymmBandMatrix(  Matrix A,  int kd){
    this(A,kd,true);
  }
  /** 
 * Constructor for LowerSymmBandMatrix
 * @param AMatrix to copy contents from. Only the parts of <code>A</code>
 * that lie within the allocated band are copied over, the rest
 * is ignored
 * @param kdNumber of bands off the main diagonal (off diagonals)
 * @param deepTrue for a deep copy. For shallow copies, <code>A</code>
 * must be a banded matrix
 */
  public LowerSymmBandMatrix(  Matrix A,  int kd,  boolean deep){
    super(A,kd,0,deep,UpLo.Lower);
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
  @Override public LowerSymmBandMatrix copy(){
    return new LowerSymmBandMatrix(this,kd);
  }
}
