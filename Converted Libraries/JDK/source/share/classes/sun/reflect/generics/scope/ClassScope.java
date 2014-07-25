package sun.reflect.generics.scope;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
/** 
 * This class represents the scope containing the type variables of
 * a class.
 */
public class ClassScope extends AbstractScope<Class<?>> implements Scope {
  private ClassScope(  Class<?> c){
    super(c);
  }
  /** 
 * Overrides the abstract method in the superclass.
 * @return the enclosing scope
 */
  protected Scope computeEnclosingScope(){
    Class<?> receiver=getRecvr();
    Method m=receiver.getEnclosingMethod();
    if (m != null)     return MethodScope.make(m);
    Constructor<?> cnstr=receiver.getEnclosingConstructor();
    if (cnstr != null)     return ConstructorScope.make(cnstr);
    Class<?> c=receiver.getEnclosingClass();
    if (c != null)     return ClassScope.make(c);
    return DummyScope.make();
  }
  /** 
 * Factory method. Takes a <tt>Class</tt> object and creates a
 * scope for it.
 * @param c - a Class whose scope we want to obtain
 * @return The type-variable scope for the class c
 */
  public static ClassScope make(  Class<?> c){
    return new ClassScope(c);
  }
}
