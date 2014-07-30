package org.apache.commons.math3.optim.nonlinear.vector.jacobian;
import java.util.Arrays;
import org.apache.commons.math3.exception.ConvergenceException;
import org.apache.commons.math3.exception.MathUnsupportedOperationException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.optim.PointVectorValuePair;
import org.apache.commons.math3.optim.ConvergenceChecker;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.util.Precision;
import org.apache.commons.math3.util.FastMath;
/** 
 * This class solves a least-squares problem using the Levenberg-Marquardt
 * algorithm.
 * <br/>
 * Constraints are not supported: the call to{@link #optimize(OptimizationData[]) optimize} will throw{@link MathUnsupportedOperationException} if bounds are passed to it.
 * <p>This implementation <em>should</em> work even for over-determined systems
 * (i.e. systems having more point than equations). Over-determined systems
 * are solved by ignoring the point which have the smallest impact according
 * to their jacobian column norm. Only the rank of the matrix and some loop bounds
 * are changed to implement this.</p>
 * <p>The resolution engine is a simple translation of the MINPACK <a
 * href="http://www.netlib.org/minpack/lmder.f">lmder</a> routine with minor
 * changes. The changes include the over-determined resolution, the use of
 * inherited convergence checker and the Q.R. decomposition which has been
 * rewritten following the algorithm described in the
 * P. Lascaux and R. Theodor book <i>Analyse num&eacute;rique matricielle
 * appliqu&eacute;e &agrave; l'art de l'ing&eacute;nieur</i>, Masson 1986.</p>
 * <p>The authors of the original fortran version are:
 * <ul>
 * <li>Argonne National Laboratory. MINPACK project. March 1980</li>
 * <li>Burton S. Garbow</li>
 * <li>Kenneth E. Hillstrom</li>
 * <li>Jorge J. More</li>
 * </ul>
 * The redistribution policy for MINPACK is available <a
 * href="http://www.netlib.org/minpack/disclaimer">here</a>, for convenience, it
 * is reproduced below.</p>
 * <table border="0" width="80%" cellpadding="10" align="center" bgcolor="#E0E0E0">
 * <tr><td>
 * Minpack Copyright Notice (1999) University of Chicago.
 * All rights reserved
 * </td></tr>
 * <tr><td>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * <ol>
 * <li>Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.</li>
 * <li>Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution.</li>
 * <li>The end-user documentation included with the redistribution, if any,
 * must include the following acknowledgment:
 * <code>This product includes software developed by the University of
 * Chicago, as Operator of Argonne National Laboratory.</code>
 * Alternately, this acknowledgment may appear in the software itself,
 * if and wherever such third-party acknowledgments normally appear.</li>
 * <li><strong>WARRANTY DISCLAIMER. THE SOFTWARE IS SUPPLIED "AS IS"
 * WITHOUT WARRANTY OF ANY KIND. THE COPYRIGHT HOLDER, THE
 * UNITED STATES, THE UNITED STATES DEPARTMENT OF ENERGY, AND
 * THEIR EMPLOYEES: (1) DISCLAIM ANY WARRANTIES, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO ANY IMPLIED WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, TITLE
 * OR NON-INFRINGEMENT, (2) DO NOT ASSUME ANY LEGAL LIABILITY
 * OR RESPONSIBILITY FOR THE ACCURACY, COMPLETENESS, OR
 * USEFULNESS OF THE SOFTWARE, (3) DO NOT REPRESENT THAT USE OF
 * THE SOFTWARE WOULD NOT INFRINGE PRIVATELY OWNED RIGHTS, (4)
 * DO NOT WARRANT THAT THE SOFTWARE WILL FUNCTION
 * UNINTERRUPTED, THAT IT IS ERROR-FREE OR THAT ANY ERRORS WILL
 * BE CORRECTED.</strong></li>
 * <li><strong>LIMITATION OF LIABILITY. IN NO EVENT WILL THE COPYRIGHT
 * HOLDER, THE UNITED STATES, THE UNITED STATES DEPARTMENT OF
 * ENERGY, OR THEIR EMPLOYEES: BE LIABLE FOR ANY INDIRECT,
 * INCIDENTAL, CONSEQUENTIAL, SPECIAL OR PUNITIVE DAMAGES OF
 * ANY KIND OR NATURE, INCLUDING BUT NOT LIMITED TO LOSS OF
 * PROFITS OR LOSS OF DATA, FOR ANY REASON WHATSOEVER, WHETHER
 * SUCH LIABILITY IS ASSERTED ON THE BASIS OF CONTRACT, TORT
 * (INCLUDING NEGLIGENCE OR STRICT LIABILITY), OR OTHERWISE,
 * EVEN IF ANY OF SAID PARTIES HAS BEEN WARNED OF THE
 * POSSIBILITY OF SUCH LOSS OR DAMAGES.</strong></li>
 * <ol></td></tr>
 * </table>
 * @version $Id: LevenbergMarquardtOptimizer.java 1462503 2013-03-29 15:48:27Z luc $
 * @since 2.0
 */
