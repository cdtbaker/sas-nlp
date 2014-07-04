package org.ojalgo.access;
import java.util.Iterator;
public class ColumnsIterator<N extends Number> implements Access1D<N>, Iterator<Access1D<N>> {
final class ColumnIterable implements Iterable<Access1D<N>> {
    ColumnIterable(){
      super();
    }
    public Iterator<Access1D<N>> iterator(){
      return ColumnsIterator.this;
    }
  }
  public static <S extends Number>Iterable<Access1D<S>> make(  final Access2D<S> access){
    return new ColumnsIterator<S>(access).iterable;
  }
  private final Access2D<N> myAccess2D;
  private long myCurrentColumn=-1L;
  final ColumnIterable iterable=new ColumnIterable();
  @SuppressWarnings("unused") private ColumnsIterator(){
    this(null);
  }
  ColumnsIterator(  final Access2D<N> access){
    super();
    myAccess2D=access;
  }
  public long count(){
    return myAccess2D.countRows();
  }
  public double doubleValue(  final long index){
    return myAccess2D.doubleValue(index,myCurrentColumn);
  }
  public N get(  final long index){
    return myAccess2D.get(index,myCurrentColumn);
  }
  public boolean hasNext(){
    return (myCurrentColumn + 1L) < myAccess2D.countColumns();
  }
  public Iterator<N> iterator(){
    return new Iterator1D<>(this);
  }
  public Access1D<N> next(){
    myCurrentColumn++;
    return this;
  }
  public void remove(){
    throw new UnsupportedOperationException();
  }
  public int size(){
    return (int)this.count();
  }
}
