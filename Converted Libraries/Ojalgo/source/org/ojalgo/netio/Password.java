package org.ojalgo.netio;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.ojalgo.random.Uniform;
/** 
 * Password
 * @author apete
 */
public class Password {
  private static MessageDigest INSTANCE;
  /** 
 * @param aPassword An unencrypted (plain text) password
 * @return An encrypted password
 */
  public static String encrypt(  final String aPassword){
    String retVal=null;
    final MessageDigest tmpDigest=Password.getInstance();
    if (aPassword != null) {
      final byte[] tmpBytes=tmpDigest.digest(aPassword.getBytes());
      for (int i=0; i < tmpBytes.length; i++) {
        if (tmpBytes[i] < 0) {
          tmpBytes[i]=(byte)(tmpBytes[i] + 128);
        }
        if (tmpBytes[i] < 32) {
          tmpBytes[i]=(byte)(tmpBytes[i] + 32);
        }
        if ((tmpBytes[i] == 34) || (tmpBytes[i] == 38) || (tmpBytes[i] == 39)|| (tmpBytes[i] == 47)|| (tmpBytes[i] == 60)|| (tmpBytes[i] == 62)|| (tmpBytes[i] == 92)) {
          tmpBytes[i]=32;
        }
      }
      retVal=new String(tmpBytes).trim();
    }
    return retVal;
  }
  /** 
 * @param aPassword An unencrypted (plain text) password
 * @param aToBytesEncoding
 * @param aFromBytesEncoding
 * @return An encrypted password
 */
  public static String encrypt(  final String aPassword,  final String aToBytesEncoding,  final String aFromBytesEncoding){
    String retVal=null;
    final MessageDigest tmpDigest=Password.getInstance();
    if (aPassword != null) {
      try {
        final byte[] tmpBytes=tmpDigest.digest(aPassword.getBytes(aToBytesEncoding));
        retVal=new String(tmpBytes,aFromBytesEncoding).trim();
      }
 catch (      final UnsupportedEncodingException anE) {
        BasicLogger.logError(anE.toString());
      }
    }
    return retVal;
  }
  public static String makeClearText(  final int length){
    final char[] retVal=new char[length];
    final Uniform tmpRandom=new Uniform(0,128);
    for (int c=0; c < length; c++) {
      int tmpChar=ASCII.NBSP;
      do {
        tmpChar=tmpRandom.intValue();
      }
 while (!ASCII.isAlphanumeric(tmpChar));
      retVal[c]=(char)tmpChar;
    }
    return String.valueOf(retVal);
  }
  private static MessageDigest getInstance(){
    if (INSTANCE == null) {
      try {
        INSTANCE=MessageDigest.getInstance("MD5");
      }
 catch (      final NoSuchAlgorithmException anE) {
        BasicLogger.logError(anE.toString());
      }
    }
    return INSTANCE;
  }
  protected Password(){
    super();
  }
}
