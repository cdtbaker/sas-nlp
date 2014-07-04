package sun.invoke.util;
import java.lang.invoke.MethodType;
import sun.invoke.empty.Empty;
/** 
 * This class centralizes information about the JVM verifier
 * and its requirements about type correctness.
 * @author jrose
 */
public class VerifyType {
  private VerifyType(){
  }
  /** 
 * True if a value can be stacked as the source type and unstacked as the
 * destination type, without violating the JVM's type consistency.
 * @param call the type of a stacked value
 * @param recv the type by which we'd like to treat it
 * @return whether the retyping can be done without motion or reformatting
 */
  public static boolean isNullConversion(  Class<?> src,  Class<?> dst){
    if (src == dst)     return true;
    if (dst.isInterface())     dst=Object.class;
    if (src.isInterface())     src=Object.class;
    if (src == dst)     return true;
    if (dst == void.class)     return true;
    if (isNullType(src))     return !dst.isPrimitive();
    if (!src.isPrimitive())     return dst.isAssignableFrom(src);
    if (!dst.isPrimitive())     return false;
    Wrapper sw=Wrapper.forPrimitiveType(src);
    if (dst == int.class)     return sw.isSubwordOrInt();
    Wrapper dw=Wrapper.forPrimitiveType(dst);
    if (!sw.isSubwordOrInt())     return false;
    if (!dw.isSubwordOrInt())     return false;
    if (!dw.isSigned() && sw.isSigned())     return false;
    return dw.bitWidth() > sw.bitWidth();
  }
  /** 
 * Specialization of isNullConversion to reference types.
 * @param call the type of a stacked value
 * @param recv the reference type by which we'd like to treat it
 * @return whether the retyping can be done without a cast
 */
  public static boolean isNullReferenceConversion(  Class<?> src,  Class<?> dst){
    assert (!dst.isPrimitive());
    if (dst.isInterface())     return true;
    if (isNullType(src))     return true;
    return dst.isAssignableFrom(src);
  }
  /** 
 * Is the given type java.lang.Null or an equivalent null-only type?
 */
  public static boolean isNullType(  Class<?> type){
    if (type == null)     return false;
    return type == NULL_CLASS || type == Empty.class;
  }
  private static final Class<?> NULL_CLASS;
static {
    Class<?> nullClass=null;
    try {
      nullClass=Class.forName("java.lang.Null");
    }
 catch (    ClassNotFoundException ex) {
    }
    NULL_CLASS=nullClass;
  }
  /** 
 * True if a method handle can receive a call under a slightly different
 * method type, without moving or reformatting any stack elements.
 * @param call the type of call being made
 * @param recv the type of the method handle receiving the call
 * @return whether the retyping can be done without motion or reformatting
 */
  public static boolean isNullConversion(  MethodType call,  MethodType recv){
    if (call == recv)     return true;
    int len=call.parameterCount();
    if (len != recv.parameterCount())     return false;
    for (int i=0; i < len; i++)     if (!isNullConversion(call.parameterType(i),recv.parameterType(i)))     return false;
    return isNullConversion(recv.returnType(),call.returnType());
  }
  /** 
 * Determine if the JVM verifier allows a value of type call to be
 * passed to a formal parameter (or return variable) of type recv.
 * Returns 1 if the verifier allows the types to match without conversion.
 * Returns -1 if the types can be made to match by a JVM-supported adapter.
 * Cases supported are:
 * <ul><li>checkcast
 * </li><li>conversion between any two integral types (but not floats)
 * </li><li>unboxing from a wrapper to its corresponding primitive type
 * </li><li>conversion in either direction between float and double
 * </li></ul>
 * (Autoboxing is not supported here; it must be done via Java code.)
 * Returns 0 otherwise.
 */
  public static int canPassUnchecked(  Class<?> src,  Class<?> dst){
    if (src == dst)     return 1;
    if (dst.isPrimitive()) {
      if (dst == void.class)       return 1;
      if (src == void.class)       return 0;
      if (!src.isPrimitive())       return 0;
      Wrapper sw=Wrapper.forPrimitiveType(src);
      Wrapper dw=Wrapper.forPrimitiveType(dst);
      if (sw.isSubwordOrInt() && dw.isSubwordOrInt()) {
        if (sw.bitWidth() >= dw.bitWidth())         return -1;
        if (!dw.isSigned() && sw.isSigned())         return -1;
        return 1;
      }
      if (src == float.class || dst == float.class) {
        if (src == double.class || dst == double.class)         return -1;
 else         return 0;
      }
 else {
        return 0;
      }
    }
 else     if (src.isPrimitive()) {
      return 0;
    }
    if (isNullReferenceConversion(src,dst))     return 1;
    return -1;
  }
  public static int canPassRaw(  Class<?> src,  Class<?> dst){
    if (dst.isPrimitive()) {
      if (dst == void.class)       return 1;
      if (src == void.class)       return dst == int.class ? 1 : 0;
      if (isNullType(src))       return 1;
      if (!src.isPrimitive())       return 0;
      Wrapper sw=Wrapper.forPrimitiveType(src);
      Wrapper dw=Wrapper.forPrimitiveType(dst);
      if (sw.stackSlots() == dw.stackSlots())       return 1;
      if (sw.isSubwordOrInt() && dw == Wrapper.VOID)       return 1;
      return 0;
    }
 else     if (src.isPrimitive()) {
      return 0;
    }
    if (isNullReferenceConversion(src,dst))     return 1;
    return -1;
  }
  public static boolean isSpreadArgType(  Class<?> spreadArg){
    return spreadArg.isArray();
  }
  public static Class<?> spreadArgElementType(  Class<?> spreadArg,  int i){
    return spreadArg.getComponentType();
  }
}
