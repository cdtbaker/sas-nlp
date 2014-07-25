package org.ojalgo.type;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
public final class KeyCounter<K> {
  protected static final int INT_ZERO=0;
  private final HashMap<K,AtomicInteger> myDelegate=new HashMap<K,AtomicInteger>();
  public KeyCounter(){
    super();
  }
  public int decrement(  final K aKey){
    return this.getValue(aKey).decrementAndGet();
  }
  @Override public boolean equals(  final Object obj){
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof KeyCounter)) {
      return false;
    }
    final KeyCounter<?> other=(KeyCounter<?>)obj;
    if (myDelegate == null) {
      if (other.myDelegate != null) {
        return false;
      }
    }
 else     if (!myDelegate.equals(other.myDelegate)) {
      return false;
    }
    return true;
  }
  public int get(  final K aKey){
    return this.getValue(aKey).get();
  }
  @Override public int hashCode(){
    final int prime=31;
    int result=1;
    result=(prime * result) + ((myDelegate == null) ? 0 : myDelegate.hashCode());
    return result;
  }
  public int increment(  final K aKey){
    return this.getValue(aKey).incrementAndGet();
  }
  public int reset(  final K aKey){
    this.getValue(aKey).set(INT_ZERO);
    return INT_ZERO;
  }
  public int set(  final K aKey,  final int aValue){
    this.getValue(aKey).set(aValue);
    return aValue;
  }
  @Override public String toString(){
    return myDelegate.toString();
  }
  private synchronized AtomicInteger getValue(  final K aKey){
    AtomicInteger retVal=myDelegate.get(aKey);
    if (retVal == null) {
      retVal=new AtomicInteger();
      myDelegate.put(aKey,retVal);
    }
    return retVal;
  }
}
