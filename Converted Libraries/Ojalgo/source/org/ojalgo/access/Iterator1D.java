package org.ojalgo.access;
import java.util.Iterator;
public final class Iterator1D<N extends Number> implements Iterator<N> {
  private long cursor=0L;
  private final Access1D<N> myAccess;
  private final long myCount;
  public Iterator1D(  final Access1D<N> access){
    super();
    myAccess=access;
    myCount=access.count();
  }
  @SuppressWarnings("unused") private Iterator1D(){
    this(null);
  }
  public boolean hasNext(){
    return cursor < myCount;
  }
  public N next(){
    return myAccess.get(cursor++);
  }
  public void remove(){
    throw new UnsupportedOperationException();
  }
}
