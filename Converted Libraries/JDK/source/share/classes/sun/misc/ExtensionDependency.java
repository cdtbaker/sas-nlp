package sun.misc;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Enumeration;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.jar.Attributes;
import java.util.jar.Attributes.Name;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedExceptionAction;
import java.security.PrivilegedActionException;
import java.net.URL;
import java.net.MalformedURLException;
import sun.net.www.ParseUtil;
/** 
 * <p>
 * This class checks dependent extensions a particular jar file may have
 * declared through its manifest attributes.
 * </p>
 * Jar file declared dependent extensions through the extension-list
 * attribute. The extension-list contains a list of keys used to
 * fetch the other attributes describing the required extension.
 * If key is the extension key declared in the extension-list
 * attribute, the following describing attribute can be found in
 * the manifest :
 * key-Extension-Name:  (Specification package name)
 * key-Specification-Version: (Specification-Version)
 * key-Implementation-Version: (Implementation-Version)
 * key-Implementation-Vendor-Id: (Imlementation-Vendor-Id)
 * key-Implementation-Version: (Implementation version)
 * key-Implementation-URL: (URL to download the requested extension)
 * <p>
 * This class also maintain versioning consistency of installed
 * extensions dependencies declared in jar file manifest.
 * </p>
 * @author  Jerome Dochez
 */
