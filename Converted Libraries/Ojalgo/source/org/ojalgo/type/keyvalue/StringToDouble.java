package org.ojalgo.type.keyvalue;
import org.ojalgo.netio.ASCII;
public final class StringToDouble implements KeyValue<String,Double> {
  public final String key;
  public final double value;
  public StringToDouble(  final String aKey,  final double aValue){
    super();
    key=aKey;
    value=aValue;
  }
  StringToDouble(){
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
    if (!(obj instanceof StringToDouble)) {
      return false;
    }
    final StringToDouble other=(StringToDouble)obj;
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
