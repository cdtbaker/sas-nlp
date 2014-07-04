package sun.rmi.rmic;
import java.io.File;
import sun.tools.java.Identifier;
/** 
 * Util provides static utility methods used by other rmic classes.
 * WARNING: The contents of this source file are not part of any
 * supported API.  Code that depends on them does so at its own risk:
 * they are subject to change or removal without notice.
 * @author Bryan Atsatt
 */
public class Util implements sun.rmi.rmic.Constants {
  /** 
 * Return the directory that should be used for output for a given
 * class.
 * @param theClass The fully qualified name of the class.
 * @param rootDir The directory to use as the root of the
 * package heirarchy.  May be null, in which case the current
 * working directory is used as the root.
 */
  public static File getOutputDirectoryFor(  Identifier theClass,  File rootDir,  BatchEnvironment env){
    File outputDir=null;
    String className=theClass.getFlatName().toString().replace('.',SIGC_INNERCLASS);
    String qualifiedClassName=className;
    String packagePath=null;
    String packageName=theClass.getQualifier().toString();
    if (packageName.length() > 0) {
      qualifiedClassName=packageName + "." + className;
      packagePath=packageName.replace('.',File.separatorChar);
    }
    if (rootDir != null) {
      if (packagePath != null) {
        outputDir=new File(rootDir,packagePath);
        ensureDirectory(outputDir,env);
      }
 else {
        outputDir=rootDir;
      }
    }
 else {
      String workingDirPath=System.getProperty("user.dir");
      File workingDir=new File(workingDirPath);
      if (packagePath == null) {
        outputDir=workingDir;
      }
 else {
        outputDir=new File(workingDir,packagePath);
        ensureDirectory(outputDir,env);
      }
    }
    return outputDir;
  }
  private static void ensureDirectory(  File dir,  BatchEnvironment env){
    if (!dir.exists()) {
      dir.mkdirs();
      if (!dir.exists()) {
        env.error(0,"rmic.cannot.create.dir",dir.getAbsolutePath());
        throw new InternalError();
      }
    }
  }
}
