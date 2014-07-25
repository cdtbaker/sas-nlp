package com.sun.jmx.mbeanserver;
import static com.sun.jmx.mbeanserver.Util.*;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.management.NotCompliantMBeanException;
/** 
 * <p>An analyzer for a given MBean interface.  The analyzer can
 * be for Standard MBeans or MXBeans, depending on the MBeanIntrospector
 * passed at construction.
 * <p>The analyzer can
 * visit the attributes and operations of the interface, calling
 * a caller-supplied visitor method for each one.</p>
 * @param<M>
 *  Method or ConvertingMethod according as this is a
 * Standard MBean or an MXBean.
 * @since 1.6
 */
class MBeanAnalyzer<M> {
static interface MBeanVisitor<M> {
    public void visitAttribute(    String attributeName,    M getter,    M setter);
    public void visitOperation(    String operationName,    M operation);
  }
  void visit(  MBeanVisitor<M> visitor){
    for (    Map.Entry<String,AttrMethods<M>> entry : attrMap.entrySet()) {
      String name=entry.getKey();
      AttrMethods<M> am=entry.getValue();
      visitor.visitAttribute(name,am.getter,am.setter);
    }
    for (    Map.Entry<String,List<M>> entry : opMap.entrySet()) {
      for (      M m : entry.getValue())       visitor.visitOperation(entry.getKey(),m);
    }
  }
  private Map<String,List<M>> opMap=newInsertionOrderMap();
  private Map<String,AttrMethods<M>> attrMap=newInsertionOrderMap();
private static class AttrMethods<M> {
    M getter;
    M setter;
  }
  /** 
 * <p>Return an MBeanAnalyzer for the given MBean interface and
 * MBeanIntrospector.  Calling this method twice with the same
 * parameters may return the same object or two different but
 * equivalent objects.
 */
  static <M>MBeanAnalyzer<M> analyzer(  Class<?> mbeanType,  MBeanIntrospector<M> introspector) throws NotCompliantMBeanException {
    return new MBeanAnalyzer<M>(mbeanType,introspector);
  }
  private MBeanAnalyzer(  Class<?> mbeanType,  MBeanIntrospector<M> introspector) throws NotCompliantMBeanException {
    if (!mbeanType.isInterface()) {
      throw new NotCompliantMBeanException("Not an interface: " + mbeanType.getName());
    }
    try {
      initMaps(mbeanType,introspector);
    }
 catch (    Exception x) {
      throw Introspector.throwException(mbeanType,x);
    }
  }
  private void initMaps(  Class<?> mbeanType,  MBeanIntrospector<M> introspector) throws Exception {
    final List<Method> methods1=introspector.getMethods(mbeanType);
    final List<Method> methods=eliminateCovariantMethods(methods1);
    for (    Method m : methods) {
      final String name=m.getName();
      final int nParams=m.getParameterTypes().length;
      final M cm=introspector.mFrom(m);
      String attrName="";
      if (name.startsWith("get"))       attrName=name.substring(3);
 else       if (name.startsWith("is") && m.getReturnType() == boolean.class)       attrName=name.substring(2);
      if (attrName.length() != 0 && nParams == 0 && m.getReturnType() != void.class) {
        AttrMethods<M> am=attrMap.get(attrName);
        if (am == null)         am=new AttrMethods<M>();
 else {
          if (am.getter != null) {
            final String msg="Attribute " + attrName + " has more than one getter";
            throw new NotCompliantMBeanException(msg);
          }
        }
        am.getter=cm;
        attrMap.put(attrName,am);
      }
 else       if (name.startsWith("set") && name.length() > 3 && nParams == 1 && m.getReturnType() == void.class) {
        attrName=name.substring(3);
        AttrMethods<M> am=attrMap.get(attrName);
        if (am == null)         am=new AttrMethods<M>();
 else         if (am.setter != null) {
          final String msg="Attribute " + attrName + " has more than one setter";
          throw new NotCompliantMBeanException(msg);
        }
        am.setter=cm;
        attrMap.put(attrName,am);
      }
 else {
        List<M> cms=opMap.get(name);
        if (cms == null)         cms=newList();
        cms.add(cm);
        opMap.put(name,cms);
      }
    }
    for (    Map.Entry<String,AttrMethods<M>> entry : attrMap.entrySet()) {
      AttrMethods<M> am=entry.getValue();
      if (!introspector.consistent(am.getter,am.setter)) {
        final String msg="Getter and setter for " + entry.getKey() + " have inconsistent types";
        throw new NotCompliantMBeanException(msg);
      }
    }
  }
  /** 
 * A comparator that defines a total order so that methods have the
 * same name and identical signatures appear next to each others.
 * The methods are sorted in such a way that methods which
 * override each other will sit next to each other, with the
 * overridden method first - e.g. Object getFoo() is placed before
 * Integer getFoo(). This makes it possible to determine whether
 * a method overrides another one simply by looking at the method(s)
 * that precedes it in the list. (see eliminateCovariantMethods).
 */
private static class MethodOrder implements Comparator<Method> {
    public int compare(    Method a,    Method b){
      final int cmp=a.getName().compareTo(b.getName());
      if (cmp != 0)       return cmp;
      final Class<?>[] aparams=a.getParameterTypes();
      final Class<?>[] bparams=b.getParameterTypes();
      if (aparams.length != bparams.length)       return aparams.length - bparams.length;
      if (!Arrays.equals(aparams,bparams)) {
        return Arrays.toString(aparams).compareTo(Arrays.toString(bparams));
      }
      final Class<?> aret=a.getReturnType();
      final Class<?> bret=b.getReturnType();
      if (aret == bret)       return 0;
      if (aret.isAssignableFrom(bret))       return -1;
      return +1;
    }
    public final static MethodOrder instance=new MethodOrder();
  }
  static List<Method> eliminateCovariantMethods(  List<Method> startMethods){
    final int len=startMethods.size();
    final Method[] sorted=startMethods.toArray(new Method[len]);
    Arrays.sort(sorted,MethodOrder.instance);
    final Set<Method> overridden=newSet();
    for (int i=1; i < len; i++) {
      final Method m0=sorted[i - 1];
      final Method m1=sorted[i];
      if (!m0.getName().equals(m1.getName()))       continue;
      if (Arrays.equals(m0.getParameterTypes(),m1.getParameterTypes())) {
        if (!overridden.add(m0))         throw new RuntimeException("Internal error: duplicate Method");
      }
    }
    final List<Method> methods=newList(startMethods);
    methods.removeAll(overridden);
    return methods;
  }
}
