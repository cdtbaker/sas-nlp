package no.uib.cipr.matrix;
import no.uib.cipr.matrix.TridiagMatrix;
/** 
 * Test of tridiagonal matrices
 */
public class TridiagMatrixTest extends StructImmutableMatrixTestAbstract {
  public TridiagMatrixTest(  String arg0){
    super(arg0);
  }
  @Override protected void createPrimary() throws Exception {
    int n=Utilities.getInt(1,max);
    A=new TridiagMatrix(n);
    Ad=Utilities.bandPopulate(A,1,1);
  }
  @Override public void testTransMatrixSolve(){
  }
  @Override public void testTransVectorSolve(){
  }
}
