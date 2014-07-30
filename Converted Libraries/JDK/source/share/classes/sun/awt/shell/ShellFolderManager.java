package sun.awt.shell;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.concurrent.Callable;
/** 
 * @author Michael Martak
 * @since 1.4
 */
class ShellFolderManager {
  /** 
 * Create a shell folder from a file.
 * Override to return machine-dependent behavior.
 */
  public ShellFolder createShellFolder(  File file) throws FileNotFoundException {
    return new DefaultShellFolder(null,file);
  }
  /** 
 * @param key a <code>String</code>
 * "fileChooserDefaultFolder":
 * Returns a <code>File</code> - the default shellfolder for a new filechooser
 * "roots":
 * Returns a <code>File[]</code> - containing the root(s) of the displayable hieararchy
 * "fileChooserComboBoxFolders":
 * Returns a <code>File[]</code> - an array of shellfolders representing the list to
 * show by default in the file chooser's combobox
 * "fileChooserShortcutPanelFolders":
 * Returns a <code>File[]</code> - an array of shellfolders representing well-known
 * folders, such as Desktop, Documents, History, Network, Home, etc.
 * This is used in the shortcut panel of the filechooser on Windows 2000
 * and Windows Me.
 * "fileChooserIcon <icon>":
 * Returns an <code>Image</code> - icon can be ListView, DetailsView, UpFolder, NewFolder or
 * ViewMenu (Windows only).
 * @return An Object matching the key string.
 */
  public Object get(  String key){
    if (key.equals("fileChooserDefaultFolder")) {
      File homeDir=new File(System.getProperty("user.home"));
      try {
        return createShellFolder(homeDir);
      }
 catch (      FileNotFoundException e) {
        return homeDir;
      }
    }
 else     if (key.equals("roots")) {
      return File.listRoots();
    }
 else     if (key.equals("fileChooserComboBoxFolders")) {
      return get("roots");
    }
 else     if (key.equals("fileChooserShortcutPanelFolders")) {
      return new File[]{(File)get("fileChooserDefaultFolder")};
    }
    return null;
  }
  /** 
 * Does <code>dir</code> represent a "computer" such as a node on the network, or
 * "My Computer" on the desktop.
 */
  public boolean isComputerNode(  File dir){
    return false;
  }
  public boolean isFileSystemRoot(  File dir){
    if (dir instanceof ShellFolder && !((ShellFolder)dir).isFileSystem()) {
      return false;
    }
    return (dir.getParentFile() == null);
  }
  protected ShellFolder.Invoker createInvoker(){
    return new DirectInvoker();
  }
private static class DirectInvoker implements ShellFolder.Invoker {
    public <T>T invoke(    Callable<T> task) throws Exception {
      return task.call();
    }
  }
}