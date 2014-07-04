package com.sun.org.apache.xml.internal.security.signature;
/** 
 * Raised if verifying a {@link com.sun.org.apache.xml.internal.security.signature.Reference} fails
 * because of an uninitialized {@link com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput}
 * @author Christian Geuer-Pollmann
 */
public class ReferenceNotInitializedException extends XMLSignatureException {
  /** 
 */
  private static final long serialVersionUID=1L;
  /** 
 * Constructor ReferenceNotInitializedException
 */
  public ReferenceNotInitializedException(){
    super();
  }
  /** 
 * Constructor ReferenceNotInitializedException
 * @param _msgID
 */
  public ReferenceNotInitializedException(  String _msgID){
    super(_msgID);
  }
  /** 
 * Constructor ReferenceNotInitializedException
 * @param _msgID
 * @param exArgs
 */
  public ReferenceNotInitializedException(  String _msgID,  Object exArgs[]){
    super(_msgID,exArgs);
  }
  /** 
 * Constructor ReferenceNotInitializedException
 * @param _msgID
 * @param _originalException
 */
  public ReferenceNotInitializedException(  String _msgID,  Exception _originalException){
    super(_msgID,_originalException);
  }
  /** 
 * Constructor ReferenceNotInitializedException
 * @param _msgID
 * @param exArgs
 * @param _originalException
 */
  public ReferenceNotInitializedException(  String _msgID,  Object exArgs[],  Exception _originalException){
    super(_msgID,exArgs,_originalException);
  }
}
