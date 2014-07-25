package org.ojalgo.matrix.decomposition.task;
import java.math.BigDecimal;
import org.ojalgo.access.Access2D;
import org.ojalgo.matrix.MatrixUtils;
import org.ojalgo.matrix.decomposition.CholeskyDecomposition;
import org.ojalgo.matrix.decomposition.DecompositionStore;
import org.ojalgo.matrix.decomposition.LUDecomposition;
import org.ojalgo.matrix.decomposition.QRDecomposition;
import org.ojalgo.matrix.decomposition.SingularValueDecomposition;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.scalar.ComplexNumber;
public interface SolverTask<N extends Number> extends DecompositionTask<N> {
public static abstract class Factory<N extends Number> {
    public final SolverTask<N> make(    final MatrixStore<N> templateBody,    final MatrixStore<N> templateRhs){
      return this.make(templateBody,templateRhs,MatrixUtils.isHermitian(templateBody));
    }
    public abstract SolverTask<N> make(    MatrixStore<N> templateBody,    MatrixStore<N> templateRhs,    boolean symmetric);
  }
  public static final Factory<BigDecimal> BIG=new Factory<BigDecimal>(){
    @Override public SolverTask<BigDecimal> make(    final MatrixStore<BigDecimal> templateBody,    final MatrixStore<BigDecimal> templateRhs,    final boolean symmetric){
      if (symmetric) {
        return CholeskyDecomposition.make(templateBody);
      }
 else       if (templateBody.countRows() == templateBody.countColumns()) {
        return LUDecomposition.make(templateBody);
      }
 else       if (templateBody.countRows() >= templateBody.countColumns()) {
        return QRDecomposition.make(templateBody);
      }
 else {
        return SingularValueDecomposition.make(templateBody);
      }
    }
  }
;
  public static final Factory<ComplexNumber> COMPLEX=new Factory<ComplexNumber>(){
    @Override public SolverTask<ComplexNumber> make(    final MatrixStore<ComplexNumber> templateBody,    final MatrixStore<ComplexNumber> templateRhs,    final boolean symmetric){
      if (symmetric) {
        return CholeskyDecomposition.make(templateBody);
      }
 else       if (templateBody.countRows() == templateBody.countColumns()) {
        return LUDecomposition.make(templateBody);
      }
 else       if (templateBody.countRows() >= templateBody.countColumns()) {
        return QRDecomposition.make(templateBody);
      }
 else {
        return SingularValueDecomposition.make(templateBody);
      }
    }
  }
;
  public static final Factory<Double> PRIMITIVE=new Factory<Double>(){
    @Override public SolverTask<Double> make(    final MatrixStore<Double> templateBody,    final MatrixStore<Double> templateRhs,    final boolean symmetric){
      if (symmetric) {
        final long tmpDim=templateBody.countColumns();
        if (tmpDim == 1l) {
          return AbstractSolver.FULL_1X1;
        }
 else         if (tmpDim == 2l) {
          return AbstractSolver.SYMMETRIC_2X2;
        }
 else         if (tmpDim == 3l) {
          return AbstractSolver.SYMMETRIC_3X3;
        }
 else         if (tmpDim == 4l) {
          return AbstractSolver.SYMMETRIC_4X4;
        }
 else         if (tmpDim == 5l) {
          return AbstractSolver.SYMMETRIC_5X5;
        }
 else {
          return CholeskyDecomposition.make(templateBody);
        }
      }
 else {
        final long tmpDim=templateBody.countColumns();
        if (templateBody.countRows() == tmpDim) {
          if (tmpDim == 1l) {
            return AbstractSolver.FULL_1X1;
          }
 else           if (tmpDim == 2l) {
            return AbstractSolver.FULL_2X2;
          }
 else           if (tmpDim == 3l) {
            return AbstractSolver.FULL_3X3;
          }
 else           if (tmpDim == 4l) {
            return AbstractSolver.FULL_4X4;
          }
 else           if (tmpDim == 5l) {
            return AbstractSolver.FULL_5X5;
          }
 else {
            return LUDecomposition.make(templateBody);
          }
        }
 else         if (templateBody.countRows() >= tmpDim) {
          if (tmpDim <= 5) {
            return AbstractSolver.LEAST_SQUARES;
          }
 else {
            return QRDecomposition.make(templateBody);
          }
        }
 else {
          return SingularValueDecomposition.make(templateBody);
        }
      }
    }
  }
;
  /** 
 * <p>
 * Implementiong this method is optional.
 * </p>
 * Will create a {@linkplain DecompositionStore} instance suitable for use with{@link #solve(Access2D,DecompositionStore)}. When solving an equation system [A][X]=[B] ([mxn][nxb]=[mxb]) the
 * preallocated memory/matrix will typically be either mxb or nxb (if A is square then there is no doubt).
 * @param templateBody
 * @param templateRHS
 * @return
 * @throws UnsupportedOperationException When/if this feature is not implemented
 */
  DecompositionStore<N> preallocate(  Access2D<N> templateBody,  Access2D<N> templateRHS);
  /** 
 * [A][X]=[B] or [this][return]=[aRHS]
 */
  MatrixStore<N> solve(  Access2D<N> body,  Access2D<N> rhs) throws TaskException ;
  /** 
 * <p>
 * Implementiong this method is optional.
 * </p>
 * <p>
 * Exactly how a specific implementation makes use of <code>preallocated</code> is not specified by this interface.
 * It must be documented for each implementation.
 * </p>
 * <p>
 * Should produce the same results as calling {@link #solve(Access2D)}.
 * </p>
 * @param rhs The Right Hand Side, wont be modfied
 * @param preallocated Preallocated memory for the results, possibly some intermediate results. You must assume this
 * is modified, but you cannot assume it will contain the full/final/correct solution.
 * @return The solution
 * @throws UnsupportedOperationException When/if this feature is not implemented
 */
  MatrixStore<N> solve(  Access2D<N> body,  Access2D<N> rhs,  DecompositionStore<N> preallocated) throws TaskException ;
}
