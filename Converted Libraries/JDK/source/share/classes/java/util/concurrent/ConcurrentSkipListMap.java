package java.util.concurrent;
import java.util.*;
import java.util.concurrent.atomic.*;
/** 
 * A scalable concurrent {@link ConcurrentNavigableMap} implementation.
 * The map is sorted according to the {@linkplain Comparable natural
 * ordering} of its keys, or by a {@link Comparator} provided at map
 * creation time, depending on which constructor is used.
 * <p>This class implements a concurrent variant of <a
 * href="http://en.wikipedia.org/wiki/Skip_list" target="_top">SkipLists</a>
 * providing expected average <i>log(n)</i> time cost for the
 * <tt>containsKey</tt>, <tt>get</tt>, <tt>put</tt> and
 * <tt>remove</tt> operations and their variants.  Insertion, removal,
 * update, and access operations safely execute concurrently by
 * multiple threads.  Iterators are <i>weakly consistent</i>, returning
 * elements reflecting the state of the map at some point at or since
 * the creation of the iterator.  They do <em>not</em> throw {@link ConcurrentModificationException}, and may proceed concurrently with
 * other operations. Ascending key ordered views and their iterators
 * are faster than descending ones.
 * <p>All <tt>Map.Entry</tt> pairs returned by methods in this class
 * and its views represent snapshots of mappings at the time they were
 * produced. They do <em>not</em> support the <tt>Entry.setValue</tt>
 * method. (Note however that it is possible to change mappings in the
 * associated map using <tt>put</tt>, <tt>putIfAbsent</tt>, or
 * <tt>replace</tt>, depending on exactly which effect you need.)
 * <p>Beware that, unlike in most collections, the <tt>size</tt>
 * method is <em>not</em> a constant-time operation. Because of the
 * asynchronous nature of these maps, determining the current number
 * of elements requires a traversal of the elements, and so may report
 * inaccurate results if this collection is modified during traversal.
 * Additionally, the bulk operations <tt>putAll</tt>, <tt>equals</tt>,
 * <tt>toArray</tt>, <tt>containsValue</tt>, and <tt>clear</tt> are
 * <em>not</em> guaranteed to be performed atomically. For example, an
 * iterator operating concurrently with a <tt>putAll</tt> operation
 * might view only some of the added elements.
 * <p>This class and its views and iterators implement all of the
 * <em>optional</em> methods of the {@link Map} and {@link Iterator}interfaces. Like most other concurrent collections, this class does
 * <em>not</em> permit the use of <tt>null</tt> keys or values because some
 * null return values cannot be reliably distinguished from the absence of
 * elements.
 * <p>This class is a member of the
 * <a href="{@docRoot}/../technotes/guides/collections/index.html">
 * Java Collections Framework</a>.
 * @author Doug Lea
 * @param<K>
 *  the type of keys maintained by this map
 * @param<V>
 *  the type of mapped values
 * @since 1.6
 */
