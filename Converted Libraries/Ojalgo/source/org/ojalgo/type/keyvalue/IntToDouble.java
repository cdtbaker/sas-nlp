package org.ojalgo.type.keyvalue;
import static org.ojalgo.constant.PrimitiveMath.*;
import org.ojalgo.netio.ASCII;
public final class IntToDouble implements KeyValue<Integer,Double> {
  public final int key;
  public final double value;
  public IntToDouble(  final int aKey,  final double aValue){
    super();
    key=aKey;
    value=aValue;
  }
  public IntToDouble(  final int aKey,  final Double aValue){
    super();
    key=aKey;
    value=aValue != null ? aValue : ZERO;
  }
  public IntToDouble(  final Integer aKey,  final double aValue){
    super();
    key=aKey != null ? aKey : 0;
    value=aValue;
  }
  public IntToDouble(  final Integer aKey,  final Double aValue){
    super();
    key=aKey != null ? aKey : 0;
    value=aValue != null ? aValue : ZERO;
  }
  IntToDouble(){
    this(0,ZERO);
  }
  public int compareTo(  final IntToDouble aReference){
    return (key < aReference.key ? -1 : (key == aReference.key ? 0 : 1));
  }
  public int compareTo(  final KeyValue<Integer,?> aReference){
    return Double.compare(key,aReference.getKey());
  }
  @Override public boolean equals(  final Object obj){
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof IntToDouble)) {
      return false;
    }
    final IntToDouble other=(IntToDouble)obj;
    if (key != other.key) {
      return false;
    }
    return true;
  }
  public Integer getKey(){
    return key;
  }
  public Double getValue(){
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
