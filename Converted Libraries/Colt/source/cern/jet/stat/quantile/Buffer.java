package cern.jet.stat.quantile;
/** 
 * A buffer holding elements; internally used for computing approximate quantiles.
 */
abstract class Buffer extends cern.colt.PersistentObject {
  protected int weight;
  protected int level;
  protected int k;
  protected boolean isAllocated;
  /** 
 * This method was created in VisualAge.
 * @param k int
 */
  public Buffer(  int k){
    this.k=k;
    this.weight=1;
    this.level=0;
    this.isAllocated=false;
  }
  /** 
 * Clears the receiver.
 */
  public abstract void clear();
  /** 
 * Returns whether the receiver is already allocated.
 */
  public boolean isAllocated(){
    return isAllocated;
  }
  /** 
 * Returns whether the receiver is empty.
 */
  public abstract boolean isEmpty();
  /** 
 * Returns whether the receiver is empty.
 */
  public abstract boolean isFull();
  /** 
 * Returns whether the receiver is partial.
 */
  public boolean isPartial(){
    return !(isEmpty() || isFull());
  }
  /** 
 * Returns whether the receiver's level.
 */
  public int level(){
    return level;
  }
  /** 
 * Sets the receiver's level.
 */
  public void level(  int level){
    this.level=level;
  }
  /** 
 * Returns the number of elements contained in the receiver.
 */
  public abstract int size();
  /** 
 * Sorts the receiver.
 */
  public abstract void sort();
  /** 
 * Returns whether the receiver's weight.
 */
  public int weight(){
    return weight;
  }
  /** 
 * Sets the receiver's weight.
 */
  public void weight(  int weight){
    this.weight=weight;
  }
}
