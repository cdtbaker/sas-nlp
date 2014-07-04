package java.util.concurrent;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;
/** 
 * An unbounded concurrent {@linkplain Deque deque} based on linked nodes.
 * Concurrent insertion, removal, and access operations execute safely
 * across multiple threads.
 * A {@code ConcurrentLinkedDeque} is an appropriate choice when
 * many threads will share access to a common collection.
 * Like most other concurrent collection implementations, this class
 * does not permit the use of {@code null} elements.
 * <p>Iterators are <i>weakly consistent</i>, returning elements
 * reflecting the state of the deque at some point at or since the
 * creation of the iterator.  They do <em>not</em> throw {@link java.util.ConcurrentModificationExceptionConcurrentModificationException}, and may proceed concurrently with
 * other operations.
 * <p>Beware that, unlike in most collections, the {@code size} method
 * is <em>NOT</em> a constant-time operation. Because of the
 * asynchronous nature of these deques, determining the current number
 * of elements requires a traversal of the elements, and so may report
 * inaccurate results if this collection is modified during traversal.
 * Additionally, the bulk operations {@code addAll},{@code removeAll}, {@code retainAll}, {@code containsAll},{@code equals}, and {@code toArray} are <em>not</em> guaranteed
 * to be performed atomically. For example, an iterator operating
 * concurrently with an {@code addAll} operation might view only some
 * of the added elements.
 * <p>This class and its iterator implement all of the <em>optional</em>
 * methods of the {@link Deque} and {@link Iterator} interfaces.
 * <p>Memory consistency effects: As with other concurrent collections,
 * actions in a thread prior to placing an object into a{@code ConcurrentLinkedDeque}<a href="package-summary.html#MemoryVisibility"><i>happen-before</i></a>
 * actions subsequent to the access or removal of that element from
 * the {@code ConcurrentLinkedDeque} in another thread.
 * <p>This class is a member of the
 * <a href="{@docRoot}/../technotes/guides/collections/index.html">
 * Java Collections Framework</a>.
 * @since 1.7
 * @author Doug Lea
 * @author Martin Buchholz
 * @param<E>
 *  the type of elements held in this collection
 */
