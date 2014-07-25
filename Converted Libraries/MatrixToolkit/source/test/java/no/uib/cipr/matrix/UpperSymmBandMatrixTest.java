package no.uib.cipr.matrix;
import no.uib.cipr.matrix.UpperSymmBandMatrix;
/** 
 * Test of UpperSymmBandMatrix
 */
public class UpperSymmBandMatrixTest extends StructImmutableMatrixTestAbstract {
  public UpperSymmBandMatrixTest(  String arg0){
    super(arg0);
  }
  @Override protected void createPrimary() throws Exception {
    int n=Utilities.getInt(1,max);
    int kd=Math.min(Utilities.getInt(max),Math.max(n - 1,0));
    A=new UpperSymmBandMatrix(n,kd);
    Ad=Utilities.bandPopulate(A,kd,kd);
    Utilities.upperSymmetrice(Ad);
  }
}
