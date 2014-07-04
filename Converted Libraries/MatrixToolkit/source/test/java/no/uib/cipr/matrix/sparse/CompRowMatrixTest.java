package no.uib.cipr.matrix.sparse;
import no.uib.cipr.matrix.sparse.CompRowMatrix;
import no.uib.cipr.matrix.Utilities;
/** 
 * Test of CompRowMatrix
 */
public class CompRowMatrixTest extends SparseStructImmutableMatrixTestAbstract {
  public CompRowMatrixTest(  String arg0){
    super(arg0);
  }
  @Override protected void createPrimary() throws Exception {
    int n=Utilities.getInt(1,max);
    int m=Utilities.getInt(1,max);
    int b=Utilities.getInt(Math.min(bmax,m));
    int[][] nz=Utilities.getRowPattern(n,m,b);
    A=new CompRowMatrix(n,m,nz);
    Ad=Utilities.rowPopulate(A,nz);
  }
}
