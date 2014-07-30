package j2dbench;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Enumeration;
import java.util.Vector;
import java.io.PrintWriter;
public class ResultSet {
  static Hashtable preferprops;
  static Hashtable ignoreprops;
  static String preferredkeys[]={"java.version","java.runtime.version","java.class.version","java.vm.version","java.vm.name","java.vm.info","java.home","java.compiler","os.arch","os.name","os.version","user.name","sun.cpu.endian","sun.cpu.isalist"};
  static String ignoredkeys[]={"user.dir","user.home","user.timezone","path.separator","line.separator","file.separator","file.encoding","file.encoding.pkg","java.class.path","java.library.path","java.io.tmpdir","java.ext.dirs","java.endorsed.dirs","java.util.prefs.PreferencesFactory","sun.java2d.fontpath","sun.boot.library.path","sun.boot.class.path"};
  static String unknown="unknown";
static {
    preferprops=new Hashtable();
    for (int i=0; i < preferredkeys.length; i++) {
      preferprops.put(preferredkeys[i],unknown);
    }
    ignoreprops=new Hashtable();
    for (int i=0; i < ignoredkeys.length; i++) {
      ignoreprops.put(ignoredkeys[i],unknown);
    }
  }
  Hashtable props;
  Vector results;
  long start;
  long end;
  String title;
  String description;
  public ResultSet(){
    Properties sysprops=System.getProperties();
    props=(Hashtable)preferprops.clone();
    Enumeration enum_=sysprops.keys();
    while (enum_.hasMoreElements()) {
      Object key=enum_.nextElement();
      if (!ignoreprops.containsKey(key)) {
        props.put(key,sysprops.get(key));
      }
    }
    results=new Vector();
    start=System.currentTimeMillis();
  }
  public void setTitle(  String title){
    this.title=title;
  }
  public void setDescription(  String desc){
    this.description=desc;
  }
  public void record(  Result result){
    results.addElement(result);
  }
  public void summarize(){
    end=System.currentTimeMillis();
    for (int i=0; i < results.size(); i++) {
      ((Result)results.elementAt(i)).summarize();
    }
  }
  public void write(  PrintWriter pw){
    pw.println("<result-set version=\"0.1\" name=\"" + title + "\">");
    pw.println("  <test-desc>" + description + "</test-desc>");
    pw.println("  <test-date start=\"" + start + "\" end=\""+ end+ "\"/>");
    for (int i=0; i < preferredkeys.length; i++) {
      String key=preferredkeys[i];
      pw.println("  <sys-prop key=\"" + key + "\" value=\""+ props.get(key)+ "\"/>");
    }
    Enumeration enum_=props.keys();
    while (enum_.hasMoreElements()) {
      Object key=enum_.nextElement();
      if (!preferprops.containsKey(key)) {
        pw.println("  <sys-prop key=\"" + key + "\" value=\""+ props.get(key)+ "\"/>");
      }
    }
    for (int i=0; i < results.size(); i++) {
      ((Result)results.elementAt(i)).write(pw);
    }
    pw.println("</result-set>");
  }
}