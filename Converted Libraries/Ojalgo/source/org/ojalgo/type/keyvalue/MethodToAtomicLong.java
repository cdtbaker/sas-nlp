package org.ojalgo.type.keyvalue;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicLong;
import org.ojalgo.netio.ASCII;
public final class MethodToAtomicLong implements KeyValue<Method,AtomicLong> {
  public final Method key;
  public final AtomicLong value;
  public MethodToAtomicLong(  final Method aKey){
    this(aKey,new AtomicLong());
  }
  public MethodToAtomicLong(  final Method aKey,  final AtomicLong aValue){
    super();
    key=aKey;
    value=aValue;
  }
  MethodToAtomicLong(){
    this(null,null);
  }
  public int compareTo(  final KeyValue<Method,?> aReference){
    return key.toGenericString().compareTo(aReference.getKey().toGenericString());
  }
  @Override public boolean equals(  final Object obj){
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof MethodToAtomicLong)) {
      return false;
    }
    final MethodToAtomicLong other=(MethodToAtomicLong)obj;
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
  public Method getKey(){
    return key;
  }
  public AtomicLong getValue(){
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
