package no.uib.cipr.matrix.sparse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import no.uib.cipr.matrix.AbstractMatrix;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Matrix;
import no.uib.cipr.matrix.MatrixEntry;
import no.uib.cipr.matrix.Vector;
import no.uib.cipr.matrix.io.MatrixInfo;
import no.uib.cipr.matrix.io.MatrixSize;
import no.uib.cipr.matrix.io.MatrixVectorReader;
import com.github.fommil.netlib.BLAS;
/** 
 * Compressed row storage (CRS) matrix
 */
public class CompRowMatrix extends AbstractMatrix {
  /** 
 * Matrix data
 */
  double[] data;
  /** 
 * Column indices. These are kept sorted within each row.
 */
  int[] columnIndex;
  /** 
 * Indices to the start of each row
 */
  int[] rowPointer;
  /** 
 * Constructor for CompRowMatrix
 * @param rReader to get sparse matrix from
 */
  public CompRowMatrix(  MatrixVectorReader r) throws IOException {
    super(0,0);
    MatrixInfo info=null;
    if (r.hasInfo())     info=r.readMatrixInfo();
 else     info=new MatrixInfo(true,MatrixInfo.MatrixField.Real,MatrixInfo.MatrixSymmetry.General);
    if (info.isPattern())     throw new UnsupportedOperationException("Pattern matrices are not supported");
    if (info.isDense())     throw new UnsupportedOperationException("Dense matrices are not supported");
    if (info.isComplex())     throw new UnsupportedOperationException("Complex matrices are not supported");
    MatrixSize size=r.readMatrixSize(info);
    numRows=size.numRows();
    numColumns=size.numColumns();
    int numEntries=size.numEntries();
    int[] row=new int[numEntries];
    int[] column=new int[numEntries];
    double[] entry=new double[numEntries];
    r.readCoordinate(row,column,entry);
    r.add(-1,row);
    r.add(-1,column);
    List<Set<Integer>> rnz=new ArrayList<Set<Integer>>(numRows);
    for (int i=0; i < numRows; ++i)     rnz.add(new HashSet<Integer>());
    for (int i=0; i < numEntries; ++i)     rnz.get(row[i]).add(column[i]);
    if (info.isSymmetric() || info.isSkewSymmetric())     for (int i=0; i < numEntries; ++i)     if (row[i] != column[i])     rnz.get(column[i]).add(row[i]);
    int[][] nz=new int[numRows][];
    for (int i=0; i < numRows; ++i) {
      nz[i]=new int[rnz.get(i).size()];
      int j=0;
      for (      Integer colind : rnz.get(i))       nz[i][j++]=colind;
    }
    construct(nz);
    for (int i=0; i < size.numEntries(); ++i)     set(row[i],column[i],entry[i]);
    if (info.isSymmetric())     for (int i=0; i < numEntries; ++i) {
      if (row[i] != column[i])       set(column[i],row[i],entry[i]);
    }
 else     if (info.isSkewSymmetric())     for (int i=0; i < numEntries; ++i) {
      if (row[i] != column[i])       set(column[i],row[i],-entry[i]);
    }
  }
  /** 
 * Constructor for CompRowMatrix
 * @param numRowsNumber of rows
 * @param numColumnsNumber of columns
 * @param nzThe nonzero column indices on each row
 */
  public CompRowMatrix(  int numRows,  int numColumns,  int[][] nz){
    super(numRows,numColumns);
    construct(nz);
  }
  private void construct(  int[][] nz){
    int nnz=0;
    for (int i=0; i < nz.length; ++i)     nnz+=nz[i].length;
    rowPointer=new int[numRows + 1];
    columnIndex=new int[nnz];
    data=new double[nnz];
    if (nz.length != numRows)     throw new IllegalArgumentException("nz.length != numRows");
    for (int i=1; i <= numRows; ++i) {
      rowPointer[i]=rowPointer[i - 1] + nz[i - 1].length;
      for (int j=rowPointer[i - 1], k=0; j < rowPointer[i]; ++j, ++k) {
        columnIndex[j]=nz[i - 1][k];
        if (nz[i - 1][k] < 0 || nz[i - 1][k] >= numColumns)         throw new IllegalArgumentException("nz[" + (i - 1) + "]["+ k+ "]="+ nz[i - 1][k]+ ", which is not a valid column index");
      }
      Arrays.sort(columnIndex,rowPointer[i - 1],rowPointer[i]);
    }
  }
  private void construct(  Matrix A,  boolean deep){
    if (deep) {
      if (A instanceof CompRowMatrix) {
        CompRowMatrix Ac=(CompRowMatrix)A;
        data=new double[Ac.data.length];
        columnIndex=new int[Ac.columnIndex.length];
        rowPointer=new int[Ac.rowPointer.length];
        System.arraycopy(Ac.data,0,data,0,data.length);
        System.arraycopy(Ac.columnIndex,0,columnIndex,0,columnIndex.length);
        System.arraycopy(Ac.rowPointer,0,rowPointer,0,rowPointer.length);
      }
 else {
        List<Set<Integer>> rnz=new ArrayList<Set<Integer>>(numRows);
        for (int i=0; i < numRows; ++i)         rnz.add(new HashSet<Integer>());
        for (        MatrixEntry e : A)         rnz.get(e.row()).add(e.column());
        int[][] nz=new int[numRows][];
        for (int i=0; i < numRows; ++i) {
          nz[i]=new int[rnz.get(i).size()];
          int j=0;
          for (          Integer colind : rnz.get(i))           nz[i][j++]=colind;
        }
        construct(nz);
        set(A);
      }
    }
 else {
      CompRowMatrix Ac=(CompRowMatrix)A;
      columnIndex=Ac.getColumnIndices();
      rowPointer=Ac.getRowPointers();
      data=Ac.getData();
    }
  }
  /** 
 * Constructor for CompRowMatrix
 * @param ACopies from this matrix
 * @param deepTrue if the copy is to be deep. If it is a shallow copy,
 * <code>A</code> must be a <code>CompRowMatrix</code>
 */
  public CompRowMatrix(  Matrix A,  boolean deep){
    super(A);
    construct(A,deep);
  }
  /** 
 * Constructor for CompRowMatrix
 * @param ACopies from this matrix. The copy will be deep
 */
  public CompRowMatrix(  Matrix A){
    this(A,true);
  }
  /** 
 * Returns the column indices
 */
  public int[] getColumnIndices(){
    return columnIndex;
  }
  /** 
 * Returns the row pointers
 */
  public int[] getRowPointers(){
    return rowPointer;
  }
  /** 
 * Returns the internal data storage
 */
  public double[] getData(){
    return data;
  }
  @Override public Matrix mult(  Matrix B,  Matrix C){
    checkMultAdd(B,C);
    C.zero();
    for (int i=0; i < numRows; ++i) {
      for (int j=0; j < C.numColumns(); ++j) {
        double dot=0;
        for (int k=rowPointer[i]; k < rowPointer[i + 1]; ++k) {
          dot+=data[k] * B.get(columnIndex[k],j);
        }
        if (dot != 0) {
          C.set(i,j,dot);
        }
      }
    }
    return C;
  }
  @Override public Vector mult(  Vector x,  Vector y){
    checkMultAdd(x,y);
    y.zero();
    if (x instanceof DenseVector) {
      double[] xd=((DenseVector)x).getData();
      for (int i=0; i < numRows; ++i) {
        double dot=0;
        for (int j=rowPointer[i]; j < rowPointer[i + 1]; j++) {
          dot+=data[j] * xd[columnIndex[j]];
        }
        if (dot != 0) {
          y.set(i,dot);
        }
      }
      return y;
    }
    for (int i=0; i < numRows; ++i) {
      double dot=0;
      for (int j=rowPointer[i]; j < rowPointer[i + 1]; j++) {
        dot+=data[j] * x.get(columnIndex[j]);
      }
      y.set(i,dot);
    }
    return y;
  }
  @Override public Vector multAdd(  double alpha,  Vector x,  Vector y){
    if (!(x instanceof DenseVector) || !(y instanceof DenseVector))     return super.multAdd(alpha,x,y);
    checkMultAdd(x,y);
    double[] xd=((DenseVector)x).getData();
    double[] yd=((DenseVector)y).getData();
    for (int i=0; i < numRows; ++i) {
      double dot=0;
      for (int j=rowPointer[i]; j < rowPointer[i + 1]; ++j)       dot+=data[j] * xd[columnIndex[j]];
      yd[i]+=alpha * dot;
    }
    return y;
  }
  @Override public Vector transMult(  Vector x,  Vector y){
    if (!(x instanceof DenseVector) || !(y instanceof DenseVector))     return super.transMult(x,y);
    checkTransMultAdd(x,y);
    double[] xd=((DenseVector)x).getData();
    double[] yd=((DenseVector)y).getData();
    y.zero();
    for (int i=0; i < numRows; ++i)     for (int j=rowPointer[i]; j < rowPointer[i + 1]; ++j)     yd[columnIndex[j]]+=data[j] * xd[i];
    return y;
  }
  @Override public Vector transMultAdd(  double alpha,  Vector x,  Vector y){
    if (!(x instanceof DenseVector) || !(y instanceof DenseVector))     return super.transMultAdd(alpha,x,y);
    checkTransMultAdd(x,y);
    double[] xd=((DenseVector)x).getData();
    double[] yd=((DenseVector)y).getData();
    y.scale(1. / alpha);
    for (int i=0; i < numRows; ++i)     for (int j=rowPointer[i]; j < rowPointer[i + 1]; ++j)     yd[columnIndex[j]]+=data[j] * xd[i];
    return y.scale(alpha);
  }
  @Override public void set(  int row,  int column,  double value){
    check(row,column);
    int index=getIndex(row,column);
    data[index]=value;
  }
  @Override public void add(  int row,  int column,  double value){
    check(row,column);
    int index=getIndex(row,column);
    data[index]+=value;
  }
  @Override public double get(  int row,  int column){
    check(row,column);
    int index=no.uib.cipr.matrix.sparse.Arrays.binarySearch(columnIndex,column,rowPointer[row],rowPointer[row + 1]);
    if (index >= 0)     return data[index];
 else     return 0;
  }
  /** 
 * Finds the insertion index
 */
  private int getIndex(  int row,  int column){
    int i=no.uib.cipr.matrix.sparse.Arrays.binarySearch(columnIndex,column,rowPointer[row],rowPointer[row + 1]);
    if (i != -1 && columnIndex[i] == column)     return i;
 else     throw new IndexOutOfBoundsException("Entry (" + (row + 1) + ", "+ (column + 1)+ ") is not in the matrix structure");
  }
  @Override public CompRowMatrix copy(){
    return new CompRowMatrix(this);
  }
  @Override public Iterator<MatrixEntry> iterator(){
    return new CompRowMatrixIterator();
  }
  @Override public CompRowMatrix zero(){
    Arrays.fill(data,0);
    return this;
  }
  @Override public Matrix set(  Matrix B){
    if (!(B instanceof CompRowMatrix))     return super.set(B);
    checkSize(B);
    CompRowMatrix Bc=(CompRowMatrix)B;
    if (Bc.columnIndex.length != columnIndex.length || Bc.rowPointer.length != rowPointer.length) {
      data=new double[Bc.data.length];
      columnIndex=new int[Bc.columnIndex.length];
      rowPointer=new int[Bc.rowPointer.length];
    }
    System.arraycopy(Bc.data,0,data,0,data.length);
    System.arraycopy(Bc.columnIndex,0,columnIndex,0,columnIndex.length);
    System.arraycopy(Bc.rowPointer,0,rowPointer,0,rowPointer.length);
    return this;
  }
  /** 
 * Iterator over a compressed row matrix
 */
private class CompRowMatrixIterator implements Iterator<MatrixEntry> {
    private int row, cursor;
    private CompRowMatrixEntry entry=new CompRowMatrixEntry();
    public CompRowMatrixIterator(){
      nextNonEmptyRow();
    }
    /** 
 * Locates the first non-empty row, starting at the current. After the
 * new row has been found, the cursor is also updated
 */
    private void nextNonEmptyRow(){
      while (row < numRows() && rowPointer[row] == rowPointer[row + 1])       row++;
      cursor=rowPointer[row];
    }
    public boolean hasNext(){
      return cursor < data.length;
    }
    public MatrixEntry next(){
      entry.update(row,cursor);
      if (cursor < rowPointer[row + 1] - 1)       cursor++;
 else {
        row++;
        nextNonEmptyRow();
      }
      return entry;
    }
    public void remove(){
      entry.set(0);
    }
  }
  /** 
 * Entry of a compressed row matrix
 */
private class CompRowMatrixEntry implements MatrixEntry {
    private int row, cursor;
    /** 
 * Updates the entry
 */
    public void update(    int row,    int cursor){
      this.row=row;
      this.cursor=cursor;
    }
    public int row(){
      return row;
    }
    public int column(){
      return columnIndex[cursor];
    }
    public double get(){
      return data[cursor];
    }
    public void set(    double value){
      data[cursor]=value;
    }
  }
}
