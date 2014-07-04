package no.uib.cipr.matrix.sparse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import no.uib.cipr.matrix.DenseLU;
import no.uib.cipr.matrix.DenseMatrix;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Matrix;
import no.uib.cipr.matrix.Vector;
/** 
 * Algebraic multigrid preconditioner. Uses the smoothed aggregation method
 * described by Vanek, Mandel, and Brezina (1996).
 */
public class AMG implements Preconditioner {
  /** 
 * Relaxations at each level
 */
  private SSOR[] preM, postM;
  /** 
 * The number of levels
 */
  private int m;
  /** 
 * System matrix at each level, except at the coarsest
 */
  private CompRowMatrix[] A;
  /** 
 * LU factorization at the coarsest level
 */
  private DenseLU lu;
  /** 
 * Solution, right-hand side, and residual vectors at each level
 */
  private DenseVector[] u, f, r;
  /** 
 * Interpolation operators going to a finer mesh
 */
  private CompColMatrix[] I;
  /** 
 * Smallest matrix size before terminating the AMG setup phase. Matrices
 * smaller than this will be solved by a direct solver
 */
  private final int min;
  /** 
 * Number of times to perform the pre- and post-smoothings
 */
  private final int nu1, nu2;
  /** 
 * Determines cycle type. gamma=1 is V, gamma=2 is W
 */
  private final int gamma;
  /** 
 * Overrelaxation parameters in the pre- and post-smoothings, and with the
 * possibility of distinct values in the forward and reverse sweeps
 */
  private final double omegaPreF, omegaPreR, omegaPostF, omegaPostR;
  /** 
 * Perform a reverse (backwards) smoothing sweep
 */
  private final boolean reverse;
  /** 
 * Jacobi damping parameter, between zero and one. If it equals zero, the
 * method reduces to the standard aggregate multigrid method
 */
  private final double omega;
  /** 
 * Operating in transpose mode?
 */
  private boolean transpose;
  /** 
 * Sets up the algebraic multigrid preconditioner
 * @param omegaPreFOverrelaxation parameter in the forward sweep of the
 * pre-smoothing
 * @param omegaPreROverrelaxation parameter in the backwards sweep of the
 * pre-smoothing
 * @param omegaPostFOverrelaxation parameter in the forward sweep of the
 * post-smoothing
 * @param omegaPostROverrelaxation parameter in the backwards sweep of the
 * post-smoothing
 * @param nu1Number of pre-relaxations to perform
 * @param nu2Number of post-relaxations to perform
 * @param gammaNumber of times to go to a coarser level
 * @param minSmallest matrix size before using a direct solver
 * @param omegaJacobi damping parameter, between zero and one. If it equals
 * zero, the method reduces to the standard aggregate multigrid
 * method
 */
  public AMG(  double omegaPreF,  double omegaPreR,  double omegaPostF,  double omegaPostR,  int nu1,  int nu2,  int gamma,  int min,  double omega){
    this.omegaPreF=omegaPreF;
    this.omegaPreR=omegaPreR;
    this.omegaPostF=omegaPostF;
    this.omegaPostR=omegaPostR;
    reverse=true;
    this.nu1=nu1;
    this.nu2=nu2;
    this.gamma=gamma;
    this.min=min;
    this.omega=omega;
  }
  /** 
 * Sets up the algebraic multigrid preconditioner. Uses an SOR method,
 * without the backward sweep in SSOR
 * @param omegaPreOverrelaxation parameter in the pre-smoothing
 * @param omegaPostOverrelaxation parameter in the post-smoothing
 * @param nu1Number of pre-relaxations to perform
 * @param nu2Number of post-relaxations to perform
 * @param gammaNumber of times to go to a coarser level
 * @param minSmallest matrix size before using a direct solver
 * @param omegaJacobi damping parameter, between zero and one. If it equals
 * zero, the method reduces to the standard aggregate multigrid
 * method
 */
  public AMG(  double omegaPre,  double omegaPost,  int nu1,  int nu2,  int gamma,  int min,  double omega){
    this.omegaPreF=omegaPre;
    this.omegaPreR=omegaPre;
    this.omegaPostF=omegaPost;
    this.omegaPostR=omegaPost;
    reverse=false;
    this.nu1=nu1;
    this.nu2=nu2;
    this.gamma=gamma;
    this.min=min;
    this.omega=omega;
  }
  /** 
 * Sets up the algebraic multigrid preconditioner using some default
 * parameters. In the presmoothing, <code>omegaF=1</code> and
 * <code>omegaR=1.85</code>, while in the postsmoothing,
 * <code>omegaF=1.85</code> and <code>omegaR=1</code>. Sets
 * <code>nu1=nu2=gamma=1</code>, has a smallest matrix size of 40, and
 * sets <code>omega=2/3</code>.
 */
  public AMG(){
    this(1,1.85,1.85,1,1,1,1,40,2. / 3);
  }
  public Vector apply(  Vector b,  Vector x){
    u[0].set(x);
    f[0].set(b);
    transpose=false;
    cycle(0);
    return x.set(u[0]);
  }
  public Vector transApply(  Vector b,  Vector x){
    u[0].set(x);
    f[0].set(b);
    transpose=true;
    cycle(0);
    return x.set(u[0]);
  }
  public void setMatrix(  Matrix A){
    List<CompRowMatrix> Al=new LinkedList<CompRowMatrix>();
    List<CompColMatrix> Il=new LinkedList<CompColMatrix>();
    Al.add(new CompRowMatrix(A));
    for (int k=0; Al.get(k).numRows() > min; ++k) {
      CompRowMatrix Af=Al.get(k);
      double eps=0.08 * Math.pow(0.5,k);
      Aggregator aggregator=new Aggregator(Af,eps);
      if (aggregator.getAggregates().size() == 0)       break;
      Interpolator sa=new Interpolator(aggregator,Af,omega);
      Al.add(sa.getGalerkinOperator());
      Il.add(sa.getInterpolationOperator());
    }
    m=Al.size();
    if (m == 0)     throw new RuntimeException("Matrix too small for AMG");
    I=new CompColMatrix[m - 1];
    this.A=new CompRowMatrix[m - 1];
    Il.toArray(I);
    for (int i=0; i < Al.size() - 1; ++i)     this.A[i]=Al.get(i);
    DenseMatrix Ac=new DenseMatrix(Al.get(Al.size() - 1));
    lu=new DenseLU(Ac.numRows(),Ac.numColumns());
    lu.factor(Ac);
    u=new DenseVector[m];
    f=new DenseVector[m];
    r=new DenseVector[m];
    for (int k=0; k < m; ++k) {
      int n=Al.get(k).numRows();
      u[k]=new DenseVector(n);
      f[k]=new DenseVector(n);
      r[k]=new DenseVector(n);
    }
    preM=new SSOR[m - 1];
    postM=new SSOR[m - 1];
    for (int k=0; k < m - 1; ++k) {
      CompRowMatrix Ak=this.A[k];
      preM[k]=new SSOR(Ak,reverse,omegaPreF,omegaPreR);
      postM[k]=new SSOR(Ak,reverse,omegaPostF,omegaPostR);
      preM[k].setMatrix(Ak);
      postM[k].setMatrix(Ak);
    }
  }
  /** 
 * Performs a multigrid cycle
 * @param kLevel to cycle at. Start by calling <code>cycle(0)</code>
 */
  private void cycle(  int k){
    if (k == m - 1)     directSolve();
 else {
      preRelax(k);
      u[k + 1].zero();
      A[k].multAdd(-1,u[k],r[k].set(f[k]));
      I[k].transMult(r[k],f[k + 1]);
      for (int i=0; i < gamma; ++i)       cycle(k + 1);
      I[k].multAdd(u[k + 1],u[k]);
      postRelax(k);
    }
  }
  /** 
 * Solves directly at the coarsest level
 */
  private void directSolve(){
    int k=m - 1;
    u[k].set(f[k]);
    DenseMatrix U=new DenseMatrix(u[k],false);
    if (transpose)     lu.transSolve(U);
 else     lu.solve(U);
  }
  /** 
 * Applies the relaxation scheme at the given level
 * @param kMultigrid level
 */
  private void preRelax(  int k){
    for (int i=0; i < nu1; ++i)     if (transpose)     preM[k].transApply(f[k],u[k]);
 else     preM[k].apply(f[k],u[k]);
  }
  /** 
 * Applies the relaxation scheme at the given level
 * @param kMultigrid level
 */
  private void postRelax(  int k){
    for (int i=0; i < nu2; ++i)     if (transpose)     postM[k].transApply(f[k],u[k]);
 else     postM[k].apply(f[k],u[k]);
  }
  /** 
 * Creates aggregates. These are disjoint sets, each of which represents one
 * node at a coarser mesh by aggregating together a set of fine nodes
 */
private static class Aggregator {
    /** 
 * The aggregates
 */
    private List<Set<Integer>> C;
    /** 
 * Diagonal indices into the sparse matrix
 */
    private int[] diagind;
    /** 
 * The strongly coupled node neighborhood of a given node
 */
    private List<Set<Integer>> N;
    /** 
 * Creates the aggregates
 * @param ASparse matrix
 * @param epsTolerance for selecting the strongly coupled node
 * neighborhoods. Between zero and one.
 */
    public Aggregator(    CompRowMatrix A,    double eps){
      diagind=findDiagonalIndices(A);
      N=findNodeNeighborhood(A,diagind,eps);
      boolean[] R=createInitialR(A);
      C=createInitialAggregates(N,R);
      C=enlargeAggregates(C,N,R);
      C=createFinalAggregates(C,N,R);
    }
    /** 
 * Gets the aggregates
 */
    public List<Set<Integer>> getAggregates(){
      return C;
    }
    /** 
 * Returns the matrix diagonal indices. This is a by-product of the
 * aggregation
 */
    public int[] getDiagonalIndices(){
      return diagind;
    }
    /** 
 * Returns the strongly coupled node neighborhoods of a given node. This
 * is a by-product of the aggregation
 */
    public List<Set<Integer>> getNodeNeighborhoods(){
      return N;
    }
    /** 
 * Finds the diagonal indices of the matrix
 */
    private int[] findDiagonalIndices(    CompRowMatrix A){
      int[] rowptr=A.getRowPointers();
      int[] colind=A.getColumnIndices();
      int[] diagind=new int[A.numRows()];
      for (int i=0; i < A.numRows(); ++i) {
        diagind[i]=no.uib.cipr.matrix.sparse.Arrays.binarySearch(colind,i,rowptr[i],rowptr[i + 1]);
        if (diagind[i] < 0)         throw new RuntimeException("Matrix is missing a diagonal entry on row " + (i + 1));
      }
      return diagind;
    }
    /** 
 * Finds the strongly coupled node neighborhoods
 */
    private List<Set<Integer>> findNodeNeighborhood(    CompRowMatrix A,    int[] diagind,    double eps){
      N=new ArrayList<Set<Integer>>(A.numRows());
      int[] rowptr=A.getRowPointers();
      int[] colind=A.getColumnIndices();
      double[] data=A.getData();
      for (int i=0; i < A.numRows(); ++i) {
        Set<Integer> Ni=new HashSet<Integer>();
        double aii=data[diagind[i]];
        for (int j=rowptr[i]; j < rowptr[i + 1]; ++j) {
          double aij=data[j];
          double ajj=data[diagind[colind[j]]];
          if (Math.abs(aij) >= eps * Math.sqrt(aii * ajj))           Ni.add(colind[j]);
        }
        N.add(Ni);
      }
      return N;
    }
    /** 
 * Creates the initial R-set by including only the connected nodes
 */
    private boolean[] createInitialR(    CompRowMatrix A){
      boolean[] R=new boolean[A.numRows()];
      int[] rowptr=A.getRowPointers();
      int[] colind=A.getColumnIndices();
      double[] data=A.getData();
      for (int i=0; i < A.numRows(); ++i) {
        boolean hasOffDiagonal=false;
        for (int j=rowptr[i]; j < rowptr[i + 1]; ++j)         if (colind[j] != i && data[j] != 0) {
          hasOffDiagonal=true;
          break;
        }
        R[i]=hasOffDiagonal;
      }
      return R;
    }
    /** 
 * Creates the initial aggregates
 */
    private List<Set<Integer>> createInitialAggregates(    List<Set<Integer>> N,    boolean[] R){
      C=new ArrayList<Set<Integer>>();
      for (int i=0; i < R.length; ++i) {
        if (!R[i])         continue;
        boolean free=true;
        for (        int j : N.get(i))         free&=R[j];
        if (free) {
          C.add(new HashSet<Integer>(N.get(i)));
          for (          int j : N.get(i))           R[j]=false;
        }
      }
      return C;
    }
    /** 
 * Enlarges the aggregates
 */
    private List<Set<Integer>> enlargeAggregates(    List<Set<Integer>> C,    List<Set<Integer>> N,    boolean[] R){
      List<List<Integer>> belong=new ArrayList<List<Integer>>(R.length);
      for (int i=0; i < R.length; ++i)       belong.add(new ArrayList<Integer>());
      for (int k=0; k < C.size(); ++k)       for (      int j : C.get(k))       belong.get(j).add(k);
      int[] intersect=new int[C.size()];
      for (int i=0; i < R.length; ++i) {
        if (!R[i])         continue;
        Arrays.fill(intersect,0);
        int largest=0, maxValue=0;
        for (        int j : N.get(i))         for (        int k : belong.get(j)) {
          intersect[k]++;
          if (intersect[k] > maxValue) {
            largest=k;
            maxValue=intersect[largest];
          }
        }
        if (maxValue > 0) {
          R[i]=false;
          C.get(largest).add(i);
        }
      }
      return C;
    }
    /** 
 * Creates final aggregates from the remaining unallocated nodes
 */
    private List<Set<Integer>> createFinalAggregates(    List<Set<Integer>> C,    List<Set<Integer>> N,    boolean[] R){
      for (int i=0; i < R.length; ++i) {
        if (!R[i])         continue;
        Set<Integer> Cn=new HashSet<Integer>();
        for (        int j : N.get(i))         if (R[j]) {
          R[j]=false;
          Cn.add(j);
        }
        if (!Cn.isEmpty())         C.add(Cn);
      }
      return C;
    }
  }
  /** 
 * Creates interpolation (prolongation) operators using based on the
 * aggregates. Can optionally smooth the aggregates
 */
private static class Interpolator {
    /** 
 * The Galerkin coarse-space operator
 */
    private CompRowMatrix Ac;
    /** 
 * The interpolation (prolongation) matrix
 */
    private CompColMatrix I;
    /** 
 * Creates the interpolation (prolongation) and Galerkin operators
 * @param aggregatorAggregates
 * @param AMatrix
 * @param omegaJacobi damping parameter between zero and one. If zero, no
 * smoothing is performed, and a faster algorithm for forming
 * the Galerkin operator will be used.
 */
    public Interpolator(    Aggregator aggregator,    CompRowMatrix A,    double omega){
      List<Set<Integer>> C=aggregator.getAggregates();
      List<Set<Integer>> N=aggregator.getNodeNeighborhoods();
      int[] diagind=aggregator.getDiagonalIndices();
      int[] pt=createTentativeProlongation(C,A.numRows());
      if (omega != 0) {
        List<Map<Integer,Double>> P=createSmoothedProlongation(C,N,A,diagind,omega,pt);
        I=createInterpolationMatrix(P,A.numRows());
        Ac=createGalerkinSlow(I,A);
      }
 else {
        Ac=createGalerkinFast(A,pt,C.size());
        I=createInterpolationMatrix(pt,C.size());
      }
    }
    /** 
 * Creates the tentative prolongation operator. Since the columns are
 * all disjoint, and its entries are binary, it is possible to store it
 * in a single array. Its length equals the number of fine nodes, and
 * the entries are the indices to the corresponding aggregate (C-set).
 */
    private int[] createTentativeProlongation(    List<Set<Integer>> C,    int n){
      int[] pt=new int[n];
      Arrays.fill(pt,-1);
      for (int i=0; i < C.size(); ++i)       for (      int j : C.get(i))       pt[j]=i;
      return pt;
    }
    /** 
 * Creates the Galerkin operator using the assumption of disjoint
 * (non-smoothed) aggregates
 */
    private CompRowMatrix createGalerkinFast(    CompRowMatrix A,    int[] pt,    int c){
      int n=pt.length;
      FlexCompRowMatrix Ac=new FlexCompRowMatrix(c,c);
      int[] rowptr=A.getRowPointers();
      int[] colind=A.getColumnIndices();
      double[] data=A.getData();
      for (int i=0; i < n; ++i)       if (pt[i] != -1)       for (int j=rowptr[i]; j < rowptr[i + 1]; ++j)       if (pt[colind[j]] != -1)       Ac.add(pt[i],pt[colind[j]],data[j]);
      return new CompRowMatrix(Ac);
    }
    /** 
 * Creates the interpolation (prolongation) matrix based on the smoothed
 * aggregates
 */
    private CompColMatrix createInterpolationMatrix(    List<Map<Integer,Double>> P,    int n){
      int c=P.size();
      int[][] nz=new int[c][];
      for (int j=0; j < c; ++j) {
        Map<Integer,Double> Pj=P.get(j);
        nz[j]=new int[Pj.size()];
        int l=0;
        for (        int k : Pj.keySet())         nz[j][l++]=k;
      }
      I=new CompColMatrix(n,c,nz);
      for (int j=0; j < c; ++j) {
        Map<Integer,Double> Pj=P.get(j);
        for (        Map.Entry<Integer,Double> e : Pj.entrySet())         I.set(e.getKey(),j,e.getValue());
      }
      return I;
    }
    /** 
 * Creates the interpolation (prolongation) matrix based on the
 * non-smoothed aggregates
 */
    private CompColMatrix createInterpolationMatrix(    int[] pt,    int c){
      FlexCompColMatrix If=new FlexCompColMatrix(pt.length,c);
      for (int i=0; i < pt.length; ++i)       if (pt[i] != -1)       If.set(i,pt[i],1);
      return new CompColMatrix(If);
    }
    /** 
 * Gets the interpolation (prolongation) operator
 */
    public CompColMatrix getInterpolationOperator(){
      return I;
    }
    /** 
 * Creates the smoothes interpolation (prolongation) operator by a
 * single sweep of the damped Jacobi method
 */
    private List<Map<Integer,Double>> createSmoothedProlongation(    List<Set<Integer>> C,    List<Set<Integer>> N,    CompRowMatrix A,    int[] diagind,    double omega,    int[] pt){
      int n=A.numRows(), c=C.size();
      List<Map<Integer,Double>> P=new ArrayList<Map<Integer,Double>>(c);
      for (int i=0; i < c; ++i)       P.add(new HashMap<Integer,Double>());
      int[] rowptr=A.getRowPointers();
      int[] colind=A.getColumnIndices();
      double[] data=A.getData();
      double[] dot=new double[c];
      for (int i=0; i < n; ++i) {
        if (pt[i] == -1)         continue;
        Arrays.fill(dot,0);
        Set<Integer> Ni=N.get(i);
        double weakAij=0;
        for (int j=rowptr[i]; j < rowptr[i + 1]; ++j) {
          if (pt[colind[j]] == -1)           continue;
          double aij=data[j];
          if (aij != 0 && !Ni.contains(colind[j])) {
            weakAij+=aij;
            continue;
          }
          dot[pt[colind[j]]]+=aij;
        }
        dot[pt[i]]-=weakAij;
        double scale=-omega / data[diagind[i]];
        for (int j=0; j < dot.length; ++j)         dot[j]*=scale;
        dot[pt[i]]++;
        for (int j=0; j < dot.length; ++j)         if (dot[j] != 0)         P.get(j).put(i,dot[j]);
      }
      return P;
    }
    /** 
 * Creates the entries of the Galerkin operator
 * <code>Ac = I<sup>T</sup> A I</code>. This is a very
 * time-consuming operation
 */
    private CompRowMatrix createGalerkinSlow(    CompColMatrix I,    CompRowMatrix A){
      int n=I.numRows(), c=I.numColumns();
      FlexCompRowMatrix Ac=new FlexCompRowMatrix(c,c);
      double[] aiCol=new double[n];
      double[] iCol=new double[n];
      DenseVector aiV=new DenseVector(aiCol,false);
      DenseVector iV=new DenseVector(iCol,false);
      double[] itaiCol=new double[c];
      DenseVector itaiV=new DenseVector(itaiCol,false);
      int[] colptr=I.getColumnPointers();
      int[] rowind=I.getRowIndices();
      double[] Idata=I.getData();
      for (int k=0; k < c; ++k) {
        iV.zero();
        for (int i=colptr[k]; i < colptr[k + 1]; ++i)         iCol[rowind[i]]=Idata[i];
        A.mult(iV,aiV);
        I.transMult(aiV,itaiV);
        for (int i=0; i < c; ++i)         if (itaiCol[i] != 0)         Ac.set(i,k,itaiCol[i]);
      }
      return new CompRowMatrix(Ac);
    }
    /** 
 * Gets the Galerkin operator
 */
    public CompRowMatrix getGalerkinOperator(){
      return Ac;
    }
  }
}
