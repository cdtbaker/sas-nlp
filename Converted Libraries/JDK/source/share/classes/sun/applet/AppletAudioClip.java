package sun.applet;
import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.net.URL;
import java.net.URLConnection;
import java.applet.AudioClip;
import com.sun.media.sound.JavaSoundAudioClip;
/** 
 * Applet audio clip;
 * @author Arthur van Hoff, Kara Kytle
 */
public class AppletAudioClip implements AudioClip {
  private URL url=null;
  private AudioClip audioClip=null;
  boolean DEBUG=false;
  /** 
 * Constructs an AppletAudioClip from an URL.
 */
  public AppletAudioClip(  URL url){
    this.url=url;
    try {
      InputStream in=url.openStream();
      createAppletAudioClip(in);
    }
 catch (    IOException e) {
      if (DEBUG) {
        System.err.println("IOException creating AppletAudioClip" + e);
      }
    }
  }
  /** 
 * Constructs an AppletAudioClip from a URLConnection.
 */
  public AppletAudioClip(  URLConnection uc){
    try {
      createAppletAudioClip(uc.getInputStream());
    }
 catch (    IOException e) {
      if (DEBUG) {
        System.err.println("IOException creating AppletAudioClip" + e);
      }
    }
  }
  /** 
 * For constructing directly from Jar entries, or any other
 * raw Audio data. Note that the data provided must include the format
 * header.
 */
  public AppletAudioClip(  byte[] data){
    try {
      InputStream in=new ByteArrayInputStream(data);
      createAppletAudioClip(in);
    }
 catch (    IOException e) {
      if (DEBUG) {
        System.err.println("IOException creating AppletAudioClip " + e);
      }
    }
  }
  void createAppletAudioClip(  InputStream in) throws IOException {
    try {
      audioClip=new JavaSoundAudioClip(in);
    }
 catch (    Exception e3) {
      throw new IOException("Failed to construct the AudioClip: " + e3);
    }
  }
  public synchronized void play(){
    if (audioClip != null)     audioClip.play();
  }
  public synchronized void loop(){
    if (audioClip != null)     audioClip.loop();
  }
  public synchronized void stop(){
    if (audioClip != null)     audioClip.stop();
  }
}
