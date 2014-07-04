package com.sun.org.apache.xml.internal.security.signature;
/** 
 * Raised when the computed hash value doesn't match the given <i>DigestValue</i>.  Additional human readable info is passed to the constructor -- this being the benefit of raising an exception or returning a value.
 * @author Christian Geuer-Pollmann
 */
public class InvalidDigestValueException extends XMLSignatureException {
  /** 
 */
  private static final long serialVersionUID=1L;
  /** 
 * Constructor InvalidDigestValueException
 */
  public InvalidDigestValueException(){
    super();
  }
  /** 
 * Constructor InvalidDigestValueException
 * @param _msgID
 */
  public InvalidDigestValueException(  String _msgID){
    super(_msgID);
  }
  /** 
 * Constructor InvalidDigestValueException
 * @param _msgID
 * @param exArgs
 */
  public InvalidDigestValueException(  String _msgID,  Object exArgs[]){
    super(_msgID,exArgs);
  }
  /** 
 * Constructor InvalidDigestValueException
 * @param _msgID
 * @param _originalException
 */
  public InvalidDigestValueException(  String _msgID,  Exception _originalException){
    super(_msgID,_originalException);
  }
  /** 
 * Constructor InvalidDigestValueException
 * @param _msgID
 * @param exArgs
 * @param _originalException
 */
  public InvalidDigestValueException(  String _msgID,  Object exArgs[],  Exception _originalException){
    super(_msgID,exArgs,_originalException);
  }
}
