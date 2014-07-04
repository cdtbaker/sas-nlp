package org.ejml.alg.dense.mult;
/** 
 * If two matrices did not have compatible dimensions for the operation this exception
 * is thrown.
 * @author Peter Abeles
 */
public class MatrixDimensionException extends RuntimeException {
  public MatrixDimensionException(){
  }
  public MatrixDimensionException(  String message){
    super(message);
  }
}
