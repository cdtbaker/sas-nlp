package sun.rmi.rmic.newrmic;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.RootDoc;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import sun.rmi.rmic.newrmic.jrmp.JrmpGenerator;
import sun.tools.util.CommandLine;
/** 
 * The rmic front end.  This class contains the "main" method for rmic
 * command line invocation.
 * A Main instance contains the stream to output error messages and
 * other diagnostics to.
 * An rmic compilation batch (for example, one rmic command line
 * invocation) is executed by invoking the "compile" method of a Main
 * instance.
 * WARNING: The contents of this source file are not part of any
 * supported API.  Code that depends on them does so at its own risk:
 * they are subject to change or removal without notice.
 * NOTE: If and when there is a J2SE API for invoking SDK tools, this
 * class should be updated to support that API.
 * NOTE: This class is the front end for a "new" rmic implementation,
 * which uses javadoc and the doclet API for reading class files and
 * javac for compiling generated source files.  This implementation is
 * incomplete: it lacks any CORBA-based back end implementations, and
 * thus the command line options "-idl", "-iiop", and their related
 * options are not yet supported.  The front end for the "old",
 * oldjavac-based rmic implementation is sun.rmi.rmic.Main.
 * @author Peter Jones
 */
public class Main {
  /** 
 * guards "batchCount" 
 */
  private static final Object batchCountLock=new Object();
  /** 
 * number of batches run; used to generated batch IDs 
 */
  private static long batchCount=0;
  /** 
 * maps batch ID to batch data 
 */
  private static final Map<Long,Batch> batchTable=Collections.synchronizedMap(new HashMap<Long,Batch>());
  /** 
 * stream to output error messages and other diagnostics to 
 */
  private final PrintStream out;
  /** 
 * name of this program, to use in error messages 
 */
  private final String program;
  /** 
 * Command line entry point.
 */
  public static void main(  String[] args){
    Main rmic=new Main(System.err,"rmic");
    System.exit(rmic.compile(args) ? 0 : 1);
  }
  /** 
 * Creates a Main instance that writes output to the specified
 * stream.  The specified program name is used in error messages.
 */
  public Main(  OutputStream out,  String program){
    this.out=out instanceof PrintStream ? (PrintStream)out : new PrintStream(out);
    this.program=program;
  }
  /** 
 * Compiles a batch of input classes, as given by the specified
 * command line arguments.  Protocol-specific generators are
 * determined by the choice options on the command line.  Returns
 * true if successful, or false if an error occurred.
 * NOTE: This method is retained for transitional consistency with
 * previous implementations.
 */
  public boolean compile(  String[] args){
    long startTime=System.currentTimeMillis();
    long batchID;
synchronized (batchCountLock) {
      batchID=batchCount++;
    }
    Batch batch=parseArgs(args);
    if (batch == null) {
      return false;
    }
    boolean status;
    try {
      batchTable.put(batchID,batch);
      status=invokeJavadoc(batch,batchID);
    }
  finally {
      batchTable.remove(batchID);
    }
    if (batch.verbose) {
      long deltaTime=System.currentTimeMillis() - startTime;
      output(Resources.getText("rmic.done_in",Long.toString(deltaTime)));
    }
    return status;
  }
  /** 
 * Prints the specified string to the output stream of this Main
 * instance.
 */
  public void output(  String msg){
    out.println(msg);
  }
  /** 
 * Prints an error message to the output stream of this Main
 * instance.  The first argument is used as a key in rmic's
 * resource bundle, and the rest of the arguments are used as
 * arguments in the formatting of the resource string.
 */
  public void error(  String msg,  String... args){
    output(Resources.getText(msg,args));
  }
  /** 
 * Prints rmic's usage message to the output stream of this Main
 * instance.
 * This method is public so that it can be used by the "parseArgs"
 * methods of Generator implementations.
 */
  public void usage(){
    error("rmic.usage",program);
  }
  /** 
 * Processes rmic command line arguments.  Returns a Batch object
 * representing the command line arguments if successful, or null
 * if an error occurred.  Processed elements of the args array are
 * set to null.
 */
  private Batch parseArgs(  String[] args){
    Batch batch=new Batch();
    try {
      args=CommandLine.parse(args);
    }
 catch (    FileNotFoundException e) {
      error("rmic.cant.read",e.getMessage());
      return null;
    }
catch (    IOException e) {
      e.printStackTrace(out);
      return null;
    }
    for (int i=0; i < args.length; i++) {
      if (args[i] == null) {
        continue;
      }
 else       if (args[i].equals("-Xnew")) {
        args[i]=null;
      }
 else       if (args[i].equals("-show")) {
        error("rmic.option.unsupported",args[i]);
        usage();
        return null;
      }
 else       if (args[i].equals("-O")) {
        error("rmic.option.unsupported",args[i]);
        args[i]=null;
      }
 else       if (args[i].equals("-debug")) {
        error("rmic.option.unsupported",args[i]);
        args[i]=null;
      }
 else       if (args[i].equals("-depend")) {
        error("rmic.option.unsupported",args[i]);
        args[i]=null;
      }
 else       if (args[i].equals("-keep") || args[i].equals("-keepgenerated")) {
        batch.keepGenerated=true;
        args[i]=null;
      }
 else       if (args[i].equals("-g")) {
        batch.debug=true;
        args[i]=null;
      }
 else       if (args[i].equals("-nowarn")) {
        batch.noWarn=true;
        args[i]=null;
      }
 else       if (args[i].equals("-nowrite")) {
        batch.noWrite=true;
        args[i]=null;
      }
 else       if (args[i].equals("-verbose")) {
        batch.verbose=true;
        args[i]=null;
      }
 else       if (args[i].equals("-Xnocompile")) {
        batch.noCompile=true;
        batch.keepGenerated=true;
        args[i]=null;
      }
 else       if (args[i].equals("-bootclasspath")) {
        if ((i + 1) >= args.length) {
          error("rmic.option.requires.argument",args[i]);
          usage();
          return null;
        }
        if (batch.bootClassPath != null) {
          error("rmic.option.already.seen",args[i]);
          usage();
          return null;
        }
        args[i]=null;
        batch.bootClassPath=args[++i];
        assert batch.bootClassPath != null;
        args[i]=null;
      }
 else       if (args[i].equals("-extdirs")) {
        if ((i + 1) >= args.length) {
          error("rmic.option.requires.argument",args[i]);
          usage();
          return null;
        }
        if (batch.extDirs != null) {
          error("rmic.option.already.seen",args[i]);
          usage();
          return null;
        }
        args[i]=null;
        batch.extDirs=args[++i];
        assert batch.extDirs != null;
        args[i]=null;
      }
 else       if (args[i].equals("-classpath")) {
        if ((i + 1) >= args.length) {
          error("rmic.option.requires.argument",args[i]);
          usage();
          return null;
        }
        if (batch.classPath != null) {
          error("rmic.option.already.seen",args[i]);
          usage();
          return null;
        }
        args[i]=null;
        batch.classPath=args[++i];
        assert batch.classPath != null;
        args[i]=null;
      }
 else       if (args[i].equals("-d")) {
        if ((i + 1) >= args.length) {
          error("rmic.option.requires.argument",args[i]);
          usage();
          return null;
        }
        if (batch.destDir != null) {
          error("rmic.option.already.seen",args[i]);
          usage();
          return null;
        }
        args[i]=null;
        batch.destDir=new File(args[++i]);
        assert batch.destDir != null;
        args[i]=null;
        if (!batch.destDir.exists()) {
          error("rmic.no.such.directory",batch.destDir.getPath());
          usage();
          return null;
        }
      }
 else       if (args[i].equals("-v1.1") || args[i].equals("-vcompat") || args[i].equals("-v1.2")) {
        Generator gen=new JrmpGenerator();
        batch.generators.add(gen);
        if (!gen.parseArgs(args,this)) {
          return null;
        }
      }
 else       if (args[i].equalsIgnoreCase("-iiop")) {
        error("rmic.option.unimplemented",args[i]);
        return null;
      }
 else       if (args[i].equalsIgnoreCase("-idl")) {
        error("rmic.option.unimplemented",args[i]);
        return null;
      }
 else       if (args[i].equalsIgnoreCase("-xprint")) {
        error("rmic.option.unimplemented",args[i]);
        return null;
      }
    }
    for (int i=0; i < args.length; i++) {
      if (args[i] != null) {
        if (args[i].startsWith("-")) {
          error("rmic.no.such.option",args[i]);
          usage();
          return null;
        }
 else {
          batch.classes.add(args[i]);
        }
      }
    }
    if (batch.classes.isEmpty()) {
      usage();
      return null;
    }
    if (batch.generators.isEmpty()) {
      batch.generators.add(new JrmpGenerator());
    }
    return batch;
  }
  /** 
 * Doclet class entry point.
 */
  public static boolean start(  RootDoc rootDoc){
    long batchID=-1;
    for (    String[] option : rootDoc.options()) {
      if (option[0].equals("-batchID")) {
        try {
          batchID=Long.parseLong(option[1]);
        }
 catch (        NumberFormatException e) {
          throw new AssertionError(e);
        }
      }
    }
    Batch batch=batchTable.get(batchID);
    assert batch != null;
    BatchEnvironment env;
    try {
      Constructor<? extends BatchEnvironment> cons=batch.envClass.getConstructor(new Class[]{RootDoc.class});
      env=cons.newInstance(rootDoc);
    }
 catch (    NoSuchMethodException e) {
      throw new AssertionError(e);
    }
catch (    IllegalAccessException e) {
      throw new AssertionError(e);
    }
catch (    InstantiationException e) {
      throw new AssertionError(e);
    }
catch (    InvocationTargetException e) {
      throw new AssertionError(e);
    }
    env.setVerbose(batch.verbose);
    File destDir=batch.destDir;
    if (destDir == null) {
      destDir=new File(System.getProperty("user.dir"));
    }
    for (    String inputClassName : batch.classes) {
      ClassDoc inputClass=rootDoc.classNamed(inputClassName);
      try {
        for (        Generator gen : batch.generators) {
          gen.generate(env,inputClass,destDir);
        }
      }
 catch (      NullPointerException e) {
      }
    }
    boolean status=true;
    List<File> generatedFiles=env.generatedFiles();
    if (!batch.noCompile && !batch.noWrite && !generatedFiles.isEmpty()) {
      status=batch.enclosingMain().invokeJavac(batch,generatedFiles);
    }
    if (!batch.keepGenerated) {
      for (      File file : generatedFiles) {
        file.delete();
      }
    }
    return status;
  }
  /** 
 * Doclet class method that indicates that this doclet class
 * recognizes (only) the "-batchID" option on the javadoc command
 * line, and that the "-batchID" option comprises two arguments on
 * the javadoc command line.
 */
  public static int optionLength(  String option){
    if (option.equals("-batchID")) {
      return 2;
    }
 else {
      return 0;
    }
  }
  /** 
 * Runs the javadoc tool to invoke this class as a doclet, passing
 * command line options derived from the specified batch data and
 * indicating the specified batch ID.
 * NOTE: This method currently uses a J2SE-internal API to run
 * javadoc.  If and when there is a J2SE API for invoking SDK
 * tools, this method should be updated to use that API instead.
 */
  private boolean invokeJavadoc(  Batch batch,  long batchID){
    List<String> javadocArgs=new ArrayList<String>();
    javadocArgs.add("-private");
    javadocArgs.add("-Xclasses");
    if (batch.verbose) {
      javadocArgs.add("-verbose");
    }
    if (batch.bootClassPath != null) {
      javadocArgs.add("-bootclasspath");
      javadocArgs.add(batch.bootClassPath);
    }
    if (batch.extDirs != null) {
      javadocArgs.add("-extdirs");
      javadocArgs.add(batch.extDirs);
    }
    if (batch.classPath != null) {
      javadocArgs.add("-classpath");
      javadocArgs.add(batch.classPath);
    }
    javadocArgs.add("-batchID");
    javadocArgs.add(Long.toString(batchID));
    Set<String> classNames=new HashSet<String>();
    for (    Generator gen : batch.generators) {
      classNames.addAll(gen.bootstrapClassNames());
    }
    classNames.addAll(batch.classes);
    for (    String s : classNames) {
      javadocArgs.add(s);
    }
    int status=com.sun.tools.javadoc.Main.execute(program,new PrintWriter(out,true),new PrintWriter(out,true),new PrintWriter(out,true),this.getClass().getName(),javadocArgs.toArray(new String[javadocArgs.size()]));
    return status == 0;
  }
  /** 
 * Runs the javac tool to compile the specified source files,
 * passing command line options derived from the specified batch
 * data.
 * NOTE: This method currently uses a J2SE-internal API to run
 * javac.  If and when there is a J2SE API for invoking SDK tools,
 * this method should be updated to use that API instead.
 */
  private boolean invokeJavac(  Batch batch,  List<File> files){
    List<String> javacArgs=new ArrayList<String>();
    javacArgs.add("-nowarn");
    if (batch.debug) {
      javacArgs.add("-g");
    }
    if (batch.verbose) {
      javacArgs.add("-verbose");
    }
    if (batch.bootClassPath != null) {
      javacArgs.add("-bootclasspath");
      javacArgs.add(batch.bootClassPath);
    }
    if (batch.extDirs != null) {
      javacArgs.add("-extdirs");
      javacArgs.add(batch.extDirs);
    }
    if (batch.classPath != null) {
      javacArgs.add("-classpath");
      javacArgs.add(batch.classPath);
    }
    javacArgs.add("-source");
    javacArgs.add("1.3");
    javacArgs.add("-target");
    javacArgs.add("1.1");
    for (    File file : files) {
      javacArgs.add(file.getPath());
    }
    int status=com.sun.tools.javac.Main.compile(javacArgs.toArray(new String[javacArgs.size()]),new PrintWriter(out,true));
    return status == 0;
  }
  /** 
 * The data for an rmic compliation batch: the processed command
 * line arguments.
 */
private class Batch {
    boolean keepGenerated=false;
    boolean debug=false;
    boolean noWarn=false;
    boolean noWrite=false;
    boolean verbose=false;
    boolean noCompile=false;
    String bootClassPath=null;
    String extDirs=null;
    String classPath=null;
    File destDir=null;
    List<Generator> generators=new ArrayList<Generator>();
    Class<? extends BatchEnvironment> envClass=BatchEnvironment.class;
    List<String> classes=new ArrayList<String>();
    Batch(){
    }
    /** 
 * Returns the Main instance for this batch.
 */
    Main enclosingMain(){
      return Main.this;
    }
  }
}
