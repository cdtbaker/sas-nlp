package sun.reflect;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
/** 
 * NOTE: obsolete as of JDK 1.4 B75 and should be removed from the
 * workspace (FIXME) 
 */
public class FieldInfo {
  private String name;
  private String signature;
  private int modifiers;
  private int slot;
  FieldInfo(){
  }
  public String name(){
    return name;
  }
  /** 
 * This is in "external" format, i.e. having '.' as separator
 * rather than '/' 
 */
  public String signature(){
    return signature;
  }
  public int modifiers(){
    return modifiers;
  }
  public int slot(){
    return slot;
  }
  /** 
 * Convenience routine 
 */
  public boolean isPublic(){
    return (Modifier.isPublic(modifiers()));
  }
}