public class ConcurrentSkipListMap<K,V> extends AbstractMap<K,V> implements ConcurrentNavigableMap<K,V>, Cloneable, java.io.Serializable {
  private static final long serialVersionUID=-8627078645895051609L;
  /** 
 * Generates the initial random seed for the cheaper per-instance
 * random number generators used in randomLevel.
 */
  private static final Random seedGenerator=new Random();
  /** 
 * Special value used to identify base-level header
 */
  private static final Object BASE_HEADER=new Object();
  /** 
 * The topmost head index of the skiplist.
 */
  private transient volatile HeadIndex<K,V> head;
  /** 
 * The comparator used to maintain order in this map, or null
 * if using natural ordering.
 * @serial
 */
  private final Comparator<? super K> comparator;
  /** 
 * Seed for simple random number generator.  Not volatile since it
 * doesn't matter too much if different threads don't see updates.
 */
  private transient int randomSeed;
  /** 
 * Lazily initialized key set 
 */
  private transient KeySet keySet;
  /** 
 * Lazily initialized entry set 
 */
  private transient EntrySet entrySet;
  /** 
 * Lazily initialized values collection 
 */
  private transient Values values;
  /** 
 * Lazily initialized descending key set 
 */
  private transient ConcurrentNavigableMap<K,V> descendingMap;
  /** 
 * Initializes or resets state. Needed by constructors, clone,
 * clear, readObject. and ConcurrentSkipListSet.clone.
 * (Note that comparator must be separately initialized.)
 */
  final void initialize(){
    keySet=null;
    entrySet=null;
    values=null;
    descendingMap=null;
    randomSeed=seedGenerator.nextInt() | 0x0100;
    head=new HeadIndex<K,V>(new Node<K,V>(null,BASE_HEADER,null),null,null,1);
  }
  /** 
 * compareAndSet head node
 */
  private boolean casHead(  HeadIndex<K,V> cmp,  HeadIndex<K,V> val){
    return UNSAFE.compareAndSwapObject(this,headOffset,cmp,val);
  }
  /** 
 * Nodes hold keys and values, and are singly linked in sorted
 * order, possibly with some intervening marker nodes. The list is
 * headed by a dummy node accessible as head.node. The value field
 * is declared only as Object because it takes special non-V
 * values for marker and header nodes.
 */
static final class Node<K,V> {
    final K key;
    volatile Object value;
    volatile Node<K,V> next;
    /** 
 * Creates a new regular node.
 */
    Node(    K key,    Object value,    Node<K,V> next){
      this.key=key;
      this.value=value;
      this.next=next;
    }
    /** 
 * Creates a new marker node. A marker is distinguished by
 * having its value field point to itself.  Marker nodes also
 * have null keys, a fact that is exploited in a few places,
 * but this doesn't distinguish markers from the base-level
 * header node (head.node), which also has a null key.
 */
    Node(    Node<K,V> next){
      this.key=null;
      this.value=this;
      this.next=next;
    }
    /** 
 * compareAndSet value field
 */
    boolean casValue(    Object cmp,    Object val){
      return UNSAFE.compareAndSwapObject(this,valueOffset,cmp,val);
    }
    /** 
 * compareAndSet next field
 */
    boolean casNext(    Node<K,V> cmp,    Node<K,V> val){
      return UNSAFE.compareAndSwapObject(this,nextOffset,cmp,val);
    }
    /** 
 * Returns true if this node is a marker. This method isn't
 * actually called in any current code checking for markers
 * because callers will have already read value field and need
 * to use that read (not another done here) and so directly
 * test if value points to node.
 * @param n a possibly null reference to a node
 * @return true if this node is a marker node
 */
    boolean isMarker(){
      return value == this;
    }
    /** 
 * Returns true if this node is the header of base-level list.
 * @return true if this node is header node
 */
    boolean isBaseHeader(){
      return value == BASE_HEADER;
    }
    /** 
 * Tries to append a deletion marker to this node.
 * @param f the assumed current successor of this node
 * @return true if successful
 */
    boolean appendMarker(    Node<K,V> f){
      return casNext(f,new Node<K,V>(f));
    }
    /** 
 * Helps out a deletion by appending marker or unlinking from
 * predecessor. This is called during traversals when value
 * field seen to be null.
 * @param b predecessor
 * @param f successor
 */
    void helpDelete(    Node<K,V> b,    Node<K,V> f){
      if (f == next && this == b.next) {
        if (f == null || f.value != f)         appendMarker(f);
 else         b.casNext(this,f.next);
      }
    }
    /** 
 * Returns value if this node contains a valid key-value pair,
 * else null.
 * @return this node's value if it isn't a marker or header or
 * is deleted, else null.
 */
    V getValidValue(){
      Object v=value;
      if (v == this || v == BASE_HEADER)       return null;
      return (V)v;
    }
    /** 
 * Creates and returns a new SimpleImmutableEntry holding current
 * mapping if this node holds a valid value, else null.
 * @return new entry or null
 */
    AbstractMap.SimpleImmutableEntry<K,V> createSnapshot(){
      V v=getValidValue();
      if (v == null)       return null;
      return new AbstractMap.SimpleImmutableEntry<K,V>(key,v);
    }
    private static final sun.misc.Unsafe UNSAFE;
    private static final long valueOffset;
    private static final long nextOffset;
static {
      try {
        UNSAFE=sun.misc.Unsafe.getUnsafe();
        Class k=Node.class;
        valueOffset=UNSAFE.objectFieldOffset(k.getDeclaredField("value"));
        nextOffset=UNSAFE.objectFieldOffset(k.getDeclaredField("next"));
      }
 catch (      Exception e) {
        throw new Error(e);
      }
    }
  }
  /** 
 * Index nodes represent the levels of the skip list.  Note that
 * even though both Nodes and Indexes have forward-pointing
 * fields, they have different types and are handled in different
 * ways, that can't nicely be captured by placing field in a
 * shared abstract class.
 */
static class Index<K,V> {
    final Node<K,V> node;
    final Index<K,V> down;
    volatile Index<K,V> right;
    /** 
 * Creates index node with given values.
 */
    Index(    Node<K,V> node,    Index<K,V> down,    Index<K,V> right){
      this.node=node;
      this.down=down;
      this.right=right;
    }
    /** 
 * compareAndSet right field
 */
    final boolean casRight(    Index<K,V> cmp,    Index<K,V> val){
      return UNSAFE.compareAndSwapObject(this,rightOffset,cmp,val);
    }
    /** 
 * Returns true if the node this indexes has been deleted.
 * @return true if indexed node is known to be deleted
 */
    final boolean indexesDeletedNode(){
      return node.value == null;
    }
    /** 
 * Tries to CAS newSucc as successor.  To minimize races with
 * unlink that may lose this index node, if the node being
 * indexed is known to be deleted, it doesn't try to link in.
 * @param succ the expected current successor
 * @param newSucc the new successor
 * @return true if successful
 */
    final boolean link(    Index<K,V> succ,    Index<K,V> newSucc){
      Node<K,V> n=node;
      newSucc.right=succ;
      return n.value != null && casRight(succ,newSucc);
    }
    /** 
 * Tries to CAS right field to skip over apparent successor
 * succ.  Fails (forcing a retraversal by caller) if this node
 * is known to be deleted.
 * @param succ the expected current successor
 * @return true if successful
 */
    final boolean unlink(    Index<K,V> succ){
      return !indexesDeletedNode() && casRight(succ,succ.right);
    }
    private static final sun.misc.Unsafe UNSAFE;
    private static final long rightOffset;
static {
      try {
        UNSAFE=sun.misc.Unsafe.getUnsafe();
        Class k=Index.class;
        rightOffset=UNSAFE.objectFieldOffset(k.getDeclaredField("right"));
      }
 catch (      Exception e) {
        throw new Error(e);
      }
    }
  }
  /** 
 * Nodes heading each level keep track of their level.
 */
static final class HeadIndex<K,V> extends Index<K,V> {
    final int level;
    HeadIndex(    Node<K,V> node,    Index<K,V> down,    Index<K,V> right,    int level){
      super(node,down,right);
      this.level=level;
    }
  }
  /** 
 * Represents a key with a comparator as a Comparable.
 * Because most sorted collections seem to use natural ordering on
 * Comparables (Strings, Integers, etc), most internal methods are
 * geared to use them. This is generally faster than checking
 * per-comparison whether to use comparator or comparable because
 * it doesn't require a (Comparable) cast for each comparison.
 * (Optimizers can only sometimes remove such redundant checks
 * themselves.) When Comparators are used,
 * ComparableUsingComparators are created so that they act in the
 * same way as natural orderings. This penalizes use of
 * Comparators vs Comparables, which seems like the right
 * tradeoff.
 */
static final class ComparableUsingComparator<K> implements Comparable<K> {
    final K actualKey;
    final Comparator<? super K> cmp;
    ComparableUsingComparator(    K key,    Comparator<? super K> cmp){
      this.actualKey=key;
      this.cmp=cmp;
    }
    public int compareTo(    K k2){
      return cmp.compare(actualKey,k2);
    }
  }
  /** 
 * If using comparator, return a ComparableUsingComparator, else
 * cast key as Comparable, which may cause ClassCastException,
 * which is propagated back to caller.
 */
  private Comparable<? super K> comparable(  Object key) throws ClassCastException {
    if (key == null)     throw new NullPointerException();
    if (comparator != null)     return new ComparableUsingComparator<K>((K)key,comparator);
 else     return (Comparable<? super K>)key;
  }
  /** 
 * Compares using comparator or natural ordering. Used when the
 * ComparableUsingComparator approach doesn't apply.
 */
  int compare(  K k1,  K k2) throws ClassCastException {
    Comparator<? super K> cmp=comparator;
    if (cmp != null)     return cmp.compare(k1,k2);
 else     return ((Comparable<? super K>)k1).compareTo(k2);
  }
  /** 
 * Returns true if given key greater than or equal to least and
 * strictly less than fence, bypassing either test if least or
 * fence are null. Needed mainly in submap operations.
 */
  boolean inHalfOpenRange(  K key,  K least,  K fence){
    if (key == null)     throw new NullPointerException();
    return ((least == null || compare(key,least) >= 0) && (fence == null || compare(key,fence) < 0));
  }
  /** 
 * Returns true if given key greater than or equal to least and less
 * or equal to fence. Needed mainly in submap operations.
 */
  boolean inOpenRange(  K key,  K least,  K fence){
    if (key == null)     throw new NullPointerException();
    return ((least == null || compare(key,least) >= 0) && (fence == null || compare(key,fence) <= 0));
  }
  /** 
 * Returns a base-level node with key strictly less than given key,
 * or the base-level header if there is no such node.  Also
 * unlinks indexes to deleted nodes found along the way.  Callers
 * rely on this side-effect of clearing indices to deleted nodes.
 * @param key the key
 * @return a predecessor of key
 */
  private Node<K,V> findPredecessor(  Comparable<? super K> key){
    if (key == null)     throw new NullPointerException();
    for (; ; ) {
      Index<K,V> q=head;
      Index<K,V> r=q.right;
      for (; ; ) {
        if (r != null) {
          Node<K,V> n=r.node;
          K k=n.key;
          if (n.value == null) {
            if (!q.unlink(r))             break;
            r=q.right;
            continue;
          }
          if (key.compareTo(k) > 0) {
            q=r;
            r=r.right;
            continue;
          }
        }
        Index<K,V> d=q.down;
        if (d != null) {
          q=d;
          r=d.right;
        }
 else         return q.node;
      }
    }
  }
  /** 
 * Returns node holding key or null if no such, clearing out any
 * deleted nodes seen along the way.  Repeatedly traverses at
 * base-level looking for key starting at predecessor returned
 * from findPredecessor, processing base-level deletions as
 * encountered. Some callers rely on this side-effect of clearing
 * deleted nodes.
 * Restarts occur, at traversal step centered on node n, if:
 * (1) After reading n's next field, n is no longer assumed
 * predecessor b's current successor, which means that
 * we don't have a consistent 3-node snapshot and so cannot
 * unlink any subsequent deleted nodes encountered.
 * (2) n's value field is null, indicating n is deleted, in
 * which case we help out an ongoing structural deletion
 * before retrying.  Even though there are cases where such
 * unlinking doesn't require restart, they aren't sorted out
 * here because doing so would not usually outweigh cost of
 * restarting.
 * (3) n is a marker or n's predecessor's value field is null,
 * indicating (among other possibilities) that
 * findPredecessor returned a deleted node. We can't unlink
 * the node because we don't know its predecessor, so rely
 * on another call to findPredecessor to notice and return
 * some earlier predecessor, which it will do. This check is
 * only strictly needed at beginning of loop, (and the
 * b.value check isn't strictly needed at all) but is done
 * each iteration to help avoid contention with other
 * threads by callers that will fail to be able to change
 * links, and so will retry anyway.
 * The traversal loops in doPut, doRemove, and findNear all
 * include the same three kinds of checks. And specialized
 * versions appear in findFirst, and findLast and their
 * variants. They can't easily share code because each uses the
 * reads of fields held in locals occurring in the orders they
 * were performed.
 * @param key the key
 * @return node holding key, or null if no such
 */
  private Node<K,V> findNode(  Comparable<? super K> key){
    for (; ; ) {
      Node<K,V> b=findPredecessor(key);
      Node<K,V> n=b.next;
      for (; ; ) {
        if (n == null)         return null;
        Node<K,V> f=n.next;
        if (n != b.next)         break;
        Object v=n.value;
        if (v == null) {
          n.helpDelete(b,f);
          break;
        }
        if (v == n || b.value == null)         break;
        int c=key.compareTo(n.key);
        if (c == 0)         return n;
        if (c < 0)         return null;
        b=n;
        n=f;
      }
    }
  }
  /** 
 * Gets value for key using findNode.
 * @param okey the key
 * @return the value, or null if absent
 */
  private V doGet(  Object okey){
    Comparable<? super K> key=comparable(okey);
    for (; ; ) {
      Node<K,V> n=findNode(key);
      if (n == null)       return null;
      Object v=n.value;
      if (v != null)       return (V)v;
    }
  }
  /** 
 * Main insertion method.  Adds element if not present, or
 * replaces value if present and onlyIfAbsent is false.
 * @param kkey the key
 * @param value  the value that must be associated with key
 * @param onlyIfAbsent if should not insert if already present
 * @return the old value, or null if newly inserted
 */
  private V doPut(  K kkey,  V value,  boolean onlyIfAbsent){
    Comparable<? super K> key=comparable(kkey);
    for (; ; ) {
      Node<K,V> b=findPredecessor(key);
      Node<K,V> n=b.next;
      for (; ; ) {
        if (n != null) {
          Node<K,V> f=n.next;
          if (n != b.next)           break;
          Object v=n.value;
          if (v == null) {
            n.helpDelete(b,f);
            break;
          }
          if (v == n || b.value == null)           break;
          int c=key.compareTo(n.key);
          if (c > 0) {
            b=n;
            n=f;
            continue;
          }
          if (c == 0) {
            if (onlyIfAbsent || n.casValue(v,value))             return (V)v;
 else             break;
          }
        }
        Node<K,V> z=new Node<K,V>(kkey,value,n);
        if (!b.casNext(n,z))         break;
        int level=randomLevel();
        if (level > 0)         insertIndex(z,level);
        return null;
      }
    }
  }
  /** 
 * Returns a random level for inserting a new node.
 * Hardwired to k=1, p=0.5, max 31 (see above and
 * Pugh's "Skip List Cookbook", sec 3.4).
 * This uses the simplest of the generators described in George
 * Marsaglia's "Xorshift RNGs" paper.  This is not a high-quality
 * generator but is acceptable here.
 */
  private int randomLevel(){
    int x=randomSeed;
    x^=x << 13;
    x^=x >>> 17;
    randomSeed=x^=x << 5;
    if ((x & 0x80000001) != 0)     return 0;
    int level=1;
    while (((x>>>=1) & 1) != 0)     ++level;
    return level;
  }
  /** 
 * Creates and adds index nodes for the given node.
 * @param z the node
 * @param level the level of the index
 */
  private void insertIndex(  Node<K,V> z,  int level){
    HeadIndex<K,V> h=head;
    int max=h.level;
    if (level <= max) {
      Index<K,V> idx=null;
      for (int i=1; i <= level; ++i)       idx=new Index<K,V>(z,idx,null);
      addIndex(idx,h,level);
    }
 else {
      level=max + 1;
      Index<K,V>[] idxs=(Index<K,V>[])new Index[level + 1];
      Index<K,V> idx=null;
      for (int i=1; i <= level; ++i)       idxs[i]=idx=new Index<K,V>(z,idx,null);
      HeadIndex<K,V> oldh;
      int k;
      for (; ; ) {
        oldh=head;
        int oldLevel=oldh.level;
        if (level <= oldLevel) {
          k=level;
          break;
        }
        HeadIndex<K,V> newh=oldh;
        Node<K,V> oldbase=oldh.node;
        for (int j=oldLevel + 1; j <= level; ++j)         newh=new HeadIndex<K,V>(oldbase,newh,idxs[j],j);
        if (casHead(oldh,newh)) {
          k=oldLevel;
          break;
        }
      }
      addIndex(idxs[k],oldh,k);
    }
  }
  /** 
 * Adds given index nodes from given level down to 1.
 * @param idx the topmost index node being inserted
 * @param h the value of head to use to insert. This must be
 * snapshotted by callers to provide correct insertion level
 * @param indexLevel the level of the index
 */
  private void addIndex(  Index<K,V> idx,  HeadIndex<K,V> h,  int indexLevel){
    int insertionLevel=indexLevel;
    Comparable<? super K> key=comparable(idx.node.key);
    if (key == null)     throw new NullPointerException();
    for (; ; ) {
      int j=h.level;
      Index<K,V> q=h;
      Index<K,V> r=q.right;
      Index<K,V> t=idx;
      for (; ; ) {
        if (r != null) {
          Node<K,V> n=r.node;
          int c=key.compareTo(n.key);
          if (n.value == null) {
            if (!q.unlink(r))             break;
            r=q.right;
            continue;
          }
          if (c > 0) {
            q=r;
            r=r.right;
            continue;
          }
        }
        if (j == insertionLevel) {
          if (t.indexesDeletedNode()) {
            findNode(key);
            return;
          }
          if (!q.link(r,t))           break;
          if (--insertionLevel == 0) {
            if (t.indexesDeletedNode())             findNode(key);
            return;
          }
        }
        if (--j >= insertionLevel && j < indexLevel)         t=t.down;
        q=q.down;
        r=q.right;
      }
    }
  }
  /** 
 * Main deletion method. Locates node, nulls value, appends a
 * deletion marker, unlinks predecessor, removes associated index
 * nodes, and possibly reduces head index level.
 * Index nodes are cleared out simply by calling findPredecessor.
 * which unlinks indexes to deleted nodes found along path to key,
 * which will include the indexes to this node.  This is done
 * unconditionally. We can't check beforehand whether there are
 * index nodes because it might be the case that some or all
 * indexes hadn't been inserted yet for this node during initial
 * search for it, and we'd like to ensure lack of garbage
 * retention, so must call to be sure.
 * @param okey the key
 * @param value if non-null, the value that must be
 * associated with key
 * @return the node, or null if not found
 */
  final V doRemove(  Object okey,  Object value){
    Comparable<? super K> key=comparable(okey);
    for (; ; ) {
      Node<K,V> b=findPredecessor(key);
      Node<K,V> n=b.next;
      for (; ; ) {
        if (n == null)         return null;
        Node<K,V> f=n.next;
        if (n != b.next)         break;
        Object v=n.value;
        if (v == null) {
          n.helpDelete(b,f);
          break;
        }
        if (v == n || b.value == null)         break;
        int c=key.compareTo(n.key);
        if (c < 0)         return null;
        if (c > 0) {
          b=n;
          n=f;
          continue;
        }
        if (value != null && !value.equals(v))         return null;
        if (!n.casValue(v,null))         break;
        if (!n.appendMarker(f) || !b.casNext(n,f))         findNode(key);
 else {
          findPredecessor(key);
          if (head.right == null)           tryReduceLevel();
        }
        return (V)v;
      }
    }
  }
  /** 
 * Possibly reduce head level if it has no nodes.  This method can
 * (rarely) make mistakes, in which case levels can disappear even
 * though they are about to contain index nodes. This impacts
 * performance, not correctness.  To minimize mistakes as well as
 * to reduce hysteresis, the level is reduced by one only if the
 * topmost three levels look empty. Also, if the removed level
 * looks non-empty after CAS, we try to change it back quick
 * before anyone notices our mistake! (This trick works pretty
 * well because this method will practically never make mistakes
 * unless current thread stalls immediately before first CAS, in
 * which case it is very unlikely to stall again immediately
 * afterwards, so will recover.)
 * We put up with all this rather than just let levels grow
 * because otherwise, even a small map that has undergone a large
 * number of insertions and removals will have a lot of levels,
 * slowing down access more than would an occasional unwanted
 * reduction.
 */
  private void tryReduceLevel(){
    HeadIndex<K,V> h=head;
    HeadIndex<K,V> d;
    HeadIndex<K,V> e;
    if (h.level > 3 && (d=(HeadIndex<K,V>)h.down) != null && (e=(HeadIndex<K,V>)d.down) != null && e.right == null && d.right == null && h.right == null && casHead(h,d) && h.right != null)     casHead(d,h);
  }
  /** 
 * Specialized variant of findNode to get first valid node.
 * @return first node or null if empty
 */
  Node<K,V> findFirst(){
    for (; ; ) {
      Node<K,V> b=head.node;
      Node<K,V> n=b.next;
      if (n == null)       return null;
      if (n.value != null)       return n;
      n.helpDelete(b,n.next);
    }
  }
  /** 
 * Removes first entry; returns its snapshot.
 * @return null if empty, else snapshot of first entry
 */
  Map.Entry<K,V> doRemoveFirstEntry(){
    for (; ; ) {
      Node<K,V> b=head.node;
      Node<K,V> n=b.next;
      if (n == null)       return null;
      Node<K,V> f=n.next;
      if (n != b.next)       continue;
      Object v=n.value;
      if (v == null) {
        n.helpDelete(b,f);
        continue;
      }
      if (!n.casValue(v,null))       continue;
      if (!n.appendMarker(f) || !b.casNext(n,f))       findFirst();
      clearIndexToFirst();
      return new AbstractMap.SimpleImmutableEntry<K,V>(n.key,(V)v);
    }
  }
  /** 
 * Clears out index nodes associated with deleted first entry.
 */
  private void clearIndexToFirst(){
    for (; ; ) {
      Index<K,V> q=head;
      for (; ; ) {
        Index<K,V> r=q.right;
        if (r != null && r.indexesDeletedNode() && !q.unlink(r))         break;
        if ((q=q.down) == null) {
          if (head.right == null)           tryReduceLevel();
          return;
        }
      }
    }
  }
  /** 
 * Specialized version of find to get last valid node.
 * @return last node or null if empty
 */
  Node<K,V> findLast(){
    Index<K,V> q=head;
    for (; ; ) {
      Index<K,V> d, r;
      if ((r=q.right) != null) {
        if (r.indexesDeletedNode()) {
          q.unlink(r);
          q=head;
        }
 else         q=r;
      }
 else       if ((d=q.down) != null) {
        q=d;
      }
 else {
        Node<K,V> b=q.node;
        Node<K,V> n=b.next;
        for (; ; ) {
          if (n == null)           return b.isBaseHeader() ? null : b;
          Node<K,V> f=n.next;
          if (n != b.next)           break;
          Object v=n.value;
          if (v == null) {
            n.helpDelete(b,f);
            break;
          }
          if (v == n || b.value == null)           break;
          b=n;
          n=f;
        }
        q=head;
      }
    }
  }
  /** 
 * Specialized variant of findPredecessor to get predecessor of last
 * valid node.  Needed when removing the last entry.  It is possible
 * that all successors of returned node will have been deleted upon
 * return, in which case this method can be retried.
 * @return likely predecessor of last node
 */
  private Node<K,V> findPredecessorOfLast(){
    for (; ; ) {
      Index<K,V> q=head;
      for (; ; ) {
        Index<K,V> d, r;
        if ((r=q.right) != null) {
          if (r.indexesDeletedNode()) {
            q.unlink(r);
            break;
          }
          if (r.node.next != null) {
            q=r;
            continue;
          }
        }
        if ((d=q.down) != null)         q=d;
 else         return q.node;
      }
    }
  }
  /** 
 * Removes last entry; returns its snapshot.
 * Specialized variant of doRemove.
 * @return null if empty, else snapshot of last entry
 */
  Map.Entry<K,V> doRemoveLastEntry(){
    for (; ; ) {
      Node<K,V> b=findPredecessorOfLast();
      Node<K,V> n=b.next;
      if (n == null) {
        if (b.isBaseHeader())         return null;
 else         continue;
      }
      for (; ; ) {
        Node<K,V> f=n.next;
        if (n != b.next)         break;
        Object v=n.value;
        if (v == null) {
          n.helpDelete(b,f);
          break;
        }
        if (v == n || b.value == null)         break;
        if (f != null) {
          b=n;
          n=f;
          continue;
        }
        if (!n.casValue(v,null))         break;
        K key=n.key;
        Comparable<? super K> ck=comparable(key);
        if (!n.appendMarker(f) || !b.casNext(n,f))         findNode(ck);
 else {
          findPredecessor(ck);
          if (head.right == null)           tryReduceLevel();
        }
        return new AbstractMap.SimpleImmutableEntry<K,V>(key,(V)v);
      }
    }
  }
  private static final int EQ=1;
  private static final int LT=2;
  private static final int GT=0;
  /** 
 * Utility for ceiling, floor, lower, higher methods.
 * @param kkey the key
 * @param rel the relation -- OR'ed combination of EQ, LT, GT
 * @return nearest node fitting relation, or null if no such
 */
  Node<K,V> findNear(  K kkey,  int rel){
    Comparable<? super K> key=comparable(kkey);
    for (; ; ) {
      Node<K,V> b=findPredecessor(key);
      Node<K,V> n=b.next;
      for (; ; ) {
        if (n == null)         return ((rel & LT) == 0 || b.isBaseHeader()) ? null : b;
        Node<K,V> f=n.next;
        if (n != b.next)         break;
        Object v=n.value;
        if (v == null) {
          n.helpDelete(b,f);
          break;
        }
        if (v == n || b.value == null)         break;
        int c=key.compareTo(n.key);
        if ((c == 0 && (rel & EQ) != 0) || (c < 0 && (rel & LT) == 0))         return n;
        if (c <= 0 && (rel & LT) != 0)         return b.isBaseHeader() ? null : b;
        b=n;
        n=f;
      }
    }
  }
  /** 
 * Returns SimpleImmutableEntry for results of findNear.
 * @param key the key
 * @param rel the relation -- OR'ed combination of EQ, LT, GT
 * @return Entry fitting relation, or null if no such
 */
  AbstractMap.SimpleImmutableEntry<K,V> getNear(  K key,  int rel){
    for (; ; ) {
      Node<K,V> n=findNear(key,rel);
      if (n == null)       return null;
      AbstractMap.SimpleImmutableEntry<K,V> e=n.createSnapshot();
      if (e != null)       return e;
    }
  }
  /** 
 * Constructs a new, empty map, sorted according to the{@linkplain Comparable natural ordering} of the keys.
 */
  public ConcurrentSkipListMap(){
    this.comparator=null;
    initialize();
  }
  /** 
 * Constructs a new, empty map, sorted according to the specified
 * comparator.
 * @param comparator the comparator that will be used to order this map.
 * If <tt>null</tt>, the {@linkplain Comparable natural
 * ordering} of the keys will be used.
 */
  public ConcurrentSkipListMap(  Comparator<? super K> comparator){
    this.comparator=comparator;
    initialize();
  }
  /** 
 * Constructs a new map containing the same mappings as the given map,
 * sorted according to the {@linkplain Comparable natural ordering} of
 * the keys.
 * @param m the map whose mappings are to be placed in this map
 * @throws ClassCastException if the keys in <tt>m</tt> are not{@link Comparable}, or are not mutually comparable
 * @throws NullPointerException if the specified map or any of its keys
 * or values are null
 */
  public ConcurrentSkipListMap(  Map<? extends K,? extends V> m){
    this.comparator=null;
    initialize();
    putAll(m);
  }
  /** 
 * Constructs a new map containing the same mappings and using the
 * same ordering as the specified sorted map.
 * @param m the sorted map whose mappings are to be placed in this
 * map, and whose comparator is to be used to sort this map
 * @throws NullPointerException if the specified sorted map or any of
 * its keys or values are null
 */
  public ConcurrentSkipListMap(  SortedMap<K,? extends V> m){
    this.comparator=m.comparator();
    initialize();
    buildFromSorted(m);
  }
  /** 
 * Returns a shallow copy of this <tt>ConcurrentSkipListMap</tt>
 * instance. (The keys and values themselves are not cloned.)
 * @return a shallow copy of this map
 */
  public ConcurrentSkipListMap<K,V> clone(){
    ConcurrentSkipListMap<K,V> clone=null;
    try {
      clone=(ConcurrentSkipListMap<K,V>)super.clone();
    }
 catch (    CloneNotSupportedException e) {
      throw new InternalError();
    }
    clone.initialize();
    clone.buildFromSorted(this);
    return clone;
  }
  /** 
 * Streamlined bulk insertion to initialize from elements of
 * given sorted map.  Call only from constructor or clone
 * method.
 */
  private void buildFromSorted(  SortedMap<K,? extends V> map){
    if (map == null)     throw new NullPointerException();
    HeadIndex<K,V> h=head;
    Node<K,V> basepred=h.node;
    ArrayList<Index<K,V>> preds=new ArrayList<Index<K,V>>();
    for (int i=0; i <= h.level; ++i)     preds.add(null);
    Index<K,V> q=h;
    for (int i=h.level; i > 0; --i) {
      preds.set(i,q);
      q=q.down;
    }
    Iterator<? extends Map.Entry<? extends K,? extends V>> it=map.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry<? extends K,? extends V> e=it.next();
      int j=randomLevel();
      if (j > h.level)       j=h.level + 1;
      K k=e.getKey();
      V v=e.getValue();
      if (k == null || v == null)       throw new NullPointerException();
      Node<K,V> z=new Node<K,V>(k,v,null);
      basepred.next=z;
      basepred=z;
      if (j > 0) {
        Index<K,V> idx=null;
        for (int i=1; i <= j; ++i) {
          idx=new Index<K,V>(z,idx,null);
          if (i > h.level)           h=new HeadIndex<K,V>(h.node,h,idx,i);
          if (i < preds.size()) {
            preds.get(i).right=idx;
            preds.set(i,idx);
          }
 else           preds.add(idx);
        }
      }
    }
    head=h;
  }
  /** 
 * Save the state of this map to a stream.
 * @serialData The key (Object) and value (Object) for each
 * key-value mapping represented by the map, followed by
 * <tt>null</tt>. The key-value mappings are emitted in key-order
 * (as determined by the Comparator, or by the keys' natural
 * ordering if no Comparator).
 */
  private void writeObject(  java.io.ObjectOutputStream s) throws java.io.IOException {
    s.defaultWriteObject();
    for (Node<K,V> n=findFirst(); n != null; n=n.next) {
      V v=n.getValidValue();
      if (v != null) {
        s.writeObject(n.key);
        s.writeObject(v);
      }
    }
    s.writeObject(null);
  }
  /** 
 * Reconstitute the map from a stream.
 */
  private void readObject(  final java.io.ObjectInputStream s) throws java.io.IOException, ClassNotFoundException {
    s.defaultReadObject();
    initialize();
    HeadIndex<K,V> h=head;
    Node<K,V> basepred=h.node;
    ArrayList<Index<K,V>> preds=new ArrayList<Index<K,V>>();
    for (int i=0; i <= h.level; ++i)     preds.add(null);
    Index<K,V> q=h;
    for (int i=h.level; i > 0; --i) {
      preds.set(i,q);
      q=q.down;
    }
    for (; ; ) {
      Object k=s.readObject();
      if (k == null)       break;
      Object v=s.readObject();
      if (v == null)       throw new NullPointerException();
      K key=(K)k;
      V val=(V)v;
      int j=randomLevel();
      if (j > h.level)       j=h.level + 1;
      Node<K,V> z=new Node<K,V>(key,val,null);
      basepred.next=z;
      basepred=z;
      if (j > 0) {
        Index<K,V> idx=null;
        for (int i=1; i <= j; ++i) {
          idx=new Index<K,V>(z,idx,null);
          if (i > h.level)           h=new HeadIndex<K,V>(h.node,h,idx,i);
          if (i < preds.size()) {
            preds.get(i).right=idx;
            preds.set(i,idx);
          }
 else           preds.add(idx);
        }
      }
    }
    head=h;
  }
  /** 
 * Returns <tt>true</tt> if this map contains a mapping for the specified
 * key.
 * @param key key whose presence in this map is to be tested
 * @return <tt>true</tt> if this map contains a mapping for the specified key
 * @throws ClassCastException if the specified key cannot be compared
 * with the keys currently in the map
 * @throws NullPointerException if the specified key is null
 */
  public boolean containsKey(  Object key){
    return doGet(key) != null;
  }
  /** 
 * Returns the value to which the specified key is mapped,
 * or {@code null} if this map contains no mapping for the key.
 * <p>More formally, if this map contains a mapping from a key{@code k} to a value {@code v} such that {@code key} compares
 * equal to {@code k} according to the map's ordering, then this
 * method returns {@code v}; otherwise it returns {@code null}.
 * (There can be at most one such mapping.)
 * @throws ClassCastException if the specified key cannot be compared
 * with the keys currently in the map
 * @throws NullPointerException if the specified key is null
 */
  public V get(  Object key){
    return doGet(key);
  }
  /** 
 * Associates the specified value with the specified key in this map.
 * If the map previously contained a mapping for the key, the old
 * value is replaced.
 * @param key key with which the specified value is to be associated
 * @param value value to be associated with the specified key
 * @return the previous value associated with the specified key, or
 * <tt>null</tt> if there was no mapping for the key
 * @throws ClassCastException if the specified key cannot be compared
 * with the keys currently in the map
 * @throws NullPointerException if the specified key or value is null
 */
  public V put(  K key,  V value){
    if (value == null)     throw new NullPointerException();
    return doPut(key,value,false);
  }
  /** 
 * Removes the mapping for the specified key from this map if present.
 * @param key key for which mapping should be removed
 * @return the previous value associated with the specified key, or
 * <tt>null</tt> if there was no mapping for the key
 * @throws ClassCastException if the specified key cannot be compared
 * with the keys currently in the map
 * @throws NullPointerException if the specified key is null
 */
  public V remove(  Object key){
    return doRemove(key,null);
  }
  /** 
 * Returns <tt>true</tt> if this map maps one or more keys to the
 * specified value.  This operation requires time linear in the
 * map size. Additionally, it is possible for the map to change
 * during execution of this method, in which case the returned
 * result may be inaccurate.
 * @param value value whose presence in this map is to be tested
 * @return <tt>true</tt> if a mapping to <tt>value</tt> exists;
 * <tt>false</tt> otherwise
 * @throws NullPointerException if the specified value is null
 */
  public boolean containsValue(  Object value){
    if (value == null)     throw new NullPointerException();
    for (Node<K,V> n=findFirst(); n != null; n=n.next) {
      V v=n.getValidValue();
      if (v != null && value.equals(v))       return true;
    }
    return false;
  }
  /** 
 * Returns the number of key-value mappings in this map.  If this map
 * contains more than <tt>Integer.MAX_VALUE</tt> elements, it
 * returns <tt>Integer.MAX_VALUE</tt>.
 * <p>Beware that, unlike in most collections, this method is
 * <em>NOT</em> a constant-time operation. Because of the
 * asynchronous nature of these maps, determining the current
 * number of elements requires traversing them all to count them.
 * Additionally, it is possible for the size to change during
 * execution of this method, in which case the returned result
 * will be inaccurate. Thus, this method is typically not very
 * useful in concurrent applications.
 * @return the number of elements in this map
 */
  public int size(){
    long count=0;
    for (Node<K,V> n=findFirst(); n != null; n=n.next) {
      if (n.getValidValue() != null)       ++count;
    }
    return (count >= Integer.MAX_VALUE) ? Integer.MAX_VALUE : (int)count;
  }
  /** 
 * Returns <tt>true</tt> if this map contains no key-value mappings.
 * @return <tt>true</tt> if this map contains no key-value mappings
 */
  public boolean isEmpty(){
    return findFirst() == null;
  }
  /** 
 * Removes all of the mappings from this map.
 */
  public void clear(){
    initialize();
  }
  /** 
 * Returns a {@link NavigableSet} view of the keys contained in this map.
 * The set's iterator returns the keys in ascending order.
 * The set is backed by the map, so changes to the map are
 * reflected in the set, and vice-versa.  The set supports element
 * removal, which removes the corresponding mapping from the map,
 * via the {@code Iterator.remove}, {@code Set.remove},{@code removeAll}, {@code retainAll}, and {@code clear}operations.  It does not support the {@code add} or {@code addAll}operations.
 * <p>The view's {@code iterator} is a "weakly consistent" iterator
 * that will never throw {@link ConcurrentModificationException},
 * and guarantees to traverse elements as they existed upon
 * construction of the iterator, and may (but is not guaranteed to)
 * reflect any modifications subsequent to construction.
 * <p>This method is equivalent to method {@code navigableKeySet}.
 * @return a navigable set view of the keys in this map
 */
  public NavigableSet<K> keySet(){
    KeySet ks=keySet;
    return (ks != null) ? ks : (keySet=new KeySet(this));
  }
  public NavigableSet<K> navigableKeySet(){
    KeySet ks=keySet;
    return (ks != null) ? ks : (keySet=new KeySet(this));
  }
  /** 
 * Returns a {@link Collection} view of the values contained in this map.
 * The collection's iterator returns the values in ascending order
 * of the corresponding keys.
 * The collection is backed by the map, so changes to the map are
 * reflected in the collection, and vice-versa.  The collection
 * supports element removal, which removes the corresponding
 * mapping from the map, via the <tt>Iterator.remove</tt>,
 * <tt>Collection.remove</tt>, <tt>removeAll</tt>,
 * <tt>retainAll</tt> and <tt>clear</tt> operations.  It does not
 * support the <tt>add</tt> or <tt>addAll</tt> operations.
 * <p>The view's <tt>iterator</tt> is a "weakly consistent" iterator
 * that will never throw {@link ConcurrentModificationException},
 * and guarantees to traverse elements as they existed upon
 * construction of the iterator, and may (but is not guaranteed to)
 * reflect any modifications subsequent to construction.
 */
  public Collection<V> values(){
    Values vs=values;
    return (vs != null) ? vs : (values=new Values(this));
  }
  /** 
 * Returns a {@link Set} view of the mappings contained in this map.
 * The set's iterator returns the entries in ascending key order.
 * The set is backed by the map, so changes to the map are
 * reflected in the set, and vice-versa.  The set supports element
 * removal, which removes the corresponding mapping from the map,
 * via the <tt>Iterator.remove</tt>, <tt>Set.remove</tt>,
 * <tt>removeAll</tt>, <tt>retainAll</tt> and <tt>clear</tt>
 * operations.  It does not support the <tt>add</tt> or
 * <tt>addAll</tt> operations.
 * <p>The view's <tt>iterator</tt> is a "weakly consistent" iterator
 * that will never throw {@link ConcurrentModificationException},
 * and guarantees to traverse elements as they existed upon
 * construction of the iterator, and may (but is not guaranteed to)
 * reflect any modifications subsequent to construction.
 * <p>The <tt>Map.Entry</tt> elements returned by
 * <tt>iterator.next()</tt> do <em>not</em> support the
 * <tt>setValue</tt> operation.
 * @return a set view of the mappings contained in this map,
 * sorted in ascending key order
 */
  public Set<Map.Entry<K,V>> entrySet(){
    EntrySet es=entrySet;
    return (es != null) ? es : (entrySet=new EntrySet(this));
  }
  public ConcurrentNavigableMap<K,V> descendingMap(){
    ConcurrentNavigableMap<K,V> dm=descendingMap;
    return (dm != null) ? dm : (descendingMap=new SubMap<K,V>(this,null,false,null,false,true));
  }
  public NavigableSet<K> descendingKeySet(){
    return descendingMap().navigableKeySet();
  }
  /** 
 * Compares the specified object with this map for equality.
 * Returns <tt>true</tt> if the given object is also a map and the
 * two maps represent the same mappings.  More formally, two maps
 * <tt>m1</tt> and <tt>m2</tt> represent the same mappings if
 * <tt>m1.entrySet().equals(m2.entrySet())</tt>.  This
 * operation may return misleading results if either map is
 * concurrently modified during execution of this method.
 * @param o object to be compared for equality with this map
 * @return <tt>true</tt> if the specified object is equal to this map
 */
  public boolean equals(  Object o){
    if (o == this)     return true;
    if (!(o instanceof Map))     return false;
    Map<?,?> m=(Map<?,?>)o;
    try {
      for (      Map.Entry<K,V> e : this.entrySet())       if (!e.getValue().equals(m.get(e.getKey())))       return false;
      for (      Map.Entry<?,?> e : m.entrySet()) {
        Object k=e.getKey();
        Object v=e.getValue();
        if (k == null || v == null || !v.equals(get(k)))         return false;
      }
      return true;
    }
 catch (    ClassCastException unused) {
      return false;
    }
catch (    NullPointerException unused) {
      return false;
    }
  }
  /** 
 * {@inheritDoc}
 * @return the previous value associated with the specified key,
 * or <tt>null</tt> if there was no mapping for the key
 * @throws ClassCastException if the specified key cannot be compared
 * with the keys currently in the map
 * @throws NullPointerException if the specified key or value is null
 */
  public V putIfAbsent(  K key,  V value){
    if (value == null)     throw new NullPointerException();
    return doPut(key,value,true);
  }
  /** 
 * {@inheritDoc}
 * @throws ClassCastException if the specified key cannot be compared
 * with the keys currently in the map
 * @throws NullPointerException if the specified key is null
 */
  public boolean remove(  Object key,  Object value){
    if (key == null)     throw new NullPointerException();
    if (value == null)     return false;
    return doRemove(key,value) != null;
  }
  /** 
 * {@inheritDoc}
 * @throws ClassCastException if the specified key cannot be compared
 * with the keys currently in the map
 * @throws NullPointerException if any of the arguments are null
 */
  public boolean replace(  K key,  V oldValue,  V newValue){
    if (oldValue == null || newValue == null)     throw new NullPointerException();
    Comparable<? super K> k=comparable(key);
    for (; ; ) {
      Node<K,V> n=findNode(k);
      if (n == null)       return false;
      Object v=n.value;
      if (v != null) {
        if (!oldValue.equals(v))         return false;
        if (n.casValue(v,newValue))         return true;
      }
    }
  }
  /** 
 * {@inheritDoc}
 * @return the previous value associated with the specified key,
 * or <tt>null</tt> if there was no mapping for the key
 * @throws ClassCastException if the specified key cannot be compared
 * with the keys currently in the map
 * @throws NullPointerException if the specified key or value is null
 */
  public V replace(  K key,  V value){
    if (value == null)     throw new NullPointerException();
    Comparable<? super K> k=comparable(key);
    for (; ; ) {
      Node<K,V> n=findNode(k);
      if (n == null)       return null;
      Object v=n.value;
      if (v != null && n.casValue(v,value))       return (V)v;
    }
  }
  public Comparator<? super K> comparator(){
    return comparator;
  }
  /** 
 * @throws NoSuchElementException {@inheritDoc}
 */
  public K firstKey(){
    Node<K,V> n=findFirst();
    if (n == null)     throw new NoSuchElementException();
    return n.key;
  }
  /** 
 * @throws NoSuchElementException {@inheritDoc}
 */
  public K lastKey(){
    Node<K,V> n=findLast();
    if (n == null)     throw new NoSuchElementException();
    return n.key;
  }
  /** 
 * @throws ClassCastException {@inheritDoc}
 * @throws NullPointerException if {@code fromKey} or {@code toKey} is null
 * @throws IllegalArgumentException {@inheritDoc}
 */
  public ConcurrentNavigableMap<K,V> subMap(  K fromKey,  boolean fromInclusive,  K toKey,  boolean toInclusive){
    if (fromKey == null || toKey == null)     throw new NullPointerException();
    return new SubMap<K,V>(this,fromKey,fromInclusive,toKey,toInclusive,false);
  }
  /** 
 * @throws ClassCastException {@inheritDoc}
 * @throws NullPointerException if {@code toKey} is null
 * @throws IllegalArgumentException {@inheritDoc}
 */
  public ConcurrentNavigableMap<K,V> headMap(  K toKey,  boolean inclusive){
    if (toKey == null)     throw new NullPointerException();
    return new SubMap<K,V>(this,null,false,toKey,inclusive,false);
  }
  /** 
 * @throws ClassCastException {@inheritDoc}
 * @throws NullPointerException if {@code fromKey} is null
 * @throws IllegalArgumentException {@inheritDoc}
 */
  public ConcurrentNavigableMap<K,V> tailMap(  K fromKey,  boolean inclusive){
    if (fromKey == null)     throw new NullPointerException();
    return new SubMap<K,V>(this,fromKey,inclusive,null,false,false);
  }
  /** 
 * @throws ClassCastException {@inheritDoc}
 * @throws NullPointerException if {@code fromKey} or {@code toKey} is null
 * @throws IllegalArgumentException {@inheritDoc}
 */
  public ConcurrentNavigableMap<K,V> subMap(  K fromKey,  K toKey){
    return subMap(fromKey,true,toKey,false);
  }
  /** 
 * @throws ClassCastException {@inheritDoc}
 * @throws NullPointerException if {@code toKey} is null
 * @throws IllegalArgumentException {@inheritDoc}
 */
  public ConcurrentNavigableMap<K,V> headMap(  K toKey){
    return headMap(toKey,false);
  }
  /** 
 * @throws ClassCastException {@inheritDoc}
 * @throws NullPointerException if {@code fromKey} is null
 * @throws IllegalArgumentException {@inheritDoc}
 */
  public ConcurrentNavigableMap<K,V> tailMap(  K fromKey){
    return tailMap(fromKey,true);
  }
  /** 
 * Returns a key-value mapping associated with the greatest key
 * strictly less than the given key, or <tt>null</tt> if there is
 * no such key. The returned entry does <em>not</em> support the
 * <tt>Entry.setValue</tt> method.
 * @throws ClassCastException {@inheritDoc}
 * @throws NullPointerException if the specified key is null
 */
  public Map.Entry<K,V> lowerEntry(  K key){
    return getNear(key,LT);
  }
  /** 
 * @throws ClassCastException {@inheritDoc}
 * @throws NullPointerException if the specified key is null
 */
  public K lowerKey(  K key){
    Node<K,V> n=findNear(key,LT);
    return (n == null) ? null : n.key;
  }
  /** 
 * Returns a key-value mapping associated with the greatest key
 * less than or equal to the given key, or <tt>null</tt> if there
 * is no such key. The returned entry does <em>not</em> support
 * the <tt>Entry.setValue</tt> method.
 * @param key the key
 * @throws ClassCastException {@inheritDoc}
 * @throws NullPointerException if the specified key is null
 */
  public Map.Entry<K,V> floorEntry(  K key){
    return getNear(key,LT | EQ);
  }
  /** 
 * @param key the key
 * @throws ClassCastException {@inheritDoc}
 * @throws NullPointerException if the specified key is null
 */
  public K floorKey(  K key){
    Node<K,V> n=findNear(key,LT | EQ);
    return (n == null) ? null : n.key;
  }
  /** 
 * Returns a key-value mapping associated with the least key
 * greater than or equal to the given key, or <tt>null</tt> if
 * there is no such entry. The returned entry does <em>not</em>
 * support the <tt>Entry.setValue</tt> method.
 * @throws ClassCastException {@inheritDoc}
 * @throws NullPointerException if the specified key is null
 */
  public Map.Entry<K,V> ceilingEntry(  K key){
    return getNear(key,GT | EQ);
  }
  /** 
 * @throws ClassCastException {@inheritDoc}
 * @throws NullPointerException if the specified key is null
 */
  public K ceilingKey(  K key){
    Node<K,V> n=findNear(key,GT | EQ);
    return (n == null) ? null : n.key;
  }
  /** 
 * Returns a key-value mapping associated with the least key
 * strictly greater than the given key, or <tt>null</tt> if there
 * is no such key. The returned entry does <em>not</em> support
 * the <tt>Entry.setValue</tt> method.
 * @param key the key
 * @throws ClassCastException {@inheritDoc}
 * @throws NullPointerException if the specified key is null
 */
  public Map.Entry<K,V> higherEntry(  K key){
    return getNear(key,GT);
  }
  /** 
 * @param key the key
 * @throws ClassCastException {@inheritDoc}
 * @throws NullPointerException if the specified key is null
 */
  public K higherKey(  K key){
    Node<K,V> n=findNear(key,GT);
    return (n == null) ? null : n.key;
  }
  /** 
 * Returns a key-value mapping associated with the least
 * key in this map, or <tt>null</tt> if the map is empty.
 * The returned entry does <em>not</em> support
 * the <tt>Entry.setValue</tt> method.
 */
  public Map.Entry<K,V> firstEntry(){
    for (; ; ) {
      Node<K,V> n=findFirst();
      if (n == null)       return null;
      AbstractMap.SimpleImmutableEntry<K,V> e=n.createSnapshot();
      if (e != null)       return e;
    }
  }
  /** 
 * Returns a key-value mapping associated with the greatest
 * key in this map, or <tt>null</tt> if the map is empty.
 * The returned entry does <em>not</em> support
 * the <tt>Entry.setValue</tt> method.
 */
  public Map.Entry<K,V> lastEntry(){
    for (; ; ) {
      Node<K,V> n=findLast();
      if (n == null)       return null;
      AbstractMap.SimpleImmutableEntry<K,V> e=n.createSnapshot();
      if (e != null)       return e;
    }
  }
  /** 
 * Removes and returns a key-value mapping associated with
 * the least key in this map, or <tt>null</tt> if the map is empty.
 * The returned entry does <em>not</em> support
 * the <tt>Entry.setValue</tt> method.
 */
  public Map.Entry<K,V> pollFirstEntry(){
    return doRemoveFirstEntry();
  }
  /** 
 * Removes and returns a key-value mapping associated with
 * the greatest key in this map, or <tt>null</tt> if the map is empty.
 * The returned entry does <em>not</em> support
 * the <tt>Entry.setValue</tt> method.
 */
  public Map.Entry<K,V> pollLastEntry(){
    return doRemoveLastEntry();
  }
  /** 
 * Base of iterator classes:
 */
