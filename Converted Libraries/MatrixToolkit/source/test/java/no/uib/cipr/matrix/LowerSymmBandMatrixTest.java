package no.uib.cipr.matrix;
import no.uib.cipr.matrix.LowerSymmBandMatrix;
/** 
 * Test of LowerSymmBandMatrix
 */
public class LowerSymmBandMatrixTest extends StructImmutableMatrixTestAbstract {
  public LowerSymmBandMatrixTest(  String arg0){
    super(arg0);
  }
  @Override protected void createPrimary() throws Exception {
    int n=Utilities.getInt(1,max);
    int kd=Math.min(Utilities.getInt(max),Math.max(n - 1,0));
    A=new LowerSymmBandMatrix(n,kd);
    Ad=Utilities.bandPopulate(A,kd,kd);
    Utilities.lowerSymmetrice(Ad);
  }
}
