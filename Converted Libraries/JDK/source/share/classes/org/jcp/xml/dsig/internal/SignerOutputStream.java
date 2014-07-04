package org.jcp.xml.dsig.internal;
import java.io.ByteArrayOutputStream;
import java.security.Signature;
import java.security.SignatureException;
/** 
 * Derived from Apache sources and changed to use java.security.Signature
 * objects as input instead of com.sun.org.apache.xml.internal.security.algorithms.SignatureAlgorithm
 * objects.
 * @author raul
 * @author Sean Mullan
 */
public class SignerOutputStream extends ByteArrayOutputStream {
  private final Signature sig;
  public SignerOutputStream(  Signature sig){
    this.sig=sig;
  }
  /** 
 * @inheritDoc 
 */
  public void write(  byte[] arg0){
    super.write(arg0,0,arg0.length);
    try {
      sig.update(arg0);
    }
 catch (    SignatureException e) {
      throw new RuntimeException("" + e);
    }
  }
  /** 
 * @inheritDoc 
 */
  public void write(  int arg0){
    super.write(arg0);
    try {
      sig.update((byte)arg0);
    }
 catch (    SignatureException e) {
      throw new RuntimeException("" + e);
    }
  }
  /** 
 * @inheritDoc 
 */
  public void write(  byte[] arg0,  int arg1,  int arg2){
    super.write(arg0,arg1,arg2);
    try {
      sig.update(arg0,arg1,arg2);
    }
 catch (    SignatureException e) {
      throw new RuntimeException("" + e);
    }
  }
}
