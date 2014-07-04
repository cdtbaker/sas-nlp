package com.sun.media.sound;
import java.util.Vector;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.spi.MixerProvider;
/** 
 * Port provider.
 * @author Florian Bomers
 */
public class PortMixerProvider extends MixerProvider {
  /** 
 * Set of info objects for all port input devices on the system.
 */
  private static PortMixerInfo[] infos;
  /** 
 * Set of all port input devices on the system.
 */
  private static PortMixer[] devices;
static {
    Platform.initialize();
  }
  /** 
 * Required public no-arg constructor.
 */
  public PortMixerProvider(){
    if (Platform.isPortsEnabled()) {
      init();
    }
 else {
      infos=new PortMixerInfo[0];
      devices=new PortMixer[0];
    }
  }
  private static synchronized void init(){
    int numDevices=nGetNumDevices();
    if (infos == null || infos.length != numDevices) {
      if (Printer.trace)       Printer.trace("PortMixerProvider: init()");
      infos=new PortMixerInfo[numDevices];
      devices=new PortMixer[numDevices];
      for (int i=0; i < infos.length; i++) {
        infos[i]=nNewPortMixerInfo(i);
      }
      if (Printer.trace)       Printer.trace("PortMixerProvider: init(): found numDevices: " + numDevices);
    }
  }
  public Mixer.Info[] getMixerInfo(){
    Mixer.Info[] localArray=new Mixer.Info[infos.length];
    System.arraycopy(infos,0,localArray,0,infos.length);
    return localArray;
  }
  public Mixer getMixer(  Mixer.Info info){
    for (int i=0; i < infos.length; i++) {
      if (infos[i].equals(info)) {
        return getDevice(infos[i]);
      }
    }
    throw new IllegalArgumentException("Mixer " + info.toString() + " not supported by this provider.");
  }
  private Mixer getDevice(  PortMixerInfo info){
    int index=info.getIndex();
    if (devices[index] == null) {
      devices[index]=new PortMixer(info);
    }
    return devices[index];
  }
  /** 
 * Info class for PortMixers.  Adds an index value for
 * making native references to a particular device.
 * This constructor is called from native.
 */
static class PortMixerInfo extends Mixer.Info {
    private int index;
    private PortMixerInfo(    int index,    String name,    String vendor,    String description,    String version){
      super("Port " + name,vendor,description,version);
      this.index=index;
    }
    int getIndex(){
      return index;
    }
  }
  private static native int nGetNumDevices();
  private static native PortMixerInfo nNewPortMixerInfo(  int mixerIndex);
}
