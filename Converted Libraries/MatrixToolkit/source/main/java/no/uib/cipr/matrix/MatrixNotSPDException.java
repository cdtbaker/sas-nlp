package no.uib.cipr.matrix;
/** 
 * Matrix is not symmetrical, positive definite
 */
public class MatrixNotSPDException extends RuntimeException {
  private static final long serialVersionUID=4806417891899193518L;
  /** 
 * Constructor for MatrixNotSPDException
 */
  public MatrixNotSPDException(){
    super();
  }
  /** 
 * Constructor for MatrixNotSPDException
 * @param messageDescription of the exception
 */
  public MatrixNotSPDException(  String message){
    super(message);
  }
}
