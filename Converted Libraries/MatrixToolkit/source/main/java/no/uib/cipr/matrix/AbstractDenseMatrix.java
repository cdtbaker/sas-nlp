package no.uib.cipr.matrix;
import java.text.DecimalFormat;
import java.util.Arrays;
/** 
 * Partial implementation of a dense matrix
 */
abstract class AbstractDenseMatrix extends AbstractMatrix {
  /** 
 * Matrix contents
 */
  double[] data;
  /** 
 * Constructor for AbstractDenseMatrix. The matrix contents will be set to
 * zero
 * @param numRowsNumber of rows
 * @param numColumnsNumber of columns
 */
  public AbstractDenseMatrix(  int numRows,  int numColumns){
    super(numRows,numColumns);
    final long size=(long)numRows * numColumns;
    if (size > Integer.MAX_VALUE) {
      throw new IllegalArgumentException("Matrix of " + numRows + " x "+ numColumns+ " = "+ size+ " elements is too large to be allocated using a single Java array.");
    }
    data=new double[numRows * numColumns];
  }
  /** 
 * Constructor for AbstractDenseMatrix. Matrix is copied from the supplied
 * matrix
 * @param AMatrix to copy from
 */
  public AbstractDenseMatrix(  Matrix A){
    this(A,true);
  }
  /** 
 * Constructor for AbstractDenseMatrix. Matrix is copied from the supplied
 * matrix
 * @param AMatrix to copy from
 * @param deepTrue for deep copy, false for reference
 */
  public AbstractDenseMatrix(  Matrix A,  boolean deep){
    super(A);
    if (deep) {
      data=new double[numRows * numColumns];
      copy(A);
    }
 else     this.data=((AbstractDenseMatrix)A).getData();
  }
  /** 
 * Set this matrix equal to the given matrix
 */
  abstract void copy(  Matrix A);
  /** 
 * Returns the matrix contents. Ordering depends on the underlying storage
 * assumptions
 */
  public double[] getData(){
    return data;
  }
  @Override public void add(  int row,  int column,  double value){
    data[getIndex(row,column)]+=value;
  }
  @Override public void set(  int row,  int column,  double value){
    data[getIndex(row,column)]=value;
  }
  @Override public double get(  int row,  int column){
    return data[getIndex(row,column)];
  }
  /** 
 * Checks the row and column indices, and returns the linear data index
 */
  int getIndex(  int row,  int column){
    check(row,column);
    return row + column * numRows;
  }
  @Override public Matrix set(  Matrix B){
    if (!(getClass().isAssignableFrom(B.getClass())))     return super.set(B);
    checkSize(B);
    double[] Bd=((AbstractDenseMatrix)B).getData();
    if (Bd == data)     return this;
    System.arraycopy(Bd,0,data,0,data.length);
    return this;
  }
  @Override public Matrix zero(){
    Arrays.fill(data,0);
    return this;
  }
  @Override public String toString(){
    StringBuilder out=new StringBuilder();
    DecimalFormat df=new DecimalFormat("####0.00");
    for (int i=0; i < numRows(); i++) {
      for (int j=0; j < numColumns(); j++) {
        double value=get(i,j);
        if (value >= 0)         out.append(" ");
        out.append(" " + df.format(value));
      }
      out.append("\n");
    }
    return out.toString();
  }
}
