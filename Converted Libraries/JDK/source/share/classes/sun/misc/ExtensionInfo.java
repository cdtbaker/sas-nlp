package sun.misc;
import java.util.StringTokenizer;
import java.util.jar.Attributes;
import java.util.jar.Attributes.Name;
import java.util.ResourceBundle;
import java.util.MissingResourceException;
import java.text.MessageFormat;
import java.lang.Character.*;
/** 
 * This class holds all necessary information to install or
 * upgrade a extension on the user's disk
 * @author  Jerome Dochez
 */
public class ExtensionInfo {
  /** 
 * <p>
 * public static values returned by the isCompatible method
 * </p>
 */
  public static final int COMPATIBLE=0;
  public static final int REQUIRE_SPECIFICATION_UPGRADE=1;
  public static final int REQUIRE_IMPLEMENTATION_UPGRADE=2;
  public static final int REQUIRE_VENDOR_SWITCH=3;
  public static final int INCOMPATIBLE=4;
  /** 
 * <p>
 * attributes fully describer an extension. The underlying described
 * extension may be installed and requested.
 * <p>
 */
  public String title;
  public String name;
  public String specVersion;
  public String specVendor;
  public String implementationVersion;
  public String vendor;
  public String vendorId;
  public String url;
  private static final ResourceBundle rb=ResourceBundle.getBundle("sun.misc.resources.Messages");
  /** 
 * <p>
 * Create a new uninitialized extension information object
 * </p>
 */
  public ExtensionInfo(){
  }
  /** 
 * <p>
 * Create and initialize an extension information object.
 * The initialization uses the attributes passed as being
 * the content of a manifest file to load the extension
 * information from.
 * Since manifest file may contain information on several
 * extension they may depend on, the extension key parameter
 * is prepanded to the attribute name to make the key used
 * to retrieve the attribute from the manifest file
 * <p>
 * @param extensionKey unique extension key in the manifest
 * @param attr Attributes of a manifest file
 */
  public ExtensionInfo(  String extensionKey,  Attributes attr) throws NullPointerException {
    String s;
    if (extensionKey != null) {
      s=extensionKey + "-";
    }
 else {
      s="";
    }
    String attrKey=s + Name.EXTENSION_NAME.toString();
    name=attr.getValue(attrKey);
    if (name != null)     name=name.trim();
    attrKey=s + Name.SPECIFICATION_TITLE.toString();
    title=attr.getValue(attrKey);
    if (title != null)     title=title.trim();
    attrKey=s + Name.SPECIFICATION_VERSION.toString();
    specVersion=attr.getValue(attrKey);
    if (specVersion != null)     specVersion=specVersion.trim();
    attrKey=s + Name.SPECIFICATION_VENDOR.toString();
    specVendor=attr.getValue(attrKey);
    if (specVendor != null)     specVendor=specVendor.trim();
    attrKey=s + Name.IMPLEMENTATION_VERSION.toString();
    implementationVersion=attr.getValue(attrKey);
    if (implementationVersion != null)     implementationVersion=implementationVersion.trim();
    attrKey=s + Name.IMPLEMENTATION_VENDOR.toString();
    vendor=attr.getValue(attrKey);
    if (vendor != null)     vendor=vendor.trim();
    attrKey=s + Name.IMPLEMENTATION_VENDOR_ID.toString();
    vendorId=attr.getValue(attrKey);
    if (vendorId != null)     vendorId=vendorId.trim();
    attrKey=s + Name.IMPLEMENTATION_URL.toString();
    url=attr.getValue(attrKey);
    if (url != null)     url=url.trim();
  }
  /** 
 * <p>
 * @return true if the extension described by this extension information
 * is compatible with the extension described by the extension
 * information passed as a parameter
 * </p>
 * @param the requested extension information to compare to
 */
  public int isCompatibleWith(  ExtensionInfo ei){
    if (name == null || ei.name == null)     return INCOMPATIBLE;
    if (name.compareTo(ei.name) == 0) {
      if (specVersion == null || ei.specVersion == null)       return COMPATIBLE;
      int version=compareExtensionVersion(specVersion,ei.specVersion);
      if (version < 0) {
        if (vendorId != null && ei.vendorId != null) {
          if (vendorId.compareTo(ei.vendorId) != 0) {
            return REQUIRE_VENDOR_SWITCH;
          }
        }
        return REQUIRE_SPECIFICATION_UPGRADE;
      }
 else {
        if (vendorId != null && ei.vendorId != null) {
          if (vendorId.compareTo(ei.vendorId) != 0) {
            return REQUIRE_VENDOR_SWITCH;
          }
 else {
            if (implementationVersion != null && ei.implementationVersion != null) {
              version=compareExtensionVersion(implementationVersion,ei.implementationVersion);
              if (version < 0) {
                return REQUIRE_IMPLEMENTATION_UPGRADE;
              }
            }
          }
        }
        return COMPATIBLE;
      }
    }
    return INCOMPATIBLE;
  }
  /** 
 * <p>
 * helper method to print sensible information on the undelying described
 * extension
 * </p>
 */
  public String toString(){
    return "Extension : title(" + title + "), name("+ name+ "), spec vendor("+ specVendor+ "), spec version("+ specVersion+ "), impl vendor("+ vendor+ "), impl vendor id("+ vendorId+ "), impl version("+ implementationVersion+ "), impl url("+ url+ ")";
  }
  private int compareExtensionVersion(  String source,  String target) throws NumberFormatException {
    source=source.toLowerCase();
    target=target.toLowerCase();
    return strictCompareExtensionVersion(source,target);
  }
  private int strictCompareExtensionVersion(  String source,  String target) throws NumberFormatException {
    if (source.equals(target))     return 0;
    StringTokenizer stk=new StringTokenizer(source,".,");
    StringTokenizer ttk=new StringTokenizer(target,".,");
    int n=0, m=0, result=0;
    if (stk.hasMoreTokens())     n=convertToken(stk.nextToken().toString());
    if (ttk.hasMoreTokens())     m=convertToken(ttk.nextToken().toString());
    if (n > m)     return 1;
 else     if (m > n)     return -1;
 else {
      int sIdx=source.indexOf(".");
      int tIdx=target.indexOf(".");
      if (sIdx == -1)       sIdx=source.length() - 1;
      if (tIdx == -1)       tIdx=target.length() - 1;
      return strictCompareExtensionVersion(source.substring(sIdx + 1),target.substring(tIdx + 1));
    }
  }
  private int convertToken(  String token){
    if (token == null || token.equals(""))     return 0;
    int charValue=0;
    int charVersion=0;
    int patchVersion=0;
    int strLength=token.length();
    int endIndex=strLength;
    char lastChar;
    Object[] args={name};
    MessageFormat mf=new MessageFormat(rb.getString("optpkg.versionerror"));
    String versionError=mf.format(args);
    int prIndex=token.indexOf("-");
    int patchIndex=token.indexOf("_");
    if (prIndex == -1 && patchIndex == -1) {
      try {
        return Integer.parseInt(token) * 100;
      }
 catch (      NumberFormatException e) {
        System.out.println(versionError);
        return 0;
      }
    }
 else     if (patchIndex != -1) {
      int prversion;
      try {
        prversion=Integer.parseInt(token.substring(0,patchIndex));
        lastChar=token.charAt(strLength - 1);
        if (Character.isLetter(lastChar)) {
          charValue=Character.getNumericValue(lastChar);
          endIndex=strLength - 1;
          patchVersion=Integer.parseInt(token.substring(patchIndex + 1,endIndex));
          if (charValue >= Character.getNumericValue('a') && charValue <= Character.getNumericValue('z')) {
            charVersion=(patchVersion * 100) + charValue;
          }
 else {
            charVersion=0;
            System.out.println(versionError);
          }
        }
 else {
          patchVersion=Integer.parseInt(token.substring(patchIndex + 1,endIndex));
        }
      }
 catch (      NumberFormatException e) {
        System.out.println(versionError);
        return 0;
      }
      return prversion * 100 + (patchVersion + charVersion);
    }
 else {
      int mrversion;
      try {
        mrversion=Integer.parseInt(token.substring(0,prIndex));
      }
 catch (      NumberFormatException e) {
        System.out.println(versionError);
        return 0;
      }
      String prString=token.substring(prIndex + 1);
      String msVersion="";
      int delta=0;
      if (prString.indexOf("ea") != -1) {
        msVersion=prString.substring(2);
        delta=50;
      }
 else       if (prString.indexOf("alpha") != -1) {
        msVersion=prString.substring(5);
        delta=40;
      }
 else       if (prString.indexOf("beta") != -1) {
        msVersion=prString.substring(4);
        delta=30;
      }
 else       if (prString.indexOf("rc") != -1) {
        msVersion=prString.substring(2);
        delta=20;
      }
      if (msVersion == null || msVersion.equals("")) {
        return mrversion * 100 - delta;
      }
 else {
        try {
          return mrversion * 100 - delta + Integer.parseInt(msVersion);
        }
 catch (        NumberFormatException e) {
          System.out.println(versionError);
          return 0;
        }
      }
    }
  }
}
