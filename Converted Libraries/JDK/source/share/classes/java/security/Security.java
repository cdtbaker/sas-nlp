package java.security;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.io.*;
import java.net.URL;
import sun.security.util.Debug;
import sun.security.util.PropertyExpander;
import java.security.Provider.Service;
import sun.security.jca.*;
/** 
 * <p>This class centralizes all security properties and common security
 * methods. One of its primary uses is to manage providers.
 * @author Benjamin Renaud
 */
public final class Security {
  private static final Debug sdebug=Debug.getInstance("properties");
  private static Properties props;
private static class ProviderProperty {
    String className;
    Provider provider;
  }
static {
    AccessController.doPrivileged(new PrivilegedAction<Void>(){
      public Void run(){
        initialize();
        return null;
      }
    }
);
  }
  private static void initialize(){
    props=new Properties();
    boolean loadedProps=false;
    boolean overrideAll=false;
    File propFile=securityPropFile("java.security");
    if (propFile.exists()) {
      InputStream is=null;
      try {
        FileInputStream fis=new FileInputStream(propFile);
        is=new BufferedInputStream(fis);
        props.load(is);
        loadedProps=true;
        if (sdebug != null) {
          sdebug.println("reading security properties file: " + propFile);
        }
      }
 catch (      IOException e) {
        if (sdebug != null) {
          sdebug.println("unable to load security properties from " + propFile);
          e.printStackTrace();
        }
      }
 finally {
        if (is != null) {
          try {
            is.close();
          }
 catch (          IOException ioe) {
            if (sdebug != null) {
              sdebug.println("unable to close input stream");
            }
          }
        }
      }
    }
    if ("true".equalsIgnoreCase(props.getProperty("security.overridePropertiesFile"))) {
      String extraPropFile=System.getProperty("java.security.properties");
      if (extraPropFile != null && extraPropFile.startsWith("=")) {
        overrideAll=true;
        extraPropFile=extraPropFile.substring(1);
      }
      if (overrideAll) {
        props=new Properties();
        if (sdebug != null) {
          sdebug.println("overriding other security properties files!");
        }
      }
      if (extraPropFile != null) {
        BufferedInputStream bis=null;
        try {
          URL propURL;
          extraPropFile=PropertyExpander.expand(extraPropFile);
          propFile=new File(extraPropFile);
          if (propFile.exists()) {
            propURL=new URL("file:" + propFile.getCanonicalPath());
          }
 else {
            propURL=new URL(extraPropFile);
          }
          bis=new BufferedInputStream(propURL.openStream());
          props.load(bis);
          loadedProps=true;
          if (sdebug != null) {
            sdebug.println("reading security properties file: " + propURL);
            if (overrideAll) {
              sdebug.println("overriding other security properties files!");
            }
          }
        }
 catch (        Exception e) {
          if (sdebug != null) {
            sdebug.println("unable to load security properties from " + extraPropFile);
            e.printStackTrace();
          }
        }
 finally {
          if (bis != null) {
            try {
              bis.close();
            }
 catch (            IOException ioe) {
              if (sdebug != null) {
                sdebug.println("unable to close input stream");
              }
            }
          }
        }
      }
    }
    if (!loadedProps) {
      initializeStatic();
      if (sdebug != null) {
        sdebug.println("unable to load security properties " + "-- using defaults");
      }
    }
  }
  private static void initializeStatic(){
    props.put("security.provider.1","sun.security.provider.Sun");
    props.put("security.provider.2","sun.security.rsa.SunRsaSign");
    props.put("security.provider.3","com.sun.net.ssl.internal.ssl.Provider");
    props.put("security.provider.4","com.sun.crypto.provider.SunJCE");
    props.put("security.provider.5","sun.security.jgss.SunProvider");
    props.put("security.provider.6","com.sun.security.sasl.Provider");
  }
  /** 
 * Don't let anyone instantiate this.
 */
  private Security(){
  }
  private static File securityPropFile(  String filename){
    String sep=File.separator;
    return new File(System.getProperty("java.home") + sep + "lib"+ sep+ "security"+ sep+ filename);
  }
  /** 
 * Looks up providers, and returns the property (and its associated
 * provider) mapping the key, if any.
 * The order in which the providers are looked up is the
 * provider-preference order, as specificed in the security
 * properties file.
 */
  private static ProviderProperty getProviderProperty(  String key){
    ProviderProperty entry=null;
    List<Provider> providers=Providers.getProviderList().providers();
    for (int i=0; i < providers.size(); i++) {
      String matchKey=null;
      Provider prov=providers.get(i);
      String prop=prov.getProperty(key);
      if (prop == null) {
        for (Enumeration<Object> e=prov.keys(); e.hasMoreElements() && prop == null; ) {
          matchKey=(String)e.nextElement();
          if (key.equalsIgnoreCase(matchKey)) {
            prop=prov.getProperty(matchKey);
            break;
          }
        }
      }
      if (prop != null) {
        ProviderProperty newEntry=new ProviderProperty();
        newEntry.className=prop;
        newEntry.provider=prov;
        return newEntry;
      }
    }
    return entry;
  }
  /** 
 * Returns the property (if any) mapping the key for the given provider.
 */
  private static String getProviderProperty(  String key,  Provider provider){
    String prop=provider.getProperty(key);
    if (prop == null) {
      for (Enumeration<Object> e=provider.keys(); e.hasMoreElements() && prop == null; ) {
        String matchKey=(String)e.nextElement();
        if (key.equalsIgnoreCase(matchKey)) {
          prop=provider.getProperty(matchKey);
          break;
        }
      }
    }
    return prop;
  }
  /** 
 * Gets a specified property for an algorithm. The algorithm name
 * should be a standard name. See the <a href=
 * "{@docRoot}/../technotes/guides/security/StandardNames.html">
 * Java Cryptography Architecture Standard Algorithm Name Documentation</a>
 * for information about standard algorithm names.
 * One possible use is by specialized algorithm parsers, which may map
 * classes to algorithms which they understand (much like Key parsers
 * do).
 * @param algName the algorithm name.
 * @param propName the name of the property to get.
 * @return the value of the specified property.
 * @deprecated This method used to return the value of a proprietary
 * property in the master file of the "SUN" Cryptographic Service
 * Provider in order to determine how to parse algorithm-specific
 * parameters. Use the new provider-based and algorithm-independent
 * <code>AlgorithmParameters</code> and <code>KeyFactory</code> engine
 * classes (introduced in the J2SE version 1.2 platform) instead.
 */
  @Deprecated public static String getAlgorithmProperty(  String algName,  String propName){
    ProviderProperty entry=getProviderProperty("Alg." + propName + "."+ algName);
    if (entry != null) {
      return entry.className;
    }
 else {
      return null;
    }
  }
  /** 
 * Adds a new provider, at a specified position. The position is
 * the preference order in which providers are searched for
 * requested algorithms.  The position is 1-based, that is,
 * 1 is most preferred, followed by 2, and so on.
 * <p>If the given provider is installed at the requested position,
 * the provider that used to be at that position, and all providers
 * with a position greater than <code>position</code>, are shifted up
 * one position (towards the end of the list of installed providers).
 * <p>A provider cannot be added if it is already installed.
 * <p>First, if there is a security manager, its
 * <code>checkSecurityAccess</code>
 * method is called with the string
 * <code>"insertProvider."+provider.getName()</code>
 * to see if it's ok to add a new provider.
 * If the default implementation of <code>checkSecurityAccess</code>
 * is used (i.e., that method is not overriden), then this will result in
 * a call to the security manager's <code>checkPermission</code> method
 * with a
 * <code>SecurityPermission("insertProvider."+provider.getName())</code>
 * permission.
 * @param provider the provider to be added.
 * @param position the preference position that the caller would
 * like for this provider.
 * @return the actual preference position in which the provider was
 * added, or -1 if the provider was not added because it is
 * already installed.
 * @throws NullPointerException if provider is null
 * @throws SecurityExceptionif a security manager exists and its <code>{@link java.lang.SecurityManager#checkSecurityAccess}</code> method
 * denies access to add a new provider
 * @see #getProvider
 * @see #removeProvider
 * @see java.security.SecurityPermission
 */
  public static synchronized int insertProviderAt(  Provider provider,  int position){
    String providerName=provider.getName();
    check("insertProvider." + providerName);
    ProviderList list=Providers.getFullProviderList();
    ProviderList newList=ProviderList.insertAt(list,provider,position - 1);
    if (list == newList) {
      return -1;
    }
    Providers.setProviderList(newList);
    return newList.getIndex(providerName) + 1;
  }
  /** 
 * Adds a provider to the next position available.
 * <p>First, if there is a security manager, its
 * <code>checkSecurityAccess</code>
 * method is called with the string
 * <code>"insertProvider."+provider.getName()</code>
 * to see if it's ok to add a new provider.
 * If the default implementation of <code>checkSecurityAccess</code>
 * is used (i.e., that method is not overriden), then this will result in
 * a call to the security manager's <code>checkPermission</code> method
 * with a
 * <code>SecurityPermission("insertProvider."+provider.getName())</code>
 * permission.
 * @param provider the provider to be added.
 * @return the preference position in which the provider was
 * added, or -1 if the provider was not added because it is
 * already installed.
 * @throws NullPointerException if provider is null
 * @throws SecurityExceptionif a security manager exists and its <code>{@link java.lang.SecurityManager#checkSecurityAccess}</code> method
 * denies access to add a new provider
 * @see #getProvider
 * @see #removeProvider
 * @see java.security.SecurityPermission
 */
  public static int addProvider(  Provider provider){
    return insertProviderAt(provider,0);
  }
  /** 
 * Removes the provider with the specified name.
 * <p>When the specified provider is removed, all providers located
 * at a position greater than where the specified provider was are shifted
 * down one position (towards the head of the list of installed
 * providers).
 * <p>This method returns silently if the provider is not installed or
 * if name is null.
 * <p>First, if there is a security manager, its
 * <code>checkSecurityAccess</code>
 * method is called with the string <code>"removeProvider."+name</code>
 * to see if it's ok to remove the provider.
 * If the default implementation of <code>checkSecurityAccess</code>
 * is used (i.e., that method is not overriden), then this will result in
 * a call to the security manager's <code>checkPermission</code> method
 * with a <code>SecurityPermission("removeProvider."+name)</code>
 * permission.
 * @param name the name of the provider to remove.
 * @throws SecurityExceptionif a security manager exists and its <code>{@link java.lang.SecurityManager#checkSecurityAccess}</code> method
 * denies
 * access to remove the provider
 * @see #getProvider
 * @see #addProvider
 */
  public static synchronized void removeProvider(  String name){
    check("removeProvider." + name);
    ProviderList list=Providers.getFullProviderList();
    ProviderList newList=ProviderList.remove(list,name);
    Providers.setProviderList(newList);
  }
  /** 
 * Returns an array containing all the installed providers. The order of
 * the providers in the array is their preference order.
 * @return an array of all the installed providers.
 */
  public static Provider[] getProviders(){
    return Providers.getFullProviderList().toArray();
  }
  /** 
 * Returns the provider installed with the specified name, if
 * any. Returns null if no provider with the specified name is
 * installed or if name is null.
 * @param name the name of the provider to get.
 * @return the provider of the specified name.
 * @see #removeProvider
 * @see #addProvider
 */
  public static Provider getProvider(  String name){
    return Providers.getProviderList().getProvider(name);
  }
  /** 
 * Returns an array containing all installed providers that satisfy the
 * specified selection criterion, or null if no such providers have been
 * installed. The returned providers are ordered
 * according to their <a href=
 * "#insertProviderAt(java.security.Provider, int)">preference order</a>.
 * <p> A cryptographic service is always associated with a particular
 * algorithm or type. For example, a digital signature service is
 * always associated with a particular algorithm (e.g., DSA),
 * and a CertificateFactory service is always associated with
 * a particular certificate type (e.g., X.509).
 * <p>The selection criterion must be specified in one of the following two
 * formats:
 * <ul>
 * <li> <i>&lt;crypto_service>.&lt;algorithm_or_type></i> <p> The
 * cryptographic service name must not contain any dots.
 * <p> A
 * provider satisfies the specified selection criterion iff the provider
 * implements the
 * specified algorithm or type for the specified cryptographic service.
 * <p> For example, "CertificateFactory.X.509"
 * would be satisfied by any provider that supplied
 * a CertificateFactory implementation for X.509 certificates.
 * <li> <i>&lt;crypto_service>.&lt;algorithm_or_type>
 * &lt;attribute_name>:&lt attribute_value></i>
 * <p> The cryptographic service name must not contain any dots. There
 * must be one or more space charaters between the
 * <i>&lt;algorithm_or_type></i> and the <i>&lt;attribute_name></i>.
 * <p> A provider satisfies this selection criterion iff the
 * provider implements the specified algorithm or type for the specified
 * cryptographic service and its implementation meets the
 * constraint expressed by the specified attribute name/value pair.
 * <p> For example, "Signature.SHA1withDSA KeySize:1024" would be
 * satisfied by any provider that implemented
 * the SHA1withDSA signature algorithm with a keysize of 1024 (or larger).
 * </ul>
 * <p> See the <a href=
 * "{@docRoot}/../technotes/guides/security/StandardNames.html">
 * Java Cryptography Architecture Standard Algorithm Name Documentation</a>
 * for information about standard cryptographic service names, standard
 * algorithm names and standard attribute names.
 * @param filter the criterion for selecting
 * providers. The filter is case-insensitive.
 * @return all the installed providers that satisfy the selection
 * criterion, or null if no such providers have been installed.
 * @throws InvalidParameterExceptionif the filter is not in the required format
 * @throws NullPointerException if filter is null
 * @see #getProviders(java.util.Map)
 * @since 1.3
 */
  public static Provider[] getProviders(  String filter){
    String key=null;
    String value=null;
    int index=filter.indexOf(':');
    if (index == -1) {
      key=filter;
      value="";
    }
 else {
      key=filter.substring(0,index);
      value=filter.substring(index + 1);
    }
    Hashtable<String,String> hashtableFilter=new Hashtable<>(1);
    hashtableFilter.put(key,value);
    return (getProviders(hashtableFilter));
  }
  /** 
 * Returns an array containing all installed providers that satisfy the
 * specified* selection criteria, or null if no such providers have been
 * installed. The returned providers are ordered
 * according to their <a href=
 * "#insertProviderAt(java.security.Provider, int)">preference order</a>.
 * <p>The selection criteria are represented by a map.
 * Each map entry represents a selection criterion.
 * A provider is selected iff it satisfies all selection
 * criteria. The key for any entry in such a map must be in one of the
 * following two formats:
 * <ul>
 * <li> <i>&lt;crypto_service>.&lt;algorithm_or_type></i>
 * <p> The cryptographic service name must not contain any dots.
 * <p> The value associated with the key must be an empty string.
 * <p> A provider
 * satisfies this selection criterion iff the provider implements the
 * specified algorithm or type for the specified cryptographic service.
 * <li>  <i>&lt;crypto_service>.&lt;algorithm_or_type> &lt;attribute_name></i>
 * <p> The cryptographic service name must not contain any dots. There
 * must be one or more space charaters between the <i>&lt;algorithm_or_type></i>
 * and the <i>&lt;attribute_name></i>.
 * <p> The value associated with the key must be a non-empty string.
 * A provider satisfies this selection criterion iff the
 * provider implements the specified algorithm or type for the specified
 * cryptographic service and its implementation meets the
 * constraint expressed by the specified attribute name/value pair.
 * </ul>
 * <p> See the <a href=
 * "../../../technotes/guides/security/StandardNames.html">
 * Java Cryptography Architecture Standard Algorithm Name Documentation</a>
 * for information about standard cryptographic service names, standard
 * algorithm names and standard attribute names.
 * @param filter the criteria for selecting
 * providers. The filter is case-insensitive.
 * @return all the installed providers that satisfy the selection
 * criteria, or null if no such providers have been installed.
 * @throws InvalidParameterExceptionif the filter is not in the required format
 * @throws NullPointerException if filter is null
 * @see #getProviders(java.lang.String)
 * @since 1.3
 */
  public static Provider[] getProviders(  Map<String,String> filter){
    Provider[] allProviders=Security.getProviders();
    Set<String> keySet=filter.keySet();
    LinkedHashSet<Provider> candidates=new LinkedHashSet<>(5);
    if ((keySet == null) || (allProviders == null)) {
      return allProviders;
    }
    boolean firstSearch=true;
    for (Iterator<String> ite=keySet.iterator(); ite.hasNext(); ) {
      String key=ite.next();
      String value=filter.get(key);
      LinkedHashSet<Provider> newCandidates=getAllQualifyingCandidates(key,value,allProviders);
      if (firstSearch) {
        candidates=newCandidates;
        firstSearch=false;
      }
      if ((newCandidates != null) && !newCandidates.isEmpty()) {
        for (Iterator<Provider> cansIte=candidates.iterator(); cansIte.hasNext(); ) {
          Provider prov=cansIte.next();
          if (!newCandidates.contains(prov)) {
            cansIte.remove();
          }
        }
      }
 else {
        candidates=null;
        break;
      }
    }
    if ((candidates == null) || (candidates.isEmpty()))     return null;
    Object[] candidatesArray=candidates.toArray();
    Provider[] result=new Provider[candidatesArray.length];
    for (int i=0; i < result.length; i++) {
      result[i]=(Provider)candidatesArray[i];
    }
    return result;
  }
  private static final Map<String,Class> spiMap=new ConcurrentHashMap<>();
  /** 
 * Return the Class object for the given engine type
 * (e.g. "MessageDigest"). Works for Spis in the java.security package
 * only.
 */
  private static Class getSpiClass(  String type){
    Class clazz=spiMap.get(type);
    if (clazz != null) {
      return clazz;
    }
    try {
      clazz=Class.forName("java.security." + type + "Spi");
      spiMap.put(type,clazz);
      return clazz;
    }
 catch (    ClassNotFoundException e) {
      throw new AssertionError("Spi class not found",e);
    }
  }
  static Object[] getImpl(  String algorithm,  String type,  String provider) throws NoSuchAlgorithmException, NoSuchProviderException {
    if (provider == null) {
      return GetInstance.getInstance(type,getSpiClass(type),algorithm).toArray();
    }
 else {
      return GetInstance.getInstance(type,getSpiClass(type),algorithm,provider).toArray();
    }
  }
  static Object[] getImpl(  String algorithm,  String type,  String provider,  Object params) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
    if (provider == null) {
      return GetInstance.getInstance(type,getSpiClass(type),algorithm,params).toArray();
    }
 else {
      return GetInstance.getInstance(type,getSpiClass(type),algorithm,params,provider).toArray();
    }
  }
  static Object[] getImpl(  String algorithm,  String type,  Provider provider) throws NoSuchAlgorithmException {
    return GetInstance.getInstance(type,getSpiClass(type),algorithm,provider).toArray();
  }
  static Object[] getImpl(  String algorithm,  String type,  Provider provider,  Object params) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
    return GetInstance.getInstance(type,getSpiClass(type),algorithm,params,provider).toArray();
  }
  /** 
 * Gets a security property value.
 * <p>First, if there is a security manager, its
 * <code>checkPermission</code>  method is called with a
 * <code>java.security.SecurityPermission("getProperty."+key)</code>
 * permission to see if it's ok to retrieve the specified
 * security property value..
 * @param key the key of the property being retrieved.
 * @return the value of the security property corresponding to key.
 * @throws SecurityExceptionif a security manager exists and its <code>{@link java.lang.SecurityManager#checkPermission}</code> method
 * denies
 * access to retrieve the specified security property value
 * @throws NullPointerException is key is null
 * @see #setProperty
 * @see java.security.SecurityPermission
 */
  public static String getProperty(  String key){
    SecurityManager sm=System.getSecurityManager();
    if (sm != null) {
      sm.checkPermission(new SecurityPermission("getProperty." + key));
    }
    String name=props.getProperty(key);
    if (name != null)     name=name.trim();
    return name;
  }
  /** 
 * Sets a security property value.
 * <p>First, if there is a security manager, its
 * <code>checkPermission</code> method is called with a
 * <code>java.security.SecurityPermission("setProperty."+key)</code>
 * permission to see if it's ok to set the specified
 * security property value.
 * @param key the name of the property to be set.
 * @param datum the value of the property to be set.
 * @throws SecurityExceptionif a security manager exists and its <code>{@link java.lang.SecurityManager#checkPermission}</code> method
 * denies access to set the specified security property value
 * @throws NullPointerException if key or datum is null
 * @see #getProperty
 * @see java.security.SecurityPermission
 */
  public static void setProperty(  String key,  String datum){
    check("setProperty." + key);
    props.put(key,datum);
    invalidateSMCache(key);
  }
  private static void invalidateSMCache(  String key){
    final boolean pa=key.equals("package.access");
    final boolean pd=key.equals("package.definition");
    if (pa || pd) {
      AccessController.doPrivileged(new PrivilegedAction<Void>(){
        public Void run(){
          try {
            Class cl=Class.forName("java.lang.SecurityManager",false,null);
            Field f=null;
            boolean accessible=false;
            if (pa) {
              f=cl.getDeclaredField("packageAccessValid");
              accessible=f.isAccessible();
              f.setAccessible(true);
            }
 else {
              f=cl.getDeclaredField("packageDefinitionValid");
              accessible=f.isAccessible();
              f.setAccessible(true);
            }
            f.setBoolean(f,false);
            f.setAccessible(accessible);
          }
 catch (          Exception e1) {
          }
          return null;
        }
      }
);
    }
  }
  private static void check(  String directive){
    SecurityManager security=System.getSecurityManager();
    if (security != null) {
      security.checkSecurityAccess(directive);
    }
  }
  private static LinkedHashSet<Provider> getAllQualifyingCandidates(  String filterKey,  String filterValue,  Provider[] allProviders){
    String[] filterComponents=getFilterComponents(filterKey,filterValue);
    String serviceName=filterComponents[0];
    String algName=filterComponents[1];
    String attrName=filterComponents[2];
    return getProvidersNotUsingCache(serviceName,algName,attrName,filterValue,allProviders);
  }
  private static LinkedHashSet<Provider> getProvidersNotUsingCache(  String serviceName,  String algName,  String attrName,  String filterValue,  Provider[] allProviders){
    LinkedHashSet<Provider> candidates=new LinkedHashSet<>(5);
    for (int i=0; i < allProviders.length; i++) {
      if (isCriterionSatisfied(allProviders[i],serviceName,algName,attrName,filterValue)) {
        candidates.add(allProviders[i]);
      }
    }
    return candidates;
  }
  private static boolean isCriterionSatisfied(  Provider prov,  String serviceName,  String algName,  String attrName,  String filterValue){
    String key=serviceName + '.' + algName;
    if (attrName != null) {
      key+=' ' + attrName;
    }
    String propValue=getProviderProperty(key,prov);
    if (propValue == null) {
      String standardName=getProviderProperty("Alg.Alias." + serviceName + "."+ algName,prov);
      if (standardName != null) {
        key=serviceName + "." + standardName;
        if (attrName != null) {
          key+=' ' + attrName;
        }
        propValue=getProviderProperty(key,prov);
      }
      if (propValue == null) {
        return false;
      }
    }
    if (attrName == null) {
      return true;
    }
    if (isStandardAttr(attrName)) {
      return isConstraintSatisfied(attrName,filterValue,propValue);
    }
 else {
      return filterValue.equalsIgnoreCase(propValue);
    }
  }
  private static boolean isStandardAttr(  String attribute){
    if (attribute.equalsIgnoreCase("KeySize"))     return true;
    if (attribute.equalsIgnoreCase("ImplementedIn"))     return true;
    return false;
  }
  private static boolean isConstraintSatisfied(  String attribute,  String value,  String prop){
    if (attribute.equalsIgnoreCase("KeySize")) {
      int requestedSize=Integer.parseInt(value);
      int maxSize=Integer.parseInt(prop);
      if (requestedSize <= maxSize) {
        return true;
      }
 else {
        return false;
      }
    }
    if (attribute.equalsIgnoreCase("ImplementedIn")) {
      return value.equalsIgnoreCase(prop);
    }
    return false;
  }
  static String[] getFilterComponents(  String filterKey,  String filterValue){
    int algIndex=filterKey.indexOf('.');
    if (algIndex < 0) {
      throw new InvalidParameterException("Invalid filter");
    }
    String serviceName=filterKey.substring(0,algIndex);
    String algName=null;
    String attrName=null;
    if (filterValue.length() == 0) {
      algName=filterKey.substring(algIndex + 1).trim();
      if (algName.length() == 0) {
        throw new InvalidParameterException("Invalid filter");
      }
    }
 else {
      int attrIndex=filterKey.indexOf(' ');
      if (attrIndex == -1) {
        throw new InvalidParameterException("Invalid filter");
      }
 else {
        attrName=filterKey.substring(attrIndex + 1).trim();
        if (attrName.length() == 0) {
          throw new InvalidParameterException("Invalid filter");
        }
      }
      if ((attrIndex < algIndex) || (algIndex == attrIndex - 1)) {
        throw new InvalidParameterException("Invalid filter");
      }
 else {
        algName=filterKey.substring(algIndex + 1,attrIndex);
      }
    }
    String[] result=new String[3];
    result[0]=serviceName;
    result[1]=algName;
    result[2]=attrName;
    return result;
  }
  /** 
 * Returns a Set of Strings containing the names of all available
 * algorithms or types for the specified Java cryptographic service
 * (e.g., Signature, MessageDigest, Cipher, Mac, KeyStore). Returns
 * an empty Set if there is no provider that supports the
 * specified service or if serviceName is null. For a complete list
 * of Java cryptographic services, please see the
 * <a href="../../../technotes/guides/security/crypto/CryptoSpec.html">Java
 * Cryptography Architecture API Specification &amp; Reference</a>.
 * Note: the returned set is immutable.
 * @param serviceName the name of the Java cryptographic
 * service (e.g., Signature, MessageDigest, Cipher, Mac, KeyStore).
 * Note: this parameter is case-insensitive.
 * @return a Set of Strings containing the names of all available
 * algorithms or types for the specified Java cryptographic service
 * or an empty set if no provider supports the specified service.
 * @since 1.4
 */
  public static Set<String> getAlgorithms(  String serviceName){
    if ((serviceName == null) || (serviceName.length() == 0) || (serviceName.endsWith("."))) {
      return Collections.EMPTY_SET;
    }
    HashSet<String> result=new HashSet<>();
    Provider[] providers=Security.getProviders();
    for (int i=0; i < providers.length; i++) {
      for (Enumeration<Object> e=providers[i].keys(); e.hasMoreElements(); ) {
        String currentKey=((String)e.nextElement()).toUpperCase();
        if (currentKey.startsWith(serviceName.toUpperCase())) {
          if (currentKey.indexOf(" ") < 0) {
            result.add(currentKey.substring(serviceName.length() + 1));
          }
        }
      }
    }
    return Collections.unmodifiableSet(result);
  }
}
