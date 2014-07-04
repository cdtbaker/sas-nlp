package sun.reflect.generics.scope;
import java.lang.reflect.Constructor;
/** 
 * This class represents the scope containing the type variables of
 * a constructor.
 */
public class ConstructorScope extends AbstractScope<Constructor> {
  private ConstructorScope(  Constructor c){
    super(c);
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
 * Factory method. Takes a <tt>Constructor</tt> object and creates a
 * scope for it.
 * @param m - A Constructor whose scope we want to obtain
 * @return The type-variable scope for the constructor m
 */
  public static ConstructorScope make(  Constructor c){
    return new ConstructorScope(c);
  }
}
