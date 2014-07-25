package java.lang.invoke;
import sun.invoke.util.ValueConversions;
import sun.invoke.util.Wrapper;
import java.lang.reflect.*;
import static java.lang.invoke.MethodHandleStatics.*;
import static java.lang.invoke.MethodHandles.Lookup.IMPL_LOOKUP;
/** 
 * Adapters which mediate between incoming calls which are generic
 * and outgoing calls which are not.  Any call can be represented generically
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
class FromGeneric {
  private final MethodType targetType;
  private final MethodType internalType;
  private final Adapter adapter;
  private final MethodHandle entryPoint;
  private final MethodHandle unboxingInvoker;
  private final MethodHandle returnConversion;
  /** 
 * Compute and cache information common to all unboxing adapters
 * that can call out to targets of the erasure-family of the given erased type.
 */
  private FromGeneric(  MethodType targetType){
    this.targetType=targetType;
    MethodType internalType0;
    Adapter ad=findAdapter(internalType0=targetType.erase());
    if (ad != null) {
      this.internalType=internalType0;
      this.adapter=ad;
      this.entryPoint=ad.prototypeEntryPoint();
      this.returnConversion=computeReturnConversion(targetType,internalType0);
      this.unboxingInvoker=computeUnboxingInvoker(targetType,internalType0);
      return;
    }
    MethodType primsAsObj=targetType.form().primArgsAsBoxes();
    MethodType objArgsRawRet=primsAsObj.form().primsAsInts();
    if (objArgsRawRet != targetType)     ad=findAdapter(internalType0=objArgsRawRet);
    if (ad == null) {
      ad=buildAdapterFromBytecodes(internalType0=targetType);
    }
    this.internalType=internalType0;
    this.adapter=ad;
    MethodType tepType=targetType.insertParameterTypes(0,adapter.getClass());
    this.entryPoint=ad.prototypeEntryPoint();
    this.returnConversion=computeReturnConversion(targetType,internalType0);
    this.unboxingInvoker=computeUnboxingInvoker(targetType,internalType0);
  }
