package org.ojalgo.type.keyvalue;
import org.ojalgo.netio.ASCII;
public final class ComparableToDouble<K extends Comparable<K>> implements KeyValue<K,Double> {
  public final K key;
  public final double value;
  public ComparableToDouble(  final K aKey,  final double avalue){
    super();
    key=aKey;
    value=avalue;
  }
  @SuppressWarnings("unused") private ComparableToDouble(){
    this(null,0.0);
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
    if (!(obj instanceof ComparableToDouble)) {
      return false;
    }
    final ComparableToDouble<?> other=(ComparableToDouble<?>)obj;
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
  public Double getValue(){
    return value;
  }
  @Override public int hashCode(){
    final int prime=31;
    int result=1;
    result=(prime * result) + ((key == null) ? 0 : key.hashCode());
    return result;
  }
  @Override public String toString(){
    return String.valueOf(key) + String.valueOf(ASCII.EQUALS) + String.valueOf(value);
  }
}
