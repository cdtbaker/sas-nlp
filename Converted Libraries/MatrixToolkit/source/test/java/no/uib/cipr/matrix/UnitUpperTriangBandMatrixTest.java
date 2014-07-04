package no.uib.cipr.matrix;
import no.uib.cipr.matrix.UnitUpperTriangBandMatrix;
/** 
 * Test of UnitUpperTriangBandMatrix
 */
public class UnitUpperTriangBandMatrixTest extends UnitTriangMatrixTestAbstract {
  public UnitUpperTriangBandMatrixTest(  String arg0){
    super(arg0);
  }
  @Override protected void createPrimary() throws Exception {
    int n=Utilities.getInt(1,max);
    int kd=Math.min(Utilities.getInt(max),Math.max(n - 1,0));
    A=new UnitUpperTriangBandMatrix(n,kd);
    Ad=Utilities.unitBandPopulate(A,0,kd);
    Utilities.unitSet(Ad);
  }
}
