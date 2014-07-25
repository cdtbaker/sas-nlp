package com.sun.media.sound;
import java.util.Vector;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.spi.MixerProvider;
/** 
 * DirectAudioDevice provider.
 * @author Florian Bomers
 */
public class DirectAudioDeviceProvider extends MixerProvider {
  /** 
 * Set of info objects for all port input devices on the system.
 */
  private static DirectAudioDeviceInfo[] infos;
  /** 
 * Set of all port input devices on the system.
 */
  private static DirectAudioDevice[] devices;
static {
    Platform.initialize();
  }
  /** 
 * Required public no-arg constructor.
 */
  public DirectAudioDeviceProvider(){
    if (Platform.isDirectAudioEnabled()) {
      init();
    }
 else {
      infos=new DirectAudioDeviceInfo[0];
      devices=new DirectAudioDevice[0];
    }
  }
  private synchronized static void init(){
    int numDevices=nGetNumDevices();
    if (infos == null || infos.length != numDevices) {
      if (Printer.trace)       Printer.trace("DirectAudioDeviceProvider: init()");
      infos=new DirectAudioDeviceInfo[numDevices];
      devices=new DirectAudioDevice[numDevices];
      for (int i=0; i < infos.length; i++) {
        infos[i]=nNewDirectAudioDeviceInfo(i);
      }
      if (Printer.trace)       Printer.trace("DirectAudioDeviceProvider: init(): found numDevices: " + numDevices);
    }
  }
  public Mixer.Info[] getMixerInfo(){
    Mixer.Info[] localArray=new Mixer.Info[infos.length];
    System.arraycopy(infos,0,localArray,0,infos.length);
    return localArray;
  }
  public Mixer getMixer(  Mixer.Info info){
    if (info == null) {
      for (int i=0; i < infos.length; i++) {
        Mixer mixer=getDevice(infos[i]);
        if (mixer.getSourceLineInfo().length > 0) {
          return mixer;
        }
      }
    }
    for (int i=0; i < infos.length; i++) {
      if (infos[i].equals(info)) {
        return getDevice(infos[i]);
      }
    }
    throw new IllegalArgumentException("Mixer " + info.toString() + " not supported by this provider.");
  }
  private Mixer getDevice(  DirectAudioDeviceInfo info){
    int index=info.getIndex();
    if (devices[index] == null) {
      devices[index]=new DirectAudioDevice(info);
    }
    return devices[index];
  }
  /** 
 * Info class for DirectAudioDevices.  Adds an index value and a string for
 * making native references to a particular device.
 * This constructor is called from native.
 */
static class DirectAudioDeviceInfo extends Mixer.Info {
    private int index;
    private int maxSimulLines;
    private int deviceID;
    private DirectAudioDeviceInfo(    int index,    int deviceID,    int maxSimulLines,    String name,    String vendor,    String description,    String version){
      super(name,vendor,"Direct Audio Device: " + description,version);
      this.index=index;
      this.maxSimulLines=maxSimulLines;
      this.deviceID=deviceID;
    }
    int getIndex(){
      return index;
    }
    int getMaxSimulLines(){
      return maxSimulLines;
    }
    int getDeviceID(){
      return deviceID;
    }
  }
  private static native int nGetNumDevices();
  private static native DirectAudioDeviceInfo nNewDirectAudioDeviceInfo(  int deviceIndex);
}
