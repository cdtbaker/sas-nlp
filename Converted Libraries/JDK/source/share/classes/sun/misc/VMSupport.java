package sun.misc;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.jar.Attributes;
public class VMSupport {
  private static Properties agentProps=null;
  /** 
 * Returns the agent properties.
 */
  public static synchronized Properties getAgentProperties(){
    if (agentProps == null) {
      agentProps=new Properties();
      initAgentProperties(agentProps);
    }
    return agentProps;
  }
  private static native Properties initAgentProperties(  Properties props);
  /** 
 * Write the given properties list to a byte array and return it. Properties with
 * a key or value that is not a String is filtered out. The stream written to the byte
 * array is ISO 8859-1 encoded.
 */
  private static byte[] serializePropertiesToByteArray(  Properties p) throws IOException {
    ByteArrayOutputStream out=new ByteArrayOutputStream(4096);
    Properties props=new Properties();
    Set<String> keyset=p.stringPropertyNames();
    for (    String key : keyset) {
      String value=p.getProperty(key);
      props.put(key,value);
    }
    props.store(out,null);
    return out.toByteArray();
  }
  public static byte[] serializePropertiesToByteArray() throws IOException {
    return serializePropertiesToByteArray(System.getProperties());
  }
  public static byte[] serializeAgentPropertiesToByteArray() throws IOException {
    return serializePropertiesToByteArray(getAgentProperties());
  }
  public static boolean isClassPathAttributePresent(  String path){
    try {
      Manifest man=(new JarFile(path)).getManifest();
      if (man != null) {
        if (man.getMainAttributes().getValue(Attributes.Name.CLASS_PATH) != null) {
          return true;
        }
      }
      return false;
    }
 catch (    IOException ioe) {
      throw new RuntimeException(ioe.getMessage());
    }
  }
}
