package sun.security.jca;
import java.util.*;
import java.security.*;
import java.security.Provider.Service;
/** 
 * Collection of utility methods to facilitate implementing getInstance()
 * methods in the JCA/JCE/JSSE/... framework.
 * @author  Andreas Sterbenz
 * @since   1.5
 */
public class GetInstance {
  private GetInstance(){
  }
  /** 
 * Static inner class representing a newly created instance.
 */
public static final class Instance {
    public final Provider provider;
    public final Object impl;
    private Instance(    Provider provider,    Object impl){
      this.provider=provider;
      this.impl=impl;
    }
    public Object[] toArray(){
      return new Object[]{impl,provider};
    }
  }
  public static Service getService(  String type,  String algorithm) throws NoSuchAlgorithmException {
    ProviderList list=Providers.getProviderList();
    Service s=list.getService(type,algorithm);
    if (s == null) {
      throw new NoSuchAlgorithmException(algorithm + " " + type+ " not available");
    }
    return s;
  }
  public static Service getService(  String type,  String algorithm,  String provider) throws NoSuchAlgorithmException, NoSuchProviderException {
    if ((provider == null) || (provider.length() == 0)) {
      throw new IllegalArgumentException("missing provider");
    }
    Provider p=Providers.getProviderList().getProvider(provider);
    if (p == null) {
      throw new NoSuchProviderException("no such provider: " + provider);
    }
    Service s=p.getService(type,algorithm);
    if (s == null) {
      throw new NoSuchAlgorithmException("no such algorithm: " + algorithm + " for provider "+ provider);
    }
    return s;
  }
  public static Service getService(  String type,  String algorithm,  Provider provider) throws NoSuchAlgorithmException {
    if (provider == null) {
      throw new IllegalArgumentException("missing provider");
    }
    Service s=provider.getService(type,algorithm);
    if (s == null) {
      throw new NoSuchAlgorithmException("no such algorithm: " + algorithm + " for provider "+ provider.getName());
    }
    return s;
  }
  /** 
 * Return a List of all the available Services that implement
 * (type, algorithm). Note that the list is initialized lazily
 * and Provider loading and lookup is only trigered when
 * necessary.
 */
  public static List<Service> getServices(  String type,  String algorithm){
    ProviderList list=Providers.getProviderList();
    return list.getServices(type,algorithm);
  }
  /** 
 * This method exists for compatibility with JCE only. It will be removed
 * once JCE has been changed to use the replacement method.
 * @deprecated use getServices(List<ServiceId>) instead
 */
  @Deprecated public static List<Service> getServices(  String type,  List<String> algorithms){
    ProviderList list=Providers.getProviderList();
    return list.getServices(type,algorithms);
  }
  /** 
 * Return a List of all the available Services that implement any of
 * the specified algorithms. See getServices(String, String) for detals.
 */
  public static List<Service> getServices(  List<ServiceId> ids){
    ProviderList list=Providers.getProviderList();
    return list.getServices(ids);
  }
  public static Instance getInstance(  String type,  Class clazz,  String algorithm) throws NoSuchAlgorithmException {
    ProviderList list=Providers.getProviderList();
    Service firstService=list.getService(type,algorithm);
    if (firstService == null) {
      throw new NoSuchAlgorithmException(algorithm + " " + type+ " not available");
    }
    NoSuchAlgorithmException failure;
    try {
      return getInstance(firstService,clazz);
    }
 catch (    NoSuchAlgorithmException e) {
      failure=e;
    }
    for (    Service s : list.getServices(type,algorithm)) {
      if (s == firstService) {
        continue;
      }
      try {
        return getInstance(s,clazz);
      }
 catch (      NoSuchAlgorithmException e) {
        failure=e;
      }
    }
    throw failure;
  }
  public static Instance getInstance(  String type,  Class clazz,  String algorithm,  Object param) throws NoSuchAlgorithmException {
    List<Service> services=getServices(type,algorithm);
    NoSuchAlgorithmException failure=null;
    for (    Service s : services) {
      try {
        return getInstance(s,clazz,param);
      }
 catch (      NoSuchAlgorithmException e) {
        failure=e;
      }
    }
    if (failure != null) {
      throw failure;
    }
 else {
      throw new NoSuchAlgorithmException(algorithm + " " + type+ " not available");
    }
  }
  public static Instance getInstance(  String type,  Class clazz,  String algorithm,  String provider) throws NoSuchAlgorithmException, NoSuchProviderException {
    return getInstance(getService(type,algorithm,provider),clazz);
  }
  public static Instance getInstance(  String type,  Class clazz,  String algorithm,  Object param,  String provider) throws NoSuchAlgorithmException, NoSuchProviderException {
    return getInstance(getService(type,algorithm,provider),clazz,param);
  }
  public static Instance getInstance(  String type,  Class clazz,  String algorithm,  Provider provider) throws NoSuchAlgorithmException {
    return getInstance(getService(type,algorithm,provider),clazz);
  }
  public static Instance getInstance(  String type,  Class clazz,  String algorithm,  Object param,  Provider provider) throws NoSuchAlgorithmException {
    return getInstance(getService(type,algorithm,provider),clazz,param);
  }
  public static Instance getInstance(  Service s,  Class clazz) throws NoSuchAlgorithmException {
    Object instance=s.newInstance(null);
    checkSuperClass(s,instance.getClass(),clazz);
    return new Instance(s.getProvider(),instance);
  }
  public static Instance getInstance(  Service s,  Class clazz,  Object param) throws NoSuchAlgorithmException {
    Object instance=s.newInstance(param);
    checkSuperClass(s,instance.getClass(),clazz);
    return new Instance(s.getProvider(),instance);
  }
  /** 
 * Check is subClass is a subclass of superClass. If not,
 * throw a NoSuchAlgorithmException.
 */
  public static void checkSuperClass(  Service s,  Class subClass,  Class superClass) throws NoSuchAlgorithmException {
    if (superClass == null) {
      return;
    }
    if (superClass.isAssignableFrom(subClass) == false) {
      throw new NoSuchAlgorithmException("class configured for " + s.getType() + ": "+ s.getClassName()+ " not a "+ s.getType());
    }
  }
}
