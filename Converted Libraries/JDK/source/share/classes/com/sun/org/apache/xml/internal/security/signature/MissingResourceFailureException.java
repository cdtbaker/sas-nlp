package com.sun.org.apache.xml.internal.security.signature;
/** 
 * Thrown by {@link com.sun.org.apache.xml.internal.security.signature.SignedInfo#verify()} when
 * testing the signature fails because of uninitialized{@link com.sun.org.apache.xml.internal.security.signature.Reference}s.
 * @author Christian Geuer-Pollmann
 * @see ReferenceNotInitializedException
 */
public class MissingResourceFailureException extends XMLSignatureException {
  /** 
 */
  private static final long serialVersionUID=1L;
  /** 
 * Field uninitializedReference 
 */
  Reference uninitializedReference=null;
  /** 
 * MissingKeyResourceFailureException constructor.
 * @param _msgID
 * @param reference
 * @see #getReference
 */
  public MissingResourceFailureException(  String _msgID,  Reference reference){
    super(_msgID);
    this.uninitializedReference=reference;
  }
  /** 
 * Constructor MissingResourceFailureException
 * @param _msgID
 * @param exArgs
 * @param reference
 * @see #getReference
 */
  public MissingResourceFailureException(  String _msgID,  Object exArgs[],  Reference reference){
    super(_msgID,exArgs);
    this.uninitializedReference=reference;
  }
  /** 
 * Constructor MissingResourceFailureException
 * @param _msgID
 * @param _originalException
 * @param reference
 * @see #getReference
 */
  public MissingResourceFailureException(  String _msgID,  Exception _originalException,  Reference reference){
    super(_msgID,_originalException);
    this.uninitializedReference=reference;
  }
  /** 
 * Constructor MissingResourceFailureException
 * @param _msgID
 * @param exArgs
 * @param _originalException
 * @param reference
 * @see #getReference
 */
  public MissingResourceFailureException(  String _msgID,  Object exArgs[],  Exception _originalException,  Reference reference){
    super(_msgID,exArgs,_originalException);
    this.uninitializedReference=reference;
  }
  /** 
 * used to set the uninitialized {@link com.sun.org.apache.xml.internal.security.signature.Reference}
 * @param reference the Reference object
 * @see #getReference
 */
  public void setReference(  Reference reference){
    this.uninitializedReference=reference;
  }
  /** 
 * used to get the uninitialized {@link com.sun.org.apache.xml.internal.security.signature.Reference}This allows to supply the correct {@link com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput}to the {@link com.sun.org.apache.xml.internal.security.signature.Reference} to try again verification.
 * @return the Reference object
 * @see #setReference
 */
  public Reference getReference(){
    return this.uninitializedReference;
  }
}
