package org.ojalgo.type.keyvalue;
import org.ojalgo.netio.ASCII;
public final class ComparableToObject<K extends Comparable<K>,V extends Object> implements KeyValue<K,V> {
  public final K key;
  public final V value;
  public ComparableToObject(  final K aKey,  final V avalue){
    super();
    key=aKey;
    value=avalue;
  }
  @SuppressWarnings("unused") private ComparableToObject(){
    this(null,null);
  }
  public int compareTo(  final KeyValue<K,?> aReference){
    return key.compareTo(aReference.getKey());
  }
  @Override public boolean equals(  final Object obj){
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof ComparableToObject)) {
      return false;
    }
    final ComparableToObject<?,?> other=(ComparableToObject<?,?>)obj;
    if (key == null) {
      if (other.key != null) {
        return false;
      }
    }
 else     if (!key.equals(other.key)) {
      return false;
    }
    return true;
  }
  public K getKey(){
    return key;
  }
  public V getValue(){
    return value;
  }
  @Override public int hashCode(){
    final int prime=31;
    int result=1;
    result=prime * result + ((key == null) ? 0 : key.hashCode());
    return result;
  }
  @Override public String toString(){
    return String.valueOf(key) + String.valueOf(ASCII.EQUALS) + String.valueOf(value);
  }
}
