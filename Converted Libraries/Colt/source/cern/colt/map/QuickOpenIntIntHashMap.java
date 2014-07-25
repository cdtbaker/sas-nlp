package cern.colt.map;
/** 
 * Status: Experimental; Do not use for production yet. Hash map holding (key,value) associations of type <tt>(int-->int)</tt>; Automatically grows and shrinks as needed; Implemented using open addressing with double hashing.
 * First see the <a href="package-summary.html">package summary</a> and javadoc <a href="package-tree.html">tree view</a> to get the broad picture.
 * Implements open addressing with double hashing, using "Brent's variation".
 * Brent's variation slows insertions a bit down (not much) but reduces probes (collisions) for successful searches, in particular for large load factors.
 * (It does not improve unsuccessful searches.)
 * See D. Knuth, Searching and Sorting, 3rd ed., p.533-545
 * @author wolfgang.hoschek@cern.ch
 * @version 1.0, 09/24/99
 * @see java.util.HashMap
 */
class QuickOpenIntIntHashMap extends OpenIntIntHashMap {
  public int totalProbesSaved=0;
  /** 
 * Constructs an empty map with default capacity and default load factors.
 */
  public QuickOpenIntIntHashMap(){
    this(defaultCapacity);
  }
  /** 
 * Constructs an empty map with the specified initial capacity and default load factors.
 * @param initialCapacity   the initial capacity of the map.
 * @throws IllegalArgumentException if the initial capacity is less
 * than zero.
 */
  public QuickOpenIntIntHashMap(  int initialCapacity){
    this(initialCapacity,defaultMinLoadFactor,defaultMaxLoadFactor);
  }
  /** 
 * Constructs an empty map with
 * the specified initial capacity and the specified minimum and maximum load factor.
 * @param initialCapacity   the initial capacity.
 * @param minLoadFactor        the minimum load factor.
 * @param maxLoadFactor        the maximum load factor.
 * @throws IllegalArgumentException if <tt>initialCapacity < 0 || (minLoadFactor < 0.0 || minLoadFactor >= 1.0) || (maxLoadFactor <= 0.0 || maxLoadFactor >= 1.0) || (minLoadFactor >= maxLoadFactor)</tt>.
 */
  public QuickOpenIntIntHashMap(  int initialCapacity,  double minLoadFactor,  double maxLoadFactor){
    setUp(initialCapacity,minLoadFactor,maxLoadFactor);
  }
  /** 
 * Associates the given key with the given value.
 * Replaces any old <tt>(key,someOtherValue)</tt> association, if existing.
 * @param key the key the value shall be associated with.
 * @param value the value to be associated.
 * @return <tt>true</tt> if the receiver did not already contain such a key;
 * <tt>false</tt> if the receiver did already contain such a key - the new value has now replaced the formerly associated value.
 */
  public boolean put(  int key,  int value){
    int key0;
    final int tab[]=table;
    final byte stat[]=state;
    final int length=tab.length;
    int hash=HashFunctions.hash(key) & 0x7FFFFFFF;
    int i=hash % length;
    int decrement=(hash / length) % length;
    if (decrement == 0)     decrement=1;
    int t=0;
    int p0=i;
    while (stat[i] == FULL && tab[i] != key) {
      t++;
      i-=decrement;
      if (i < 0)       i+=length;
    }
    if (stat[i] == FULL) {
      this.values[i]=value;
      return false;
    }
    if (this.distinct > this.highWaterMark) {
      int newCapacity=chooseGrowCapacity(this.distinct + 1,this.minLoadFactor,this.maxLoadFactor);
      rehash(newCapacity);
      return put(key,value);
    }
    while (t > 1) {
      key0=tab[p0];
      hash=HashFunctions.hash(key0) & 0x7FFFFFFF;
      decrement=(hash / length) % length;
      if (decrement == 0)       decrement=1;
      int pc=p0 - decrement;
      if (pc < 0)       pc+=length;
      if (stat[pc] != FREE) {
        p0=pc;
        t--;
      }
 else {
        this.totalProbesSaved+=(t - 1);
        tab[pc]=key0;
        stat[pc]=FULL;
        values[pc]=values[p0];
        i=p0;
        t=0;
      }
    }
    this.table[i]=key;
    this.values[i]=value;
    if (this.state[i] == FREE)     this.freeEntries--;
    this.state[i]=FULL;
    this.distinct++;
    if (this.freeEntries < 1) {
      int newCapacity=chooseGrowCapacity(this.distinct + 1,this.minLoadFactor,this.maxLoadFactor);
      rehash(newCapacity);
    }
    return true;
  }
  /** 
 * Rehashes the contents of the receiver into a new table
 * with a smaller or larger capacity.
 * This method is called automatically when the
 * number of keys in the receiver exceeds the high water mark or falls below the low water mark.
 */
  protected void rehash(  int newCapacity){
    int oldCapacity=table.length;
    int oldTable[]=table;
    int oldValues[]=values;
    byte oldState[]=state;
    int newTable[]=new int[newCapacity];
    int newValues[]=new int[newCapacity];
    byte newState[]=new byte[newCapacity];
    this.lowWaterMark=chooseLowWaterMark(newCapacity,this.minLoadFactor);
    this.highWaterMark=chooseHighWaterMark(newCapacity,this.maxLoadFactor);
    this.table=newTable;
    this.values=newValues;
    this.state=newState;
    this.freeEntries=newCapacity - this.distinct;
    int tmp=this.distinct;
    this.distinct=Integer.MIN_VALUE;
    for (int i=oldCapacity; i-- > 0; ) {
      if (oldState[i] == FULL) {
        put(oldTable[i],oldValues[i]);
      }
    }
    this.distinct=tmp;
  }
}