public class ConcurrentLinkedDeque<E> extends AbstractCollection<E> implements Deque<E>, java.io.Serializable {
  private static final long serialVersionUID=876323262645176354L;
  /** 
 * A node from which the first node on list (that is, the unique node p
 * with p.prev == null && p.next != p) can be reached in O(1) time.
 * Invariants:
 * - the first node is always O(1) reachable from head via prev links
 * - all live nodes are reachable from the first node via succ()
 * - head != null
 * - (tmp = head).next != tmp || tmp != head
 * - head is never gc-unlinked (but may be unlinked)
 * Non-invariants:
 * - head.item may or may not be null
 * - head may not be reachable from the first or last node, or from tail
 */
  private transient volatile Node<E> head;
  /** 
 * A node from which the last node on list (that is, the unique node p
 * with p.next == null && p.prev != p) can be reached in O(1) time.
 * Invariants:
 * - the last node is always O(1) reachable from tail via next links
 * - all live nodes are reachable from the last node via pred()
 * - tail != null
 * - tail is never gc-unlinked (but may be unlinked)
 * Non-invariants:
 * - tail.item may or may not be null
 * - tail may not be reachable from the first or last node, or from head
 */
  private transient volatile Node<E> tail;
  private static final Node<Object> PREV_TERMINATOR, NEXT_TERMINATOR;
  @SuppressWarnings("unchecked") Node<E> prevTerminator(){
    return (Node<E>)PREV_TERMINATOR;
  }
  @SuppressWarnings("unchecked") Node<E> nextTerminator(){
    return (Node<E>)NEXT_TERMINATOR;
  }
static final class Node<E> {
    volatile Node<E> prev;
    volatile E item;
    volatile Node<E> next;
    Node(){
    }
    /** 
 * Constructs a new node.  Uses relaxed write because item can
 * only be seen after publication via casNext or casPrev.
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
    void lazySetPrev(    Node<E> val){
      UNSAFE.putOrderedObject(this,prevOffset,val);
    }
    boolean casPrev(    Node<E> cmp,    Node<E> val){
      return UNSAFE.compareAndSwapObject(this,prevOffset,cmp,val);
    }
    private static final sun.misc.Unsafe UNSAFE;
    private static final long prevOffset;
    private static final long itemOffset;
    private static final long nextOffset;
static {
      try {
        UNSAFE=sun.misc.Unsafe.getUnsafe();
        Class k=Node.class;
        prevOffset=UNSAFE.objectFieldOffset(k.getDeclaredField("prev"));
        itemOffset=UNSAFE.objectFieldOffset(k.getDeclaredField("item"));
        nextOffset=UNSAFE.objectFieldOffset(k.getDeclaredField("next"));
      }
 catch (      Exception e) {
        throw new Error(e);
      }
    }
  }
  /** 
 * Links e as first element.
 */
  private void linkFirst(  E e){
    checkNotNull(e);
    final Node<E> newNode=new Node<E>(e);
    restartFromHead:     for (; ; )     for (Node<E> h=head, p=h, q; ; ) {
      if ((q=p.prev) != null && (q=(p=q).prev) != null)       p=(h != (h=head)) ? h : q;
 else       if (p.next == p)       continue restartFromHead;
 else {
        newNode.lazySetNext(p);
        if (p.casPrev(null,newNode)) {
          if (p != h)           casHead(h,newNode);
          return;
        }
      }
    }
  }
  /** 
 * Links e as last element.
 */
  private void linkLast(  E e){
    checkNotNull(e);
    final Node<E> newNode=new Node<E>(e);
    restartFromTail:     for (; ; )     for (Node<E> t=tail, p=t, q; ; ) {
      if ((q=p.next) != null && (q=(p=q).next) != null)       p=(t != (t=tail)) ? t : q;
 else       if (p.prev == p)       continue restartFromTail;
 else {
        newNode.lazySetPrev(p);
        if (p.casNext(null,newNode)) {
          if (p != t)           casTail(t,newNode);
          return;
        }
      }
    }
  }
  private static final int HOPS=2;
  /** 
 * Unlinks non-null node x.
 */
  void unlink(  Node<E> x){
    final Node<E> prev=x.prev;
    final Node<E> next=x.next;
    if (prev == null) {
      unlinkFirst(x,next);
    }
 else     if (next == null) {
      unlinkLast(x,prev);
    }
 else {
      Node<E> activePred, activeSucc;
      boolean isFirst, isLast;
      int hops=1;
      for (Node<E> p=prev; ; ++hops) {
        if (p.item != null) {
          activePred=p;
          isFirst=false;
          break;
        }
        Node<E> q=p.prev;
        if (q == null) {
          if (p.next == p)           return;
          activePred=p;
          isFirst=true;
          break;
        }
 else         if (p == q)         return;
 else         p=q;
      }
      for (Node<E> p=next; ; ++hops) {
        if (p.item != null) {
          activeSucc=p;
          isLast=false;
          break;
        }
        Node<E> q=p.next;
        if (q == null) {
          if (p.prev == p)           return;
          activeSucc=p;
          isLast=true;
          break;
        }
 else         if (p == q)         return;
 else         p=q;
      }
      if (hops < HOPS && (isFirst | isLast))       return;
      skipDeletedSuccessors(activePred);
      skipDeletedPredecessors(activeSucc);
      if ((isFirst | isLast) && (activePred.next == activeSucc) && (activeSucc.prev == activePred)&& (isFirst ? activePred.prev == null : activePred.item != null)&& (isLast ? activeSucc.next == null : activeSucc.item != null)) {
        updateHead();
        updateTail();
        x.lazySetPrev(isFirst ? prevTerminator() : x);
        x.lazySetNext(isLast ? nextTerminator() : x);
      }
    }
  }
  /** 
 * Unlinks non-null first node.
 */
  private void unlinkFirst(  Node<E> first,  Node<E> next){
    for (Node<E> o=null, p=next, q; ; ) {
      if (p.item != null || (q=p.next) == null) {
        if (o != null && p.prev != p && first.casNext(next,p)) {
          skipDeletedPredecessors(p);
          if (first.prev == null && (p.next == null || p.item != null) && p.prev == first) {
            updateHead();
            updateTail();
            o.lazySetNext(o);
            o.lazySetPrev(prevTerminator());
          }
        }
        return;
      }
 else       if (p == q)       return;
 else {
        o=p;
        p=q;
      }
    }
  }
  /** 
 * Unlinks non-null last node.
 */
  private void unlinkLast(  Node<E> last,  Node<E> prev){
    for (Node<E> o=null, p=prev, q; ; ) {
      if (p.item != null || (q=p.prev) == null) {
        if (o != null && p.next != p && last.casPrev(prev,p)) {
          skipDeletedSuccessors(p);
          if (last.next == null && (p.prev == null || p.item != null) && p.next == last) {
            updateHead();
            updateTail();
            o.lazySetPrev(o);
            o.lazySetNext(nextTerminator());
          }
        }
        return;
      }
 else       if (p == q)       return;
 else {
        o=p;
        p=q;
      }
    }
  }
  /** 
 * Guarantees that any node which was unlinked before a call to
 * this method will be unreachable from head after it returns.
 * Does not guarantee to eliminate slack, only that head will
 * point to a node that was active while this method was running.
 */
  private final void updateHead(){
    Node<E> h, p, q;
    restartFromHead:     while ((h=head).item == null && (p=h.prev) != null) {
      for (; ; ) {
        if ((q=p.prev) == null || (q=(p=q).prev) == null) {
          if (casHead(h,p))           return;
 else           continue restartFromHead;
        }
 else         if (h != head)         continue restartFromHead;
 else         p=q;
      }
    }
  }
  /** 
 * Guarantees that any node which was unlinked before a call to
 * this method will be unreachable from tail after it returns.
 * Does not guarantee to eliminate slack, only that tail will
 * point to a node that was active while this method was running.
 */
  private final void updateTail(){
    Node<E> t, p, q;
    restartFromTail:     while ((t=tail).item == null && (p=t.next) != null) {
      for (; ; ) {
        if ((q=p.next) == null || (q=(p=q).next) == null) {
          if (casTail(t,p))           return;
 else           continue restartFromTail;
        }
 else         if (t != tail)         continue restartFromTail;
 else         p=q;
      }
    }
  }
  private void skipDeletedPredecessors(  Node<E> x){
    whileActive:     do {
      Node<E> prev=x.prev;
      Node<E> p=prev;
      findActive:       for (; ; ) {
        if (p.item != null)         break findActive;
        Node<E> q=p.prev;
        if (q == null) {
          if (p.next == p)           continue whileActive;
          break findActive;
        }
 else         if (p == q)         continue whileActive;
 else         p=q;
      }
      if (prev == p || x.casPrev(prev,p))       return;
    }
 while (x.item != null || x.next == null);
  }
  private void skipDeletedSuccessors(  Node<E> x){
    whileActive:     do {
      Node<E> next=x.next;
      Node<E> p=next;
      findActive:       for (; ; ) {
        if (p.item != null)         break findActive;
        Node<E> q=p.next;
        if (q == null) {
          if (p.prev == p)           continue whileActive;
          break findActive;
        }
 else         if (p == q)         continue whileActive;
 else         p=q;
      }
      if (next == p || x.casNext(next,p))       return;
    }
 while (x.item != null || x.prev == null);
  }
  /** 
 * Returns the successor of p, or the first node if p.next has been
 * linked to self, which will only be true if traversing with a
 * stale pointer that is now off the list.
 */
  final Node<E> succ(  Node<E> p){
    Node<E> q=p.next;
    return (p == q) ? first() : q;
  }
  /** 
 * Returns the predecessor of p, or the last node if p.prev has been
 * linked to self, which will only be true if traversing with a
 * stale pointer that is now off the list.
 */
  final Node<E> pred(  Node<E> p){
    Node<E> q=p.prev;
    return (p == q) ? last() : q;
  }
  /** 
 * Returns the first node, the unique node p for which:
 * p.prev == null && p.next != p
 * The returned node may or may not be logically deleted.
 * Guarantees that head is set to the returned node.
 */
  Node<E> first(){
    restartFromHead:     for (; ; )     for (Node<E> h=head, p=h, q; ; ) {
      if ((q=p.prev) != null && (q=(p=q).prev) != null)       p=(h != (h=head)) ? h : q;
 else       if (p == h || casHead(h,p))       return p;
 else       continue restartFromHead;
    }
  }
  /** 
 * Returns the last node, the unique node p for which:
 * p.next == null && p.prev != p
 * The returned node may or may not be logically deleted.
 * Guarantees that tail is set to the returned node.
 */
  Node<E> last(){
    restartFromTail:     for (; ; )     for (Node<E> t=tail, p=t, q; ; ) {
      if ((q=p.next) != null && (q=(p=q).next) != null)       p=(t != (t=tail)) ? t : q;
 else       if (p == t || casTail(t,p))       return p;
 else       continue restartFromTail;
    }
  }
  /** 
 * Throws NullPointerException if argument is null.
 * @param v the element
 */
  private static void checkNotNull(  Object v){
    if (v == null)     throw new NullPointerException();
  }
  /** 
 * Returns element unless it is null, in which case throws
 * NoSuchElementException.
 * @param v the element
 * @return the element
 */
  private E screenNullResult(  E v){
    if (v == null)     throw new NoSuchElementException();
    return v;
  }
  /** 
 * Creates an array list and fills it with elements of this list.
 * Used by toArray.
 * @return the arrayList
 */
  private ArrayList<E> toArrayList(){
    ArrayList<E> list=new ArrayList<E>();
    for (Node<E> p=first(); p != null; p=succ(p)) {
      E item=p.item;
      if (item != null)       list.add(item);
    }
    return list;
  }
  /** 
 * Constructs an empty deque.
 */
  public ConcurrentLinkedDeque(){
    head=tail=new Node<E>(null);
  }
  /** 
 * Constructs a deque initially containing the elements of
 * the given collection, added in traversal order of the
 * collection's iterator.
 * @param c the collection of elements to initially contain
 * @throws NullPointerException if the specified collection or any
 * of its elements are null
 */
  public ConcurrentLinkedDeque(  Collection<? extends E> c){
    Node<E> h=null, t=null;
    for (    E e : c) {
      checkNotNull(e);
      Node<E> newNode=new Node<E>(e);
      if (h == null)       h=t=newNode;
 else {
        t.lazySetNext(newNode);
        newNode.lazySetPrev(t);
        t=newNode;
      }
    }
    initHeadTail(h,t);
  }
  /** 
 * Initializes head and tail, ensuring invariants hold.
 */
  private void initHeadTail(  Node<E> h,  Node<E> t){
    if (h == t) {
      if (h == null)       h=t=new Node<E>(null);
 else {
        Node<E> newNode=new Node<E>(null);
        t.lazySetNext(newNode);
        newNode.lazySetPrev(t);
        t=newNode;
      }
    }
    head=h;
    tail=t;
  }
  /** 
 * Inserts the specified element at the front of this deque.
 * As the deque is unbounded, this method will never throw{@link IllegalStateException}.
 * @throws NullPointerException if the specified element is null
 */
  public void addFirst(  E e){
    linkFirst(e);
  }
  /** 
 * Inserts the specified element at the end of this deque.
 * As the deque is unbounded, this method will never throw{@link IllegalStateException}.
 * <p>This method is equivalent to {@link #add}.
 * @throws NullPointerException if the specified element is null
 */
  public void addLast(  E e){
    linkLast(e);
  }
  /** 
 * Inserts the specified element at the front of this deque.
 * As the deque is unbounded, this method will never return {@code false}.
 * @return {@code true} (as specified by {@link Deque#offerFirst})
 * @throws NullPointerException if the specified element is null
 */
  public boolean offerFirst(  E e){
    linkFirst(e);
    return true;
  }
  /** 
 * Inserts the specified element at the end of this deque.
 * As the deque is unbounded, this method will never return {@code false}.
 * <p>This method is equivalent to {@link #add}.
 * @return {@code true} (as specified by {@link Deque#offerLast})
 * @throws NullPointerException if the specified element is null
 */
  public boolean offerLast(  E e){
    linkLast(e);
    return true;
  }
  public E peekFirst(){
    for (Node<E> p=first(); p != null; p=succ(p)) {
      E item=p.item;
      if (item != null)       return item;
    }
    return null;
  }
  public E peekLast(){
    for (Node<E> p=last(); p != null; p=pred(p)) {
      E item=p.item;
      if (item != null)       return item;
    }
    return null;
  }
  /** 
 * @throws NoSuchElementException {@inheritDoc}
 */
  public E getFirst(){
    return screenNullResult(peekFirst());
  }
  /** 
 * @throws NoSuchElementException {@inheritDoc}
 */
  public E getLast(){
    return screenNullResult(peekLast());
  }
  public E pollFirst(){
    for (Node<E> p=first(); p != null; p=succ(p)) {
      E item=p.item;
      if (item != null && p.casItem(item,null)) {
        unlink(p);
        return item;
      }
    }
    return null;
  }
  public E pollLast(){
    for (Node<E> p=last(); p != null; p=pred(p)) {
      E item=p.item;
      if (item != null && p.casItem(item,null)) {
        unlink(p);
        return item;
      }
    }
    return null;
  }
  /** 
 * @throws NoSuchElementException {@inheritDoc}
 */
  public E removeFirst(){
    return screenNullResult(pollFirst());
  }
  /** 
 * @throws NoSuchElementException {@inheritDoc}
 */
  public E removeLast(){
    return screenNullResult(pollLast());
  }
  /** 
 * Inserts the specified element at the tail of this deque.
 * As the deque is unbounded, this method will never return {@code false}.
 * @return {@code true} (as specified by {@link Queue#offer})
 * @throws NullPointerException if the specified element is null
 */
  public boolean offer(  E e){
    return offerLast(e);
  }
  /** 
 * Inserts the specified element at the tail of this deque.
 * As the deque is unbounded, this method will never throw{@link IllegalStateException} or return {@code false}.
 * @return {@code true} (as specified by {@link Collection#add})
 * @throws NullPointerException if the specified element is null
 */
  public boolean add(  E e){
    return offerLast(e);
  }
  public E poll(){
    return pollFirst();
  }
  public E remove(){
    return removeFirst();
  }
  public E peek(){
    return peekFirst();
  }
  public E element(){
    return getFirst();
  }
  public void push(  E e){
    addFirst(e);
  }
  public E pop(){
    return removeFirst();
  }
  /** 
 * Removes the first element {@code e} such that{@code o.equals(e)}, if such an element exists in this deque.
 * If the deque does not contain the element, it is unchanged.
 * @param o element to be removed from this deque, if present
 * @return {@code true} if the deque contained the specified element
 * @throws NullPointerException if the specified element is null
 */
  public boolean removeFirstOccurrence(  Object o){
    checkNotNull(o);
    for (Node<E> p=first(); p != null; p=succ(p)) {
      E item=p.item;
      if (item != null && o.equals(item) && p.casItem(item,null)) {
        unlink(p);
        return true;
      }
    }
    return false;
  }
  /** 
 * Removes the last element {@code e} such that{@code o.equals(e)}, if such an element exists in this deque.
 * If the deque does not contain the element, it is unchanged.
 * @param o element to be removed from this deque, if present
 * @return {@code true} if the deque contained the specified element
 * @throws NullPointerException if the specified element is null
 */
  public boolean removeLastOccurrence(  Object o){
    checkNotNull(o);
    for (Node<E> p=last(); p != null; p=pred(p)) {
      E item=p.item;
      if (item != null && o.equals(item) && p.casItem(item,null)) {
        unlink(p);
        return true;
      }
    }
    return false;
  }
  /** 
 * Returns {@code true} if this deque contains at least one
 * element {@code e} such that {@code o.equals(e)}.
 * @param o element whose presence in this deque is to be tested
 * @return {@code true} if this deque contains the specified element
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
 * Returns {@code true} if this collection contains no elements.
 * @return {@code true} if this collection contains no elements
 */
  public boolean isEmpty(){
    return peekFirst() == null;
  }
  /** 
 * Returns the number of elements in this deque.  If this deque
 * contains more than {@code Integer.MAX_VALUE} elements, it
 * returns {@code Integer.MAX_VALUE}.
 * <p>Beware that, unlike in most collections, this method is
 * <em>NOT</em> a constant-time operation. Because of the
 * asynchronous nature of these deques, determining the current
 * number of elements requires traversing them all to count them.
 * Additionally, it is possible for the size to change during
 * execution of this method, in which case the returned result
 * will be inaccurate. Thus, this method is typically not very
 * useful in concurrent applications.
 * @return the number of elements in this deque
 */
  public int size(){
    int count=0;
    for (Node<E> p=first(); p != null; p=succ(p))     if (p.item != null)     if (++count == Integer.MAX_VALUE)     break;
    return count;
  }
  /** 
 * Removes the first element {@code e} such that{@code o.equals(e)}, if such an element exists in this deque.
 * If the deque does not contain the element, it is unchanged.
 * @param o element to be removed from this deque, if present
 * @return {@code true} if the deque contained the specified element
 * @throws NullPointerException if the specified element is null
 */
  public boolean remove(  Object o){
    return removeFirstOccurrence(o);
  }
  /** 
 * Appends all of the elements in the specified collection to the end of
 * this deque, in the order that they are returned by the specified
 * collection's iterator.  Attempts to {@code addAll} of a deque to
 * itself result in {@code IllegalArgumentException}.
 * @param c the elements to be inserted into this deque
 * @return {@code true} if this deque changed as a result of the call
 * @throws NullPointerException if the specified collection or any
 * of its elements are null
 * @throws IllegalArgumentException if the collection is this deque
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
        newNode.lazySetPrev(last);
        last=newNode;
      }
    }
    if (beginningOfTheEnd == null)     return false;
    restartFromTail:     for (; ; )     for (Node<E> t=tail, p=t, q; ; ) {
      if ((q=p.next) != null && (q=(p=q).next) != null)       p=(t != (t=tail)) ? t : q;
 else       if (p.prev == p)       continue restartFromTail;
 else {
        beginningOfTheEnd.lazySetPrev(p);
        if (p.casNext(null,beginningOfTheEnd)) {
          if (!casTail(t,last)) {
            t=tail;
            if (last.next == null)             casTail(t,last);
          }
          return true;
        }
      }
    }
  }
  /** 
 * Removes all of the elements from this deque.
 */
  public void clear(){
    while (pollFirst() != null)     ;
  }
  /** 
 * Returns an array containing all of the elements in this deque, in
 * proper sequence (from first to last element).
 * <p>The returned array will be "safe" in that no references to it are
 * maintained by this deque.  (In other words, this method must allocate
 * a new array).  The caller is thus free to modify the returned array.
 * <p>This method acts as bridge between array-based and collection-based
 * APIs.
 * @return an array containing all of the elements in this deque
 */
  public Object[] toArray(){
    return toArrayList().toArray();
  }
  /** 
 * Returns an array containing all of the elements in this deque,
 * in proper sequence (from first to last element); the runtime
 * type of the returned array is that of the specified array.  If
 * the deque fits in the specified array, it is returned therein.
 * Otherwise, a new array is allocated with the runtime type of
 * the specified array and the size of this deque.
 * <p>If this deque fits in the specified array with room to spare
 * (i.e., the array has more elements than this deque), the element in
 * the array immediately following the end of the deque is set to{@code null}.
 * <p>Like the {@link #toArray()} method, this method acts as
 * bridge between array-based and collection-based APIs.  Further,
 * this method allows precise control over the runtime type of the
 * output array, and may, under certain circumstances, be used to
 * save allocation costs.
 * <p>Suppose {@code x} is a deque known to contain only strings.
 * The following code can be used to dump the deque into a newly
 * allocated array of {@code String}:
 * <pre>
 * String[] y = x.toArray(new String[0]);</pre>
 * Note that {@code toArray(new Object[0])} is identical in function to{@code toArray()}.
 * @param a the array into which the elements of the deque are to
 * be stored, if it is big enough; otherwise, a new array of the
 * same runtime type is allocated for this purpose
 * @return an array containing all of the elements in this deque
 * @throws ArrayStoreException if the runtime type of the specified array
 * is not a supertype of the runtime type of every element in
 * this deque
 * @throws NullPointerException if the specified array is null
 */
  public <T>T[] toArray(  T[] a){
    return toArrayList().toArray(a);
  }
  /** 
 * Returns an iterator over the elements in this deque in proper sequence.
 * The elements will be returned in order from first (head) to last (tail).
 * <p>The returned iterator is a "weakly consistent" iterator that
 * will never throw {@link java.util.ConcurrentModificationExceptionConcurrentModificationException}, and guarantees to traverse
 * elements as they existed upon construction of the iterator, and
 * may (but is not guaranteed to) reflect any modifications
 * subsequent to construction.
 * @return an iterator over the elements in this deque in proper sequence
 */
  public Iterator<E> iterator(){
    return new Itr();
  }
  /** 
 * Returns an iterator over the elements in this deque in reverse
 * sequential order.  The elements will be returned in order from
 * last (tail) to first (head).
 * <p>The returned iterator is a "weakly consistent" iterator that
 * will never throw {@link java.util.ConcurrentModificationExceptionConcurrentModificationException}, and guarantees to traverse
 * elements as they existed upon construction of the iterator, and
 * may (but is not guaranteed to) reflect any modifications
 * subsequent to construction.
 * @return an iterator over the elements in this deque in reverse order
 */
  public Iterator<E> descendingIterator(){
    return new DescendingItr();
  }
private abstract class AbstractItr implements Iterator<E> {
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
 * Node returned by most recent call to next. Needed by remove.
 * Reset to null if this element is deleted by a call to remove.
 */
    private Node<E> lastRet;
    abstract Node<E> startNode();
    abstract Node<E> nextNode(    Node<E> p);
    AbstractItr(){
      advance();
    }
    /** 
 * Sets nextNode and nextItem to next valid node, or to null
 * if no such.
 */
    private void advance(){
      lastRet=nextNode;
      Node<E> p=(nextNode == null) ? startNode() : nextNode(nextNode);
      for (; ; p=nextNode(p)) {
        if (p == null) {
          nextNode=null;
          nextItem=null;
          break;
        }
        E item=p.item;
        if (item != null) {
          nextNode=p;
          nextItem=item;
          break;
        }
      }
    }
    public boolean hasNext(){
      return nextItem != null;
    }
    public E next(){
      E item=nextItem;
      if (item == null)       throw new NoSuchElementException();
      advance();
      return item;
    }
    public void remove(){
      Node<E> l=lastRet;
      if (l == null)       throw new IllegalStateException();
      l.item=null;
      unlink(l);
      lastRet=null;
    }
  }
  /** 
 * Forward iterator 
 */
private class Itr extends AbstractItr {
    Node<E> startNode(){
      return first();
    }
    Node<E> nextNode(    Node<E> p){
      return succ(p);
    }
  }
  /** 
 * Descending iterator 
 */
private class DescendingItr extends AbstractItr {
    Node<E> startNode(){
      return last();
    }
    Node<E> nextNode(    Node<E> p){
      return pred(p);
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
      E item=p.item;
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
        newNode.lazySetPrev(t);
        t=newNode;
      }
    }
    initHeadTail(h,t);
  }
  private boolean casHead(  Node<E> cmp,  Node<E> val){
    return UNSAFE.compareAndSwapObject(this,headOffset,cmp,val);
  }
  private boolean casTail(  Node<E> cmp,  Node<E> val){
    return UNSAFE.compareAndSwapObject(this,tailOffset,cmp,val);
  }
  private static final sun.misc.Unsafe UNSAFE;
  private static final long headOffset;
  private static final long tailOffset;
static {
    PREV_TERMINATOR=new Node<Object>();
    PREV_TERMINATOR.next=PREV_TERMINATOR;
    NEXT_TERMINATOR=new Node<Object>();
    NEXT_TERMINATOR.prev=NEXT_TERMINATOR;
    try {
      UNSAFE=sun.misc.Unsafe.getUnsafe();
      Class k=ConcurrentLinkedDeque.class;
      headOffset=UNSAFE.objectFieldOffset(k.getDeclaredField("head"));
      tailOffset=UNSAFE.objectFieldOffset(k.getDeclaredField("tail"));
    }
 catch (    Exception e) {
      throw new Error(e);
    }
  }
}
