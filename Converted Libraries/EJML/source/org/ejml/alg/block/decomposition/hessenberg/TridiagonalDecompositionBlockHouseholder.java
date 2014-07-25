package org.ejml.alg.block.decomposition.hessenberg;
import org.ejml.alg.block.BlockMultiplication;
import org.ejml.alg.block.decomposition.qr.BlockMatrix64HouseholderQR;
import org.ejml.alg.dense.decomposition.hessenberg.TridiagonalSimilarDecomposition;
import org.ejml.data.BlockMatrix64F;
import org.ejml.data.D1Submatrix64F;
import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.CommonOps;
import static org.ejml.alg.block.BlockInnerMultiplication.blockMultPlusTransA;
/** 
 * <p>
 * Tridiagonal similar decomposition for block matrices.  Orthogonal matrices are computed using
 * householder vectors.
 * </p>
 * <p>
 * Based off algorithm in section 2 of J. J. Dongarra, D. C. Sorensen, S. J. Hammarling,
 * "Block Reduction of Matrices to Condensed Forms for Eigenvalue Computations" Journal of
 * Computations and Applied Mathematics 27 (1989) 215-227<b>
 * <br>
 * Computations of Householder reflectors has been modified from what is presented in that paper to how 
 * it is performed in "Fundamentals of Matrix Computations" 2nd ed. by David S. Watkins.
 * </p>
 * @author Peter Abeles
 */
