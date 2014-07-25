package no.uib.cipr.matrix;
import java.util.Arrays;
import java.util.Iterator;
/** 
 * Partial implementation of a banded matrix
 */
abstract class AbstractBandMatrix extends AbstractMatrix {
  /** 
 * Matrix data
 */
  double[] data;
  /** 
 * Number of upper and lower diagonals
 */
  int kl, ku;
  /** 
 * Size of the matrix. It is always square
 */
  int n;
  /** 
 * Constructor for AbstractBandMatrix
 * @param nSize of the matrix. Since the matrix must be square, this
 * equals both the number of rows and columns
 * @param klNumber of diagonals below the main diagonal
 * @param kuNumber of diagonals above the main diagonal
 */
  public AbstractBandMatrix(  int n,  int kl,  int ku){
    super(n,n);
    this.n=n;
    if (kl < 0 || ku < 0)     throw new IllegalArgumentException("kl < 0 || ku < 0");
    this.kl=kl;
    this.ku=ku;
    data=new double[numColumns * (1 + kl + ku)];
  }
  /** 
 * Constructor for AbstractBandMatrix
 * @param AMatrix to copy from
 * @param klNumber of diagonals below the main diagonal
 * @param kuNumber of diagonals above the main diagonal
 */
  public AbstractBandMatrix(  Matrix A,  int kl,  int ku){
    this(A,kl,ku,true);
  }
  /** 
 * Constructor for AbstractBandMatrix
 * @param AMatrix to copy from
 * @param klNumber of diagonals below the main diagonal
 * @param kuNumber of diagonals above the main diagonal
 * @param deepTrue if a deep copy is made. For a shallow copy,
 * <code>A</code> must be a banded matrix
 */
  public AbstractBandMatrix(  Matrix A,  int kl,  int ku,  boolean deep){
    super(A);
    if (kl < 0 || ku < 0)     throw new IllegalArgumentException("kl < 0 || ku < 0");
    if (!isSquare())     throw new IllegalArgumentException("Band matrix must be square");
    this.n=numRows;
    this.kl=kl;
    this.ku=ku;
    if (deep) {
      data=new double[numColumns * (1 + kl + ku)];
      copy(A);
    }
 else     this.data=((AbstractBandMatrix)A).getData();
  }
  /** 
 * Returns the matrix contents
 */
  public double[] getData(){
    return data;
  }
  @Override public void add(  int row,  int column,  double value){
    checkBand(row,column);
    data[getIndex(row,column)]+=value;
  }
  @Override public void set(  int row,  int column,  double value){
    checkBand(row,column);
    data[getIndex(row,column)]=value;
  }
  @Override public double get(  int row,  int column){
    if (!inBand(row,column))     return 0;
    return data[getIndex(row,column)];
  }
  /** 
 * Returns the number of lower diagonals
 */
  public int numSubDiagonals(){
    return kl;
  }
  /** 
 * Returns the number of upper diagonals
 */
  public int numSuperDiagonals(){
    return ku;
  }
  /** 
 * Returns true if the given indices are within the band
 */
  boolean inBand(  int row,  int column){
    return column - ku <= row && row <= column + kl;
  }
  /** 
 * Checks that the indices are within the band
 */
  void checkBand(  int row,  int column){
    if (!inBand(row,column))     throw new IndexOutOfBoundsException("Insertion index out of band");
  }
  /** 
 * Checks the row and column indices, and returns the linear data index
 */
  int getIndex(  int row,  int column){
    check(row,column);
    return ku + row - column + column * (kl + ku + 1);
  }
  /** 
 * Set this matrix equal to the given matrix
 */
  void copy(  Matrix A){
    for (    MatrixEntry e : A)     if (inBand(e.row(),e.column()))     set(e.row(),e.column(),e.get());
  }
  @Override public Matrix set(  Matrix B){
    if (!(B instanceof AbstractBandMatrix))     return super.set(B);
    checkSize(B);
    AbstractBandMatrix Bb=(AbstractBandMatrix)B;
    if (Bb.kl != kl)     throw new IllegalArgumentException("B.kl != kl");
    if (Bb.ku != ku)     throw new IllegalArgumentException("B.ku != ku");
    double[] Bd=Bb.getData();
    if (Bd == data)     return this;
    System.arraycopy(Bd,0,data,0,data.length);
    return this;
  }
  @Override public Matrix zero(){
    Arrays.fill(data,0);
    return this;
  }
  @Override public Iterator<MatrixEntry> iterator(){
    return new BandMatrixIterator();
  }
  /** 
 * Iterator over a band matrix
 */
class BandMatrixIterator extends RefMatrixIterator {
    /** 
 * Matrix bandwidths. Cannot be taken directly from the matrix since
 * that breaks iterating over symmetrical matrices
 */
    private final int lkl, lku;
    public BandMatrixIterator(    int lkl,    int lku){
      this.lkl=lkl;
      this.lku=lku;
    }
    public BandMatrixIterator(){
      this(kl,ku);
    }
    @Override public MatrixEntry next(){
      entry.update(row,column);
      if (row < Math.min(column + lkl,n - 1) && row >= Math.max(column - lku,0))       row++;
 else {
        column++;
        row=Math.max(column - lku,0);
      }
      return entry;
    }
  }
}
