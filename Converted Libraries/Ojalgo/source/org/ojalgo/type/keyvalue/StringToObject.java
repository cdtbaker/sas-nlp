package org.ojalgo.type.keyvalue;
import org.ojalgo.netio.ASCII;
public final class StringToObject<V extends Object> implements KeyValue<String,V> {
  public final String key;
  public final V value;
  public StringToObject(  final String aKey,  final V aValue){
    super();
    key=aKey;
    value=aValue;
  }
  StringToObject(){
    this(null,null);
  }
  public int compareTo(  final KeyValue<String,?> aReference){
    return key.compareTo(aReference.getKey());
  }
  @Override public boolean equals(  final Object obj){
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof StringToObject)) {
      return false;
    }
    final StringToObject<?> other=(StringToObject<?>)obj;
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
  public String getKey(){
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
