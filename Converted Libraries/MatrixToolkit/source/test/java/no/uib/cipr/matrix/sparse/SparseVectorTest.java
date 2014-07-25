package no.uib.cipr.matrix.sparse;
import java.util.Arrays;
import java.util.Iterator;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Vector;
import no.uib.cipr.matrix.Utilities;
import no.uib.cipr.matrix.VectorEntry;
import no.uib.cipr.matrix.VectorTestAbstract;
/** 
 * Test of SparseVector
 */
public class SparseVectorTest extends VectorTestAbstract {
  public SparseVectorTest(  String arg0){
    super(arg0);
  }
  @Override protected void createPrimary() throws Exception {
    int n=Utilities.getInt(1,max);
    int m=Math.min(Utilities.getInt(max),n);
    x=new SparseVector(n);
    xd=Utilities.populate(x,m);
  }
  public void testSparseVectorIndices(){
    SparseVector vector=new SparseVector(Integer.MAX_VALUE);
    int[] index=vector.getIndex();
    assert index != null;
    assert index.length == 0;
    double[] entries=new double[5];
    entries[0]=0.0;
    entries[1]=0.0;
    entries[2]=1.0;
    entries[3]=0.0;
    entries[4]=2.0;
    Vector dense=new DenseVector(entries,false);
    vector=new SparseVector(dense);
    index=vector.getIndex();
    assert index != null;
    assert index.length == 5 : "expected length of 5, but got " + index.length + ", with elements "+ Arrays.toString(index);
  }
  public void testBug27(){
    double[] tfVector={0.0,0.5,0.0,0.4,0.0};
    DenseVector dense=new DenseVector(tfVector,false);
    SparseVector vectorTF=new SparseVector(dense);
    vectorTF.compact();
    assertTrue(vectorTF.getUsed() == 2);
    for (Iterator<VectorEntry> it=vectorTF.iterator(); it.hasNext(); ) {
      VectorEntry ve=it.next();
      int index=ve.index();
      double value=ve.get();
      assertTrue(tfVector[index] == value);
    }
  }
}
