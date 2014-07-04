package sun.net.www.protocol.jar;
import java.io.*;
import java.net.*;
import java.util.jar.*;
public interface URLJarFileCallBack {
  public JarFile retrieve(  URL url) throws IOException ;
}
