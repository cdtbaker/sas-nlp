package no.uib.cipr.matrix.io;
/** 
 * Contains the size of a matrix stored in the <a
 * href="http://math.nist.gov/MatrixMarket">Matrix Market</a> exchange format
 */
public class MatrixSize {
  /** 
 * Number of rows
 */
  private int numRows;
  /** 
 * Number of columns
 */
  private int numColumns;
  /** 
 * Number of entries stored
 */
  private int numEntries;
  /** 
 * Constructor for MatrixSize
 * @param numRowsNumber of rows in the matrix
 * @param numColumnsNumber of columns in the matrix
 * @param infoInfo on the matrix
 */
  public MatrixSize(  int numRows,  int numColumns,  MatrixInfo info){
    this.numRows=numRows;
    this.numColumns=numColumns;
    if (!info.isDense())     throw new IllegalArgumentException("Matrix must be dense");
    if (info.isGeneral())     numEntries=numRows * numColumns;
 else     if (info.isSymmetric() || info.isHermitian())     numEntries=((numRows * numColumns - numRows) / 2 + numRows);
 else     if (info.isSkewSymmetric())     numEntries=(numRows * numColumns - numRows) / 2;
  }
  /** 
 * Constructor for MatrixSize
 * @param numRowsNumber of rows in the matrix
 * @param numColumnsNumber of columns in the matrix
 * @param numEntriesNumber of entries stored
 */
  public MatrixSize(  int numRows,  int numColumns,  int numEntries){
    this.numRows=numRows;
    this.numColumns=numColumns;
    this.numEntries=numEntries;
    long maxR=numRows, maxC=numColumns, max=maxR * maxC;
    if (numEntries > max)     throw new IllegalArgumentException("numEntries > numRows*numColumns");
  }
  /** 
 * Returns the number of rows in the matrix
 */
  public int numRows(){
    return numRows;
  }
  /** 
 * Returns the number of columns in the matrix
 */
  public int numColumns(){
    return numColumns;
  }
  /** 
 * Returns the number of entries stored
 */
  public int numEntries(){
    return numEntries;
  }
  /** 
 * Returns <code>true</code> if the matrix is square, else
 * <code>false</code>
 */
  public boolean isSquare(){
    return numRows == numColumns;
  }
}
