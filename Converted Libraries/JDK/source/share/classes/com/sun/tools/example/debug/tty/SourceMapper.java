package com.sun.tools.example.debug.tty;
import com.sun.jdi.Location;
import com.sun.jdi.AbsentInformationException;
import java.util.List;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.io.*;
class SourceMapper {
  private final String[] dirs;
  SourceMapper(  List<String> sourcepath){
    List<String> dirList=new ArrayList<String>();
    for (    String element : sourcepath) {
      if (!(element.endsWith(".jar") || element.endsWith(".zip"))) {
        dirList.add(element);
      }
    }
    dirs=dirList.toArray(new String[0]);
  }
  SourceMapper(  String sourcepath){
    StringTokenizer st=new StringTokenizer(sourcepath,File.pathSeparator);
    List<String> dirList=new ArrayList<String>();
    while (st.hasMoreTokens()) {
      String s=st.nextToken();
      if (!(s.endsWith(".jar") || s.endsWith(".zip"))) {
        dirList.add(s);
      }
    }
    dirs=dirList.toArray(new String[0]);
  }
  String getSourcePath(){
    int i=0;
    StringBuffer sp;
    if (dirs.length < 1) {
      return "";
    }
 else {
      sp=new StringBuffer(dirs[i++]);
    }
    for (; i < dirs.length; i++) {
      sp.append(File.pathSeparator);
      sp.append(dirs[i]);
    }
    return sp.toString();
  }
  /** 
 * Return a File cooresponding to the source of this location.
 * Return null if not available.
 */
  File sourceFile(  Location loc){
    try {
      String filename=loc.sourceName();
      String refName=loc.declaringType().name();
      int iDot=refName.lastIndexOf('.');
      String pkgName=(iDot >= 0) ? refName.substring(0,iDot + 1) : "";
      String full=pkgName.replace('.',File.separatorChar) + filename;
      for (int i=0; i < dirs.length; ++i) {
        File path=new File(dirs[i],full);
        if (path.exists()) {
          return path;
        }
      }
      return null;
    }
 catch (    AbsentInformationException e) {
      return null;
    }
  }
  /** 
 * Return a BufferedReader cooresponding to the source
 * of this location.
 * Return null if not available.
 * Note: returned reader must be closed.
 */
  BufferedReader sourceReader(  Location loc){
    File sourceFile=sourceFile(loc);
    if (sourceFile == null) {
      return null;
    }
    try {
      return new BufferedReader(new FileReader(sourceFile));
    }
 catch (    IOException exc) {
    }
    return null;
  }
}
