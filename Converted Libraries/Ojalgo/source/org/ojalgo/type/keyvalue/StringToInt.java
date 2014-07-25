package org.ojalgo.type.keyvalue;
import org.ojalgo.netio.ASCII;
public final class StringToInt implements KeyValue<String,Integer> {
  public final String key;
  public final int value;
  public StringToInt(  final String aKey,  final int aValue){
    super();
    key=aKey;
    value=aValue;
  }
  StringToInt(){
    this(null,0);
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
    if (!(obj instanceof StringToInt)) {
      return false;
    }
    final StringToInt other=(StringToInt)obj;
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
  public Integer getValue(){
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
