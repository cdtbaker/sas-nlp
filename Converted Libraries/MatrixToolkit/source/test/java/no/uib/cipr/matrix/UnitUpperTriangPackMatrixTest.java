package no.uib.cipr.matrix;
import no.uib.cipr.matrix.UnitUpperTriangPackMatrix;
/** 
 * Test of UnitUpperTriangPackMatrix
 */
public class UnitUpperTriangPackMatrixTest extends UnitTriangMatrixTestAbstract {
  public UnitUpperTriangPackMatrixTest(  String arg0){
    super(arg0);
  }
  @Override protected void createPrimary() throws Exception {
    int n=Utilities.getInt(1,max);
    A=new UnitUpperTriangPackMatrix(n);
    Ad=Utilities.unitUpperPopulate(A);
    Utilities.unitSet(Ad);
  }
}
