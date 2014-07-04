package sun.tools.javac;
import sun.tools.java.*;
import sun.tools.util.CommandLine;
import sun.tools.asm.Assembler;
import java.util.*;
import java.io.*;
import java.text.MessageFormat;
/** 
 * Main program of the Java compiler
 * WARNING: The contents of this source file are not part of any
 * supported API.  Code that depends on them does so at its own risk:
 * they are subject to change or removal without notice.
 * @deprecated As of J2SE 1.3, the preferred way to compile Java
 * language sources is by using the new compiler,
 * com.sun.tools.javac.Main.
 */
@Deprecated public class Main implements Constants {
  /** 
 * Name of the program.
 */
  String program;
  /** 
 * The stream where error message are printed.
 */
  OutputStream out;
  /** 
 * Constructor.
 */
  public Main(  OutputStream out,  String program){
    this.out=out;
    this.program=program;
  }
  /** 
 * Exit status.
 * We introduce a separate integer status variable, and do not alter the
 * convention that 'compile' returns a boolean true upon a successful
 * compilation with no errors.  (JavaTest relies on this.)
 */
  public static final int EXIT_OK=0;
  public static final int EXIT_ERROR=1;
  public static final int EXIT_CMDERR=2;
  public static final int EXIT_SYSERR=3;
  public static final int EXIT_ABNORMAL=4;
  private int exitStatus;
  public int getExitStatus(){
    return exitStatus;
  }
  public boolean compilationPerformedSuccessfully(){
    return exitStatus == EXIT_OK || exitStatus == EXIT_ERROR;
  }
  public boolean compilationReportedErrors(){
    return exitStatus != EXIT_OK;
  }
  /** 
 * Output a message.
 */
  private void output(  String msg){
    PrintStream out=this.out instanceof PrintStream ? (PrintStream)this.out : new PrintStream(this.out,true);
    out.println(msg);
  }
  /** 
 * Top level error message.  This method is called when the
 * environment could not be set up yet.
 */
  private void error(  String msg){
    exitStatus=EXIT_CMDERR;
    output(getText(msg));
  }
  private void error(  String msg,  String arg1){
    exitStatus=EXIT_CMDERR;
    output(getText(msg,arg1));
  }
  private void error(  String msg,  String arg1,  String arg2){
    exitStatus=EXIT_CMDERR;
    output(getText(msg,arg1,arg2));
  }
  /** 
 * Print usage message and make exit status an error.
 * Note: 'javac' invoked without any arguments is considered
 * be an error.
 */
  public void usage_error(){
    error("main.usage",program);
  }
  private static ResourceBundle messageRB;
  /** 
 * Initialize ResourceBundle
 */
  static void initResource(){
    try {
      messageRB=ResourceBundle.getBundle("sun.tools.javac.resources.javac");
    }
 catch (    MissingResourceException e) {
      throw new Error("Fatal: Resource for javac is missing");
    }
  }
  /** 
 * get and format message string from resource
 */
  public static String getText(  String key){
    return getText(key,(String)null);
  }
  public static String getText(  String key,  int num){
    return getText(key,Integer.toString(num));
  }
  public static String getText(  String key,  String fixed){
    return getText(key,fixed,null);
  }
  public static String getText(  String key,  String fixed1,  String fixed2){
    return getText(key,fixed1,fixed2,null);
  }
  public static String getText(  String key,  String fixed1,  String fixed2,  String fixed3){
    if (messageRB == null) {
      initResource();
    }
    try {
      String message=messageRB.getString(key);
      return MessageFormat.format(message,fixed1,fixed2,fixed3);
    }
 catch (    MissingResourceException e) {
      if (fixed1 == null)       fixed1="null";
      if (fixed2 == null)       fixed2="null";
      if (fixed3 == null)       fixed3="null";
      String message="JAVAC MESSAGE FILE IS BROKEN: key={0}, arguments={1}, {2}, {3}";
      return MessageFormat.format(message,key,fixed1,fixed2,fixed3);
    }
  }
  private static final String[] releases={"1.1","1.2","1.3","1.4"};
  private static final short[] majorVersions={45,46,47,48};
  private static final short[] minorVersions={3,0,0,0};
  /** 
 * Run the compiler
 */
  public synchronized boolean compile(  String argv[]){
    String sourcePathArg=null;
    String classPathArg=null;
    String sysClassPathArg=null;
    String extDirsArg=null;
    boolean verbosePath=false;
    String targetArg=null;
    short majorVersion=JAVA_DEFAULT_VERSION;
    short minorVersion=JAVA_DEFAULT_MINOR_VERSION;
    File destDir=null;
    File covFile=null;
    String optJcov="-Xjcov";
    String optJcovFile="-Xjcov:file=";
    int flags=F_WARNINGS | F_DEBUG_LINES | F_DEBUG_SOURCE;
    long tm=System.currentTimeMillis();
    Vector v=new Vector();
    boolean nowrite=false;
    String props=null;
    String encoding=null;
    String prior_g=null;
    String prior_O=null;
    exitStatus=EXIT_OK;
    try {
      argv=CommandLine.parse(argv);
    }
 catch (    FileNotFoundException e) {
      error("javac.err.cant.read",e.getMessage());
      System.exit(1);
    }
catch (    IOException e) {
      e.printStackTrace();
      System.exit(1);
    }
    for (int i=0; i < argv.length; i++) {
      if (argv[i].equals("-g")) {
        if (prior_g != null && !(prior_g.equals("-g")))         error("main.conflicting.options",prior_g,"-g");
        prior_g="-g";
        flags|=F_DEBUG_LINES;
        flags|=F_DEBUG_VARS;
        flags|=F_DEBUG_SOURCE;
      }
 else       if (argv[i].equals("-g:none")) {
        if (prior_g != null && !(prior_g.equals("-g:none")))         error("main.conflicting.options",prior_g,"-g:none");
        prior_g="-g:none";
        flags&=~F_DEBUG_LINES;
        flags&=~F_DEBUG_VARS;
        flags&=~F_DEBUG_SOURCE;
      }
 else       if (argv[i].startsWith("-g:")) {
        if (prior_g != null && !(prior_g.equals(argv[i])))         error("main.conflicting.options",prior_g,argv[i]);
        prior_g=argv[i];
        String args=argv[i].substring("-g:".length());
        flags&=~F_DEBUG_LINES;
        flags&=~F_DEBUG_VARS;
        flags&=~F_DEBUG_SOURCE;
        while (true) {
          if (args.startsWith("lines")) {
            flags|=F_DEBUG_LINES;
            args=args.substring("lines".length());
          }
 else           if (args.startsWith("vars")) {
            flags|=F_DEBUG_VARS;
            args=args.substring("vars".length());
          }
 else           if (args.startsWith("source")) {
            flags|=F_DEBUG_SOURCE;
            args=args.substring("source".length());
          }
 else {
            error("main.bad.debug.option",argv[i]);
            usage_error();
            return false;
          }
          if (args.length() == 0)           break;
          if (args.startsWith(","))           args=args.substring(",".length());
        }
      }
 else       if (argv[i].equals("-O")) {
        if (prior_O != null && !(prior_O.equals("-O")))         error("main.conflicting.options",prior_O,"-O");
        prior_O="-O";
      }
 else       if (argv[i].equals("-nowarn")) {
        flags&=~F_WARNINGS;
      }
 else       if (argv[i].equals("-deprecation")) {
        flags|=F_DEPRECATION;
      }
 else       if (argv[i].equals("-verbose")) {
        flags|=F_VERBOSE;
      }
 else       if (argv[i].equals("-nowrite")) {
        nowrite=true;
      }
 else       if (argv[i].equals("-classpath")) {
        if ((i + 1) < argv.length) {
          if (classPathArg != null) {
            error("main.option.already.seen","-classpath");
          }
          classPathArg=argv[++i];
        }
 else {
          error("main.option.requires.argument","-classpath");
          usage_error();
          return false;
        }
      }
 else       if (argv[i].equals("-sourcepath")) {
        if ((i + 1) < argv.length) {
          if (sourcePathArg != null) {
            error("main.option.already.seen","-sourcepath");
          }
          sourcePathArg=argv[++i];
        }
 else {
          error("main.option.requires.argument","-sourcepath");
          usage_error();
          return false;
        }
      }
 else       if (argv[i].equals("-sysclasspath")) {
        if ((i + 1) < argv.length) {
          if (sysClassPathArg != null) {
            error("main.option.already.seen","-sysclasspath");
          }
          sysClassPathArg=argv[++i];
        }
 else {
          error("main.option.requires.argument","-sysclasspath");
          usage_error();
          return false;
        }
      }
 else       if (argv[i].equals("-bootclasspath")) {
        if ((i + 1) < argv.length) {
          if (sysClassPathArg != null) {
            error("main.option.already.seen","-bootclasspath");
          }
          sysClassPathArg=argv[++i];
        }
 else {
          error("main.option.requires.argument","-bootclasspath");
          usage_error();
          return false;
        }
      }
 else       if (argv[i].equals("-extdirs")) {
        if ((i + 1) < argv.length) {
          if (extDirsArg != null) {
            error("main.option.already.seen","-extdirs");
          }
          extDirsArg=argv[++i];
        }
 else {
          error("main.option.requires.argument","-extdirs");
          usage_error();
          return false;
        }
      }
 else       if (argv[i].equals("-encoding")) {
        if ((i + 1) < argv.length) {
          if (encoding != null)           error("main.option.already.seen","-encoding");
          encoding=argv[++i];
        }
 else {
          error("main.option.requires.argument","-encoding");
          usage_error();
          return false;
        }
      }
 else       if (argv[i].equals("-target")) {
        if ((i + 1) < argv.length) {
          if (targetArg != null)           error("main.option.already.seen","-target");
          targetArg=argv[++i];
          int j;
          for (j=0; j < releases.length; j++) {
            if (releases[j].equals(targetArg)) {
              majorVersion=majorVersions[j];
              minorVersion=minorVersions[j];
              break;
            }
          }
          if (j == releases.length) {
            error("main.unknown.release",targetArg);
            usage_error();
            return false;
          }
        }
 else {
          error("main.option.requires.argument","-target");
          usage_error();
          return false;
        }
      }
 else       if (argv[i].equals("-d")) {
        if ((i + 1) < argv.length) {
          if (destDir != null)           error("main.option.already.seen","-d");
          destDir=new File(argv[++i]);
          if (!destDir.exists()) {
            error("main.no.such.directory",destDir.getPath());
            usage_error();
            return false;
          }
        }
 else {
          error("main.option.requires.argument","-d");
          usage_error();
          return false;
        }
      }
 else       if (argv[i].equals(optJcov)) {
        flags|=F_COVERAGE;
        flags&=~F_OPT;
        flags&=~F_OPT_INTERCLASS;
      }
 else       if ((argv[i].startsWith(optJcovFile)) && (argv[i].length() > optJcovFile.length())) {
        covFile=new File(argv[i].substring(optJcovFile.length()));
        flags&=~F_OPT;
        flags&=~F_OPT_INTERCLASS;
        flags|=F_COVERAGE;
        flags|=F_COVDATA;
      }
 else       if (argv[i].equals("-XO")) {
        if (prior_O != null && !(prior_O.equals("-XO")))         error("main.conflicting.options",prior_O,"-XO");
        prior_O="-XO";
        flags|=F_OPT;
      }
 else       if (argv[i].equals("-Xinterclass")) {
        if (prior_O != null && !(prior_O.equals("-Xinterclass")))         error("main.conflicting.options",prior_O,"-Xinterclass");
        prior_O="-Xinterclass";
        flags|=F_OPT;
        flags|=F_OPT_INTERCLASS;
        flags|=F_DEPENDENCIES;
      }
 else       if (argv[i].equals("-Xdepend")) {
        flags|=F_DEPENDENCIES;
      }
 else       if (argv[i].equals("-Xdebug")) {
        flags|=F_DUMP;
      }
 else       if (argv[i].equals("-xdepend") || argv[i].equals("-Xjws")) {
        flags|=F_PRINT_DEPENDENCIES;
        if (out == System.err) {
          out=System.out;
        }
      }
 else       if (argv[i].equals("-Xstrictdefault")) {
        flags|=F_STRICTDEFAULT;
      }
 else       if (argv[i].equals("-Xverbosepath")) {
        verbosePath=true;
      }
 else       if (argv[i].equals("-Xstdout")) {
        out=System.out;
      }
 else       if (argv[i].equals("-X")) {
        error("main.unsupported.usage");
        return false;
      }
 else       if (argv[i].equals("-Xversion1.2")) {
        flags|=F_VERSION12;
      }
 else       if (argv[i].endsWith(".java")) {
        v.addElement(argv[i]);
      }
 else {
        error("main.no.such.option",argv[i]);
        usage_error();
        return false;
      }
    }
    if (v.size() == 0 || exitStatus == EXIT_CMDERR) {
      usage_error();
      return false;
    }
    BatchEnvironment env=BatchEnvironment.create(out,sourcePathArg,classPathArg,sysClassPathArg,extDirsArg);
    if (verbosePath) {
      output(getText("main.path.msg",env.sourcePath.toString(),env.binaryPath.toString()));
    }
    env.flags|=flags;
    env.majorVersion=majorVersion;
    env.minorVersion=minorVersion;
    env.covFile=covFile;
    env.setCharacterEncoding(encoding);
    String noMemoryErrorString=getText("main.no.memory");
    String stackOverflowErrorString=getText("main.stack.overflow");
    env.error(0,"warn.class.is.deprecated","sun.tools.javac.Main");
    try {
      for (Enumeration e=v.elements(); e.hasMoreElements(); ) {
        File file=new File((String)e.nextElement());
        try {
          env.parseFile(new ClassFile(file));
        }
 catch (        FileNotFoundException ee) {
          env.error(0,"cant.read",file.getPath());
          exitStatus=EXIT_CMDERR;
        }
      }
      for (Enumeration e=env.getClasses(); e.hasMoreElements(); ) {
        ClassDeclaration c=(ClassDeclaration)e.nextElement();
        if (c.getStatus() == CS_PARSED) {
          if (c.getClassDefinition().isLocal())           continue;
          try {
            c.getClassDefinition(env);
          }
 catch (          ClassNotFound ee) {
          }
        }
      }
      ByteArrayOutputStream buf=new ByteArrayOutputStream(4096);
      boolean done;
      do {
        done=true;
        env.flushErrors();
        for (Enumeration e=env.getClasses(); e.hasMoreElements(); ) {
          ClassDeclaration c=(ClassDeclaration)e.nextElement();
          SourceClass src;
switch (c.getStatus()) {
case CS_UNDEFINED:
            if (!env.dependencies()) {
              break;
            }
case CS_SOURCE:
          if (tracing)           env.dtEvent("Main.compile (SOURCE): loading, " + c);
        done=false;
      env.loadDefinition(c);
    if (c.getStatus() != CS_PARSED) {
      if (tracing)       env.dtEvent("Main.compile (SOURCE): not parsed, " + c);
      break;
    }
case CS_PARSED:
  if (c.getClassDefinition().isInsideLocal()) {
    if (tracing)     env.dtEvent("Main.compile (PARSED): skipping local class, " + c);
    continue;
  }
done=false;
if (tracing) env.dtEvent("Main.compile (PARSED): checking, " + c);
src=(SourceClass)c.getClassDefinition(env);
src.check(env);
c.setDefinition(src,CS_CHECKED);
case CS_CHECKED:
src=(SourceClass)c.getClassDefinition(env);
if (src.getError()) {
if (tracing) env.dtEvent("Main.compile (CHECKED): bailing out on error, " + c);
c.setDefinition(src,CS_COMPILED);
break;
}
done=false;
buf.reset();
if (tracing) env.dtEvent("Main.compile (CHECKED): compiling, " + c);
src.compile(buf);
c.setDefinition(src,CS_COMPILED);
src.cleanup(env);
if (src.getNestError() || nowrite) {
continue;
}
String pkgName=c.getName().getQualifier().toString().replace('.',File.separatorChar);
String className=c.getName().getFlatName().toString().replace('.',SIGC_INNERCLASS) + ".class";
File file;
if (destDir != null) {
if (pkgName.length() > 0) {
file=new File(destDir,pkgName);
if (!file.exists()) {
file.mkdirs();
}
file=new File(file,className);
}
 else {
file=new File(destDir,className);
}
}
 else {
ClassFile classfile=(ClassFile)src.getSource();
if (classfile.isZipped()) {
env.error(0,"cant.write",classfile.getPath());
exitStatus=EXIT_CMDERR;
continue;
}
file=new File(classfile.getPath());
file=new File(file.getParent(),className);
}
try {
FileOutputStream out=new FileOutputStream(file.getPath());
buf.writeTo(out);
out.close();
if (env.verbose()) {
output(getText("main.wrote",file.getPath()));
}
}
 catch (IOException ee) {
env.error(0,"cant.write",file.getPath());
exitStatus=EXIT_CMDERR;
}
if (env.print_dependencies()) {
src.printClassDependencies(env);
}
}
}
}
 while (!done);
}
 catch (OutOfMemoryError ee) {
env.output(noMemoryErrorString);
exitStatus=EXIT_SYSERR;
return false;
}
catch (StackOverflowError ee) {
env.output(stackOverflowErrorString);
exitStatus=EXIT_SYSERR;
return false;
}
catch (Error ee) {
if (env.nerrors == 0 || env.dump()) {
ee.printStackTrace();
env.error(0,"fatal.error");
exitStatus=EXIT_ABNORMAL;
}
}
catch (Exception ee) {
if (env.nerrors == 0 || env.dump()) {
ee.printStackTrace();
env.error(0,"fatal.exception");
exitStatus=EXIT_ABNORMAL;
}
}
int ndepfiles=env.deprecationFiles.size();
if (ndepfiles > 0 && env.warnings()) {
int ndeps=env.ndeprecations;
Object file1=env.deprecationFiles.elementAt(0);
if (env.deprecation()) {
if (ndepfiles > 1) {
env.error(0,"warn.note.deprecations",new Integer(ndepfiles),new Integer(ndeps));
}
 else {
env.error(0,"warn.note.1deprecation",file1,new Integer(ndeps));
}
}
 else {
if (ndepfiles > 1) {
env.error(0,"warn.note.deprecations.silent",new Integer(ndepfiles),new Integer(ndeps));
}
 else {
env.error(0,"warn.note.1deprecation.silent",file1,new Integer(ndeps));
}
}
}
env.flushErrors();
env.shutdown();
boolean status=true;
if (env.nerrors > 0) {
String msg="";
if (env.nerrors > 1) {
msg=getText("main.errors",env.nerrors);
}
 else {
msg=getText("main.1error");
}
if (env.nwarnings > 0) {
if (env.nwarnings > 1) {
msg+=", " + getText("main.warnings",env.nwarnings);
}
 else {
msg+=", " + getText("main.1warning");
}
}
output(msg);
if (exitStatus == EXIT_OK) {
exitStatus=EXIT_ERROR;
}
status=false;
}
 else {
if (env.nwarnings > 0) {
if (env.nwarnings > 1) {
output(getText("main.warnings",env.nwarnings));
}
 else {
output(getText("main.1warning"));
}
}
}
if (env.covdata()) {
Assembler CovAsm=new Assembler();
CovAsm.GenJCov(env);
}
if (env.verbose()) {
tm=System.currentTimeMillis() - tm;
output(getText("main.done_in",Long.toString(tm)));
}
return status;
}
/** 
 * Main program
 */
public static void main(String argv[]){
OutputStream out=System.err;
if (Boolean.getBoolean("javac.pipe.output")) {
out=System.out;
}
Main compiler=new Main(out,"javac");
System.exit(compiler.compile(argv) ? 0 : compiler.exitStatus);
}
}
