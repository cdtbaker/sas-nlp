package org.ojalgo.matrix.jama;
import java.util.Iterator;
import org.ojalgo.access.Access1D;
import org.ojalgo.access.Access2D;
import org.ojalgo.access.Iterator1D;
import org.ojalgo.access.RowsIterator;
final class ColumnsMatrix implements Access2D<Double> {
  private final long myColumnLength;
  private final double[][] myColumns;
  public ColumnsMatrix(  final int rowsCount,  final int columnsCount){
    super();
    myColumns=new double[columnsCount][rowsCount];
    myColumnLength=rowsCount;
  }
  @SuppressWarnings("unused") private ColumnsMatrix(){
    this(0,0);
  }
  public double[] column(  final int column){
    return myColumns[column];
  }
  public Iterator<double[]> columns(){
    return new Iterator<double[]>(){
      private int myNextCol=0;
      public boolean hasNext(){
        return myNextCol < myColumns.length;
      }
      public double[] next(){
        return myColumns[myNextCol++];
      }
      public void remove(){
        throw new UnsupportedOperationException();
      }
    }
;
  }
  public long count(){
    return myColumns.length * myColumnLength;
  }
  public long countColumns(){
    return myColumns.length;
  }
  public long countRows(){
    return myColumnLength;
  }
  @Override public double doubleValue(  final long index){
    return myColumns[(int)(index / myColumnLength)][(int)(index % myColumnLength)];
  }
  @Override public double doubleValue(  final long row,  final long column){
    return myColumns[(int)column][(int)row];
  }
  public Double get(  final long index){
    return myColumns[(int)(index / myColumnLength)][(int)(index % myColumnLength)];
  }
  @Override public Double get(  final long row,  final long column){
    return myColumns[(int)column][(int)row];
  }
  @Override public int getColDim(){
    return myColumns.length;
  }
  @Override public int getRowDim(){
    return (int)this.countRows();
  }
  public Iterator<Double> iterator(){
    return new Iterator1D<Double>(this);
  }
  public Iterable<Access1D<Double>> rows(){
    return RowsIterator.make(this);
  }
  public int size(){
    return (int)this.count();
  }
}
