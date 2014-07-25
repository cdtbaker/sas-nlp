package sun.applet;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;
import sun.net.www.ParseUtil;
/** 
 * The main entry point into AppletViewer.
 */
public class Main {
  /** 
 * The file which contains all of the AppletViewer specific properties.
 */
  static File theUserPropertiesFile;
  /** 
 * The default key/value pairs for the required user-specific properties.
 */
  static final String[][] avDefaultUserProps={{"http.proxyHost",""},{"http.proxyPort","80"},{"package.restrict.access.sun","true"}};
static {
    File userHome=new File(System.getProperty("user.home"));
    userHome.canWrite();
    theUserPropertiesFile=new File(userHome,".appletviewer");
  }
  private static AppletMessageHandler amh=new AppletMessageHandler("appletviewer");
  /** 
 * Member variables set according to options passed in to AppletViewer.
 */
  private boolean debugFlag=false;
  private boolean helpFlag=false;
  private String encoding=null;
  private boolean noSecurityFlag=false;
  private static boolean cmdLineTestFlag=false;
  /** 
 * The list of valid URLs passed in to AppletViewer.
 */
  private static Vector urlList=new Vector(1);
  public static final String theVersion=System.getProperty("java.version");
  /** 
 * The main entry point into AppletViewer.
 */
  public static void main(  String[] args){
    Main m=new Main();
    int ret=m.run(args);
    if ((ret != 0) || (cmdLineTestFlag))     System.exit(ret);
  }
  private int run(  String[] args){
    try {
      if (args.length == 0) {
        usage();
        return 0;
      }
      for (int i=0; i < args.length; ) {
        int j=decodeArg(args,i);
        if (j == 0) {
          throw new ParseException(lookup("main.err.unrecognizedarg",args[i]));
        }
        i+=j;
      }
    }
 catch (    ParseException e) {
      System.err.println(e.getMessage());
      return 1;
    }
    if (helpFlag) {
      usage();
      return 0;
    }
    if (urlList.size() == 0) {
      System.err.println(lookup("main.err.inputfile"));
      return 1;
    }
    if (debugFlag) {
      return invokeDebugger(args);
    }
    if (!noSecurityFlag && (System.getSecurityManager() == null))     init();
    for (int i=0; i < urlList.size(); i++) {
      try {
        AppletViewer.parse((URL)urlList.elementAt(i),encoding);
      }
 catch (      IOException e) {
        System.err.println(lookup("main.err.io",e.getMessage()));
        return 1;
      }
    }
    return 0;
  }
  private static void usage(){
    System.out.println(lookup("usage"));
  }
  /** 
 * Decode a single argument in an array and return the number of elements
 * used.
 * @param args The array of arguments.
 * @param i    The argument to decode.
 * @return     The number of array elements used when the argument was
 * decoded.
 * @exception ParseExceptionThrown when there is a problem with something in the
 * argument array.
 */
  private int decodeArg(  String[] args,  int i) throws ParseException {
    String arg=args[i];
    int argc=args.length;
    if ("-help".equalsIgnoreCase(arg) || "-?".equals(arg)) {
      helpFlag=true;
      return 1;
    }
 else     if ("-encoding".equals(arg) && (i < argc - 1)) {
      if (encoding != null)       throw new ParseException(lookup("main.err.dupoption",arg));
      encoding=args[++i];
      return 2;
    }
 else     if ("-debug".equals(arg)) {
      debugFlag=true;
      return 1;
    }
 else     if ("-Xnosecurity".equals(arg)) {
      System.err.println();
      System.err.println(lookup("main.warn.nosecmgr"));
      System.err.println();
      noSecurityFlag=true;
      return 1;
    }
 else     if ("-XcmdLineTest".equals(arg)) {
      cmdLineTestFlag=true;
      return 1;
    }
 else     if (arg.startsWith("-")) {
      throw new ParseException(lookup("main.err.unsupportedopt",arg));
    }
 else {
      URL url=parseURL(arg);
      if (url != null) {
        urlList.addElement(url);
        return 1;
      }
    }
    return 0;
  }
  /** 
 * Following the relevant RFC, construct a valid URL based on the passed in
 * string.
 * @param url  a string which represents either a relative or absolute URL.
 * @return     a URL when the passed in string can be interpreted according
 * to the RFC, <code>null</code> otherwise.
 * @exception ParseExceptionThrown when we are unable to construct a proper URL from the
 * passed in string.
 */
  private URL parseURL(  String url) throws ParseException {
    URL u=null;
    String prefix="file:";
    try {
      if (url.indexOf(':') <= 1) {
        u=ParseUtil.fileToEncodedURL(new File(url));
      }
 else       if (url.startsWith(prefix) && url.length() != prefix.length() && !(new File(url.substring(prefix.length())).isAbsolute())) {
        String path=ParseUtil.fileToEncodedURL(new File(System.getProperty("user.dir"))).getPath() + url.substring(prefix.length());
        u=new URL("file","",path);
      }
 else {
        u=new URL(url);
      }
    }
 catch (    MalformedURLException e) {
      throw new ParseException(lookup("main.err.badurl",url,e.getMessage()));
    }
    return u;
  }
  /** 
 * Invoke the debugger with the arguments passed in to appletviewer.
 * @param args The arguments passed into the debugger.
 * @return     <code>0</code> if the debugger is invoked successfully,
 * <code>1</code> otherwise.
 */
  private int invokeDebugger(  String[] args){
    String[] newArgs=new String[args.length + 1];
    int current=0;
    String phonyDir=System.getProperty("java.home") + File.separator + "phony";
    newArgs[current++]="-Djava.class.path=" + phonyDir;
    newArgs[current++]="sun.applet.Main";
    for (int i=0; i < args.length; i++) {
      if (!("-debug".equals(args[i]))) {
        newArgs[current++]=args[i];
      }
    }
    try {
      Class c=Class.forName("com.sun.tools.example.debug.tty.TTY",true,ClassLoader.getSystemClassLoader());
      Method m=c.getDeclaredMethod("main",new Class[]{String[].class});
      m.invoke(null,new Object[]{newArgs});
    }
 catch (    ClassNotFoundException cnfe) {
      System.err.println(lookup("main.debug.cantfinddebug"));
      return 1;
    }
catch (    NoSuchMethodException nsme) {
      System.err.println(lookup("main.debug.cantfindmain"));
      return 1;
    }
catch (    InvocationTargetException ite) {
      System.err.println(lookup("main.debug.exceptionindebug"));
      return 1;
    }
catch (    IllegalAccessException iae) {
      System.err.println(lookup("main.debug.cantaccess"));
      return 1;
    }
    return 0;
  }
  private void init(){
    Properties avProps=getAVProps();
    avProps.put("browser","sun.applet.AppletViewer");
    avProps.put("browser.version","1.06");
    avProps.put("browser.vendor","Oracle Corporation");
    avProps.put("http.agent","Java(tm) 2 SDK, Standard Edition v" + theVersion);
    avProps.put("package.restrict.definition.java","true");
    avProps.put("package.restrict.definition.sun","true");
    avProps.put("java.version.applet","true");
    avProps.put("java.vendor.applet","true");
    avProps.put("java.vendor.url.applet","true");
    avProps.put("java.class.version.applet","true");
    avProps.put("os.name.applet","true");
    avProps.put("os.version.applet","true");
    avProps.put("os.arch.applet","true");
    avProps.put("file.separator.applet","true");
    avProps.put("path.separator.applet","true");
    avProps.put("line.separator.applet","true");
    Properties sysProps=System.getProperties();
    for (Enumeration e=sysProps.propertyNames(); e.hasMoreElements(); ) {
      String key=(String)e.nextElement();
      String val=(String)sysProps.getProperty(key);
      String oldVal;
      if ((oldVal=(String)avProps.setProperty(key,val)) != null)       System.err.println(lookup("main.warn.prop.overwrite",key,oldVal,val));
    }
    System.setProperties(avProps);
    if (!noSecurityFlag) {
      System.setSecurityManager(new AppletSecurity());
    }
 else {
      System.err.println(lookup("main.nosecmgr"));
    }
  }
  /** 
 * Read the AppletViewer user-specific properties.  Typically, these
 * properties should reside in the file $USER/.appletviewer.  If this file
 * does not exist, one will be created.  Information for this file will
 * be gleaned from $USER/.hotjava/properties.  If that file does not exist,
 * then default values will be used.
 * @return     A Properties object containing all of the AppletViewer
 * user-specific properties.
 */
  private Properties getAVProps(){
    Properties avProps=new Properties();
    File dotAV=theUserPropertiesFile;
    if (dotAV.exists()) {
      if (dotAV.canRead()) {
        avProps=getAVProps(dotAV);
      }
 else {
        System.err.println(lookup("main.warn.cantreadprops",dotAV.toString()));
        avProps=setDefaultAVProps();
      }
    }
 else {
      File userHome=new File(System.getProperty("user.home"));
      File dotHJ=new File(userHome,".hotjava");
      dotHJ=new File(dotHJ,"properties");
      if (dotHJ.exists()) {
        avProps=getAVProps(dotHJ);
      }
 else {
        System.err.println(lookup("main.warn.cantreadprops",dotHJ.toString()));
        avProps=setDefaultAVProps();
      }
      try {
        FileOutputStream out=new FileOutputStream(dotAV);
        avProps.store(out,lookup("main.prop.store"));
        out.close();
      }
 catch (      IOException e) {
        System.err.println(lookup("main.err.prop.cantsave",dotAV.toString()));
      }
    }
    return avProps;
  }
  /** 
 * Set the AppletViewer user-specific properties to be the default values.
 * @return     A Properties object containing all of the AppletViewer
 * user-specific properties, set to the default values.
 */
  private Properties setDefaultAVProps(){
    Properties avProps=new Properties();
    for (int i=0; i < avDefaultUserProps.length; i++) {
      avProps.setProperty(avDefaultUserProps[i][0],avDefaultUserProps[i][1]);
    }
    return avProps;
  }
  /** 
 * Given a file, find only the properties that are setable by AppletViewer.
 * @param inFile A Properties file from which we select the properties of
 * interest.
 * @return     A Properties object containing all of the AppletViewer
 * user-specific properties.
 */
  private Properties getAVProps(  File inFile){
    Properties avProps=new Properties();
    Properties tmpProps=new Properties();
    try {
      FileInputStream in=new FileInputStream(inFile);
      tmpProps.load(new BufferedInputStream(in));
      in.close();
    }
 catch (    IOException e) {
      System.err.println(lookup("main.err.prop.cantread",inFile.toString()));
    }
    for (int i=0; i < avDefaultUserProps.length; i++) {
      String value=tmpProps.getProperty(avDefaultUserProps[i][0]);
      if (value != null) {
        avProps.setProperty(avDefaultUserProps[i][0],value);
      }
 else {
        avProps.setProperty(avDefaultUserProps[i][0],avDefaultUserProps[i][1]);
      }
    }
    return avProps;
  }
  /** 
 * Methods for easier i18n handling.
 */
  private static String lookup(  String key){
    return amh.getMessage(key);
  }
  private static String lookup(  String key,  String arg0){
    return amh.getMessage(key,arg0);
  }
  private static String lookup(  String key,  String arg0,  String arg1){
    return amh.getMessage(key,arg0,arg1);
  }
  private static String lookup(  String key,  String arg0,  String arg1,  String arg2){
    return amh.getMessage(key,arg0,arg1,arg2);
  }
class ParseException extends RuntimeException {
    public ParseException(    String msg){
      super(msg);
    }
    public ParseException(    Throwable t){
      super(t.getMessage());
      this.t=t;
    }
    Throwable t=null;
  }
}
