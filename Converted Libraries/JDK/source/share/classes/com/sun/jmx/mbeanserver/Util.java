package com.sun.jmx.mbeanserver;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
public class Util {
  public static ObjectName newObjectName(  String string){
    try {
      return new ObjectName(string);
    }
 catch (    MalformedObjectNameException e) {
      throw new IllegalArgumentException(e);
    }
  }
  static <K,V>Map<K,V> newMap(){
    return new HashMap<K,V>();
  }
  static <K,V>Map<K,V> newSynchronizedMap(){
    return Collections.synchronizedMap(Util.<K,V>newMap());
  }
  static <K,V>IdentityHashMap<K,V> newIdentityHashMap(){
    return new IdentityHashMap<K,V>();
  }
  static <K,V>Map<K,V> newSynchronizedIdentityHashMap(){
    Map<K,V> map=newIdentityHashMap();
    return Collections.synchronizedMap(map);
  }
  static <K,V>SortedMap<K,V> newSortedMap(){
    return new TreeMap<K,V>();
  }
  static <K,V>SortedMap<K,V> newSortedMap(  Comparator<? super K> comp){
    return new TreeMap<K,V>(comp);
  }
  static <K,V>Map<K,V> newInsertionOrderMap(){
    return new LinkedHashMap<K,V>();
  }
  static <E>Set<E> newSet(){
    return new HashSet<E>();
  }
  static <E>Set<E> newSet(  Collection<E> c){
    return new HashSet<E>(c);
  }
  static <E>List<E> newList(){
    return new ArrayList<E>();
  }
  static <E>List<E> newList(  Collection<E> c){
    return new ArrayList<E>(c);
  }
  @SuppressWarnings("unchecked") public static <T>T cast(  Object x){
    return (T)x;
  }
  /** 
 * Computes a descriptor hashcode from its names and values.
 * @param names  the sorted array of descriptor names.
 * @param values the array of descriptor values.
 * @return a hash code value, as described in {@link #hashCode(Descriptor)}
 */
  public static int hashCode(  String[] names,  Object[] values){
    int hash=0;
    for (int i=0; i < names.length; i++) {
      Object v=values[i];
      int h;
      if (v == null) {
        h=0;
      }
 else       if (v instanceof Object[]) {
        h=Arrays.deepHashCode((Object[])v);
      }
 else       if (v.getClass().isArray()) {
        h=Arrays.deepHashCode(new Object[]{v}) - 31;
      }
 else {
        h=v.hashCode();
      }
      hash+=names[i].toLowerCase().hashCode() ^ h;
    }
    return hash;
  }
  /** 
 * Match a part of a string against a shell-style pattern.
 * The only pattern characters recognized are <code>?</code>,
 * standing for any one character,
 * and <code>*</code>, standing for any string of
 * characters, including the empty string. For instance,{@code wildmatch("sandwich","sa?d*ch",1,4,1,4)} will match{@code "and"} against {@code "a?d"}.
 * @param str  the string containing the sequence to match.
 * @param pat  a string containing a pattern to match the sub string
 * against.
 * @param stri   the index in the string at which matching should begin.
 * @param strend the index in the string at which the matching should
 * end.
 * @param pati   the index in the pattern at which matching should begin.
 * @param patend the index in the pattern at which the matching should
 * end.
 * @return true if and only if the string matches the pattern.
 */
  private static boolean wildmatch(  final String str,  final String pat,  int stri,  final int strend,  int pati,  final int patend){
    int starstri;
    int starpati;
    starstri=starpati=-1;
    while (true) {
      if (pati < patend) {
        final char patc=pat.charAt(pati);
switch (patc) {
case '?':
          if (stri == strend)           break;
        stri++;
      pati++;
    continue;
case '*':
  pati++;
starpati=pati;
starstri=stri;
continue;
default :
if (stri < strend && str.charAt(stri) == patc) {
stri++;
pati++;
continue;
}
break;
}
}
 else if (stri == strend) return true;
if (starpati < 0 || starstri == strend) return false;
pati=starpati;
starstri++;
stri=starstri;
}
}
/** 
 * Match a string against a shell-style pattern.  The only pattern
 * characters recognized are <code>?</code>, standing for any one
 * character, and <code>*</code>, standing for any string of
 * characters, including the empty string.
 * @param str the string to match.
 * @param pat the pattern to match the string against.
 * @return true if and only if the string matches the pattern.
 */
public static boolean wildmatch(String str,String pat){
return wildmatch(str,pat,0,str.length(),0,pat.length());
}
}
