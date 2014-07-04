package no.uib.cipr.matrix;
import no.uib.cipr.matrix.SPDTridiagMatrix;
/** 
 * Test of symmetrical, positive definite tridiagonal matrices
 */
public class SPDTridiagMatrixTest extends StructImmutableMatrixTestAbstract {
  public SPDTridiagMatrixTest(  String arg0){
    super(arg0);
  }
  @Override protected void createPrimary() throws Exception {
    int n=Utilities.getInt(1,max);
    A=new SPDTridiagMatrix(n);
    Ad=Utilities.bandPopulate(A,1,1);
    Utilities.lowerSymmetrice(Ad);
  }
}
