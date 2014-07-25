package sun.reflect.generics.scope;
import java.lang.reflect.Method;
/** 
 * This class represents the scope containing the type variables of
 * a method.
 */
public class MethodScope extends AbstractScope<Method> {
  private MethodScope(  Method m){
    super(m);
  }
  private Class<?> getEnclosingClass(){
    return getRecvr().getDeclaringClass();
  }
  /** 
 * Overrides the abstract method in the superclass.
 * @return the enclosing scope
 */
  protected Scope computeEnclosingScope(){
    return ClassScope.make(getEnclosingClass());
  }
  /** 
 * Factory method. Takes a <tt>Method</tt> object and creates a
 * scope for it.
 * @param m - A Method whose scope we want to obtain
 * @return The type-variable scope for the method m
 */
  public static MethodScope make(  Method m){
    return new MethodScope(m);
  }
}
