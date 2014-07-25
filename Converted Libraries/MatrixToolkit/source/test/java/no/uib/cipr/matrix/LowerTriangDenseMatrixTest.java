package no.uib.cipr.matrix;
import no.uib.cipr.matrix.LowerTriangDenseMatrix;
/** 
 * Test of LowerTriangDenseMatrix
 */
public class LowerTriangDenseMatrixTest extends TriangMatrixTestAbstract {
  public LowerTriangDenseMatrixTest(  String arg0){
    super(arg0);
  }
  @Override protected void createPrimary() throws Exception {
    int n=Utilities.getInt(1,max);
    A=new LowerTriangDenseMatrix(n);
    Ad=Utilities.lowerPopulate(A);
    Utilities.addDiagonal(A,Ad,1);
  }
}
