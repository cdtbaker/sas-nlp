package com.sun.org.apache.xml.internal.security.c14n;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
/** 
 * Class CanonicalizationException
 * @author Christian Geuer-Pollmann
 */
public class CanonicalizationException extends XMLSecurityException {
  /** 
 */
  private static final long serialVersionUID=1L;
  /** 
 * Constructor CanonicalizationException
 */
  public CanonicalizationException(){
    super();
  }
  /** 
 * Constructor CanonicalizationException
 * @param _msgID
 */
  public CanonicalizationException(  String _msgID){
    super(_msgID);
  }
  /** 
 * Constructor CanonicalizationException
 * @param _msgID
 * @param exArgs
 */
  public CanonicalizationException(  String _msgID,  Object exArgs[]){
    super(_msgID,exArgs);
  }
  /** 
 * Constructor CanonicalizationException
 * @param _msgID
 * @param _originalException
 */
  public CanonicalizationException(  String _msgID,  Exception _originalException){
    super(_msgID,_originalException);
  }
  /** 
 * Constructor CanonicalizationException
 * @param _msgID
 * @param exArgs
 * @param _originalException
 */
  public CanonicalizationException(  String _msgID,  Object exArgs[],  Exception _originalException){
    super(_msgID,exArgs,_originalException);
  }
}
