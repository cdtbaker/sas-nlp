package sun.launcher;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.util.ResourceBundle;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Locale.Category;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
public enum LauncherHelper {INSTANCE; private static final String MAIN_CLASS="Main-Class";
private static StringBuilder outBuf=new StringBuilder();
private static ResourceBundle javarb=null;
private static final String INDENT="    ";
private static final String VM_SETTINGS="VM settings:";
private static final String PROP_SETTINGS="Property settings:";
private static final String LOCALE_SETTINGS="Locale settings:";
private static final String diagprop="sun.java.launcher.diag";
private static final String defaultBundleName="sun.launcher.resources.launcher";
private static class ResourceBundleHolder {
  private static final ResourceBundle RB=ResourceBundle.getBundle(defaultBundleName);
}
static void showSettings(boolean printToStderr,String optionFlag,long initialHeapSize,long maxHeapSize,long stackSize,boolean isServer){
  PrintStream ostream=(printToStderr) ? System.err : System.out;
  String opts[]=optionFlag.split(":");
  String optStr=(opts.length > 1 && opts[1] != null) ? opts[1].trim() : "all";
switch (optStr) {
case "vm":
    printVmSettings(ostream,initialHeapSize,maxHeapSize,stackSize,isServer);
  break;
case "properties":
printProperties(ostream);
break;
case "locale":
printLocale(ostream);
break;
default :
printVmSettings(ostream,initialHeapSize,maxHeapSize,stackSize,isServer);
printProperties(ostream);
printLocale(ostream);
break;
}
}
private static void printVmSettings(PrintStream ostream,long initialHeapSize,long maxHeapSize,long stackSize,boolean isServer){
ostream.println(VM_SETTINGS);
if (stackSize != 0L) {
ostream.println(INDENT + "Stack Size: " + SizePrefix.scaleValue(stackSize));
}
if (initialHeapSize != 0L) {
ostream.println(INDENT + "Min. Heap Size: " + SizePrefix.scaleValue(initialHeapSize));
}
if (maxHeapSize != 0L) {
ostream.println(INDENT + "Max. Heap Size: " + SizePrefix.scaleValue(maxHeapSize));
}
 else {
ostream.println(INDENT + "Max. Heap Size (Estimated): " + SizePrefix.scaleValue(Runtime.getRuntime().maxMemory()));
}
ostream.println(INDENT + "Ergonomics Machine Class: " + ((isServer) ? "server" : "client"));
ostream.println(INDENT + "Using VM: " + System.getProperty("java.vm.name"));
ostream.println();
}
private static void printProperties(PrintStream ostream){
Properties p=System.getProperties();
ostream.println(PROP_SETTINGS);
List<String> sortedPropertyKeys=new ArrayList<>();
sortedPropertyKeys.addAll(p.stringPropertyNames());
Collections.sort(sortedPropertyKeys);
for (String x : sortedPropertyKeys) {
printPropertyValue(ostream,x,p.getProperty(x));
}
ostream.println();
}
private static boolean isPath(String key){
return key.endsWith(".dirs") || key.endsWith(".path");
}
private static void printPropertyValue(PrintStream ostream,String key,String value){
ostream.print(INDENT + key + " = ");
if (key.equals("line.separator")) {
for (byte b : value.getBytes()) {
switch (b) {
case 0xd:
ostream.print("\\r ");
break;
case 0xa:
ostream.print("\\n ");
break;
default :
ostream.printf("0x%02X",b & 0xff);
break;
}
}
ostream.println();
return;
}
if (!isPath(key)) {
ostream.println(value);
return;
}
String[] values=value.split(System.getProperty("path.separator"));
boolean first=true;
for (String s : values) {
if (first) {
ostream.println(s);
first=false;
}
 else {
ostream.println(INDENT + INDENT + s);
}
}
}
private static void printLocale(PrintStream ostream){
Locale locale=Locale.getDefault();
ostream.println(LOCALE_SETTINGS);
ostream.println(INDENT + "default locale = " + locale.getDisplayLanguage());
ostream.println(INDENT + "default display locale = " + Locale.getDefault(Category.DISPLAY).getDisplayName());
ostream.println(INDENT + "default format locale = " + Locale.getDefault(Category.FORMAT).getDisplayName());
printLocales(ostream);
ostream.println();
}
private static void printLocales(PrintStream ostream){
Locale[] tlocales=Locale.getAvailableLocales();
final int len=tlocales == null ? 0 : tlocales.length;
if (len < 1) {
return;
}
Set<String> sortedSet=new TreeSet<>();
for (Locale l : tlocales) {
sortedSet.add(l.toString());
}
ostream.print(INDENT + "available locales = ");
Iterator<String> iter=sortedSet.iterator();
final int last=len - 1;
for (int i=0; iter.hasNext(); i++) {
String s=iter.next();
ostream.print(s);
if (i != last) {
ostream.print(", ");
}
if ((i + 1) % 8 == 0) {
ostream.println();
ostream.print(INDENT + INDENT);
}
}
}
private enum SizePrefix {KILO(1024,"K"), MEGA(1024 * 1024,"M"), GIGA(1024 * 1024 * 1024,"G"), TERA(1024L * 1024L * 1024L* 1024L,"T"); long size;
String abbrev;
SizePrefix(long size,String abbrev){
this.size=size;
this.abbrev=abbrev;
}
private static String scale(long v,SizePrefix prefix){
return BigDecimal.valueOf(v).divide(BigDecimal.valueOf(prefix.size),2,RoundingMode.HALF_EVEN).toPlainString() + prefix.abbrev;
}
static String scaleValue(long v){
if (v < MEGA.size) {
return scale(v,KILO);
}
 else if (v < GIGA.size) {
return scale(v,MEGA);
}
 else if (v < TERA.size) {
return scale(v,GIGA);
}
 else {
return scale(v,TERA);
}
}
}
/** 
 * A private helper method to get a localized message and also
 * apply any arguments that we might pass.
 */
private static String getLocalizedMessage(String key,Object... args){
String msg=ResourceBundleHolder.RB.getString(key);
return (args != null) ? MessageFormat.format(msg,args) : msg;
}
/** 
 * The java -help message is split into 3 parts, an invariant, followed
 * by a set of platform dependent variant messages, finally an invariant
 * set of lines.
 * This method initializes the help message for the first time, and also
 * assembles the invariant header part of the message.
 */
static void initHelpMessage(String progname){
outBuf=outBuf.append(getLocalizedMessage("java.launcher.opt.header",(progname == null) ? "java" : progname));
outBuf=outBuf.append(getLocalizedMessage("java.launcher.opt.datamodel",32));
outBuf=outBuf.append(getLocalizedMessage("java.launcher.opt.datamodel",64));
}
/** 
 * Appends the vm selection messages to the header, already created.
 * initHelpSystem must already be called.
 */
static void appendVmSelectMessage(String vm1,String vm2){
outBuf=outBuf.append(getLocalizedMessage("java.launcher.opt.vmselect",vm1,vm2));
}
/** 
 * Appends the vm synoym message to the header, already created.
 * initHelpSystem must be called before using this method.
 */
static void appendVmSynonymMessage(String vm1,String vm2){
outBuf=outBuf.append(getLocalizedMessage("java.launcher.opt.hotspot",vm1,vm2));
}
/** 
 * Appends the vm Ergo message to the header, already created.
 * initHelpSystem must be called before using this method.
 */
static void appendVmErgoMessage(boolean isServerClass,String vm){
outBuf=outBuf.append(getLocalizedMessage("java.launcher.ergo.message1",vm));
outBuf=(isServerClass) ? outBuf.append(",\n" + getLocalizedMessage("java.launcher.ergo.message2") + "\n\n") : outBuf.append(".\n\n");
}
/** 
 * Appends the last invariant part to the previously created messages,
 * and finishes up the printing to the desired output stream.
 * initHelpSystem must be called before using this method.
 */
static void printHelpMessage(boolean printToStderr){
PrintStream ostream=(printToStderr) ? System.err : System.out;
outBuf=outBuf.append(getLocalizedMessage("java.launcher.opt.footer",File.pathSeparator));
ostream.println(outBuf.toString());
}
/** 
 * Prints the Xusage text to the desired output stream.
 */
static void printXUsageMessage(boolean printToStderr){
PrintStream ostream=(printToStderr) ? System.err : System.out;
ostream.println(getLocalizedMessage("java.launcher.X.usage",File.pathSeparator));
}
static String getMainClassFromJar(PrintStream ostream,String jarname){
try {
JarFile jarFile=null;
try {
jarFile=new JarFile(jarname);
Manifest manifest=jarFile.getManifest();
if (manifest == null) {
abort(ostream,null,"java.launcher.jar.error2",jarname);
}
Attributes mainAttrs=manifest.getMainAttributes();
if (mainAttrs == null) {
abort(ostream,null,"java.launcher.jar.error3",jarname);
}
return mainAttrs.getValue(MAIN_CLASS).trim();
}
  finally {
if (jarFile != null) {
jarFile.close();
}
}
}
 catch (IOException ioe) {
abort(ostream,ioe,"java.launcher.jar.error1",jarname);
}
return null;
}
private static final int LM_UNKNOWN=0;
private static final int LM_CLASS=1;
private static final int LM_JAR=2;
static void abort(PrintStream ostream,Throwable t,String msgKey,Object... args){
if (msgKey != null) {
ostream.println(getLocalizedMessage(msgKey,args));
}
if (sun.misc.VM.getSavedProperty(diagprop) != null) {
if (t != null) {
t.printStackTrace();
}
 else {
Thread.currentThread().dumpStack();
}
}
System.exit(1);
}
/** 
 * This method does the following:
 * 1. gets the classname from a Jar's manifest, if necessary
 * 2. loads the class using the System ClassLoader
 * 3. ensures the availability and accessibility of the main method,
 * using signatureDiagnostic method.
 * a. does the class exist
 * b. is there a main
 * c. is the main public
 * d. is the main static
 * c. does the main take a String array for args
 * 4. and off we go......
 * @param printToStderr
 * @param isJar
 * @param name
 * @return
 */
public static Class<?> checkAndLoadMain(boolean printToStderr,int mode,String what){
final PrintStream ostream=(printToStderr) ? System.err : System.out;
final ClassLoader ld=ClassLoader.getSystemClassLoader();
String cn=null;
switch (mode) {
case LM_CLASS:
cn=what;
break;
case LM_JAR:
cn=getMainClassFromJar(ostream,what);
break;
default :
throw new InternalError("" + mode + ": Unknown launch mode");
}
cn=cn.replace('/','.');
Class<?> c=null;
try {
c=ld.loadClass(cn);
}
 catch (ClassNotFoundException cnfe) {
abort(ostream,cnfe,"java.launcher.cls.error1",cn);
}
getMainMethod(ostream,c);
return c;
}
static Method getMainMethod(PrintStream ostream,Class<?> clazz){
String classname=clazz.getName();
Method method=null;
try {
method=clazz.getMethod("main",String[].class);
}
 catch (NoSuchMethodException nsme) {
abort(ostream,null,"java.launcher.cls.error4",classname);
}
int mod=method.getModifiers();
if (!Modifier.isStatic(mod)) {
abort(ostream,null,"java.launcher.cls.error2","static",classname);
}
if (method.getReturnType() != java.lang.Void.TYPE) {
abort(ostream,null,"java.launcher.cls.error3",classname);
}
return method;
}
private static final String encprop="sun.jnu.encoding";
private static String encoding=null;
private static boolean isCharsetSupported=false;
static String makePlatformString(boolean printToStderr,byte[] inArray){
final PrintStream ostream=(printToStderr) ? System.err : System.out;
if (encoding == null) {
encoding=System.getProperty(encprop);
isCharsetSupported=Charset.isSupported(encoding);
}
try {
String out=isCharsetSupported ? new String(inArray,encoding) : new String(inArray);
return out;
}
 catch (UnsupportedEncodingException uee) {
abort(ostream,uee,null);
}
return null;
}
}
