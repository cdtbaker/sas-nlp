package com.sun.beans.finder;
import com.sun.beans.WeakCache;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
/** 
 * This utility class provides {@code static} methods
 * to find a public constructor with specified parameter types
 * in specified class.
 * @since 1.7
 * @author Sergey A. Malenkov
 */
public final class ConstructorFinder extends AbstractFinder<Constructor<?>> {
  private static final WeakCache<Signature,Constructor<?>> CACHE=new WeakCache<Signature,Constructor<?>>();
  /** 
 * Finds public constructor
 * that is declared in public class.
 * @param type  the class that can have constructor
 * @param args  parameter types that is used to find constructor
 * @return object that represents found constructor
 * @throws NoSuchMethodException if constructor could not be found
 * or some constructors are found
 */
  public static Constructor<?> findConstructor(  Class<?> type,  Class<?>... args) throws NoSuchMethodException {
    if (type.isPrimitive()) {
      throw new NoSuchMethodException("Primitive wrapper does not contain constructors");
    }
    if (type.isInterface()) {
      throw new NoSuchMethodException("Interface does not contain constructors");
    }
    if (Modifier.isAbstract(type.getModifiers())) {
      throw new NoSuchMethodException("Abstract class cannot be instantiated");
    }
    if (!Modifier.isPublic(type.getModifiers())) {
      throw new NoSuchMethodException("Class is not accessible");
    }
    PrimitiveWrapperMap.replacePrimitivesWithWrappers(args);
    Signature signature=new Signature(type,args);
    Constructor<?> constructor=CACHE.get(signature);
    if (constructor != null) {
      return constructor;
    }
    constructor=new ConstructorFinder(args).find(type.getConstructors());
    CACHE.put(signature,constructor);
    return constructor;
  }
  /** 
 * Creates constructor finder with specified array of parameter types.
 * @param args  the array of parameter types
 */
  private ConstructorFinder(  Class<?>[] args){
    super(args);
  }
  /** 
 * Returns an array of {@code Class} objects
 * that represent the formal parameter types of the constructor
 * Returns an empty array if the constructor takes no parameters.
 * @param constructor  the object that represents constructor
 * @return the parameter types of the constructor
 */
  @Override protected Class<?>[] getParameters(  Constructor<?> constructor){
    return constructor.getParameterTypes();
  }
  /** 
 * Returns {@code true} if and only if the constructor
 * was declared to take a variable number of arguments.
 * @param constructor  the object that represents constructor
 * @return {@code true} if the constructor was declared
 * to take a variable number of arguments;{@code false} otherwise
 */
  @Override protected boolean isVarArgs(  Constructor<?> constructor){
    return constructor.isVarArgs();
  }
  /** 
 * Checks validness of the constructor.
 * The valid constructor should be public.
 * @param constructor  the object that represents constructor
 * @return {@code true} if the constructor is valid,{@code false} otherwise
 */
  @Override protected boolean isValid(  Constructor<?> constructor){
    return Modifier.isPublic(constructor.getModifiers());
  }
}
