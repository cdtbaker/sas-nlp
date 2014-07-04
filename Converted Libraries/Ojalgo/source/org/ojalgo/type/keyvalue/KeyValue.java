package org.ojalgo.type.keyvalue;
/** 
 * <p>
 * A key-value pair or key-to-value map. The intention is that{@linkplain #equals(Object)}, {@linkplain #hashCode()} and{@linkplain #compareTo(Object)} operates on the key part only.
 * </p>
 * <p>
 * This is NOT compatible with how for instance {@linkplain java.util.Map.Entry}implements those methods.
 * </p>
 * <p>
 * Further it is indented that implementations should be immutable.
 * </p>
 * @author apete
 */
public interface KeyValue<K extends Object,V extends Object> extends Comparable<KeyValue<K,?>> {
  K getKey();
  V getValue();
}
