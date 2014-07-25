package com.sun.org.apache.xml.internal.security.keys;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
/** 
 * @author $Author: mullan $
 */
public class ContentHandlerAlreadyRegisteredException extends XMLSecurityException {
  /** 
 */
  private static final long serialVersionUID=1L;
  /** 
 * Constructor ContentHandlerAlreadyRegisteredException
 */
  public ContentHandlerAlreadyRegisteredException(){
    super();
  }
  /** 
 * Constructor ContentHandlerAlreadyRegisteredException
 * @param _msgID
 */
  public ContentHandlerAlreadyRegisteredException(  String _msgID){
    super(_msgID);
  }
  /** 
 * Constructor ContentHandlerAlreadyRegisteredException
 * @param _msgID
 * @param exArgs
 */
  public ContentHandlerAlreadyRegisteredException(  String _msgID,  Object exArgs[]){
    super(_msgID,exArgs);
  }
  /** 
 * Constructor ContentHandlerAlreadyRegisteredException
 * @param _msgID
 * @param _originalException
 */
  public ContentHandlerAlreadyRegisteredException(  String _msgID,  Exception _originalException){
    super(_msgID,_originalException);
  }
  /** 
 * Constructor ContentHandlerAlreadyRegisteredException
 * @param _msgID
 * @param exArgs
 * @param _originalException
 */
  public ContentHandlerAlreadyRegisteredException(  String _msgID,  Object exArgs[],  Exception _originalException){
    super(_msgID,exArgs,_originalException);
  }
}
