package org.ojalgo.type.keyvalue;
import static org.ojalgo.constant.PrimitiveMath.*;
import org.ojalgo.constant.PrimitiveMath;
import org.ojalgo.netio.ASCII;
public final class LongToDouble implements KeyValue<Long,Double> {
  public final long key;
  public final double value;
  public LongToDouble(  final long aKey){
    super();
    key=aKey;
    value=PrimitiveMath.NaN;
  }
  public LongToDouble(  final long aKey,  final double aValue){
    super();
    key=aKey;
    value=aValue;
  }
  public LongToDouble(  final long aKey,  final Double aValue){
    super();
    key=aKey;
    value=aValue != null ? aValue : ZERO;
  }
  public LongToDouble(  final Long aKey,  final double aValue){
    super();
    key=aKey != null ? aKey : 0l;
    value=aValue;
  }
  public LongToDouble(  final Long aKey,  final Double aValue){
    super();
    key=aKey != null ? aKey : 0l;
    value=aValue != null ? aValue : ZERO;
  }
  LongToDouble(){
    this(0l,ZERO);
  }
  public int compareTo(  final KeyValue<Long,?> aReference){
    return (key < aReference.getKey() ? -1 : (key == aReference.getKey() ? 0 : 1));
  }
  public int compareTo(  final LongToDouble aReference){
    return (key < aReference.key ? -1 : (key == aReference.key ? 0 : 1));
  }
  @Override public boolean equals(  final Object obj){
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof LongToDouble)) {
      return false;
    }
    final LongToDouble other=(LongToDouble)obj;
    if (key != other.key) {
      return false;
    }
    return true;
  }
  public Long getKey(){
    return key;
  }
  public Double getValue(){
    return value;
  }
  @Override public int hashCode(){
    final int prime=31;
    int result=1;
    result=(prime * result) + (int)(key ^ (key >>> 32));
    return result;
  }
  @Override public String toString(){
    return String.valueOf(key) + String.valueOf(ASCII.EQUALS) + String.valueOf(value);
  }
}
