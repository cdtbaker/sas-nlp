package no.uib.cipr.matrix.sparse;
import no.uib.cipr.matrix.sparse.FlexCompColMatrix;
import no.uib.cipr.matrix.Utilities;
/** 
 * Test of FlexCompColMatrix
 */
public class FlexCompColMatrixTest extends SparseMatrixTestAbstract {
  public FlexCompColMatrixTest(  String arg0){
    super(arg0);
  }
  @Override protected void createPrimary() throws Exception {
    int n=Utilities.getInt(1,max);
    int m=Utilities.getInt(1,max);
    int b=Utilities.getInt(Math.min(bmax,m));
    A=new FlexCompColMatrix(n,m);
    Ad=Utilities.columnPopulate(A,b);
  }
}
