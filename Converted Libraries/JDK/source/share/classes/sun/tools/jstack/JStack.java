package sun.tools.jstack;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import java.io.IOException;
import java.io.InputStream;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.AttachNotSupportedException;
import sun.tools.attach.HotSpotVirtualMachine;
public class JStack {
  public static void main(  String[] args) throws Exception {
    if (args.length == 0) {
      usage();
    }
    boolean useSA=false;
    boolean mixed=false;
    boolean locks=false;
    int optionCount=0;
    while (optionCount < args.length) {
      String arg=args[optionCount];
      if (!arg.startsWith("-")) {
        break;
      }
      if (arg.equals("-F")) {
        useSA=true;
      }
 else {
        if (arg.equals("-m")) {
          mixed=true;
        }
 else {
          if (arg.equals("-l")) {
            locks=true;
          }
 else {
            usage();
          }
        }
      }
      optionCount++;
    }
    if (mixed) {
      useSA=true;
    }
    int paramCount=args.length - optionCount;
    if (paramCount == 0 || paramCount > 2) {
      usage();
    }
    if (paramCount == 2) {
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
      runJStackTool(mixed,locks,params);
    }
 else {
      String pid=args[optionCount];
      String params[];
      if (locks) {
        params=new String[]{"-l"};
      }
 else {
        params=new String[0];
      }
      runThreadDump(pid,params);
    }
  }
  private static void runJStackTool(  boolean mixed,  boolean locks,  String args[]) throws Exception {
    Class<?> cl=loadSAClass();
    if (cl == null) {
      usage();
    }
    if (mixed) {
      args=prepend("-m",args);
    }
    if (locks) {
      args=prepend("-l",args);
    }
    Class[] argTypes={String[].class};
    Method m=cl.getDeclaredMethod("main",argTypes);
    Object[] invokeArgs={args};
    m.invoke(null,invokeArgs);
  }
  private static Class loadSAClass(){
    try {
      return Class.forName("sun.jvm.hotspot.tools.JStack",true,ClassLoader.getSystemClassLoader());
    }
 catch (    Exception x) {
    }
    return null;
  }
  private static void runThreadDump(  String pid,  String args[]) throws Exception {
    VirtualMachine vm=null;
    try {
      vm=VirtualMachine.attach(pid);
    }
 catch (    Exception x) {
      String msg=x.getMessage();
      if (msg != null) {
        System.err.println(pid + ": " + msg);
      }
 else {
        x.printStackTrace();
      }
      if ((x instanceof AttachNotSupportedException) && (loadSAClass() != null)) {
        System.err.println("The -F option can be used when the target " + "process is not responding");
      }
      System.exit(1);
    }
    InputStream in=((HotSpotVirtualMachine)vm).remoteDataDump((Object[])args);
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
  private static void usage(){
    System.out.println("Usage:");
    System.out.println("    jstack [-l] <pid>");
    System.out.println("        (to connect to running process)");
    if (loadSAClass() != null) {
      System.out.println("    jstack -F [-m] [-l] <pid>");
      System.out.println("        (to connect to a hung process)");
      System.out.println("    jstack [-m] [-l] <executable> <core>");
      System.out.println("        (to connect to a core file)");
      System.out.println("    jstack [-m] [-l] [server_id@]<remote server IP or hostname>");
      System.out.println("        (to connect to a remote debug server)");
    }
    System.out.println("");
    System.out.println("Options:");
    if (loadSAClass() != null) {
      System.out.println("    -F  to force a thread dump. Use when jstack <pid> does not respond" + " (process is hung)");
      System.out.println("    -m  to print both java and native frames (mixed mode)");
    }
    System.out.println("    -l  long listing. Prints additional information about locks");
    System.out.println("    -h or -help to print this help message");
    System.exit(1);
  }
}
