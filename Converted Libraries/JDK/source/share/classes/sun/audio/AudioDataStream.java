package sun.audio;
import java.io.*;
import javax.sound.sampled.*;
import javax.sound.midi.*;
/** 
 * An input stream to play AudioData.
 * @see AudioPlayer
 * @see AudioData
 * @author Arthur van Hoff
 * @author Kara Kytle
 */
public class AudioDataStream extends ByteArrayInputStream {
  AudioData ad;
  /** 
 * Constructor
 */
  public AudioDataStream(  AudioData data){
    super(data.buffer);
    this.ad=data;
  }
  AudioData getAudioData(){
    return ad;
  }
}
