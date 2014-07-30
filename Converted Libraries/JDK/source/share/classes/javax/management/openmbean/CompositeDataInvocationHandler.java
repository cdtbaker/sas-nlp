package javax.management.openmbean;
import com.sun.jmx.mbeanserver.MXBeanLookup;
import com.sun.jmx.mbeanserver.MXBeanMapping;
import com.sun.jmx.mbeanserver.MXBeanMappingFactory;
import com.sun.jmx.mbeanserver.DefaultMXBeanMappingFactory;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
/** 
 * <p>An {@link InvocationHandler} that forwards getter methods to a{@link CompositeData}.  If you have an interface that contains
 * only getter methods (such as {@code String getName()} or{@code boolean isActive()}) then you can use this class in
 * conjunction with the {@link Proxy} class to produce an implementation
 * of the interface where each getter returns the value of the
 * corresponding item in a {@code CompositeData}.</p>
 * <p>For example, suppose you have an interface like this:
 * <blockquote>
 * <pre>
 * public interface NamedNumber {
 * public int getNumber();
 * public String getName();
 * }
 * </pre>
 * </blockquote>
 * and a {@code CompositeData} constructed like this:
 * <blockquote>
 * <pre>
 * CompositeData cd =
 * new {@link CompositeDataSupport}(
 * someCompositeType,
 * new String[] {"number", "name"},
 * new Object[] {<b>5</b>, "five"}
 * );
 * </pre>
 * </blockquote>
 * then you can construct an object implementing {@code NamedNumber}and backed by the object {@code cd} like this:
 * <blockquote>
 * <pre>
 * InvocationHandler handler =
 * new CompositeDataInvocationHandler(cd);
 * NamedNumber nn = (NamedNumber)
 * Proxy.newProxyInstance(NamedNumber.class.getClassLoader(),
 * new Class[] {NamedNumber.class},
 * handler);
 * </pre>
 * </blockquote>
 * A call to {@code nn.getNumber()} will then return <b>5</b>.</p>
 * <p>If the first letter of the property defined by a getter is a
 * capital, then this handler will look first for an item in the{@code CompositeData} beginning with a capital, then, if that is
 * not found, for an item beginning with the corresponding lowercase
 * letter or code point.  For a getter called {@code getNumber()}, the
 * handler will first look for an item called {@code Number}, then for{@code number}.  If the getter is called {@code getnumber()}, then
 * the item must be called {@code number}.</p>
 * <p>If the method given to {@link #invoke invoke} is the method{@code boolean equals(Object)} inherited from {@code Object}, then
 * it will return true if and only if the argument is a {@code Proxy}whose {@code InvocationHandler} is also a {@codeCompositeDataInvocationHandler} and whose backing {@codeCompositeData} is equal (not necessarily identical) to this
 * object's.  If the method given to {@code invoke} is the method{@code int hashCode()} inherited from {@code Object}, then it will
 * return a value that is consistent with this definition of {@codeequals}: if two objects are equal according to {@code equals}, then
 * they will have the same {@code hashCode}.</p>
 * @since 1.6
 */
public class CompositeDataInvocationHandler implements InvocationHandler {
  /** 
 * <p>Construct a handler backed by the given {@codeCompositeData}.</p>
 * @param compositeData the {@code CompositeData} that will supply
 * information to getters.
 * @throws IllegalArgumentException if {@code compositeData}is null.
 */
  public CompositeDataInvocationHandler(  CompositeData compositeData){
    this(compositeData,null);
  }
  /** 
 * <p>Construct a handler backed by the given {@codeCompositeData}.</p>
 * @param mbsc the {@code MBeanServerConnection} related to this{@code CompositeData}.  This is only relevant if a method in
 * the interface for which this is an invocation handler returns
 * a type that is an MXBean interface.  Otherwise, it can be null.
 * @param compositeData the {@code CompositeData} that will supply
 * information to getters.
 * @throws IllegalArgumentException if {@code compositeData}is null.
 */
  CompositeDataInvocationHandler(  CompositeData compositeData,  MXBeanLookup lookup){
    if (compositeData == null)     throw new IllegalArgumentException("compositeData");
    this.compositeData=compositeData;
    this.lookup=lookup;
  }
  /** 
 * Return the {@code CompositeData} that was supplied to the
 * constructor.
 * @return the {@code CompositeData} that this handler is backed
 * by.  This is never null.
 */
  public CompositeData getCompositeData(){
    assert compositeData != null;
    return compositeData;
  }
  public Object invoke(  Object proxy,  Method method,  Object[] args) throws Throwable {
    final String methodName=method.getName();
    if (method.getDeclaringClass() == Object.class) {
      if (methodName.equals("toString") && args == null)       return "Proxy[" + compositeData + "]";
 else       if (methodName.equals("hashCode") && args == null)       return compositeData.hashCode() + 0x43444948;
 else       if (methodName.equals("equals") && args.length == 1 && method.getParameterTypes()[0] == Object.class)       return equals(proxy,args[0]);
 else {
        return method.invoke(this,args);
      }
    }
    String propertyName=DefaultMXBeanMappingFactory.propertyName(method);
    if (propertyName == null) {
      throw new IllegalArgumentException("Method is not getter: " + method.getName());
    }
    Object openValue;
    if (compositeData.containsKey(propertyName))     openValue=compositeData.get(propertyName);
 else {
      String decap=DefaultMXBeanMappingFactory.decapitalize(propertyName);
      if (compositeData.containsKey(decap))       openValue=compositeData.get(decap);
 else {
        final String msg="No CompositeData item " + propertyName + (decap.equals(propertyName) ? "" : " or " + decap)+ " to match "+ methodName;
        throw new IllegalArgumentException(msg);
      }
    }
    MXBeanMapping mapping=MXBeanMappingFactory.DEFAULT.mappingForType(method.getGenericReturnType(),MXBeanMappingFactory.DEFAULT);
    return mapping.fromOpenValue(openValue);
  }
  private boolean equals(  Object proxy,  Object other){
    if (other == null)     return false;
    final Class<?> proxyClass=proxy.getClass();
    final Class<?> otherClass=other.getClass();
    if (proxyClass != otherClass)     return false;
    InvocationHandler otherih=Proxy.getInvocationHandler(other);
    if (!(otherih instanceof CompositeDataInvocationHandler))     return false;
    CompositeDataInvocationHandler othercdih=(CompositeDataInvocationHandler)otherih;
    return compositeData.equals(othercdih.compositeData);
  }
  private final CompositeData compositeData;
  private final MXBeanLookup lookup;
}