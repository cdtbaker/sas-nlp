package com.sun.org.apache.xml.internal.security.encryption;
/** 
 * <code>CipherValue</code> is the wrapper for cipher text.
 * @author Axl Mattheus
 */
public interface CipherValue {
  /** 
 * Resturns the Base 64 encoded, encrypted octets that is the
 * <code>CihperValue</code>.
 * @return cipher value.
 */
  String getValue();
  /** 
 * Sets the Base 64 encoded, encrypted octets that is the
 * <code>CihperValue</code>.
 * @param value the cipher value.
 */
  void setValue(  String value);
}
