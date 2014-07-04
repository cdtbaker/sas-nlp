package java.lang.invoke;
import static java.lang.invoke.MethodHandleStatics.*;
import static java.lang.invoke.MethodHandles.Lookup.IMPL_LOOKUP;
/** 
 * Unary function composition, useful for many small plumbing jobs.
 * The invoke method takes a single reference argument, and returns a reference
 * Internally, it first calls the {@code filter} method on the argument,
 * Making up the difference between the raw method type and the
 * final method type is the responsibility of a JVM-level adapter.
 * @author jrose
 */
class FilterOneArgument extends BoundMethodHandle {
  protected final MethodHandle filter;
  protected final MethodHandle target;
  @Override String debugString(){
    return target.toString();
  }
  protected Object invoke(  Object argument) throws Throwable {
    Object filteredArgument=filter.invokeExact(argument);
    return target.invokeExact(filteredArgument);
  }
  private static final MethodHandle INVOKE;
static {
    try {
      INVOKE=IMPL_LOOKUP.findVirtual(FilterOneArgument.class,"invoke",MethodType.genericMethodType(1));
    }
 catch (    ReflectiveOperationException ex) {
      throw uncaughtException(ex);
    }
  }
  protected FilterOneArgument(  MethodHandle filter,  MethodHandle target){
    super(INVOKE);
    this.filter=filter;
    this.target=target;
  }
static {
    assert (MethodHandleNatives.workaroundWithoutRicochetFrames());
  }
  public static MethodHandle make(  MethodHandle filter,  MethodHandle target){
    if (filter == null)     return target;
    if (target == null)     return filter;
    return new FilterOneArgument(filter,target);
  }
}
