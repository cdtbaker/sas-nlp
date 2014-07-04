package com.sun.org.apache.xml.internal.security.signature;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
/** 
 * All XML Signature related exceptions inherit herefrom.
 * @see MissingResourceFailureException InvalidDigestValueException InvalidSignatureValueException
 * @author Christian Geuer-Pollmann
 */
public class XMLSignatureException extends XMLSecurityException {
  /** 
 */
  private static final long serialVersionUID=1L;
  /** 
 * Constructor XMLSignatureException
 */
  public XMLSignatureException(){
    super();
  }
  /** 
 * Constructor XMLSignatureException
 * @param _msgID
 */
  public XMLSignatureException(  String _msgID){
    super(_msgID);
  }
  /** 
 * Constructor XMLSignatureException
 * @param _msgID
 * @param exArgs
 */
  public XMLSignatureException(  String _msgID,  Object exArgs[]){
    super(_msgID,exArgs);
  }
  /** 
 * Constructor XMLSignatureException
 * @param _msgID
 * @param _originalException
 */
  public XMLSignatureException(  String _msgID,  Exception _originalException){
    super(_msgID,_originalException);
  }
  /** 
 * Constructor XMLSignatureException
 * @param _msgID
 * @param exArgs
 * @param _originalException
 */
  public XMLSignatureException(  String _msgID,  Object exArgs[],  Exception _originalException){
    super(_msgID,exArgs,_originalException);
  }
}
