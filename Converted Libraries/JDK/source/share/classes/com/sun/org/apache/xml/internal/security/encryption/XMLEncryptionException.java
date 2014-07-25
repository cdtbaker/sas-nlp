package com.sun.org.apache.xml.internal.security.encryption;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
/** 
 */
public class XMLEncryptionException extends XMLSecurityException {
  /** 
 */
  private static final long serialVersionUID=1L;
  /** 
 */
  public XMLEncryptionException(){
    super();
  }
  /** 
 * @param _msgID
 */
  public XMLEncryptionException(  String _msgID){
    super(_msgID);
  }
  /** 
 * @param _msgID
 * @param exArgs
 */
  public XMLEncryptionException(  String _msgID,  Object exArgs[]){
    super(_msgID,exArgs);
  }
  /** 
 * @param _msgID
 * @param _originalException
 */
  public XMLEncryptionException(  String _msgID,  Exception _originalException){
    super(_msgID,_originalException);
  }
  /** 
 * @param _msgID
 * @param exArgs
 * @param _originalException
 */
  public XMLEncryptionException(  String _msgID,  Object exArgs[],  Exception _originalException){
    super(_msgID,exArgs,_originalException);
  }
}
