package sun.audio;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
/** 
 * A Sun-specific AudioStream that supports native audio formats.
 */
public class NativeAudioStream extends FilterInputStream {
  public NativeAudioStream(  InputStream in) throws IOException {
    super(in);
  }
  public int getLength(){
    return 0;
  }
}
