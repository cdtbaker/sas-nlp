package no.uib.cipr.matrix;
import no.uib.cipr.matrix.UnitLowerTriangBandMatrix;
/** 
 * Test of UnitLowerTriangBandMatrix
 */
public class UnitLowerTriangBandMatrixTest extends UnitTriangMatrixTestAbstract {
  public UnitLowerTriangBandMatrixTest(  String arg0){
    super(arg0);
  }
  @Override protected void createPrimary() throws Exception {
    int n=Utilities.getInt(1,max);
    int kd=Math.min(Utilities.getInt(max),Math.max(n - 1,0));
    A=new UnitLowerTriangBandMatrix(n,kd);
    Ad=Utilities.unitBandPopulate(A,kd,0);
    Utilities.unitSet(Ad);
  }
}
