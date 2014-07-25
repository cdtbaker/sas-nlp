package java.util.concurrent;
import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;
/** 
 * An unbounded thread-safe {@linkplain Queue queue} based on linked nodes.
 * This queue orders elements FIFO (first-in-first-out).
 * The <em>head</em> of the queue is that element that has been on the
 * queue the longest time.
 * The <em>tail</em> of the queue is that element that has been on the
 * queue the shortest time. New elements
 * are inserted at the tail of the queue, and the queue retrieval
 * operations obtain elements at the head of the queue.
 * A {@code ConcurrentLinkedQueue} is an appropriate choice when
 * many threads will share access to a common collection.
 * Like most other concurrent collection implementations, this class
 * does not permit the use of {@code null} elements.
 * <p>This implementation employs an efficient &quot;wait-free&quot;
 * algorithm based on one described in <a
 * href="http://www.cs.rochester.edu/u/michael/PODC96.html"> Simple,
 * Fast, and Practical Non-Blocking and Blocking Concurrent Queue
 * Algorithms</a> by Maged M. Michael and Michael L. Scott.
 * <p>Iterators are <i>weakly consistent</i>, returning elements
 * reflecting the state of the queue at some point at or since the
 * creation of the iterator.  They do <em>not</em> throw {@link java.util.ConcurrentModificationException}, and may proceed concurrently
 * with other operations.  Elements contained in the queue since the creation
 * of the iterator will be returned exactly once.
 * <p>Beware that, unlike in most collections, the {@code size} method
 * is <em>NOT</em> a constant-time operation. Because of the
 * asynchronous nature of these queues, determining the current number
 * of elements requires a traversal of the elements, and so may report
 * inaccurate results if this collection is modified during traversal.
 * Additionally, the bulk operations {@code addAll},{@code removeAll}, {@code retainAll}, {@code containsAll},{@code equals}, and {@code toArray} are <em>not</em> guaranteed
 * to be performed atomically. For example, an iterator operating
 * concurrently with an {@code addAll} operation might view only some
 * of the added elements.
 * <p>This class and its iterator implement all of the <em>optional</em>
 * methods of the {@link Queue} and {@link Iterator} interfaces.
 * <p>Memory consistency effects: As with other concurrent
 * collections, actions in a thread prior to placing an object into a{@code ConcurrentLinkedQueue}<a href="package-summary.html#MemoryVisibility"><i>happen-before</i></a>
 * actions subsequent to the access or removal of that element from
 * the {@code ConcurrentLinkedQueue} in another thread.
 * <p>This class is a member of the
 * <a href="{@docRoot}/../technotes/guides/collections/index.html">
 * Java Collections Framework</a>.
 * @since 1.5
 * @author Doug Lea
 * @param<E>
 *  the type of elements held in this collection
 */
