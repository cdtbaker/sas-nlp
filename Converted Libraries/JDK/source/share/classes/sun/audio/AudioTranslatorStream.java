package sun.audio;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
/** 
 * Translator for native audio formats (not implemented in this release).
 */
public class AudioTranslatorStream extends NativeAudioStream {
  private int length=0;
  public AudioTranslatorStream(  InputStream in) throws IOException {
    super(in);
    throw new InvalidAudioFormatException();
  }
  public int getLength(){
    return length;
  }
}