abstract class Iter<T> implements Iterator<T> {
    /** 
 * the last node returned by next() 
 */
    Node<K,V> lastReturned;
    /** 
 * the next node to return from next(); 
 */
    Node<K,V> next;
    /** 
 * Cache of next value field to maintain weak consistency 
 */
    V nextValue;
    /** 
 * Initializes ascending iterator for entire range. 
 */
    Iter(){
      for (; ; ) {
        next=findFirst();
        if (next == null)         break;
        Object x=next.value;
        if (x != null && x != next) {
          nextValue=(V)x;
          break;
        }
      }
    }
    public final boolean hasNext(){
      return next != null;
    }
    /** 
 * Advances next to higher entry. 
 */
    final void advance(){
      if (next == null)       throw new NoSuchElementException();
      lastReturned=next;
      for (; ; ) {
        next=next.next;
        if (next == null)         break;
        Object x=next.value;
        if (x != null && x != next) {
          nextValue=(V)x;
          break;
        }
      }
    }
    public void remove(){
      Node<K,V> l=lastReturned;
      if (l == null)       throw new IllegalStateException();
      ConcurrentSkipListMap.this.remove(l.key);
      lastReturned=null;
    }
  }
final class ValueIterator extends Iter<V> {
    public V next(){
      V v=nextValue;
      advance();
      return v;
    }
  }
