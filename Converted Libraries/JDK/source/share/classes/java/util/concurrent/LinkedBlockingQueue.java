package java.util.concurrent;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
/** 
 * An optionally-bounded {@linkplain BlockingQueue blocking queue} based on
 * linked nodes.
 * This queue orders elements FIFO (first-in-first-out).
 * The <em>head</em> of the queue is that element that has been on the
 * queue the longest time.
 * The <em>tail</em> of the queue is that element that has been on the
 * queue the shortest time. New elements
 * are inserted at the tail of the queue, and the queue retrieval
 * operations obtain elements at the head of the queue.
 * Linked queues typically have higher throughput than array-based queues but
 * less predictable performance in most concurrent applications.
 * <p> The optional capacity bound constructor argument serves as a
 * way to prevent excessive queue expansion. The capacity, if unspecified,
 * is equal to {@link Integer#MAX_VALUE}.  Linked nodes are
 * dynamically created upon each insertion unless this would bring the
 * queue above capacity.
 * <p>This class and its iterator implement all of the
 * <em>optional</em> methods of the {@link Collection} and {@link Iterator} interfaces.
 * <p>This class is a member of the
 * <a href="{@docRoot}/../technotes/guides/collections/index.html">
 * Java Collections Framework</a>.
 * @since 1.5
 * @author Doug Lea
 * @param<E>
 *  the type of elements held in this collection
 */
