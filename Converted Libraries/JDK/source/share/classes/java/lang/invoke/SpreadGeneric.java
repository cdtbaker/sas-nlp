package java.lang.invoke;
import sun.invoke.util.ValueConversions;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import static java.lang.invoke.MethodHandleStatics.*;
import static java.lang.invoke.MethodHandles.Lookup.IMPL_LOOKUP;
/** 
 * Generic spread adapter.
 * Expands a final argument into multiple (zero or more) arguments, keeping the others the same.
 * @author jrose
 */
class SpreadGeneric {
  private final MethodType targetType;
  private final int spreadCount;
  private final Adapter adapter;
  private final MethodHandle entryPoint;
  /** 
 * Compute and cache information common to all spreading adapters
 * that accept calls of the given (generic) type.
 */
  private SpreadGeneric(  MethodType targetType,  int spreadCount){
    assert (targetType == targetType.generic());
    this.targetType=targetType;
    this.spreadCount=spreadCount;
    MethodHandle[] ep={null};
    Adapter ad=findAdapter(this,ep);
    if (ad != null) {
      this.adapter=ad;
      this.entryPoint=ep[0];
      return;
    }
    this.adapter=buildAdapterFromBytecodes(targetType,spreadCount,ep);
    this.entryPoint=ep[0];
  }
static {
    assert (MethodHandleNatives.workaroundWithoutRicochetFrames());
  }
  /** 
 * From targetType remove the last spreadCount arguments, and instead
 * append a simple Object argument.
 */
  static MethodType preSpreadType(  MethodType targetType,  int spreadCount){
    @SuppressWarnings("unchecked") ArrayList<Class<?>> params=new ArrayList(targetType.parameterList());
    int outargs=params.size();
    params.subList(outargs - spreadCount,outargs).clear();
    params.add(Object.class);
    return MethodType.methodType(targetType.returnType(),params);
  }
  MethodHandle makeInstance(  MethodHandle target){
    MethodType type=target.type();
    if (type != targetType) {
      throw new UnsupportedOperationException("NYI type=" + type);
    }
    return adapter.makeInstance(this,target);
  }
  /** 
 * Build an adapter of the given generic type, which invokes typedTarget
 * on the incoming arguments, after unboxing as necessary.
 * The return value is boxed if necessary.
 * @param genericType  the required type of the result
 * @param typedTarget the target
 * @return an adapter method handle
 */
  public static MethodHandle make(  MethodHandle target,  int spreadCount){
    MethodType type=target.type();
    MethodType gtype=type.generic();
    if (type == gtype) {
      return SpreadGeneric.of(type,spreadCount).makeInstance(target);
    }
 else {
      MethodHandle gtarget=FromGeneric.make(target);
      assert (gtarget.type() == gtype);
      MethodHandle gspread=SpreadGeneric.of(gtype,spreadCount).makeInstance(gtarget);
      return ToGeneric.make(preSpreadType(type,spreadCount),gspread);
    }
  }
  /** 
 * Return the adapter information for this type's erasure. 
 */
  static SpreadGeneric of(  MethodType targetType,  int spreadCount){
    if (targetType != targetType.generic())     throw new UnsupportedOperationException("NYI type=" + targetType);
    MethodTypeForm form=targetType.form();
    int outcount=form.parameterCount();
    assert (spreadCount <= outcount);
    SpreadGeneric[] spreadGens=form.spreadGeneric;
    if (spreadGens == null)     form.spreadGeneric=spreadGens=new SpreadGeneric[outcount + 1];
    SpreadGeneric spreadGen=spreadGens[spreadCount];
    if (spreadGen == null)     spreadGens[spreadCount]=spreadGen=new SpreadGeneric(form.erasedType(),spreadCount);
    return spreadGen;
  }
  String debugString(){
    return getClass().getSimpleName() + targetType + "["+ spreadCount+ "]";
  }
  /** 
 * A check/coercion that happens once before any selections. 
 */
  protected Object check(  Object av,  int n){
    checkSpreadArgument(av,n);
    return av;
  }
  /** 
 * The selection operator for spreading; note that it takes Object not Object[]. 
 */
  protected Object select(  Object av,  int n){
    return ((Object[])av)[n];
  }
  static Adapter findAdapter(  SpreadGeneric outer,  MethodHandle[] ep){
    MethodType targetType=outer.targetType;
    int spreadCount=outer.spreadCount;
    int outargs=targetType.parameterCount();
    int inargs=outargs - spreadCount;
    if (inargs < 0)     return null;
    MethodType entryType=MethodType.genericMethodType(inargs + 1);
    String cname1="S" + outargs;
    String[] cnames={cname1};
    String iname="invoke_S" + spreadCount;
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
        ctor=acls.getDeclaredConstructor(SpreadGeneric.class);
      }
 catch (      NoSuchMethodException ex) {
      }
catch (      SecurityException ex) {
      }
      if (ctor == null)       continue;
      try {
        Adapter ad=ctor.newInstance(outer);
        ep[0]=entryPoint;
        return ad;
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
  static Adapter buildAdapterFromBytecodes(  MethodType targetType,  int spreadCount,  MethodHandle[] ep){
    throw new UnsupportedOperationException("NYI");
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
    protected final SpreadGeneric outer;
    protected final MethodHandle target;
    @Override String debugString(){
      return addTypeString(target,this);
    }
    static final MethodHandle NO_ENTRY=ValueConversions.identity();
    protected boolean isPrototype(){
      return target == null;
    }
    protected Adapter(    SpreadGeneric outer){
      super(NO_ENTRY);
      this.outer=outer;
      this.target=null;
      assert (isPrototype());
    }
    protected Adapter(    SpreadGeneric outer,    MethodHandle target){
      super(outer.entryPoint);
      this.outer=outer;
      this.target=target;
    }
    /** 
 * Make a copy of self, with new fields. 
 */
    protected abstract Adapter makeInstance(    SpreadGeneric outer,    MethodHandle target);
    protected Object check(    Object av,    int n){
      return outer.check(av,n);
    }
    protected Object select(    Object av,    int n){
      return outer.select(av,n);
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
static class S0 extends Adapter {
    protected S0(    SpreadGeneric outer){
      super(outer);
    }
    protected S0(    SpreadGeneric outer,    MethodHandle t){
      super(outer,t);
    }
    protected S0 makeInstance(    SpreadGeneric outer,    MethodHandle t){
      return new S0(outer,t);
    }
    protected Object invoke_S0(    Object av) throws Throwable {
      av=super.check(av,0);
      return target.invokeExact();
    }
  }
static class S1 extends Adapter {
    protected S1(    SpreadGeneric outer){
      super(outer);
    }
    protected S1(    SpreadGeneric outer,    MethodHandle t){
      super(outer,t);
    }
    protected S1 makeInstance(    SpreadGeneric outer,    MethodHandle t){
      return new S1(outer,t);
    }
    protected Object invoke_S0(    Object a0,    Object av) throws Throwable {
      av=super.check(av,0);
      return target.invokeExact(a0);
    }
    protected Object invoke_S1(    Object av) throws Throwable {
      av=super.check(av,1);
      return target.invokeExact(super.select(av,0));
    }
  }
static class S2 extends Adapter {
    protected S2(    SpreadGeneric outer){
      super(outer);
    }
    protected S2(    SpreadGeneric outer,    MethodHandle t){
      super(outer,t);
    }
    protected S2 makeInstance(    SpreadGeneric outer,    MethodHandle t){
      return new S2(outer,t);
    }
    protected Object invoke_S0(    Object a0,    Object a1,    Object av) throws Throwable {
      av=super.check(av,0);
      return target.invokeExact(a0,a1);
    }
    protected Object invoke_S1(    Object a0,    Object av) throws Throwable {
      av=super.check(av,1);
      return target.invokeExact(a0,super.select(av,0));
    }
    protected Object invoke_S2(    Object av) throws Throwable {
      av=super.check(av,2);
      return target.invokeExact(super.select(av,0),super.select(av,1));
    }
  }
static class S3 extends Adapter {
    protected S3(    SpreadGeneric outer){
      super(outer);
    }
    protected S3(    SpreadGeneric outer,    MethodHandle t){
      super(outer,t);
    }
    protected S3 makeInstance(    SpreadGeneric outer,    MethodHandle t){
      return new S3(outer,t);
    }
    protected Object invoke_S0(    Object a0,    Object a1,    Object a2,    Object av) throws Throwable {
      av=super.check(av,0);
      return target.invokeExact(a0,a1,a2);
    }
    protected Object invoke_S1(    Object a0,    Object a1,    Object av) throws Throwable {
      av=super.check(av,1);
      return target.invokeExact(a0,a1,super.select(av,0));
    }
    protected Object invoke_S2(    Object a0,    Object av) throws Throwable {
      av=super.check(av,2);
      return target.invokeExact(a0,super.select(av,0),super.select(av,1));
    }
    protected Object invoke_S3(    Object av) throws Throwable {
      av=super.check(av,3);
      return target.invokeExact(super.select(av,0),super.select(av,1),super.select(av,2));
    }
  }
static class S4 extends Adapter {
    protected S4(    SpreadGeneric outer){
      super(outer);
    }
    protected S4(    SpreadGeneric outer,    MethodHandle t){
      super(outer,t);
    }
    protected S4 makeInstance(    SpreadGeneric outer,    MethodHandle t){
      return new S4(outer,t);
    }
    protected Object invoke_S0(    Object a0,    Object a1,    Object a2,    Object a3,    Object av) throws Throwable {
      av=super.check(av,0);
      return target.invokeExact(a0,a1,a2,a3);
    }
    protected Object invoke_S1(    Object a0,    Object a1,    Object a2,    Object av) throws Throwable {
      av=super.check(av,1);
      return target.invokeExact(a0,a1,a2,super.select(av,0));
    }
    protected Object invoke_S2(    Object a0,    Object a1,    Object av) throws Throwable {
      av=super.check(av,2);
      return target.invokeExact(a0,a1,super.select(av,0),super.select(av,1));
    }
    protected Object invoke_S3(    Object a0,    Object av) throws Throwable {
      av=super.check(av,3);
      return target.invokeExact(a0,super.select(av,0),super.select(av,1),super.select(av,2));
    }
    protected Object invoke_S4(    Object av) throws Throwable {
      av=super.check(av,4);
      return target.invokeExact(super.select(av,0),super.select(av,1),super.select(av,2),super.select(av,3));
    }
  }
static class S5 extends Adapter {
    protected S5(    SpreadGeneric outer){
      super(outer);
    }
    protected S5(    SpreadGeneric outer,    MethodHandle t){
      super(outer,t);
    }
    protected S5 makeInstance(    SpreadGeneric outer,    MethodHandle t){
      return new S5(outer,t);
    }
    protected Object invoke_S0(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object av) throws Throwable {
      av=super.check(av,0);
      return target.invokeExact(a0,a1,a2,a3,a4);
    }
    protected Object invoke_S1(    Object a0,    Object a1,    Object a2,    Object a3,    Object av) throws Throwable {
      av=super.check(av,1);
      return target.invokeExact(a0,a1,a2,a3,super.select(av,0));
    }
    protected Object invoke_S2(    Object a0,    Object a1,    Object a2,    Object av) throws Throwable {
      av=super.check(av,2);
      return target.invokeExact(a0,a1,a2,super.select(av,0),super.select(av,1));
    }
    protected Object invoke_S3(    Object a0,    Object a1,    Object av) throws Throwable {
      av=super.check(av,3);
      return target.invokeExact(a0,a1,super.select(av,0),super.select(av,1),super.select(av,2));
    }
    protected Object invoke_S4(    Object a0,    Object av) throws Throwable {
      av=super.check(av,4);
      return target.invokeExact(a0,super.select(av,0),super.select(av,1),super.select(av,2),super.select(av,3));
    }
    protected Object invoke_S5(    Object av) throws Throwable {
      av=super.check(av,5);
      return target.invokeExact(super.select(av,0),super.select(av,1),super.select(av,2),super.select(av,3),super.select(av,4));
    }
  }
static class S6 extends Adapter {
    protected S6(    SpreadGeneric outer){
      super(outer);
    }
    protected S6(    SpreadGeneric outer,    MethodHandle t){
      super(outer,t);
    }
    protected S6 makeInstance(    SpreadGeneric outer,    MethodHandle t){
      return new S6(outer,t);
    }
    protected Object invoke_S0(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    Object av) throws Throwable {
      av=super.check(av,0);
      return target.invokeExact(a0,a1,a2,a3,a4,a5);
    }
    protected Object invoke_S1(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object av) throws Throwable {
      av=super.check(av,1);
      return target.invokeExact(a0,a1,a2,a3,a4,super.select(av,0));
    }
    protected Object invoke_S2(    Object a0,    Object a1,    Object a2,    Object a3,    Object av) throws Throwable {
      av=super.check(av,2);
      return target.invokeExact(a0,a1,a2,a3,super.select(av,0),super.select(av,1));
    }
    protected Object invoke_S3(    Object a0,    Object a1,    Object a2,    Object av) throws Throwable {
      av=super.check(av,3);
      return target.invokeExact(a0,a1,a2,super.select(av,0),super.select(av,1),super.select(av,2));
    }
    protected Object invoke_S4(    Object a0,    Object a1,    Object av) throws Throwable {
      av=super.check(av,4);
      return target.invokeExact(a0,a1,super.select(av,0),super.select(av,1),super.select(av,2),super.select(av,3));
    }
    protected Object invoke_S5(    Object a0,    Object av) throws Throwable {
      av=super.check(av,5);
      return target.invokeExact(a0,super.select(av,0),super.select(av,1),super.select(av,2),super.select(av,3),super.select(av,4));
    }
    protected Object invoke_S6(    Object av) throws Throwable {
      av=super.check(av,6);
      return target.invokeExact(super.select(av,0),super.select(av,1),super.select(av,2),super.select(av,3),super.select(av,4),super.select(av,5));
    }
  }
static class S7 extends Adapter {
    protected S7(    SpreadGeneric outer){
      super(outer);
    }
    protected S7(    SpreadGeneric outer,    MethodHandle t){
      super(outer,t);
    }
    protected S7 makeInstance(    SpreadGeneric outer,    MethodHandle t){
      return new S7(outer,t);
    }
    protected Object invoke_S0(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    Object a6,    Object av) throws Throwable {
      av=super.check(av,0);
      return target.invokeExact(a0,a1,a2,a3,a4,a5,a6);
    }
    protected Object invoke_S1(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    Object av) throws Throwable {
      av=super.check(av,1);
      return target.invokeExact(a0,a1,a2,a3,a4,a5,super.select(av,0));
    }
    protected Object invoke_S2(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object av) throws Throwable {
      av=super.check(av,2);
      return target.invokeExact(a0,a1,a2,a3,a4,super.select(av,0),super.select(av,1));
    }
    protected Object invoke_S3(    Object a0,    Object a1,    Object a2,    Object a3,    Object av) throws Throwable {
      av=super.check(av,3);
      return target.invokeExact(a0,a1,a2,a3,super.select(av,0),super.select(av,1),super.select(av,2));
    }
    protected Object invoke_S4(    Object a0,    Object a1,    Object a2,    Object av) throws Throwable {
      av=super.check(av,4);
      return target.invokeExact(a0,a1,a2,super.select(av,0),super.select(av,1),super.select(av,2),super.select(av,3));
    }
    protected Object invoke_S5(    Object a0,    Object a1,    Object av) throws Throwable {
      av=super.check(av,5);
      return target.invokeExact(a0,a1,super.select(av,0),super.select(av,1),super.select(av,2),super.select(av,3),super.select(av,4));
    }
    protected Object invoke_S6(    Object a0,    Object av) throws Throwable {
      av=super.check(av,6);
      return target.invokeExact(a0,super.select(av,0),super.select(av,1),super.select(av,2),super.select(av,3),super.select(av,4),super.select(av,5));
    }
    protected Object invoke_S7(    Object av) throws Throwable {
      av=super.check(av,7);
      return target.invokeExact(super.select(av,0),super.select(av,1),super.select(av,2),super.select(av,3),super.select(av,4),super.select(av,5),super.select(av,6));
    }
  }
static class S8 extends Adapter {
    protected S8(    SpreadGeneric outer){
      super(outer);
    }
    protected S8(    SpreadGeneric outer,    MethodHandle t){
      super(outer,t);
    }
    protected S8 makeInstance(    SpreadGeneric outer,    MethodHandle t){
      return new S8(outer,t);
    }
    protected Object invoke_S0(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    Object a6,    Object a7,    Object av) throws Throwable {
      av=super.check(av,0);
      return target.invokeExact(a0,a1,a2,a3,a4,a5,a6,a7);
    }
    protected Object invoke_S1(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    Object a6,    Object av) throws Throwable {
      av=super.check(av,1);
      return target.invokeExact(a0,a1,a2,a3,a4,a5,a6,super.select(av,0));
    }
    protected Object invoke_S2(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    Object av) throws Throwable {
      av=super.check(av,2);
      return target.invokeExact(a0,a1,a2,a3,a4,a5,super.select(av,0),super.select(av,1));
    }
    protected Object invoke_S3(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object av) throws Throwable {
      av=super.check(av,3);
      return target.invokeExact(a0,a1,a2,a3,a4,super.select(av,0),super.select(av,1),super.select(av,2));
    }
    protected Object invoke_S4(    Object a0,    Object a1,    Object a2,    Object a3,    Object av) throws Throwable {
      av=super.check(av,4);
      return target.invokeExact(a0,a1,a2,a3,super.select(av,0),super.select(av,1),super.select(av,2),super.select(av,3));
    }
    protected Object invoke_S5(    Object a0,    Object a1,    Object a2,    Object av) throws Throwable {
      av=super.check(av,5);
      return target.invokeExact(a0,a1,a2,super.select(av,0),super.select(av,1),super.select(av,2),super.select(av,3),super.select(av,4));
    }
    protected Object invoke_S6(    Object a0,    Object a1,    Object av) throws Throwable {
      av=super.check(av,6);
      return target.invokeExact(a0,a1,super.select(av,0),super.select(av,1),super.select(av,2),super.select(av,3),super.select(av,4),super.select(av,5));
    }
    protected Object invoke_S7(    Object a0,    Object av) throws Throwable {
      av=super.check(av,7);
      return target.invokeExact(a0,super.select(av,0),super.select(av,1),super.select(av,2),super.select(av,3),super.select(av,4),super.select(av,5),super.select(av,6));
    }
    protected Object invoke_S8(    Object av) throws Throwable {
      av=super.check(av,8);
      return target.invokeExact(super.select(av,0),super.select(av,1),super.select(av,2),super.select(av,3),super.select(av,4),super.select(av,5),super.select(av,6),super.select(av,7));
    }
  }
static class S9 extends Adapter {
    protected S9(    SpreadGeneric outer){
      super(outer);
    }
    protected S9(    SpreadGeneric outer,    MethodHandle t){
      super(outer,t);
    }
    protected S9 makeInstance(    SpreadGeneric outer,    MethodHandle t){
      return new S9(outer,t);
    }
    protected Object invoke_S0(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    Object a6,    Object a7,    Object a8,    Object av) throws Throwable {
      av=super.check(av,0);
      return target.invokeExact(a0,a1,a2,a3,a4,a5,a6,a7,a8);
    }
    protected Object invoke_S1(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    Object a6,    Object a7,    Object av) throws Throwable {
      av=super.check(av,1);
      return target.invokeExact(a0,a1,a2,a3,a4,a5,a6,a7,super.select(av,0));
    }
    protected Object invoke_S2(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    Object a6,    Object av) throws Throwable {
      av=super.check(av,2);
      return target.invokeExact(a0,a1,a2,a3,a4,a5,a6,super.select(av,0),super.select(av,1));
    }
    protected Object invoke_S3(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    Object av) throws Throwable {
      av=super.check(av,3);
      return target.invokeExact(a0,a1,a2,a3,a4,a5,super.select(av,0),super.select(av,1),super.select(av,2));
    }
    protected Object invoke_S4(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object av) throws Throwable {
      av=super.check(av,4);
      return target.invokeExact(a0,a1,a2,a3,a4,super.select(av,0),super.select(av,1),super.select(av,2),super.select(av,3));
    }
    protected Object invoke_S5(    Object a0,    Object a1,    Object a2,    Object a3,    Object av) throws Throwable {
      av=super.check(av,5);
      return target.invokeExact(a0,a1,a2,a3,super.select(av,0),super.select(av,1),super.select(av,2),super.select(av,3),super.select(av,4));
    }
    protected Object invoke_S6(    Object a0,    Object a1,    Object a2,    Object av) throws Throwable {
      av=super.check(av,6);
      return target.invokeExact(a0,a1,a2,super.select(av,0),super.select(av,1),super.select(av,2),super.select(av,3),super.select(av,4),super.select(av,5));
    }
    protected Object invoke_S7(    Object a0,    Object a1,    Object av) throws Throwable {
      av=super.check(av,7);
      return target.invokeExact(a0,a1,super.select(av,0),super.select(av,1),super.select(av,2),super.select(av,3),super.select(av,4),super.select(av,5),super.select(av,6));
    }
    protected Object invoke_S8(    Object a0,    Object av) throws Throwable {
      av=super.check(av,8);
      return target.invokeExact(a0,super.select(av,0),super.select(av,1),super.select(av,2),super.select(av,3),super.select(av,4),super.select(av,5),super.select(av,6),super.select(av,7));
    }
    protected Object invoke_S9(    Object av) throws Throwable {
      av=super.check(av,9);
      return target.invokeExact(super.select(av,0),super.select(av,1),super.select(av,2),super.select(av,3),super.select(av,4),super.select(av,5),super.select(av,6),super.select(av,7),super.select(av,8));
    }
  }
static class S10 extends Adapter {
    protected S10(    SpreadGeneric outer){
      super(outer);
    }
    protected S10(    SpreadGeneric outer,    MethodHandle t){
      super(outer,t);
    }
    protected S10 makeInstance(    SpreadGeneric outer,    MethodHandle t){
      return new S10(outer,t);
    }
    protected Object invoke_S0(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    Object a6,    Object a7,    Object a8,    Object a9,    Object av) throws Throwable {
      av=super.check(av,0);
      return target.invokeExact(a0,a1,a2,a3,a4,a5,a6,a7,a8,a9);
    }
    protected Object invoke_S1(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    Object a6,    Object a7,    Object a8,    Object av) throws Throwable {
      av=super.check(av,1);
      return target.invokeExact(a0,a1,a2,a3,a4,a5,a6,a7,a8,super.select(av,0));
    }
    protected Object invoke_S2(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    Object a6,    Object a7,    Object av) throws Throwable {
      av=super.check(av,2);
      return target.invokeExact(a0,a1,a2,a3,a4,a5,a6,a7,super.select(av,0),super.select(av,1));
    }
    protected Object invoke_S3(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    Object a6,    Object av) throws Throwable {
      av=super.check(av,3);
      return target.invokeExact(a0,a1,a2,a3,a4,a5,a6,super.select(av,0),super.select(av,1),super.select(av,2));
    }
    protected Object invoke_S4(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object a5,    Object av) throws Throwable {
      av=super.check(av,4);
      return target.invokeExact(a0,a1,a2,a3,a4,a5,super.select(av,0),super.select(av,1),super.select(av,2),super.select(av,3));
    }
    protected Object invoke_S5(    Object a0,    Object a1,    Object a2,    Object a3,    Object a4,    Object av) throws Throwable {
      av=super.check(av,5);
      return target.invokeExact(a0,a1,a2,a3,a4,super.select(av,0),super.select(av,1),super.select(av,2),super.select(av,3),super.select(av,4));
    }
    protected Object invoke_S6(    Object a0,    Object a1,    Object a2,    Object a3,    Object av) throws Throwable {
      av=super.check(av,6);
      return target.invokeExact(a0,a1,a2,a3,super.select(av,0),super.select(av,1),super.select(av,2),super.select(av,3),super.select(av,4),super.select(av,5));
    }
    protected Object invoke_S7(    Object a0,    Object a1,    Object a2,    Object av) throws Throwable {
      av=super.check(av,7);
      return target.invokeExact(a0,a1,a2,super.select(av,0),super.select(av,1),super.select(av,2),super.select(av,3),super.select(av,4),super.select(av,5),super.select(av,6));
    }
    protected Object invoke_S8(    Object a0,    Object a1,    Object av) throws Throwable {
      av=super.check(av,8);
      return target.invokeExact(a0,a1,super.select(av,0),super.select(av,1),super.select(av,2),super.select(av,3),super.select(av,4),super.select(av,5),super.select(av,6),super.select(av,7));
    }
    protected Object invoke_S9(    Object a0,    Object av) throws Throwable {
      av=super.check(av,9);
      return target.invokeExact(a0,super.select(av,0),super.select(av,1),super.select(av,2),super.select(av,3),super.select(av,4),super.select(av,5),super.select(av,6),super.select(av,7),super.select(av,8));
    }
    protected Object invoke_S10(    Object av) throws Throwable {
      av=super.check(av,10);
      return target.invokeExact(super.select(av,0),super.select(av,1),super.select(av,2),super.select(av,3),super.select(av,4),super.select(av,5),super.select(av,6),super.select(av,7),super.select(av,8),super.select(av,9));
    }
  }
}
