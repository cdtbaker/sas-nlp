package no.uib.cipr.matrix;
import no.uib.cipr.matrix.DenseMatrix;
import no.uib.cipr.matrix.LQ;
import no.uib.cipr.matrix.Matrix;
/** 
 * LQ test
 */
public class LQTest extends OrthogonalTestAbstract {
  public LQTest(  String arg0){
    super(arg0);
  }
  public void testStaticFactorize(){
    assertEquals(A,LQ.factorize(A));
  }
  public void testRepeatStaticFactorize(){
    assertEquals(A,LQ.factorize(A));
    assertEquals(A,LQ.factorize(A));
  }
  public void testFactor(){
    LQ lq=new LQ(A.numRows(),A.numColumns());
    assertEquals(A,lq.factor(new DenseMatrix(A)));
  }
  public void testRepeatFactor(){
    LQ lq=new LQ(A.numRows(),A.numColumns());
    lq.factor(new DenseMatrix(A));
    assertEquals(A,lq);
    lq.factor(new DenseMatrix(A));
    assertEquals(A,lq);
  }
  public void testStaticFactorizeNonSquare(){
    assertEquals(Ac,LQ.factorize(Ac));
  }
  public void testRepeatStaticFactorizeNonSquare(){
    assertEquals(Ac,LQ.factorize(Ac));
    assertEquals(Ac,LQ.factorize(Ac));
  }
  public void testFactorNonSquare(){
    LQ lq=new LQ(Ac.numRows(),Ac.numColumns());
    assertEquals(Ac,lq.factor(new DenseMatrix(Ac)));
  }
  public void testRepeatFactorNonSquare(){
    LQ lq=new LQ(Ac.numRows(),Ac.numColumns());
    lq.factor(new DenseMatrix(Ac));
    assertEquals(Ac,lq);
    lq.factor(new DenseMatrix(Ac));
    assertEquals(Ac,lq);
  }
  private void assertEquals(  Matrix A,  LQ lq){
    assertEquals(A,lq.getL().mult(lq.getQ(),A.copy().zero()));
  }
}
