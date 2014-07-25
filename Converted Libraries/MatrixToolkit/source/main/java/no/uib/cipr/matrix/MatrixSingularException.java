package no.uib.cipr.matrix;
/** 
 * Matrix is singular
 */
public class MatrixSingularException extends RuntimeException {
  private static final long serialVersionUID=-8054618754675367225L;
  /** 
 * Constructor for MatrixSingularException
 */
  public MatrixSingularException(){
    super();
  }
  /** 
 * Constructor for MatrixSingularException
 * @param messageDescription of the exception
 */
  public MatrixSingularException(  String message){
    super(message);
  }
}
