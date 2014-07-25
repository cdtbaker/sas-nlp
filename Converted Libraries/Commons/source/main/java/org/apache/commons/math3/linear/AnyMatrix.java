package org.apache.commons.math3.linear;
/** 
 * Interface defining very basic matrix operations.
 * @version $Id: AnyMatrix.java 1416643 2012-12-03 19:37:14Z tn $
 * @since 2.0
 */
public interface AnyMatrix {
  /** 
 * Is this a square matrix?
 * @return true if the matrix is square (rowDimension = columnDimension)
 */
  boolean isSquare();
  /** 
 * Returns the number of rows in the matrix.
 * @return rowDimension
 */
  int getRowDimension();
  /** 
 * Returns the number of columns in the matrix.
 * @return columnDimension
 */
  int getColumnDimension();
}
