package no.uib.cipr.matrix.sparse;
import no.uib.cipr.matrix.sparse.FlexCompRowMatrix;
import no.uib.cipr.matrix.Utilities;
/** 
 * Test of FlexCompRowMatrix
 */
public class FlexCompRowMatrixTest extends SparseMatrixTestAbstract {
  public FlexCompRowMatrixTest(  String arg0){
    super(arg0);
  }
  @Override protected void createPrimary() throws Exception {
    int n=Utilities.getInt(1,max);
    int m=Utilities.getInt(1,max);
    int b=Utilities.getInt(Math.min(bmax,m));
    A=new FlexCompRowMatrix(n,m);
    Ad=Utilities.rowPopulate(A,b);
  }
}
