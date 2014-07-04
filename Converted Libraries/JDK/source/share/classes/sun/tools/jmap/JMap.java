package sun.tools.jmap;
import java.lang.reflect.Method;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.AttachNotSupportedException;
import sun.tools.attach.HotSpotVirtualMachine;
public class JMap {
  private static String HISTO_OPTION="-histo";
  private static String LIVE_HISTO_OPTION="-histo:live";
  private static String DUMP_OPTION_PREFIX="-dump:";
  private static String SA_TOOL_OPTIONS="-heap|-heap:format=b|-permstat|-finalizerinfo";
  private static String FORCE_SA_OPTION="-F";
  private static String DEFAULT_OPTION="-pmap";
  public static void main(  String[] args) throws Exception {
    if (args.length == 0) {
      usage();
    }
    boolean useSA=false;
    String option=null;
    int optionCount=0;
    while (optionCount < args.length) {
      String arg=args[optionCount];
      if (!arg.startsWith("-")) {
        break;
      }
      if (arg.equals(FORCE_SA_OPTION)) {
        useSA=true;
      }
 else {
        if (option != null) {
          usage();
        }
        option=arg;
      }
      optionCount++;
    }
    if (option == null) {
      option=DEFAULT_OPTION;
    }
    if (option.matches(SA_TOOL_OPTIONS)) {
      useSA=true;
    }
    int paramCount=args.length - optionCount;
    if (paramCount == 0 || paramCount > 2) {
      usage();
    }
    if (optionCount == 0 || paramCount != 1) {
      useSA=true;
    }
 else {
      if (!args[optionCount].matches("[0-9]+")) {
        useSA=true;
      }
    }
    if (useSA) {
      String params[]=new String[paramCount];
      for (int i=optionCount; i < args.length; i++) {
        params[i - optionCount]=args[i];
      }
      runTool(option,params);
    }
 else {
      String pid=args[1];
      if (option.equals(HISTO_OPTION)) {
        histo(pid,false);
      }
 else       if (option.equals(LIVE_HISTO_OPTION)) {
        histo(pid,true);
      }
 else       if (option.startsWith(DUMP_OPTION_PREFIX)) {
        dump(pid,option);
      }
 else {
        usage();
      }
    }
  }
  private static void runTool(  String option,  String args[]) throws Exception {
    String[][] tools={{"-pmap","sun.jvm.hotspot.tools.PMap"},{"-heap","sun.jvm.hotspot.tools.HeapSummary"},{"-heap:format=b","sun.jvm.hotspot.tools.HeapDumper"},{"-histo","sun.jvm.hotspot.tools.ObjectHistogram"},{"-permstat","sun.jvm.hotspot.tools.PermStat"},{"-finalizerinfo","sun.jvm.hotspot.tools.FinalizerInfo"}};
    String tool=null;
    if (option.startsWith(DUMP_OPTION_PREFIX)) {
      String fn=parseDumpOptions(option);
      if (fn == null)       usage();
      tool="sun.jvm.hotspot.tools.HeapDumper";
      args=prepend(fn,args);
      args=prepend("-f",args);
    }
 else {
      int i=0;
      while (i < tools.length) {
        if (option.equals(tools[i][0])) {
          tool=tools[i][1];
          break;
        }
        i++;
      }
    }
    if (tool == null) {
      usage();
    }
    Class<?> c=loadClass(tool);
    if (c == null) {
      usage();
    }
    Class[] argTypes={String[].class};
    Method m=c.getDeclaredMethod("main",argTypes);
    Object[] invokeArgs={args};
    m.invoke(null,invokeArgs);
  }
  private static Class loadClass(  String name){
    try {
      return Class.forName(name,true,ClassLoader.getSystemClassLoader());
    }
 catch (    Exception x) {
    }
    return null;
  }
  private static final String LIVE_OBJECTS_OPTION="-live";
  private static final String ALL_OBJECTS_OPTION="-all";
  private static void histo(  String pid,  boolean live) throws IOException {
    VirtualMachine vm=attach(pid);
    InputStream in=((HotSpotVirtualMachine)vm).heapHisto(live ? LIVE_OBJECTS_OPTION : ALL_OBJECTS_OPTION);
    drain(vm,in);
  }
  private static void dump(  String pid,  String options) throws IOException {
    String filename=parseDumpOptions(options);
    if (filename == null) {
      usage();
    }
    filename=new File(filename).getCanonicalPath();
    boolean live=isDumpLiveObjects(options);
    VirtualMachine vm=attach(pid);
    System.out.println("Dumping heap to " + filename + " ...");
    InputStream in=((HotSpotVirtualMachine)vm).dumpHeap((Object)filename,(live ? LIVE_OBJECTS_OPTION : ALL_OBJECTS_OPTION));
    drain(vm,in);
  }
  private static String parseDumpOptions(  String arg){
    assert arg.startsWith(DUMP_OPTION_PREFIX);
    String filename=null;
    String options[]=arg.substring(DUMP_OPTION_PREFIX.length()).split(",");
    for (int i=0; i < options.length; i++) {
      String option=options[i];
      if (option.equals("format=b")) {
      }
 else       if (option.equals("live")) {
      }
 else {
        if (option.startsWith("file=")) {
          filename=option.substring(5);
          if (filename.length() == 0) {
            return null;
          }
        }
 else {
          return null;
        }
      }
    }
    return filename;
  }
  private static boolean isDumpLiveObjects(  String arg){
    String options[]=arg.substring(DUMP_OPTION_PREFIX.length()).split(",");
    for (    String suboption : options) {
      if (suboption.equals("live")) {
        return true;
      }
    }
    return false;
  }
  private static VirtualMachine attach(  String pid){
    try {
      return VirtualMachine.attach(pid);
    }
 catch (    Exception x) {
      String msg=x.getMessage();
      if (msg != null) {
        System.err.println(pid + ": " + msg);
      }
 else {
        x.printStackTrace();
      }
      if ((x instanceof AttachNotSupportedException) && haveSA()) {
        System.err.println("The -F option can be used when the " + "target process is not responding");
      }
      System.exit(1);
      return null;
    }
  }
  private static void drain(  VirtualMachine vm,  InputStream in) throws IOException {
    byte b[]=new byte[256];
    int n;
    do {
      n=in.read(b);
      if (n > 0) {
        String s=new String(b,0,n,"UTF-8");
        System.out.print(s);
      }
    }
 while (n > 0);
    in.close();
    vm.detach();
  }
  private static String[] prepend(  String arg,  String args[]){
    String[] newargs=new String[args.length + 1];
    newargs[0]=arg;
    System.arraycopy(args,0,newargs,1,args.length);
    return newargs;
  }
  private static boolean haveSA(){
    Class c=loadClass("sun.jvm.hotspot.tools.HeapSummary");
    return (c != null);
  }
  private static void usage(){
    System.out.println("Usage:");
    if (haveSA()) {
      System.out.println("    jmap [option] <pid>");
      System.out.println("        (to connect to running process)");
      System.out.println("    jmap [option] <executable <core>");
      System.out.println("        (to connect to a core file)");
      System.out.println("    jmap [option] [server_id@]<remote server IP or hostname>");
      System.out.println("        (to connect to remote debug server)");
      System.out.println("");
      System.out.println("where <option> is one of:");
      System.out.println("    <none>               to print same info as Solaris pmap");
      System.out.println("    -heap                to print java heap summary");
      System.out.println("    -histo[:live]        to print histogram of java object heap; if the \"live\"");
      System.out.println("                         suboption is specified, only count live objects");
      System.out.println("    -permstat            to print permanent generation statistics");
      System.out.println("    -finalizerinfo       to print information on objects awaiting finalization");
      System.out.println("    -dump:<dump-options> to dump java heap in hprof binary format");
      System.out.println("                         dump-options:");
      System.out.println("                           live         dump only live objects; if not specified,");
      System.out.println("                                        all objects in the heap are dumped.");
      System.out.println("                           format=b     binary format");
      System.out.println("                           file=<file>  dump heap to <file>");
      System.out.println("                         Example: jmap -dump:live,format=b,file=heap.bin <pid>");
      System.out.println("    -F                   force. Use with -dump:<dump-options> <pid> or -histo");
      System.out.println("                         to force a heap dump or histogram when <pid> does not");
      System.out.println("                         respond. The \"live\" suboption is not supported");
      System.out.println("                         in this mode.");
      System.out.println("    -h | -help           to print this help message");
      System.out.println("    -J<flag>             to pass <flag> directly to the runtime system");
    }
 else {
      System.out.println("    jmap -histo <pid>");
      System.out.println("      (to connect to running process and print histogram of java object heap");
      System.out.println("    jmap -dump:<dump-options> <pid>");
      System.out.println("      (to connect to running process and dump java heap)");
      System.out.println("");
      System.out.println("    dump-options:");
      System.out.println("      format=b     binary default");
      System.out.println("      file=<file>  dump heap to <file>");
      System.out.println("");
      System.out.println("    Example:       jmap -dump:format=b,file=heap.bin <pid>");
    }
    System.exit(1);
  }
}
