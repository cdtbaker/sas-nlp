package org.ojalgo.matrix.store.operation;
/** 
 * aData array to be updated
 * aRowDim, aFirstCol & aColLimit (or aFirstRow, aRowLimit & aColDim) as needed.
 * other, operation specific, arguments in logical order
 * @author apete
 */
abstract class MatrixOperation {
  protected MatrixOperation(){
    super();
  }
}