public class ExtensionDependency {
  private static Vector providers;
  /** 
 * <p>
 * Register an ExtensionInstallationProvider. The provider is responsible
 * for handling the installation (upgrade) of any missing extensions.
 * </p>
 * @param eip ExtensionInstallationProvider implementation
 */
  public synchronized static void addExtensionInstallationProvider(  ExtensionInstallationProvider eip){
    if (providers == null) {
      providers=new Vector();
    }
    providers.add(eip);
  }
  /** 
 * <p>
 * Unregister a previously installed installation provider
 * </p>
 */
  public synchronized static void removeExtensionInstallationProvider(  ExtensionInstallationProvider eip){
    providers.remove(eip);
  }
  /** 
 * <p>
 * Checks the dependencies of the jar file on installed extension.
 * </p>
 * @param jarFile containing the attriutes declaring the dependencies
 */
  public static boolean checkExtensionsDependencies(  JarFile jar){
    if (providers == null) {
      return true;
    }
    try {
      ExtensionDependency extDep=new ExtensionDependency();
      return extDep.checkExtensions(jar);
    }
 catch (    ExtensionInstallationException e) {
      debug(e.getMessage());
    }
    return false;
  }
  protected boolean checkExtensions(  JarFile jar) throws ExtensionInstallationException {
    Manifest man;
    try {
      man=jar.getManifest();
    }
 catch (    IOException e) {
      return false;
    }
    if (man == null) {
      return true;
    }
    boolean result=true;
    Attributes attr=man.getMainAttributes();
    if (attr != null) {
      String value=attr.getValue(Name.EXTENSION_LIST);
      if (value != null) {
        StringTokenizer st=new StringTokenizer(value);
        while (st.hasMoreTokens()) {
          String extensionName=st.nextToken();
          debug("The file " + jar.getName() + " appears to depend on "+ extensionName);
          String extName=extensionName + "-" + Name.EXTENSION_NAME.toString();
          if (attr.getValue(extName) == null) {
            debug("The jar file " + jar.getName() + " appers to depend on "+ extensionName+ " but does not define the "+ extName+ " attribute in its manifest ");
          }
 else {
            if (!checkExtension(extensionName,attr)) {
              debug("Failed installing " + extensionName);
              result=false;
            }
          }
        }
      }
 else {
        debug("No dependencies for " + jar.getName());
      }
    }
    return result;
  }
  protected synchronized boolean checkExtension(  final String extensionName,  final Attributes attr) throws ExtensionInstallationException {
    debug("Checking extension " + extensionName);
    if (checkExtensionAgainstInstalled(extensionName,attr))     return true;
    debug("Extension not currently installed ");
    ExtensionInfo reqInfo=new ExtensionInfo(extensionName,attr);
    return installExtension(reqInfo,null);
  }
  boolean checkExtensionAgainstInstalled(  String extensionName,  Attributes attr) throws ExtensionInstallationException {
    File fExtension=checkExtensionExists(extensionName);
    if (fExtension != null) {
      try {
        if (checkExtensionAgainst(extensionName,attr,fExtension))         return true;
      }
 catch (      FileNotFoundException e) {
        debugException(e);
      }
catch (      IOException e) {
        debugException(e);
      }
      return false;
    }
 else {
      File[] installedExts;
      try {
        installedExts=getInstalledExtensions();
      }
 catch (      IOException e) {
        debugException(e);
        return false;
      }
      for (int i=0; i < installedExts.length; i++) {
        try {
          if (checkExtensionAgainst(extensionName,attr,installedExts[i]))           return true;
        }
 catch (        FileNotFoundException e) {
          debugException(e);
        }
catch (        IOException e) {
          debugException(e);
        }
      }
    }
    return false;
  }
  protected boolean checkExtensionAgainst(  String extensionName,  Attributes attr,  final File file) throws IOException, FileNotFoundException, ExtensionInstallationException {
    debug("Checking extension " + extensionName + " against "+ file.getName());
    Manifest man;
    try {
      man=AccessController.doPrivileged(new PrivilegedExceptionAction<Manifest>(){
        public Manifest run() throws IOException, FileNotFoundException {
          if (!file.exists())           throw new FileNotFoundException(file.getName());
          JarFile jarFile=new JarFile(file);
          return jarFile.getManifest();
        }
      }
);
    }
 catch (    PrivilegedActionException e) {
      if (e.getException() instanceof FileNotFoundException)       throw (FileNotFoundException)e.getException();
      throw (IOException)e.getException();
    }
    ExtensionInfo reqInfo=new ExtensionInfo(extensionName,attr);
    debug("Requested Extension : " + reqInfo);
    int isCompatible=ExtensionInfo.INCOMPATIBLE;
    ExtensionInfo instInfo=null;
    if (man != null) {
      Attributes instAttr=man.getMainAttributes();
      if (instAttr != null) {
        instInfo=new ExtensionInfo(null,instAttr);
        debug("Extension Installed " + instInfo);
        isCompatible=instInfo.isCompatibleWith(reqInfo);
switch (isCompatible) {
case ExtensionInfo.COMPATIBLE:
          debug("Extensions are compatible");
        return true;
case ExtensionInfo.INCOMPATIBLE:
      debug("Extensions are incompatible");
    return false;
default :
  debug("Extensions require an upgrade or vendor switch");
return installExtension(reqInfo,instInfo);
}
}
}
return false;
}
protected boolean installExtension(ExtensionInfo reqInfo,ExtensionInfo instInfo) throws ExtensionInstallationException {
Vector currentProviders;
synchronized (providers) {
currentProviders=(Vector)providers.clone();
}
for (Enumeration e=currentProviders.elements(); e.hasMoreElements(); ) {
ExtensionInstallationProvider eip=(ExtensionInstallationProvider)e.nextElement();
if (eip != null) {
if (eip.installExtension(reqInfo,instInfo)) {
debug(reqInfo.name + " installation successful");
Launcher.ExtClassLoader cl=(Launcher.ExtClassLoader)Launcher.getLauncher().getClassLoader().getParent();
addNewExtensionsToClassLoader(cl);
return true;
}
}
}
debug(reqInfo.name + " installation failed");
return false;
}
/** 
 * <p>
 * Checks if the extension, that is specified in the extension-list in
 * the applet jar manifest, is already installed (i.e. exists in the
 * extension directory).
 * </p>
 * @param extensionName extension name in the extension-list
 * @return the extension if it exists in the extension directory
 */
private File checkExtensionExists(String extensionName){
final String extName=extensionName;
final String[] fileExt={".jar",".zip"};
return AccessController.doPrivileged(new PrivilegedAction<File>(){
public File run(){
try {
File fExtension;
File[] dirs=getExtDirs();
for (int i=0; i < dirs.length; i++) {
for (int j=0; j < fileExt.length; j++) {
  if (extName.toLowerCase().endsWith(fileExt[j])) {
    fExtension=new File(dirs[i],extName);
  }
 else {
    fExtension=new File(dirs[i],extName + fileExt[j]);
  }
  debug("checkExtensionExists:fileName " + fExtension.getName());
  if (fExtension.exists()) {
    return fExtension;
  }
}
}
return null;
}
 catch (Exception e) {
debugException(e);
return null;
}
}
}
);
}
/** 
 * <p>
 * @return the java.ext.dirs property as a list of directory
 * </p>
 */
private static File[] getExtDirs(){
String s=java.security.AccessController.doPrivileged(new sun.security.action.GetPropertyAction("java.ext.dirs"));
File[] dirs;
if (s != null) {
StringTokenizer st=new StringTokenizer(s,File.pathSeparator);
int count=st.countTokens();
debug("getExtDirs count " + count);
dirs=new File[count];
for (int i=0; i < count; i++) {
dirs[i]=new File(st.nextToken());
debug("getExtDirs dirs[" + i + "] "+ dirs[i]);
}
}
 else {
dirs=new File[0];
debug("getExtDirs dirs " + dirs);
}
debug("getExtDirs dirs.length " + dirs.length);
return dirs;
}
private static File[] getExtFiles(File[] dirs) throws IOException {
Vector<File> urls=new Vector<File>();
for (int i=0; i < dirs.length; i++) {
String[] files=dirs[i].list(new JarFilter());
if (files != null) {
debug("getExtFiles files.length " + files.length);
for (int j=0; j < files.length; j++) {
File f=new File(dirs[i],files[j]);
urls.add(f);
debug("getExtFiles f[" + j + "] "+ f);
}
}
}
File[] ua=new File[urls.size()];
urls.copyInto(ua);
debug("getExtFiles ua.length " + ua.length);
return ua;
}
private File[] getInstalledExtensions() throws IOException {
return AccessController.doPrivileged(new PrivilegedAction<File[]>(){
public File[] run(){
try {
return getExtFiles(getExtDirs());
}
 catch (IOException e) {
debug("Cannot get list of installed extensions");
debugException(e);
return new File[0];
}
}
}
);
}
private Boolean addNewExtensionsToClassLoader(Launcher.ExtClassLoader cl){
try {
File[] installedExts=getInstalledExtensions();
for (int i=0; i < installedExts.length; i++) {
final File instFile=installedExts[i];
URL instURL=AccessController.doPrivileged(new PrivilegedAction<URL>(){
public URL run(){
try {
  return ParseUtil.fileToEncodedURL(instFile);
}
 catch (MalformedURLException e) {
  debugException(e);
  return null;
}
}
}
);
if (instURL != null) {
URL[] urls=cl.getURLs();
boolean found=false;
for (int j=0; j < urls.length; j++) {
debug("URL[" + j + "] is "+ urls[j]+ " looking for "+ instURL);
if (urls[j].toString().compareToIgnoreCase(instURL.toString()) == 0) {
  found=true;
  debug("Found !");
}
}
if (!found) {
debug("Not Found ! adding to the classloader " + instURL);
cl.addExtURL(instURL);
}
}
}
}
 catch (MalformedURLException e) {
e.printStackTrace();
}
catch (IOException e) {
e.printStackTrace();
}
return Boolean.TRUE;
}
static final boolean DEBUG=false;
private static void debug(String s){
if (DEBUG) {
System.err.println(s);
}
}
private void debugException(Throwable e){
if (DEBUG) {
e.printStackTrace();
}
}
}
