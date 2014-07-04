package java.lang.invoke;
import sun.invoke.util.Wrapper;
import static java.lang.invoke.MethodHandleStatics.*;
/** 
 * Shared information for a group of method types, which differ
 * only by reference types, and therefore share a common erasure
 * and wrapping.
 * <p>
 * For an empirical discussion of the structure of method types,
 * see <a href="http://groups.google.com/group/jvm-languages/browse_thread/thread/ac9308ae74da9b7e/">
 * the thread "Avoiding Boxing" on jvm-languages</a>.
 * There are approximately 2000 distinct erased method types in the JDK.
 * There are a little over 10 times that number of unerased types.
 * No more than half of these are likely to be loaded at once.
 * @author John Rose
 */
class MethodTypeForm {
  final int[] argToSlotTable, slotToArgTable;
  final long argCounts;
  final long primCounts;
  final int vmslots;
  private Object vmlayout;
  final MethodType erasedType;
  MethodType primsAsBoxes;
  MethodType primArgsAsBoxes;
  MethodType primsAsInts;
  MethodType primsAsLongs;
  MethodType primsAtEnd;
  ToGeneric toGeneric;
  FromGeneric fromGeneric;
  SpreadGeneric[] spreadGeneric;
  FilterGeneric filterGeneric;
  MethodHandle genericInvoker;
  public MethodType erasedType(){
    return erasedType;
  }
  protected MethodTypeForm(  MethodType erasedType){
    this.erasedType=erasedType;
    Class<?>[] ptypes=erasedType.ptypes();
    int ptypeCount=ptypes.length;
    int pslotCount=ptypeCount;
    int rtypeCount=1;
    int rslotCount=1;
    int[] argToSlotTab=null, slotToArgTab=null;
    int pac=0, lac=0, prc=0, lrc=0;
    Class<?> epts[]=ptypes;
    for (int i=0; i < epts.length; i++) {
      Class<?> pt=epts[i];
      if (pt != Object.class) {
        assert (pt.isPrimitive());
        ++pac;
        if (hasTwoArgSlots(pt))         ++lac;
      }
    }
    pslotCount+=lac;
    Class<?> rt=erasedType.returnType();
    if (rt != Object.class) {
      ++prc;
      if (hasTwoArgSlots(rt))       ++lrc;
      if (rt == void.class)       rtypeCount=rslotCount=0;
 else       rslotCount+=lrc;
    }
    if (lac != 0) {
      int slot=ptypeCount + lac;
      slotToArgTab=new int[slot + 1];
      argToSlotTab=new int[1 + ptypeCount];
      argToSlotTab[0]=slot;
      for (int i=0; i < epts.length; i++) {
        Class<?> pt=epts[i];
        if (hasTwoArgSlots(pt))         --slot;
        --slot;
        slotToArgTab[slot]=i + 1;
        argToSlotTab[1 + i]=slot;
      }
      assert (slot == 0);
    }
    this.primCounts=pack(lrc,prc,lac,pac);
    this.argCounts=pack(rslotCount,rtypeCount,pslotCount,ptypeCount);
    if (slotToArgTab == null) {
      int slot=ptypeCount;
      slotToArgTab=new int[slot + 1];
      argToSlotTab=new int[1 + ptypeCount];
      argToSlotTab[0]=slot;
      for (int i=0; i < ptypeCount; i++) {
        --slot;
        slotToArgTab[slot]=i + 1;
        argToSlotTab[1 + i]=slot;
      }
    }
    this.argToSlotTable=argToSlotTab;
    this.slotToArgTable=slotToArgTab;
    if (pslotCount >= 256)     throw newIllegalArgumentException("too many arguments");
    this.vmslots=parameterSlotCount();
    if (!hasPrimitives()) {
      primsAsBoxes=erasedType;
      primArgsAsBoxes=erasedType;
      primsAsInts=erasedType;
      primsAsLongs=erasedType;
      primsAtEnd=erasedType;
    }
  }
  /** 
 * Turn all primitive types to corresponding wrapper types.
 */
  public MethodType primsAsBoxes(){
    MethodType ct=primsAsBoxes;
    if (ct != null)     return ct;
    MethodType t=erasedType;
    ct=canonicalize(erasedType,WRAP,WRAP);
    if (ct == null)     ct=t;
    return primsAsBoxes=ct;
  }
  /** 
 * Turn all primitive argument types to corresponding wrapper types.
 * Subword and void return types are promoted to int.
 */
  public MethodType primArgsAsBoxes(){
    MethodType ct=primArgsAsBoxes;
    if (ct != null)     return ct;
    MethodType t=erasedType;
    ct=canonicalize(erasedType,RAW_RETURN,WRAP);
    if (ct == null)     ct=t;
    return primArgsAsBoxes=ct;
  }
  /** 
 * Turn all primitive types to either int or long.
 * Floating point return types are not changed, because
 * they may require special calling sequences.
 * A void return value is turned to int.
 */
  public MethodType primsAsInts(){
    MethodType ct=primsAsInts;
    if (ct != null)     return ct;
    MethodType t=erasedType;
    ct=canonicalize(t,RAW_RETURN,INTS);
    if (ct == null)     ct=t;
    return primsAsInts=ct;
  }
  /** 
 * Turn all primitive types to either int or long.
 * Floating point return types are not changed, because
 * they may require special calling sequences.
 * A void return value is turned to int.
 */
  public MethodType primsAsLongs(){
    MethodType ct=primsAsLongs;
    if (ct != null)     return ct;
    MethodType t=erasedType;
    ct=canonicalize(t,RAW_RETURN,LONGS);
    if (ct == null)     ct=t;
    return primsAsLongs=ct;
  }
  /** 
 * Stably sort parameters into 3 buckets: ref, int, long. 
 */
  public MethodType primsAtEnd(){
    MethodType ct=primsAtEnd;
    if (ct != null)     return ct;
    MethodType t=erasedType;
    int pac=primitiveParameterCount();
    if (pac == 0)     return primsAtEnd=t;
    int argc=parameterCount();
    int lac=longPrimitiveParameterCount();
    if (pac == argc && (lac == 0 || lac == argc))     return primsAtEnd=t;
    int[] reorder=primsAtEndOrder(t);
    ct=reorderParameters(t,reorder,null);
    return primsAtEnd=ct;
  }
  /** 
 * Compute a new ordering of parameters so that all references
 * are before all ints or longs, and all ints are before all longs.
 * For this ordering, doubles count as longs, and all other primitive
 * values count as ints.
 * As a special case, if the parameters are already in the specified
 * order, this method returns a null reference, rather than an array
 * specifying a null permutation.
 * <p>
 * For example, the type {@code (int,boolean,int,Object,String)void}produces the order {@code} 
 * 3,4,0,1,2}}, the type{@code (long,int,String)void} produces {@code} 
 * 2,1,2}}, and
 * the type {@code (Object,int)Object} produces {@code null}.
 */
  public static int[] primsAtEndOrder(  MethodType mt){
    MethodTypeForm form=mt.form();
    if (form.primsAtEnd == form.erasedType)     return null;
    int argc=form.parameterCount();
    int[] paramOrder=new int[argc];
    int pac=form.primitiveParameterCount();
    int lac=form.longPrimitiveParameterCount();
    int rfill=0, ifill=argc - pac, lfill=argc - lac;
    Class<?>[] ptypes=mt.ptypes();
    boolean changed=false;
    for (int i=0; i < ptypes.length; i++) {
      Class<?> pt=ptypes[i];
      int ord;
      if (!pt.isPrimitive())       ord=rfill++;
 else       if (!hasTwoArgSlots(pt))       ord=ifill++;
 else       ord=lfill++;
      if (ord != i)       changed=true;
      assert (paramOrder[ord] == 0);
      paramOrder[ord]=i;
    }
    assert (rfill == argc - pac && ifill == argc - lac && lfill == argc);
    if (!changed) {
      form.primsAtEnd=form.erasedType;
      return null;
    }
    return paramOrder;
  }
  /** 
 * Put the existing parameters of mt into a new order, given by newParamOrder.
 * The third argument is logically appended to mt.parameterArray,
 * so that elements of newParamOrder can index either pre-existing or
 * new parameter types.
 */
  public static MethodType reorderParameters(  MethodType mt,  int[] newParamOrder,  Class<?>[] moreParams){
    if (newParamOrder == null)     return mt;
    Class<?>[] ptypes=mt.ptypes();
    Class<?>[] ntypes=new Class<?>[newParamOrder.length];
    int maxParam=ptypes.length + (moreParams == null ? 0 : moreParams.length);
    boolean changed=(ntypes.length != ptypes.length);
    for (int i=0; i < newParamOrder.length; i++) {
      int param=newParamOrder[i];
      if (param != i)       changed=true;
      Class<?> nt;
      if (param < ptypes.length)       nt=ptypes[param];
 else       if (param == maxParam)       nt=mt.returnType();
 else       nt=moreParams[param - ptypes.length];
      ntypes[i]=nt;
    }
    if (!changed)     return mt;
    return MethodType.makeImpl(mt.returnType(),ntypes,true);
  }
  private static boolean hasTwoArgSlots(  Class<?> type){
    return type == long.class || type == double.class;
  }
  private static long pack(  int a,  int b,  int c,  int d){
    assert (((a | b | c| d) & ~0xFFFF) == 0);
    long hw=((a << 16) | b), lw=((c << 16) | d);
    return (hw << 32) | lw;
  }
  private static char unpack(  long packed,  int word){
    assert (word <= 3);
    return (char)(packed >> ((3 - word) * 16));
  }
  public int parameterCount(){
    return unpack(argCounts,3);
  }
  public int parameterSlotCount(){
    return unpack(argCounts,2);
  }
  public int returnCount(){
    return unpack(argCounts,1);
  }
  public int returnSlotCount(){
    return unpack(argCounts,0);
  }
  public int primitiveParameterCount(){
    return unpack(primCounts,3);
  }
  public int longPrimitiveParameterCount(){
    return unpack(primCounts,2);
  }
  public int primitiveReturnCount(){
    return unpack(primCounts,1);
  }
  public int longPrimitiveReturnCount(){
    return unpack(primCounts,0);
  }
  public boolean hasPrimitives(){
    return primCounts != 0;
  }
  public boolean hasLongPrimitives(){
    return (longPrimitiveParameterCount() | longPrimitiveReturnCount()) != 0;
  }
  public int parameterToArgSlot(  int i){
    return argToSlotTable[1 + i];
  }
  public int argSlotToParameter(  int argSlot){
    return slotToArgTable[argSlot] - 1;
  }
  static MethodTypeForm findForm(  MethodType mt){
    MethodType erased=canonicalize(mt,ERASE,ERASE);
    if (erased == null) {
      return new MethodTypeForm(mt);
    }
 else {
      return erased.form();
    }
  }
  /** 
 * Codes for {@link #canonicalize(java.lang.Class,int)}.
 * ERASE means change every reference to {@code Object}.
 * WRAP means convert primitives (including {@code void} to their
 * corresponding wrapper types.  UNWRAP means the reverse of WRAP.
 * INTS means convert all non-void primitive types to int or long,
 * according to size.  LONGS means convert all non-void primitives
 * to long, regardless of size.  RAW_RETURN means convert a type
 * (assumed to be a return type) to int if it is smaller than an int,
 * or if it is void.
 */
  public static final int NO_CHANGE=0, ERASE=1, WRAP=2, UNWRAP=3, INTS=4, LONGS=5, RAW_RETURN=6;
  /** 
 * Canonicalize the types in the given method type.
 * If any types change, intern the new type, and return it.
 * Otherwise return null.
 */
  public static MethodType canonicalize(  MethodType mt,  int howRet,  int howArgs){
    Class<?>[] ptypes=mt.ptypes();
    Class<?>[] ptc=MethodTypeForm.canonicalizes(ptypes,howArgs);
    Class<?> rtype=mt.returnType();
    Class<?> rtc=MethodTypeForm.canonicalize(rtype,howRet);
    if (ptc == null && rtc == null) {
      return null;
    }
    if (rtc == null)     rtc=rtype;
    if (ptc == null)     ptc=ptypes;
    return MethodType.makeImpl(rtc,ptc,true);
  }
  /** 
 * Canonicalize the given return or param type.
 * Return null if the type is already canonicalized.
 */
  static Class<?> canonicalize(  Class<?> t,  int how){
    Class<?> ct;
    if (t == Object.class) {
    }
 else     if (!t.isPrimitive()) {
switch (how) {
case UNWRAP:
        ct=Wrapper.asPrimitiveType(t);
      if (ct != t)       return ct;
    break;
case RAW_RETURN:
case ERASE:
  return Object.class;
}
}
 else if (t == void.class) {
switch (how) {
case RAW_RETURN:
return int.class;
case WRAP:
return Void.class;
}
}
 else {
switch (how) {
case WRAP:
return Wrapper.asWrapperType(t);
case INTS:
if (t == int.class || t == long.class) return null;
if (t == double.class) return long.class;
return int.class;
case LONGS:
if (t == long.class) return null;
return long.class;
case RAW_RETURN:
if (t == int.class || t == long.class || t == float.class || t == double.class) return null;
return int.class;
}
}
return null;
}
/** 
 * Canonicalize each param type in the given array.
 * Return null if all types are already canonicalized.
 */
static Class<?>[] canonicalizes(Class<?>[] ts,int how){
Class<?>[] cs=null;
for (int imax=ts.length, i=0; i < imax; i++) {
Class<?> c=canonicalize(ts[i],how);
if (c == void.class) c=null;
if (c != null) {
if (cs == null) cs=ts.clone();
cs[i]=c;
}
}
return cs;
}
void notifyGenericMethodType(){
if (genericInvoker != null) return;
try {
genericInvoker=InvokeGeneric.generalInvokerOf(erasedType);
}
 catch (Exception ex) {
Error err=new InternalError("Exception while resolving inexact invoke");
err.initCause(ex);
throw err;
}
}
@Override public String toString(){
return "Form" + erasedType;
}
}
