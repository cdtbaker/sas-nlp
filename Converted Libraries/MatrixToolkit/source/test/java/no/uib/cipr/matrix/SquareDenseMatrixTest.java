package no.uib.cipr.matrix;
import no.uib.cipr.matrix.DenseMatrix;
/** 
 * Test of square dense matrices. This is done as some solvers change for square
 * matrices (LU and QR)
 */
public class SquareDenseMatrixTest extends MatrixTestAbstract {
  public SquareDenseMatrixTest(  String arg0){
    super(arg0);
  }
  @Override protected void createPrimary() throws Exception {
    int n=Utilities.getInt(1,max);
    A=new DenseMatrix(n,n);
    Ad=Utilities.populate(A);
  }
}
