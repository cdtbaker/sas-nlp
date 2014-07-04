package com.sun.org.apache.xml.internal.security.utils.resolver;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import org.w3c.dom.Attr;
/** 
 * This Exception is thrown if something related to the{@link com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolver} goes wrong.
 * @author $Author: mullan $
 */
public class ResourceResolverException extends XMLSecurityException {
  /** 
 */
  private static final long serialVersionUID=1L;
  /** 
 * Constructor ResourceResolverException
 * @param _msgID
 * @param uri
 * @param BaseURI
 */
  public ResourceResolverException(  String _msgID,  Attr uri,  String BaseURI){
    super(_msgID);
    this._uri=uri;
    this._BaseURI=BaseURI;
  }
  /** 
 * Constructor ResourceResolverException
 * @param _msgID
 * @param exArgs
 * @param uri
 * @param BaseURI
 */
  public ResourceResolverException(  String _msgID,  Object exArgs[],  Attr uri,  String BaseURI){
    super(_msgID,exArgs);
    this._uri=uri;
    this._BaseURI=BaseURI;
  }
  /** 
 * Constructor ResourceResolverException
 * @param _msgID
 * @param _originalException
 * @param uri
 * @param BaseURI
 */
  public ResourceResolverException(  String _msgID,  Exception _originalException,  Attr uri,  String BaseURI){
    super(_msgID,_originalException);
    this._uri=uri;
    this._BaseURI=BaseURI;
  }
  /** 
 * Constructor ResourceResolverException
 * @param _msgID
 * @param exArgs
 * @param _originalException
 * @param uri
 * @param BaseURI
 */
  public ResourceResolverException(  String _msgID,  Object exArgs[],  Exception _originalException,  Attr uri,  String BaseURI){
    super(_msgID,exArgs,_originalException);
    this._uri=uri;
    this._BaseURI=BaseURI;
  }
  Attr _uri=null;
  /** 
 * @param uri
 */
  public void setURI(  Attr uri){
    this._uri=uri;
  }
  /** 
 * @return the uri
 */
  public Attr getURI(){
    return this._uri;
  }
  String _BaseURI;
  /** 
 * @param BaseURI
 */
  public void setBaseURI(  String BaseURI){
    this._BaseURI=BaseURI;
  }
  /** 
 * @return the basUri
 */
  public String getBaseURI(){
    return this._BaseURI;
  }
}
