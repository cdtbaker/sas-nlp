package com.sun.org.apache.xml.internal.security.c14n;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
/** 
 * @author Christian Geuer-Pollmann
 */
public class InvalidCanonicalizerException extends XMLSecurityException {
  /** 
 */
  private static final long serialVersionUID=1L;
  /** 
 * Constructor InvalidCanonicalizerException
 */
  public InvalidCanonicalizerException(){
    super();
  }
  /** 
 * Constructor InvalidCanonicalizerException
 * @param _msgID
 */
  public InvalidCanonicalizerException(  String _msgID){
    super(_msgID);
  }
  /** 
 * Constructor InvalidCanonicalizerException
 * @param _msgID
 * @param exArgs
 */
  public InvalidCanonicalizerException(  String _msgID,  Object exArgs[]){
    super(_msgID,exArgs);
  }
  /** 
 * Constructor InvalidCanonicalizerException
 * @param _msgID
 * @param _originalException
 */
  public InvalidCanonicalizerException(  String _msgID,  Exception _originalException){
    super(_msgID,_originalException);
  }
  /** 
 * Constructor InvalidCanonicalizerException
 * @param _msgID
 * @param exArgs
 * @param _originalException
 */
  public InvalidCanonicalizerException(  String _msgID,  Object exArgs[],  Exception _originalException){
    super(_msgID,exArgs,_originalException);
  }
}