final class KeyIterator extends Iter<K> {
    public K next(){
      Node<K,V> n=next;
      advance();
      return n.key;
    }
  }
final class EntryIterator extends Iter<Map.Entry<K,V>> {
    public Map.Entry<K,V> next(){
      Node<K,V> n=next;
      V v=nextValue;
      advance();
      return new AbstractMap.SimpleImmutableEntry<K,V>(n.key,v);
    }
  }
  Iterator<K> keyIterator(){
    return new KeyIterator();
  }
  Iterator<V> valueIterator(){
    return new ValueIterator();
  }
  Iterator<Map.Entry<K,V>> entryIterator(){
    return new EntryIterator();
  }
  static final <E>List<E> toList(  Collection<E> c){
    List<E> list=new ArrayList<E>();
    for (    E e : c)     list.add(e);
    return list;
  }
static final class KeySet<E> extends AbstractSet<E> implements NavigableSet<E> {
    private final ConcurrentNavigableMap<E,Object> m;
    KeySet(    ConcurrentNavigableMap<E,Object> map){
      m=map;
    }
    public int size(){
      return m.size();
    }
    public boolean isEmpty(){
      return m.isEmpty();
    }
    public boolean contains(    Object o){
      return m.containsKey(o);
    }
    public boolean remove(    Object o){
      return m.remove(o) != null;
    }
    public void clear(){
      m.clear();
    }
    public E lower(    E e){
      return m.lowerKey(e);
    }
    public E floor(    E e){
      return m.floorKey(e);
    }
    public E ceiling(    E e){
      return m.ceilingKey(e);
    }
    public E higher(    E e){
      return m.higherKey(e);
    }
    public Comparator<? super E> comparator(){
      return m.comparator();
    }
    public E first(){
      return m.firstKey();
    }
    public E last(){
      return m.lastKey();
    }
    public E pollFirst(){
      Map.Entry<E,Object> e=m.pollFirstEntry();
      return (e == null) ? null : e.getKey();
    }
    public E pollLast(){
      Map.Entry<E,Object> e=m.pollLastEntry();
      return (e == null) ? null : e.getKey();
    }
    public Iterator<E> iterator(){
      if (m instanceof ConcurrentSkipListMap)       return ((ConcurrentSkipListMap<E,Object>)m).keyIterator();
 else       return ((ConcurrentSkipListMap.SubMap<E,Object>)m).keyIterator();
    }
    public boolean equals(    Object o){
      if (o == this)       return true;
      if (!(o instanceof Set))       return false;
      Collection<?> c=(Collection<?>)o;
      try {
        return containsAll(c) && c.containsAll(this);
      }
 catch (      ClassCastException unused) {
        return false;
      }
catch (      NullPointerException unused) {
        return false;
      }
    }
    public Object[] toArray(){
      return toList(this).toArray();
    }
    public <T>T[] toArray(    T[] a){
      return toList(this).toArray(a);
    }
    public Iterator<E> descendingIterator(){
      return descendingSet().iterator();
    }
    public NavigableSet<E> subSet(    E fromElement,    boolean fromInclusive,    E toElement,    boolean toInclusive){
      return new KeySet<E>(m.subMap(fromElement,fromInclusive,toElement,toInclusive));
    }
    public NavigableSet<E> headSet(    E toElement,    boolean inclusive){
      return new KeySet<E>(m.headMap(toElement,inclusive));
    }
    public NavigableSet<E> tailSet(    E fromElement,    boolean inclusive){
      return new KeySet<E>(m.tailMap(fromElement,inclusive));
    }
    public NavigableSet<E> subSet(    E fromElement,    E toElement){
      return subSet(fromElement,true,toElement,false);
    }
    public NavigableSet<E> headSet(    E toElement){
      return headSet(toElement,false);
    }
    public NavigableSet<E> tailSet(    E fromElement){
      return tailSet(fromElement,true);
    }
    public NavigableSet<E> descendingSet(){
      return new KeySet(m.descendingMap());
    }
  }
