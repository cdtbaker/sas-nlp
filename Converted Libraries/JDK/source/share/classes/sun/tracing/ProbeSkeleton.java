package sun.tracing;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import com.sun.tracing.Probe;
/** 
 * Provides common code for implementation of {@code Probe} classes.
 * @since 1.7
 */
public abstract class ProbeSkeleton implements Probe {
  protected Class<?>[] parameters;
  protected ProbeSkeleton(  Class<?>[] parameters){
    this.parameters=parameters;
  }
  public abstract boolean isEnabled();
  /** 
 * Triggers the probe with verified arguments.
 * The caller of this method must have already determined that the
 * arity and types of the arguments match what the probe was
 * declared with.
 */
  public abstract void uncheckedTrigger(  Object[] args);
  private static boolean isAssignable(  Object o,  Class<?> formal){
    if (o != null) {
      if (!formal.isInstance(o)) {
        if (formal.isPrimitive()) {
          try {
            Field f=o.getClass().getField("TYPE");
            return formal.isAssignableFrom((Class<?>)f.get(null));
          }
 catch (          Exception e) {
          }
        }
        return false;
      }
    }
    return true;
  }
  /** 
 * Performs a type-check of the parameters before triggering the probe.
 */
  public void trigger(  Object... args){
    if (args.length != parameters.length) {
      throw new IllegalArgumentException("Wrong number of arguments");
    }
 else {
      for (int i=0; i < parameters.length; ++i) {
        if (!isAssignable(args[i],parameters[i])) {
          throw new IllegalArgumentException("Wrong type of argument at position " + i);
        }
      }
      uncheckedTrigger(args);
    }
  }
}
