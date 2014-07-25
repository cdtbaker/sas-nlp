package com.sun.media.sound;
import java.util.StringTokenizer;
/** 
 * Audio configuration class for exposing attributes specific to the platform or system.
 * @author Kara Kytle
 * @author Florian Bomers
 */
class Platform {
  private static final String libNameMain="jsound";
  private static final String libNameALSA="jsoundalsa";
  private static final String libNameDSound="jsoundds";
  public static final int LIB_MAIN=1;
  public static final int LIB_ALSA=2;
  public static final int LIB_DSOUND=4;
  private static int loadedLibs=0;
  public static final int FEATURE_MIDIIO=1;
  public static final int FEATURE_PORTS=2;
  public static final int FEATURE_DIRECT_AUDIO=3;
  private static boolean signed8;
  private static boolean bigEndian;
  private static String javahome;
  private static String classpath;
static {
    if (Printer.trace)     Printer.trace(">> Platform.java: static");
    loadLibraries();
    readProperties();
  }
  /** 
 * Private constructor.
 */
  private Platform(){
  }
  /** 
 * Dummy method for forcing initialization.
 */
  static void initialize(){
    if (Printer.trace)     Printer.trace("Platform: initialize()");
  }
  /** 
 * Determine whether the system is big-endian.
 */
  static boolean isBigEndian(){
    return bigEndian;
  }
  /** 
 * Determine whether the system takes signed 8-bit data.
 */
  static boolean isSigned8(){
    return signed8;
  }
  /** 
 * Obtain javahome.
 * $$kk: 04.16.99: this is *bad*!!
 */
  static String getJavahome(){
    return javahome;
  }
  /** 
 * Obtain classpath.
 * $$jb: 04.21.99: this is *bad* too!!
 */
  static String getClasspath(){
    return classpath;
  }
  /** 
 * Load the native library or libraries.
 */
  private static void loadLibraries(){
    if (Printer.trace)     Printer.trace(">>Platform.loadLibraries");
    try {
      JSSecurityManager.loadLibrary(libNameMain);
      loadedLibs|=LIB_MAIN;
    }
 catch (    SecurityException e) {
      if (Printer.err)       Printer.err("Security exception loading main native library.  JavaSound requires access to these resources.");
      throw (e);
    }
    String extraLibs=nGetExtraLibraries();
    StringTokenizer st=new StringTokenizer(extraLibs);
    while (st.hasMoreTokens()) {
      String lib=st.nextToken();
      try {
        JSSecurityManager.loadLibrary(lib);
        if (lib.equals(libNameALSA)) {
          loadedLibs|=LIB_ALSA;
          if (Printer.debug)           Printer.debug("Loaded ALSA lib successfully.");
        }
 else         if (lib.equals(libNameDSound)) {
          loadedLibs|=LIB_DSOUND;
          if (Printer.debug)           Printer.debug("Loaded DirectSound lib successfully.");
        }
 else {
          if (Printer.err)           Printer.err("Loaded unknown lib '" + lib + "' successfully.");
        }
      }
 catch (      Throwable t) {
        if (Printer.err)         Printer.err("Couldn't load library " + lib + ": "+ t.toString());
      }
    }
  }
  static boolean isMidiIOEnabled(){
    return isFeatureLibLoaded(FEATURE_MIDIIO);
  }
  static boolean isPortsEnabled(){
    return isFeatureLibLoaded(FEATURE_PORTS);
  }
  static boolean isDirectAudioEnabled(){
    return isFeatureLibLoaded(FEATURE_DIRECT_AUDIO);
  }
  private static boolean isFeatureLibLoaded(  int feature){
    if (Printer.debug)     Printer.debug("Platform: Checking for feature " + feature + "...");
    int requiredLib=nGetLibraryForFeature(feature);
    boolean isLoaded=(requiredLib != 0) && ((loadedLibs & requiredLib) == requiredLib);
    if (Printer.debug)     Printer.debug("          ...needs library " + requiredLib + ". Result is loaded="+ isLoaded);
    return isLoaded;
  }
  private native static boolean nIsBigEndian();
  private native static boolean nIsSigned8();
  private native static String nGetExtraLibraries();
  private native static int nGetLibraryForFeature(  int feature);
  /** 
 * Read the required system properties.
 */
  private static void readProperties(){
    bigEndian=nIsBigEndian();
    signed8=nIsSigned8();
    javahome=JSSecurityManager.getProperty("java.home");
    classpath=JSSecurityManager.getProperty("java.class.path");
  }
}