static final class Values<E> extends AbstractCollection<E> {
    private final ConcurrentNavigableMap<Object,E> m;
    Values(    ConcurrentNavigableMap<Object,E> map){
      m=map;
    }
    public Iterator<E> iterator(){
      if (m instanceof ConcurrentSkipListMap)       return ((ConcurrentSkipListMap<Object,E>)m).valueIterator();
 else       return ((SubMap<Object,E>)m).valueIterator();
    }
    public boolean isEmpty(){
      return m.isEmpty();
    }
    public int size(){
      return m.size();
    }
    public boolean contains(    Object o){
      return m.containsValue(o);
    }
    public void clear(){
      m.clear();
    }
    public Object[] toArray(){
      return toList(this).toArray();
    }
    public <T>T[] toArray(    T[] a){
      return toList(this).toArray(a);
    }
  }
static final class EntrySet<K1,V1> extends AbstractSet<Map.Entry<K1,V1>> {
    private final ConcurrentNavigableMap<K1,V1> m;
    EntrySet(    ConcurrentNavigableMap<K1,V1> map){
      m=map;
    }
    public Iterator<Map.Entry<K1,V1>> iterator(){
      if (m instanceof ConcurrentSkipListMap)       return ((ConcurrentSkipListMap<K1,V1>)m).entryIterator();
 else       return ((SubMap<K1,V1>)m).entryIterator();
    }
    public boolean contains(    Object o){
      if (!(o instanceof Map.Entry))       return false;
      Map.Entry<K1,V1> e=(Map.Entry<K1,V1>)o;
      V1 v=m.get(e.getKey());
      return v != null && v.equals(e.getValue());
    }
    public boolean remove(    Object o){
      if (!(o instanceof Map.Entry))       return false;
      Map.Entry<K1,V1> e=(Map.Entry<K1,V1>)o;
      return m.remove(e.getKey(),e.getValue());
    }
    public boolean isEmpty(){
      return m.isEmpty();
    }
    public int size(){
      return m.size();
    }
    public void clear(){
      m.clear();
    }
    public boolean equals(    Object o){
      if (o == this)       return true;
      if (!(o instanceof Set))       return false;
      Collection<?> c=(Collection<?>)o;
      try {
        return containsAll(c) && c.containsAll(this);
      }
 catch (      ClassCastException unused) {
        return false;
      }
catch (      NullPointerException unused) {
        return false;
      }
    }
    public Object[] toArray(){
      return toList(this).toArray();
    }
    public <T>T[] toArray(    T[] a){
      return toList(this).toArray(a);
    }
  }
  /** 
 * Submaps returned by {@link ConcurrentSkipListMap} submap operations
 * represent a subrange of mappings of their underlying
 * maps. Instances of this class support all methods of their
 * underlying maps, differing in that mappings outside their range are
 * ignored, and attempts to add mappings outside their ranges result
 * in {@link IllegalArgumentException}.  Instances of this class are
 * constructed only using the <tt>subMap</tt>, <tt>headMap</tt>, and
 * <tt>tailMap</tt> methods of their underlying maps.
 * @serial include
 */
