package com.sun.org.apache.xml.internal.security.keys.content.keyvalues;
import java.security.PublicKey;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
/** 
 * @author $Author: mullan $
 */
public interface KeyValueContent {
  /** 
 * Method getPublicKey
 * @return the public key
 * @throws XMLSecurityException
 */
  public PublicKey getPublicKey() throws XMLSecurityException ;
}