static {
    assert (MethodHandleNatives.workaroundWithoutRicochetFrames());
  }
  /** 
 * The typed target will be called according to targetType.
 * The adapter code will in fact see the raw result from internalType,
 * and must box it into an object.  Produce a converter for this.
 */
  private static MethodHandle computeReturnConversion(  MethodType targetType,  MethodType internalType){
    Class<?> tret=targetType.returnType();
    Class<?> iret=internalType.returnType();
    Wrapper wrap=Wrapper.forBasicType(tret);
    if (!iret.isPrimitive()) {
      assert (iret == Object.class);
      return ValueConversions.identity();
    }
 else     if (wrap.primitiveType() == iret) {
      return ValueConversions.box(wrap);
    }
 else {
      assert (tret == double.class ? iret == long.class : iret == int.class);
      return ValueConversions.boxRaw(wrap);
    }
  }
  /** 
 * The typed target will need an exact invocation point; provide it here.
 * The adapter will possibly need to make a slightly different call,
 * so adapt the invoker.  This way, the logic for making up the
 * difference between what the adapter can call and what the target
 * needs can be cached once per type.
 */
  private static MethodHandle computeUnboxingInvoker(  MethodType targetType,  MethodType internalType){
    assert (internalType == internalType.erase());
    MethodHandle invoker=targetType.invokers().exactInvoker();
    MethodType fixArgsType=internalType.changeReturnType(targetType.returnType());
    MethodHandle fixArgs=MethodHandleImpl.convertArguments(invoker,Invokers.invokerType(fixArgsType),invoker.type(),0);
    if (fixArgs == null)     throw new InternalError("bad fixArgs");
    MethodHandle retyper=AdapterMethodHandle.makeRetypeRaw(Invokers.invokerType(internalType),fixArgs);
    if (retyper == null)     throw new InternalError("bad retyper");
    return retyper;
  }
  Adapter makeInstance(  MethodHandle typedTarget){
    MethodType type=typedTarget.type();
    if (type == targetType) {
      return adapter.makeInstance(entryPoint,unboxingInvoker,returnConversion,typedTarget);
    }
    assert (type.erase() == targetType);
    MethodHandle invoker=computeUnboxingInvoker(type,internalType);
    return adapter.makeInstance(entryPoint,invoker,returnConversion,typedTarget);
  }
  /** 
 * Build an adapter of the given generic type, which invokes typedTarget
 * on the incoming arguments, after unboxing as necessary.
 * The return value is boxed if necessary.
 * @param typedTarget the target
 * @return an adapter method handle
 */
  public static MethodHandle make(  MethodHandle typedTarget){
    MethodType type=typedTarget.type();
    if (type == type.generic())     return typedTarget;
    return FromGeneric.of(type).makeInstance(typedTarget);
  }
  /** 
 * Return the adapter information for this type's erasure. 
 */
  static FromGeneric of(  MethodType type){
    MethodTypeForm form=type.form();
    FromGeneric fromGen=form.fromGeneric;
    if (fromGen == null)     form.fromGeneric=fromGen=new FromGeneric(form.erasedType());
    return fromGen;
  }
  public String toString(){
    return "FromGeneric" + targetType;
  }
  static Adapter findAdapter(  MethodType internalType){
    MethodType entryType=internalType.generic();
    MethodTypeForm form=internalType.form();
    Class<?> rtype=internalType.returnType();
    int argc=form.parameterCount();
    int lac=form.longPrimitiveParameterCount();
    int iac=form.primitiveParameterCount() - lac;
    String intsAndLongs=(iac > 0 ? "I" + iac : "") + (lac > 0 ? "J" + lac : "");
    String rawReturn=String.valueOf(Wrapper.forPrimitiveType(rtype).basicTypeChar());
    String cname0=rawReturn + argc;
    String cname1="A" + argc;
    String[] cnames={cname0 + intsAndLongs,cname0,cname1 + intsAndLongs,cname1};
    String iname="invoke_" + cname0 + intsAndLongs;
    for (    String cname : cnames) {
      Class<? extends Adapter> acls=Adapter.findSubClass(cname);
      if (acls == null)       continue;
      MethodHandle entryPoint=null;
      try {
        entryPoint=IMPL_LOOKUP.findSpecial(acls,iname,entryType,acls);
      }
 catch (      ReflectiveOperationException ex) {
      }
      if (entryPoint == null)       continue;
      Constructor<? extends Adapter> ctor=null;
      try {
        ctor=acls.getDeclaredConstructor(MethodHandle.class);
      }
 catch (      NoSuchMethodException ex) {
      }
catch (      SecurityException ex) {
      }
      if (ctor == null)       continue;
      try {
        return ctor.newInstance(entryPoint);
      }
 catch (      IllegalArgumentException ex) {
      }
catch (      InvocationTargetException wex) {
        Throwable ex=wex.getTargetException();
        if (ex instanceof Error)         throw (Error)ex;
        if (ex instanceof RuntimeException)         throw (RuntimeException)ex;
      }
catch (      InstantiationException ex) {
      }
catch (      IllegalAccessException ex) {
      }
    }
    return null;
  }
  static Adapter buildAdapterFromBytecodes(  MethodType internalType){
    throw new UnsupportedOperationException("NYI " + internalType);
  }
  /** 
 * This adapter takes some untyped arguments, and returns an untyped result.
 * Internally, it applies the invoker to the target, which causes the
 * objects to be unboxed; the result is a raw type in L/I/J/F/D.
 * This result is passed to convert, which is responsible for
 * converting the raw result into a boxed object.
 * The invoker is kept separate from the target because it can be
 * generated once per type erasure family, and reused across adapters.
 */
