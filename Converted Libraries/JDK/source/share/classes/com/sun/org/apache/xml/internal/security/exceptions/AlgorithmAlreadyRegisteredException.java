package com.sun.org.apache.xml.internal.security.exceptions;
/** 
 * @author Christian Geuer-Pollmann
 */
public class AlgorithmAlreadyRegisteredException extends XMLSecurityException {
  /** 
 */
  private static final long serialVersionUID=1L;
  /** 
 * Constructor AlgorithmAlreadyRegisteredException
 */
  public AlgorithmAlreadyRegisteredException(){
    super();
  }
  /** 
 * Constructor AlgorithmAlreadyRegisteredException
 * @param _msgID
 */
  public AlgorithmAlreadyRegisteredException(  String _msgID){
    super(_msgID);
  }
  /** 
 * Constructor AlgorithmAlreadyRegisteredException
 * @param _msgID
 * @param exArgs
 */
  public AlgorithmAlreadyRegisteredException(  String _msgID,  Object exArgs[]){
    super(_msgID,exArgs);
  }
  /** 
 * Constructor AlgorithmAlreadyRegisteredException
 * @param _msgID
 * @param _originalException
 */
  public AlgorithmAlreadyRegisteredException(  String _msgID,  Exception _originalException){
    super(_msgID,_originalException);
  }
  /** 
 * Constructor AlgorithmAlreadyRegisteredException
 * @param _msgID
 * @param exArgs
 * @param _originalException
 */
  public AlgorithmAlreadyRegisteredException(  String _msgID,  Object exArgs[],  Exception _originalException){
    super(_msgID,exArgs,_originalException);
  }
}
