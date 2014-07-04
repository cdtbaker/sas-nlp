package sun.tracing.dtrace;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import sun.tracing.ProviderSkeleton;
import sun.tracing.ProbeSkeleton;
import com.sun.tracing.Provider;
import com.sun.tracing.ProviderName;
import com.sun.tracing.ProbeName;
import com.sun.tracing.dtrace.Attributes;
import com.sun.tracing.dtrace.ModuleName;
import com.sun.tracing.dtrace.FunctionName;
import com.sun.tracing.dtrace.StabilityLevel;
import com.sun.tracing.dtrace.DependencyClass;
import sun.misc.ProxyGenerator;
class DTraceProvider extends ProviderSkeleton {
  private Activation activation;
  private Object proxy;
  private final static Class[] constructorParams={InvocationHandler.class};
  private final String proxyClassNamePrefix="$DTraceTracingProxy";
  static final String DEFAULT_MODULE="java_tracing";
  static final String DEFAULT_FUNCTION="unspecified";
  private static long nextUniqueNumber=0;
  private static synchronized long getUniqueNumber(){
    return nextUniqueNumber++;
  }
  protected ProbeSkeleton createProbe(  Method m){
    return new DTraceProbe(proxy,m);
  }
  DTraceProvider(  Class<? extends Provider> type){
    super(type);
  }
  void setProxy(  Object p){
    proxy=p;
  }
  void setActivation(  Activation a){
    this.activation=a;
  }
  public void dispose(){
    if (activation != null) {
      activation.disposeProvider(this);
      activation=null;
    }
    super.dispose();
  }
  /** 
 * Magic routine which creates an implementation of the user's interface.
 * This method uses the ProxyGenerator directly to bypass the
 * java.lang.reflect.proxy cache so that we get a unique class each
 * time it's called and can't accidently reuse a $Proxy class.
 * @return an implementation of the user's interface
 */
  @SuppressWarnings("unchecked") public <T extends Provider>T newProxyInstance(){
    long num=getUniqueNumber();
    String proxyPkg="";
    if (!Modifier.isPublic(providerType.getModifiers())) {
      String name=providerType.getName();
      int n=name.lastIndexOf('.');
      proxyPkg=((n == -1) ? "" : name.substring(0,n + 1));
    }
    String proxyName=proxyPkg + proxyClassNamePrefix + num;
    Class<?> proxyClass=null;
    byte[] proxyClassFile=ProxyGenerator.generateProxyClass(proxyName,new Class<?>[]{providerType});
    try {
      proxyClass=JVM.defineClass(providerType.getClassLoader(),proxyName,proxyClassFile,0,proxyClassFile.length);
    }
 catch (    ClassFormatError e) {
      throw new IllegalArgumentException(e.toString());
    }
    try {
      Constructor cons=proxyClass.getConstructor(constructorParams);
      return (T)cons.newInstance(new Object[]{this});
    }
 catch (    NoSuchMethodException e) {
      throw new InternalError(e.toString());
    }
catch (    IllegalAccessException e) {
      throw new InternalError(e.toString());
    }
catch (    InstantiationException e) {
      throw new InternalError(e.toString());
    }
catch (    InvocationTargetException e) {
      throw new InternalError(e.toString());
    }
  }
  public Object invoke(  Object proxy,  Method method,  Object[] args){
    if (method.getDeclaringClass() != providerType) {
      try {
        return method.invoke(this,args);
      }
 catch (      IllegalAccessException e) {
        assert false;
      }
catch (      InvocationTargetException e) {
        assert false;
      }
    }
 else     if (active) {
      assert false : "This method should have been overridden by the JVM";
    }
    return null;
  }
  public String getProviderName(){
    return super.getProviderName();
  }
  String getModuleName(){
    return getAnnotationString(providerType,ModuleName.class,DEFAULT_MODULE);
  }
  static String getProbeName(  Method method){
    return getAnnotationString(method,ProbeName.class,method.getName());
  }
  static String getFunctionName(  Method method){
    return getAnnotationString(method,FunctionName.class,DEFAULT_FUNCTION);
  }
  DTraceProbe[] getProbes(){
    return probes.values().toArray(new DTraceProbe[0]);
  }
  StabilityLevel getNameStabilityFor(  Class<? extends Annotation> type){
    Attributes attrs=(Attributes)getAnnotationValue(providerType,type,"value",null);
    if (attrs == null) {
      return StabilityLevel.PRIVATE;
    }
 else {
      return attrs.name();
    }
  }
  StabilityLevel getDataStabilityFor(  Class<? extends Annotation> type){
    Attributes attrs=(Attributes)getAnnotationValue(providerType,type,"value",null);
    if (attrs == null) {
      return StabilityLevel.PRIVATE;
    }
 else {
      return attrs.data();
    }
  }
  DependencyClass getDependencyClassFor(  Class<? extends Annotation> type){
    Attributes attrs=(Attributes)getAnnotationValue(providerType,type,"value",null);
    if (attrs == null) {
      return DependencyClass.UNKNOWN;
    }
 else {
      return attrs.dependency();
    }
  }
}
