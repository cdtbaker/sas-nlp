package org.apache.commons.math3.optimization.general;
import org.apache.commons.math3.exception.ConvergenceException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.MathInternalError;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.BlockRealMatrix;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.QRDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.SingularMatrixException;
import org.apache.commons.math3.optimization.ConvergenceChecker;
import org.apache.commons.math3.optimization.SimpleVectorValueChecker;
import org.apache.commons.math3.optimization.PointVectorValuePair;
/** 
 * Gauss-Newton least-squares solver.
 * <p>
 * This class solve a least-square problem by solving the normal equations
 * of the linearized problem at each iteration. Either LU decomposition or
 * QR decomposition can be used to solve the normal equations. LU decomposition
 * is faster but QR decomposition is more robust for difficult problems.
 * </p>
 * @version $Id: GaussNewtonOptimizer.java 1423687 2012-12-18 21:56:18Z erans $
 * @deprecated As of 3.1 (to be removed in 4.0).
 * @since 2.0
 */
@Deprecated public class GaussNewtonOptimizer extends AbstractLeastSquaresOptimizer {
  /** 
 * Indicator for using LU decomposition. 
 */
  private final boolean useLU;
  /** 
 * Simple constructor with default settings.
 * The normal equations will be solved using LU decomposition and the
 * convergence check is set to a {@link SimpleVectorValueChecker}with default tolerances.
 * @deprecated See {@link SimpleVectorValueChecker#SimpleVectorValueChecker()}
 */
  @Deprecated public GaussNewtonOptimizer(){
    this(true);
  }
  /** 
 * Simple constructor with default settings.
 * The normal equations will be solved using LU decomposition.
 * @param checker Convergence checker.
 */
  public GaussNewtonOptimizer(  ConvergenceChecker<PointVectorValuePair> checker){
    this(true,checker);
  }
  /** 
 * Simple constructor with default settings.
 * The convergence check is set to a {@link SimpleVectorValueChecker}with default tolerances.
 * @param useLU If {@code true}, the normal equations will be solved
 * using LU decomposition, otherwise they will be solved using QR
 * decomposition.
 * @deprecated See {@link SimpleVectorValueChecker#SimpleVectorValueChecker()}
 */
  @Deprecated public GaussNewtonOptimizer(  final boolean useLU){
    this(useLU,new SimpleVectorValueChecker());
  }
  /** 
 * @param useLU If {@code true}, the normal equations will be solved
 * using LU decomposition, otherwise they will be solved using QR
 * decomposition.
 * @param checker Convergence checker.
 */
  public GaussNewtonOptimizer(  final boolean useLU,  ConvergenceChecker<PointVectorValuePair> checker){
    super(checker);
    this.useLU=useLU;
  }
  /** 
 * {@inheritDoc} 
 */
  @Override public PointVectorValuePair doOptimize(){
    final ConvergenceChecker<PointVectorValuePair> checker=getConvergenceChecker();
    if (checker == null) {
      throw new NullArgumentException();
    }
    final double[] targetValues=getTarget();
    final int nR=targetValues.length;
    final RealMatrix weightMatrix=getWeight();
    final double[] residualsWeights=new double[nR];
    for (int i=0; i < nR; i++) {
      residualsWeights[i]=weightMatrix.getEntry(i,i);
    }
    final double[] currentPoint=getStartPoint();
    final int nC=currentPoint.length;
    PointVectorValuePair current=null;
    int iter=0;
    for (boolean converged=false; !converged; ) {
      ++iter;
      PointVectorValuePair previous=current;
      final double[] currentObjective=computeObjectiveValue(currentPoint);
      final double[] currentResiduals=computeResiduals(currentObjective);
      final RealMatrix weightedJacobian=computeWeightedJacobian(currentPoint);
      current=new PointVectorValuePair(currentPoint,currentObjective);
      final double[] b=new double[nC];
      final double[][] a=new double[nC][nC];
      for (int i=0; i < nR; ++i) {
        final double[] grad=weightedJacobian.getRow(i);
        final double weight=residualsWeights[i];
        final double residual=currentResiduals[i];
        final double wr=weight * residual;
        for (int j=0; j < nC; ++j) {
          b[j]+=wr * grad[j];
        }
        for (int k=0; k < nC; ++k) {
          double[] ak=a[k];
          double wgk=weight * grad[k];
          for (int l=0; l < nC; ++l) {
            ak[l]+=wgk * grad[l];
          }
        }
      }
      try {
        RealMatrix mA=new BlockRealMatrix(a);
        DecompositionSolver solver=useLU ? new LUDecomposition(mA).getSolver() : new QRDecomposition(mA).getSolver();
        final double[] dX=solver.solve(new ArrayRealVector(b,false)).toArray();
        for (int i=0; i < nC; ++i) {
          currentPoint[i]+=dX[i];
        }
      }
 catch (      SingularMatrixException e) {
        throw new ConvergenceException(LocalizedFormats.UNABLE_TO_SOLVE_SINGULAR_PROBLEM);
      }
      if (previous != null) {
        converged=checker.converged(iter,previous,current);
        if (converged) {
          cost=computeCost(currentResiduals);
          point=current.getPoint();
          return current;
        }
      }
    }
    throw new MathInternalError();
  }
}