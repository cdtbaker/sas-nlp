package org.ejml.alg.dense.decomposition.eig.watched;
import org.ejml.UtilEjml;
import org.ejml.alg.dense.decomposition.eig.EigenvalueSmall;
import org.ejml.alg.dense.decomposition.qr.QrHelperFunctions;
import org.ejml.data.Complex64F;
import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.MatrixFeatures;
import java.util.Random;
/** 
 * <p>
 * The double step implicit Eigenvalue decomposition algorithm is fairly complicated and needs to be designed so that
 * it can handle several special cases.  To aid in development and debugging this class was created.  It allows
 * individual components to be tested and to print out their results.  This shows how each step is performed.
 * </p>
 * <p>
 * Do not use this class to compute the eigenvalues since it is much slower than a non-debug implementation.
 * </p>
 */
public class WatchedDoubleStepQREigen {
  private Random rand=new Random(0x2342);
  private int N;
  DenseMatrix64F A;
  private DenseMatrix64F u;
  private double gamma;
  private DenseMatrix64F _temp;
  int numStepsFind[];
  int steps;
  Complex64F eigenvalues[];
  int numEigen;
  private EigenvalueSmall valueSmall=new EigenvalueSmall();
  private double temp[]=new double[9];
  private boolean printHumps=false;
  boolean checkHessenberg=false;
  private boolean checkOrthogonal=false;
  private boolean checkUncountable=false;
  private boolean useStandardEq=false;
  private boolean useCareful2x2=true;
  private boolean normalize=true;
  int lastExceptional;
  int numExceptional;
  int exceptionalThreshold=20;
  int maxIterations=exceptionalThreshold * 20;
  public boolean createR=true;
  public DenseMatrix64F Q;
  public void incrementSteps(){
    steps++;
  }
  public void setQ(  DenseMatrix64F Q){
    this.Q=Q;
  }
  private void addEigenvalue(  double v){
    numStepsFind[numEigen]=steps;
    eigenvalues[numEigen].set(v,0);
    numEigen++;
    steps=0;
    lastExceptional=0;
  }
  private void addEigenvalue(  double v,  double i){
    numStepsFind[numEigen]=steps;
    eigenvalues[numEigen].set(v,i);
    numEigen++;
    steps=0;
    lastExceptional=0;
  }
  public void setChecks(  boolean hessenberg,  boolean orthogonal,  boolean uncountable){
    this.checkHessenberg=hessenberg;
    this.checkOrthogonal=orthogonal;
    this.checkUncountable=uncountable;
  }
  public boolean isZero(  int x1,  int x2){
    double target=Math.abs(A.get(x1,x2));
    double above=Math.abs(A.get(x1 - 1,x2));
    double right=Math.abs(A.get(x1,x2 + 1));
    return target <= 0.5 * UtilEjml.EPS * (above + right);
  }
  public void setup(  DenseMatrix64F A){
    if (A.numRows != A.numCols)     throw new RuntimeException("Must be square");
    if (N != A.numRows) {
      N=A.numRows;
      this.A=A.copy();
      u=new DenseMatrix64F(A.numRows,1);
      _temp=new DenseMatrix64F(A.numRows,1);
      numStepsFind=new int[A.numRows];
    }
 else {
      this.A.set(A);
      UtilEjml.memset(numStepsFind,0,numStepsFind.length);
    }
    for (int i=2; i < N; i++) {
      for (int j=0; j < i - 1; j++) {
        this.A.set(i,j,0);
      }
    }
    eigenvalues=new Complex64F[A.numRows];
    for (int i=0; i < eigenvalues.length; i++) {
      eigenvalues[i]=new Complex64F();
    }
    numEigen=0;
    lastExceptional=0;
    numExceptional=0;
    steps=0;
  }
  /** 
 * Perform a shift in a random direction that is of the same magnitude as the elements in the matrix.
 */
  public void exceptionalShift(  int x1,  int x2){
    if (printHumps)     System.out.println("Performing exceptional implicit double step");
    double val=Math.abs(A.get(x2,x2));
    if (val == 0)     val=1;
    numExceptional++;
    double p=1 - Math.pow(0.1,numExceptional);
    val*=p + 2.0 * (1.0 - p) * (rand.nextDouble() - 0.5);
    if (rand.nextBoolean())     val=-val;
    performImplicitSingleStep(x1,x2,val);
    lastExceptional=steps;
  }
  /** 
 * Performs an implicit double step using the values contained in the lower right hand side
 * of the submatrix for the estimated eigenvector values.
 * @param x1
 * @param x2
 */
  public void implicitDoubleStep(  int x1,  int x2){
    if (printHumps)     System.out.println("Performing implicit double step");
    double z11=A.get(x2 - 1,x2 - 1);
    double z12=A.get(x2 - 1,x2);
    double z21=A.get(x2,x2 - 1);
    double z22=A.get(x2,x2);
    double a11=A.get(x1,x1);
    double a21=A.get(x1 + 1,x1);
    double a12=A.get(x1,x1 + 1);
    double a22=A.get(x1 + 1,x1 + 1);
    double a32=A.get(x1 + 2,x1 + 1);
    if (normalize) {
      temp[0]=a11;
      temp[1]=a21;
      temp[2]=a12;
      temp[3]=a22;
      temp[4]=a32;
      temp[5]=z11;
      temp[6]=z22;
      temp[7]=z12;
      temp[8]=z21;
      double max=Math.abs(temp[0]);
      for (int j=1; j < temp.length; j++) {
        if (Math.abs(temp[j]) > max)         max=Math.abs(temp[j]);
      }
      a11/=max;
      a21/=max;
      a12/=max;
      a22/=max;
      a32/=max;
      z11/=max;
      z22/=max;
      z12/=max;
      z21/=max;
    }
    double b11, b21, b31;
    if (useStandardEq) {
      b11=((a11 - z11) * (a11 - z22) - z21 * z12) / a21 + a12;
      b21=a11 + a22 - z11 - z22;
      b31=a32;
    }
 else {
      b11=((a11 - z11) * (a11 - z22) - z21 * z12) + a12 * a21;
      b21=(a11 + a22 - z11 - z22) * a21;
      b31=a32 * a21;
    }
    performImplicitDoubleStep(x1,x2,b11,b21,b31);
  }
  /** 
 * Performs an implicit double step given the set of two imaginary eigenvalues provided.
 * Since one eigenvalue is the complex conjugate of the other only one set of real and imaginary
 * numbers is needed.
 * @param x1 upper index of submatrix.
 * @param x2 lower index of submatrix.
 * @param real Real component of each of the eigenvalues.
 * @param img Imaginary component of one of the eigenvalues.
 */
  public void performImplicitDoubleStep(  int x1,  int x2,  double real,  double img){
    double a11=A.get(x1,x1);
    double a21=A.get(x1 + 1,x1);
    double a12=A.get(x1,x1 + 1);
    double a22=A.get(x1 + 1,x1 + 1);
    double a32=A.get(x1 + 2,x1 + 1);
    double p_plus_t=2.0 * real;
    double p_times_t=real * real + img * img;
    double b11, b21, b31;
    if (useStandardEq) {
      b11=(a11 * a11 - p_plus_t * a11 + p_times_t) / a21 + a12;
      b21=a11 + a22 - p_plus_t;
      b31=a32;
    }
 else {
      b11=(a11 * a11 - p_plus_t * a11 + p_times_t) + a12 * a21;
      b21=(a11 + a22 - p_plus_t) * a21;
      b31=a32 * a21;
    }
    performImplicitDoubleStep(x1,x2,b11,b21,b31);
  }
  private void performImplicitDoubleStep(  int x1,  int x2,  double b11,  double b21,  double b31){
    if (!bulgeDoubleStepQn(x1,b11,b21,b31,0,false))     return;
    if (Q != null) {
      QrHelperFunctions.rank1UpdateMultR(Q,u.data,gamma,0,x1,x1 + 3,_temp.data);
      if (checkOrthogonal && !MatrixFeatures.isOrthogonal(Q,1e-8)) {
        u.print();
        Q.print();
        throw new RuntimeException("Bad");
      }
    }
    if (printHumps) {
      System.out.println("Applied first Q matrix, it should be humped now. A = ");
      A.print("%12.3e");
      System.out.println("Pushing the hump off the matrix.");
    }
    for (int i=x1; i < x2 - 2; i++) {
      if (bulgeDoubleStepQn(i) && Q != null) {
        QrHelperFunctions.rank1UpdateMultR(Q,u.data,gamma,0,i + 1,i + 4,_temp.data);
        if (checkOrthogonal && !MatrixFeatures.isOrthogonal(Q,1e-8))         throw new RuntimeException("Bad");
      }
      if (printHumps) {
        System.out.println("i = " + i + " A = ");
        A.print("%12.3e");
      }
    }
    if (printHumps)     System.out.println("removing last bump");
    if (x2 - 2 >= 0 && bulgeSingleStepQn(x2 - 2) && Q != null) {
      QrHelperFunctions.rank1UpdateMultR(Q,u.data,gamma,0,x2 - 1,x2 + 1,_temp.data);
      if (checkOrthogonal && !MatrixFeatures.isOrthogonal(Q,1e-8))       throw new RuntimeException("Bad");
    }
    if (printHumps) {
      System.out.println(" A = ");
      A.print("%12.3e");
    }
    if (checkHessenberg && !MatrixFeatures.isUpperTriangle(A,1,1e-12)) {
      A.print("%12.3e");
      throw new RuntimeException("Bad matrix");
    }
  }
  public void performImplicitSingleStep(  int x1,  int x2,  double eigenvalue){
    if (!createBulgeSingleStep(x1,eigenvalue))     return;
    if (Q != null) {
      QrHelperFunctions.rank1UpdateMultR(Q,u.data,gamma,0,x1,x1 + 2,_temp.data);
      if (checkOrthogonal && !MatrixFeatures.isOrthogonal(Q,1e-8))       throw new RuntimeException("Bad");
    }
    if (printHumps) {
      System.out.println("Applied first Q matrix, it should be humped now. A = ");
      A.print("%12.3e");
      System.out.println("Pushing the hump off the matrix.");
    }
    for (int i=x1; i < x2 - 1; i++) {
      if (bulgeSingleStepQn(i) && Q != null) {
        QrHelperFunctions.rank1UpdateMultR(Q,u.data,gamma,0,i + 1,i + 3,_temp.data);
        if (checkOrthogonal && !MatrixFeatures.isOrthogonal(Q,1e-8))         throw new RuntimeException("Bad");
      }
      if (printHumps) {
        System.out.println("i = " + i + " A = ");
        A.print("%12.3e");
      }
    }
    if (checkHessenberg && !MatrixFeatures.isUpperTriangle(A,1,1e-12)) {
      A.print("%12.3e");
      throw new RuntimeException("Bad matrix");
    }
  }
  public boolean createBulgeSingleStep(  int x1,  double eigenvalue){
    double b11=A.get(x1,x1) - eigenvalue;
    double b21=A.get(x1 + 1,x1);
    double threshold=Math.abs(A.get(x1,x1)) * UtilEjml.EPS;
    return bulgeSingleStepQn(x1,b11,b21,threshold,false);
  }
  public boolean bulgeDoubleStepQn(  int i){
    double a11=A.get(i + 1,i);
    double a21=A.get(i + 2,i);
    double a31=A.get(i + 3,i);
    double threshold=Math.abs(A.get(i,i)) * UtilEjml.EPS;
    return bulgeDoubleStepQn(i + 1,a11,a21,a31,threshold,true);
  }
  public boolean bulgeDoubleStepQn(  int i,  double a11,  double a21,  double a31,  double threshold,  boolean set){
    double max;
    if (normalize) {
      double absA11=Math.abs(a11);
      double absA21=Math.abs(a21);
      double absA31=Math.abs(a31);
      max=absA11 > absA21 ? absA11 : absA21;
      if (absA31 > max)       max=absA31;
      if (max <= threshold) {
        if (set) {
          A.set(i,i - 1,0);
          A.set(i + 1,i - 1,0);
          A.set(i + 2,i - 1,0);
        }
        return false;
      }
      a11/=max;
      a21/=max;
      a31/=max;
    }
 else {
      max=1;
    }
    double tau=Math.sqrt(a11 * a11 + a21 * a21 + a31 * a31);
    if (a11 < 0)     tau=-tau;
    double div=a11 + tau;
    u.set(i,0,1);
    u.set(i + 1,0,a21 / div);
    u.set(i + 2,0,a31 / div);
    gamma=div / tau;
    QrHelperFunctions.rank1UpdateMultR(A,u.data,gamma,0,i,i + 3,_temp.data);
    if (set) {
      A.set(i,i - 1,-max * tau);
      A.set(i + 1,i - 1,0);
      A.set(i + 2,i - 1,0);
    }
    if (printHumps) {
      System.out.println("  After Q.   A =");
      A.print();
    }
    QrHelperFunctions.rank1UpdateMultL(A,u.data,gamma,0,i,i + 3);
    if (checkUncountable && MatrixFeatures.hasUncountable(A)) {
      throw new RuntimeException("bad matrix");
    }
    return true;
  }
  public boolean bulgeSingleStepQn(  int i){
    double a11=A.get(i + 1,i);
    double a21=A.get(i + 2,i);
    double threshold=Math.abs(A.get(i,i)) * UtilEjml.EPS;
    return bulgeSingleStepQn(i + 1,a11,a21,threshold,true);
  }
  public boolean bulgeSingleStepQn(  int i,  double a11,  double a21,  double threshold,  boolean set){
    double max;
    if (normalize) {
      max=Math.abs(a11);
      if (max < Math.abs(a21))       max=Math.abs(a21);
      if (max <= threshold) {
        if (set) {
          A.set(i,i - 1,0);
          A.set(i + 1,i - 1,0);
        }
        return false;
      }
      a11/=max;
      a21/=max;
    }
 else {
      max=1;
    }
    double tau=Math.sqrt(a11 * a11 + a21 * a21);
    if (a11 < 0)     tau=-tau;
    double div=a11 + tau;
    u.set(i,0,1);
    u.set(i + 1,0,a21 / div);
    gamma=div / tau;
    QrHelperFunctions.rank1UpdateMultR(A,u.data,gamma,0,i,i + 2,_temp.data);
    if (set) {
      A.set(i,i - 1,-max * tau);
      A.set(i + 1,i - 1,0);
    }
    QrHelperFunctions.rank1UpdateMultL(A,u.data,gamma,0,i,i + 2);
    if (checkUncountable && MatrixFeatures.hasUncountable(A)) {
      throw new RuntimeException("bad matrix");
    }
    return true;
  }
  public void eigen2by2_scale(  double a11,  double a12,  double a21,  double a22){
    double abs11=Math.abs(a11);
    double abs22=Math.abs(a22);
    double abs12=Math.abs(a12);
    double abs21=Math.abs(a21);
    double max=abs11 > abs22 ? abs11 : abs22;
    if (max < abs12)     max=abs12;
    if (max < abs21)     max=abs21;
    if (max == 0) {
      valueSmall.value0.real=0;
      valueSmall.value0.imaginary=0;
      valueSmall.value1.real=0;
      valueSmall.value1.imaginary=0;
    }
 else {
      a12/=max;
      a21/=max;
      a11/=max;
      a22/=max;
      if (useCareful2x2) {
        valueSmall.value2x2(a11,a12,a21,a22);
      }
 else {
        valueSmall.value2x2_fast(a11,a12,a21,a22);
      }
      valueSmall.value0.real*=max;
      valueSmall.value0.imaginary*=max;
      valueSmall.value1.real*=max;
      valueSmall.value1.imaginary*=max;
    }
  }
  public int getNumberOfEigenvalues(){
    return numEigen;
  }
  public Complex64F[] getEigenvalues(){
    return eigenvalues;
  }
  public void addComputedEigen2x2(  int x1,  int x2){
    eigen2by2_scale(A.get(x1,x1),A.get(x1,x2),A.get(x2,x1),A.get(x2,x2));
    if (checkUncountable && (Double.isNaN(valueSmall.value0.real) || Double.isNaN(valueSmall.value1.real))) {
      throw new RuntimeException("Uncountable");
    }
    addEigenvalue(valueSmall.value0.real,valueSmall.value0.imaginary);
    addEigenvalue(valueSmall.value1.real,valueSmall.value1.imaginary);
  }
  public boolean isReal2x2(  int x1,  int x2){
    eigen2by2_scale(A.get(x1,x1),A.get(x1,x2),A.get(x2,x1),A.get(x2,x2));
    return valueSmall.value0.isReal();
  }
  public void addEigenAt(  int x1){
    addEigenvalue(A.get(x1,x1));
  }
  public void printSteps(){
    for (int i=0; i < N; i++) {
      System.out.println("Step[" + i + "] = "+ numStepsFind[i]);
    }
  }
}
