package com.sun.org.apache.xml.internal.security.utils.resolver;
import java.util.HashMap;
import java.util.Map;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import org.w3c.dom.Attr;
/** 
 * During reference validation, we have to retrieve resources from somewhere.
 * @author $Author: mullan $
 */
public abstract class ResourceResolverSpi {
  /** 
 * {@link java.util.logging} logging facility 
 */
  static java.util.logging.Logger log=java.util.logging.Logger.getLogger(ResourceResolverSpi.class.getName());
  /** 
 * Field _properties 
 */
  protected java.util.Map _properties=null;
  /** 
 * This is the workhorse method used to resolve resources.
 * @param uri
 * @param BaseURI
 * @return the resource wrapped arround a XMLSignatureInput
 * @throws ResourceResolverException
 */
  public abstract XMLSignatureInput engineResolve(  Attr uri,  String BaseURI) throws ResourceResolverException ;
  /** 
 * Method engineSetProperty
 * @param key
 * @param value
 */
  public void engineSetProperty(  String key,  String value){
    if (_properties == null) {
      _properties=new HashMap();
    }
    this._properties.put(key,value);
  }
  /** 
 * Method engineGetProperty
 * @param key
 * @return the value of the property
 */
  public String engineGetProperty(  String key){
    if (_properties == null) {
      return null;
    }
    return (String)this._properties.get(key);
  }
  /** 
 * @param properties
 */
  public void engineAddProperies(  Map properties){
    if (properties != null) {
      if (_properties == null) {
        _properties=new HashMap();
      }
      this._properties.putAll(properties);
    }
  }
  /** 
 * Tells if the implementation does can be reused by several threads safely.
 * It normally means that the implemantation does not have any member, or there is
 * member change betwen engineCanResolve & engineResolve invocations. Or it mantians all
 * member info in ThreadLocal methods.
 */
  public boolean engineIsThreadSafe(){
    return false;
  }
  /** 
 * This method helps the {@link ResourceResolver} to decide whether a{@link ResourceResolverSpi} is able to perform the requested action.
 * @param uri
 * @param BaseURI
 * @return true if the engine can resolve the uri
 */
  public abstract boolean engineCanResolve(  Attr uri,  String BaseURI);
  /** 
 * Method engineGetPropertyKeys
 * @return the property keys
 */
  public String[] engineGetPropertyKeys(){
    return new String[0];
  }
  /** 
 * Method understandsProperty
 * @param propertyToTest
 * @return true if understands the property
 */
  public boolean understandsProperty(  String propertyToTest){
    String[] understood=this.engineGetPropertyKeys();
    if (understood != null) {
      for (int i=0; i < understood.length; i++) {
        if (understood[i].equals(propertyToTest)) {
          return true;
        }
      }
    }
    return false;
  }
  /** 
 * Fixes a platform dependent filename to standard URI form.
 * @param str The string to fix.
 * @return Returns the fixed URI string.
 */
  public static String fixURI(  String str){
    str=str.replace(java.io.File.separatorChar,'/');
    if (str.length() >= 4) {
      char ch0=Character.toUpperCase(str.charAt(0));
      char ch1=str.charAt(1);
      char ch2=str.charAt(2);
      char ch3=str.charAt(3);
      boolean isDosFilename=((('A' <= ch0) && (ch0 <= 'Z')) && (ch1 == ':') && (ch2 == '/')&& (ch3 != '/'));
      if (isDosFilename) {
        if (log.isLoggable(java.util.logging.Level.FINE))         log.log(java.util.logging.Level.FINE,"Found DOS filename: " + str);
      }
    }
    if (str.length() >= 2) {
      char ch1=str.charAt(1);
      if (ch1 == ':') {
        char ch0=Character.toUpperCase(str.charAt(0));
        if (('A' <= ch0) && (ch0 <= 'Z')) {
          str="/" + str;
        }
      }
    }
    return str;
  }
}