public class LinkedBlockingQueue<E> extends AbstractQueue<E> implements BlockingQueue<E>, java.io.Serializable {
  private static final long serialVersionUID=-6903933977591709194L;
  /** 
 * Linked list node class
 */
static class Node<E> {
    E item;
    /** 
 * One of:
 * - the real successor Node
 * - this Node, meaning the successor is head.next
 * - null, meaning there is no successor (this is the last node)
 */
    Node<E> next;
    Node(    E x){
      item=x;
    }
  }
  /** 
 * The capacity bound, or Integer.MAX_VALUE if none 
 */
  private final int capacity;
  /** 
 * Current number of elements 
 */
  private final AtomicInteger count=new AtomicInteger(0);
  /** 
 * Head of linked list.
 * Invariant: head.item == null
 */
  private transient Node<E> head;
  /** 
 * Tail of linked list.
 * Invariant: last.next == null
 */
  private transient Node<E> last;
  /** 
 * Lock held by take, poll, etc 
 */
  private final ReentrantLock takeLock=new ReentrantLock();
  /** 
 * Wait queue for waiting takes 
 */
  private final Condition notEmpty=takeLock.newCondition();
  /** 
 * Lock held by put, offer, etc 
 */
  private final ReentrantLock putLock=new ReentrantLock();
  /** 
 * Wait queue for waiting puts 
 */
  private final Condition notFull=putLock.newCondition();
  /** 
 * Signals a waiting take. Called only from put/offer (which do not
 * otherwise ordinarily lock takeLock.)
 */
  private void signalNotEmpty(){
    final ReentrantLock takeLock=this.takeLock;
    takeLock.lock();
    try {
      notEmpty.signal();
    }
  finally {
      takeLock.unlock();
    }
  }
  /** 
 * Signals a waiting put. Called only from take/poll.
 */
  private void signalNotFull(){
    final ReentrantLock putLock=this.putLock;
    putLock.lock();
    try {
      notFull.signal();
    }
  finally {
      putLock.unlock();
    }
  }
  /** 
 * Links node at end of queue.
 * @param node the node
 */
  private void enqueue(  Node<E> node){
    last=last.next=node;
  }
  /** 
 * Removes a node from head of queue.
 * @return the node
 */
  private E dequeue(){
    Node<E> h=head;
    Node<E> first=h.next;
    h.next=h;
    head=first;
    E x=first.item;
    first.item=null;
    return x;
  }
  /** 
 * Lock to prevent both puts and takes.
 */
  void fullyLock(){
    putLock.lock();
    takeLock.lock();
  }
  /** 
 * Unlock to allow both puts and takes.
 */
  void fullyUnlock(){
    takeLock.unlock();
    putLock.unlock();
  }
  /** 
 * Creates a {@code LinkedBlockingQueue} with a capacity of{@link Integer#MAX_VALUE}.
 */
  public LinkedBlockingQueue(){
    this(Integer.MAX_VALUE);
  }
  /** 
 * Creates a {@code LinkedBlockingQueue} with the given (fixed) capacity.
 * @param capacity the capacity of this queue
 * @throws IllegalArgumentException if {@code capacity} is not greater
 * than zero
 */
  public LinkedBlockingQueue(  int capacity){
    if (capacity <= 0)     throw new IllegalArgumentException();
    this.capacity=capacity;
    last=head=new Node<E>(null);
  }
  /** 
 * Creates a {@code LinkedBlockingQueue} with a capacity of{@link Integer#MAX_VALUE}, initially containing the elements of the
 * given collection,
 * added in traversal order of the collection's iterator.
 * @param c the collection of elements to initially contain
 * @throws NullPointerException if the specified collection or any
 * of its elements are null
 */
  public LinkedBlockingQueue(  Collection<? extends E> c){
    this(Integer.MAX_VALUE);
    final ReentrantLock putLock=this.putLock;
    putLock.lock();
    try {
      int n=0;
      for (      E e : c) {
        if (e == null)         throw new NullPointerException();
        if (n == capacity)         throw new IllegalStateException("Queue full");
        enqueue(new Node<E>(e));
        ++n;
      }
      count.set(n);
    }
  finally {
      putLock.unlock();
    }
  }
  /** 
 * Returns the number of elements in this queue.
 * @return the number of elements in this queue
 */
  public int size(){
    return count.get();
  }
  /** 
 * Returns the number of additional elements that this queue can ideally
 * (in the absence of memory or resource constraints) accept without
 * blocking. This is always equal to the initial capacity of this queue
 * less the current {@code size} of this queue.
 * <p>Note that you <em>cannot</em> always tell if an attempt to insert
 * an element will succeed by inspecting {@code remainingCapacity}because it may be the case that another thread is about to
 * insert or remove an element.
 */
  public int remainingCapacity(){
    return capacity - count.get();
  }
  /** 
 * Inserts the specified element at the tail of this queue, waiting if
 * necessary for space to become available.
 * @throws InterruptedException {@inheritDoc}
 * @throws NullPointerException {@inheritDoc}
 */
  public void put(  E e) throws InterruptedException {
    if (e == null)     throw new NullPointerException();
    int c=-1;
    Node<E> node=new Node(e);
    final ReentrantLock putLock=this.putLock;
    final AtomicInteger count=this.count;
    putLock.lockInterruptibly();
    try {
      while (count.get() == capacity) {
        notFull.await();
      }
      enqueue(node);
      c=count.getAndIncrement();
      if (c + 1 < capacity)       notFull.signal();
    }
  finally {
      putLock.unlock();
    }
    if (c == 0)     signalNotEmpty();
  }
  /** 
 * Inserts the specified element at the tail of this queue, waiting if
 * necessary up to the specified wait time for space to become available.
 * @return {@code true} if successful, or {@code false} if
 * the specified waiting time elapses before space is available.
 * @throws InterruptedException {@inheritDoc}
 * @throws NullPointerException {@inheritDoc}
 */
  public boolean offer(  E e,  long timeout,  TimeUnit unit) throws InterruptedException {
    if (e == null)     throw new NullPointerException();
    long nanos=unit.toNanos(timeout);
    int c=-1;
    final ReentrantLock putLock=this.putLock;
    final AtomicInteger count=this.count;
    putLock.lockInterruptibly();
    try {
      while (count.get() == capacity) {
        if (nanos <= 0)         return false;
        nanos=notFull.awaitNanos(nanos);
      }
      enqueue(new Node<E>(e));
      c=count.getAndIncrement();
      if (c + 1 < capacity)       notFull.signal();
    }
  finally {
      putLock.unlock();
    }
    if (c == 0)     signalNotEmpty();
    return true;
  }
  /** 
 * Inserts the specified element at the tail of this queue if it is
 * possible to do so immediately without exceeding the queue's capacity,
 * returning {@code true} upon success and {@code false} if this queue
 * is full.
 * When using a capacity-restricted queue, this method is generally
 * preferable to method {@link BlockingQueue#add add}, which can fail to
 * insert an element only by throwing an exception.
 * @throws NullPointerException if the specified element is null
 */
  public boolean offer(  E e){
    if (e == null)     throw new NullPointerException();
    final AtomicInteger count=this.count;
    if (count.get() == capacity)     return false;
    int c=-1;
    Node<E> node=new Node(e);
    final ReentrantLock putLock=this.putLock;
    putLock.lock();
    try {
      if (count.get() < capacity) {
        enqueue(node);
        c=count.getAndIncrement();
        if (c + 1 < capacity)         notFull.signal();
      }
    }
  finally {
      putLock.unlock();
    }
    if (c == 0)     signalNotEmpty();
    return c >= 0;
  }
  public E take() throws InterruptedException {
    E x;
    int c=-1;
    final AtomicInteger count=this.count;
    final ReentrantLock takeLock=this.takeLock;
    takeLock.lockInterruptibly();
    try {
      while (count.get() == 0) {
        notEmpty.await();
      }
      x=dequeue();
      c=count.getAndDecrement();
      if (c > 1)       notEmpty.signal();
    }
  finally {
      takeLock.unlock();
    }
    if (c == capacity)     signalNotFull();
    return x;
  }
  public E poll(  long timeout,  TimeUnit unit) throws InterruptedException {
    E x=null;
    int c=-1;
    long nanos=unit.toNanos(timeout);
    final AtomicInteger count=this.count;
    final ReentrantLock takeLock=this.takeLock;
    takeLock.lockInterruptibly();
    try {
      while (count.get() == 0) {
        if (nanos <= 0)         return null;
        nanos=notEmpty.awaitNanos(nanos);
      }
      x=dequeue();
      c=count.getAndDecrement();
      if (c > 1)       notEmpty.signal();
    }
  finally {
      takeLock.unlock();
    }
    if (c == capacity)     signalNotFull();
    return x;
  }
  public E poll(){
    final AtomicInteger count=this.count;
    if (count.get() == 0)     return null;
    E x=null;
    int c=-1;
    final ReentrantLock takeLock=this.takeLock;
    takeLock.lock();
    try {
      if (count.get() > 0) {
        x=dequeue();
        c=count.getAndDecrement();
        if (c > 1)         notEmpty.signal();
      }
    }
  finally {
      takeLock.unlock();
    }
    if (c == capacity)     signalNotFull();
    return x;
  }
  public E peek(){
    if (count.get() == 0)     return null;
    final ReentrantLock takeLock=this.takeLock;
    takeLock.lock();
    try {
      Node<E> first=head.next;
      if (first == null)       return null;
 else       return first.item;
    }
  finally {
      takeLock.unlock();
    }
  }
  /** 
 * Unlinks interior Node p with predecessor trail.
 */
  void unlink(  Node<E> p,  Node<E> trail){
    p.item=null;
    trail.next=p.next;
    if (last == p)     last=trail;
    if (count.getAndDecrement() == capacity)     notFull.signal();
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
    fullyLock();
    try {
      for (Node<E> trail=head, p=trail.next; p != null; trail=p, p=p.next) {
        if (o.equals(p.item)) {
          unlink(p,trail);
          return true;
        }
      }
      return false;
    }
  finally {
      fullyUnlock();
    }
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
    fullyLock();
    try {
      for (Node<E> p=head.next; p != null; p=p.next)       if (o.equals(p.item))       return true;
      return false;
    }
  finally {
      fullyUnlock();
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
    fullyLock();
    try {
      int size=count.get();
      Object[] a=new Object[size];
      int k=0;
      for (Node<E> p=head.next; p != null; p=p.next)       a[k++]=p.item;
      return a;
    }
  finally {
      fullyUnlock();
    }
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
    fullyLock();
    try {
      int size=count.get();
      if (a.length < size)       a=(T[])java.lang.reflect.Array.newInstance(a.getClass().getComponentType(),size);
      int k=0;
      for (Node<E> p=head.next; p != null; p=p.next)       a[k++]=(T)p.item;
      if (a.length > k)       a[k]=null;
      return a;
    }
  finally {
      fullyUnlock();
    }
  }
  public String toString(){
    fullyLock();
    try {
      Node<E> p=head.next;
      if (p == null)       return "[]";
      StringBuilder sb=new StringBuilder();
      sb.append('[');
      for (; ; ) {
        E e=p.item;
        sb.append(e == this ? "(this Collection)" : e);
        p=p.next;
        if (p == null)         return sb.append(']').toString();
        sb.append(',').append(' ');
      }
    }
  finally {
      fullyUnlock();
    }
  }
  /** 
 * Atomically removes all of the elements from this queue.
 * The queue will be empty after this call returns.
 */
  public void clear(){
    fullyLock();
    try {
      for (Node<E> p, h=head; (p=h.next) != null; h=p) {
        h.next=h;
        p.item=null;
      }
      head=last;
      if (count.getAndSet(0) == capacity)       notFull.signal();
    }
  finally {
      fullyUnlock();
    }
  }
  /** 
 * @throws UnsupportedOperationException {@inheritDoc}
 * @throws ClassCastException            {@inheritDoc}
 * @throws NullPointerException          {@inheritDoc}
 * @throws IllegalArgumentException      {@inheritDoc}
 */
  public int drainTo(  Collection<? super E> c){
    return drainTo(c,Integer.MAX_VALUE);
  }
  /** 
 * @throws UnsupportedOperationException {@inheritDoc}
 * @throws ClassCastException            {@inheritDoc}
 * @throws NullPointerException          {@inheritDoc}
 * @throws IllegalArgumentException      {@inheritDoc}
 */
  public int drainTo(  Collection<? super E> c,  int maxElements){
    if (c == null)     throw new NullPointerException();
    if (c == this)     throw new IllegalArgumentException();
    boolean signalNotFull=false;
    final ReentrantLock takeLock=this.takeLock;
    takeLock.lock();
    try {
      int n=Math.min(maxElements,count.get());
      Node<E> h=head;
      int i=0;
      try {
        while (i < n) {
          Node<E> p=h.next;
          c.add(p.item);
          p.item=null;
          h.next=h;
          h=p;
          ++i;
        }
        return n;
      }
  finally {
        if (i > 0) {
          head=h;
          signalNotFull=(count.getAndAdd(-i) == capacity);
        }
      }
    }
  finally {
      takeLock.unlock();
      if (signalNotFull)       signalNotFull();
    }
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
    private Node<E> current;
    private Node<E> lastRet;
    private E currentElement;
    Itr(){
      fullyLock();
      try {
        current=head.next;
        if (current != null)         currentElement=current.item;
      }
  finally {
        fullyUnlock();
      }
    }
    public boolean hasNext(){
      return current != null;
    }
    /** 
 * Returns the next live successor of p, or null if no such.
 * Unlike other traversal methods, iterators need to handle both:
 * - dequeued nodes (p.next == p)
 * - (possibly multiple) interior removed nodes (p.item == null)
 */
    private Node<E> nextNode(    Node<E> p){
      for (; ; ) {
        Node<E> s=p.next;
        if (s == p)         return head.next;
        if (s == null || s.item != null)         return s;
        p=s;
      }
    }
    public E next(){
      fullyLock();
      try {
        if (current == null)         throw new NoSuchElementException();
        E x=currentElement;
        lastRet=current;
        current=nextNode(current);
        currentElement=(current == null) ? null : current.item;
        return x;
      }
  finally {
        fullyUnlock();
      }
    }
    public void remove(){
      if (lastRet == null)       throw new IllegalStateException();
      fullyLock();
      try {
        Node<E> node=lastRet;
        lastRet=null;
        for (Node<E> trail=head, p=trail.next; p != null; trail=p, p=p.next) {
          if (p == node) {
            unlink(p,trail);
            break;
          }
        }
      }
  finally {
        fullyUnlock();
      }
    }
  }
  /** 
 * Save the state to a stream (that is, serialize it).
 * @serialData The capacity is emitted (int), followed by all of
 * its elements (each an {@code Object}) in the proper order,
 * followed by a null
 * @param s the stream
 */
  private void writeObject(  java.io.ObjectOutputStream s) throws java.io.IOException {
    fullyLock();
    try {
      s.defaultWriteObject();
      for (Node<E> p=head.next; p != null; p=p.next)       s.writeObject(p.item);
      s.writeObject(null);
    }
  finally {
      fullyUnlock();
    }
  }
  /** 
 * Reconstitute this queue instance from a stream (that is,
 * deserialize it).
 * @param s the stream
 */
  private void readObject(  java.io.ObjectInputStream s) throws java.io.IOException, ClassNotFoundException {
    s.defaultReadObject();
    count.set(0);
    last=head=new Node<E>(null);
    for (; ; ) {
      @SuppressWarnings("unchecked") E item=(E)s.readObject();
      if (item == null)       break;
      add(item);
    }
  }
}