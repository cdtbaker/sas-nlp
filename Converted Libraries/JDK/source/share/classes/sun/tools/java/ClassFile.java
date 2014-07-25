package sun.tools.java;
import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.*;
/** 
 * This class is used to represent a file loaded from the class path, and
 * can either be a regular file or a zip file entry.
 * WARNING: The contents of this source file are not part of any
 * supported API.  Code that depends on them does so at its own risk:
 * they are subject to change or removal without notice.
 */
public class ClassFile {
  private File file;
  private ZipFile zipFile;
  private ZipEntry zipEntry;
  /** 
 * Constructor for instance representing a regular file
 */
  public ClassFile(  File file){
    this.file=file;
  }
  /** 
 * Constructor for instance representing a zip file entry
 */
  public ClassFile(  ZipFile zf,  ZipEntry ze){
    this.zipFile=zf;
    this.zipEntry=ze;
  }
  /** 
 * Returns true if this is zip file entry
 */
  public boolean isZipped(){
    return zipFile != null;
  }
  /** 
 * Returns input stream to either regular file or zip file entry
 */
  public InputStream getInputStream() throws IOException {
    if (file != null) {
      return new FileInputStream(file);
    }
 else {
      try {
        return zipFile.getInputStream(zipEntry);
      }
 catch (      ZipException e) {
        throw new IOException(e.getMessage());
      }
    }
  }
  /** 
 * Returns true if file exists.
 */
  public boolean exists(){
    return file != null ? file.exists() : true;
  }
  /** 
 * Returns true if this is a directory.
 */
  public boolean isDirectory(){
    return file != null ? file.isDirectory() : zipEntry.getName().endsWith("/");
  }
  /** 
 * Return last modification time
 */
  public long lastModified(){
    return file != null ? file.lastModified() : zipEntry.getTime();
  }
  /** 
 * Get file path. The path for a zip file entry will also include
 * the zip file name.
 */
  public String getPath(){
    if (file != null) {
      return file.getPath();
    }
 else {
      return zipFile.getName() + "(" + zipEntry.getName()+ ")";
    }
  }
  /** 
 * Get name of file entry excluding directory name
 */
  public String getName(){
    return file != null ? file.getName() : zipEntry.getName();
  }
  /** 
 * Get absolute name of file entry
 */
  public String getAbsoluteName(){
    String absoluteName;
    if (file != null) {
      try {
        absoluteName=file.getCanonicalPath();
      }
 catch (      IOException e) {
        absoluteName=file.getAbsolutePath();
      }
    }
 else {
      absoluteName=zipFile.getName() + "(" + zipEntry.getName()+ ")";
    }
    return absoluteName;
  }
  /** 
 * Get length of file
 */
  public long length(){
    return file != null ? file.length() : zipEntry.getSize();
  }
  public String toString(){
    return (file != null) ? file.toString() : zipEntry.toString();
  }
}
