package com.sun.org.apache.xml.internal.security.signature;
/** 
 * Raised if testing the signature value over <i>DigestValue</i> fails because of invalid signature.
 * @see InvalidDigestValueException  MissingKeyFailureException  MissingResourceFailureException
 * @author Christian Geuer-Pollmann
 */
public class InvalidSignatureValueException extends XMLSignatureException {
  /** 
 */
  private static final long serialVersionUID=1L;
  /** 
 * Constructor InvalidSignatureValueException
 */
  public InvalidSignatureValueException(){
    super();
  }
  /** 
 * Constructor InvalidSignatureValueException
 * @param _msgID
 */
  public InvalidSignatureValueException(  String _msgID){
    super(_msgID);
  }
  /** 
 * Constructor InvalidSignatureValueException
 * @param _msgID
 * @param exArgs
 */
  public InvalidSignatureValueException(  String _msgID,  Object exArgs[]){
    super(_msgID,exArgs);
  }
  /** 
 * Constructor InvalidSignatureValueException
 * @param _msgID
 * @param _originalException
 */
  public InvalidSignatureValueException(  String _msgID,  Exception _originalException){
    super(_msgID,_originalException);
  }
  /** 
 * Constructor InvalidSignatureValueException
 * @param _msgID
 * @param exArgs
 * @param _originalException
 */
  public InvalidSignatureValueException(  String _msgID,  Object exArgs[],  Exception _originalException){
    super(_msgID,exArgs,_originalException);
  }
}
