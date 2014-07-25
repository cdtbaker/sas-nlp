package java.util.concurrent;
import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;
/** 
 * An unbounded {@link TransferQueue} based on linked nodes.
 * This queue orders elements FIFO (first-in-first-out) with respect
 * to any given producer.  The <em>head</em> of the queue is that
 * element that has been on the queue the longest time for some
 * producer.  The <em>tail</em> of the queue is that element that has
 * been on the queue the shortest time for some producer.
 * <p>Beware that, unlike in most collections, the {@code size} method
 * is <em>NOT</em> a constant-time operation. Because of the
 * asynchronous nature of these queues, determining the current number
 * of elements requires a traversal of the elements, and so may report
 * inaccurate results if this collection is modified during traversal.
 * Additionally, the bulk operations {@code addAll},{@code removeAll}, {@code retainAll}, {@code containsAll},{@code equals}, and {@code toArray} are <em>not</em> guaranteed
 * to be performed atomically. For example, an iterator operating
 * concurrently with an {@code addAll} operation might view only some
 * of the added elements.
 * <p>This class and its iterator implement all of the
 * <em>optional</em> methods of the {@link Collection} and {@link Iterator} interfaces.
 * <p>Memory consistency effects: As with other concurrent
 * collections, actions in a thread prior to placing an object into a{@code LinkedTransferQueue}<a href="package-summary.html#MemoryVisibility"><i>happen-before</i></a>
 * actions subsequent to the access or removal of that element from
 * the {@code LinkedTransferQueue} in another thread.
 * <p>This class is a member of the
 * <a href="{@docRoot}/../technotes/guides/collections/index.html">
 * Java Collections Framework</a>.
 * @since 1.7
 * @author Doug Lea
 * @param<E>
 *  the type of elements held in this collection
 */
