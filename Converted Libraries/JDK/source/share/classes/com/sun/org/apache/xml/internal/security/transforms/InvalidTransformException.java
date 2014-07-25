package com.sun.org.apache.xml.internal.security.transforms;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
/** 
 * @author Christian Geuer-Pollmann
 */
public class InvalidTransformException extends XMLSecurityException {
  /** 
 */
  private static final long serialVersionUID=1L;
  /** 
 * Constructor InvalidTransformException
 */
  public InvalidTransformException(){
    super();
  }
  /** 
 * Constructor InvalidTransformException
 * @param _msgId
 */
  public InvalidTransformException(  String _msgId){
    super(_msgId);
  }
  /** 
 * Constructor InvalidTransformException
 * @param _msgId
 * @param exArgs
 */
  public InvalidTransformException(  String _msgId,  Object exArgs[]){
    super(_msgId,exArgs);
  }
  /** 
 * Constructor InvalidTransformException
 * @param _msgId
 * @param _originalException
 */
  public InvalidTransformException(  String _msgId,  Exception _originalException){
    super(_msgId,_originalException);
  }
  /** 
 * Constructor InvalidTransformException
 * @param _msgId
 * @param exArgs
 * @param _originalException
 */
  public InvalidTransformException(  String _msgId,  Object exArgs[],  Exception _originalException){
    super(_msgId,exArgs,_originalException);
  }
}