static abstract class Adapter extends BoundMethodHandle {
    protected final MethodHandle invoker;
    protected final MethodHandle convert;
    protected final MethodHandle target;
    @Override String debugString(){
      return addTypeString(target,this);
    }
    protected boolean isPrototype(){
      return target == null;
    }
    protected Adapter(    MethodHandle entryPoint){
      this(entryPoint,null,entryPoint,null);
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
    protected Object convert_L(    Object result) throws Throwable {
      return convert.invokeExact(result);
    }
    protected Object convert_I(    int result) throws Throwable {
      return convert.invokeExact(result);
    }
    protected Object convert_J(    long result) throws Throwable {
      return convert.invokeExact(result);
    }
    protected Object convert_F(    float result) throws Throwable {
      return convert.invokeExact(result);
    }
    protected Object convert_D(    double result) throws Throwable {
      return convert.invokeExact(result);
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
    protected Object invoke_L0() throws Throwable {
      return convert_L((Object)invoker.invokeExact(target));
    }
    protected Object invoke_I0() throws Throwable {
      return convert_I((int)invoker.invokeExact(target));
    }
    protected Object invoke_J0() throws Throwable {
      return convert_J((long)invoker.invokeExact(target));
    }
    protected Object invoke_F0() throws Throwable {
      return convert_F((float)invoker.invokeExact(target));
    }
    protected Object invoke_D0() throws Throwable {
      return convert_D((double)invoker.invokeExact(target));
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
    protected Object invoke_L1(    Object a0) throws Throwable {
      return convert_L((Object)invoker.invokeExact(target,a0));
    }
    protected Object invoke_I1(    Object a0) throws Throwable {
      return convert_I((int)invoker.invokeExact(target,a0));
    }
    protected Object invoke_J1(    Object a0) throws Throwable {
      return convert_J((long)invoker.invokeExact(target,a0));
    }
    protected Object invoke_F1(    Object a0) throws Throwable {
      return convert_F((float)invoker.invokeExact(target,a0));
    }
    protected Object invoke_D1(    Object a0) throws Throwable {
      return convert_D((double)invoker.invokeExact(target,a0));
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
    protected Object invoke_L2(    Object a0,    Object a1) throws Throwable {
      return convert_L((Object)invoker.invokeExact(target,a0,a1));
    }
    protected Object invoke_I2(    Object a0,    Object a1) throws Throwable {
      return convert_I((int)invoker.invokeExact(target,a0,a1));
    }
    protected Object invoke_J2(    Object a0,    Object a1) throws Throwable {
      return convert_J((long)invoker.invokeExact(target,a0,a1));
    }
    protected Object invoke_F2(    Object a0,    Object a1) throws Throwable {
      return convert_F((float)invoker.invokeExact(target,a0,a1));
    }
    protected Object invoke_D2(    Object a0,    Object a1) throws Throwable {
      return convert_D((double)invoker.invokeExact(target,a0,a1));
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
    protected Object invoke_L3(    Object a0,    Object a1,    Object a2) throws Throwable {
      return convert_L((Object)invoker.invokeExact(target,a0,a1,a2));
    }
    protected Object invoke_I3(    Object a0,    Object a1,    Object a2) throws Throwable {
      return convert_I((int)invoker.invokeExact(target,a0,a1,a2));
    }
    protected Object invoke_J3(    Object a0,    Object a1,    Object a2) throws Throwable {
      return convert_J((long)invoker.invokeExact(target,a0,a1,a2));
    }
    protected Object invoke_F3(    Object a0,    Object a1,    Object a2) throws Throwable {
      return convert_F((float)invoker.invokeExact(target,a0,a1,a2));
    }
    protected Object invoke_D3(    Object a0,    Object a1,    Object a2) throws Throwable {
      return convert_D((double)invoker.invokeExact(target,a0,a1,a2));
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
    protected Object invoke_L4(    Object a0,    Object a1,    Object a2,    Object a3) throws Throwable {
      return convert_L((Object)invoker.invokeExact(target,a0,a1,a2,a3));
    }
    protected Object invoke_I4(    Object a0,    Object a1,    Object a2,    Object a3) throws Throwable {
      return convert_I((int)invoker.invokeExact(target,a0,a1,a2,a3));
    }
    protected Object invoke_J4(    Object a0,    Object a1,    Object a2,    Object a3) throws Throwable {
      return convert_J((long)invoker.invokeExact(target,a0,a1,a2,a3));
    }
    protected Object invoke_F4(    Object a0,    Object a1,    Object a2,    Object a3) throws Throwable {
      return convert_F((float)invoker.invokeExact(target,a0,a1,a2,a3));
    }
    protected Object invoke_D4(    Object a0,    Object a1,    Object a2,    Object a3) throws Throwable {
      return convert_D((double)invoker.invokeExact(target,a0,a1,a2,a3));
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
    protected Object invoke_L5(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4) throws Throwable {
      return convert_L((Object)invoker.invokeExact(target,a0,a1,a2,a3,a4));
    }
    protected Object invoke_I5(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4) throws Throwable {
      return convert_I((int)invoker.invokeExact(target,a0,a1,a2,a3,a4));
    }
    protected Object invoke_J5(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4) throws Throwable {
      return convert_J((long)invoker.invokeExact(target,a0,a1,a2,a3,a4));
    }
    protected Object invoke_F5(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4) throws Throwable {
      return convert_F((float)invoker.invokeExact(target,a0,a1,a2,a3,a4));
    }
    protected Object invoke_D5(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4) throws Throwable {
      return convert_D((double)invoker.invokeExact(target,a0,a1,a2,a3,a4));
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
    protected Object invoke_L6(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5) throws Throwable {
      return convert_L((Object)invoker.invokeExact(target,a0,a1,a2,a3,a4,a5));
    }
    protected Object invoke_I6(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5) throws Throwable {
      return convert_I((int)invoker.invokeExact(target,a0,a1,a2,a3,a4,a5));
    }
    protected Object invoke_J6(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5) throws Throwable {
      return convert_J((long)invoker.invokeExact(target,a0,a1,a2,a3,a4,a5));
    }
    protected Object invoke_F6(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5) throws Throwable {
      return convert_F((float)invoker.invokeExact(target,a0,a1,a2,a3,a4,a5));
    }
    protected Object invoke_D6(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5) throws Throwable {
      return convert_D((double)invoker.invokeExact(target,a0,a1,a2,a3,a4,a5));
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
    protected Object invoke_L7(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    Object a6) throws Throwable {
      return convert_L((Object)invoker.invokeExact(target,a0,a1,a2,a3,a4,a5,a6));
    }
    protected Object invoke_I7(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    Object a6) throws Throwable {
      return convert_I((int)invoker.invokeExact(target,a0,a1,a2,a3,a4,a5,a6));
    }
    protected Object invoke_J7(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    Object a6) throws Throwable {
      return convert_J((long)invoker.invokeExact(target,a0,a1,a2,a3,a4,a5,a6));
    }
    protected Object invoke_F7(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    Object a6) throws Throwable {
      return convert_F((float)invoker.invokeExact(target,a0,a1,a2,a3,a4,a5,a6));
    }
    protected Object invoke_D7(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    Object a6) throws Throwable {
      return convert_D((double)invoker.invokeExact(target,a0,a1,a2,a3,a4,a5,a6));
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
    protected Object invoke_L8(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    Object a6,    Object a7) throws Throwable {
      return convert_L((Object)invoker.invokeExact(target,a0,a1,a2,a3,a4,a5,a6,a7));
    }
    protected Object invoke_I8(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    Object a6,    Object a7) throws Throwable {
      return convert_I((int)invoker.invokeExact(target,a0,a1,a2,a3,a4,a5,a6,a7));
    }
    protected Object invoke_J8(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    Object a6,    Object a7) throws Throwable {
      return convert_J((long)invoker.invokeExact(target,a0,a1,a2,a3,a4,a5,a6,a7));
    }
    protected Object invoke_F8(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    Object a6,    Object a7) throws Throwable {
      return convert_F((float)invoker.invokeExact(target,a0,a1,a2,a3,a4,a5,a6,a7));
    }
    protected Object invoke_D8(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    Object a6,    Object a7) throws Throwable {
      return convert_D((double)invoker.invokeExact(target,a0,a1,a2,a3,a4,a5,a6,a7));
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
    protected Object invoke_L9(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    Object a6,    Object a7,    Object a8) throws Throwable {
      return convert_L((Object)invoker.invokeExact(target,a0,a1,a2,a3,a4,a5,a6,a7,a8));
    }
    protected Object invoke_I9(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    Object a6,    Object a7,    Object a8) throws Throwable {
      return convert_I((int)invoker.invokeExact(target,a0,a1,a2,a3,a4,a5,a6,a7,a8));
    }
    protected Object invoke_J9(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    Object a6,    Object a7,    Object a8) throws Throwable {
      return convert_J((long)invoker.invokeExact(target,a0,a1,a2,a3,a4,a5,a6,a7,a8));
    }
    protected Object invoke_F9(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    Object a6,    Object a7,    Object a8) throws Throwable {
      return convert_F((float)invoker.invokeExact(target,a0,a1,a2,a3,a4,a5,a6,a7,a8));
    }
    protected Object invoke_D9(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    Object a6,    Object a7,    Object a8) throws Throwable {
      return convert_D((double)invoker.invokeExact(target,a0,a1,a2,a3,a4,a5,a6,a7,a8));
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
    protected Object invoke_L10(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    Object a6,    Object a7,    Object a8,    Object a9) throws Throwable {
      return convert_L((Object)invoker.invokeExact(target,a0,a1,a2,a3,a4,a5,a6,a7,a8,a9));
    }
    protected Object invoke_I10(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    Object a6,    Object a7,    Object a8,    Object a9) throws Throwable {
      return convert_I((int)invoker.invokeExact(target,a0,a1,a2,a3,a4,a5,a6,a7,a8,a9));
    }
    protected Object invoke_J10(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    Object a6,    Object a7,    Object a8,    Object a9) throws Throwable {
      return convert_J((long)invoker.invokeExact(target,a0,a1,a2,a3,a4,a5,a6,a7,a8,a9));
    }
    protected Object invoke_F10(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    Object a6,    Object a7,    Object a8,    Object a9) throws Throwable {
      return convert_F((float)invoker.invokeExact(target,a0,a1,a2,a3,a4,a5,a6,a7,a8,a9));
    }
    protected Object invoke_D10(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    Object a6,    Object a7,    Object a8,    Object a9) throws Throwable {
      return convert_D((double)invoker.invokeExact(target,a0,a1,a2,a3,a4,a5,a6,a7,a8,a9));
    }
  }
}
