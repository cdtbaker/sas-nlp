package no.uib.cipr.matrix.sparse;
import java.util.Iterator;
import java.util.List;
/** 
 * An iterator over an array of iterable objects
 */
class SuperIterator<T extends Iterable<E>,E> implements Iterator<SuperIterator.SuperIteratorEntry> {
  private List<T> iterable;
  /** 
 * Two iterators. We need the "next" iterator so that hasNext works properly
 * from one iterable to the next. Using a single iterator won't do
 */
  private Iterator<E> current, next;
  private int currentIndex=0, nextIndex=0;
  /** 
 * Recyled entry returned from next()
 */
  private SuperIteratorEntry<E> entry;
  /** 
 * Constructor for SuperIterator
 * @param iterableIterable objects to iterate over
 */
  public SuperIterator(  List<T> iterable){
    this.iterable=iterable;
    entry=new SuperIteratorEntry<E>();
    if (iterable.size() == 0) {
      current=new DummyIterator();
      next=new DummyIterator();
    }
 else {
      next=iterable.get(nextIndex).iterator();
      moveNext();
      current=iterable.get(currentIndex).iterator();
      moveCurrent();
      if (next.hasNext())       next.next();
    }
  }
  private void moveNext(){
    while (nextIndex < iterable.size() - 1 && !next.hasNext())     next=iterable.get(++nextIndex).iterator();
  }
  private void moveCurrent(){
    while (currentIndex < iterable.size() - 1 && !current.hasNext())     current=iterable.get(++currentIndex).iterator();
  }
  public boolean hasNext(){
    return current.hasNext() || next.hasNext();
  }
  public SuperIteratorEntry<E> next(){
    entry.update(currentIndex,current.next());
    moveCurrent();
    moveNext();
    if (next.hasNext())     next.next();
    return entry;
  }
  public void remove(){
    current.remove();
  }
  /** 
 * Dummy iterator, for degenerate cases
 */
private class DummyIterator implements Iterator<E> {
    public boolean hasNext(){
      return false;
    }
    public E next(){
      return null;
    }
    public void remove(){
      throw new UnsupportedOperationException();
    }
  }
  /** 
 * Entry returned from this superiterator
 */
public static class SuperIteratorEntry<F> {
    /** 
 * Index of the iterator which returned this
 */
    private int i;
    /** 
 * Object returned
 */
    private F o;
    void update(    int i,    F o){
      this.i=i;
      this.o=o;
    }
    public int index(){
      return i;
    }
    public F get(){
      return o;
    }
  }
}
