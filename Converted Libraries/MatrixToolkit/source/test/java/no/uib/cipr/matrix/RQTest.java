package no.uib.cipr.matrix;
import no.uib.cipr.matrix.DenseMatrix;
import no.uib.cipr.matrix.Matrix;
import no.uib.cipr.matrix.RQ;
/** 
 * RQ test
 */
public class RQTest extends OrthogonalTestAbstract {
  public RQTest(  String arg0){
    super(arg0);
  }
  public void testStaticFactorize(){
    assertEquals(A,RQ.factorize(A));
  }
  public void testRepeatStaticFactorize(){
    assertEquals(A,RQ.factorize(A));
    assertEquals(A,RQ.factorize(A));
  }
  public void testFactor(){
    RQ c=new RQ(A.numRows(),A.numColumns());
    assertEquals(A,c.factor(new DenseMatrix(A)));
  }
  public void testRepeatFactor(){
    RQ rq=new RQ(A.numRows(),A.numColumns());
    rq.factor(new DenseMatrix(A));
    assertEquals(A,rq);
    rq.factor(new DenseMatrix(A));
    assertEquals(A,rq);
  }
  public void testStaticFactorizeNonSquare(){
    assertEquals(Ac,RQ.factorize(Ac));
  }
  public void testRepeatStaticFactorizeNonSquare(){
    assertEquals(Ac,RQ.factorize(Ac));
    assertEquals(Ac,RQ.factorize(Ac));
  }
  public void testFactorNonSquare(){
    RQ rq=new RQ(Ac.numRows(),Ac.numColumns());
    assertEquals(Ac,rq.factor(new DenseMatrix(Ac)));
  }
  public void testRepeatFactorNonSquare(){
    RQ rq=new RQ(Ac.numRows(),Ac.numColumns());
    rq.factor(new DenseMatrix(Ac));
    assertEquals(Ac,rq);
    rq.factor(new DenseMatrix(Ac));
    assertEquals(Ac,rq);
  }
  private void assertEquals(  Matrix A,  RQ rq){
    assertEquals(A,rq.getR().mult(rq.getQ(),A.copy().zero()));
  }
}