public class LinkedTransferQueue<E> extends AbstractQueue<E> implements TransferQueue<E>, java.io.Serializable {
  private static final long serialVersionUID=-3223113410248163686L;
  /** 
 * True if on multiprocessor 
 */
  private static final boolean MP=Runtime.getRuntime().availableProcessors() > 1;
  /** 
 * The number of times to spin (with randomly interspersed calls
 * to Thread.yield) on multiprocessor before blocking when a node
 * is apparently the first waiter in the queue.  See above for
 * explanation. Must be a power of two. The value is empirically
 * derived -- it works pretty well across a variety of processors,
 * numbers of CPUs, and OSes.
 */
  private static final int FRONT_SPINS=1 << 7;
  /** 
 * The number of times to spin before blocking when a node is
 * preceded by another node that is apparently spinning.  Also
 * serves as an increment to FRONT_SPINS on phase changes, and as
 * base average frequency for yielding during spins. Must be a
 * power of two.
 */
  private static final int CHAINED_SPINS=FRONT_SPINS >>> 1;
  /** 
 * The maximum number of estimated removal failures (sweepVotes)
 * to tolerate before sweeping through the queue unlinking
 * cancelled nodes that were not unlinked upon initial
 * removal. See above for explanation. The value must be at least
 * two to avoid useless sweeps when removing trailing nodes.
 */
  static final int SWEEP_THRESHOLD=32;
  /** 
 * Queue nodes. Uses Object, not E, for items to allow forgetting
 * them after use.  Relies heavily on Unsafe mechanics to minimize
 * unnecessary ordering constraints: Writes that are intrinsically
 * ordered wrt other accesses or CASes use simple relaxed forms.
 */
static final class Node {
    final boolean isData;
    volatile Object item;
    volatile Node next;
    volatile Thread waiter;
    final boolean casNext(    Node cmp,    Node val){
      return UNSAFE.compareAndSwapObject(this,nextOffset,cmp,val);
    }
    final boolean casItem(    Object cmp,    Object val){
      return UNSAFE.compareAndSwapObject(this,itemOffset,cmp,val);
    }
    /** 
 * Constructs a new node.  Uses relaxed write because item can
 * only be seen after publication via casNext.
 */
    Node(    Object item,    boolean isData){
      UNSAFE.putObject(this,itemOffset,item);
      this.isData=isData;
    }
    /** 
 * Links node to itself to avoid garbage retention.  Called
 * only after CASing head field, so uses relaxed write.
 */
    final void forgetNext(){
      UNSAFE.putObject(this,nextOffset,this);
    }
    /** 
 * Sets item to self and waiter to null, to avoid garbage
 * retention after matching or cancelling. Uses relaxed writes
 * because order is already constrained in the only calling
 * contexts: item is forgotten only after volatile/atomic
 * mechanics that extract items.  Similarly, clearing waiter
 * follows either CAS or return from park (if ever parked;
 * else we don't care).
 */
    final void forgetContents(){
      UNSAFE.putObject(this,itemOffset,this);
      UNSAFE.putObject(this,waiterOffset,null);
    }
    /** 
 * Returns true if this node has been matched, including the
 * case of artificial matches due to cancellation.
 */
    final boolean isMatched(){
      Object x=item;
      return (x == this) || ((x == null) == isData);
    }
    /** 
 * Returns true if this is an unmatched request node.
 */
    final boolean isUnmatchedRequest(){
      return !isData && item == null;
    }
    /** 
 * Returns true if a node with the given mode cannot be
 * appended to this node because this node is unmatched and
 * has opposite data mode.
 */
    final boolean cannotPrecede(    boolean haveData){
      boolean d=isData;
      Object x;
      return d != haveData && (x=item) != this && (x != null) == d;
    }
    /** 
 * Tries to artificially match a data node -- used by remove.
 */
    final boolean tryMatchData(){
      Object x=item;
      if (x != null && x != this && casItem(x,null)) {
        LockSupport.unpark(waiter);
        return true;
      }
      return false;
    }
    private static final long serialVersionUID=-3375979862319811754L;
    private static final sun.misc.Unsafe UNSAFE;
    private static final long itemOffset;
    private static final long nextOffset;
    private static final long waiterOffset;
static {
      try {
        UNSAFE=sun.misc.Unsafe.getUnsafe();
        Class k=Node.class;
        itemOffset=UNSAFE.objectFieldOffset(k.getDeclaredField("item"));
        nextOffset=UNSAFE.objectFieldOffset(k.getDeclaredField("next"));
        waiterOffset=UNSAFE.objectFieldOffset(k.getDeclaredField("waiter"));
      }
 catch (      Exception e) {
        throw new Error(e);
      }
    }
  }
  /** 
 * head of the queue; null until first enqueue 
 */
  transient volatile Node head;
  /** 
 * tail of the queue; null until first append 
 */
  private transient volatile Node tail;
  /** 
 * The number of apparent failures to unsplice removed nodes 
 */
  private transient volatile int sweepVotes;
  private boolean casTail(  Node cmp,  Node val){
    return UNSAFE.compareAndSwapObject(this,tailOffset,cmp,val);
  }
  private boolean casHead(  Node cmp,  Node val){
    return UNSAFE.compareAndSwapObject(this,headOffset,cmp,val);
  }
  private boolean casSweepVotes(  int cmp,  int val){
    return UNSAFE.compareAndSwapInt(this,sweepVotesOffset,cmp,val);
  }
  private static final int NOW=0;
  private static final int ASYNC=1;
  private static final int SYNC=2;
  private static final int TIMED=3;
  @SuppressWarnings("unchecked") static <E>E cast(  Object item){
    return (E)item;
  }
  /** 
 * Implements all queuing methods. See above for explanation.
 * @param e the item or null for take
 * @param haveData true if this is a put, else a take
 * @param how NOW, ASYNC, SYNC, or TIMED
 * @param nanos timeout in nanosecs, used only if mode is TIMED
 * @return an item if matched, else e
 * @throws NullPointerException if haveData mode but e is null
 */
  private E xfer(  E e,  boolean haveData,  int how,  long nanos){
    if (haveData && (e == null))     throw new NullPointerException();
    Node s=null;
    retry:     for (; ; ) {
      for (Node h=head, p=h; p != null; ) {
        boolean isData=p.isData;
        Object item=p.item;
        if (item != p && (item != null) == isData) {
          if (isData == haveData)           break;
          if (p.casItem(item,e)) {
            for (Node q=p; q != h; ) {
              Node n=q.next;
              if (head == h && casHead(h,n == null ? q : n)) {
                h.forgetNext();
                break;
              }
              if ((h=head) == null || (q=h.next) == null || !q.isMatched())               break;
            }
            LockSupport.unpark(p.waiter);
            return this.<E>cast(item);
          }
        }
        Node n=p.next;
        p=(p != n) ? n : (h=head);
      }
      if (how != NOW) {
        if (s == null)         s=new Node(e,haveData);
        Node pred=tryAppend(s,haveData);
        if (pred == null)         continue retry;
        if (how != ASYNC)         return awaitMatch(s,pred,e,(how == TIMED),nanos);
      }
      return e;
    }
  }
  /** 
 * Tries to append node s as tail.
 * @param s the node to append
 * @param haveData true if appending in data mode
 * @return null on failure due to losing race with append in
 * different mode, else s's predecessor, or s itself if no
 * predecessor
 */
  private Node tryAppend(  Node s,  boolean haveData){
    for (Node t=tail, p=t; ; ) {
      Node n, u;
      if (p == null && (p=head) == null) {
        if (casHead(null,s))         return s;
      }
 else       if (p.cannotPrecede(haveData))       return null;
 else       if ((n=p.next) != null)       p=p != t && t != (u=tail) ? (t=u) : (p != n) ? n : null;
 else       if (!p.casNext(null,s))       p=p.next;
 else {
        if (p != t) {
          while ((tail != t || !casTail(t,s)) && (t=tail) != null && (s=t.next) != null && (s=s.next) != null && s != t)           ;
        }
        return p;
      }
    }
  }
  /** 
 * Spins/yields/blocks until node s is matched or caller gives up.
 * @param s the waiting node
 * @param pred the predecessor of s, or s itself if it has no
 * predecessor, or null if unknown (the null case does not occur
 * in any current calls but may in possible future extensions)
 * @param e the comparison value for checking match
 * @param timed if true, wait only until timeout elapses
 * @param nanos timeout in nanosecs, used only if timed is true
 * @return matched item, or e if unmatched on interrupt or timeout
 */
  private E awaitMatch(  Node s,  Node pred,  E e,  boolean timed,  long nanos){
    long lastTime=timed ? System.nanoTime() : 0L;
    Thread w=Thread.currentThread();
    int spins=-1;
    ThreadLocalRandom randomYields=null;
    for (; ; ) {
      Object item=s.item;
      if (item != e) {
        s.forgetContents();
        return this.<E>cast(item);
      }
      if ((w.isInterrupted() || (timed && nanos <= 0)) && s.casItem(e,s)) {
        unsplice(pred,s);
        return e;
      }
      if (spins < 0) {
        if ((spins=spinsFor(pred,s.isData)) > 0)         randomYields=ThreadLocalRandom.current();
      }
 else       if (spins > 0) {
        --spins;
        if (randomYields.nextInt(CHAINED_SPINS) == 0)         Thread.yield();
      }
 else       if (s.waiter == null) {
        s.waiter=w;
      }
 else       if (timed) {
        long now=System.nanoTime();
        if ((nanos-=now - lastTime) > 0)         LockSupport.parkNanos(this,nanos);
        lastTime=now;
      }
 else {
        LockSupport.park(this);
      }
    }
  }
  /** 
 * Returns spin/yield value for a node with given predecessor and
 * data mode. See above for explanation.
 */
  private static int spinsFor(  Node pred,  boolean haveData){
    if (MP && pred != null) {
      if (pred.isData != haveData)       return FRONT_SPINS + CHAINED_SPINS;
      if (pred.isMatched())       return FRONT_SPINS;
      if (pred.waiter == null)       return CHAINED_SPINS;
    }
    return 0;
  }
  /** 
 * Returns the successor of p, or the head node if p.next has been
 * linked to self, which will only be true if traversing with a
 * stale pointer that is now off the list.
 */
  final Node succ(  Node p){
    Node next=p.next;
    return (p == next) ? head : next;
  }
  /** 
 * Returns the first unmatched node of the given mode, or null if
 * none.  Used by methods isEmpty, hasWaitingConsumer.
 */
  private Node firstOfMode(  boolean isData){
    for (Node p=head; p != null; p=succ(p)) {
      if (!p.isMatched())       return (p.isData == isData) ? p : null;
    }
    return null;
  }
  /** 
 * Returns the item in the first unmatched node with isData; or
 * null if none.  Used by peek.
 */
  private E firstDataItem(){
    for (Node p=head; p != null; p=succ(p)) {
      Object item=p.item;
      if (p.isData) {
        if (item != null && item != p)         return this.<E>cast(item);
      }
 else       if (item == null)       return null;
    }
    return null;
  }
  /** 
 * Traverses and counts unmatched nodes of the given mode.
 * Used by methods size and getWaitingConsumerCount.
 */
  private int countOfMode(  boolean data){
    int count=0;
    for (Node p=head; p != null; ) {
      if (!p.isMatched()) {
        if (p.isData != data)         return 0;
        if (++count == Integer.MAX_VALUE)         break;
      }
      Node n=p.next;
      if (n != p)       p=n;
 else {
        count=0;
        p=head;
      }
    }
    return count;
  }
final class Itr implements Iterator<E> {
    private Node nextNode;
    private E nextItem;
    private Node lastRet;
    private Node lastPred;
    /** 
 * Moves to next node after prev, or first node if prev null.
 */
    private void advance(    Node prev){
      Node r, b;
      if ((r=lastRet) != null && !r.isMatched())       lastPred=r;
 else       if ((b=lastPred) == null || b.isMatched())       lastPred=null;
 else {
        Node s, n;
        while ((s=b.next) != null && s != b && s.isMatched() && (n=s.next) != null && n != s)         b.casNext(s,n);
      }
      this.lastRet=prev;
      for (Node p=prev, s, n; ; ) {
        s=(p == null) ? head : p.next;
        if (s == null)         break;
 else         if (s == p) {
          p=null;
          continue;
        }
        Object item=s.item;
        if (s.isData) {
          if (item != null && item != s) {
            nextItem=LinkedTransferQueue.<E>cast(item);
            nextNode=s;
            return;
          }
        }
 else         if (item == null)         break;
        if (p == null)         p=s;
 else         if ((n=s.next) == null)         break;
 else         if (s == n)         p=null;
 else         p.casNext(s,n);
      }
      nextNode=null;
      nextItem=null;
    }
    Itr(){
      advance(null);
    }
    public final boolean hasNext(){
      return nextNode != null;
    }
    public final E next(){
      Node p=nextNode;
      if (p == null)       throw new NoSuchElementException();
      E e=nextItem;
      advance(p);
      return e;
    }
    public final void remove(){
      final Node lastRet=this.lastRet;
      if (lastRet == null)       throw new IllegalStateException();
      this.lastRet=null;
      if (lastRet.tryMatchData())       unsplice(lastPred,lastRet);
    }
  }
  /** 
 * Unsplices (now or later) the given deleted/cancelled node with
 * the given predecessor.
 * @param pred a node that was at one time known to be the
 * predecessor of s, or null or s itself if s is/was at head
 * @param s the node to be unspliced
 */
  final void unsplice(  Node pred,  Node s){
    s.forgetContents();
    if (pred != null && pred != s && pred.next == s) {
      Node n=s.next;
      if (n == null || (n != s && pred.casNext(s,n) && pred.isMatched())) {
        for (; ; ) {
          Node h=head;
          if (h == pred || h == s || h == null)           return;
          if (!h.isMatched())           break;
          Node hn=h.next;
          if (hn == null)           return;
          if (hn != h && casHead(h,hn))           h.forgetNext();
        }
        if (pred.next != pred && s.next != s) {
          for (; ; ) {
            int v=sweepVotes;
            if (v < SWEEP_THRESHOLD) {
              if (casSweepVotes(v,v + 1))               break;
            }
 else             if (casSweepVotes(v,0)) {
              sweep();
              break;
            }
          }
        }
      }
    }
  }
  /** 
 * Unlinks matched (typically cancelled) nodes encountered in a
 * traversal from head.
 */
  private void sweep(){
    for (Node p=head, s, n; p != null && (s=p.next) != null; ) {
      if (!s.isMatched())       p=s;
 else       if ((n=s.next) == null)       break;
 else       if (s == n)       p=head;
 else       p.casNext(s,n);
    }
  }
  /** 
 * Main implementation of remove(Object)
 */
  private boolean findAndRemove(  Object e){
    if (e != null) {
      for (Node pred=null, p=head; p != null; ) {
        Object item=p.item;
        if (p.isData) {
          if (item != null && item != p && e.equals(item) && p.tryMatchData()) {
            unsplice(pred,p);
            return true;
          }
        }
 else         if (item == null)         break;
        pred=p;
        if ((p=p.next) == pred) {
          pred=null;
          p=head;
        }
      }
    }
    return false;
  }
  /** 
 * Creates an initially empty {@code LinkedTransferQueue}.
 */
  public LinkedTransferQueue(){
  }
  /** 
 * Creates a {@code LinkedTransferQueue}initially containing the elements of the given collection,
 * added in traversal order of the collection's iterator.
 * @param c the collection of elements to initially contain
 * @throws NullPointerException if the specified collection or any
 * of its elements are null
 */
  public LinkedTransferQueue(  Collection<? extends E> c){
    this();
    addAll(c);
  }
  /** 
 * Inserts the specified element at the tail of this queue.
 * As the queue is unbounded, this method will never block.
 * @throws NullPointerException if the specified element is null
 */
  public void put(  E e){
    xfer(e,true,ASYNC,0);
  }
  /** 
 * Inserts the specified element at the tail of this queue.
 * As the queue is unbounded, this method will never block or
 * return {@code false}.
 * @return {@code true} (as specified by{@link BlockingQueue#offer(Object,long,TimeUnit) BlockingQueue.offer})
 * @throws NullPointerException if the specified element is null
 */
  public boolean offer(  E e,  long timeout,  TimeUnit unit){
    xfer(e,true,ASYNC,0);
    return true;
  }
  /** 
 * Inserts the specified element at the tail of this queue.
 * As the queue is unbounded, this method will never return {@code false}.
 * @return {@code true} (as specified by {@link Queue#offer})
 * @throws NullPointerException if the specified element is null
 */
  public boolean offer(  E e){
    xfer(e,true,ASYNC,0);
    return true;
  }
  /** 
 * Inserts the specified element at the tail of this queue.
 * As the queue is unbounded, this method will never throw{@link IllegalStateException} or return {@code false}.
 * @return {@code true} (as specified by {@link Collection#add})
 * @throws NullPointerException if the specified element is null
 */
  public boolean add(  E e){
    xfer(e,true,ASYNC,0);
    return true;
  }
  /** 
 * Transfers the element to a waiting consumer immediately, if possible.
 * <p>More precisely, transfers the specified element immediately
 * if there exists a consumer already waiting to receive it (in{@link #take} or timed {@link #poll(long,TimeUnit) poll}),
 * otherwise returning {@code false} without enqueuing the element.
 * @throws NullPointerException if the specified element is null
 */
  public boolean tryTransfer(  E e){
    return xfer(e,true,NOW,0) == null;
  }
  /** 
 * Transfers the element to a consumer, waiting if necessary to do so.
 * <p>More precisely, transfers the specified element immediately
 * if there exists a consumer already waiting to receive it (in{@link #take} or timed {@link #poll(long,TimeUnit) poll}),
 * else inserts the specified element at the tail of this queue
 * and waits until the element is received by a consumer.
 * @throws NullPointerException if the specified element is null
 */
  public void transfer(  E e) throws InterruptedException {
    if (xfer(e,true,SYNC,0) != null) {
      Thread.interrupted();
      throw new InterruptedException();
    }
  }
  /** 
 * Transfers the element to a consumer if it is possible to do so
 * before the timeout elapses.
 * <p>More precisely, transfers the specified element immediately
 * if there exists a consumer already waiting to receive it (in{@link #take} or timed {@link #poll(long,TimeUnit) poll}),
 * else inserts the specified element at the tail of this queue
 * and waits until the element is received by a consumer,
 * returning {@code false} if the specified wait time elapses
 * before the element can be transferred.
 * @throws NullPointerException if the specified element is null
 */
  public boolean tryTransfer(  E e,  long timeout,  TimeUnit unit) throws InterruptedException {
    if (xfer(e,true,TIMED,unit.toNanos(timeout)) == null)     return true;
    if (!Thread.interrupted())     return false;
    throw new InterruptedException();
  }
  public E take() throws InterruptedException {
    E e=xfer(null,false,SYNC,0);
    if (e != null)     return e;
    Thread.interrupted();
    throw new InterruptedException();
  }
  public E poll(  long timeout,  TimeUnit unit) throws InterruptedException {
    E e=xfer(null,false,TIMED,unit.toNanos(timeout));
    if (e != null || !Thread.interrupted())     return e;
    throw new InterruptedException();
  }
  public E poll(){
    return xfer(null,false,NOW,0);
  }
  /** 
 * @throws NullPointerException     {@inheritDoc}
 * @throws IllegalArgumentException {@inheritDoc}
 */
  public int drainTo(  Collection<? super E> c){
    if (c == null)     throw new NullPointerException();
    if (c == this)     throw new IllegalArgumentException();
    int n=0;
    E e;
    while ((e=poll()) != null) {
      c.add(e);
      ++n;
    }
    return n;
  }
  /** 
 * @throws NullPointerException     {@inheritDoc}
 * @throws IllegalArgumentException {@inheritDoc}
 */
  public int drainTo(  Collection<? super E> c,  int maxElements){
    if (c == null)     throw new NullPointerException();
    if (c == this)     throw new IllegalArgumentException();
    int n=0;
    E e;
    while (n < maxElements && (e=poll()) != null) {
      c.add(e);
      ++n;
    }
    return n;
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
  public E peek(){
    return firstDataItem();
  }
  /** 
 * Returns {@code true} if this queue contains no elements.
 * @return {@code true} if this queue contains no elements
 */
  public boolean isEmpty(){
    for (Node p=head; p != null; p=succ(p)) {
      if (!p.isMatched())       return !p.isData;
    }
    return true;
  }
  public boolean hasWaitingConsumer(){
    return firstOfMode(false) != null;
  }
  /** 
 * Returns the number of elements in this queue.  If this queue
 * contains more than {@code Integer.MAX_VALUE} elements, returns{@code Integer.MAX_VALUE}.
 * <p>Beware that, unlike in most collections, this method is
 * <em>NOT</em> a constant-time operation. Because of the
 * asynchronous nature of these queues, determining the current
 * number of elements requires an O(n) traversal.
 * @return the number of elements in this queue
 */
  public int size(){
    return countOfMode(true);
  }
  public int getWaitingConsumerCount(){
    return countOfMode(false);
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
    return findAndRemove(o);
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
    for (Node p=head; p != null; p=succ(p)) {
      Object item=p.item;
      if (p.isData) {
        if (item != null && item != p && o.equals(item))         return true;
      }
 else       if (item == null)       break;
    }
    return false;
  }
  /** 
 * Always returns {@code Integer.MAX_VALUE} because a{@code LinkedTransferQueue} is not capacity constrained.
 * @return {@code Integer.MAX_VALUE} (as specified by{@link BlockingQueue#remainingCapacity()})
 */
  public int remainingCapacity(){
    return Integer.MAX_VALUE;
  }
  /** 
 * Saves the state to a stream (that is, serializes it).
 * @serialData All of the elements (each an {@code E}) in
 * the proper order, followed by a null
 * @param s the stream
 */
  private void writeObject(  java.io.ObjectOutputStream s) throws java.io.IOException {
    s.defaultWriteObject();
    for (    E e : this)     s.writeObject(e);
    s.writeObject(null);
  }
  /** 
 * Reconstitutes the Queue instance from a stream (that is,
 * deserializes it).
 * @param s the stream
 */
  private void readObject(  java.io.ObjectInputStream s) throws java.io.IOException, ClassNotFoundException {
    s.defaultReadObject();
    for (; ; ) {
      @SuppressWarnings("unchecked") E item=(E)s.readObject();
      if (item == null)       break;
 else       offer(item);
    }
  }
  private static final sun.misc.Unsafe UNSAFE;
  private static final long headOffset;
  private static final long tailOffset;
  private static final long sweepVotesOffset;
static {
    try {
      UNSAFE=sun.misc.Unsafe.getUnsafe();
      Class k=LinkedTransferQueue.class;
      headOffset=UNSAFE.objectFieldOffset(k.getDeclaredField("head"));
      tailOffset=UNSAFE.objectFieldOffset(k.getDeclaredField("tail"));
      sweepVotesOffset=UNSAFE.objectFieldOffset(k.getDeclaredField("sweepVotes"));
    }
 catch (    Exception e) {
      throw new Error(e);
    }
  }
}
