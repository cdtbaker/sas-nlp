package org.ojalgo.netio;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import org.ojalgo.ProgrammingError;
/** 
 * ResourceLocator
 * @author apete
 */
public final class ResourceLocator {
  private String myHost=null;
  private String myPath=null;
  private int myPort=-1;
  private Map<String,String> myQueryParameters=new TreeMap<String,String>();
  private String myScheme="http";
  public ResourceLocator(){
    super();
  }
  public String addQueryParameter(  String aKey,  String aValue){
    return myQueryParameters.put(aKey,aValue);
  }
  /** 
 * Open connection and return a buffered input stream reader.
 */
  public BufferedReader getStreamReader(){
    try {
      return new BufferedInputStreamReader(this.toURL().openStream());
    }
 catch (    IOException anException) {
      return null;
    }
  }
  public String removeQueryParameter(  String aKey){
    return myQueryParameters.remove(aKey);
  }
  public void setHost(  String someHost){
    myHost=someHost;
  }
  public void setPath(  String somePath){
    myPath=somePath;
  }
  /** 
 * The default (null) value is -1.
 */
  public void setPort(  int somePort){
    myPort=somePort;
  }
  public void setQueryParameters(  Map<String,String> someQueryParameters){
    myQueryParameters=someQueryParameters;
  }
  /** 
 * Protocol
 * The default value is "http"
 */
  public void setScheme(  String someScheme){
    myScheme=someScheme;
  }
  private URI makeURI(){
    try {
      return new URI(myScheme,null,myHost,myPort,myPath,this.query(),null);
    }
 catch (    URISyntaxException anException) {
      throw new ProgrammingError(anException);
    }
  }
  private String query(){
    if (myQueryParameters.size() >= 1) {
      StringBuilder retVal=new StringBuilder();
      Entry<String,String> tmpEntry;
      for (Iterator<Entry<String,String>> tmpIter=myQueryParameters.entrySet().iterator(); tmpIter.hasNext(); ) {
        tmpEntry=tmpIter.next();
        retVal.append(tmpEntry.getKey());
        retVal.append('=');
        retVal.append(tmpEntry.getValue());
        retVal.append('&');
      }
      retVal.setLength(retVal.length() - 1);
      return retVal.toString();
    }
 else {
      return null;
    }
  }
  private URL toURL(){
    try {
      return this.makeURI().toURL();
    }
 catch (    MalformedURLException anException) {
      throw new ProgrammingError(anException);
    }
  }
}
