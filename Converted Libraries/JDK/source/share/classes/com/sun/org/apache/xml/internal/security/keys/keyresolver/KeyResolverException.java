package com.sun.org.apache.xml.internal.security.keys.keyresolver;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
/** 
 * @author $Author: mullan $
 */
public class KeyResolverException extends XMLSecurityException {
  /** 
 */
  private static final long serialVersionUID=1L;
  /** 
 * Constructor KeyResolverException
 */
  public KeyResolverException(){
    super();
  }
  /** 
 * Constructor KeyResolverException
 * @param _msgID
 */
  public KeyResolverException(  String _msgID){
    super(_msgID);
  }
  /** 
 * Constructor KeyResolverException
 * @param _msgID
 * @param exArgs
 */
  public KeyResolverException(  String _msgID,  Object exArgs[]){
    super(_msgID,exArgs);
  }
  /** 
 * Constructor KeyResolverException
 * @param _msgID
 * @param _originalException
 */
  public KeyResolverException(  String _msgID,  Exception _originalException){
    super(_msgID,_originalException);
  }
  /** 
 * Constructor KeyResolverException
 * @param _msgID
 * @param exArgs
 * @param _originalException
 */
  public KeyResolverException(  String _msgID,  Object exArgs[],  Exception _originalException){
    super(_msgID,exArgs,_originalException);
  }
}
