package com.sun.org.apache.xml.internal.security.keys.keyresolver;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
/** 
 * @author $Author: mullan $
 */
public class InvalidKeyResolverException extends XMLSecurityException {
  /** 
 */
  private static final long serialVersionUID=1L;
  /** 
 * Constructor InvalidKeyResolverException
 */
  public InvalidKeyResolverException(){
    super();
  }
  /** 
 * Constructor InvalidKeyResolverException
 * @param _msgID
 */
  public InvalidKeyResolverException(  String _msgID){
    super(_msgID);
  }
  /** 
 * Constructor InvalidKeyResolverException
 * @param _msgID
 * @param exArgs
 */
  public InvalidKeyResolverException(  String _msgID,  Object exArgs[]){
    super(_msgID,exArgs);
  }
  /** 
 * Constructor InvalidKeyResolverException
 * @param _msgID
 * @param _originalException
 */
  public InvalidKeyResolverException(  String _msgID,  Exception _originalException){
    super(_msgID,_originalException);
  }
  /** 
 * Constructor InvalidKeyResolverException
 * @param _msgID
 * @param exArgs
 * @param _originalException
 */
  public InvalidKeyResolverException(  String _msgID,  Object exArgs[],  Exception _originalException){
    super(_msgID,exArgs,_originalException);
  }
}
