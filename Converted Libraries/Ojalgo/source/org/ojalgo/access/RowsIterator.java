package org.ojalgo.access;
import java.util.Iterator;
public class RowsIterator<N extends Number> implements Access1D<N>, Iterator<Access1D<N>> {
final class RowIterable implements Iterable<Access1D<N>> {
    RowIterable(){
      super();
    }
    public Iterator<Access1D<N>> iterator(){
      return RowsIterator.this;
    }
  }
  public static <S extends Number>Iterable<Access1D<S>> make(  final Access2D<S> access){
    return new RowsIterator<S>(access).iterable;
  }
  private final Access2D<N> myAccess2D;
  private long myCurrentRow=-1L;
  final RowIterable iterable=new RowIterable();
  @SuppressWarnings("unused") private RowsIterator(){
    this(null);
  }
  RowsIterator(  final Access2D<N> access){
    super();
    myAccess2D=access;
  }
  public long count(){
    return myAccess2D.countColumns();
  }
  public double doubleValue(  final long index){
    return myAccess2D.doubleValue(myCurrentRow,index);
  }
  public N get(  final long index){
    return myAccess2D.get(myCurrentRow,index);
  }
  public boolean hasNext(){
    return (myCurrentRow + 1L) < myAccess2D.countRows();
  }
  public Iterator<N> iterator(){
    return new Iterator1D<>(this);
  }
  public Access1D<N> next(){
    myCurrentRow++;
    return this;
  }
  public void remove(){
    throw new UnsupportedOperationException();
  }
  public int size(){
    return (int)this.count();
  }
}