static final class SubMap<K,V> extends AbstractMap<K,V> implements ConcurrentNavigableMap<K,V>, Cloneable, java.io.Serializable {
    private static final long serialVersionUID=-7647078645895051609L;
    /** 
 * Underlying map 
 */
    private final ConcurrentSkipListMap<K,V> m;
    /** 
 * lower bound key, or null if from start 
 */
    private final K lo;
    /** 
 * upper bound key, or null if to end 
 */
    private final K hi;
    /** 
 * inclusion flag for lo 
 */
    private final boolean loInclusive;
    /** 
 * inclusion flag for hi 
 */
    private final boolean hiInclusive;
    /** 
 * direction 
 */
    private final boolean isDescending;
    private transient KeySet<K> keySetView;
    private transient Set<Map.Entry<K,V>> entrySetView;
    private transient Collection<V> valuesView;
    /** 
 * Creates a new submap, initializing all fields
 */
    SubMap(    ConcurrentSkipListMap<K,V> map,    K fromKey,    boolean fromInclusive,    K toKey,    boolean toInclusive,    boolean isDescending){
      if (fromKey != null && toKey != null && map.compare(fromKey,toKey) > 0)       throw new IllegalArgumentException("inconsistent range");
      this.m=map;
      this.lo=fromKey;
      this.hi=toKey;
      this.loInclusive=fromInclusive;
      this.hiInclusive=toInclusive;
      this.isDescending=isDescending;
    }
    private boolean tooLow(    K key){
      if (lo != null) {
        int c=m.compare(key,lo);
        if (c < 0 || (c == 0 && !loInclusive))         return true;
      }
      return false;
    }
    private boolean tooHigh(    K key){
      if (hi != null) {
        int c=m.compare(key,hi);
        if (c > 0 || (c == 0 && !hiInclusive))         return true;
      }
      return false;
    }
    private boolean inBounds(    K key){
      return !tooLow(key) && !tooHigh(key);
    }
    private void checkKeyBounds(    K key) throws IllegalArgumentException {
      if (key == null)       throw new NullPointerException();
      if (!inBounds(key))       throw new IllegalArgumentException("key out of range");
    }
    /** 
 * Returns true if node key is less than upper bound of range
 */
    private boolean isBeforeEnd(    ConcurrentSkipListMap.Node<K,V> n){
      if (n == null)       return false;
      if (hi == null)       return true;
      K k=n.key;
      if (k == null)       return true;
      int c=m.compare(k,hi);
      if (c > 0 || (c == 0 && !hiInclusive))       return false;
      return true;
    }
    /** 
 * Returns lowest node. This node might not be in range, so
 * most usages need to check bounds
 */
    private ConcurrentSkipListMap.Node<K,V> loNode(){
      if (lo == null)       return m.findFirst();
 else       if (loInclusive)       return m.findNear(lo,m.GT | m.EQ);
 else       return m.findNear(lo,m.GT);
    }
    /** 
 * Returns highest node. This node might not be in range, so
 * most usages need to check bounds
 */
    private ConcurrentSkipListMap.Node<K,V> hiNode(){
      if (hi == null)       return m.findLast();
 else       if (hiInclusive)       return m.findNear(hi,m.LT | m.EQ);
 else       return m.findNear(hi,m.LT);
    }
    /** 
 * Returns lowest absolute key (ignoring directonality)
 */
    private K lowestKey(){
      ConcurrentSkipListMap.Node<K,V> n=loNode();
      if (isBeforeEnd(n))       return n.key;
 else       throw new NoSuchElementException();
    }
    /** 
 * Returns highest absolute key (ignoring directonality)
 */
    private K highestKey(){
      ConcurrentSkipListMap.Node<K,V> n=hiNode();
      if (n != null) {
        K last=n.key;
        if (inBounds(last))         return last;
      }
      throw new NoSuchElementException();
    }
    private Map.Entry<K,V> lowestEntry(){
      for (; ; ) {
        ConcurrentSkipListMap.Node<K,V> n=loNode();
        if (!isBeforeEnd(n))         return null;
        Map.Entry<K,V> e=n.createSnapshot();
        if (e != null)         return e;
      }
    }
    private Map.Entry<K,V> highestEntry(){
      for (; ; ) {
        ConcurrentSkipListMap.Node<K,V> n=hiNode();
        if (n == null || !inBounds(n.key))         return null;
        Map.Entry<K,V> e=n.createSnapshot();
        if (e != null)         return e;
      }
    }
    private Map.Entry<K,V> removeLowest(){
      for (; ; ) {
        Node<K,V> n=loNode();
        if (n == null)         return null;
        K k=n.key;
        if (!inBounds(k))         return null;
        V v=m.doRemove(k,null);
        if (v != null)         return new AbstractMap.SimpleImmutableEntry<K,V>(k,v);
      }
    }
    private Map.Entry<K,V> removeHighest(){
      for (; ; ) {
        Node<K,V> n=hiNode();
        if (n == null)         return null;
        K k=n.key;
        if (!inBounds(k))         return null;
        V v=m.doRemove(k,null);
        if (v != null)         return new AbstractMap.SimpleImmutableEntry<K,V>(k,v);
      }
    }
    /** 
 * Submap version of ConcurrentSkipListMap.getNearEntry
 */
    private Map.Entry<K,V> getNearEntry(    K key,    int rel){
      if (isDescending) {
        if ((rel & m.LT) == 0)         rel|=m.LT;
 else         rel&=~m.LT;
      }
      if (tooLow(key))       return ((rel & m.LT) != 0) ? null : lowestEntry();
      if (tooHigh(key))       return ((rel & m.LT) != 0) ? highestEntry() : null;
      for (; ; ) {
        Node<K,V> n=m.findNear(key,rel);
        if (n == null || !inBounds(n.key))         return null;
        K k=n.key;
        V v=n.getValidValue();
        if (v != null)         return new AbstractMap.SimpleImmutableEntry<K,V>(k,v);
      }
    }
    private K getNearKey(    K key,    int rel){
      if (isDescending) {
        if ((rel & m.LT) == 0)         rel|=m.LT;
 else         rel&=~m.LT;
      }
      if (tooLow(key)) {
        if ((rel & m.LT) == 0) {
          ConcurrentSkipListMap.Node<K,V> n=loNode();
          if (isBeforeEnd(n))           return n.key;
        }
        return null;
      }
      if (tooHigh(key)) {
        if ((rel & m.LT) != 0) {
          ConcurrentSkipListMap.Node<K,V> n=hiNode();
          if (n != null) {
            K last=n.key;
            if (inBounds(last))             return last;
          }
        }
        return null;
      }
      for (; ; ) {
        Node<K,V> n=m.findNear(key,rel);
        if (n == null || !inBounds(n.key))         return null;
        K k=n.key;
        V v=n.getValidValue();
        if (v != null)         return k;
      }
    }
    public boolean containsKey(    Object key){
      if (key == null)       throw new NullPointerException();
      K k=(K)key;
      return inBounds(k) && m.containsKey(k);
    }
    public V get(    Object key){
      if (key == null)       throw new NullPointerException();
      K k=(K)key;
      return ((!inBounds(k)) ? null : m.get(k));
    }
    public V put(    K key,    V value){
      checkKeyBounds(key);
      return m.put(key,value);
    }
    public V remove(    Object key){
      K k=(K)key;
      return (!inBounds(k)) ? null : m.remove(k);
    }
    public int size(){
      long count=0;
      for (ConcurrentSkipListMap.Node<K,V> n=loNode(); isBeforeEnd(n); n=n.next) {
        if (n.getValidValue() != null)         ++count;
      }
      return count >= Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)count;
    }
    public boolean isEmpty(){
      return !isBeforeEnd(loNode());
    }
    public boolean containsValue(    Object value){
      if (value == null)       throw new NullPointerException();
      for (ConcurrentSkipListMap.Node<K,V> n=loNode(); isBeforeEnd(n); n=n.next) {
        V v=n.getValidValue();
        if (v != null && value.equals(v))         return true;
      }
      return false;
    }
    public void clear(){
      for (ConcurrentSkipListMap.Node<K,V> n=loNode(); isBeforeEnd(n); n=n.next) {
        if (n.getValidValue() != null)         m.remove(n.key);
      }
    }
    public V putIfAbsent(    K key,    V value){
      checkKeyBounds(key);
      return m.putIfAbsent(key,value);
    }
    public boolean remove(    Object key,    Object value){
      K k=(K)key;
      return inBounds(k) && m.remove(k,value);
    }
    public boolean replace(    K key,    V oldValue,    V newValue){
      checkKeyBounds(key);
      return m.replace(key,oldValue,newValue);
    }
    public V replace(    K key,    V value){
      checkKeyBounds(key);
      return m.replace(key,value);
    }
    public Comparator<? super K> comparator(){
      Comparator<? super K> cmp=m.comparator();
      if (isDescending)       return Collections.reverseOrder(cmp);
 else       return cmp;
    }
    /** 
 * Utility to create submaps, where given bounds override
 * unbounded(null) ones and/or are checked against bounded ones.
 */
    private SubMap<K,V> newSubMap(    K fromKey,    boolean fromInclusive,    K toKey,    boolean toInclusive){
      if (isDescending) {
        K tk=fromKey;
        fromKey=toKey;
        toKey=tk;
        boolean ti=fromInclusive;
        fromInclusive=toInclusive;
        toInclusive=ti;
      }
      if (lo != null) {
        if (fromKey == null) {
          fromKey=lo;
          fromInclusive=loInclusive;
        }
 else {
          int c=m.compare(fromKey,lo);
          if (c < 0 || (c == 0 && !loInclusive && fromInclusive))           throw new IllegalArgumentException("key out of range");
        }
      }
      if (hi != null) {
        if (toKey == null) {
          toKey=hi;
          toInclusive=hiInclusive;
        }
 else {
          int c=m.compare(toKey,hi);
          if (c > 0 || (c == 0 && !hiInclusive && toInclusive))           throw new IllegalArgumentException("key out of range");
        }
      }
      return new SubMap<K,V>(m,fromKey,fromInclusive,toKey,toInclusive,isDescending);
    }
    public SubMap<K,V> subMap(    K fromKey,    boolean fromInclusive,    K toKey,    boolean toInclusive){
      if (fromKey == null || toKey == null)       throw new NullPointerException();
      return newSubMap(fromKey,fromInclusive,toKey,toInclusive);
    }
    public SubMap<K,V> headMap(    K toKey,    boolean inclusive){
      if (toKey == null)       throw new NullPointerException();
      return newSubMap(null,false,toKey,inclusive);
    }
    public SubMap<K,V> tailMap(    K fromKey,    boolean inclusive){
      if (fromKey == null)       throw new NullPointerException();
      return newSubMap(fromKey,inclusive,null,false);
    }
    public SubMap<K,V> subMap(    K fromKey,    K toKey){
      return subMap(fromKey,true,toKey,false);
    }
    public SubMap<K,V> headMap(    K toKey){
      return headMap(toKey,false);
    }
    public SubMap<K,V> tailMap(    K fromKey){
      return tailMap(fromKey,true);
    }
    public SubMap<K,V> descendingMap(){
      return new SubMap<K,V>(m,lo,loInclusive,hi,hiInclusive,!isDescending);
    }
    public Map.Entry<K,V> ceilingEntry(    K key){
      return getNearEntry(key,(m.GT | m.EQ));
    }
    public K ceilingKey(    K key){
      return getNearKey(key,(m.GT | m.EQ));
    }
    public Map.Entry<K,V> lowerEntry(    K key){
      return getNearEntry(key,(m.LT));
    }
    public K lowerKey(    K key){
      return getNearKey(key,(m.LT));
    }
    public Map.Entry<K,V> floorEntry(    K key){
      return getNearEntry(key,(m.LT | m.EQ));
    }
    public K floorKey(    K key){
      return getNearKey(key,(m.LT | m.EQ));
    }
    public Map.Entry<K,V> higherEntry(    K key){
      return getNearEntry(key,(m.GT));
    }
    public K higherKey(    K key){
      return getNearKey(key,(m.GT));
    }
    public K firstKey(){
      return isDescending ? highestKey() : lowestKey();
    }
    public K lastKey(){
      return isDescending ? lowestKey() : highestKey();
    }
    public Map.Entry<K,V> firstEntry(){
      return isDescending ? highestEntry() : lowestEntry();
    }
    public Map.Entry<K,V> lastEntry(){
      return isDescending ? lowestEntry() : highestEntry();
    }
    public Map.Entry<K,V> pollFirstEntry(){
      return isDescending ? removeHighest() : removeLowest();
    }
    public Map.Entry<K,V> pollLastEntry(){
      return isDescending ? removeLowest() : removeHighest();
    }
    public NavigableSet<K> keySet(){
      KeySet<K> ks=keySetView;
      return (ks != null) ? ks : (keySetView=new KeySet(this));
    }
    public NavigableSet<K> navigableKeySet(){
      KeySet<K> ks=keySetView;
      return (ks != null) ? ks : (keySetView=new KeySet(this));
    }
    public Collection<V> values(){
      Collection<V> vs=valuesView;
      return (vs != null) ? vs : (valuesView=new Values(this));
    }
    public Set<Map.Entry<K,V>> entrySet(){
      Set<Map.Entry<K,V>> es=entrySetView;
      return (es != null) ? es : (entrySetView=new EntrySet(this));
    }
    public NavigableSet<K> descendingKeySet(){
      return descendingMap().navigableKeySet();
    }
    Iterator<K> keyIterator(){
      return new SubMapKeyIterator();
    }
    Iterator<V> valueIterator(){
      return new SubMapValueIterator();
    }
    Iterator<Map.Entry<K,V>> entryIterator(){
      return new SubMapEntryIterator();
    }
    /** 
 * Variant of main Iter class to traverse through submaps.
 */