public class ConcurrentLinkedQueue<E> extends AbstractQueue<E> implements Queue<E>, java.io.Serializable {
  private static final long serialVersionUID=196745693267521676L;
private static class Node<E> {
    volatile E item;
    volatile Node<E> next;
    /** 
 * Constructs a new node.  Uses relaxed write because item can
 * only be seen after publication via casNext.
 */
    Node(    E item){
      UNSAFE.putObject(this,itemOffset,item);
    }
    boolean casItem(    E cmp,    E val){
      return UNSAFE.compareAndSwapObject(this,itemOffset,cmp,val);
    }
    void lazySetNext(    Node<E> val){
      UNSAFE.putOrderedObject(this,nextOffset,val);
    }
    boolean casNext(    Node<E> cmp,    Node<E> val){
      return UNSAFE.compareAndSwapObject(this,nextOffset,cmp,val);
    }
    private static final sun.misc.Unsafe UNSAFE;
    private static final long itemOffset;
    private static final long nextOffset;
static {
      try {
        UNSAFE=sun.misc.Unsafe.getUnsafe();
        Class k=Node.class;
        itemOffset=UNSAFE.objectFieldOffset(k.getDeclaredField("item"));
        nextOffset=UNSAFE.objectFieldOffset(k.getDeclaredField("next"));
      }
 catch (      Exception e) {
        throw new Error(e);
      }
    }
  }
  /** 
 * A node from which the first live (non-deleted) node (if any)
 * can be reached in O(1) time.
 * Invariants:
 * - all live nodes are reachable from head via succ()
 * - head != null
 * - (tmp = head).next != tmp || tmp != head
 * Non-invariants:
 * - head.item may or may not be null.
 * - it is permitted for tail to lag behind head, that is, for tail
 * to not be reachable from head!
 */
  private transient volatile Node<E> head;
  /** 
 * A node from which the last node on list (that is, the unique
 * node with node.next == null) can be reached in O(1) time.
 * Invariants:
 * - the last node is always reachable from tail via succ()
 * - tail != null
 * Non-invariants:
 * - tail.item may or may not be null.
 * - it is permitted for tail to lag behind head, that is, for tail
 * to not be reachable from head!
 * - tail.next may or may not be self-pointing to tail.
 */
  private transient volatile Node<E> tail;
  /** 
 * Creates a {@code ConcurrentLinkedQueue} that is initially empty.
 */
  public ConcurrentLinkedQueue(){
    head=tail=new Node<E>(null);
  }
  /** 
 * Creates a {@code ConcurrentLinkedQueue}initially containing the elements of the given collection,
 * added in traversal order of the collection's iterator.
 * @param c the collection of elements to initially contain
 * @throws NullPointerException if the specified collection or any
 * of its elements are null
 */
  public ConcurrentLinkedQueue(  Collection<? extends E> c){
    Node<E> h=null, t=null;
    for (    E e : c) {
      checkNotNull(e);
      Node<E> newNode=new Node<E>(e);
      if (h == null)       h=t=newNode;
 else {
        t.lazySetNext(newNode);
        t=newNode;
      }
    }
    if (h == null)     h=t=new Node<E>(null);
    head=h;
    tail=t;
  }
  /** 
 * Inserts the specified element at the tail of this queue.
 * As the queue is unbounded, this method will never throw{@link IllegalStateException} or return {@code false}.
 * @return {@code true} (as specified by {@link Collection#add})
 * @throws NullPointerException if the specified element is null
 */
  public boolean add(  E e){
    return offer(e);
  }
  /** 
 * Try to CAS head to p. If successful, repoint old head to itself
 * as sentinel for succ(), below.
 */
  final void updateHead(  Node<E> h,  Node<E> p){
    if (h != p && casHead(h,p))     h.lazySetNext(h);
  }
  /** 
 * Returns the successor of p, or the head node if p.next has been
 * linked to self, which will only be true if traversing with a
 * stale pointer that is now off the list.
 */
  final Node<E> succ(  Node<E> p){
    Node<E> next=p.next;
    return (p == next) ? head : next;
  }
  /** 
 * Inserts the specified element at the tail of this queue.
 * As the queue is unbounded, this method will never return {@code false}.
 * @return {@code true} (as specified by {@link Queue#offer})
 * @throws NullPointerException if the specified element is null
 */
  public boolean offer(  E e){
    checkNotNull(e);
    final Node<E> newNode=new Node<E>(e);
    for (Node<E> t=tail, p=t; ; ) {
      Node<E> q=p.next;
      if (q == null) {
        if (p.casNext(null,newNode)) {
          if (p != t)           casTail(t,newNode);
          return true;
        }
      }
 else       if (p == q)       p=(t != (t=tail)) ? t : head;
 else       p=(p != t && t != (t=tail)) ? t : q;
    }
  }
  public E poll(){
    restartFromHead:     for (; ; ) {
      for (Node<E> h=head, p=h, q; ; ) {
        E item=p.item;
        if (item != null && p.casItem(item,null)) {
          if (p != h)           updateHead(h,((q=p.next) != null) ? q : p);
          return item;
        }
 else         if ((q=p.next) == null) {
          updateHead(h,p);
          return null;
        }
 else         if (p == q)         continue restartFromHead;
 else         p=q;
      }
    }
  }
  public E peek(){
    restartFromHead:     for (; ; ) {
      for (Node<E> h=head, p=h, q; ; ) {
        E item=p.item;
        if (item != null || (q=p.next) == null) {
          updateHead(h,p);
          return item;
        }
 else         if (p == q)         continue restartFromHead;
 else         p=q;
      }
    }
  }
  /** 
 * Returns the first live (non-deleted) node on list, or null if none.
 * This is yet another variant of poll/peek; here returning the
 * first node, not element.  We could make peek() a wrapper around
 * first(), but that would cost an extra volatile read of item,
 * and the need to add a retry loop to deal with the possibility
 * of losing a race to a concurrent poll().
 */
  Node<E> first(){
    restartFromHead:     for (; ; ) {
      for (Node<E> h=head, p=h, q; ; ) {
        boolean hasItem=(p.item != null);
        if (hasItem || (q=p.next) == null) {
          updateHead(h,p);
          return hasItem ? p : null;
        }
 else         if (p == q)         continue restartFromHead;
 else         p=q;
      }
    }
  }
  /** 
 * Returns {@code true} if this queue contains no elements.
 * @return {@code true} if this queue contains no elements
 */
  public boolean isEmpty(){
    return first() == null;
  }
  /** 
 * Returns the number of elements in this queue.  If this queue
 * contains more than {@code Integer.MAX_VALUE} elements, returns{@code Integer.MAX_VALUE}.
 * <p>Beware that, unlike in most collections, this method is
 * <em>NOT</em> a constant-time operation. Because of the
 * asynchronous nature of these queues, determining the current
 * number of elements requires an O(n) traversal.
 * Additionally, if elements are added or removed during execution
 * of this method, the returned result may be inaccurate.  Thus,
 * this method is typically not very useful in concurrent
 * applications.
 * @return the number of elements in this queue
 */
  public int size(){
    int count=0;
    for (Node<E> p=first(); p != null; p=succ(p))     if (p.item != null)     if (++count == Integer.MAX_VALUE)     break;
    return count;
  }
  /** 
 * Returns {@code true} if this queue contains the specified element.
 * More formally, returns {@code true} if and only if this queue contains
 * at least one element {@code e} such that {@code o.equals(e)}.
 * @param o object to be checked for containment in this queue
 * @return {@code true} if this queue contains the specified element
 */
  public boolean contains(  Object o){
    if (o == null)     return false;
    for (Node<E> p=first(); p != null; p=succ(p)) {
      E item=p.item;
      if (item != null && o.equals(item))       return true;
    }
    return false;
  }
  /** 
 * Removes a single instance of the specified element from this queue,
 * if it is present.  More formally, removes an element {@code e} such
 * that {@code o.equals(e)}, if this queue contains one or more such
 * elements.
 * Returns {@code true} if this queue contained the specified element
 * (or equivalently, if this queue changed as a result of the call).
 * @param o element to be removed from this queue, if present
 * @return {@code true} if this queue changed as a result of the call
 */
  public boolean remove(  Object o){
    if (o == null)     return false;
    Node<E> pred=null;
    for (Node<E> p=first(); p != null; p=succ(p)) {
      E item=p.item;
      if (item != null && o.equals(item) && p.casItem(item,null)) {
        Node<E> next=succ(p);
        if (pred != null && next != null)         pred.casNext(p,next);
        return true;
      }
      pred=p;
    }
    return false;
  }
  /** 
 * Appends all of the elements in the specified collection to the end of
 * this queue, in the order that they are returned by the specified
 * collection's iterator.  Attempts to {@code addAll} of a queue to
 * itself result in {@code IllegalArgumentException}.
 * @param c the elements to be inserted into this queue
 * @return {@code true} if this queue changed as a result of the call
 * @throws NullPointerException if the specified collection or any
 * of its elements are null
 * @throws IllegalArgumentException if the collection is this queue
 */
  public boolean addAll(  Collection<? extends E> c){
    if (c == this)     throw new IllegalArgumentException();
    Node<E> beginningOfTheEnd=null, last=null;
    for (    E e : c) {
      checkNotNull(e);
      Node<E> newNode=new Node<E>(e);
      if (beginningOfTheEnd == null)       beginningOfTheEnd=last=newNode;
 else {
        last.lazySetNext(newNode);
        last=newNode;
      }
    }
    if (beginningOfTheEnd == null)     return false;
    for (Node<E> t=tail, p=t; ; ) {
      Node<E> q=p.next;
      if (q == null) {
        if (p.casNext(null,beginningOfTheEnd)) {
          if (!casTail(t,last)) {
            t=tail;
            if (last.next == null)             casTail(t,last);
          }
          return true;
        }
      }
 else       if (p == q)       p=(t != (t=tail)) ? t : head;
 else       p=(p != t && t != (t=tail)) ? t : q;
    }
  }
  /** 
 * Returns an array containing all of the elements in this queue, in
 * proper sequence.
 * <p>The returned array will be "safe" in that no references to it are
 * maintained by this queue.  (In other words, this method must allocate
 * a new array).  The caller is thus free to modify the returned array.
 * <p>This method acts as bridge between array-based and collection-based
 * APIs.
 * @return an array containing all of the elements in this queue
 */
  public Object[] toArray(){
    ArrayList<E> al=new ArrayList<E>();
    for (Node<E> p=first(); p != null; p=succ(p)) {
      E item=p.item;
      if (item != null)       al.add(item);
    }
    return al.toArray();
  }
  /** 
 * Returns an array containing all of the elements in this queue, in
 * proper sequence; the runtime type of the returned array is that of
 * the specified array.  If the queue fits in the specified array, it
 * is returned therein.  Otherwise, a new array is allocated with the
 * runtime type of the specified array and the size of this queue.
 * <p>If this queue fits in the specified array with room to spare
 * (i.e., the array has more elements than this queue), the element in
 * the array immediately following the end of the queue is set to{@code null}.
 * <p>Like the {@link #toArray()} method, this method acts as bridge between
 * array-based and collection-based APIs.  Further, this method allows
 * precise control over the runtime type of the output array, and may,
 * under certain circumstances, be used to save allocation costs.
 * <p>Suppose {@code x} is a queue known to contain only strings.
 * The following code can be used to dump the queue into a newly
 * allocated array of {@code String}:
 * <pre>
 * String[] y = x.toArray(new String[0]);</pre>
 * Note that {@code toArray(new Object[0])} is identical in function to{@code toArray()}.
 * @param a the array into which the elements of the queue are to
 * be stored, if it is big enough; otherwise, a new array of the
 * same runtime type is allocated for this purpose
 * @return an array containing all of the elements in this queue
 * @throws ArrayStoreException if the runtime type of the specified array
 * is not a supertype of the runtime type of every element in
 * this queue
 * @throws NullPointerException if the specified array is null
 */
  @SuppressWarnings("unchecked") public <T>T[] toArray(  T[] a){
    int k=0;
    Node<E> p;
    for (p=first(); p != null && k < a.length; p=succ(p)) {
      E item=p.item;
      if (item != null)       a[k++]=(T)item;
    }
    if (p == null) {
      if (k < a.length)       a[k]=null;
      return a;
    }
    ArrayList<E> al=new ArrayList<E>();
    for (Node<E> q=first(); q != null; q=succ(q)) {
      E item=q.item;
      if (item != null)       al.add(item);
    }
    return al.toArray(a);
  }
  /** 
 * Returns an iterator over the elements in this queue in proper sequence.
 * The elements will be returned in order from first (head) to last (tail).
 * <p>The returned iterator is a "weakly consistent" iterator that
 * will never throw {@link java.util.ConcurrentModificationExceptionConcurrentModificationException}, and guarantees to traverse
 * elements as they existed upon construction of the iterator, and
 * may (but is not guaranteed to) reflect any modifications
 * subsequent to construction.
 * @return an iterator over the elements in this queue in proper sequence
 */
  public Iterator<E> iterator(){
    return new Itr();
  }
private class Itr implements Iterator<E> {
    /** 
 * Next node to return item for.
 */
    private Node<E> nextNode;
    /** 
 * nextItem holds on to item fields because once we claim
 * that an element exists in hasNext(), we must return it in
 * the following next() call even if it was in the process of
 * being removed when hasNext() was called.
 */
    private E nextItem;
    /** 
 * Node of the last returned item, to support remove.
 */
    private Node<E> lastRet;
    Itr(){
      advance();
    }
    /** 
 * Moves to next valid node and returns item to return for
 * next(), or null if no such.
 */
    private E advance(){
      lastRet=nextNode;
      E x=nextItem;
      Node<E> pred, p;
      if (nextNode == null) {
        p=first();
        pred=null;
      }
 else {
        pred=nextNode;
        p=succ(nextNode);
      }
      for (; ; ) {
        if (p == null) {
          nextNode=null;
          nextItem=null;
          return x;
        }
        E item=p.item;
        if (item != null) {
          nextNode=p;
          nextItem=item;
          return x;
        }
 else {
          Node<E> next=succ(p);
          if (pred != null && next != null)           pred.casNext(p,next);
          p=next;
        }
      }
    }
    public boolean hasNext(){
      return nextNode != null;
    }
    public E next(){
      if (nextNode == null)       throw new NoSuchElementException();
      return advance();
    }
    public void remove(){
      Node<E> l=lastRet;
      if (l == null)       throw new IllegalStateException();
      l.item=null;
      lastRet=null;
    }
  }
  /** 
 * Saves the state to a stream (that is, serializes it).
 * @serialData All of the elements (each an {@code E}) in
 * the proper order, followed by a null
 * @param s the stream
 */
  private void writeObject(  java.io.ObjectOutputStream s) throws java.io.IOException {
    s.defaultWriteObject();
    for (Node<E> p=first(); p != null; p=succ(p)) {
      Object item=p.item;
      if (item != null)       s.writeObject(item);
    }
    s.writeObject(null);
  }
  /** 
 * Reconstitutes the instance from a stream (that is, deserializes it).
 * @param s the stream
 */
  private void readObject(  java.io.ObjectInputStream s) throws java.io.IOException, ClassNotFoundException {
    s.defaultReadObject();
    Node<E> h=null, t=null;
    Object item;
    while ((item=s.readObject()) != null) {
      @SuppressWarnings("unchecked") Node<E> newNode=new Node<E>((E)item);
      if (h == null)       h=t=newNode;
 else {
        t.lazySetNext(newNode);
        t=newNode;
      }
    }
    if (h == null)     h=t=new Node<E>(null);
    head=h;
    tail=t;
  }
  /** 
 * Throws NullPointerException if argument is null.
 * @param v the element
 */
  private static void checkNotNull(  Object v){
    if (v == null)     throw new NullPointerException();
  }
  private boolean casTail(  Node<E> cmp,  Node<E> val){
    return UNSAFE.compareAndSwapObject(this,tailOffset,cmp,val);
  }
  private boolean casHead(  Node<E> cmp,  Node<E> val){
    return UNSAFE.compareAndSwapObject(this,headOffset,cmp,val);
  }
  private static final sun.misc.Unsafe UNSAFE;
  private static final long headOffset;
  private static final long tailOffset;
static {
    try {
      UNSAFE=sun.misc.Unsafe.getUnsafe();
      Class k=ConcurrentLinkedQueue.class;
      headOffset=UNSAFE.objectFieldOffset(k.getDeclaredField("head"));
      tailOffset=UNSAFE.objectFieldOffset(k.getDeclaredField("tail"));
    }
 catch (    Exception e) {
      throw new Error(e);
    }
  }
}
