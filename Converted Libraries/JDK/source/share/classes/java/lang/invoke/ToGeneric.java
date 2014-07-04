package java.lang.invoke;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import sun.invoke.util.ValueConversions;
import sun.invoke.util.Wrapper;
import static java.lang.invoke.MethodHandleStatics.*;
import static java.lang.invoke.MethodHandles.Lookup.IMPL_LOOKUP;
/** 
 * Adapters which mediate between incoming calls which are not generic
 * and outgoing calls which are.  Any call can be represented generically
 * boxing up its arguments, and (on return) unboxing the return value.
 * <p>
 * A call is "generic" (in MethodHandle terms) if its MethodType features
 * only Object arguments.  A non-generic call therefore features
 * primitives and/or reference types other than Object.
 * An adapter has types for its incoming and outgoing calls.
 * The incoming call type is simply determined by the adapter's type
 * (the MethodType it presents to callers).  The outgoing call type
 * is determined by the adapter's target (a MethodHandle that the adapter
 * either binds internally or else takes as a leading argument).
 * (To stretch the term, adapter-like method handles may have multiple
 * targets or be polymorphic across multiple call types.)
 * @author jrose
 */
class ToGeneric {
  private final MethodType entryType;
  private final MethodType rawEntryType;
  private final Adapter adapter;
  private final MethodHandle entryPoint;
  private final int[] primsAtEndOrder;
  private final MethodHandle invoker;
  private final MethodHandle returnConversion;
  /** 
 * Compute and cache information common to all generifying (boxing) adapters
 * that implement members of the erasure-family of the given erased type.
 */
  private ToGeneric(  MethodType entryType){
    assert (entryType.erase() == entryType);
    this.entryType=entryType;
    MethodHandle invoker0=entryType.generic().invokers().exactInvoker();
    MethodType rawEntryTypeInit;
    Adapter ad=findAdapter(rawEntryTypeInit=entryType);
    if (ad != null) {
      this.returnConversion=computeReturnConversion(entryType,rawEntryTypeInit,false);
      this.rawEntryType=rawEntryTypeInit;
      this.adapter=ad;
      this.entryPoint=ad.prototypeEntryPoint();
      this.primsAtEndOrder=null;
      this.invoker=invoker0;
      return;
    }
    MethodType primsAtEnd=entryType.form().primsAtEnd();
    this.primsAtEndOrder=MethodTypeForm.primsAtEndOrder(entryType);
    if (primsAtEndOrder != null) {
      ToGeneric va2=ToGeneric.of(primsAtEnd);
      this.adapter=va2.adapter;
      if (true)       throw new UnsupportedOperationException("NYI: primitive parameters must follow references; entryType = " + entryType);
      this.entryPoint=MethodHandleImpl.permuteArguments(va2.entryPoint,primsAtEnd,entryType,primsAtEndOrder);
      return;
    }
    MethodType intsAtEnd=primsAtEnd.form().primsAsInts();
    ad=findAdapter(rawEntryTypeInit=intsAtEnd);
    MethodHandle rawEntryPoint;
    if (ad != null) {
      rawEntryPoint=ad.prototypeEntryPoint();
    }
 else {
      MethodType longsAtEnd=primsAtEnd.form().primsAsLongs();
      ad=findAdapter(rawEntryTypeInit=longsAtEnd);
      if (ad != null) {
        MethodType eptWithLongs=longsAtEnd.insertParameterTypes(0,ad.getClass());
        MethodType eptWithInts=intsAtEnd.insertParameterTypes(0,ad.getClass());
        rawEntryPoint=ad.prototypeEntryPoint();
        MethodType midType=eptWithLongs;
        for (int i=0, nargs=midType.parameterCount(); i < nargs; i++) {
          if (midType.parameterType(i) != eptWithInts.parameterType(i)) {
            assert (midType.parameterType(i) == long.class);
            assert (eptWithInts.parameterType(i) == int.class);
            MethodType nextType=midType.changeParameterType(i,int.class);
            rawEntryPoint=MethodHandleImpl.convertArguments(rawEntryPoint,nextType,midType,0);
            midType=nextType;
          }
        }
        assert (midType == eptWithInts);
      }
 else {
        ad=buildAdapterFromBytecodes(rawEntryTypeInit=intsAtEnd);
        rawEntryPoint=ad.prototypeEntryPoint();
      }
    }
    MethodType tepType=entryType.insertParameterTypes(0,ad.getClass());
    this.entryPoint=AdapterMethodHandle.makeRetypeRaw(tepType,rawEntryPoint);
    if (this.entryPoint == null)     throw new UnsupportedOperationException("cannot retype to " + entryType + " from "+ rawEntryPoint.type().dropParameterTypes(0,1));
    this.returnConversion=computeReturnConversion(entryType,rawEntryTypeInit,false);
    this.rawEntryType=rawEntryTypeInit;
    this.adapter=ad;
    this.invoker=makeRawArgumentFilter(invoker0,rawEntryTypeInit,entryType);
  }
static {
    assert (MethodHandleNatives.workaroundWithoutRicochetFrames());
  }
  /** 
 * A generic argument list will be created by a call of type 'raw'.
 * The values need to be reboxed for to match 'cooked'.
 * Do this on the fly.
 */
  static MethodHandle makeRawArgumentFilter(  MethodHandle invoker,  MethodType raw,  MethodType cooked){
    MethodHandle filteredInvoker=null;
    for (int i=0, nargs=raw.parameterCount(); i < nargs; i++) {
      Class<?> src=raw.parameterType(i);
      Class<?> dst=cooked.parameterType(i);
      if (src == dst)       continue;
      assert (src.isPrimitive() && dst.isPrimitive());
      if (filteredInvoker == null) {
        filteredInvoker=AdapterMethodHandle.makeCheckCast(invoker.type().generic(),invoker,0,MethodHandle.class);
        if (filteredInvoker == null)         throw new UnsupportedOperationException("NYI");
      }
      MethodHandle reboxer=ValueConversions.rebox(dst);
      filteredInvoker=FilterGeneric.makeArgumentFilter(1 + i,reboxer,filteredInvoker);
      if (filteredInvoker == null)       throw new InternalError();
    }
    if (filteredInvoker == null)     return invoker;
    return AdapterMethodHandle.makeRetypeOnly(invoker.type(),filteredInvoker);
  }
  /** 
 * Caller will be expecting a result from a call to {@code type},
 * while the internal adapter entry point is rawEntryType.
 * Also, the internal target method will be returning a boxed value,
 * as an untyped object.
 * <p>
 * Produce a value converter which will be typed to convert from{@code Object} to the return value of {@code rawEntryType}, and will
 * in fact ensure that the value is compatible with the return type of{@code type}.
 */
  private static MethodHandle computeReturnConversion(  MethodType type,  MethodType rawEntryType,  boolean mustCast){
    Class<?> tret=type.returnType();
    Class<?> rret=rawEntryType.returnType();
    if (mustCast || !tret.isPrimitive()) {
      assert (!tret.isPrimitive());
      assert (!rret.isPrimitive());
      if (rret == Object.class && !mustCast)       return null;
      return ValueConversions.cast(tret);
    }
 else     if (tret == rret) {
      return ValueConversions.unbox(tret);
    }
 else {
      assert (rret.isPrimitive());
      assert (tret == double.class ? rret == long.class : rret == int.class);
      return ValueConversions.unboxRaw(tret);
    }
  }
  Adapter makeInstance(  MethodType type,  MethodHandle genericTarget){
    genericTarget.getClass();
    MethodHandle convert=returnConversion;
    if (primsAtEndOrder != null)     throw new UnsupportedOperationException("NYI");
    if (type == entryType) {
      if (convert == null)       convert=ValueConversions.identity();
      return adapter.makeInstance(entryPoint,invoker,convert,genericTarget);
    }
    assert (type.erase() == entryType);
    if (convert == null)     convert=computeReturnConversion(type,rawEntryType,true);
    MethodType tepType=type.insertParameterTypes(0,adapter.getClass());
    MethodHandle typedEntryPoint=AdapterMethodHandle.makeRetypeRaw(tepType,entryPoint);
    return adapter.makeInstance(typedEntryPoint,invoker,convert,genericTarget);
  }
  /** 
 * Build an adapter of the given type, which invokes genericTarget
 * on the incoming arguments, after boxing as necessary.
 * The return value is unboxed if necessary.
 * @param type  the required type of the
 * @param genericTarget the target, which must accept and return only Object values
 * @return an adapter method handle
 */
  public static MethodHandle make(  MethodType type,  MethodHandle genericTarget){
    MethodType gtype=genericTarget.type();
    if (type.generic() != gtype)     throw newIllegalArgumentException("type must be generic");
    if (type == gtype)     return genericTarget;
    return ToGeneric.of(type).makeInstance(type,genericTarget);
  }
  /** 
 * Return the adapter information for this type's erasure. 
 */
  static ToGeneric of(  MethodType type){
    MethodTypeForm form=type.form();
    ToGeneric toGen=form.toGeneric;
    if (toGen == null)     form.toGeneric=toGen=new ToGeneric(form.erasedType());
    return toGen;
  }
  String debugString(){
    return "ToGeneric" + entryType + (primsAtEndOrder != null ? "[reorder]" : "");
  }
  static Adapter findAdapter(  MethodType entryPointType){
    MethodTypeForm form=entryPointType.form();
    Class<?> rtype=entryPointType.returnType();
    int argc=form.parameterCount();
    int lac=form.longPrimitiveParameterCount();
    int iac=form.primitiveParameterCount() - lac;
    String intsAndLongs=(iac > 0 ? "I" + iac : "") + (lac > 0 ? "J" + lac : "");
    String rawReturn=String.valueOf(Wrapper.forPrimitiveType(rtype).basicTypeChar());
    String iname0="invoke_" + rawReturn;
    String iname1="invoke";
    String[] inames={iname0,iname1};
    String cname0=rawReturn + argc;
    String cname1="A" + argc;
    String[] cnames={cname1,cname1 + intsAndLongs,cname0,cname0 + intsAndLongs};
    for (    String cname : cnames) {
      Class<? extends Adapter> acls=Adapter.findSubClass(cname);
      if (acls == null)       continue;
      for (      String iname : inames) {
        MethodHandle entryPoint=null;
        try {
          entryPoint=IMPL_LOOKUP.findSpecial(acls,iname,entryPointType,acls);
        }
 catch (        ReflectiveOperationException ex) {
        }
        if (entryPoint == null)         continue;
        Constructor<? extends Adapter> ctor=null;
        try {
          ctor=acls.getDeclaredConstructor(MethodHandle.class);
        }
 catch (        NoSuchMethodException ex) {
        }
catch (        SecurityException ex) {
        }
        if (ctor == null)         continue;
        try {
          return ctor.newInstance(entryPoint);
        }
 catch (        IllegalArgumentException ex) {
        }
catch (        InvocationTargetException wex) {
          Throwable ex=wex.getTargetException();
          if (ex instanceof Error)           throw (Error)ex;
          if (ex instanceof RuntimeException)           throw (RuntimeException)ex;
        }
catch (        InstantiationException ex) {
        }
catch (        IllegalAccessException ex) {
        }
      }
    }
    return null;
  }
  static Adapter buildAdapterFromBytecodes(  MethodType entryPointType){
    throw new UnsupportedOperationException("NYI: " + entryPointType);
  }
  /** 
 * The invoke method takes some particular but unconstrained spread
 * of raw argument types, and returns a raw return type (in L/I/J/F/D).
 * Internally, it converts the incoming arguments uniformly into objects.
 * This series of objects is then passed to the {@code target} method,
 * which returns a result object.  This result is finally converted,
 * via another method handle {@code convert}, which is responsible for
 * converting the object result into the raw return value.
 */
static abstract class Adapter extends BoundMethodHandle {
    protected final MethodHandle invoker;
    protected final MethodHandle target;
    protected final MethodHandle convert;
    @Override String debugString(){
      return target == null ? "prototype:" + convert : addTypeString(target,this);
    }
    protected boolean isPrototype(){
      return target == null;
    }
    protected Adapter(    MethodHandle entryPoint){
      super(entryPoint);
      this.invoker=null;
      this.convert=entryPoint;
      this.target=null;
      assert (isPrototype());
    }
    protected MethodHandle prototypeEntryPoint(){
      if (!isPrototype())       throw new InternalError();
      return convert;
    }
    protected Adapter(    MethodHandle entryPoint,    MethodHandle invoker,    MethodHandle convert,    MethodHandle target){
      super(entryPoint);
      this.invoker=invoker;
      this.convert=convert;
      this.target=target;
    }
    /** 
 * Make a copy of self, with new fields. 
 */
    protected abstract Adapter makeInstance(    MethodHandle entryPoint,    MethodHandle invoker,    MethodHandle convert,    MethodHandle target);
    protected Object target() throws Throwable {
      return invoker.invokeExact(target);
    }
    protected Object target(    Object a0) throws Throwable {
      return invoker.invokeExact(target,a0);
    }
    protected Object target(    Object a0,    Object a1) throws Throwable {
      return invoker.invokeExact(target,a0,a1);
    }
    protected Object target(    Object a0,    Object a1,    Object a2) throws Throwable {
      return invoker.invokeExact(target,a0,a1,a2);
    }
    protected Object target(    Object a0,    Object a1,    Object a2,    Object a3) throws Throwable {
      return invoker.invokeExact(target,a0,a1,a2,a3);
    }
    protected Object return_L(    Object res) throws Throwable {
      return (Object)convert.invokeExact(res);
    }
    protected int return_I(    Object res) throws Throwable {
      return (int)convert.invokeExact(res);
    }
    protected long return_J(    Object res) throws Throwable {
      return (long)convert.invokeExact(res);
    }
    protected float return_F(    Object res) throws Throwable {
      return (float)convert.invokeExact(res);
    }
    protected double return_D(    Object res) throws Throwable {
      return (double)convert.invokeExact(res);
    }
    static private final String CLASS_PREFIX;
static {
      String aname=Adapter.class.getName();
      String sname=Adapter.class.getSimpleName();
      if (!aname.endsWith(sname))       throw new InternalError();
      CLASS_PREFIX=aname.substring(0,aname.length() - sname.length());
    }
    /** 
 * Find a sibing class of Adapter. 
 */
    static Class<? extends Adapter> findSubClass(    String name){
      String cname=Adapter.CLASS_PREFIX + name;
      try {
        return Class.forName(cname).asSubclass(Adapter.class);
      }
 catch (      ClassNotFoundException ex) {
        return null;
      }
catch (      ClassCastException ex) {
        return null;
      }
    }
  }
static class A0 extends Adapter {
    protected A0(    MethodHandle entryPoint){
      super(entryPoint);
    }
    protected A0(    MethodHandle e,    MethodHandle i,    MethodHandle c,    MethodHandle t){
      super(e,i,c,t);
    }
    protected A0 makeInstance(    MethodHandle e,    MethodHandle i,    MethodHandle c,    MethodHandle t){
      return new A0(e,i,c,t);
    }
    protected Object target() throws Throwable {
      return invoker.invokeExact(target);
    }
    protected Object targetA0() throws Throwable {
      return target();
    }
    protected Object invoke_L() throws Throwable {
      return return_L(targetA0());
    }
    protected int invoke_I() throws Throwable {
      return return_I(targetA0());
    }
    protected long invoke_J() throws Throwable {
      return return_J(targetA0());
    }
    protected float invoke_F() throws Throwable {
      return return_F(targetA0());
    }
    protected double invoke_D() throws Throwable {
      return return_D(targetA0());
    }
  }
static class A1 extends Adapter {
    protected A1(    MethodHandle entryPoint){
      super(entryPoint);
    }
    protected A1(    MethodHandle e,    MethodHandle i,    MethodHandle c,    MethodHandle t){
      super(e,i,c,t);
    }
    protected A1 makeInstance(    MethodHandle e,    MethodHandle i,    MethodHandle c,    MethodHandle t){
      return new A1(e,i,c,t);
    }
    protected Object target(    Object a0) throws Throwable {
      return invoker.invokeExact(target,a0);
    }
    protected Object targetA1(    Object a0) throws Throwable {
      return target(a0);
    }
    protected Object targetA1(    int a0) throws Throwable {
      return target(a0);
    }
    protected Object targetA1(    long a0) throws Throwable {
      return target(a0);
    }
    protected Object invoke_L(    Object a0) throws Throwable {
      return return_L(targetA1(a0));
    }
    protected int invoke_I(    Object a0) throws Throwable {
      return return_I(targetA1(a0));
    }
    protected long invoke_J(    Object a0) throws Throwable {
      return return_J(targetA1(a0));
    }
    protected float invoke_F(    Object a0) throws Throwable {
      return return_F(targetA1(a0));
    }
    protected double invoke_D(    Object a0) throws Throwable {
      return return_D(targetA1(a0));
    }
    protected Object invoke_L(    int a0) throws Throwable {
      return return_L(targetA1(a0));
    }
    protected int invoke_I(    int a0) throws Throwable {
      return return_I(targetA1(a0));
    }
    protected long invoke_J(    int a0) throws Throwable {
      return return_J(targetA1(a0));
    }
    protected float invoke_F(    int a0) throws Throwable {
      return return_F(targetA1(a0));
    }
    protected double invoke_D(    int a0) throws Throwable {
      return return_D(targetA1(a0));
    }
    protected Object invoke_L(    long a0) throws Throwable {
      return return_L(targetA1(a0));
    }
    protected int invoke_I(    long a0) throws Throwable {
      return return_I(targetA1(a0));
    }
    protected long invoke_J(    long a0) throws Throwable {
      return return_J(targetA1(a0));
    }
    protected float invoke_F(    long a0) throws Throwable {
      return return_F(targetA1(a0));
    }
    protected double invoke_D(    long a0) throws Throwable {
      return return_D(targetA1(a0));
    }
  }
static class A2 extends Adapter {
    protected A2(    MethodHandle entryPoint){
      super(entryPoint);
    }
    protected A2(    MethodHandle e,    MethodHandle i,    MethodHandle c,    MethodHandle t){
      super(e,i,c,t);
    }
    protected A2 makeInstance(    MethodHandle e,    MethodHandle i,    MethodHandle c,    MethodHandle t){
      return new A2(e,i,c,t);
    }
    protected Object target(    Object a0,    Object a1) throws Throwable {
      return invoker.invokeExact(target,a0,a1);
    }
    protected Object targetA2(    Object a0,    Object a1) throws Throwable {
      return target(a0,a1);
    }
    protected Object targetA2(    Object a0,    int a1) throws Throwable {
      return target(a0,a1);
    }
    protected Object targetA2(    int a0,    int a1) throws Throwable {
      return target(a0,a1);
    }
    protected Object targetA2(    Object a0,    long a1) throws Throwable {
      return target(a0,a1);
    }
    protected Object targetA2(    long a0,    long a1) throws Throwable {
      return target(a0,a1);
    }
    protected Object invoke_L(    Object a0,    Object a1) throws Throwable {
      return return_L(targetA2(a0,a1));
    }
    protected int invoke_I(    Object a0,    Object a1) throws Throwable {
      return return_I(targetA2(a0,a1));
    }
    protected long invoke_J(    Object a0,    Object a1) throws Throwable {
      return return_J(targetA2(a0,a1));
    }
    protected float invoke_F(    Object a0,    Object a1) throws Throwable {
      return return_F(targetA2(a0,a1));
    }
    protected double invoke_D(    Object a0,    Object a1) throws Throwable {
      return return_D(targetA2(a0,a1));
    }
    protected Object invoke_L(    Object a0,    int a1) throws Throwable {
      return return_L(targetA2(a0,a1));
    }
    protected int invoke_I(    Object a0,    int a1) throws Throwable {
      return return_I(targetA2(a0,a1));
    }
    protected long invoke_J(    Object a0,    int a1) throws Throwable {
      return return_J(targetA2(a0,a1));
    }
    protected float invoke_F(    Object a0,    int a1) throws Throwable {
      return return_F(targetA2(a0,a1));
    }
    protected double invoke_D(    Object a0,    int a1) throws Throwable {
      return return_D(targetA2(a0,a1));
    }
    protected Object invoke_L(    int a0,    int a1) throws Throwable {
      return return_L(targetA2(a0,a1));
    }
    protected int invoke_I(    int a0,    int a1) throws Throwable {
      return return_I(targetA2(a0,a1));
    }
    protected long invoke_J(    int a0,    int a1) throws Throwable {
      return return_J(targetA2(a0,a1));
    }
    protected float invoke_F(    int a0,    int a1) throws Throwable {
      return return_F(targetA2(a0,a1));
    }
    protected double invoke_D(    int a0,    int a1) throws Throwable {
      return return_D(targetA2(a0,a1));
    }
    protected Object invoke_L(    Object a0,    long a1) throws Throwable {
      return return_L(targetA2(a0,a1));
    }
    protected int invoke_I(    Object a0,    long a1) throws Throwable {
      return return_I(targetA2(a0,a1));
    }
    protected long invoke_J(    Object a0,    long a1) throws Throwable {
      return return_J(targetA2(a0,a1));
    }
    protected float invoke_F(    Object a0,    long a1) throws Throwable {
      return return_F(targetA2(a0,a1));
    }
    protected double invoke_D(    Object a0,    long a1) throws Throwable {
      return return_D(targetA2(a0,a1));
    }
    protected Object invoke_L(    long a0,    long a1) throws Throwable {
      return return_L(targetA2(a0,a1));
    }
    protected int invoke_I(    long a0,    long a1) throws Throwable {
      return return_I(targetA2(a0,a1));
    }
    protected long invoke_J(    long a0,    long a1) throws Throwable {
      return return_J(targetA2(a0,a1));
    }
    protected float invoke_F(    long a0,    long a1) throws Throwable {
      return return_F(targetA2(a0,a1));
    }
    protected double invoke_D(    long a0,    long a1) throws Throwable {
      return return_D(targetA2(a0,a1));
    }
  }
static class A3 extends Adapter {
    protected A3(    MethodHandle entryPoint){
      super(entryPoint);
    }
    protected A3(    MethodHandle e,    MethodHandle i,    MethodHandle c,    MethodHandle t){
      super(e,i,c,t);
    }
    protected A3 makeInstance(    MethodHandle e,    MethodHandle i,    MethodHandle c,    MethodHandle t){
      return new A3(e,i,c,t);
    }
    protected Object target(    Object a0,    Object a1,    Object a2) throws Throwable {
      return invoker.invokeExact(target,a0,a1,a2);
    }
    protected Object targetA3(    Object a0,    Object a1,    Object a2) throws Throwable {
      return target(a0,a1,a2);
    }
    protected Object targetA3(    Object a0,    Object a1,    int a2) throws Throwable {
      return target(a0,a1,a2);
    }
    protected Object targetA3(    Object a0,    int a1,    int a2) throws Throwable {
      return target(a0,a1,a2);
    }
    protected Object targetA3(    int a0,    int a1,    int a2) throws Throwable {
      return target(a0,a1,a2);
    }
    protected Object targetA3(    Object a0,    Object a1,    long a2) throws Throwable {
      return target(a0,a1,a2);
    }
    protected Object targetA3(    Object a0,    long a1,    long a2) throws Throwable {
      return target(a0,a1,a2);
    }
    protected Object targetA3(    long a0,    long a1,    long a2) throws Throwable {
      return target(a0,a1,a2);
    }
    protected Object invoke_L(    Object a0,    Object a1,    Object a2) throws Throwable {
      return return_L(targetA3(a0,a1,a2));
    }
    protected int invoke_I(    Object a0,    Object a1,    Object a2) throws Throwable {
      return return_I(targetA3(a0,a1,a2));
    }
    protected long invoke_J(    Object a0,    Object a1,    Object a2) throws Throwable {
      return return_J(targetA3(a0,a1,a2));
    }
    protected float invoke_F(    Object a0,    Object a1,    Object a2) throws Throwable {
      return return_F(targetA3(a0,a1,a2));
    }
    protected double invoke_D(    Object a0,    Object a1,    Object a2) throws Throwable {
      return return_D(targetA3(a0,a1,a2));
    }
    protected Object invoke_L(    Object a0,    Object a1,    int a2) throws Throwable {
      return return_L(targetA3(a0,a1,a2));
    }
    protected int invoke_I(    Object a0,    Object a1,    int a2) throws Throwable {
      return return_I(targetA3(a0,a1,a2));
    }
    protected long invoke_J(    Object a0,    Object a1,    int a2) throws Throwable {
      return return_J(targetA3(a0,a1,a2));
    }
    protected float invoke_F(    Object a0,    Object a1,    int a2) throws Throwable {
      return return_F(targetA3(a0,a1,a2));
    }
    protected double invoke_D(    Object a0,    Object a1,    int a2) throws Throwable {
      return return_D(targetA3(a0,a1,a2));
    }
    protected Object invoke_L(    Object a0,    int a1,    int a2) throws Throwable {
      return return_L(targetA3(a0,a1,a2));
    }
    protected int invoke_I(    Object a0,    int a1,    int a2) throws Throwable {
      return return_I(targetA3(a0,a1,a2));
    }
    protected long invoke_J(    Object a0,    int a1,    int a2) throws Throwable {
      return return_J(targetA3(a0,a1,a2));
    }
    protected float invoke_F(    Object a0,    int a1,    int a2) throws Throwable {
      return return_F(targetA3(a0,a1,a2));
    }
    protected double invoke_D(    Object a0,    int a1,    int a2) throws Throwable {
      return return_D(targetA3(a0,a1,a2));
    }
    protected Object invoke_L(    int a0,    int a1,    int a2) throws Throwable {
      return return_L(targetA3(a0,a1,a2));
    }
    protected int invoke_I(    int a0,    int a1,    int a2) throws Throwable {
      return return_I(targetA3(a0,a1,a2));
    }
    protected long invoke_J(    int a0,    int a1,    int a2) throws Throwable {
      return return_J(targetA3(a0,a1,a2));
    }
    protected float invoke_F(    int a0,    int a1,    int a2) throws Throwable {
      return return_F(targetA3(a0,a1,a2));
    }
    protected double invoke_D(    int a0,    int a1,    int a2) throws Throwable {
      return return_D(targetA3(a0,a1,a2));
    }
    protected Object invoke_L(    Object a0,    Object a1,    long a2) throws Throwable {
      return return_L(targetA3(a0,a1,a2));
    }
    protected int invoke_I(    Object a0,    Object a1,    long a2) throws Throwable {
      return return_I(targetA3(a0,a1,a2));
    }
    protected long invoke_J(    Object a0,    Object a1,    long a2) throws Throwable {
      return return_J(targetA3(a0,a1,a2));
    }
    protected float invoke_F(    Object a0,    Object a1,    long a2) throws Throwable {
      return return_F(targetA3(a0,a1,a2));
    }
    protected double invoke_D(    Object a0,    Object a1,    long a2) throws Throwable {
      return return_D(targetA3(a0,a1,a2));
    }
    protected Object invoke_L(    Object a0,    long a1,    long a2) throws Throwable {
      return return_L(targetA3(a0,a1,a2));
    }
    protected int invoke_I(    Object a0,    long a1,    long a2) throws Throwable {
      return return_I(targetA3(a0,a1,a2));
    }
    protected long invoke_J(    Object a0,    long a1,    long a2) throws Throwable {
      return return_J(targetA3(a0,a1,a2));
    }
    protected float invoke_F(    Object a0,    long a1,    long a2) throws Throwable {
      return return_F(targetA3(a0,a1,a2));
    }
    protected double invoke_D(    Object a0,    long a1,    long a2) throws Throwable {
      return return_D(targetA3(a0,a1,a2));
    }
    protected Object invoke_L(    long a0,    long a1,    long a2) throws Throwable {
      return return_L(targetA3(a0,a1,a2));
    }
    protected int invoke_I(    long a0,    long a1,    long a2) throws Throwable {
      return return_I(targetA3(a0,a1,a2));
    }
    protected long invoke_J(    long a0,    long a1,    long a2) throws Throwable {
      return return_J(targetA3(a0,a1,a2));
    }
    protected float invoke_F(    long a0,    long a1,    long a2) throws Throwable {
      return return_F(targetA3(a0,a1,a2));
    }
    protected double invoke_D(    long a0,    long a1,    long a2) throws Throwable {
      return return_D(targetA3(a0,a1,a2));
    }
  }
static class A4 extends Adapter {
    protected A4(    MethodHandle entryPoint){
      super(entryPoint);
    }
    protected A4(    MethodHandle e,    MethodHandle i,    MethodHandle c,    MethodHandle t){
      super(e,i,c,t);
    }
    protected A4 makeInstance(    MethodHandle e,    MethodHandle i,    MethodHandle c,    MethodHandle t){
      return new A4(e,i,c,t);
    }
    protected Object target(    Object a0,    Object a1,    Object a2,    Object a3) throws Throwable {
      return invoker.invokeExact(target,a0,a1,a2,a3);
    }
    protected Object targetA4(    Object a0,    Object a1,    Object a2,    Object a3) throws Throwable {
      return target(a0,a1,a2,a3);
    }
    protected Object targetA4(    Object a0,    Object a1,    Object a2,    int a3) throws Throwable {
      return target(a0,a1,a2,a3);
    }
    protected Object targetA4(    Object a0,    Object a1,    int a2,    int a3) throws Throwable {
      return target(a0,a1,a2,a3);
    }
    protected Object targetA4(    Object a0,    int a1,    int a2,    int a3) throws Throwable {
      return target(a0,a1,a2,a3);
    }
    protected Object targetA4(    int a0,    int a1,    int a2,    int a3) throws Throwable {
      return target(a0,a1,a2,a3);
    }
    protected Object targetA4(    Object a0,    Object a1,    Object a2,    long a3) throws Throwable {
      return target(a0,a1,a2,a3);
    }
    protected Object targetA4(    Object a0,    Object a1,    long a2,    long a3) throws Throwable {
      return target(a0,a1,a2,a3);
    }
    protected Object targetA4(    Object a0,    long a1,    long a2,    long a3) throws Throwable {
      return target(a0,a1,a2,a3);
    }
    protected Object targetA4(    long a0,    long a1,    long a2,    long a3) throws Throwable {
      return target(a0,a1,a2,a3);
    }
    protected Object invoke_L(    Object a0,    Object a1,    Object a2,    Object a3) throws Throwable {
      return return_L(targetA4(a0,a1,a2,a3));
    }
    protected int invoke_I(    Object a0,    Object a1,    Object a2,    Object a3) throws Throwable {
      return return_I(targetA4(a0,a1,a2,a3));
    }
    protected long invoke_J(    Object a0,    Object a1,    Object a2,    Object a3) throws Throwable {
      return return_J(targetA4(a0,a1,a2,a3));
    }
    protected float invoke_F(    Object a0,    Object a1,    Object a2,    Object a3) throws Throwable {
      return return_F(targetA4(a0,a1,a2,a3));
    }
    protected double invoke_D(    Object a0,    Object a1,    Object a2,    Object a3) throws Throwable {
      return return_D(targetA4(a0,a1,a2,a3));
    }
    protected Object invoke_L(    Object a0,    Object a1,    Object a2,    int a3) throws Throwable {
      return return_L(targetA4(a0,a1,a2,a3));
    }
    protected int invoke_I(    Object a0,    Object a1,    Object a2,    int a3) throws Throwable {
      return return_I(targetA4(a0,a1,a2,a3));
    }
    protected long invoke_J(    Object a0,    Object a1,    Object a2,    int a3) throws Throwable {
      return return_J(targetA4(a0,a1,a2,a3));
    }
    protected float invoke_F(    Object a0,    Object a1,    Object a2,    int a3) throws Throwable {
      return return_F(targetA4(a0,a1,a2,a3));
    }
    protected double invoke_D(    Object a0,    Object a1,    Object a2,    int a3) throws Throwable {
      return return_D(targetA4(a0,a1,a2,a3));
    }
    protected Object invoke_L(    Object a0,    Object a1,    int a2,    int a3) throws Throwable {
      return return_L(targetA4(a0,a1,a2,a3));
    }
    protected int invoke_I(    Object a0,    Object a1,    int a2,    int a3) throws Throwable {
      return return_I(targetA4(a0,a1,a2,a3));
    }
    protected long invoke_J(    Object a0,    Object a1,    int a2,    int a3) throws Throwable {
      return return_J(targetA4(a0,a1,a2,a3));
    }
    protected float invoke_F(    Object a0,    Object a1,    int a2,    int a3) throws Throwable {
      return return_F(targetA4(a0,a1,a2,a3));
    }
    protected double invoke_D(    Object a0,    Object a1,    int a2,    int a3) throws Throwable {
      return return_D(targetA4(a0,a1,a2,a3));
    }
    protected Object invoke_L(    Object a0,    int a1,    int a2,    int a3) throws Throwable {
      return return_L(targetA4(a0,a1,a2,a3));
    }
    protected int invoke_I(    Object a0,    int a1,    int a2,    int a3) throws Throwable {
      return return_I(targetA4(a0,a1,a2,a3));
    }
    protected long invoke_J(    Object a0,    int a1,    int a2,    int a3) throws Throwable {
      return return_J(targetA4(a0,a1,a2,a3));
    }
    protected float invoke_F(    Object a0,    int a1,    int a2,    int a3) throws Throwable {
      return return_F(targetA4(a0,a1,a2,a3));
    }
    protected double invoke_D(    Object a0,    int a1,    int a2,    int a3) throws Throwable {
      return return_D(targetA4(a0,a1,a2,a3));
    }
    protected Object invoke_L(    int a0,    int a1,    int a2,    int a3) throws Throwable {
      return return_L(targetA4(a0,a1,a2,a3));
    }
    protected int invoke_I(    int a0,    int a1,    int a2,    int a3) throws Throwable {
      return return_I(targetA4(a0,a1,a2,a3));
    }
    protected long invoke_J(    int a0,    int a1,    int a2,    int a3) throws Throwable {
      return return_J(targetA4(a0,a1,a2,a3));
    }
    protected float invoke_F(    int a0,    int a1,    int a2,    int a3) throws Throwable {
      return return_F(targetA4(a0,a1,a2,a3));
    }
    protected double invoke_D(    int a0,    int a1,    int a2,    int a3) throws Throwable {
      return return_D(targetA4(a0,a1,a2,a3));
    }
    protected Object invoke_L(    Object a0,    Object a1,    Object a2,    long a3) throws Throwable {
      return return_L(targetA4(a0,a1,a2,a3));
    }
    protected int invoke_I(    Object a0,    Object a1,    Object a2,    long a3) throws Throwable {
      return return_I(targetA4(a0,a1,a2,a3));
    }
    protected long invoke_J(    Object a0,    Object a1,    Object a2,    long a3) throws Throwable {
      return return_J(targetA4(a0,a1,a2,a3));
    }
    protected float invoke_F(    Object a0,    Object a1,    Object a2,    long a3) throws Throwable {
      return return_F(targetA4(a0,a1,a2,a3));
    }
    protected double invoke_D(    Object a0,    Object a1,    Object a2,    long a3) throws Throwable {
      return return_D(targetA4(a0,a1,a2,a3));
    }
    protected Object invoke_L(    Object a0,    Object a1,    long a2,    long a3) throws Throwable {
      return return_L(targetA4(a0,a1,a2,a3));
    }
    protected int invoke_I(    Object a0,    Object a1,    long a2,    long a3) throws Throwable {
      return return_I(targetA4(a0,a1,a2,a3));
    }
    protected long invoke_J(    Object a0,    Object a1,    long a2,    long a3) throws Throwable {
      return return_J(targetA4(a0,a1,a2,a3));
    }
    protected float invoke_F(    Object a0,    Object a1,    long a2,    long a3) throws Throwable {
      return return_F(targetA4(a0,a1,a2,a3));
    }
    protected double invoke_D(    Object a0,    Object a1,    long a2,    long a3) throws Throwable {
      return return_D(targetA4(a0,a1,a2,a3));
    }
    protected Object invoke_L(    Object a0,    long a1,    long a2,    long a3) throws Throwable {
      return return_L(targetA4(a0,a1,a2,a3));
    }
    protected int invoke_I(    Object a0,    long a1,    long a2,    long a3) throws Throwable {
      return return_I(targetA4(a0,a1,a2,a3));
    }
    protected long invoke_J(    Object a0,    long a1,    long a2,    long a3) throws Throwable {
      return return_J(targetA4(a0,a1,a2,a3));
    }
    protected float invoke_F(    Object a0,    long a1,    long a2,    long a3) throws Throwable {
      return return_F(targetA4(a0,a1,a2,a3));
    }
    protected double invoke_D(    Object a0,    long a1,    long a2,    long a3) throws Throwable {
      return return_D(targetA4(a0,a1,a2,a3));
    }
    protected Object invoke_L(    long a0,    long a1,    long a2,    long a3) throws Throwable {
      return return_L(targetA4(a0,a1,a2,a3));
    }
    protected int invoke_I(    long a0,    long a1,    long a2,    long a3) throws Throwable {
      return return_I(targetA4(a0,a1,a2,a3));
    }
    protected long invoke_J(    long a0,    long a1,    long a2,    long a3) throws Throwable {
      return return_J(targetA4(a0,a1,a2,a3));
    }
    protected float invoke_F(    long a0,    long a1,    long a2,    long a3) throws Throwable {
      return return_F(targetA4(a0,a1,a2,a3));
    }
    protected double invoke_D(    long a0,    long a1,    long a2,    long a3) throws Throwable {
      return return_D(targetA4(a0,a1,a2,a3));
    }
  }
static class A5 extends Adapter {
    protected A5(    MethodHandle entryPoint){
      super(entryPoint);
    }
    protected A5(    MethodHandle e,    MethodHandle i,    MethodHandle c,    MethodHandle t){
      super(e,i,c,t);
    }
    protected A5 makeInstance(    MethodHandle e,    MethodHandle i,    MethodHandle c,    MethodHandle t){
      return new A5(e,i,c,t);
    }
    protected Object target(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4) throws Throwable {
      return invoker.invokeExact(target,a0,a1,a2,a3,a4);
    }
    protected Object targetA5(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4) throws Throwable {
      return target(a0,a1,a2,a3,a4);
    }
    protected Object targetA5(    Object a0,    Object a1,    Object a2,    Object a3,    int a4) throws Throwable {
      return target(a0,a1,a2,a3,a4);
    }
    protected Object targetA5(    Object a0,    Object a1,    Object a2,    int a3,    int a4) throws Throwable {
      return target(a0,a1,a2,a3,a4);
    }
    protected Object targetA5(    Object a0,    Object a1,    int a2,    int a3,    int a4) throws Throwable {
      return target(a0,a1,a2,a3,a4);
    }
    protected Object targetA5(    Object a0,    int a1,    int a2,    int a3,    int a4) throws Throwable {
      return target(a0,a1,a2,a3,a4);
    }
    protected Object targetA5(    int a0,    int a1,    int a2,    int a3,    int a4) throws Throwable {
      return target(a0,a1,a2,a3,a4);
    }
    protected Object targetA5(    Object a0,    Object a1,    Object a2,    Object a3,    long a4) throws Throwable {
      return target(a0,a1,a2,a3,a4);
    }
    protected Object targetA5(    Object a0,    Object a1,    Object a2,    long a3,    long a4) throws Throwable {
      return target(a0,a1,a2,a3,a4);
    }
    protected Object targetA5(    Object a0,    Object a1,    long a2,    long a3,    long a4) throws Throwable {
      return target(a0,a1,a2,a3,a4);
    }
    protected Object targetA5(    Object a0,    long a1,    long a2,    long a3,    long a4) throws Throwable {
      return target(a0,a1,a2,a3,a4);
    }
    protected Object targetA5(    long a0,    long a1,    long a2,    long a3,    long a4) throws Throwable {
      return target(a0,a1,a2,a3,a4);
    }
    protected Object invoke_L(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4) throws Throwable {
      return return_L(targetA5(a0,a1,a2,a3,a4));
    }
    protected int invoke_I(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4) throws Throwable {
      return return_I(targetA5(a0,a1,a2,a3,a4));
    }
    protected long invoke_J(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4) throws Throwable {
      return return_J(targetA5(a0,a1,a2,a3,a4));
    }
    protected Object invoke_L(    Object a0,    Object a1,    Object a2,    Object a3,    int a4) throws Throwable {
      return return_L(targetA5(a0,a1,a2,a3,a4));
    }
    protected int invoke_I(    Object a0,    Object a1,    Object a2,    Object a3,    int a4) throws Throwable {
      return return_I(targetA5(a0,a1,a2,a3,a4));
    }
    protected long invoke_J(    Object a0,    Object a1,    Object a2,    Object a3,    int a4) throws Throwable {
      return return_J(targetA5(a0,a1,a2,a3,a4));
    }
    protected Object invoke_L(    Object a0,    Object a1,    Object a2,    int a3,    int a4) throws Throwable {
      return return_L(targetA5(a0,a1,a2,a3,a4));
    }
    protected int invoke_I(    Object a0,    Object a1,    Object a2,    int a3,    int a4) throws Throwable {
      return return_I(targetA5(a0,a1,a2,a3,a4));
    }
    protected long invoke_J(    Object a0,    Object a1,    Object a2,    int a3,    int a4) throws Throwable {
      return return_J(targetA5(a0,a1,a2,a3,a4));
    }
    protected Object invoke_L(    Object a0,    Object a1,    int a2,    int a3,    int a4) throws Throwable {
      return return_L(targetA5(a0,a1,a2,a3,a4));
    }
    protected int invoke_I(    Object a0,    Object a1,    int a2,    int a3,    int a4) throws Throwable {
      return return_I(targetA5(a0,a1,a2,a3,a4));
    }
    protected long invoke_J(    Object a0,    Object a1,    int a2,    int a3,    int a4) throws Throwable {
      return return_J(targetA5(a0,a1,a2,a3,a4));
    }
    protected Object invoke_L(    Object a0,    int a1,    int a2,    int a3,    int a4) throws Throwable {
      return return_L(targetA5(a0,a1,a2,a3,a4));
    }
    protected int invoke_I(    Object a0,    int a1,    int a2,    int a3,    int a4) throws Throwable {
      return return_I(targetA5(a0,a1,a2,a3,a4));
    }
    protected long invoke_J(    Object a0,    int a1,    int a2,    int a3,    int a4) throws Throwable {
      return return_J(targetA5(a0,a1,a2,a3,a4));
    }
    protected Object invoke_L(    int a0,    int a1,    int a2,    int a3,    int a4) throws Throwable {
      return return_L(targetA5(a0,a1,a2,a3,a4));
    }
    protected int invoke_I(    int a0,    int a1,    int a2,    int a3,    int a4) throws Throwable {
      return return_I(targetA5(a0,a1,a2,a3,a4));
    }
    protected long invoke_J(    int a0,    int a1,    int a2,    int a3,    int a4) throws Throwable {
      return return_J(targetA5(a0,a1,a2,a3,a4));
    }
    protected Object invoke_L(    Object a0,    Object a1,    Object a2,    Object a3,    long a4) throws Throwable {
      return return_L(targetA5(a0,a1,a2,a3,a4));
    }
    protected int invoke_I(    Object a0,    Object a1,    Object a2,    Object a3,    long a4) throws Throwable {
      return return_I(targetA5(a0,a1,a2,a3,a4));
    }
    protected long invoke_J(    Object a0,    Object a1,    Object a2,    Object a3,    long a4) throws Throwable {
      return return_J(targetA5(a0,a1,a2,a3,a4));
    }
    protected Object invoke_L(    Object a0,    Object a1,    Object a2,    long a3,    long a4) throws Throwable {
      return return_L(targetA5(a0,a1,a2,a3,a4));
    }
    protected int invoke_I(    Object a0,    Object a1,    Object a2,    long a3,    long a4) throws Throwable {
      return return_I(targetA5(a0,a1,a2,a3,a4));
    }
    protected long invoke_J(    Object a0,    Object a1,    Object a2,    long a3,    long a4) throws Throwable {
      return return_J(targetA5(a0,a1,a2,a3,a4));
    }
    protected Object invoke_L(    Object a0,    Object a1,    long a2,    long a3,    long a4) throws Throwable {
      return return_L(targetA5(a0,a1,a2,a3,a4));
    }
    protected int invoke_I(    Object a0,    Object a1,    long a2,    long a3,    long a4) throws Throwable {
      return return_I(targetA5(a0,a1,a2,a3,a4));
    }
    protected long invoke_J(    Object a0,    Object a1,    long a2,    long a3,    long a4) throws Throwable {
      return return_J(targetA5(a0,a1,a2,a3,a4));
    }
    protected Object invoke_L(    Object a0,    long a1,    long a2,    long a3,    long a4) throws Throwable {
      return return_L(targetA5(a0,a1,a2,a3,a4));
    }
    protected int invoke_I(    Object a0,    long a1,    long a2,    long a3,    long a4) throws Throwable {
      return return_I(targetA5(a0,a1,a2,a3,a4));
    }
    protected long invoke_J(    Object a0,    long a1,    long a2,    long a3,    long a4) throws Throwable {
      return return_J(targetA5(a0,a1,a2,a3,a4));
    }
    protected Object invoke_L(    long a0,    long a1,    long a2,    long a3,    long a4) throws Throwable {
      return return_L(targetA5(a0,a1,a2,a3,a4));
    }
    protected int invoke_I(    long a0,    long a1,    long a2,    long a3,    long a4) throws Throwable {
      return return_I(targetA5(a0,a1,a2,a3,a4));
    }
    protected long invoke_J(    long a0,    long a1,    long a2,    long a3,    long a4) throws Throwable {
      return return_J(targetA5(a0,a1,a2,a3,a4));
    }
  }
static class A6 extends Adapter {
    protected A6(    MethodHandle entryPoint){
      super(entryPoint);
    }
    protected A6(    MethodHandle e,    MethodHandle i,    MethodHandle c,    MethodHandle t){
      super(e,i,c,t);
    }
    protected A6 makeInstance(    MethodHandle e,    MethodHandle i,    MethodHandle c,    MethodHandle t){
      return new A6(e,i,c,t);
    }
    protected Object target(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5) throws Throwable {
      return invoker.invokeExact(target,a0,a1,a2,a3,a4,a5);
    }
    protected Object targetA6(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5) throws Throwable {
      return target(a0,a1,a2,a3,a4,a5);
    }
    protected Object targetA6(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    long a5) throws Throwable {
      return target(a0,a1,a2,a3,a4,a5);
    }
    protected Object targetA6(    Object a0,    Object a1,    Object a2,    Object a3,    long a4,    long a5) throws Throwable {
      return target(a0,a1,a2,a3,a4,a5);
    }
    protected Object targetA6(    Object a0,    Object a1,    Object a2,    long a3,    long a4,    long a5) throws Throwable {
      return target(a0,a1,a2,a3,a4,a5);
    }
    protected Object targetA6(    Object a0,    Object a1,    long a2,    long a3,    long a4,    long a5) throws Throwable {
      return target(a0,a1,a2,a3,a4,a5);
    }
    protected Object targetA6(    Object a0,    long a1,    long a2,    long a3,    long a4,    long a5) throws Throwable {
      return target(a0,a1,a2,a3,a4,a5);
    }
    protected Object targetA6(    long a0,    long a1,    long a2,    long a3,    long a4,    long a5) throws Throwable {
      return target(a0,a1,a2,a3,a4,a5);
    }
    protected Object invoke_L(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5) throws Throwable {
      return return_L(targetA6(a0,a1,a2,a3,a4,a5));
    }
    protected int invoke_I(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5) throws Throwable {
      return return_I(targetA6(a0,a1,a2,a3,a4,a5));
    }
    protected long invoke_J(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5) throws Throwable {
      return return_J(targetA6(a0,a1,a2,a3,a4,a5));
    }
    protected Object invoke_L(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    long a5) throws Throwable {
      return return_L(targetA6(a0,a1,a2,a3,a4,a5));
    }
    protected int invoke_I(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    long a5) throws Throwable {
      return return_I(targetA6(a0,a1,a2,a3,a4,a5));
    }
    protected long invoke_J(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    long a5) throws Throwable {
      return return_J(targetA6(a0,a1,a2,a3,a4,a5));
    }
    protected Object invoke_L(    Object a0,    Object a1,    Object a2,    Object a3,    long a4,    long a5) throws Throwable {
      return return_L(targetA6(a0,a1,a2,a3,a4,a5));
    }
    protected int invoke_I(    Object a0,    Object a1,    Object a2,    Object a3,    long a4,    long a5) throws Throwable {
      return return_I(targetA6(a0,a1,a2,a3,a4,a5));
    }
    protected long invoke_J(    Object a0,    Object a1,    Object a2,    Object a3,    long a4,    long a5) throws Throwable {
      return return_J(targetA6(a0,a1,a2,a3,a4,a5));
    }
    protected Object invoke_L(    Object a0,    Object a1,    Object a2,    long a3,    long a4,    long a5) throws Throwable {
      return return_L(targetA6(a0,a1,a2,a3,a4,a5));
    }
    protected int invoke_I(    Object a0,    Object a1,    Object a2,    long a3,    long a4,    long a5) throws Throwable {
      return return_I(targetA6(a0,a1,a2,a3,a4,a5));
    }
    protected long invoke_J(    Object a0,    Object a1,    Object a2,    long a3,    long a4,    long a5) throws Throwable {
      return return_J(targetA6(a0,a1,a2,a3,a4,a5));
    }
    protected Object invoke_L(    Object a0,    Object a1,    long a2,    long a3,    long a4,    long a5) throws Throwable {
      return return_L(targetA6(a0,a1,a2,a3,a4,a5));
    }
    protected int invoke_I(    Object a0,    Object a1,    long a2,    long a3,    long a4,    long a5) throws Throwable {
      return return_I(targetA6(a0,a1,a2,a3,a4,a5));
    }
    protected long invoke_J(    Object a0,    Object a1,    long a2,    long a3,    long a4,    long a5) throws Throwable {
      return return_J(targetA6(a0,a1,a2,a3,a4,a5));
    }
    protected Object invoke_L(    Object a0,    long a1,    long a2,    long a3,    long a4,    long a5) throws Throwable {
      return return_L(targetA6(a0,a1,a2,a3,a4,a5));
    }
    protected int invoke_I(    Object a0,    long a1,    long a2,    long a3,    long a4,    long a5) throws Throwable {
      return return_I(targetA6(a0,a1,a2,a3,a4,a5));
    }
    protected long invoke_J(    Object a0,    long a1,    long a2,    long a3,    long a4,    long a5) throws Throwable {
      return return_J(targetA6(a0,a1,a2,a3,a4,a5));
    }
    protected Object invoke_L(    long a0,    long a1,    long a2,    long a3,    long a4,    long a5) throws Throwable {
      return return_L(targetA6(a0,a1,a2,a3,a4,a5));
    }
    protected int invoke_I(    long a0,    long a1,    long a2,    long a3,    long a4,    long a5) throws Throwable {
      return return_I(targetA6(a0,a1,a2,a3,a4,a5));
    }
    protected long invoke_J(    long a0,    long a1,    long a2,    long a3,    long a4,    long a5) throws Throwable {
      return return_J(targetA6(a0,a1,a2,a3,a4,a5));
    }
  }
static class A7 extends Adapter {
    protected A7(    MethodHandle entryPoint){
      super(entryPoint);
    }
    protected A7(    MethodHandle e,    MethodHandle i,    MethodHandle c,    MethodHandle t){
      super(e,i,c,t);
    }
    protected A7 makeInstance(    MethodHandle e,    MethodHandle i,    MethodHandle c,    MethodHandle t){
      return new A7(e,i,c,t);
    }
    protected Object target(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    Object a6) throws Throwable {
      return invoker.invokeExact(target,a0,a1,a2,a3,a4,a5,a6);
    }
    protected Object targetA7(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    Object a6) throws Throwable {
      return target(a0,a1,a2,a3,a4,a5,a6);
    }
    protected Object targetA7(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    long a6) throws Throwable {
      return target(a0,a1,a2,a3,a4,a5,a6);
    }
    protected Object targetA7(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    long a5,    long a6) throws Throwable {
      return target(a0,a1,a2,a3,a4,a5,a6);
    }
    protected Object targetA7(    Object a0,    Object a1,    Object a2,    Object a3,    long a4,    long a5,    long a6) throws Throwable {
      return target(a0,a1,a2,a3,a4,a5,a6);
    }
    protected Object targetA7(    Object a0,    Object a1,    Object a2,    long a3,    long a4,    long a5,    long a6) throws Throwable {
      return target(a0,a1,a2,a3,a4,a5,a6);
    }
    protected Object targetA7(    Object a0,    Object a1,    long a2,    long a3,    long a4,    long a5,    long a6) throws Throwable {
      return target(a0,a1,a2,a3,a4,a5,a6);
    }
    protected Object targetA7(    Object a0,    long a1,    long a2,    long a3,    long a4,    long a5,    long a6) throws Throwable {
      return target(a0,a1,a2,a3,a4,a5,a6);
    }
    protected Object targetA7(    long a0,    long a1,    long a2,    long a3,    long a4,    long a5,    long a6) throws Throwable {
      return target(a0,a1,a2,a3,a4,a5,a6);
    }
    protected Object invoke_L(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    Object a6) throws Throwable {
      return return_L(targetA7(a0,a1,a2,a3,a4,a5,a6));
    }
    protected int invoke_I(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    Object a6) throws Throwable {
      return return_I(targetA7(a0,a1,a2,a3,a4,a5,a6));
    }
    protected long invoke_J(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    Object a6) throws Throwable {
      return return_J(targetA7(a0,a1,a2,a3,a4,a5,a6));
    }
    protected Object invoke_L(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    long a6) throws Throwable {
      return return_L(targetA7(a0,a1,a2,a3,a4,a5,a6));
    }
    protected int invoke_I(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    long a6) throws Throwable {
      return return_I(targetA7(a0,a1,a2,a3,a4,a5,a6));
    }
    protected long invoke_J(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    long a6) throws Throwable {
      return return_J(targetA7(a0,a1,a2,a3,a4,a5,a6));
    }
    protected Object invoke_L(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    long a5,    long a6) throws Throwable {
      return return_L(targetA7(a0,a1,a2,a3,a4,a5,a6));
    }
    protected int invoke_I(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    long a5,    long a6) throws Throwable {
      return return_I(targetA7(a0,a1,a2,a3,a4,a5,a6));
    }
    protected long invoke_J(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    long a5,    long a6) throws Throwable {
      return return_J(targetA7(a0,a1,a2,a3,a4,a5,a6));
    }
    protected Object invoke_L(    Object a0,    Object a1,    Object a2,    Object a3,    long a4,    long a5,    long a6) throws Throwable {
      return return_L(targetA7(a0,a1,a2,a3,a4,a5,a6));
    }
    protected int invoke_I(    Object a0,    Object a1,    Object a2,    Object a3,    long a4,    long a5,    long a6) throws Throwable {
      return return_I(targetA7(a0,a1,a2,a3,a4,a5,a6));
    }
    protected long invoke_J(    Object a0,    Object a1,    Object a2,    Object a3,    long a4,    long a5,    long a6) throws Throwable {
      return return_J(targetA7(a0,a1,a2,a3,a4,a5,a6));
    }
    protected Object invoke_L(    Object a0,    Object a1,    Object a2,    long a3,    long a4,    long a5,    long a6) throws Throwable {
      return return_L(targetA7(a0,a1,a2,a3,a4,a5,a6));
    }
    protected int invoke_I(    Object a0,    Object a1,    Object a2,    long a3,    long a4,    long a5,    long a6) throws Throwable {
      return return_I(targetA7(a0,a1,a2,a3,a4,a5,a6));
    }
    protected long invoke_J(    Object a0,    Object a1,    Object a2,    long a3,    long a4,    long a5,    long a6) throws Throwable {
      return return_J(targetA7(a0,a1,a2,a3,a4,a5,a6));
    }
    protected Object invoke_L(    Object a0,    Object a1,    long a2,    long a3,    long a4,    long a5,    long a6) throws Throwable {
      return return_L(targetA7(a0,a1,a2,a3,a4,a5,a6));
    }
    protected int invoke_I(    Object a0,    Object a1,    long a2,    long a3,    long a4,    long a5,    long a6) throws Throwable {
      return return_I(targetA7(a0,a1,a2,a3,a4,a5,a6));
    }
    protected long invoke_J(    Object a0,    Object a1,    long a2,    long a3,    long a4,    long a5,    long a6) throws Throwable {
      return return_J(targetA7(a0,a1,a2,a3,a4,a5,a6));
    }
    protected Object invoke_L(    Object a0,    long a1,    long a2,    long a3,    long a4,    long a5,    long a6) throws Throwable {
      return return_L(targetA7(a0,a1,a2,a3,a4,a5,a6));
    }
    protected int invoke_I(    Object a0,    long a1,    long a2,    long a3,    long a4,    long a5,    long a6) throws Throwable {
      return return_I(targetA7(a0,a1,a2,a3,a4,a5,a6));
    }
    protected long invoke_J(    Object a0,    long a1,    long a2,    long a3,    long a4,    long a5,    long a6) throws Throwable {
      return return_J(targetA7(a0,a1,a2,a3,a4,a5,a6));
    }
    protected Object invoke_L(    long a0,    long a1,    long a2,    long a3,    long a4,    long a5,    long a6) throws Throwable {
      return return_L(targetA7(a0,a1,a2,a3,a4,a5,a6));
    }
    protected int invoke_I(    long a0,    long a1,    long a2,    long a3,    long a4,    long a5,    long a6) throws Throwable {
      return return_I(targetA7(a0,a1,a2,a3,a4,a5,a6));
    }
    protected long invoke_J(    long a0,    long a1,    long a2,    long a3,    long a4,    long a5,    long a6) throws Throwable {
      return return_J(targetA7(a0,a1,a2,a3,a4,a5,a6));
    }
  }
static class A8 extends Adapter {
    protected A8(    MethodHandle entryPoint){
      super(entryPoint);
    }
    protected A8(    MethodHandle e,    MethodHandle i,    MethodHandle c,    MethodHandle t){
      super(e,i,c,t);
    }
    protected A8 makeInstance(    MethodHandle e,    MethodHandle i,    MethodHandle c,    MethodHandle t){
      return new A8(e,i,c,t);
    }
    protected Object target(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    Object a6,    Object a7) throws Throwable {
      return invoker.invokeExact(target,a0,a1,a2,a3,a4,a5,a6,a7);
    }
    protected Object targetA8(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    Object a6,    Object a7) throws Throwable {
      return target(a0,a1,a2,a3,a4,a5,a6,a7);
    }
    protected Object targetA8(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    Object a6,    long a7) throws Throwable {
      return target(a0,a1,a2,a3,a4,a5,a6,a7);
    }
    protected Object targetA8(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    long a6,    long a7) throws Throwable {
      return target(a0,a1,a2,a3,a4,a5,a6,a7);
    }
    protected Object targetA8(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    long a5,    long a6,    long a7) throws Throwable {
      return target(a0,a1,a2,a3,a4,a5,a6,a7);
    }
    protected Object targetA8(    Object a0,    Object a1,    Object a2,    Object a3,    long a4,    long a5,    long a6,    long a7) throws Throwable {
      return target(a0,a1,a2,a3,a4,a5,a6,a7);
    }
    protected Object targetA8(    Object a0,    Object a1,    Object a2,    long a3,    long a4,    long a5,    long a6,    long a7) throws Throwable {
      return target(a0,a1,a2,a3,a4,a5,a6,a7);
    }
    protected Object targetA8(    Object a0,    Object a1,    long a2,    long a3,    long a4,    long a5,    long a6,    long a7) throws Throwable {
      return target(a0,a1,a2,a3,a4,a5,a6,a7);
    }
    protected Object targetA8(    Object a0,    long a1,    long a2,    long a3,    long a4,    long a5,    long a6,    long a7) throws Throwable {
      return target(a0,a1,a2,a3,a4,a5,a6,a7);
    }
    protected Object targetA8(    long a0,    long a1,    long a2,    long a3,    long a4,    long a5,    long a6,    long a7) throws Throwable {
      return target(a0,a1,a2,a3,a4,a5,a6,a7);
    }
    protected Object invoke_L(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    Object a6,    Object a7) throws Throwable {
      return return_L(targetA8(a0,a1,a2,a3,a4,a5,a6,a7));
    }
    protected int invoke_I(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    Object a6,    Object a7) throws Throwable {
      return return_I(targetA8(a0,a1,a2,a3,a4,a5,a6,a7));
    }
    protected long invoke_J(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    Object a6,    Object a7) throws Throwable {
      return return_J(targetA8(a0,a1,a2,a3,a4,a5,a6,a7));
    }
    protected Object invoke_L(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    Object a6,    long a7) throws Throwable {
      return return_L(targetA8(a0,a1,a2,a3,a4,a5,a6,a7));
    }
    protected int invoke_I(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    Object a6,    long a7) throws Throwable {
      return return_I(targetA8(a0,a1,a2,a3,a4,a5,a6,a7));
    }
    protected long invoke_J(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    Object a6,    long a7) throws Throwable {
      return return_J(targetA8(a0,a1,a2,a3,a4,a5,a6,a7));
    }
    protected Object invoke_L(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    long a6,    long a7) throws Throwable {
      return return_L(targetA8(a0,a1,a2,a3,a4,a5,a6,a7));
    }
    protected int invoke_I(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    long a6,    long a7) throws Throwable {
      return return_I(targetA8(a0,a1,a2,a3,a4,a5,a6,a7));
    }
    protected long invoke_J(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    long a6,    long a7) throws Throwable {
      return return_J(targetA8(a0,a1,a2,a3,a4,a5,a6,a7));
    }
    protected Object invoke_L(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    long a5,    long a6,    long a7) throws Throwable {
      return return_L(targetA8(a0,a1,a2,a3,a4,a5,a6,a7));
    }
    protected int invoke_I(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    long a5,    long a6,    long a7) throws Throwable {
      return return_I(targetA8(a0,a1,a2,a3,a4,a5,a6,a7));
    }
    protected long invoke_J(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    long a5,    long a6,    long a7) throws Throwable {
      return return_J(targetA8(a0,a1,a2,a3,a4,a5,a6,a7));
    }
    protected Object invoke_L(    Object a0,    Object a1,    Object a2,    Object a3,    long a4,    long a5,    long a6,    long a7) throws Throwable {
      return return_L(targetA8(a0,a1,a2,a3,a4,a5,a6,a7));
    }
    protected int invoke_I(    Object a0,    Object a1,    Object a2,    Object a3,    long a4,    long a5,    long a6,    long a7) throws Throwable {
      return return_I(targetA8(a0,a1,a2,a3,a4,a5,a6,a7));
    }
    protected long invoke_J(    Object a0,    Object a1,    Object a2,    Object a3,    long a4,    long a5,    long a6,    long a7) throws Throwable {
      return return_J(targetA8(a0,a1,a2,a3,a4,a5,a6,a7));
    }
    protected Object invoke_L(    Object a0,    Object a1,    Object a2,    long a3,    long a4,    long a5,    long a6,    long a7) throws Throwable {
      return return_L(targetA8(a0,a1,a2,a3,a4,a5,a6,a7));
    }
    protected int invoke_I(    Object a0,    Object a1,    Object a2,    long a3,    long a4,    long a5,    long a6,    long a7) throws Throwable {
      return return_I(targetA8(a0,a1,a2,a3,a4,a5,a6,a7));
    }
    protected long invoke_J(    Object a0,    Object a1,    Object a2,    long a3,    long a4,    long a5,    long a6,    long a7) throws Throwable {
      return return_J(targetA8(a0,a1,a2,a3,a4,a5,a6,a7));
    }
    protected Object invoke_L(    Object a0,    Object a1,    long a2,    long a3,    long a4,    long a5,    long a6,    long a7) throws Throwable {
      return return_L(targetA8(a0,a1,a2,a3,a4,a5,a6,a7));
    }
    protected int invoke_I(    Object a0,    Object a1,    long a2,    long a3,    long a4,    long a5,    long a6,    long a7) throws Throwable {
      return return_I(targetA8(a0,a1,a2,a3,a4,a5,a6,a7));
    }
    protected long invoke_J(    Object a0,    Object a1,    long a2,    long a3,    long a4,    long a5,    long a6,    long a7) throws Throwable {
      return return_J(targetA8(a0,a1,a2,a3,a4,a5,a6,a7));
    }
    protected Object invoke_L(    Object a0,    long a1,    long a2,    long a3,    long a4,    long a5,    long a6,    long a7) throws Throwable {
      return return_L(targetA8(a0,a1,a2,a3,a4,a5,a6,a7));
    }
    protected int invoke_I(    Object a0,    long a1,    long a2,    long a3,    long a4,    long a5,    long a6,    long a7) throws Throwable {
      return return_I(targetA8(a0,a1,a2,a3,a4,a5,a6,a7));
    }
    protected long invoke_J(    Object a0,    long a1,    long a2,    long a3,    long a4,    long a5,    long a6,    long a7) throws Throwable {
      return return_J(targetA8(a0,a1,a2,a3,a4,a5,a6,a7));
    }
    protected Object invoke_L(    long a0,    long a1,    long a2,    long a3,    long a4,    long a5,    long a6,    long a7) throws Throwable {
      return return_L(targetA8(a0,a1,a2,a3,a4,a5,a6,a7));
    }
    protected int invoke_I(    long a0,    long a1,    long a2,    long a3,    long a4,    long a5,    long a6,    long a7) throws Throwable {
      return return_I(targetA8(a0,a1,a2,a3,a4,a5,a6,a7));
    }
    protected long invoke_J(    long a0,    long a1,    long a2,    long a3,    long a4,    long a5,    long a6,    long a7) throws Throwable {
      return return_J(targetA8(a0,a1,a2,a3,a4,a5,a6,a7));
    }
  }
static class A9 extends Adapter {
    protected A9(    MethodHandle entryPoint){
      super(entryPoint);
    }
    protected A9(    MethodHandle e,    MethodHandle i,    MethodHandle c,    MethodHandle t){
      super(e,i,c,t);
    }
    protected A9 makeInstance(    MethodHandle e,    MethodHandle i,    MethodHandle c,    MethodHandle t){
      return new A9(e,i,c,t);
    }
    protected Object target(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    Object a6,    Object a7,    Object a8) throws Throwable {
      return invoker.invokeExact(target,a0,a1,a2,a3,a4,a5,a6,a7,a8);
    }
    protected Object targetA9(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    Object a6,    Object a7,    Object a8) throws Throwable {
      return target(a0,a1,a2,a3,a4,a5,a6,a7,a8);
    }
    protected Object targetA9(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    Object a6,    Object a7,    long a8) throws Throwable {
      return target(a0,a1,a2,a3,a4,a5,a6,a7,a8);
    }
    protected Object targetA9(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    Object a6,    long a7,    long a8) throws Throwable {
      return target(a0,a1,a2,a3,a4,a5,a6,a7,a8);
    }
    protected Object targetA9(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    long a6,    long a7,    long a8) throws Throwable {
      return target(a0,a1,a2,a3,a4,a5,a6,a7,a8);
    }
    protected Object targetA9(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    long a5,    long a6,    long a7,    long a8) throws Throwable {
      return target(a0,a1,a2,a3,a4,a5,a6,a7,a8);
    }
    protected Object targetA9(    Object a0,    Object a1,    Object a2,    Object a3,    long a4,    long a5,    long a6,    long a7,    long a8) throws Throwable {
      return target(a0,a1,a2,a3,a4,a5,a6,a7,a8);
    }
    protected Object targetA9(    Object a0,    Object a1,    Object a2,    long a3,    long a4,    long a5,    long a6,    long a7,    long a8) throws Throwable {
      return target(a0,a1,a2,a3,a4,a5,a6,a7,a8);
    }
    protected Object targetA9(    Object a0,    Object a1,    long a2,    long a3,    long a4,    long a5,    long a6,    long a7,    long a8) throws Throwable {
      return target(a0,a1,a2,a3,a4,a5,a6,a7,a8);
    }
    protected Object targetA9(    Object a0,    long a1,    long a2,    long a3,    long a4,    long a5,    long a6,    long a7,    long a8) throws Throwable {
      return target(a0,a1,a2,a3,a4,a5,a6,a7,a8);
    }
    protected Object targetA9(    long a0,    long a1,    long a2,    long a3,    long a4,    long a5,    long a6,    long a7,    long a8) throws Throwable {
      return target(a0,a1,a2,a3,a4,a5,a6,a7,a8);
    }
    protected Object invoke_L(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    Object a6,    Object a7,    Object a8) throws Throwable {
      return return_L(targetA9(a0,a1,a2,a3,a4,a5,a6,a7,a8));
    }
    protected int invoke_I(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    Object a6,    Object a7,    Object a8) throws Throwable {
      return return_I(targetA9(a0,a1,a2,a3,a4,a5,a6,a7,a8));
    }
    protected long invoke_J(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    Object a6,    Object a7,    Object a8) throws Throwable {
      return return_J(targetA9(a0,a1,a2,a3,a4,a5,a6,a7,a8));
    }
    protected Object invoke_L(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    Object a6,    Object a7,    long a8) throws Throwable {
      return return_L(targetA9(a0,a1,a2,a3,a4,a5,a6,a7,a8));
    }
    protected int invoke_I(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    Object a6,    Object a7,    long a8) throws Throwable {
      return return_I(targetA9(a0,a1,a2,a3,a4,a5,a6,a7,a8));
    }
    protected long invoke_J(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    Object a6,    Object a7,    long a8) throws Throwable {
      return return_J(targetA9(a0,a1,a2,a3,a4,a5,a6,a7,a8));
    }
    protected Object invoke_L(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    Object a6,    long a7,    long a8) throws Throwable {
      return return_L(targetA9(a0,a1,a2,a3,a4,a5,a6,a7,a8));
    }
    protected int invoke_I(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    Object a6,    long a7,    long a8) throws Throwable {
      return return_I(targetA9(a0,a1,a2,a3,a4,a5,a6,a7,a8));
    }
    protected long invoke_J(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    Object a6,    long a7,    long a8) throws Throwable {
      return return_J(targetA9(a0,a1,a2,a3,a4,a5,a6,a7,a8));
    }
    protected Object invoke_L(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    long a6,    long a7,    long a8) throws Throwable {
      return return_L(targetA9(a0,a1,a2,a3,a4,a5,a6,a7,a8));
    }
    protected int invoke_I(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    long a6,    long a7,    long a8) throws Throwable {
      return return_I(targetA9(a0,a1,a2,a3,a4,a5,a6,a7,a8));
    }
    protected long invoke_J(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    long a6,    long a7,    long a8) throws Throwable {
      return return_J(targetA9(a0,a1,a2,a3,a4,a5,a6,a7,a8));
    }
    protected Object invoke_L(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    long a5,    long a6,    long a7,    long a8) throws Throwable {
      return return_L(targetA9(a0,a1,a2,a3,a4,a5,a6,a7,a8));
    }
    protected int invoke_I(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    long a5,    long a6,    long a7,    long a8) throws Throwable {
      return return_I(targetA9(a0,a1,a2,a3,a4,a5,a6,a7,a8));
    }
    protected long invoke_J(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    long a5,    long a6,    long a7,    long a8) throws Throwable {
      return return_J(targetA9(a0,a1,a2,a3,a4,a5,a6,a7,a8));
    }
    protected Object invoke_L(    Object a0,    Object a1,    Object a2,    Object a3,    long a4,    long a5,    long a6,    long a7,    long a8) throws Throwable {
      return return_L(targetA9(a0,a1,a2,a3,a4,a5,a6,a7,a8));
    }
    protected int invoke_I(    Object a0,    Object a1,    Object a2,    Object a3,    long a4,    long a5,    long a6,    long a7,    long a8) throws Throwable {
      return return_I(targetA9(a0,a1,a2,a3,a4,a5,a6,a7,a8));
    }
    protected long invoke_J(    Object a0,    Object a1,    Object a2,    Object a3,    long a4,    long a5,    long a6,    long a7,    long a8) throws Throwable {
      return return_J(targetA9(a0,a1,a2,a3,a4,a5,a6,a7,a8));
    }
    protected Object invoke_L(    Object a0,    Object a1,    Object a2,    long a3,    long a4,    long a5,    long a6,    long a7,    long a8) throws Throwable {
      return return_L(targetA9(a0,a1,a2,a3,a4,a5,a6,a7,a8));
    }
    protected int invoke_I(    Object a0,    Object a1,    Object a2,    long a3,    long a4,    long a5,    long a6,    long a7,    long a8) throws Throwable {
      return return_I(targetA9(a0,a1,a2,a3,a4,a5,a6,a7,a8));
    }
    protected long invoke_J(    Object a0,    Object a1,    Object a2,    long a3,    long a4,    long a5,    long a6,    long a7,    long a8) throws Throwable {
      return return_J(targetA9(a0,a1,a2,a3,a4,a5,a6,a7,a8));
    }
    protected Object invoke_L(    Object a0,    Object a1,    long a2,    long a3,    long a4,    long a5,    long a6,    long a7,    long a8) throws Throwable {
      return return_L(targetA9(a0,a1,a2,a3,a4,a5,a6,a7,a8));
    }
    protected int invoke_I(    Object a0,    Object a1,    long a2,    long a3,    long a4,    long a5,    long a6,    long a7,    long a8) throws Throwable {
      return return_I(targetA9(a0,a1,a2,a3,a4,a5,a6,a7,a8));
    }
    protected long invoke_J(    Object a0,    Object a1,    long a2,    long a3,    long a4,    long a5,    long a6,    long a7,    long a8) throws Throwable {
      return return_J(targetA9(a0,a1,a2,a3,a4,a5,a6,a7,a8));
    }
    protected Object invoke_L(    Object a0,    long a1,    long a2,    long a3,    long a4,    long a5,    long a6,    long a7,    long a8) throws Throwable {
      return return_L(targetA9(a0,a1,a2,a3,a4,a5,a6,a7,a8));
    }
    protected int invoke_I(    Object a0,    long a1,    long a2,    long a3,    long a4,    long a5,    long a6,    long a7,    long a8) throws Throwable {
      return return_I(targetA9(a0,a1,a2,a3,a4,a5,a6,a7,a8));
    }
    protected long invoke_J(    Object a0,    long a1,    long a2,    long a3,    long a4,    long a5,    long a6,    long a7,    long a8) throws Throwable {
      return return_J(targetA9(a0,a1,a2,a3,a4,a5,a6,a7,a8));
    }
    protected Object invoke_L(    long a0,    long a1,    long a2,    long a3,    long a4,    long a5,    long a6,    long a7,    long a8) throws Throwable {
      return return_L(targetA9(a0,a1,a2,a3,a4,a5,a6,a7,a8));
    }
    protected int invoke_I(    long a0,    long a1,    long a2,    long a3,    long a4,    long a5,    long a6,    long a7,    long a8) throws Throwable {
      return return_I(targetA9(a0,a1,a2,a3,a4,a5,a6,a7,a8));
    }
    protected long invoke_J(    long a0,    long a1,    long a2,    long a3,    long a4,    long a5,    long a6,    long a7,    long a8) throws Throwable {
      return return_J(targetA9(a0,a1,a2,a3,a4,a5,a6,a7,a8));
    }
  }
static class A10 extends Adapter {
    protected A10(    MethodHandle entryPoint){
      super(entryPoint);
    }
    protected A10(    MethodHandle e,    MethodHandle i,    MethodHandle c,    MethodHandle t){
      super(e,i,c,t);
    }
    protected A10 makeInstance(    MethodHandle e,    MethodHandle i,    MethodHandle c,    MethodHandle t){
      return new A10(e,i,c,t);
    }
    protected Object target(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    Object a6,    Object a7,    Object a8,    Object a9) throws Throwable {
      return invoker.invokeExact(target,a0,a1,a2,a3,a4,a5,a6,a7,a8,a9);
    }
    protected Object targetA10(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    Object a6,    Object a7,    Object a8,    Object a9) throws Throwable {
      return target(a0,a1,a2,a3,a4,a5,a6,a7,a8,a9);
    }
    protected Object targetA10(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    Object a6,    Object a7,    Object a8,    long a9) throws Throwable {
      return target(a0,a1,a2,a3,a4,a5,a6,a7,a8,a9);
    }
    protected Object targetA10(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    Object a6,    Object a7,    long a8,    long a9) throws Throwable {
      return target(a0,a1,a2,a3,a4,a5,a6,a7,a8,a9);
    }
    protected Object targetA10(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    Object a6,    long a7,    long a8,    long a9) throws Throwable {
      return target(a0,a1,a2,a3,a4,a5,a6,a7,a8,a9);
    }
    protected Object targetA10(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    long a6,    long a7,    long a8,    long a9) throws Throwable {
      return target(a0,a1,a2,a3,a4,a5,a6,a7,a8,a9);
    }
    protected Object targetA10(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    long a5,    long a6,    long a7,    long a8,    long a9) throws Throwable {
      return target(a0,a1,a2,a3,a4,a5,a6,a7,a8,a9);
    }
    protected Object targetA10(    Object a0,    Object a1,    Object a2,    Object a3,    long a4,    long a5,    long a6,    long a7,    long a8,    long a9) throws Throwable {
      return target(a0,a1,a2,a3,a4,a5,a6,a7,a8,a9);
    }
    protected Object targetA10(    Object a0,    Object a1,    Object a2,    long a3,    long a4,    long a5,    long a6,    long a7,    long a8,    long a9) throws Throwable {
      return target(a0,a1,a2,a3,a4,a5,a6,a7,a8,a9);
    }
    protected Object targetA10(    Object a0,    Object a1,    long a2,    long a3,    long a4,    long a5,    long a6,    long a7,    long a8,    long a9) throws Throwable {
      return target(a0,a1,a2,a3,a4,a5,a6,a7,a8,a9);
    }
    protected Object targetA10(    Object a0,    long a1,    long a2,    long a3,    long a4,    long a5,    long a6,    long a7,    long a8,    long a9) throws Throwable {
      return target(a0,a1,a2,a3,a4,a5,a6,a7,a8,a9);
    }
    protected Object targetA10(    long a0,    long a1,    long a2,    long a3,    long a4,    long a5,    long a6,    long a7,    long a8,    long a9) throws Throwable {
      return target(a0,a1,a2,a3,a4,a5,a6,a7,a8,a9);
    }
    protected Object invoke_L(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    Object a6,    Object a7,    Object a8,    Object a9) throws Throwable {
      return return_L(targetA10(a0,a1,a2,a3,a4,a5,a6,a7,a8,a9));
    }
    protected int invoke_I(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    Object a6,    Object a7,    Object a8,    Object a9) throws Throwable {
      return return_I(targetA10(a0,a1,a2,a3,a4,a5,a6,a7,a8,a9));
    }
    protected long invoke_J(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    Object a6,    Object a7,    Object a8,    Object a9) throws Throwable {
      return return_J(targetA10(a0,a1,a2,a3,a4,a5,a6,a7,a8,a9));
    }
    protected Object invoke_L(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    Object a6,    Object a7,    Object a8,    long a9) throws Throwable {
      return return_L(targetA10(a0,a1,a2,a3,a4,a5,a6,a7,a8,a9));
    }
    protected int invoke_I(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    Object a6,    Object a7,    Object a8,    long a9) throws Throwable {
      return return_I(targetA10(a0,a1,a2,a3,a4,a5,a6,a7,a8,a9));
    }
    protected long invoke_J(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    Object a6,    Object a7,    Object a8,    long a9) throws Throwable {
      return return_J(targetA10(a0,a1,a2,a3,a4,a5,a6,a7,a8,a9));
    }
    protected Object invoke_L(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    Object a6,    Object a7,    long a8,    long a9) throws Throwable {
      return return_L(targetA10(a0,a1,a2,a3,a4,a5,a6,a7,a8,a9));
    }
    protected int invoke_I(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    Object a6,    Object a7,    long a8,    long a9) throws Throwable {
      return return_I(targetA10(a0,a1,a2,a3,a4,a5,a6,a7,a8,a9));
    }
    protected long invoke_J(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    Object a6,    Object a7,    long a8,    long a9) throws Throwable {
      return return_J(targetA10(a0,a1,a2,a3,a4,a5,a6,a7,a8,a9));
    }
    protected Object invoke_L(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    Object a6,    long a7,    long a8,    long a9) throws Throwable {
      return return_L(targetA10(a0,a1,a2,a3,a4,a5,a6,a7,a8,a9));
    }
    protected int invoke_I(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    Object a6,    long a7,    long a8,    long a9) throws Throwable {
      return return_I(targetA10(a0,a1,a2,a3,a4,a5,a6,a7,a8,a9));
    }
    protected long invoke_J(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    Object a6,    long a7,    long a8,    long a9) throws Throwable {
      return return_J(targetA10(a0,a1,a2,a3,a4,a5,a6,a7,a8,a9));
    }
    protected Object invoke_L(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    long a6,    long a7,    long a8,    long a9) throws Throwable {
      return return_L(targetA10(a0,a1,a2,a3,a4,a5,a6,a7,a8,a9));
    }
    protected int invoke_I(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    long a6,    long a7,    long a8,    long a9) throws Throwable {
      return return_I(targetA10(a0,a1,a2,a3,a4,a5,a6,a7,a8,a9));
    }
    protected long invoke_J(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    long a6,    long a7,    long a8,    long a9) throws Throwable {
      return return_J(targetA10(a0,a1,a2,a3,a4,a5,a6,a7,a8,a9));
    }
    protected Object invoke_L(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    long a5,    long a6,    long a7,    long a8,    long a9) throws Throwable {
      return return_L(targetA10(a0,a1,a2,a3,a4,a5,a6,a7,a8,a9));
    }
    protected int invoke_I(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    long a5,    long a6,    long a7,    long a8,    long a9) throws Throwable {
      return return_I(targetA10(a0,a1,a2,a3,a4,a5,a6,a7,a8,a9));
    }
    protected long invoke_J(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    long a5,    long a6,    long a7,    long a8,    long a9) throws Throwable {
      return return_J(targetA10(a0,a1,a2,a3,a4,a5,a6,a7,a8,a9));
    }
    protected Object invoke_L(    Object a0,    Object a1,    Object a2,    Object a3,    long a4,    long a5,    long a6,    long a7,    long a8,    long a9) throws Throwable {
      return return_L(targetA10(a0,a1,a2,a3,a4,a5,a6,a7,a8,a9));
    }
    protected int invoke_I(    Object a0,    Object a1,    Object a2,    Object a3,    long a4,    long a5,    long a6,    long a7,    long a8,    long a9) throws Throwable {
      return return_I(targetA10(a0,a1,a2,a3,a4,a5,a6,a7,a8,a9));
    }
    protected long invoke_J(    Object a0,    Object a1,    Object a2,    Object a3,    long a4,    long a5,    long a6,    long a7,    long a8,    long a9) throws Throwable {
      return return_J(targetA10(a0,a1,a2,a3,a4,a5,a6,a7,a8,a9));
    }
    protected Object invoke_L(    Object a0,    Object a1,    Object a2,    long a3,    long a4,    long a5,    long a6,    long a7,    long a8,    long a9) throws Throwable {
      return return_L(targetA10(a0,a1,a2,a3,a4,a5,a6,a7,a8,a9));
    }
    protected int invoke_I(    Object a0,    Object a1,    Object a2,    long a3,    long a4,    long a5,    long a6,    long a7,    long a8,    long a9) throws Throwable {
      return return_I(targetA10(a0,a1,a2,a3,a4,a5,a6,a7,a8,a9));
    }
    protected long invoke_J(    Object a0,    Object a1,    Object a2,    long a3,    long a4,    long a5,    long a6,    long a7,    long a8,    long a9) throws Throwable {
      return return_J(targetA10(a0,a1,a2,a3,a4,a5,a6,a7,a8,a9));
    }
    protected Object invoke_L(    Object a0,    Object a1,    long a2,    long a3,    long a4,    long a5,    long a6,    long a7,    long a8,    long a9) throws Throwable {
      return return_L(targetA10(a0,a1,a2,a3,a4,a5,a6,a7,a8,a9));
    }
    protected int invoke_I(    Object a0,    Object a1,    long a2,    long a3,    long a4,    long a5,    long a6,    long a7,    long a8,    long a9) throws Throwable {
      return return_I(targetA10(a0,a1,a2,a3,a4,a5,a6,a7,a8,a9));
    }
    protected long invoke_J(    Object a0,    Object a1,    long a2,    long a3,    long a4,    long a5,    long a6,    long a7,    long a8,    long a9) throws Throwable {
      return return_J(targetA10(a0,a1,a2,a3,a4,a5,a6,a7,a8,a9));
    }
    protected Object invoke_L(    Object a0,    long a1,    long a2,    long a3,    long a4,    long a5,    long a6,    long a7,    long a8,    long a9) throws Throwable {
      return return_L(targetA10(a0,a1,a2,a3,a4,a5,a6,a7,a8,a9));
    }
    protected int invoke_I(    Object a0,    long a1,    long a2,    long a3,    long a4,    long a5,    long a6,    long a7,    long a8,    long a9) throws Throwable {
      return return_I(targetA10(a0,a1,a2,a3,a4,a5,a6,a7,a8,a9));
    }
    protected long invoke_J(    Object a0,    long a1,    long a2,    long a3,    long a4,    long a5,    long a6,    long a7,    long a8,    long a9) throws Throwable {
      return return_J(targetA10(a0,a1,a2,a3,a4,a5,a6,a7,a8,a9));
    }
    protected Object invoke_L(    long a0,    long a1,    long a2,    long a3,    long a4,    long a5,    long a6,    long a7,    long a8,    long a9) throws Throwable {
      return return_L(targetA10(a0,a1,a2,a3,a4,a5,a6,a7,a8,a9));
    }
    protected int invoke_I(    long a0,    long a1,    long a2,    long a3,    long a4,    long a5,    long a6,    long a7,    long a8,    long a9) throws Throwable {
      return return_I(targetA10(a0,a1,a2,a3,a4,a5,a6,a7,a8,a9));
    }
    protected long invoke_J(    long a0,    long a1,    long a2,    long a3,    long a4,    long a5,    long a6,    long a7,    long a8,    long a9) throws Throwable {
      return return_J(targetA10(a0,a1,a2,a3,a4,a5,a6,a7,a8,a9));
    }
  }
}
