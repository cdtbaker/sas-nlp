package com.sun.jmx.remote.util;
import java.lang.ref.SoftReference;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.WeakHashMap;
import com.sun.jmx.mbeanserver.Util;
/** 
 * <p>Like WeakHashMap, except that the keys of the <em>n</em> most
 * recently-accessed entries are kept as {@link SoftReference soft
 * references}.  Accessing an element means creating it, or retrieving
 * it with {@link #get(Object) get}.  Because these entries are kept
 * with soft references, they will tend to remain even if their keys
 * are not referenced elsewhere.  But if memory is short, they will
 * be removed.</p>
 */
public class CacheMap<K,V> extends WeakHashMap<K,V> {
  /** 
 * <p>Create a <code>CacheMap</code> that can keep up to
 * <code>nSoftReferences</code> as soft references.</p>
 * @param nSoftReferences Maximum number of keys to keep as soft
 * references.  Access times for {@link #get(Object) get} and{@link #put(Object,Object) put} have a component that scales
 * linearly with <code>nSoftReferences</code>, so this value
 * should not be too great.
 * @throws IllegalArgumentException if
 * <code>nSoftReferences</code> is negative.
 */
  public CacheMap(  int nSoftReferences){
    if (nSoftReferences < 0) {
      throw new IllegalArgumentException("nSoftReferences = " + nSoftReferences);
    }
    this.nSoftReferences=nSoftReferences;
  }
  public V put(  K key,  V value){
    cache(key);
    return super.put(key,value);
  }
  public V get(  Object key){
    cache(Util.<K>cast(key));
    return super.get(key);
  }
  private void cache(  K key){
    Iterator<SoftReference<K>> it=cache.iterator();
    while (it.hasNext()) {
      SoftReference<K> sref=it.next();
      K key1=sref.get();
      if (key1 == null)       it.remove();
 else       if (key.equals(key1)) {
        it.remove();
        cache.add(0,sref);
        return;
      }
    }
    int size=cache.size();
    if (size == nSoftReferences) {
      if (size == 0)       return;
      it.remove();
    }
    cache.add(0,new SoftReference<K>(key));
  }
  private final LinkedList<SoftReference<K>> cache=new LinkedList<SoftReference<K>>();
  private final int nSoftReferences;
}
