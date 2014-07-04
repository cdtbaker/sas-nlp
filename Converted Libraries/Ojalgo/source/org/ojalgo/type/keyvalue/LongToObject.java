package org.ojalgo.type.keyvalue;
import org.ojalgo.netio.ASCII;
public final class LongToObject<V extends Object> implements KeyValue<Long,V> {
  public final long key;
  public final V value;
  public LongToObject(  final long aKey,  final V aValue){
    super();
    key=aKey;
    value=aValue;
  }
  public LongToObject(  final Long aKey,  final V aValue){
    super();
    key=aKey != null ? aKey : 0l;
    value=aValue;
  }
  LongToObject(){
    this(0l,null);
  }
  public int compareTo(  final KeyValue<Long,?> aReference){
    return (key < aReference.getKey() ? -1 : (key == aReference.getKey() ? 0 : 1));
  }
  public int compareTo(  final LongToObject<V> aReference){
    return (key < aReference.key ? -1 : (key == aReference.key ? 0 : 1));
  }
  @Override public boolean equals(  final Object obj){
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof LongToObject)) {
      return false;
    }
    final LongToObject<?> other=(LongToObject<?>)obj;
    if (key != other.key) {
      return false;
    }
    return true;
  }
  public Long getKey(){
    return key;
  }
  public V getValue(){
    return value;
  }
  @Override public int hashCode(){
    final int prime=31;
    int result=1;
    result=prime * result + (int)(key ^ (key >>> 32));
    return result;
  }
  @Override public String toString(){
    return String.valueOf(key) + String.valueOf(ASCII.EQUALS) + String.valueOf(value);
  }
}
