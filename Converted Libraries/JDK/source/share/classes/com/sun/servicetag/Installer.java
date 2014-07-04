package com.sun.servicetag;
import java.io.*;
import java.util.HashSet;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import static com.sun.servicetag.Util.*;
/** 
 * Service Tag Installer for Java SE.
 */
public class Installer {
  private static String SVCTAG_DIR_PATH="servicetag.dir.path";
  private static String SVCTAG_ENABLE_REGISTRATION="servicetag.registration.enabled";
  private final static String ORACLE="Oracle";
  private final static String SUN="Sun Microsystems";
  private final static String REGISTRATION_XML="registration.xml";
  private final static String SERVICE_TAG_FILE="servicetag";
  private final static String REGISTRATION_HTML_NAME="register";
  private final static Locale[] knownSupportedLocales=new Locale[]{Locale.ENGLISH,Locale.JAPANESE,Locale.SIMPLIFIED_CHINESE};
  private final static String javaHome=System.getProperty("java.home");
  private static File svcTagDir;
  private static File serviceTagFile;
  private static File regXmlFile;
  private static RegistrationData registration;
  private static boolean supportRegistration;
  private static String registerHtmlParent;
  private static Set<Locale> supportedLocales=new HashSet<Locale>();
  private static Properties swordfishProps=null;
  private static String[] jreArchs=null;
static {
    String dir=System.getProperty(SVCTAG_DIR_PATH);
    if (dir == null) {
      svcTagDir=new File(getJrePath(),"lib" + File.separator + SERVICE_TAG_FILE);
    }
 else {
      svcTagDir=new File(dir);
    }
    serviceTagFile=new File(svcTagDir,SERVICE_TAG_FILE);
    regXmlFile=new File(svcTagDir,REGISTRATION_XML);
    if (System.getProperty(SVCTAG_ENABLE_REGISTRATION) == null) {
      supportRegistration=isJdk();
    }
 else {
      supportRegistration=true;
    }
  }
  private Installer(){
  }
  static ServiceTag getJavaServiceTag(  String source) throws IOException {
    String vendor=System.getProperty("java.vendor","");
    if (!vendor.startsWith(SUN) && !vendor.startsWith(ORACLE)) {
      return null;
    }
    boolean cleanup=false;
    try {
      if (loadSwordfishEntries() == null) {
        return null;
      }
      ServiceTag st=getJavaServiceTag();
      if (st != null && st.getSource().equals(source)) {
        if (Registry.isSupported()) {
          installSystemServiceTag();
        }
        return st;
      }
      cleanup=true;
      deleteRegistrationData();
      cleanup=false;
      return createServiceTag(source);
    }
  finally {
      if (cleanup) {
        if (regXmlFile.exists()) {
          regXmlFile.delete();
        }
        if (serviceTagFile.exists()) {
          serviceTagFile.delete();
        }
      }
    }
  }
  /** 
 * Returns the Java SE registration data located in
 * the <JRE>/lib/servicetag/registration.xml by default.
 * @throws IllegalArgumentException if the registration data
 * is of invalid format.
 */
  private static synchronized RegistrationData getRegistrationData() throws IOException {
    if (registration != null) {
      return registration;
    }
    if (regXmlFile.exists()) {
      BufferedInputStream in=null;
      try {
        in=new BufferedInputStream(new FileInputStream(regXmlFile));
        registration=RegistrationData.loadFromXML(in);
      }
 catch (      IllegalArgumentException ex) {
        System.err.println("Error: Bad registration data \"" + regXmlFile + "\":"+ ex.getMessage());
        throw ex;
      }
 finally {
        if (in != null) {
          in.close();
        }
      }
    }
 else {
      registration=new RegistrationData();
    }
    return registration;
  }
  /** 
 * Write the registration data to the registration.xml file.
 * The offline registration page has to be regenerated with
 * the new registration data.
 * @throws java.io.IOException
 */
  private static synchronized void writeRegistrationXml() throws IOException {
    if (!svcTagDir.exists()) {
      if (!svcTagDir.mkdir()) {
        throw new IOException("Failed to create directory: " + svcTagDir);
      }
    }
    deleteRegistrationHtmlPage();
    getRegistrationHtmlPage();
    BufferedOutputStream out=null;
    try {
      out=new BufferedOutputStream(new FileOutputStream(regXmlFile));
      getRegistrationData().storeToXML(out);
    }
 catch (    IllegalArgumentException ex) {
      System.err.println("Error: Bad registration data \"" + regXmlFile + "\":"+ ex.getMessage());
      throw ex;
    }
 finally {
      if (out != null) {
        out.close();
      }
    }
  }
  /** 
 * Returns the instance urn(s) stored in the servicetag file
 * or empty set if file not exists.
 */
  private static Set<String> getInstalledURNs() throws IOException {
    Set<String> urnSet=new HashSet<String>();
    if (serviceTagFile.exists()) {
      BufferedReader in=null;
      try {
        in=new BufferedReader(new FileReader(serviceTagFile));
        String urn;
        while ((urn=in.readLine()) != null) {
          urn=urn.trim();
          if (urn.length() > 0) {
            urnSet.add(urn);
          }
        }
      }
  finally {
        if (in != null) {
          in.close();
        }
      }
    }
    return urnSet;
  }
  /** 
 * Return the Java SE service tag(s) if it exists.
 * Typically only one Java SE service tag but it could have two for
 * Solaris 32-bit and 64-bit on the same install directory.
 * @return the service tag(s) for Java SE
 */
  private static ServiceTag[] getJavaServiceTagArray() throws IOException {
    RegistrationData regData=getRegistrationData();
    Set<ServiceTag> svcTags=regData.getServiceTags();
    Set<ServiceTag> result=new HashSet<ServiceTag>();
    Properties props=loadSwordfishEntries();
    String jdkUrn=props.getProperty("servicetag.jdk.urn");
    String jreUrn=props.getProperty("servicetag.jre.urn");
    for (    ServiceTag st : svcTags) {
      if (st.getProductURN().equals(jdkUrn) || st.getProductURN().equals(jreUrn)) {
        result.add(st);
      }
    }
    return result.toArray(new ServiceTag[0]);
  }
  /** 
 * Returns the Java SE service tag for this running platform;
 * or null if not exist.
 * This method will return the 64-bit service tag if the JDK
 * supports both 32-bit and 64-bit if already created.
 */
  private static ServiceTag getJavaServiceTag() throws IOException {
    String definedId=getProductDefinedId();
    for (    ServiceTag st : getJavaServiceTagArray()) {
      if (st.getProductDefinedInstanceID().equals(definedId)) {
        return st;
      }
    }
    return null;
  }
  /** 
 * Create a service tag for Java SE and install in the system
 * service tag registry if supported.
 * A registration data <JRE>/lib/servicetag/registration.xml
 * will be created to storeToXML the XML entry for Java SE service tag.
 * If the system supports service tags, this method will install
 * the Java SE service tag in the system service tag registry and
 * its <tt>instance_urn</tt> will be stored to <JRE>/lib/servicetag/servicetag.
 * If <JRE>/lib/servicetag/registration.xml exists but is not installed
 * in the system service tag registry (i.e. servicetag doesn't exist),
 * this method will install it as described above.
 * If the system supports service tag, stclient will be used
 * to create the Java SE service tag.
 * A Solaris 32-bit and 64-bit JDK will be installed in the same
 * directory but the registration.xml will have 2 service tags.
 * The servicetag file will also contain 2 instance_urns for that case.
 */
  private static ServiceTag createServiceTag(  String svcTagSource) throws IOException {
    ServiceTag newSvcTag=null;
    if (getJavaServiceTag() == null) {
      newSvcTag=newServiceTag(svcTagSource);
    }
    if (newSvcTag != null) {
      RegistrationData regData=getRegistrationData();
      newSvcTag=regData.addServiceTag(newSvcTag);
      ServiceTag osTag=SolarisServiceTag.getServiceTag();
      if (osTag != null && regData.getServiceTag(osTag.getInstanceURN()) == null) {
        regData.addServiceTag(osTag);
      }
      writeRegistrationXml();
    }
    if (Registry.isSupported()) {
      installSystemServiceTag();
    }
    return newSvcTag;
  }
  private static void installSystemServiceTag() throws IOException {
    if ((!serviceTagFile.exists() && !svcTagDir.canWrite()) || (serviceTagFile.exists() && !serviceTagFile.canWrite())) {
      return;
    }
    Set<String> urns=getInstalledURNs();
    ServiceTag[] javaSvcTags=getJavaServiceTagArray();
    if (urns.size() < javaSvcTags.length) {
      for (      ServiceTag st : javaSvcTags) {
        String instanceURN=st.getInstanceURN();
        if (!urns.contains(instanceURN)) {
          Registry.getSystemRegistry().addServiceTag(st);
        }
      }
    }
    writeInstalledUrns();
  }
  private static ServiceTag newServiceTag(  String svcTagSource) throws IOException {
    Properties props=loadSwordfishEntries();
    String productURN;
    String productName;
    if (isJdk()) {
      productURN=props.getProperty("servicetag.jdk.urn");
      productName=props.getProperty("servicetag.jdk.name");
    }
 else {
      productURN=props.getProperty("servicetag.jre.urn");
      productName=props.getProperty("servicetag.jre.name");
    }
    return ServiceTag.newInstance(ServiceTag.generateInstanceURN(),productName,System.getProperty("java.version"),productURN,props.getProperty("servicetag.parent.name"),props.getProperty("servicetag.parent.urn"),getProductDefinedId(),System.getProperty("java.vendor"),System.getProperty("os.arch"),getZoneName(),svcTagSource);
  }
  /** 
 * Delete the registration data, the offline registration pages and
 * the service tags in the system service tag registry if installed.
 * The registration.xml and servicetag file will be removed.
 */
  private static synchronized void deleteRegistrationData() throws IOException {
    try {
      deleteRegistrationHtmlPage();
      Set<String> urns=getInstalledURNs();
      if (urns.size() > 0 && Registry.isSupported()) {
        for (        String u : urns) {
          Registry.getSystemRegistry().removeServiceTag(u);
        }
      }
      registration=null;
    }
  finally {
      if (regXmlFile.exists()) {
        if (!regXmlFile.delete()) {
          throw new IOException("Failed to delete " + regXmlFile);
        }
      }
      if (serviceTagFile.exists()) {
        if (!serviceTagFile.delete()) {
          throw new IOException("Failed to delete " + serviceTagFile);
        }
      }
    }
  }
  /** 
 * Updates the registration data to contain one single service tag
 * for the running Java runtime.
 */
  private static synchronized void updateRegistrationData(  String svcTagSource) throws IOException {
    RegistrationData regData=getRegistrationData();
    ServiceTag curSvcTag=newServiceTag(svcTagSource);
    ServiceTag[] javaSvcTags=getJavaServiceTagArray();
    Set<String> urns=getInstalledURNs();
    for (    ServiceTag st : javaSvcTags) {
      if (!st.getProductDefinedInstanceID().equals(curSvcTag.getProductDefinedInstanceID())) {
        String instanceURN=st.getInstanceURN();
        regData.removeServiceTag(instanceURN);
        if (urns.contains(instanceURN) && Registry.isSupported()) {
          Registry.getSystemRegistry().removeServiceTag(instanceURN);
        }
      }
    }
    writeRegistrationXml();
    writeInstalledUrns();
  }
  private static void writeInstalledUrns() throws IOException {
    if (!Registry.isSupported() && serviceTagFile.exists()) {
      serviceTagFile.delete();
      return;
    }
    PrintWriter out=null;
    try {
      out=new PrintWriter(serviceTagFile);
      ServiceTag[] javaSvcTags=getJavaServiceTagArray();
      for (      ServiceTag st : javaSvcTags) {
        String instanceURN=st.getInstanceURN();
        out.println(instanceURN);
      }
    }
  finally {
      if (out != null) {
        out.close();
      }
    }
  }
  /** 
 * Load the values associated with the swoRDFish metadata entries
 * for Java SE. The swoRDFish metadata entries are different for
 * different release.
 * @param version Version of Java SE
 */
  private static synchronized Properties loadSwordfishEntries() throws IOException {
    if (swordfishProps != null) {
      return swordfishProps;
    }
    int version=Util.getJdkVersion();
    String filename="/com/sun/servicetag/resources/javase_" + version + "_swordfish.properties";
    InputStream in=Installer.class.getResourceAsStream(filename);
    if (in == null) {
      return null;
    }
    swordfishProps=new Properties();
    try {
      swordfishProps.load(in);
    }
  finally {
      in.close();
    }
    return swordfishProps;
  }
  /** 
 * Returns the product defined instance ID for Java SE.
 * It is a list of comma-separated name/value pairs:
 * "id=<full-version>  <arch> [<arch>]*"
 * "dir=<java.home system property value>"
 * where <full-version> is the full version string of the JRE,
 * <arch> is the architecture that the runtime supports
 * (i.e. "sparc", "sparcv9", "i386", "amd64" (ISA list))
 * For Solaris, it can be dual mode that can support both
 * 32-bit and 64-bit. the "id" will be set to
 * "1.6.0_03-b02 sparc sparcv9"
 * The "dir" property is included in the service tag to enable
 * the Service Tag software to determine if a service tag for
 * Java SE is invalid and perform appropriate service tag
 * cleanup if necessary.  See RFE# 6574781 Service Tags Enhancement.
 */
  private static String getProductDefinedId(){
    StringBuilder definedId=new StringBuilder();
    definedId.append("id=");
    definedId.append(System.getProperty("java.runtime.version"));
    String[] archs=getJreArchs();
    for (    String name : archs) {
      definedId.append(" " + name);
    }
    String location=",dir=" + javaHome;
    if ((definedId.length() + location.length()) < 256) {
      definedId.append(",dir=");
      definedId.append(javaHome);
    }
 else {
      if (isVerbose()) {
        System.err.println("Warning: Product defined instance ID exceeds the field limit:");
      }
    }
    return definedId.toString();
  }
  /** 
 * Returns the architectures that the runtime supports
 * (i.e. "sparc", "sparcv9", "i386", "amd64" (ISA list))
 * The directory name where libjava.so is located.
 * On Windows, returns the "os.arch" system property value.
 */
  private synchronized static String[] getJreArchs(){
    if (jreArchs != null) {
      return jreArchs;
    }
    Set<String> archs=new HashSet<String>();
    String os=System.getProperty("os.name");
    if (os.equals("SunOS") || os.equals("Linux")) {
      File dir=new File(getJrePath() + File.separator + "lib");
      if (dir.isDirectory()) {
        String[] children=dir.list();
        for (        String name : children) {
          File f=new File(dir,name + File.separator + "libjava.so");
          if (f.exists()) {
            archs.add(name);
          }
        }
      }
    }
 else {
      archs.add(System.getProperty("os.arch"));
    }
    jreArchs=archs.toArray(new String[0]);
    return jreArchs;
  }
  /** 
 * Return the zonename if zone is supported; otherwise, return
 * "global".
 */
  private static String getZoneName() throws IOException {
    String zonename="global";
    String command="/usr/bin/zonename";
    File f=new File(command);
    if (f.exists()) {
      ProcessBuilder pb=new ProcessBuilder(command);
      Process p=pb.start();
      String output=commandOutput(p);
      if (p.exitValue() == 0) {
        zonename=output.trim();
      }
    }
    return zonename;
  }
  private synchronized static String getRegisterHtmlParent() throws IOException {
    if (registerHtmlParent == null) {
      File htmlDir;
      if (getJrePath().endsWith(File.separator + "jre")) {
        htmlDir=new File(getJrePath(),"..");
      }
 else {
        htmlDir=new File(getJrePath());
      }
      initSupportedLocales(htmlDir);
      String path=System.getProperty(SVCTAG_DIR_PATH);
      if (path == null) {
        registerHtmlParent=htmlDir.getCanonicalPath();
      }
 else {
        File f=new File(path);
        registerHtmlParent=f.getCanonicalPath();
        if (!f.isDirectory()) {
          throw new InternalError("Path " + path + " set in \""+ SVCTAG_DIR_PATH+ "\" property is not a directory");
        }
      }
    }
    return registerHtmlParent;
  }
  /** 
 * Returns the File object of the offline registration page localized
 * for the default locale in the JDK directory.
 */
  static synchronized File getRegistrationHtmlPage() throws IOException {
    if (!supportRegistration) {
      return null;
    }
    String parent=getRegisterHtmlParent();
    File f=new File(parent,REGISTRATION_HTML_NAME + ".html");
    if (!f.exists()) {
      generateRegisterHtml(parent);
    }
    String name=REGISTRATION_HTML_NAME;
    Locale locale=getDefaultLocale();
    if (!locale.equals(Locale.ENGLISH) && supportedLocales.contains(locale)) {
      name=REGISTRATION_HTML_NAME + "_" + locale.toString();
    }
    File htmlFile=new File(parent,name + ".html");
    if (isVerbose()) {
      System.out.print("Offline registration page: " + htmlFile);
      System.out.println((htmlFile.exists() ? "" : " not exist. Use register.html"));
    }
    if (htmlFile.exists()) {
      return htmlFile;
    }
 else {
      return new File(parent,REGISTRATION_HTML_NAME + ".html");
    }
  }
  private static Locale getDefaultLocale(){
    List<Locale> candidateLocales=getCandidateLocales(Locale.getDefault());
    for (    Locale l : candidateLocales) {
      if (supportedLocales.contains(l)) {
        return l;
      }
    }
    return Locale.getDefault();
  }
  private static List<Locale> getCandidateLocales(  Locale locale){
    String language=locale.getLanguage();
    String country=locale.getCountry();
    String variant=locale.getVariant();
    List<Locale> locales=new ArrayList<Locale>(3);
    if (variant.length() > 0) {
      locales.add(locale);
    }
    if (country.length() > 0) {
      locales.add((locales.size() == 0) ? locale : new Locale(language,country,""));
    }
    if (language.length() > 0) {
      locales.add((locales.size() == 0) ? locale : new Locale(language,"",""));
    }
    return locales;
  }
  private static void deleteRegistrationHtmlPage() throws IOException {
    String parent=getRegisterHtmlParent();
    if (parent == null) {
      return;
    }
    for (    Locale locale : supportedLocales) {
      String name=REGISTRATION_HTML_NAME;
      if (!locale.equals(Locale.ENGLISH)) {
        name+="_" + locale.toString();
      }
      File f=new File(parent,name + ".html");
      if (f.exists()) {
        if (!f.delete()) {
          throw new IOException("Failed to delete " + f);
        }
      }
    }
  }
  private static void initSupportedLocales(  File jdkDir){
    if (supportedLocales.isEmpty()) {
      for (      Locale l : knownSupportedLocales) {
        supportedLocales.add(l);
      }
    }
    FilenameFilter ff=new FilenameFilter(){
      public boolean accept(      File dir,      String name){
        String fname=name.toLowerCase();
        if (fname.startsWith("readme") && fname.endsWith(".html")) {
          return true;
        }
        return false;
      }
    }
;
    String[] readmes=jdkDir.list(ff);
    for (    String name : readmes) {
      String basename=name.substring(0,name.length() - ".html".length());
      String[] ss=basename.split("_");
switch (ss.length) {
case 1:
        break;
case 2:
      supportedLocales.add(new Locale(ss[1]));
    break;
case 3:
  supportedLocales.add(new Locale(ss[1],ss[2]));
break;
default :
break;
}
}
if (isVerbose()) {
System.out.println("Supported locales: ");
for (Locale l : supportedLocales) {
System.out.println(l);
}
}
}
private static final String JDK_HEADER_PNG_KEY="@@JDK_HEADER_PNG@@";
private static final String JDK_VERSION_KEY="@@JDK_VERSION@@";
private static final String REGISTRATION_URL_KEY="@@REGISTRATION_URL@@";
private static final String REGISTRATION_PAYLOAD_KEY="@@REGISTRATION_PAYLOAD@@";
@SuppressWarnings("unchecked") private static void generateRegisterHtml(String parent) throws IOException {
int version=Util.getJdkVersion();
int update=Util.getUpdateVersion();
String jdkVersion="Version " + version;
if (update > 0) {
jdkVersion+=" Update " + update;
}
RegistrationData regData=getRegistrationData();
File img=new File(svcTagDir.getCanonicalPath(),"jdk_header.png");
String headerImageSrc=img.toURI().toString();
StringBuilder payload=new StringBuilder();
String xml=regData.toString().replaceAll("\"","%22");
BufferedReader reader=new BufferedReader(new StringReader(xml));
try {
String line=null;
while ((line=reader.readLine()) != null) {
payload.append(line.trim());
}
}
  finally {
reader.close();
}
String resourceFilename="/com/sun/servicetag/resources/register";
for (Locale locale : supportedLocales) {
String name=REGISTRATION_HTML_NAME;
String resource=resourceFilename;
if (!locale.equals(Locale.ENGLISH)) {
name+="_" + locale.toString();
resource+="_" + locale.toString();
}
File f=new File(parent,name + ".html");
InputStream in=null;
BufferedReader br=null;
PrintWriter pw=null;
String registerURL=SunConnection.getRegistrationURL(regData.getRegistrationURN(),locale,String.valueOf(version)).toString();
try {
in=Installer.class.getResourceAsStream(resource + ".html");
if (in == null) {
if (isVerbose()) {
System.out.println("Missing resouce file: " + resource + ".html");
}
continue;
}
if (isVerbose()) {
System.out.println("Generating " + f + " from "+ resource+ ".html");
}
try {
br=new BufferedReader(new InputStreamReader(in,"UTF-8"));
pw=new PrintWriter(f,"UTF-8");
String line=null;
while ((line=br.readLine()) != null) {
String output=line;
if (line.contains(JDK_VERSION_KEY)) {
  output=line.replace(JDK_VERSION_KEY,jdkVersion);
}
 else if (line.contains(JDK_HEADER_PNG_KEY)) {
  output=line.replace(JDK_HEADER_PNG_KEY,headerImageSrc);
}
 else if (line.contains(REGISTRATION_URL_KEY)) {
  output=line.replace(REGISTRATION_URL_KEY,registerURL);
}
 else if (line.contains(REGISTRATION_PAYLOAD_KEY)) {
  output=line.replace(REGISTRATION_PAYLOAD_KEY,payload.toString());
}
pw.println(output);
}
f.setReadOnly();
pw.flush();
}
  finally {
if (pw != null) {
pw.close();
}
if (br != null) {
br.close();
}
}
}
  finally {
if (in != null) {
in.close();
}
}
}
}
private static final int MAX_SOURCE_LEN=63;
/** 
 * A utility class to create a service tag for Java SE.
 * <p>
 * <b>Usage:</b><br>
 * <blockquote><tt>
 * &lt;JAVA_HOME&gt;/bin/java com.sun.servicetag.Installer
 * </tt></blockquote>
 * <p>
 */
public static void main(String[] args){
String source="Manual ";
String runtimeName=System.getProperty("java.runtime.name");
if (runtimeName.startsWith("OpenJDK")) {
source="OpenJDK ";
}
source+=System.getProperty("java.runtime.version");
if (source.length() > MAX_SOURCE_LEN) {
source=source.substring(0,MAX_SOURCE_LEN);
}
boolean delete=false;
boolean update=false;
boolean register=false;
int count=0;
while (count < args.length) {
String arg=args[count];
if (arg.trim().length() == 0) {
count++;
continue;
}
if (arg.equals("-source")) {
source=args[++count];
}
 else if (arg.equals("-delete")) {
delete=true;
}
 else if (arg.equals("-register")) {
register=true;
}
 else {
usage();
return;
}
count++;
}
try {
if (delete) {
deleteRegistrationData();
}
 else {
ServiceTag[] javaSvcTags=getJavaServiceTagArray();
String[] archs=getJreArchs();
if (javaSvcTags.length > archs.length) {
updateRegistrationData(source);
}
 else {
createServiceTag(source);
}
}
if (register) {
RegistrationData regData=getRegistrationData();
if (supportRegistration && !regData.getServiceTags().isEmpty()) {
SunConnection.register(regData,getDefaultLocale(),String.valueOf(Util.getJdkVersion()));
}
}
System.exit(0);
}
 catch (IOException e) {
System.err.println("I/O Error: " + e.getMessage());
if (isVerbose()) {
e.printStackTrace();
}
}
catch (IllegalArgumentException ex) {
if (isVerbose()) {
ex.printStackTrace();
}
}
catch (Exception e) {
System.err.println("Error: " + e.getMessage());
if (isVerbose()) {
e.printStackTrace();
}
}
System.exit(1);
}
private static void usage(){
System.out.println("Usage:");
System.out.print("    " + Installer.class.getName());
System.out.println(" [-delete|-source <source>|-register]");
System.out.println("       to create a service tag for the Java platform");
System.out.println("");
System.out.println("Internal Options:");
System.out.println("    -source: to specify the source of the service tag to be created");
System.out.println("    -delete: to delete the service tag ");
System.out.println("    -register: to register the JDK");
System.out.println("    -help:   to print this help message");
}
}