abstract class SubMapIter<T> implements Iterator<T> {
      /** 
 * the last node returned by next() 
 */
      Node<K,V> lastReturned;
      /** 
 * the next node to return from next(); 
 */
      Node<K,V> next;
      /** 
 * Cache of next value field to maintain weak consistency 
 */
      V nextValue;
      SubMapIter(){
        for (; ; ) {
          next=isDescending ? hiNode() : loNode();
          if (next == null)           break;
          Object x=next.value;
          if (x != null && x != next) {
            if (!inBounds(next.key))             next=null;
 else             nextValue=(V)x;
            break;
          }
        }
      }
      public final boolean hasNext(){
        return next != null;
      }
      final void advance(){
        if (next == null)         throw new NoSuchElementException();
        lastReturned=next;
        if (isDescending)         descend();
 else         ascend();
      }
      private void ascend(){
        for (; ; ) {
          next=next.next;
          if (next == null)           break;
          Object x=next.value;
          if (x != null && x != next) {
            if (tooHigh(next.key))             next=null;
 else             nextValue=(V)x;
            break;
          }
        }
      }
      private void descend(){
        for (; ; ) {
          next=m.findNear(lastReturned.key,LT);
          if (next == null)           break;
          Object x=next.value;
          if (x != null && x != next) {
            if (tooLow(next.key))             next=null;
 else             nextValue=(V)x;
            break;
          }
        }
      }
      public void remove(){
        Node<K,V> l=lastReturned;
        if (l == null)         throw new IllegalStateException();
        m.remove(l.key);
        lastReturned=null;
      }
    }
final class SubMapValueIterator extends SubMapIter<V> {
      public V next(){
        V v=nextValue;
        advance();
        return v;
      }
    }
final class SubMapKeyIterator extends SubMapIter<K> {
      public K next(){
        Node<K,V> n=next;
        advance();
        return n.key;
      }
    }
final class SubMapEntryIterator extends SubMapIter<Map.Entry<K,V>> {
      public Map.Entry<K,V> next(){
        Node<K,V> n=next;
        V v=nextValue;
        advance();
        return new AbstractMap.SimpleImmutableEntry<K,V>(n.key,v);
      }
    }
  }
  private static final sun.misc.Unsafe UNSAFE;
  private static final long headOffset;
static {
    try {
      UNSAFE=sun.misc.Unsafe.getUnsafe();
      Class k=ConcurrentSkipListMap.class;
      headOffset=UNSAFE.objectFieldOffset(k.getDeclaredField("head"));
    }
 catch (    Exception e) {
      throw new Error(e);
    }
  }
}
