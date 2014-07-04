package org.ejml.data;
import java.util.Iterator;
/** 
 * This is a matrix iterator for traversing through a submatrix.  For speed it is recommended
 * that you directly access the elements in the matrix, but there are some situations where this
 * can be a better design.
 * @author Peter Abeles
 */
public class MatrixIterator implements Iterator<Double> {
  private ReshapeMatrix64F a;
  private boolean rowMajor;
  private int minCol;
  private int minRow;
  private int index=0;
  private int size;
  private int submatrixStride;
  int subRow, subCol;
  /** 
 * Creates a new iterator for traversing through a submatrix inside this matrix.  It can be traversed
 * by row or by column.  Range of elements is inclusive, e.g. minRow = 0 and maxRow = 1 will include rows
 * 0 and 1.  The iteration starts at (minRow,minCol) and ends at (maxRow,maxCol)
 * @param a the matrix it is iterating through
 * @param rowMajor true means it will traverse through the submatrix by row first, false by columns.
 * @param minRow first row it will start at.
 * @param minCol first column it will start at.
 * @param maxRow last row it will stop at.
 * @param maxCol last column it will stop at.
 * @return A new MatrixIterator
 */
  public MatrixIterator(  ReshapeMatrix64F a,  boolean rowMajor,  int minRow,  int minCol,  int maxRow,  int maxCol){
    if (maxCol < minCol)     throw new IllegalArgumentException("maxCol has to be more than or equal to minCol");
    if (maxRow < minRow)     throw new IllegalArgumentException("maxRow has to be more than or equal to minCol");
    if (maxCol >= a.numCols)     throw new IllegalArgumentException("maxCol must be < numCols");
    if (maxRow >= a.numRows)     throw new IllegalArgumentException("maxRow must be < numCRows");
    this.a=a;
    this.rowMajor=rowMajor;
    this.minCol=minCol;
    this.minRow=minRow;
    size=(maxCol - minCol + 1) * (maxRow - minRow + 1);
    if (rowMajor)     submatrixStride=maxCol - minCol + 1;
 else     submatrixStride=maxRow - minRow + 1;
  }
  @Override public boolean hasNext(){
    return index < size;
  }
  @Override public Double next(){
    if (rowMajor) {
      subRow=index / submatrixStride;
      subCol=index % submatrixStride;
    }
 else {
      subRow=index % submatrixStride;
      subCol=index / submatrixStride;
    }
    index++;
    return a.get(subRow + minRow,subCol + minCol);
  }
  @Override public void remove(){
    throw new RuntimeException("Operation not supported");
  }
  /** 
 * Which element in the submatrix was returned by next()
 * @return Submatrix element's index.
 */
  public int getIndex(){
    return index - 1;
  }
  /** 
 * True if it is iterating through the matrix by rows and false if by columns.
 * @return row major or column major
 */
  public boolean isRowMajor(){
    return rowMajor;
  }
  /** 
 * Sets the value of the current element.
 * @param value The element's new value.
 */
  public void set(  double value){
    a.set(subRow + minRow,subCol + minCol,value);
  }
}