public class LevenbergMarquardtOptimizer extends AbstractLeastSquaresOptimizer {
  /** 
 * Twice the "epsilon machine". 
 */
  private static final double TWO_EPS=2 * Precision.EPSILON;
  /** 
 * Number of solved point. 
 */
  private int solvedCols;
  /** 
 * Diagonal elements of the R matrix in the Q.R. decomposition. 
 */
  private double[] diagR;
  /** 
 * Norms of the columns of the jacobian matrix. 
 */
  private double[] jacNorm;
  /** 
 * Coefficients of the Householder transforms vectors. 
 */
  private double[] beta;
  /** 
 * Columns permutation array. 
 */
  private int[] permutation;
  /** 
 * Rank of the jacobian matrix. 
 */
  private int rank;
  /** 
 * Levenberg-Marquardt parameter. 
 */
  private double lmPar;
  /** 
 * Parameters evolution direction associated with lmPar. 
 */
  private double[] lmDir;
  /** 
 * Positive input variable used in determining the initial step bound. 
 */
  private final double initialStepBoundFactor;
  /** 
 * Desired relative error in the sum of squares. 
 */
  private final double costRelativeTolerance;
  /** 
 * Desired relative error in the approximate solution parameters. 
 */
  private final double parRelativeTolerance;
  /** 
 * Desired max cosine on the orthogonality between the function vector
 * and the columns of the jacobian. 
 */
  private final double orthoTolerance;
  /** 
 * Threshold for QR ranking. 
 */
  private final double qrRankingThreshold;
  /** 
 * Weighted residuals. 
 */
  private double[] weightedResidual;
  /** 
 * Weighted Jacobian. 
 */
  private double[][] weightedJacobian;
  /** 
 * Build an optimizer for least squares problems with default values
 * for all the tuning parameters (see the {@link #LevenbergMarquardtOptimizer(double,double,double,double,double)other contructor}.
 * The default values for the algorithm settings are:
 * <ul>
 * <li>Initial step bound factor: 100</li>
 * <li>Cost relative tolerance: 1e-10</li>
 * <li>Parameters relative tolerance: 1e-10</li>
 * <li>Orthogonality tolerance: 1e-10</li>
 * <li>QR ranking threshold: {@link Precision#SAFE_MIN}</li>
 * </ul>
 */
  public LevenbergMarquardtOptimizer(){
    this(100,1e-10,1e-10,1e-10,Precision.SAFE_MIN);
  }
  /** 
 * Constructor that allows the specification of a custom convergence
 * checker.
 * Note that all the usual convergence checks will be <em>disabled</em>.
 * The default values for the algorithm settings are:
 * <ul>
 * <li>Initial step bound factor: 100</li>
 * <li>Cost relative tolerance: 1e-10</li>
 * <li>Parameters relative tolerance: 1e-10</li>
 * <li>Orthogonality tolerance: 1e-10</li>
 * <li>QR ranking threshold: {@link Precision#SAFE_MIN}</li>
 * </ul>
 * @param checker Convergence checker.
 */
  public LevenbergMarquardtOptimizer(  ConvergenceChecker<PointVectorValuePair> checker){
    this(100,checker,1e-10,1e-10,1e-10,Precision.SAFE_MIN);
  }
  /** 
 * Constructor that allows the specification of a custom convergence
 * checker, in addition to the standard ones.
 * @param initialStepBoundFactor Positive input variable used in
 * determining the initial step bound. This bound is set to the
 * product of initialStepBoundFactor and the euclidean norm of{@code diag * x} if non-zero, or else to {@code initialStepBoundFactor}itself. In most cases factor should lie in the interval{@code (0.1, 100.0)}. {@code 100} is a generally recommended value.
 * @param checker Convergence checker.
 * @param costRelativeTolerance Desired relative error in the sum of
 * squares.
 * @param parRelativeTolerance Desired relative error in the approximate
 * solution parameters.
 * @param orthoTolerance Desired max cosine on the orthogonality between
 * the function vector and the columns of the Jacobian.
 * @param threshold Desired threshold for QR ranking. If the squared norm
 * of a column vector is smaller or equal to this threshold during QR
 * decomposition, it is considered to be a zero vector and hence the rank
 * of the matrix is reduced.
 */
  public LevenbergMarquardtOptimizer(  double initialStepBoundFactor,  ConvergenceChecker<PointVectorValuePair> checker,  double costRelativeTolerance,  double parRelativeTolerance,  double orthoTolerance,  double threshold){
    super(checker);
    this.initialStepBoundFactor=initialStepBoundFactor;
    this.costRelativeTolerance=costRelativeTolerance;
    this.parRelativeTolerance=parRelativeTolerance;
    this.orthoTolerance=orthoTolerance;
    this.qrRankingThreshold=threshold;
  }
  /** 
 * Build an optimizer for least squares problems with default values
 * for some of the tuning parameters (see the {@link #LevenbergMarquardtOptimizer(double,double,double,double,double)other contructor}.
 * The default values for the algorithm settings are:
 * <ul>
 * <li>Initial step bound factor}: 100</li>
 * <li>QR ranking threshold}: {@link Precision#SAFE_MIN}</li>
 * </ul>
 * @param costRelativeTolerance Desired relative error in the sum of
 * squares.
 * @param parRelativeTolerance Desired relative error in the approximate
 * solution parameters.
 * @param orthoTolerance Desired max cosine on the orthogonality between
 * the function vector and the columns of the Jacobian.
 */
  public LevenbergMarquardtOptimizer(  double costRelativeTolerance,  double parRelativeTolerance,  double orthoTolerance){
    this(100,costRelativeTolerance,parRelativeTolerance,orthoTolerance,Precision.SAFE_MIN);
  }
  /** 
 * The arguments control the behaviour of the default convergence checking
 * procedure.
 * Additional criteria can defined through the setting of a {@link ConvergenceChecker}.
 * @param initialStepBoundFactor Positive input variable used in
 * determining the initial step bound. This bound is set to the
 * product of initialStepBoundFactor and the euclidean norm of{@code diag * x} if non-zero, or else to {@code initialStepBoundFactor}itself. In most cases factor should lie in the interval{@code (0.1, 100.0)}. {@code 100} is a generally recommended value.
 * @param costRelativeTolerance Desired relative error in the sum of
 * squares.
 * @param parRelativeTolerance Desired relative error in the approximate
 * solution parameters.
 * @param orthoTolerance Desired max cosine on the orthogonality between
 * the function vector and the columns of the Jacobian.
 * @param threshold Desired threshold for QR ranking. If the squared norm
 * of a column vector is smaller or equal to this threshold during QR
 * decomposition, it is considered to be a zero vector and hence the rank
 * of the matrix is reduced.
 */
  public LevenbergMarquardtOptimizer(  double initialStepBoundFactor,  double costRelativeTolerance,  double parRelativeTolerance,  double orthoTolerance,  double threshold){
    super(null);
    this.initialStepBoundFactor=initialStepBoundFactor;
    this.costRelativeTolerance=costRelativeTolerance;
    this.parRelativeTolerance=parRelativeTolerance;
    this.orthoTolerance=orthoTolerance;
    this.qrRankingThreshold=threshold;
  }
  /** 
 * {@inheritDoc} 
 */
  @Override protected PointVectorValuePair doOptimize(){
    checkParameters();
    final int nR=getTarget().length;
    final double[] currentPoint=getStartPoint();
    final int nC=currentPoint.length;
    solvedCols=FastMath.min(nR,nC);
    diagR=new double[nC];
    jacNorm=new double[nC];
    beta=new double[nC];
    permutation=new int[nC];
    lmDir=new double[nC];
    double delta=0;
    double xNorm=0;
    double[] diag=new double[nC];
    double[] oldX=new double[nC];
    double[] oldRes=new double[nR];
    double[] oldObj=new double[nR];
    double[] qtf=new double[nR];
    double[] work1=new double[nC];
    double[] work2=new double[nC];
    double[] work3=new double[nC];
    final RealMatrix weightMatrixSqrt=getWeightSquareRoot();
    double[] currentObjective=computeObjectiveValue(currentPoint);
    double[] currentResiduals=computeResiduals(currentObjective);
    PointVectorValuePair current=new PointVectorValuePair(currentPoint,currentObjective);
    double currentCost=computeCost(currentResiduals);
    lmPar=0;
    boolean firstIteration=true;
    final ConvergenceChecker<PointVectorValuePair> checker=getConvergenceChecker();
    while (true) {
      incrementIterationCount();
      final PointVectorValuePair previous=current;
      qrDecomposition(computeWeightedJacobian(currentPoint));
      weightedResidual=weightMatrixSqrt.operate(currentResiduals);
      for (int i=0; i < nR; i++) {
        qtf[i]=weightedResidual[i];
      }
      qTy(qtf);
      for (int k=0; k < solvedCols; ++k) {
        int pk=permutation[k];
        weightedJacobian[k][pk]=diagR[pk];
      }
      if (firstIteration) {
        xNorm=0;
        for (int k=0; k < nC; ++k) {
          double dk=jacNorm[k];
          if (dk == 0) {
            dk=1.0;
          }
          double xk=dk * currentPoint[k];
          xNorm+=xk * xk;
          diag[k]=dk;
        }
        xNorm=FastMath.sqrt(xNorm);
        delta=(xNorm == 0) ? initialStepBoundFactor : (initialStepBoundFactor * xNorm);
      }
      double maxCosine=0;
      if (currentCost != 0) {
        for (int j=0; j < solvedCols; ++j) {
          int pj=permutation[j];
          double s=jacNorm[pj];
          if (s != 0) {
            double sum=0;
            for (int i=0; i <= j; ++i) {
              sum+=weightedJacobian[i][pj] * qtf[i];
            }
            maxCosine=FastMath.max(maxCosine,FastMath.abs(sum) / (s * currentCost));
          }
        }
      }
      if (maxCosine <= orthoTolerance) {
        setCost(currentCost);
        return current;
      }
      for (int j=0; j < nC; ++j) {
        diag[j]=FastMath.max(diag[j],jacNorm[j]);
      }
      for (double ratio=0; ratio < 1.0e-4; ) {
        for (int j=0; j < solvedCols; ++j) {
          int pj=permutation[j];
          oldX[pj]=currentPoint[pj];
        }
        final double previousCost=currentCost;
        double[] tmpVec=weightedResidual;
        weightedResidual=oldRes;
        oldRes=tmpVec;
        tmpVec=currentObjective;
        currentObjective=oldObj;
        oldObj=tmpVec;
        determineLMParameter(qtf,delta,diag,work1,work2,work3);
        double lmNorm=0;
        for (int j=0; j < solvedCols; ++j) {
          int pj=permutation[j];
          lmDir[pj]=-lmDir[pj];
          currentPoint[pj]=oldX[pj] + lmDir[pj];
          double s=diag[pj] * lmDir[pj];
          lmNorm+=s * s;
        }
        lmNorm=FastMath.sqrt(lmNorm);
        if (firstIteration) {
          delta=FastMath.min(delta,lmNorm);
        }
        currentObjective=computeObjectiveValue(currentPoint);
        currentResiduals=computeResiduals(currentObjective);
        current=new PointVectorValuePair(currentPoint,currentObjective);
        currentCost=computeCost(currentResiduals);
        double actRed=-1.0;
        if (0.1 * currentCost < previousCost) {
          double r=currentCost / previousCost;
          actRed=1.0 - r * r;
        }
        for (int j=0; j < solvedCols; ++j) {
          int pj=permutation[j];
          double dirJ=lmDir[pj];
          work1[j]=0;
          for (int i=0; i <= j; ++i) {
            work1[i]+=weightedJacobian[i][pj] * dirJ;
          }
        }
        double coeff1=0;
        for (int j=0; j < solvedCols; ++j) {
          coeff1+=work1[j] * work1[j];
        }
        double pc2=previousCost * previousCost;
        coeff1=coeff1 / pc2;
        double coeff2=lmPar * lmNorm * lmNorm / pc2;
        double preRed=coeff1 + 2 * coeff2;
        double dirDer=-(coeff1 + coeff2);
        ratio=(preRed == 0) ? 0 : (actRed / preRed);
        if (ratio <= 0.25) {
          double tmp=(actRed < 0) ? (0.5 * dirDer / (dirDer + 0.5 * actRed)) : 0.5;
          if ((0.1 * currentCost >= previousCost) || (tmp < 0.1)) {
            tmp=0.1;
          }
          delta=tmp * FastMath.min(delta,10.0 * lmNorm);
          lmPar/=tmp;
        }
 else         if ((lmPar == 0) || (ratio >= 0.75)) {
          delta=2 * lmNorm;
          lmPar*=0.5;
        }
        if (ratio >= 1.0e-4) {
          firstIteration=false;
          xNorm=0;
          for (int k=0; k < nC; ++k) {
            double xK=diag[k] * currentPoint[k];
            xNorm+=xK * xK;
          }
          xNorm=FastMath.sqrt(xNorm);
          if (checker != null && checker.converged(getIterations(),previous,current)) {
            setCost(currentCost);
            return current;
          }
        }
 else {
          currentCost=previousCost;
          for (int j=0; j < solvedCols; ++j) {
            int pj=permutation[j];
            currentPoint[pj]=oldX[pj];
          }
          tmpVec=weightedResidual;
          weightedResidual=oldRes;
          oldRes=tmpVec;
          tmpVec=currentObjective;
          currentObjective=oldObj;
          oldObj=tmpVec;
          current=new PointVectorValuePair(currentPoint,currentObjective);
        }
        if ((FastMath.abs(actRed) <= costRelativeTolerance && preRed <= costRelativeTolerance && ratio <= 2.0) || delta <= parRelativeTolerance * xNorm) {
          setCost(currentCost);
          return current;
        }
        if (FastMath.abs(actRed) <= TWO_EPS && preRed <= TWO_EPS && ratio <= 2.0) {
          throw new ConvergenceException(LocalizedFormats.TOO_SMALL_COST_RELATIVE_TOLERANCE,costRelativeTolerance);
        }
 else         if (delta <= TWO_EPS * xNorm) {
          throw new ConvergenceException(LocalizedFormats.TOO_SMALL_PARAMETERS_RELATIVE_TOLERANCE,parRelativeTolerance);
        }
 else         if (maxCosine <= TWO_EPS) {
          throw new ConvergenceException(LocalizedFormats.TOO_SMALL_ORTHOGONALITY_TOLERANCE,orthoTolerance);
        }
      }
    }
  }
  /** 
 * Determine the Levenberg-Marquardt parameter.
 * <p>This implementation is a translation in Java of the MINPACK
 * <a href="http://www.netlib.org/minpack/lmpar.f">lmpar</a>
 * routine.</p>
 * <p>This method sets the lmPar and lmDir attributes.</p>
 * <p>The authors of the original fortran function are:</p>
 * <ul>
 * <li>Argonne National Laboratory. MINPACK project. March 1980</li>
 * <li>Burton  S. Garbow</li>
 * <li>Kenneth E. Hillstrom</li>
 * <li>Jorge   J. More</li>
 * </ul>
 * <p>Luc Maisonobe did the Java translation.</p>
 * @param qy array containing qTy
 * @param delta upper bound on the euclidean norm of diagR * lmDir
 * @param diag diagonal matrix
 * @param work1 work array
 * @param work2 work array
 * @param work3 work array
 */
  private void determineLMParameter(  double[] qy,  double delta,  double[] diag,  double[] work1,  double[] work2,  double[] work3){
    final int nC=weightedJacobian[0].length;
    for (int j=0; j < rank; ++j) {
      lmDir[permutation[j]]=qy[j];
    }
    for (int j=rank; j < nC; ++j) {
      lmDir[permutation[j]]=0;
    }
    for (int k=rank - 1; k >= 0; --k) {
      int pk=permutation[k];
      double ypk=lmDir[pk] / diagR[pk];
      for (int i=0; i < k; ++i) {
        lmDir[permutation[i]]-=ypk * weightedJacobian[i][pk];
      }
      lmDir[pk]=ypk;
    }
    double dxNorm=0;
    for (int j=0; j < solvedCols; ++j) {
      int pj=permutation[j];
      double s=diag[pj] * lmDir[pj];
      work1[pj]=s;
      dxNorm+=s * s;
    }
    dxNorm=FastMath.sqrt(dxNorm);
    double fp=dxNorm - delta;
    if (fp <= 0.1 * delta) {
      lmPar=0;
      return;
    }
    double sum2;
    double parl=0;
    if (rank == solvedCols) {
      for (int j=0; j < solvedCols; ++j) {
        int pj=permutation[j];
        work1[pj]*=diag[pj] / dxNorm;
      }
      sum2=0;
      for (int j=0; j < solvedCols; ++j) {
        int pj=permutation[j];
        double sum=0;
        for (int i=0; i < j; ++i) {
          sum+=weightedJacobian[i][pj] * work1[permutation[i]];
        }
        double s=(work1[pj] - sum) / diagR[pj];
        work1[pj]=s;
        sum2+=s * s;
      }
      parl=fp / (delta * sum2);
    }
    sum2=0;
    for (int j=0; j < solvedCols; ++j) {
      int pj=permutation[j];
      double sum=0;
      for (int i=0; i <= j; ++i) {
        sum+=weightedJacobian[i][pj] * qy[i];
      }
      sum/=diag[pj];
      sum2+=sum * sum;
    }
    double gNorm=FastMath.sqrt(sum2);
    double paru=gNorm / delta;
    if (paru == 0) {
      paru=Precision.SAFE_MIN / FastMath.min(delta,0.1);
    }
    lmPar=FastMath.min(paru,FastMath.max(lmPar,parl));
    if (lmPar == 0) {
      lmPar=gNorm / dxNorm;
    }
    for (int countdown=10; countdown >= 0; --countdown) {
      if (lmPar == 0) {
        lmPar=FastMath.max(Precision.SAFE_MIN,0.001 * paru);
      }
      double sPar=FastMath.sqrt(lmPar);
      for (int j=0; j < solvedCols; ++j) {
        int pj=permutation[j];
        work1[pj]=sPar * diag[pj];
      }
      determineLMDirection(qy,work1,work2,work3);
      dxNorm=0;
      for (int j=0; j < solvedCols; ++j) {
        int pj=permutation[j];
        double s=diag[pj] * lmDir[pj];
        work3[pj]=s;
        dxNorm+=s * s;
      }
      dxNorm=FastMath.sqrt(dxNorm);
      double previousFP=fp;
      fp=dxNorm - delta;
      if ((FastMath.abs(fp) <= 0.1 * delta) || ((parl == 0) && (fp <= previousFP) && (previousFP < 0))) {
        return;
      }
      for (int j=0; j < solvedCols; ++j) {
        int pj=permutation[j];
        work1[pj]=work3[pj] * diag[pj] / dxNorm;
      }
      for (int j=0; j < solvedCols; ++j) {
        int pj=permutation[j];
        work1[pj]/=work2[j];
        double tmp=work1[pj];
        for (int i=j + 1; i < solvedCols; ++i) {
          work1[permutation[i]]-=weightedJacobian[i][pj] * tmp;
        }
      }
      sum2=0;
      for (int j=0; j < solvedCols; ++j) {
        double s=work1[permutation[j]];
        sum2+=s * s;
      }
      double correction=fp / (delta * sum2);
      if (fp > 0) {
        parl=FastMath.max(parl,lmPar);
      }
 else       if (fp < 0) {
        paru=FastMath.min(paru,lmPar);
      }
      lmPar=FastMath.max(parl,lmPar + correction);
    }
  }
  /** 
 * Solve a*x = b and d*x = 0 in the least squares sense.
 * <p>This implementation is a translation in Java of the MINPACK
 * <a href="http://www.netlib.org/minpack/qrsolv.f">qrsolv</a>
 * routine.</p>
 * <p>This method sets the lmDir and lmDiag attributes.</p>
 * <p>The authors of the original fortran function are:</p>
 * <ul>
 * <li>Argonne National Laboratory. MINPACK project. March 1980</li>
 * <li>Burton  S. Garbow</li>
 * <li>Kenneth E. Hillstrom</li>
 * <li>Jorge   J. More</li>
 * </ul>
 * <p>Luc Maisonobe did the Java translation.</p>
 * @param qy array containing qTy
 * @param diag diagonal matrix
 * @param lmDiag diagonal elements associated with lmDir
 * @param work work array
 */
  private void determineLMDirection(  double[] qy,  double[] diag,  double[] lmDiag,  double[] work){
    for (int j=0; j < solvedCols; ++j) {
      int pj=permutation[j];
      for (int i=j + 1; i < solvedCols; ++i) {
        weightedJacobian[i][pj]=weightedJacobian[j][permutation[i]];
      }
      lmDir[j]=diagR[pj];
      work[j]=qy[j];
    }
    for (int j=0; j < solvedCols; ++j) {
      int pj=permutation[j];
      double dpj=diag[pj];
      if (dpj != 0) {
        Arrays.fill(lmDiag,j + 1,lmDiag.length,0);
      }
      lmDiag[j]=dpj;
      double qtbpj=0;
      for (int k=j; k < solvedCols; ++k) {
        int pk=permutation[k];
        if (lmDiag[k] != 0) {
          final double sin;
          final double cos;
          double rkk=weightedJacobian[k][pk];
          if (FastMath.abs(rkk) < FastMath.abs(lmDiag[k])) {
            final double cotan=rkk / lmDiag[k];
            sin=1.0 / FastMath.sqrt(1.0 + cotan * cotan);
            cos=sin * cotan;
          }
 else {
            final double tan=lmDiag[k] / rkk;
            cos=1.0 / FastMath.sqrt(1.0 + tan * tan);
            sin=cos * tan;
          }
          weightedJacobian[k][pk]=cos * rkk + sin * lmDiag[k];
          final double temp=cos * work[k] + sin * qtbpj;
          qtbpj=-sin * work[k] + cos * qtbpj;
          work[k]=temp;
          for (int i=k + 1; i < solvedCols; ++i) {
            double rik=weightedJacobian[i][pk];
            final double temp2=cos * rik + sin * lmDiag[i];
            lmDiag[i]=-sin * rik + cos * lmDiag[i];
            weightedJacobian[i][pk]=temp2;
          }
        }
      }
      lmDiag[j]=weightedJacobian[j][permutation[j]];
      weightedJacobian[j][permutation[j]]=lmDir[j];
    }
    int nSing=solvedCols;
    for (int j=0; j < solvedCols; ++j) {
      if ((lmDiag[j] == 0) && (nSing == solvedCols)) {
        nSing=j;
      }
      if (nSing < solvedCols) {
        work[j]=0;
      }
    }
    if (nSing > 0) {
      for (int j=nSing - 1; j >= 0; --j) {
        int pj=permutation[j];
        double sum=0;
        for (int i=j + 1; i < nSing; ++i) {
          sum+=weightedJacobian[i][pj] * work[i];
        }
        work[j]=(work[j] - sum) / lmDiag[j];
      }
    }
    for (int j=0; j < lmDir.length; ++j) {
      lmDir[permutation[j]]=work[j];
    }
  }
  /** 
 * Decompose a matrix A as A.P = Q.R using Householder transforms.
 * <p>As suggested in the P. Lascaux and R. Theodor book
 * <i>Analyse num&eacute;rique matricielle appliqu&eacute;e &agrave;
 * l'art de l'ing&eacute;nieur</i> (Masson, 1986), instead of representing
 * the Householder transforms with u<sub>k</sub> unit vectors such that:
 * <pre>
 * H<sub>k</sub> = I - 2u<sub>k</sub>.u<sub>k</sub><sup>t</sup>
 * </pre>
 * we use <sub>k</sub> non-unit vectors such that:
 * <pre>
 * H<sub>k</sub> = I - beta<sub>k</sub>v<sub>k</sub>.v<sub>k</sub><sup>t</sup>
 * </pre>
 * where v<sub>k</sub> = a<sub>k</sub> - alpha<sub>k</sub> e<sub>k</sub>.
 * The beta<sub>k</sub> coefficients are provided upon exit as recomputing
 * them from the v<sub>k</sub> vectors would be costly.</p>
 * <p>This decomposition handles rank deficient cases since the tranformations
 * are performed in non-increasing columns norms order thanks to columns
 * pivoting. The diagonal elements of the R matrix are therefore also in
 * non-increasing absolute values order.</p>
 * @param jacobian Weighted Jacobian matrix at the current point.
 * @exception ConvergenceException if the decomposition cannot be performed
 */
  private void qrDecomposition(  RealMatrix jacobian) throws ConvergenceException {
    weightedJacobian=jacobian.scalarMultiply(-1).getData();
    final int nR=weightedJacobian.length;
    final int nC=weightedJacobian[0].length;
    for (int k=0; k < nC; ++k) {
      permutation[k]=k;
      double norm2=0;
      for (int i=0; i < nR; ++i) {
        double akk=weightedJacobian[i][k];
        norm2+=akk * akk;
      }
      jacNorm[k]=FastMath.sqrt(norm2);
    }
    for (int k=0; k < nC; ++k) {
      int nextColumn=-1;
      double ak2=Double.NEGATIVE_INFINITY;
      for (int i=k; i < nC; ++i) {
        double norm2=0;
        for (int j=k; j < nR; ++j) {
          double aki=weightedJacobian[j][permutation[i]];
          norm2+=aki * aki;
        }
        if (Double.isInfinite(norm2) || Double.isNaN(norm2)) {
          throw new ConvergenceException(LocalizedFormats.UNABLE_TO_PERFORM_QR_DECOMPOSITION_ON_JACOBIAN,nR,nC);
        }
        if (norm2 > ak2) {
          nextColumn=i;
          ak2=norm2;
        }
      }
      if (ak2 <= qrRankingThreshold) {
        rank=k;
        return;
      }
      int pk=permutation[nextColumn];
      permutation[nextColumn]=permutation[k];
      permutation[k]=pk;
      double akk=weightedJacobian[k][pk];
      double alpha=(akk > 0) ? -FastMath.sqrt(ak2) : FastMath.sqrt(ak2);
      double betak=1.0 / (ak2 - akk * alpha);
      beta[pk]=betak;
      diagR[pk]=alpha;
      weightedJacobian[k][pk]-=alpha;
      for (int dk=nC - 1 - k; dk > 0; --dk) {
        double gamma=0;
        for (int j=k; j < nR; ++j) {
          gamma+=weightedJacobian[j][pk] * weightedJacobian[j][permutation[k + dk]];
        }
        gamma*=betak;
        for (int j=k; j < nR; ++j) {
          weightedJacobian[j][permutation[k + dk]]-=gamma * weightedJacobian[j][pk];
        }
      }
    }
    rank=solvedCols;
  }
  /** 
 * Compute the product Qt.y for some Q.R. decomposition.
 * @param y vector to multiply (will be overwritten with the result)
 */
  private void qTy(  double[] y){
    final int nR=weightedJacobian.length;
    final int nC=weightedJacobian[0].length;
    for (int k=0; k < nC; ++k) {
      int pk=permutation[k];
      double gamma=0;
      for (int i=k; i < nR; ++i) {
        gamma+=weightedJacobian[i][pk] * y[i];
      }
      gamma*=beta[pk];
      for (int i=k; i < nR; ++i) {
        y[i]-=gamma * weightedJacobian[i][pk];
      }
    }
  }
  /** 
 * @throws MathUnsupportedOperationException if bounds were passed to the{@link #optimize(OptimizationData[]) optimize} method.
 */
  private void checkParameters(){
    if (getLowerBound() != null || getUpperBound() != null) {
      throw new MathUnsupportedOperationException(LocalizedFormats.CONSTRAINT);
    }
  }
}