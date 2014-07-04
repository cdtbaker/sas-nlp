package no.uib.cipr.matrix.sparse;
import no.uib.cipr.matrix.Matrix;
import no.uib.cipr.matrix.Vector;
/** 
 * Partial implementation of an iterative solver
 */
public abstract class AbstractIterativeSolver implements IterativeSolver {
  /** 
 * Preconditioner to use
 */
  protected Preconditioner M;
  /** 
 * Iteration monitor
 */
  protected IterationMonitor iter;
  /** 
 * Constructor for AbstractIterativeSolver. Does not use preconditioning,
 * and uses the default linear iteration object.
 */
  public AbstractIterativeSolver(){
    M=new IdentityPreconditioner();
    iter=new DefaultIterationMonitor();
  }
  public void setPreconditioner(  Preconditioner M){
    this.M=M;
  }
  public Preconditioner getPreconditioner(){
    return M;
  }
  public IterationMonitor getIterationMonitor(){
    return iter;
  }
  public void setIterationMonitor(  IterationMonitor iter){
    this.iter=iter;
  }
  /** 
 * Checks sizes of input data for {@link #solve(Matrix,Vector,Vector)}.
 * Throws an exception if the sizes does not match.
 */
  protected void checkSizes(  Matrix A,  Vector b,  Vector x){
    if (!A.isSquare())     throw new IllegalArgumentException("!A.isSquare()");
    if (b.size() != A.numRows())     throw new IllegalArgumentException("b.size() != A.numRows()");
    if (b.size() != x.size())     throw new IllegalArgumentException("b.size() != x.size()");
  }
  /** 
 * Identity preconditioner which does nothing
 */
private static class IdentityPreconditioner implements Preconditioner {
    public Vector apply(    Vector b,    Vector x){
      return x.set(b);
    }
    public Vector transApply(    Vector b,    Vector x){
      return x.set(b);
    }
    public void setMatrix(    Matrix A){
    }
  }
}
