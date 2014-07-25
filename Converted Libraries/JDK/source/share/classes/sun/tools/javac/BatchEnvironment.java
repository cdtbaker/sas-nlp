package sun.tools.javac;
import sun.tools.java.*;
import sun.tools.tree.Node;
import sun.tools.java.Package;
import java.util.*;
import java.io.*;
/** 
 * Main environment of the batch version of the Java compiler,
 * this needs more work.
 * WARNING: The contents of this source file are not part of any
 * supported API.  Code that depends on them does so at its own risk:
 * they are subject to change or removal without notice.
 */
@Deprecated public class BatchEnvironment extends Environment implements ErrorConsumer {
  /** 
 * The stream where error message are printed.
 */
  OutputStream out;
  /** 
 * The path we use for finding source files.
 */
  protected ClassPath sourcePath;
  /** 
 * The path we use for finding class (binary) files.
 */
  protected ClassPath binaryPath;
  /** 
 * A hashtable of resource contexts.
 */
  Hashtable packages=new Hashtable(31);
  /** 
 * The classes, in order of appearance.
 */
  Vector classesOrdered=new Vector();
  /** 
 * The classes, keyed by ClassDeclaration.
 */
  Hashtable classes=new Hashtable(351);
  /** 
 * flags
 */
  public int flags;
  /** 
 * Major and minor versions to use for generated class files.
 * Environments that extend BatchEnvironment (such as javadoc's
 * Env class) get the default values below.
 * javac itself may override these versions with values determined
 * from the command line "-target" option.
 */
  public short majorVersion=JAVA_DEFAULT_VERSION;
  public short minorVersion=JAVA_DEFAULT_MINOR_VERSION;
  /** 
 * coverage data file
 */
  public File covFile;
  /** 
 * The number of errors and warnings
 */
  public int nerrors;
  public int nwarnings;
  public int ndeprecations;
  /** 
 * A list of files containing deprecation warnings.
 */
  Vector deprecationFiles=new Vector();
  /** 
 * writes out error messages
 */
  ErrorConsumer errorConsumer;
  /** 
 * Old constructors -- these constructors build a BatchEnvironment
 * with an old-style class path.
 */
  public BatchEnvironment(  ClassPath path){
    this(System.out,path);
  }
  public BatchEnvironment(  OutputStream out,  ClassPath path){
    this(out,path,(ErrorConsumer)null);
  }
  public BatchEnvironment(  OutputStream out,  ClassPath path,  ErrorConsumer errorConsumer){
    this(out,path,path,errorConsumer);
  }
  /** 
 * New constructors -- these constructors build a BatchEnvironment
 * with a source path and a binary path.
 */
  public BatchEnvironment(  ClassPath sourcePath,  ClassPath binaryPath){
    this(System.out,sourcePath,binaryPath);
  }
  public BatchEnvironment(  OutputStream out,  ClassPath sourcePath,  ClassPath binaryPath){
    this(out,sourcePath,binaryPath,(ErrorConsumer)null);
  }
  public BatchEnvironment(  OutputStream out,  ClassPath sourcePath,  ClassPath binaryPath,  ErrorConsumer errorConsumer){
    this.out=out;
    this.sourcePath=sourcePath;
    this.binaryPath=binaryPath;
    this.errorConsumer=(errorConsumer == null) ? this : errorConsumer;
  }
  /** 
 * Factory
 */
  static BatchEnvironment create(  OutputStream out,  String srcPathString,  String classPathString,  String sysClassPathString,  String extDirsString){
    ClassPath[] classPaths=classPaths(srcPathString,classPathString,sysClassPathString,extDirsString);
    return new BatchEnvironment(out,classPaths[0],classPaths[1]);
  }
  protected static ClassPath[] classPaths(  String srcPathString,  String classPathString,  String sysClassPathString,  String extDirsString){
    ClassPath sourcePath;
    ClassPath binaryPath;
    StringBuffer binaryPathBuffer=new StringBuffer();
    if (classPathString == null) {
      classPathString=System.getProperty("env.class.path");
      if (classPathString == null) {
        classPathString=".";
      }
    }
    if (srcPathString == null) {
      srcPathString=classPathString;
    }
    if (sysClassPathString == null) {
      sysClassPathString=System.getProperty("sun.boot.class.path");
      if (sysClassPathString == null) {
        sysClassPathString=classPathString;
      }
    }
    appendPath(binaryPathBuffer,sysClassPathString);
    if (extDirsString == null) {
      extDirsString=System.getProperty("java.ext.dirs");
    }
    if (extDirsString != null) {
      StringTokenizer st=new StringTokenizer(extDirsString,File.pathSeparator);
      while (st.hasMoreTokens()) {
        String dirName=st.nextToken();
        File dir=new File(dirName);
        if (!dirName.endsWith(File.separator)) {
          dirName+=File.separator;
        }
        if (dir.isDirectory()) {
          String[] files=dir.list();
          for (int i=0; i < files.length; ++i) {
            String name=files[i];
            if (name.endsWith(".jar")) {
              appendPath(binaryPathBuffer,dirName + name);
            }
          }
        }
      }
    }
    appendPath(binaryPathBuffer,classPathString);
    sourcePath=new ClassPath(srcPathString);
    binaryPath=new ClassPath(binaryPathBuffer.toString());
    return new ClassPath[]{sourcePath,binaryPath};
  }
  private static void appendPath(  StringBuffer buf,  String str){
    if (str.length() > 0) {
      if (buf.length() > 0) {
        buf.append(File.pathSeparator);
      }
      buf.append(str);
    }
  }
  /** 
 * Return flags
 */
  public int getFlags(){
    return flags;
  }
  /** 
 * Return major version to use for generated class files
 */
  public short getMajorVersion(){
    return majorVersion;
  }
  /** 
 * Return minor version to use for generated class files
 */
  public short getMinorVersion(){
    return minorVersion;
  }
  /** 
 * Return coverage data file
 */
  public File getcovFile(){
    return covFile;
  }
  /** 
 * Return an enumeration of all the currently defined classes
 * in order of appearance to getClassDeclaration().
 */
  public Enumeration getClasses(){
    return classesOrdered.elements();
  }
  /** 
 * A set of Identifiers for all packages exempt from the "exists"
 * check in Imports#resolve().  These are the current packages for
 * all classes being compiled as of the first call to isExemptPackage.
 */
  private Set exemptPackages;
  /** 
 * Tells whether an Identifier refers to a package which should be
 * exempt from the "exists" check in Imports#resolve().
 */
  public boolean isExemptPackage(  Identifier id){
    if (exemptPackages == null) {
      setExemptPackages();
    }
    return exemptPackages.contains(id);
  }
  /** 
 * Set the set of packages which are exempt from the exists check
 * in Imports#resolve().
 */
  private void setExemptPackages(){
    exemptPackages=new HashSet(101);
    for (Enumeration e=getClasses(); e.hasMoreElements(); ) {
      ClassDeclaration c=(ClassDeclaration)e.nextElement();
      if (c.getStatus() == CS_PARSED) {
        SourceClass def=(SourceClass)c.getClassDefinition();
        if (def.isLocal())         continue;
        Identifier pkg=def.getImports().getCurrentPackage();
        while (pkg != idNull && exemptPackages.add(pkg)) {
          pkg=pkg.getQualifier();
        }
      }
    }
    if (!exemptPackages.contains(idJavaLang)) {
      exemptPackages.add(idJavaLang);
      try {
        if (!getPackage(idJavaLang).exists()) {
          error(0,"package.not.found.strong",idJavaLang);
          return;
        }
      }
 catch (      IOException ee) {
        error(0,"io.exception.package",idJavaLang);
      }
    }
  }
  /** 
 * Get a class, given the fully qualified class name
 */
  public ClassDeclaration getClassDeclaration(  Identifier nm){
    return getClassDeclaration(Type.tClass(nm));
  }
  public ClassDeclaration getClassDeclaration(  Type t){
    ClassDeclaration c=(ClassDeclaration)classes.get(t);
    if (c == null) {
      classes.put(t,c=new ClassDeclaration(t.getClassName()));
      classesOrdered.addElement(c);
    }
    return c;
  }
  /** 
 * Check if a class exists
 * Applies only to package members (non-nested classes).
 */
  public boolean classExists(  Identifier nm){
    if (nm.isInner()) {
      nm=nm.getTopName();
    }
    Type t=Type.tClass(nm);
    try {
      ClassDeclaration c=(ClassDeclaration)classes.get(t);
      return (c != null) ? c.getName().equals(nm) : getPackage(nm.getQualifier()).classExists(nm.getName());
    }
 catch (    IOException e) {
      return true;
    }
  }
  /** 
 * Get the package path for a package
 */
  public Package getPackage(  Identifier pkg) throws IOException {
    Package p=(Package)packages.get(pkg);
    if (p == null) {
      packages.put(pkg,p=new Package(sourcePath,binaryPath,pkg));
    }
    return p;
  }
  /** 
 * Parse a source file
 */
  public void parseFile(  ClassFile file) throws FileNotFoundException {
    long tm=System.currentTimeMillis();
    InputStream input;
    BatchParser p;
    if (tracing)     dtEnter("parseFile: PARSING SOURCE " + file);
    Environment env=new Environment(this,file);
    try {
      input=file.getInputStream();
      env.setCharacterEncoding(getCharacterEncoding());
      p=new BatchParser(env,input);
    }
 catch (    IOException ex) {
      if (tracing)       dtEvent("parseFile: IO EXCEPTION " + file);
      throw new FileNotFoundException();
    }
    try {
      p.parseFile();
    }
 catch (    Exception e) {
      throw new CompilerError(e);
    }
    try {
      input.close();
    }
 catch (    IOException ex) {
    }
    if (verbose()) {
      tm=System.currentTimeMillis() - tm;
      output(Main.getText("benv.parsed_in",file.getPath(),Long.toString(tm)));
    }
    if (p.classes.size() == 0) {
      p.imports.resolve(env);
    }
 else {
      Enumeration e=p.classes.elements();
      ClassDefinition first=(ClassDefinition)e.nextElement();
      if (first.isInnerClass()) {
        throw new CompilerError("BatchEnvironment, first is inner");
      }
      ClassDefinition current=first;
      ClassDefinition next;
      while (e.hasMoreElements()) {
        next=(ClassDefinition)e.nextElement();
        if (next.isInnerClass()) {
          continue;
        }
        current.addDependency(next.getClassDeclaration());
        next.addDependency(current.getClassDeclaration());
        current=next;
      }
      if (current != first) {
        current.addDependency(first.getClassDeclaration());
        first.addDependency(current.getClassDeclaration());
      }
    }
    if (tracing)     dtExit("parseFile: SOURCE PARSED " + file);
  }
  /** 
 * Load a binary file
 */
  BinaryClass loadFile(  ClassFile file) throws IOException {
    long tm=System.currentTimeMillis();
    InputStream input=file.getInputStream();
    BinaryClass c=null;
    if (tracing)     dtEnter("loadFile: LOADING CLASSFILE " + file);
    try {
      DataInputStream is=new DataInputStream(new BufferedInputStream(input));
      c=BinaryClass.load(new Environment(this,file),is,loadFileFlags());
    }
 catch (    ClassFormatError e) {
      error(0,"class.format",file.getPath(),e.getMessage());
      if (tracing)       dtExit("loadFile: CLASS FORMAT ERROR " + file);
      return null;
    }
catch (    java.io.EOFException e) {
      error(0,"truncated.class",file.getPath());
      return null;
    }
    input.close();
    if (verbose()) {
      tm=System.currentTimeMillis() - tm;
      output(Main.getText("benv.loaded_in",file.getPath(),Long.toString(tm)));
    }
    if (tracing)     dtExit("loadFile: CLASSFILE LOADED " + file);
    return c;
  }
  /** 
 * Default flags for loadFile.  Subclasses may override this.
 */
  int loadFileFlags(){
    return 0;
  }
  /** 
 * Load a binary class
 */
  boolean needsCompilation(  Hashtable check,  ClassDeclaration c){
switch (c.getStatus()) {
case CS_UNDEFINED:
      if (tracing)       dtEnter("needsCompilation: UNDEFINED " + c.getName());
    loadDefinition(c);
  return needsCompilation(check,c);
case CS_UNDECIDED:
if (tracing) dtEnter("needsCompilation: UNDECIDED " + c.getName());
if (check.get(c) == null) {
check.put(c,c);
BinaryClass bin=(BinaryClass)c.getClassDefinition();
for (Enumeration e=bin.getDependencies(); e.hasMoreElements(); ) {
  ClassDeclaration dep=(ClassDeclaration)e.nextElement();
  if (needsCompilation(check,dep)) {
    c.setDefinition(bin,CS_SOURCE);
    if (tracing)     dtExit("needsCompilation: YES (source) " + c.getName());
    return true;
  }
}
}
if (tracing) dtExit("needsCompilation: NO (undecided) " + c.getName());
return false;
case CS_BINARY:
if (tracing) {
dtEnter("needsCompilation: BINARY " + c.getName());
dtExit("needsCompilation: NO (binary) " + c.getName());
}
return false;
}
if (tracing) dtExit("needsCompilation: YES " + c.getName());
return true;
}
/** 
 * Load the definition of a class
 * or at least determine how to load it.
 * The caller must repeat calls to this method
 * until it the state converges to CS_BINARY, CS_PARSED, or the like..
 * @see ClassDeclaration#getClassDefinition
 */
public void loadDefinition(ClassDeclaration c){
if (tracing) dtEnter("loadDefinition: ENTER " + c.getName() + ", status "+ c.getStatus());
switch (c.getStatus()) {
case CS_UNDEFINED:
{
if (tracing) dtEvent("loadDefinition: STATUS IS UNDEFINED");
Identifier nm=c.getName();
Package pkg;
try {
pkg=getPackage(nm.getQualifier());
}
 catch (IOException e) {
c.setDefinition(null,CS_NOTFOUND);
error(0,"io.exception",c);
if (tracing) dtExit("loadDefinition: IO EXCEPTION (package)");
return;
}
ClassFile binfile=pkg.getBinaryFile(nm.getName());
if (binfile == null) {
c.setDefinition(null,CS_SOURCE);
if (tracing) dtExit("loadDefinition: MUST BE SOURCE (no binary) " + c.getName());
return;
}
ClassFile srcfile=pkg.getSourceFile(nm.getName());
if (srcfile == null) {
if (tracing) dtEvent("loadDefinition: NO SOURCE " + c.getName());
BinaryClass bc=null;
try {
bc=loadFile(binfile);
}
 catch (IOException e) {
c.setDefinition(null,CS_NOTFOUND);
error(0,"io.exception",binfile);
if (tracing) dtExit("loadDefinition: IO EXCEPTION (binary)");
return;
}
if ((bc != null) && !bc.getName().equals(nm)) {
error(0,"wrong.class",binfile.getPath(),c,bc);
bc=null;
if (tracing) dtEvent("loadDefinition: WRONG CLASS (binary)");
}
if (bc == null) {
c.setDefinition(null,CS_NOTFOUND);
if (tracing) dtExit("loadDefinition: NOT FOUND (source or binary)");
return;
}
if (bc.getSource() != null) {
srcfile=new ClassFile(new File((String)bc.getSource()));
srcfile=pkg.getSourceFile(srcfile.getName());
if ((srcfile != null) && srcfile.exists()) {
if (tracing) dtEvent("loadDefinition: FILENAME IN BINARY " + srcfile);
if (srcfile.lastModified() > binfile.lastModified()) {
c.setDefinition(bc,CS_SOURCE);
if (tracing) dtEvent("loadDefinition: SOURCE IS NEWER " + srcfile);
bc.loadNested(this);
if (tracing) dtExit("loadDefinition: MUST BE SOURCE " + c.getName());
return;
}
if (dependencies()) {
c.setDefinition(bc,CS_UNDECIDED);
if (tracing) dtEvent("loadDefinition: UNDECIDED " + c.getName());
}
 else {
c.setDefinition(bc,CS_BINARY);
if (tracing) dtEvent("loadDefinition: MUST BE BINARY " + c.getName());
}
bc.loadNested(this);
if (tracing) dtExit("loadDefinition: EXIT " + c.getName() + ", status "+ c.getStatus());
return;
}
}
c.setDefinition(bc,CS_BINARY);
if (tracing) dtEvent("loadDefinition: MUST BE BINARY (no source) " + c.getName());
bc.loadNested(this);
if (tracing) dtExit("loadDefinition: EXIT " + c.getName() + ", status "+ c.getStatus());
return;
}
BinaryClass bc=null;
try {
if (srcfile.lastModified() > binfile.lastModified()) {
c.setDefinition(null,CS_SOURCE);
if (tracing) dtEvent("loadDefinition: MUST BE SOURCE (younger than binary) " + c.getName());
return;
}
bc=loadFile(binfile);
}
 catch (IOException e) {
error(0,"io.exception",binfile);
if (tracing) dtEvent("loadDefinition: IO EXCEPTION (binary)");
}
if ((bc != null) && !bc.getName().equals(nm)) {
error(0,"wrong.class",binfile.getPath(),c,bc);
bc=null;
if (tracing) dtEvent("loadDefinition: WRONG CLASS (binary)");
}
if (bc != null) {
Identifier name=bc.getName();
if (name.equals(c.getName())) {
if (dependencies()) {
c.setDefinition(bc,CS_UNDECIDED);
if (tracing) dtEvent("loadDefinition: UNDECIDED " + name);
}
 else {
c.setDefinition(bc,CS_BINARY);
if (tracing) dtEvent("loadDefinition: MUST BE BINARY " + name);
}
}
 else {
c.setDefinition(null,CS_NOTFOUND);
if (tracing) dtEvent("loadDefinition: NOT FOUND (source or binary)");
if (dependencies()) {
getClassDeclaration(name).setDefinition(bc,CS_UNDECIDED);
if (tracing) dtEvent("loadDefinition: UNDECIDED " + name);
}
 else {
getClassDeclaration(name).setDefinition(bc,CS_BINARY);
if (tracing) dtEvent("loadDefinition: MUST BE BINARY " + name);
}
}
}
 else {
c.setDefinition(null,CS_NOTFOUND);
if (tracing) dtEvent("loadDefinition: NOT FOUND (source or binary)");
}
if (bc != null && bc == c.getClassDefinition()) bc.loadNested(this);
if (tracing) dtExit("loadDefinition: EXIT " + c.getName() + ", status "+ c.getStatus());
return;
}
case CS_UNDECIDED:
{
if (tracing) dtEvent("loadDefinition: STATUS IS UNDECIDED");
Hashtable tab=new Hashtable();
if (!needsCompilation(tab,c)) {
for (Enumeration e=tab.keys(); e.hasMoreElements(); ) {
ClassDeclaration dep=(ClassDeclaration)e.nextElement();
if (dep.getStatus() == CS_UNDECIDED) {
dep.setDefinition(dep.getClassDefinition(),CS_BINARY);
if (tracing) dtEvent("loadDefinition: MUST BE BINARY " + dep);
}
}
}
if (tracing) dtExit("loadDefinition: EXIT " + c.getName() + ", status "+ c.getStatus());
return;
}
case CS_SOURCE:
{
if (tracing) dtEvent("loadDefinition: STATUS IS SOURCE");
ClassFile srcfile=null;
Package pkg=null;
if (c.getClassDefinition() != null) {
try {
pkg=getPackage(c.getName().getQualifier());
srcfile=pkg.getSourceFile((String)c.getClassDefinition().getSource());
}
 catch (IOException e) {
error(0,"io.exception",c);
if (tracing) dtEvent("loadDefinition: IO EXCEPTION (package)");
}
if (srcfile == null) {
String fn=(String)c.getClassDefinition().getSource();
srcfile=new ClassFile(new File(fn));
}
}
 else {
Identifier nm=c.getName();
try {
pkg=getPackage(nm.getQualifier());
srcfile=pkg.getSourceFile(nm.getName());
}
 catch (IOException e) {
error(0,"io.exception",c);
if (tracing) dtEvent("loadDefinition: IO EXCEPTION (package)");
}
if (srcfile == null) {
c.setDefinition(null,CS_NOTFOUND);
if (tracing) dtExit("loadDefinition: SOURCE NOT FOUND " + c.getName() + ", status "+ c.getStatus());
return;
}
}
try {
parseFile(srcfile);
}
 catch (FileNotFoundException e) {
error(0,"io.exception",srcfile);
if (tracing) dtEvent("loadDefinition: IO EXCEPTION (source)");
}
if ((c.getClassDefinition() == null) || (c.getStatus() == CS_SOURCE)) {
error(0,"wrong.source",srcfile.getPath(),c,pkg);
c.setDefinition(null,CS_NOTFOUND);
if (tracing) dtEvent("loadDefinition: WRONG CLASS (source) " + c.getName());
}
if (tracing) dtExit("loadDefinition: EXIT " + c.getName() + ", status "+ c.getStatus());
return;
}
}
if (tracing) dtExit("loadDefinition: EXIT " + c.getName() + ", status "+ c.getStatus());
}
/** 
 * Create a new class.
 */
public ClassDefinition makeClassDefinition(Environment toplevelEnv,long where,IdentifierToken name,String doc,int modifiers,IdentifierToken superClass,IdentifierToken interfaces[],ClassDefinition outerClass){
Identifier nm=name.getName();
long nmpos=name.getWhere();
Identifier pkgNm;
String mangledName=null;
ClassDefinition localContextClass=null;
Identifier localName=null;
if (nm.isQualified() || nm.isInner()) {
pkgNm=nm;
}
 else if ((modifiers & (M_LOCAL | M_ANONYMOUS)) != 0) {
localContextClass=outerClass.getTopClass();
for (int i=1; ; i++) {
mangledName=i + (nm.equals(idNull) ? "" : SIG_INNERCLASS + nm);
if (localContextClass.getLocalClass(mangledName) == null) {
break;
}
}
Identifier outerNm=localContextClass.getName();
pkgNm=Identifier.lookupInner(outerNm,Identifier.lookup(mangledName));
if ((modifiers & M_ANONYMOUS) != 0) {
localName=idNull;
}
 else {
localName=nm;
}
}
 else if (outerClass != null) {
pkgNm=Identifier.lookupInner(outerClass.getName(),nm);
}
 else {
pkgNm=nm;
}
ClassDeclaration c=toplevelEnv.getClassDeclaration(pkgNm);
if (c.isDefined()) {
toplevelEnv.error(nmpos,"class.multidef",c.getName(),c.getClassDefinition().getSource());
c=new ClassDeclaration(pkgNm);
}
if (superClass == null && !pkgNm.equals(idJavaLangObject)) {
superClass=new IdentifierToken(idJavaLangObject);
}
ClassDefinition sourceClass=new SourceClass(toplevelEnv,where,c,doc,modifiers,superClass,interfaces,(SourceClass)outerClass,localName);
if (outerClass != null) {
outerClass.addMember(toplevelEnv,new SourceMember(sourceClass));
if ((modifiers & (M_LOCAL | M_ANONYMOUS)) != 0) {
localContextClass.addLocalClass(sourceClass,mangledName);
}
}
return sourceClass;
}
/** 
 * Create a new field.
 */
public MemberDefinition makeMemberDefinition(Environment origEnv,long where,ClassDefinition clazz,String doc,int modifiers,Type type,Identifier name,IdentifierToken argNames[],IdentifierToken expIds[],Object value){
if (tracing) dtEvent("makeMemberDefinition: " + name + " IN "+ clazz);
Vector v=null;
if (argNames != null) {
v=new Vector(argNames.length);
for (int i=0; i < argNames.length; i++) {
v.addElement(argNames[i]);
}
}
SourceMember f=new SourceMember(where,clazz,doc,modifiers,type,name,v,expIds,(Node)value);
clazz.addMember(origEnv,f);
return f;
}
/** 
 * Release resources in classpath.
 */
public void shutdown(){
try {
if (sourcePath != null) {
sourcePath.close();
}
if (binaryPath != null && binaryPath != sourcePath) {
binaryPath.close();
}
}
 catch (IOException ee) {
output(Main.getText("benv.failed_to_close_class_path",ee.toString()));
}
sourcePath=null;
binaryPath=null;
super.shutdown();
}
/** 
 * Error String
 */
public String errorString(String err,Object arg1,Object arg2,Object arg3){
String key=null;
if (err.startsWith("warn.")) key="javac.err." + err.substring(5);
 else key="javac.err." + err;
return Main.getText(key,arg1 != null ? arg1.toString() : null,arg2 != null ? arg2.toString() : null,arg3 != null ? arg3.toString() : null);
}
/** 
 * The filename where the last errors have occurred
 */
String errorFileName;
/** 
 * List of outstanding error messages
 */
ErrorMessage errors;
/** 
 * Insert an error message in the list of outstanding error messages.
 * The list is sorted on input position and contains no duplicates.
 * The return value indicates whether or not the message was
 * actually inserted.
 * The method flushErrors() used to check for duplicate error messages.
 * It would only detect duplicates if they were contiguous.  Removing
 * non-contiguous duplicate error messages is slightly less complicated
 * at insertion time, so the functionality was moved here.  This also
 * saves a miniscule number of allocations.
 */
protected boolean insertError(long where,String message){
if (errors == null || errors.where > where) {
ErrorMessage newMsg=new ErrorMessage(where,message);
newMsg.next=errors;
errors=newMsg;
}
 else if (errors.where == where && errors.message.equals(message)) {
return false;
}
 else {
ErrorMessage current=errors;
ErrorMessage next;
while ((next=current.next) != null && next.where < where) {
current=next;
}
while ((next=current.next) != null && next.where == where) {
if (next.message.equals(message)) {
return false;
}
current=next;
}
ErrorMessage newMsg=new ErrorMessage(where,message);
newMsg.next=current.next;
current.next=newMsg;
}
return true;
}
private int errorsPushed;
/** 
 * Maximum number of errors to print.
 */
public int errorLimit=100;
private boolean hitErrorLimit;
/** 
 * Flush outstanding errors
 */
public void pushError(String errorFileName,int line,String message,String referenceText,String referenceTextPointer){
int limit=errorLimit + nwarnings;
if (++errorsPushed >= limit && errorLimit >= 0) {
if (!hitErrorLimit) {
hitErrorLimit=true;
output(errorString("too.many.errors",new Integer(errorLimit),null,null));
}
return;
}
if (errorFileName.endsWith(".java")) {
output(errorFileName + ":" + line+ ": "+ message);
output(referenceText);
output(referenceTextPointer);
}
 else {
output(errorFileName + ": " + message);
}
}
public void flushErrors(){
if (errors == null) {
return;
}
boolean inputAvail=false;
char data[]=null;
int dataLength=0;
try {
FileInputStream in=new FileInputStream(errorFileName);
data=new char[in.available()];
InputStreamReader reader=(getCharacterEncoding() != null ? new InputStreamReader(in,getCharacterEncoding()) : new InputStreamReader(in));
dataLength=reader.read(data);
reader.close();
inputAvail=true;
}
 catch (IOException e) {
}
for (ErrorMessage msg=errors; msg != null; msg=msg.next) {
int ln=(int)(msg.where >>> WHEREOFFSETBITS);
int off=(int)(msg.where & ((1L << WHEREOFFSETBITS) - 1));
if (off > dataLength) off=dataLength;
String referenceString="";
String markerString="";
if (inputAvail) {
int i, j;
for (i=off; (i > 0) && (data[i - 1] != '\n') && (data[i - 1] != '\r'); i--) ;
for (j=off; (j < dataLength) && (data[j] != '\n') && (data[j] != '\r'); j++) ;
referenceString=new String(data,i,j - i);
char strdata[]=new char[(off - i) + 1];
for (j=i; j < off; j++) {
strdata[j - i]=(data[j] == '\t') ? '\t' : ' ';
}
strdata[off - i]='^';
markerString=new String(strdata);
}
errorConsumer.pushError(errorFileName,ln,msg.message,referenceString,markerString);
}
errors=null;
}
/** 
 * Report error
 */
public void reportError(Object src,long where,String err,String msg){
if (src == null) {
if (errorFileName != null) {
flushErrors();
errorFileName=null;
}
if (err.startsWith("warn.")) {
if (warnings()) {
nwarnings++;
output(msg);
}
return;
}
output("error: " + msg);
nerrors++;
flags|=F_ERRORSREPORTED;
}
 else if (src instanceof String) {
String fileName=(String)src;
if (!fileName.equals(errorFileName)) {
flushErrors();
errorFileName=fileName;
}
if (err.startsWith("warn.")) {
if (err.indexOf("is.deprecated") >= 0) {
if (!deprecationFiles.contains(src)) {
deprecationFiles.addElement(src);
}
if (deprecation()) {
if (insertError(where,msg)) {
ndeprecations++;
}
}
 else {
ndeprecations++;
}
}
 else {
if (warnings()) {
if (insertError(where,msg)) {
nwarnings++;
}
}
 else {
nwarnings++;
}
}
}
 else {
if (insertError(where,msg)) {
nerrors++;
flags|=F_ERRORSREPORTED;
}
}
}
 else if (src instanceof ClassFile) {
reportError(((ClassFile)src).getPath(),where,err,msg);
}
 else if (src instanceof Identifier) {
reportError(src.toString(),where,err,msg);
}
 else if (src instanceof ClassDeclaration) {
try {
reportError(((ClassDeclaration)src).getClassDefinition(this),where,err,msg);
}
 catch (ClassNotFound e) {
reportError(((ClassDeclaration)src).getName(),where,err,msg);
}
}
 else if (src instanceof ClassDefinition) {
ClassDefinition c=(ClassDefinition)src;
if (!err.startsWith("warn.")) {
c.setError();
}
reportError(c.getSource(),where,err,msg);
}
 else if (src instanceof MemberDefinition) {
reportError(((MemberDefinition)src).getClassDeclaration(),where,err,msg);
}
 else {
output(src + ":error=" + err+ ":"+ msg);
}
}
/** 
 * Issue an error
 */
public void error(Object source,long where,String err,Object arg1,Object arg2,Object arg3){
if (errorsPushed >= errorLimit + nwarnings) {
return;
}
if (System.getProperty("javac.dump.stack") != null) {
output("javac.err." + err + ": "+ errorString(err,arg1,arg2,arg3));
new Exception("Stack trace").printStackTrace(new PrintStream(out));
}
reportError(source,where,err,errorString(err,arg1,arg2,arg3));
}
/** 
 * Output a string. This can either be an error message or something
 * for debugging.
 */
public void output(String msg){
PrintStream out=this.out instanceof PrintStream ? (PrintStream)this.out : new PrintStream(this.out,true);
out.println(msg);
}
}
