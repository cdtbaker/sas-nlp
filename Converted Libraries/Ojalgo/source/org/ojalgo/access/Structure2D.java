package org.ojalgo.access;
/** 
 * count() == countRows() * countColumns()
 */
interface Structure2D extends Structure1D {
  /** 
 * @return The number of columns
 */
  long countColumns();
  /** 
 * @return The number of rows
 */
  long countRows();
  /** 
 * The size of this structure in the column-direction/dimension
 * @return The number of columns
 * @deprecated v35 Use {@link #countColumns()} instead
 */
  @Deprecated int getColDim();
  /** 
 * The size of this structure in the row-direction/dimension
 * @return The number of rows
 * @deprecated v35 Use {@link #countRowss()} instead
 */
  @Deprecated int getRowDim();
}
