package java.io;
import java.util.*;
import java.io.File;
/** 
 * This class holds a set of filenames to be deleted on VM exit through a shutdown hook.
 * A set is used both to prevent double-insertion of the same file as well as offer
 * quick removal.
 */
class DeleteOnExitHook {
  private static LinkedHashSet<String> files=new LinkedHashSet<>();
static {
    sun.misc.SharedSecrets.getJavaLangAccess().registerShutdownHook(2,true,new Runnable(){
      public void run(){
        runHooks();
      }
    }
);
  }
  private DeleteOnExitHook(){
  }
  static synchronized void add(  String file){
    if (files == null) {
      throw new IllegalStateException("Shutdown in progress");
    }
    files.add(file);
  }
  static void runHooks(){
    LinkedHashSet<String> theFiles;
synchronized (DeleteOnExitHook.class) {
      theFiles=files;
      files=null;
    }
    ArrayList<String> toBeDeleted=new ArrayList<>(theFiles);
    Collections.reverse(toBeDeleted);
    for (    String filename : toBeDeleted) {
      (new File(filename)).delete();
    }
  }
}
