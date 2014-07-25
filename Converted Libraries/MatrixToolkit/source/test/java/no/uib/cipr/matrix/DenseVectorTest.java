package no.uib.cipr.matrix;
import no.uib.cipr.matrix.DenseVector;
/** 
 * Test of DenseVector
 */
public class DenseVectorTest extends VectorTestAbstract {
  public DenseVectorTest(  String arg0){
    super(arg0);
  }
  @Override protected void createPrimary() throws Exception {
    int n=Utilities.getInt(1,max);
    x=new DenseVector(n);
    xd=Utilities.populate(x);
  }
}
