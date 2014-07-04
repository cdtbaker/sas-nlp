package no.uib.cipr.matrix;
import no.uib.cipr.matrix.UnitLowerTriangPackMatrix;
/** 
 * Test of UnitLowerTriangPackMatrix
 */
public class UnitLowerTriangPackMatrixTest extends UnitTriangMatrixTestAbstract {
  public UnitLowerTriangPackMatrixTest(  String arg0){
    super(arg0);
  }
  @Override protected void createPrimary() throws Exception {
    int n=Utilities.getInt(1,max);
    A=new UnitLowerTriangPackMatrix(n);
    Ad=Utilities.unitLowerPopulate(A);
    Utilities.unitSet(Ad);
  }
}
