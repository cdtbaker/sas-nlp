package com.sun.org.apache.xml.internal.security.transforms;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
/** 
 * @author Christian Geuer-Pollmann
 */
public class TransformationException extends XMLSecurityException {
  /** 
 */
  private static final long serialVersionUID=1L;
  /** 
 * Constructor TransformationException
 */
  public TransformationException(){
    super();
  }
  /** 
 * Constructor TransformationException
 * @param _msgID
 */
  public TransformationException(  String _msgID){
    super(_msgID);
  }
  /** 
 * Constructor TransformationException
 * @param _msgID
 * @param exArgs
 */
  public TransformationException(  String _msgID,  Object exArgs[]){
    super(_msgID,exArgs);
  }
  /** 
 * Constructor TransformationException
 * @param _msgID
 * @param _originalException
 */
  public TransformationException(  String _msgID,  Exception _originalException){
    super(_msgID,_originalException);
  }
  /** 
 * Constructor TransformationException
 * @param _msgID
 * @param exArgs
 * @param _originalException
 */
  public TransformationException(  String _msgID,  Object exArgs[],  Exception _originalException){
    super(_msgID,exArgs,_originalException);
  }
}
