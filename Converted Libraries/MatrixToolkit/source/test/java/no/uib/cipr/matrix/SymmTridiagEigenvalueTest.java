package no.uib.cipr.matrix;
import no.uib.cipr.matrix.Matrices;
import no.uib.cipr.matrix.NotConvergedException;
import no.uib.cipr.matrix.SymmTridiagEVD;
import no.uib.cipr.matrix.SymmTridiagMatrix;
/** 
 * Test of the symmetric, tridiagonal eigenvalue solver
 */
public class SymmTridiagEigenvalueTest extends SymmEigenvalueTestAbstract {
  private SymmTridiagMatrix T;
  private final int max=100;
  public SymmTridiagEigenvalueTest(  String arg0){
    super(arg0);
  }
  @Override protected void setUp() throws Exception {
    int n=Utilities.getInt(1,max);
    A=Matrices.random(n,n);
    T=new SymmTridiagMatrix(A);
  }
  public void testStaticFactorize() throws NotConvergedException {
    SymmTridiagEVD evd=SymmTridiagEVD.factorize(A);
    assertEquals(T,evd.getEigenvalues(),evd.getEigenvectors());
  }
  public void testFactor() throws NotConvergedException {
    SymmTridiagEVD evd=new SymmTridiagEVD(A.numRows());
    evd.factor(T.copy());
    assertEquals(T,evd.getEigenvalues(),evd.getEigenvectors());
  }
  public void testRepeatFactor() throws NotConvergedException {
    SymmTridiagEVD evd=new SymmTridiagEVD(A.numRows());
    evd.factor(T.copy());
    assertEquals(T,evd.getEigenvalues(),evd.getEigenvectors());
    evd.factor(T.copy());
    assertEquals(T,evd.getEigenvalues(),evd.getEigenvectors());
  }
}
