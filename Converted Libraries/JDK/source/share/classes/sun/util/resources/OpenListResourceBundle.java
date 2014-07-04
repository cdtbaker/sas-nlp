package sun.util.resources;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import sun.util.ResourceBundleEnumeration;
/** 
 * Subclass of <code>ResourceBundle</code> which mimics
 * <code>ListResourceBundle</code>, but provides more hooks
 * for specialized subclass behavior. For general description,
 * see {@link java.util.ListResourceBundle}.
 * <p>
 * This class leaves handleGetObject non-final, and
 * adds a method createMap which allows subclasses to
 * use specialized Map implementations.
 */
public abstract class OpenListResourceBundle extends ResourceBundle {
  /** 
 * Sole constructor.  (For invocation by subclass constructors, typically
 * implicit.)
 */
  protected OpenListResourceBundle(){
  }
  public Object handleGetObject(  String key){
    if (key == null) {
      throw new NullPointerException();
    }
    loadLookupTablesIfNecessary();
    return lookup.get(key);
  }
  /** 
 * Implementation of ResourceBundle.getKeys.
 */
  public Enumeration<String> getKeys(){
    ResourceBundle parent=this.parent;
    return new ResourceBundleEnumeration(handleGetKeys(),(parent != null) ? parent.getKeys() : null);
  }
  /** 
 * Returns a set of keys provided in this resource bundle
 */
  public Set<String> handleGetKeys(){
    loadLookupTablesIfNecessary();
    return lookup.keySet();
  }
  /** 
 * Returns the parent bundle
 */
  public OpenListResourceBundle getParent(){
    return (OpenListResourceBundle)parent;
  }
  /** 
 * See ListResourceBundle class description.
 */
  abstract protected Object[][] getContents();
  /** 
 * Load lookup tables if they haven't been loaded already.
 */
  void loadLookupTablesIfNecessary(){
    if (lookup == null) {
      loadLookup();
    }
  }
  /** 
 * We lazily load the lookup hashtable.  This function does the
 * loading.
 */
  private synchronized void loadLookup(){
    if (lookup != null)     return;
    Object[][] contents=getContents();
    Map temp=createMap(contents.length);
    for (int i=0; i < contents.length; ++i) {
      String key=(String)contents[i][0];
      Object value=contents[i][1];
      if (key == null || value == null) {
        throw new NullPointerException();
      }
      temp.put(key,value);
    }
    lookup=temp;
  }
  /** 
 * Lets subclasses provide specialized Map implementations.
 * Default uses HashMap.
 */
  protected Map createMap(  int size){
    return new HashMap(size);
  }
  private Map lookup=null;
}
