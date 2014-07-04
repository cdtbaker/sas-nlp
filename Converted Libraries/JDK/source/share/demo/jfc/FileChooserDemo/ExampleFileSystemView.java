import java.io.File;
import java.io.IOException;
import javax.swing.filechooser.FileSystemView;
/** 
 * This is a simple example that uses the FileSystemView class.
 * You can provide a superclass of the FileSystemView class with your own functionality.
 * @author Pavel Porvatov
 */
public class ExampleFileSystemView extends FileSystemView {
  /** 
 * Creates a new folder with the default name "New folder". This method is invoked
 * when the user presses the "New folder" button.
 */
  public File createNewFolder(  File containingDir) throws IOException {
    File result=new File(containingDir,"New folder");
    if (result.exists()) {
      throw new IOException("Directory 'New folder' exists");
    }
    if (!result.mkdir()) {
      throw new IOException("Cannot create directory");
    }
    return result;
  }
  /** 
 * Returns a list which appears in a drop-down list of the FileChooser component.
 * In this implementation only the home directory is returned.
 */
  @Override public File[] getRoots(){
    return new File[]{getHomeDirectory()};
  }
  /** 
 * Returns a string that represents a directory or a file in the FileChooser component.
 * A string with all upper case letters is returned for a directory.
 * A string with all lower case letters is returned for a file.
 */
  @Override public String getSystemDisplayName(  File f){
    String displayName=super.getSystemDisplayName(f);
    return f.isDirectory() ? displayName.toUpperCase() : displayName.toLowerCase();
  }
}