public class TridiagonalDecompositionBlockHouseholder implements TridiagonalSimilarDecomposition<BlockMatrix64F> {
  protected BlockMatrix64F A;
  protected BlockMatrix64F V=new BlockMatrix64F(1,1);
  protected BlockMatrix64F tmp=new BlockMatrix64F(1,1);
  protected double gammas[]=new double[1];
  protected DenseMatrix64F zerosM=new DenseMatrix64F(1,1);
  @Override public BlockMatrix64F getT(  BlockMatrix64F T){
    if (T == null) {
      T=new BlockMatrix64F(A.numRows,A.numCols,A.blockLength);
    }
 else {
      if (T.numRows != A.numRows || T.numCols != A.numCols)       throw new IllegalArgumentException("T must have the same dimensions as the input matrix");
      CommonOps.fill(T,0);
    }
    T.set(0,0,A.data[0]);
    for (int i=1; i < A.numRows; i++) {
      double d=A.get(i - 1,i);
      T.set(i,i,A.get(i,i));
      T.set(i - 1,i,d);
      T.set(i,i - 1,d);
    }
    return T;
  }
  @Override public BlockMatrix64F getQ(  BlockMatrix64F Q,  boolean transposed){
    Q=BlockMatrix64HouseholderQR.initializeQ(Q,A.numRows,A.numCols,A.blockLength,false);
    int height=Math.min(A.blockLength,A.numRows);
    V.reshape(height,A.numCols,false);
    tmp.reshape(height,A.numCols,false);
    D1Submatrix64F subQ=new D1Submatrix64F(Q);
    D1Submatrix64F subU=new D1Submatrix64F(A);
    D1Submatrix64F subW=new D1Submatrix64F(V);
    D1Submatrix64F tmp=new D1Submatrix64F(this.tmp);
    int N=A.numRows;
    int start=N - N % A.blockLength;
    if (start == N)     start-=A.blockLength;
    if (start < 0)     start=0;
    for (int i=start; i >= 0; i-=A.blockLength) {
      int blockSize=Math.min(A.blockLength,N - i);
      subW.col0=i;
      subW.row1=blockSize;
      subW.original.reshape(subW.row1,subW.col1,false);
      if (transposed) {
        tmp.row0=i;
        tmp.row1=A.numCols;
        tmp.col0=0;
        tmp.col1=blockSize;
      }
 else {
        tmp.col0=i;
        tmp.row1=blockSize;
      }
      tmp.original.reshape(tmp.row1,tmp.col1,false);
      subU.col0=i;
      subU.row0=i;
      subU.row1=subU.row0 + blockSize;
      copyZeros(subU);
      TridiagonalBlockHelper.computeW_row(A.blockLength,subU,subW,gammas,i);
      subQ.col0=i;
      subQ.row0=i;
      if (transposed)       BlockMultiplication.multTransB(A.blockLength,subQ,subU,tmp);
 else       BlockMultiplication.mult(A.blockLength,subU,subQ,tmp);
      if (transposed)       BlockMultiplication.multPlus(A.blockLength,tmp,subW,subQ);
 else       BlockMultiplication.multPlusTransA(A.blockLength,subW,tmp,subQ);
      replaceZeros(subU);
    }
    return Q;
  }
  private void copyZeros(  D1Submatrix64F subU){
    int N=Math.min(A.blockLength,subU.col1 - subU.col0);
    for (int i=0; i < N; i++) {
      for (int j=0; j <= i; j++) {
        zerosM.unsafe_set(i,j,subU.get(i,j));
        subU.set(i,j,0);
      }
      if (subU.col0 + i + 1 < subU.original.numCols) {
        zerosM.unsafe_set(i,i + 1,subU.get(i,i + 1));
        subU.set(i,i + 1,1);
      }
    }
  }
  private void replaceZeros(  D1Submatrix64F subU){
    int N=Math.min(A.blockLength,subU.col1 - subU.col0);
    for (int i=0; i < N; i++) {
      for (int j=0; j <= i; j++) {
        subU.set(i,j,zerosM.get(i,j));
      }
      if (subU.col0 + i + 1 < subU.original.numCols) {
        subU.set(i,i + 1,zerosM.get(i,i + 1));
      }
    }
  }
  @Override public void getDiagonal(  double[] diag,  double[] off){
    diag[0]=A.data[0];
    for (int i=1; i < A.numRows; i++) {
      diag[i]=A.get(i,i);
      off[i - 1]=A.get(i - 1,i);
    }
  }
  @Override public boolean decompose(  BlockMatrix64F orig){
    if (orig.numCols != orig.numRows)     throw new IllegalArgumentException("Input matrix must be square.");
    init(orig);
    D1Submatrix64F subA=new D1Submatrix64F(A);
    D1Submatrix64F subV=new D1Submatrix64F(V);
    D1Submatrix64F subU=new D1Submatrix64F(A);
    int N=orig.numCols;
    for (int i=0; i < N; i+=A.blockLength) {
      int height=Math.min(A.blockLength,A.numRows - i);
      subA.col0=subU.col0=i;
      subA.row0=subU.row0=i;
      subU.row1=subU.row0 + height;
      subV.col0=i;
      subV.row1=height;
      subV.original.reshape(subV.row1,subV.col1,false);
      TridiagonalBlockHelper.tridiagUpperRow(A.blockLength,subA,gammas,subV);
      if (subU.row1 < orig.numCols) {
        double before=subU.get(A.blockLength - 1,A.blockLength);
        subU.set(A.blockLength - 1,A.blockLength,1);
        multPlusTransA(A.blockLength,subU,subV,subA);
        multPlusTransA(A.blockLength,subV,subU,subA);
        subU.set(A.blockLength - 1,A.blockLength,before);
      }
    }
    return true;
  }
  /** 
 * C = C + A^T*B
 * @param blockLength
 * @param A row block vector
 * @param B row block vector
 * @param C
 */
  public static void multPlusTransA(  int blockLength,  D1Submatrix64F A,  D1Submatrix64F B,  D1Submatrix64F C){
    int heightA=Math.min(blockLength,A.row1 - A.row0);
    for (int i=C.row0 + blockLength; i < C.row1; i+=blockLength) {
      int heightC=Math.min(blockLength,C.row1 - i);
      int indexA=A.row0 * A.original.numCols + (i - C.row0 + A.col0) * heightA;
      for (int j=i; j < C.col1; j+=blockLength) {
        int widthC=Math.min(blockLength,C.col1 - j);
        int indexC=i * C.original.numCols + j * heightC;
        int indexB=B.row0 * B.original.numCols + (j - C.col0 + B.col0) * heightA;
        blockMultPlusTransA(A.original.data,B.original.data,C.original.data,indexA,indexB,indexC,heightA,heightC,widthC);
      }
    }
  }
  private void init(  BlockMatrix64F orig){
    this.A=orig;
    int height=Math.min(A.blockLength,A.numRows);
    V.reshape(height,A.numCols,A.blockLength,false);
    tmp.reshape(height,A.numCols,A.blockLength,false);
    if (gammas.length < A.numCols)     gammas=new double[A.numCols];
    zerosM.reshape(A.blockLength,A.blockLength + 1,false);
  }
  @Override public boolean inputModified(){
    return true;
  }
}
