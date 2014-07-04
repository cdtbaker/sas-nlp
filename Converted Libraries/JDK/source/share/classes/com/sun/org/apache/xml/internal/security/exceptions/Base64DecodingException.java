package com.sun.org.apache.xml.internal.security.exceptions;
/** 
 * This Exception is thrown if decoding of Base64 data fails.
 * @author Christian Geuer-Pollmann
 */
public class Base64DecodingException extends XMLSecurityException {
  /** 
 */
  private static final long serialVersionUID=1L;
  /** 
 * Constructor Base64DecodingException
 */
  public Base64DecodingException(){
    super();
  }
  /** 
 * Constructor Base64DecodingException
 * @param _msgID
 */
  public Base64DecodingException(  String _msgID){
    super(_msgID);
  }
  /** 
 * Constructor Base64DecodingException
 * @param _msgID
 * @param exArgs
 */
  public Base64DecodingException(  String _msgID,  Object exArgs[]){
    super(_msgID,exArgs);
  }
  /** 
 * Constructor Base64DecodingException
 * @param _msgID
 * @param _originalException
 */
  public Base64DecodingException(  String _msgID,  Exception _originalException){
    super(_msgID,_originalException);
  }
  /** 
 * Constructor Base64DecodingException
 * @param _msgID
 * @param exArgs
 * @param _originalException
 */
  public Base64DecodingException(  String _msgID,  Object exArgs[],  Exception _originalException){
    super(_msgID,exArgs,_originalException);
  }
}
