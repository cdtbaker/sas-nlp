package com.sun.org.apache.xml.internal.security.encryption;
import java.io.IOException;
import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolver;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolverException;
import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.transforms.TransformationException;
import org.w3c.dom.Attr;
import com.sun.org.apache.xml.internal.security.utils.Base64;
/** 
 * <code>XMLCipherInput</code> is used to wrap input passed into the
 * XMLCipher encryption operations.
 * In decryption mode, it takes a <code>CipherData</code> object and allows
 * callers to dereference the CipherData into the encrypted bytes that it
 * actually represents.  This takes care of all base64 encoding etc.
 * While primarily an internal class, this can be used by applications to
 * quickly and easily retrieve the encrypted bytes from an EncryptedType
 * object
 * @author Berin Lautenbach
 */
public class XMLCipherInput {
  private static java.util.logging.Logger logger=java.util.logging.Logger.getLogger(XMLCipher.class.getName());
  /** 
 * The data we are working with 
 */
  private CipherData _cipherData;
  /** 
 * MODES 
 */
  private int _mode;
  /** 
 * Constructor for processing encrypted octets
 * @param data The <code>CipherData</code> object to read the bytes from
 * @throws XMLEncryptionException {@link XMLEncryptionException}
 */
  public XMLCipherInput(  CipherData data) throws XMLEncryptionException {
    _cipherData=data;
    _mode=XMLCipher.DECRYPT_MODE;
    if (_cipherData == null) {
      throw new XMLEncryptionException("CipherData is null");
    }
  }
  /** 
 * Constructor for processing encrypted octets
 * @param input The <code>EncryptedType</code> object to read
 * the bytes from.
 * @throws XMLEncryptionException {@link XMLEncryptionException}
 */
  public XMLCipherInput(  EncryptedType input) throws XMLEncryptionException {
    _cipherData=((input == null) ? null : input.getCipherData());
    _mode=XMLCipher.DECRYPT_MODE;
    if (_cipherData == null) {
      throw new XMLEncryptionException("CipherData is null");
    }
  }
  /** 
 * Dereferences the input and returns it as a single byte array.
 * @throws XMLEncryptionException
 * @return The decripted bytes.
 */
  public byte[] getBytes() throws XMLEncryptionException {
    if (_mode == XMLCipher.DECRYPT_MODE) {
      return getDecryptBytes();
    }
    return null;
  }
  /** 
 * Internal method to get bytes in decryption mode
 * @return the decripted bytes
 * @throws XMLEncryptionException
 */
  private byte[] getDecryptBytes() throws XMLEncryptionException {
    String base64EncodedEncryptedOctets=null;
    if (_cipherData.getDataType() == CipherData.REFERENCE_TYPE) {
      logger.log(java.util.logging.Level.FINE,"Found a reference type CipherData");
      CipherReference cr=_cipherData.getCipherReference();
      Attr uriAttr=cr.getURIAsAttr();
      XMLSignatureInput input=null;
      try {
        ResourceResolver resolver=ResourceResolver.getInstance(uriAttr,null);
        input=resolver.resolve(uriAttr,null);
      }
 catch (      ResourceResolverException ex) {
        throw new XMLEncryptionException("empty",ex);
      }
      if (input != null) {
        logger.log(java.util.logging.Level.FINE,"Managed to resolve URI \"" + cr.getURI() + "\"");
      }
 else {
        logger.log(java.util.logging.Level.FINE,"Failed to resolve URI \"" + cr.getURI() + "\"");
      }
      Transforms transforms=cr.getTransforms();
      if (transforms != null) {
        logger.log(java.util.logging.Level.FINE,"Have transforms in cipher reference");
        try {
          com.sun.org.apache.xml.internal.security.transforms.Transforms dsTransforms=transforms.getDSTransforms();
          input=dsTransforms.performTransforms(input);
        }
 catch (        TransformationException ex) {
          throw new XMLEncryptionException("empty",ex);
        }
      }
      try {
        return input.getBytes();
      }
 catch (      IOException ex) {
        throw new XMLEncryptionException("empty",ex);
      }
catch (      CanonicalizationException ex) {
        throw new XMLEncryptionException("empty",ex);
      }
    }
 else     if (_cipherData.getDataType() == CipherData.VALUE_TYPE) {
      base64EncodedEncryptedOctets=_cipherData.getCipherValue().getValue();
    }
 else {
      throw new XMLEncryptionException("CipherData.getDataType() returned unexpected value");
    }
    logger.log(java.util.logging.Level.FINE,"Encrypted octets:\n" + base64EncodedEncryptedOctets);
    byte[] encryptedBytes=null;
    try {
      encryptedBytes=Base64.decode(base64EncodedEncryptedOctets);
    }
 catch (    Base64DecodingException bde) {
      throw new XMLEncryptionException("empty",bde);
    }
    return (encryptedBytes);
  }
}
