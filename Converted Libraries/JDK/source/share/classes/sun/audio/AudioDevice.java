package sun.audio;
import java.util.Hashtable;
import java.util.Vector;
import java.util.Enumeration;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.OutputStream;
import java.io.ByteArrayInputStream;
import javax.sound.sampled.*;
import javax.sound.midi.*;
import com.sun.media.sound.DataPusher;
import com.sun.media.sound.Toolkit;
/** 
 * This class provides an interface to the Headspace Audio engine through
 * the Java Sound API.
 * This class emulates systems with multiple audio channels, mixing
 * multiple streams for the workstation's single-channel device.
 * @see AudioData
 * @see AudioDataStream
 * @see AudioStream
 * @see AudioStreamSequence
 * @see ContinuousAudioDataStream
 * @author David Rivas
 * @author Kara Kytle
 * @author Jan Borgersen
 * @author Florian Bomers
 */
public class AudioDevice {
  private boolean DEBUG=false;
  /** 
 * Hashtable of audio clips / input streams. 
 */
  private Hashtable clipStreams;
  private Vector infos;
  /** 
 * Are we currently playing audio? 
 */
  private boolean playing=false;
  /** 
 * Handle to the JS audio mixer. 
 */
  private Mixer mixer=null;
  /** 
 * The default audio player. This audio player is initialized
 * automatically.
 */
  public static final AudioDevice device=new AudioDevice();
  /** 
 * Create an AudioDevice instance.
 */
  private AudioDevice(){
    clipStreams=new Hashtable();
    infos=new Vector();
  }
  private synchronized void startSampled(  AudioInputStream as,  InputStream in) throws UnsupportedAudioFileException, LineUnavailableException {
    Info info=null;
    DataPusher datapusher=null;
    DataLine.Info lineinfo=null;
    SourceDataLine sourcedataline=null;
    as=Toolkit.getPCMConvertedAudioInputStream(as);
    if (as == null) {
      return;
    }
    lineinfo=new DataLine.Info(SourceDataLine.class,as.getFormat());
    if (!(AudioSystem.isLineSupported(lineinfo))) {
      return;
    }
    sourcedataline=(SourceDataLine)AudioSystem.getLine(lineinfo);
    datapusher=new DataPusher(sourcedataline,as);
    info=new Info(null,in,datapusher);
    infos.addElement(info);
    datapusher.start();
  }
  private synchronized void startMidi(  InputStream bis,  InputStream in) throws InvalidMidiDataException, MidiUnavailableException {
    Sequencer sequencer=null;
    Info info=null;
    sequencer=MidiSystem.getSequencer();
    sequencer.open();
    try {
      sequencer.setSequence(bis);
    }
 catch (    IOException e) {
      throw new InvalidMidiDataException(e.getMessage());
    }
    info=new Info(sequencer,in,null);
    infos.addElement(info);
    sequencer.addMetaEventListener(info);
    sequencer.start();
  }
  /** 
 * Open an audio channel.
 */
  public synchronized void openChannel(  InputStream in){
    if (DEBUG) {
      System.out.println("AudioDevice: openChannel");
      System.out.println("input stream =" + in);
    }
    Info info=null;
    for (int i=0; i < infos.size(); i++) {
      info=(AudioDevice.Info)infos.elementAt(i);
      if (info.in == in) {
        return;
      }
    }
    AudioInputStream as=null;
    if (in instanceof AudioStream) {
      if (((AudioStream)in).midiformat != null) {
        try {
          startMidi(((AudioStream)in).stream,in);
        }
 catch (        Exception e) {
          return;
        }
      }
 else       if (((AudioStream)in).ais != null) {
        try {
          startSampled(((AudioStream)in).ais,in);
        }
 catch (        Exception e) {
          return;
        }
      }
    }
 else     if (in instanceof AudioDataStream) {
      if (in instanceof ContinuousAudioDataStream) {
        try {
          AudioInputStream ais=new AudioInputStream(in,((AudioDataStream)in).getAudioData().format,AudioSystem.NOT_SPECIFIED);
          startSampled(ais,in);
        }
 catch (        Exception e) {
          return;
        }
      }
 else {
        try {
          AudioInputStream ais=new AudioInputStream(in,((AudioDataStream)in).getAudioData().format,((AudioDataStream)in).getAudioData().buffer.length);
          startSampled(ais,in);
        }
 catch (        Exception e) {
          return;
        }
      }
    }
 else {
      BufferedInputStream bis=new BufferedInputStream(in,1024);
      try {
        try {
          as=AudioSystem.getAudioInputStream(bis);
        }
 catch (        IOException ioe) {
          return;
        }
        startSampled(as,in);
      }
 catch (      UnsupportedAudioFileException e) {
        try {
          try {
            MidiFileFormat mff=MidiSystem.getMidiFileFormat(bis);
          }
 catch (          IOException ioe1) {
            return;
          }
          startMidi(bis,in);
        }
 catch (        InvalidMidiDataException e1) {
          AudioFormat defformat=new AudioFormat(AudioFormat.Encoding.ULAW,8000,8,1,1,8000,true);
          try {
            AudioInputStream defaif=new AudioInputStream(bis,defformat,AudioSystem.NOT_SPECIFIED);
            startSampled(defaif,in);
          }
 catch (          UnsupportedAudioFileException es) {
            return;
          }
catch (          LineUnavailableException es2) {
            return;
          }
        }
catch (        MidiUnavailableException e2) {
          return;
        }
      }
catch (      LineUnavailableException e) {
        return;
      }
    }
    notify();
  }
  /** 
 * Close an audio channel.
 */
  public synchronized void closeChannel(  InputStream in){
    if (DEBUG) {
      System.out.println("AudioDevice.closeChannel");
    }
    if (in == null)     return;
    Info info;
    for (int i=0; i < infos.size(); i++) {
      info=(AudioDevice.Info)infos.elementAt(i);
      if (info.in == in) {
        if (info.sequencer != null) {
          info.sequencer.stop();
          infos.removeElement(info);
        }
 else         if (info.datapusher != null) {
          info.datapusher.stop();
          infos.removeElement(info);
        }
      }
    }
    notify();
  }
  /** 
 * Open the device (done automatically)
 */
  public synchronized void open(){
  }
  /** 
 * Close the device (done automatically)
 */
  public synchronized void close(){
  }
  /** 
 * Play open audio stream(s)
 */
  public void play(){
    if (DEBUG) {
      System.out.println("exiting play()");
    }
  }
  /** 
 * Close streams
 */
  public synchronized void closeStreams(){
    Info info;
    for (int i=0; i < infos.size(); i++) {
      info=(AudioDevice.Info)infos.elementAt(i);
      if (info.sequencer != null) {
        info.sequencer.stop();
        info.sequencer.close();
        infos.removeElement(info);
      }
 else       if (info.datapusher != null) {
        info.datapusher.stop();
        infos.removeElement(info);
      }
    }
    if (DEBUG) {
      System.err.println("Audio Device: Streams all closed.");
    }
    clipStreams=new Hashtable();
    infos=new Vector();
  }
  /** 
 * Number of channels currently open.
 */
  public int openChannels(){
    return infos.size();
  }
  /** 
 * Make the debug info print out.
 */
  void setVerbose(  boolean v){
    DEBUG=v;
  }
class Info implements MetaEventListener {
    Sequencer sequencer;
    InputStream in;
    DataPusher datapusher;
    Info(    Sequencer sequencer,    InputStream in,    DataPusher datapusher){
      this.sequencer=sequencer;
      this.in=in;
      this.datapusher=datapusher;
    }
    public void meta(    MetaMessage event){
      if (event.getType() == 47 && sequencer != null) {
        sequencer.close();
      }
    }
  }
}
