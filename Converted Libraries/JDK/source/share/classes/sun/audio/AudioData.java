package sun.audio;
import java.io.*;
import javax.sound.sampled.*;
/** 
 * A clip of audio data. This data can be used to construct an
 * AudioDataStream, which can be played. <p>
 * @author  Arthur van Hoff
 * @author  Kara Kytle
 * @see AudioDataStream
 * @see AudioPlayer
 */
public class AudioData {
  private static final AudioFormat DEFAULT_FORMAT=new AudioFormat(AudioFormat.Encoding.ULAW,8000,8,1,1,8000,true);
  AudioFormat format;
  byte buffer[];
  /** 
 * Constructor
 */
  public AudioData(  byte buffer[]){
    this.buffer=buffer;
    this.format=DEFAULT_FORMAT;
    try {
      AudioInputStream ais=AudioSystem.getAudioInputStream(new ByteArrayInputStream(buffer));
      this.format=ais.getFormat();
      ais.close();
    }
 catch (    IOException e) {
    }
catch (    UnsupportedAudioFileException e1) {
    }
  }
  /** 
 * Non-public constructor; this is the one we use in ADS and CADS
 * constructors.
 */
  AudioData(  AudioFormat format,  byte[] buffer){
    this.format=format;
    this.buffer=buffer;
  }
}
