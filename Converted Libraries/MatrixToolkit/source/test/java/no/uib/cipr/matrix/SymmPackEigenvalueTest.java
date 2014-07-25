package no.uib.cipr.matrix;
import no.uib.cipr.matrix.LowerSymmPackMatrix;
import no.uib.cipr.matrix.NotConvergedException;
import no.uib.cipr.matrix.SymmPackEVD;
import no.uib.cipr.matrix.UpperSymmPackMatrix;
/** 
 * Test of the symmetric, dense eigenvalue solver
 */
public class SymmPackEigenvalueTest extends SymmEigenvalueTestAbstract {
  private LowerSymmPackMatrix L;
  private UpperSymmPackMatrix U;
  public SymmPackEigenvalueTest(  String arg0){
    super(arg0);
  }
  @Override protected void setUp() throws Exception {
    super.setUp();
    L=new LowerSymmPackMatrix(A);
    U=new UpperSymmPackMatrix(A);
  }
  @Override protected void tearDown() throws Exception {
    super.tearDown();
    L=null;
    U=null;
  }
  public void testLowerStaticFactorize() throws NotConvergedException {
    SymmPackEVD evd=SymmPackEVD.factorize(L);
    assertEquals(L,evd.getEigenvalues(),evd.getEigenvectors());
  }
  public void testUpperStaticFactorize() throws NotConvergedException {
    SymmPackEVD evd=SymmPackEVD.factorize(U);
    assertEquals(U,evd.getEigenvalues(),evd.getEigenvectors());
  }
  public void testLowerFactor() throws NotConvergedException {
    SymmPackEVD evd=new SymmPackEVD(A.numRows(),false);
    evd.factor(L.copy());
    assertEquals(L,evd.getEigenvalues(),evd.getEigenvectors());
  }
  public void testUpperFactor() throws NotConvergedException {
    SymmPackEVD evd=new SymmPackEVD(A.numRows(),true);
    evd.factor(U.copy());
    assertEquals(U,evd.getEigenvalues(),evd.getEigenvectors());
  }
  public void testLowerRepeatFactor() throws NotConvergedException {
    SymmPackEVD evd=new SymmPackEVD(A.numRows(),false);
    evd.factor(L.copy());
    assertEquals(L,evd.getEigenvalues(),evd.getEigenvectors());
    evd.factor(L.copy());
    assertEquals(L,evd.getEigenvalues(),evd.getEigenvectors());
  }
  public void testUpperRepeatFactor() throws NotConvergedException {
    SymmPackEVD evd=new SymmPackEVD(A.numRows(),true);
    evd.factor(U.copy());
    assertEquals(U,evd.getEigenvalues(),evd.getEigenvectors());
    evd.factor(U.copy());
    assertEquals(U,evd.getEigenvalues(),evd.getEigenvectors());
  }
}
