package no.uib.cipr.matrix;
import no.uib.cipr.matrix.UpperTriangDenseMatrix;
/** 
 * Test of UpperTriangDenseMatrix
 */
public class UpperTriangDenseMatrixTest extends TriangMatrixTestAbstract {
  public UpperTriangDenseMatrixTest(  String arg0){
    super(arg0);
  }
  @Override protected void createPrimary() throws Exception {
    int n=Utilities.getInt(1,max);
    A=new UpperTriangDenseMatrix(n);
    Ad=Utilities.upperPopulate(A);
    Utilities.addDiagonal(A,Ad,1);
  }
}
