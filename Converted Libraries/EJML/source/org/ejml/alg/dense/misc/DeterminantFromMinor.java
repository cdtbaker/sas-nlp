package org.ejml.alg.dense.misc;
import org.ejml.data.DenseMatrix64F;
import org.ejml.data.RowD1Matrix64F;
/** 
 * <p>
 * Computes the determinant of a matrix using Laplace expansion.  This is done
 * using minor matrices as is shown below:<br>
 * <br>
 * |A| = Sum{ i=1:k ; a<sub>ij</sub> C<sub>ij</sub> }<br>
 * <br>
 * C<sub>ij</sub> = (-1)<sup>i+j</sup> M<sub>ij</sub><br>
 * <br>
 * Where M_ij is the minor of matrix A formed by eliminating row i and column j from A.
 * </p>
 * <p>
 * This is significantly more computationally expensive than using LU decomposition, but
 * its computation has the advantage being independent of the matrices value.
 * </p>
 * @see org.ejml.alg.dense.decomposition.lu.LUDecompositionAlt
 * @author Peter Abeles
 */
public class DeterminantFromMinor {
  private int width;
  private int minWidth;
  private int[] levelIndexes;
  private double[] levelResults;
  private int[] levelRemoved;
  private int open[];
  private int numOpen;
  private DenseMatrix64F tempMat;
  private boolean dirty=false;
  /** 
 * @param width The width of the matrices that it will be computing the determinant for
 */
  public DeterminantFromMinor(  int width){
    this(width,5);
  }
  /** 
 * @param width The width of the matrices that it will be computing the determinant for
 * @param minWidth At which point should it use a predefined function to compute the determinant.
 */
  public DeterminantFromMinor(  int width,  int minWidth){
    if (minWidth > 5 || minWidth < 2) {
      throw new IllegalArgumentException("No direct function for that width");
    }
    if (width < minWidth)     minWidth=width;
    this.minWidth=minWidth;
    this.width=width;
    int numLevels=width - (minWidth - 2);
    levelResults=new double[numLevels];
    levelRemoved=new int[numLevels];
    levelIndexes=new int[numLevels];
    open=new int[width];
    tempMat=new DenseMatrix64F(minWidth - 1,minWidth - 1);
  }
  /** 
 * Computes the determinant for the specified matrix.  It must be square and have
 * the same width and height as what was specified in the constructor.
 * @param mat The matrix whose determinant is to be computed.
 * @return The determinant.
 */
  public double compute(  RowD1Matrix64F mat){
    if (width != mat.numCols || width != mat.numRows) {
      throw new RuntimeException("Unexpected matrix dimension");
    }
    initStructures();
    int level=0;
    while (true) {
      int levelWidth=width - level;
      int levelIndex=levelIndexes[level];
      if (levelIndex == levelWidth) {
        if (level == 0) {
          return levelResults[0];
        }
        int prevLevelIndex=levelIndexes[level - 1]++;
        double val=mat.get((level - 1) * width + levelRemoved[level - 1]);
        if (prevLevelIndex % 2 == 0) {
          levelResults[level - 1]+=val * levelResults[level];
        }
 else {
          levelResults[level - 1]-=val * levelResults[level];
        }
        putIntoOpen(level - 1);
        levelResults[level]=0;
        levelIndexes[level]=0;
        level--;
      }
 else {
        int excluded=openRemove(levelIndex);
        levelRemoved[level]=excluded;
        if (levelWidth == minWidth) {
          createMinor(mat);
          double subresult=mat.get(level * width + levelRemoved[level]);
          subresult*=UnrolledDeterminantFromMinor.det(tempMat);
          if (levelIndex % 2 == 0) {
            levelResults[level]+=subresult;
          }
 else {
            levelResults[level]-=subresult;
          }
          putIntoOpen(level);
          levelIndexes[level]++;
        }
 else {
          level++;
        }
      }
    }
  }
  private void initStructures(){
    for (int i=0; i < width; i++) {
      open[i]=i;
    }
    numOpen=width;
    if (dirty) {
      for (int i=0; i < levelIndexes.length; i++) {
        levelIndexes[i]=0;
        levelResults[i]=0;
        levelRemoved[i]=0;
      }
    }
    dirty=true;
  }
  private int openRemove(  int where){
    int val=open[where];
    System.arraycopy(open,where + 1,open,where,(numOpen - where - 1));
    numOpen--;
    return val;
  }
  private void openAdd(  int where,  int val){
    for (int i=numOpen; i > where; i--) {
      open[i]=open[i - 1];
    }
    numOpen++;
    open[where]=val;
  }
  private void openAdd(  int val){
    open[numOpen++]=val;
  }
  private void putIntoOpen(  int level){
    boolean added=false;
    for (int i=0; i < numOpen; i++) {
      if (open[i] > levelRemoved[level]) {
        added=true;
        openAdd(i,levelRemoved[level]);
        break;
      }
    }
    if (!added) {
      openAdd(levelRemoved[level]);
    }
  }
  private void createMinor(  RowD1Matrix64F mat){
    int w=minWidth - 1;
    int firstRow=(width - w) * width;
    for (int i=0; i < numOpen; i++) {
      int col=open[i];
      int srcIndex=firstRow + col;
      int dstIndex=i;
      for (int j=0; j < w; j++) {
        tempMat.set(dstIndex,mat.get(srcIndex));
        dstIndex+=w;
        srcIndex+=width;
      }
    }
  }
}
