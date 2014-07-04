package no.uib.cipr.matrix;
import no.uib.cipr.matrix.UnitUpperTriangDenseMatrix;
/** 
 * Test of UnitUpperTriangDenseMatrix
 */
public class UnitUpperTriangDenseMatrixTest extends UnitTriangMatrixTestAbstract {
  public UnitUpperTriangDenseMatrixTest(  String arg0){
    super(arg0);
  }
  @Override protected void createPrimary() throws Exception {
    int n=Utilities.getInt(max);
    A=new UnitUpperTriangDenseMatrix(n);
    Ad=Utilities.unitUpperPopulate(A);
    Utilities.unitSet(Ad);
  }
}
