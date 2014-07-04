package org.ojalgo.type.keyvalue;
import org.ojalgo.netio.ASCII;
public final class IntToObject<V extends Object> implements KeyValue<Integer,V> {
  public final int key;
  public final V value;
  public IntToObject(  final int aKey,  final V aValue){
    super();
    key=aKey;
    value=aValue;
  }
  public IntToObject(  final Integer aKey,  final V aValue){
    super();
    key=aKey != null ? aKey : 0;
    value=aValue;
  }
  IntToObject(){
    this(0,null);
  }
  public int compareTo(  final IntToObject<V> aReference){
    return (key < aReference.key ? -1 : (key == aReference.key ? 0 : 1));
  }
  public int compareTo(  final KeyValue<Integer,?> aReference){
    return (key < aReference.getKey() ? -1 : (key == aReference.getKey() ? 0 : 1));
  }
  @Override public boolean equals(  final Object obj){
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof IntToObject)) {
      return false;
    }
    final IntToObject<?> other=(IntToObject<?>)obj;
    if (key != other.key) {
      return false;
    }
    return true;
  }
  public Integer getKey(){
    return key;
  }
  public V getValue(){
    return value;
  }
  @Override public int hashCode(){
    final int prime=31;
    int result=1;
    result=prime * result + key;
    return result;
  }
  @Override public String toString(){
    return String.valueOf(key) + String.valueOf(ASCII.EQUALS) + String.valueOf(value);
  }
}
