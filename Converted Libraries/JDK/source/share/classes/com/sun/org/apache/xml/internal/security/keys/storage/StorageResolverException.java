package com.sun.org.apache.xml.internal.security.keys.storage;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
/** 
 * @author $Author: mullan $
 */
public class StorageResolverException extends XMLSecurityException {
  /** 
 */
  private static final long serialVersionUID=1L;
  /** 
 * Constructor StorageResolverException
 */
  public StorageResolverException(){
    super();
  }
  /** 
 * Constructor StorageResolverException
 * @param _msgID
 */
  public StorageResolverException(  String _msgID){
    super(_msgID);
  }
  /** 
 * Constructor StorageResolverException
 * @param _msgID
 * @param exArgs
 */
  public StorageResolverException(  String _msgID,  Object exArgs[]){
    super(_msgID,exArgs);
  }
  /** 
 * Constructor StorageResolverException
 * @param _msgID
 * @param _originalException
 */
  public StorageResolverException(  String _msgID,  Exception _originalException){
    super(_msgID,_originalException);
  }
  /** 
 * Constructor StorageResolverException
 * @param _msgID
 * @param exArgs
 * @param _originalException
 */
  public StorageResolverException(  String _msgID,  Object exArgs[],  Exception _originalException){
    super(_msgID,exArgs,_originalException);
  }
}
