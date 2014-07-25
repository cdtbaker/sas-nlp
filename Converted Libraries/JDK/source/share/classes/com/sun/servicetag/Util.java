package com.sun.servicetag;
import java.io.*;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.TimeZone;
import java.util.UUID;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
class Util {
  private static boolean verbose=(System.getProperty("servicetag.verbose") != null);
  private static String jrepath=null;
  private static final String REGKEY_TAIL="microsoft\\windows\\currentversion\\app paths\\stclient.exe";
  private static final String STCLIENT_TAIL="sun\\servicetag\\stclient.exe";
  private static final String WIN32_STCLIENT="c:\\Program Files (x86)\\" + STCLIENT_TAIL;
  static boolean isVerbose(){
    return verbose;
  }
  /** 
 * Gets the pathname of JRE in the running platform
 * This can be a JDK or JRE.
 */
  static synchronized String getJrePath(){
    if (jrepath == null) {
      String javaHome=System.getProperty("java.home");
      jrepath=javaHome + File.separator + "jre";
      File f=new File(jrepath,"lib");
      if (!f.exists()) {
        jrepath=javaHome;
      }
    }
    return jrepath;
  }
  /** 
 * Tests if the running platform is a JDK.
 */
  static boolean isJdk(){
    return getJrePath().endsWith(File.separator + "jre");
  }
  /** 
 * Generates the URN string of "urn:st" namespace
 */
  static String generateURN(){
    return "urn:st:" + UUID.randomUUID().toString();
  }
  static int getIntValue(  String value){
    try {
      return Integer.parseInt(value);
    }
 catch (    NumberFormatException e) {
      throw new IllegalArgumentException("\"" + value + "\""+ " expected to be an integer");
    }
  }
  /** 
 * Formats the Date into a timestamp string in YYYY-MM-dd HH:mm:ss GMT.
 * @param timestamp Date
 * @return a string representation of the timestamp
 * in the YYYY-MM-dd HH:mm:ss GMT format.
 */
  static String formatTimestamp(  Date timestamp){
    if (timestamp == null) {
      return "[No timestamp]";
    }
    SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
    df.setTimeZone(TimeZone.getTimeZone("GMT"));
    return df.format(timestamp);
  }
  /** 
 * Parses a timestamp string in YYYY-MM-dd HH:mm:ss GMT format.
 * @param timestamp Timestamp in the YYYY-MM-dd HH:mm:ss GMT format.
 * @return Date
 */
  static Date parseTimestamp(  String timestamp){
    SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
    df.setTimeZone(TimeZone.getTimeZone("GMT"));
    try {
      return df.parse(timestamp);
    }
 catch (    ParseException e) {
      e.printStackTrace();
      return new Date();
    }
  }
  static String commandOutput(  Process p) throws IOException {
    Reader r=null;
    Reader err=null;
    try {
      r=new InputStreamReader(p.getInputStream());
      err=new InputStreamReader(p.getErrorStream());
      String output=commandOutput(r);
      String errorMsg=commandOutput(err);
      p.waitFor();
      return output + errorMsg.trim();
    }
 catch (    InterruptedException e) {
      if (isVerbose()) {
        e.printStackTrace();
      }
      return e.getMessage();
    }
 finally {
      try {
        if (r != null) {
          r.close();
        }
      }
  finally {
        if (err != null) {
          err.close();
        }
      }
    }
  }
  static String commandOutput(  Reader r) throws IOException {
    StringBuilder sb=new StringBuilder();
    int c;
    while ((c=r.read()) > 0) {
      if (c != '\r') {
        sb.append((char)c);
      }
    }
    return sb.toString();
  }
  static int getJdkVersion(){
    parseVersion();
    return jdkVersion;
  }
  static int getUpdateVersion(){
    parseVersion();
    return jdkUpdate;
  }
  private static int jdkVersion=0;
  private static int jdkUpdate=0;
  private static synchronized void parseVersion(){
    if (jdkVersion > 0) {
      return;
    }
    String cs=System.getProperty("java.runtime.version");
    if (cs.length() >= 5 && Character.isDigit(cs.charAt(0)) && cs.charAt(1) == '.' && Character.isDigit(cs.charAt(2)) && cs.charAt(3) == '.' && Character.isDigit(cs.charAt(4))) {
      jdkVersion=Character.digit(cs.charAt(2),10);
      cs=cs.substring(5,cs.length());
      if (cs.charAt(0) == '_' && cs.length() >= 3 && Character.isDigit(cs.charAt(1)) && Character.isDigit(cs.charAt(2))) {
        int nextChar=3;
        try {
          String uu=cs.substring(1,3);
          jdkUpdate=Integer.valueOf(uu).intValue();
        }
 catch (        NumberFormatException e) {
          return;
        }
      }
    }
 else {
      throw new InternalError("Invalid java.runtime.version" + cs);
    }
  }
  /** 
 * Returns this java string as a null-terminated byte array
 */
  private static byte[] stringToByteArray(  String str){
    return (str + "\u0000").getBytes();
  }
  /** 
 * Converts a null-terminated byte array to java string
 */
  private static String byteArrayToString(  byte[] array){
    return new String(array,0,array.length - 1);
  }
  /** 
 * Gets the stclient path using a well known location from
 * the Windows platform Registry, ensuring the path returned
 * by the registry is really the one we are looking for,
 * otherwise it will return null.
 */
  private static File getWindowsStClientFile(  boolean wow64){
    File out=null;
    String regKey=(wow64 == true) ? "software\\Wow6432Node\\" + REGKEY_TAIL : "software\\" + REGKEY_TAIL;
    String keyName="";
    String path=getRegistryKey(regKey,keyName);
    if (path != null && (new File(path)).exists() && path.toLowerCase().endsWith(STCLIENT_TAIL.toLowerCase())) {
      out=new File(path);
    }
    if (isVerbose()) {
      System.out.println("stclient=" + out);
    }
    return out;
  }
  /** 
 * Finds a stclient in 32 and 64 bit environments, first by querying
 * the windows registry, if not then get the well known paths for
 * 64bit see http://support.microsoft.com/kb/896459
 */
  static File getWindowsStClientFile(){
    File stclient=null;
    if (System.getProperty("os.arch").equals("x86")) {
      stclient=getWindowsStClientFile(false);
      if (stclient != null) {
        return stclient;
      }
    }
 else {
      stclient=getWindowsStClientFile(true);
      if (stclient != null) {
        return stclient;
      }
      stclient=new File(WIN32_STCLIENT);
      if (stclient.canExecute()) {
        if (isVerbose()) {
          System.out.println("stclient(default)=" + stclient);
        }
        return stclient;
      }
    }
    if (isVerbose()) {
      System.out.println("stclient not found");
    }
    return null;
  }
  /** 
 * This uses reflection to access a private java windows registry
 * interface, any changes to that Class must be appropriately adjusted.
 * Returns a null if unsuccessful.
 */
  private static String getRegistryKey(  String regKey,  String keyName){
    String out=null;
    try {
      Class<?> clazz=Class.forName("java.util.prefs.WindowsPreferences");
      Method winRegOpenKeyM=clazz.getDeclaredMethod("WindowsRegOpenKey",int.class,byte[].class,int.class);
      winRegOpenKeyM.setAccessible(true);
      Method winRegCloseKeyM=clazz.getDeclaredMethod("WindowsRegCloseKey",int.class);
      winRegCloseKeyM.setAccessible(true);
      Method winRegQueryValueM=clazz.getDeclaredMethod("WindowsRegQueryValueEx",int.class,byte[].class);
      winRegQueryValueM.setAccessible(true);
      int HKLM=getValueFromStaticField("HKEY_LOCAL_MACHINE",clazz);
      int KEY_READ=getValueFromStaticField("KEY_READ",clazz);
      int ERROR_CODE=getValueFromStaticField("ERROR_CODE",clazz);
      int NATIVE_HANDLE=getValueFromStaticField("NATIVE_HANDLE",clazz);
      int ERROR_SUCCESS=getValueFromStaticField("ERROR_SUCCESS",clazz);
      byte[] reg=stringToByteArray(regKey);
      byte[] key=stringToByteArray(keyName);
      int[] result=(int[])winRegOpenKeyM.invoke(null,HKLM,reg,KEY_READ);
      if (result[ERROR_CODE] == ERROR_SUCCESS) {
        byte[] stvalue=(byte[])winRegQueryValueM.invoke(null,result[NATIVE_HANDLE],key);
        out=byteArrayToString(stvalue);
        winRegCloseKeyM.invoke(null,result[NATIVE_HANDLE]);
      }
    }
 catch (    Exception ex) {
      if (isVerbose()) {
        ex.printStackTrace();
      }
    }
    return out;
  }
  private static int getValueFromStaticField(  String fldName,  Class<?> klass) throws Exception {
    Field f=klass.getDeclaredField(fldName);
    f.setAccessible(true);
    return f.getInt(null);
  }
}
