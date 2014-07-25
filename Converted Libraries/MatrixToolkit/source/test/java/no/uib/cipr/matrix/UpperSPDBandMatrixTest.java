package no.uib.cipr.matrix;
import no.uib.cipr.matrix.UpperSPDBandMatrix;
/** 
 * Test of UpperSPDBandMatrix
 */
public class UpperSPDBandMatrixTest extends StructImmutableMatrixTestAbstract {
  public UpperSPDBandMatrixTest(  String arg0){
    super(arg0);
  }
  @Override protected void createPrimary() throws Exception {
    int n=Utilities.getInt(1,max);
    int kd=Math.min(Utilities.getInt(max),Math.max(n - 1,0));
    A=new UpperSPDBandMatrix(n,kd);
    Ad=Utilities.bandPopulate(A,kd,kd);
    Utilities.upperSymmetrice(Ad);
  }
}
