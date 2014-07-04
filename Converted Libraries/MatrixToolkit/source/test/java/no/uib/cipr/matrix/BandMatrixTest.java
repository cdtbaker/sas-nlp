package no.uib.cipr.matrix;
import no.uib.cipr.matrix.BandMatrix;
/** 
 * Test of BandMatrix
 */
public class BandMatrixTest extends StructImmutableMatrixTestAbstract {
  public BandMatrixTest(  String arg0){
    super(arg0);
  }
  @Override protected void createPrimary() throws Exception {
    int n=Utilities.getInt(1,max);
    int kl=Math.min(Utilities.getInt(max),Math.max(n - 1,0));
    int ku=Math.min(Utilities.getInt(max),Math.max(n - 1,0));
    A=new BandMatrix(n,kl,ku);
    Ad=Utilities.bandPopulate(A,kl,ku);
  }
  @Override public void testTransposeInplace(){
    BandMatrix B=(BandMatrix)A;
    if (B.numSubDiagonals() == B.numSuperDiagonals())     super.testTransposeInplace();
  }
  @Override public void testTransMatrixSolve(){
  }
  @Override public void testTransVectorSolve(){
  }
}
