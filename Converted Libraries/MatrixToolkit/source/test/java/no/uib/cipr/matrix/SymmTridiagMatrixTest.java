package no.uib.cipr.matrix;
import no.uib.cipr.matrix.SymmTridiagMatrix;
/** 
 * Test of SymmTridiagMatrix
 */
public class SymmTridiagMatrixTest extends StructImmutableMatrixTestAbstract {
  public SymmTridiagMatrixTest(  String arg0){
    super(arg0);
  }
  @Override protected void createPrimary() throws Exception {
    int n=Utilities.getInt(1,max);
    A=new SymmTridiagMatrix(n);
    Ad=Utilities.bandPopulate(A,1,1);
    Utilities.lowerSymmetrice(Ad);
  }
}
