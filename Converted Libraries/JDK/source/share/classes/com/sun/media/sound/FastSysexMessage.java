package com.sun.media.sound;
import javax.sound.midi.*;
/** 
 * optimized FastSysexMessage that doesn't copy the array upon instantiation
 * @author Florian Bomers
 */
class FastSysexMessage extends SysexMessage {
  FastSysexMessage(  byte[] data) throws InvalidMidiDataException {
    super(data);
    if (data.length == 0 || (((data[0] & 0xFF) != 0xF0) && ((data[0] & 0xFF) != 0xF7))) {
      super.setMessage(data,data.length);
    }
  }
  /** 
 * The returned array may be larger than this message is.
 * Use getLength() to get the real length of the message.
 */
  byte[] getReadOnlyMessage(){
    return data;
  }
  public void setMessage(  byte[] data,  int length) throws InvalidMidiDataException {
    if ((data.length == 0) || (((data[0] & 0xFF) != 0xF0) && ((data[0] & 0xFF) != 0xF7))) {
      super.setMessage(data,data.length);
    }
    this.length=length;
    this.data=new byte[this.length];
    System.arraycopy(data,0,this.data,0,length);
  }
}
