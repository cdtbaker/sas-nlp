package javax.sound.midi;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
/** 
 * A <code>MetaMessage</code> is a <code>{@link MidiMessage}</code> that is not meaningful to synthesizers, but
 * that can be stored in a MIDI file and interpreted by a sequencer program.
 * (See the discussion in the <code>MidiMessage</code>
 * class description.)  The Standard MIDI Files specification defines
 * various types of meta-events, such as sequence number, lyric, cue point,
 * and set tempo.  There are also meta-events
 * for such information as lyrics, copyrights, tempo indications, time and key
 * signatures, markers, etc.  For more information, see the Standard MIDI Files 1.0
 * specification, which is part of the Complete MIDI 1.0 Detailed Specification
 * published by the MIDI Manufacturer's Association
 * (<a href = http://www.midi.org>http://www.midi.org</a>).
 * <p>
 * When data is being transported using MIDI wire protocol,
 * a <code>{@link ShortMessage}</code> with the status value <code>0xFF</code> represents
 * a system reset message.  In MIDI files, this same status value denotes a <code>MetaMessage</code>.
 * The types of meta-message are distinguished from each other by the first byte
 * that follows the status byte <code>0xFF</code>.  The subsequent bytes are data
 * bytes.  As with system exclusive messages, there are an arbitrary number of
 * data bytes, depending on the type of <code>MetaMessage</code>.
 * @see MetaEventListener
 * @author David Rivas
 * @author Kara Kytle
 */
public class MetaMessage extends MidiMessage {
  /** 
 * Status byte for <code>MetaMessage</code> (0xFF, or 255), which is used
 * in MIDI files.  It has the same value as SYSTEM_RESET, which
 * is used in the real-time "MIDI wire" protocol.
 * @see MidiMessage#getStatus
 */
  public static final int META=0xFF;
  private static byte[] defaultMessage={(byte)META,0};
  /** 
 * The length of the actual message in the data array.
 * This is used to determine how many bytes of the data array
 * is the message, and how many are the status byte, the
 * type byte, and the variable-length-int describing the
 * length of the message.
 */
  private int dataLength=0;
  /** 
 * Constructs a new <code>MetaMessage</code>. The contents of
 * the message are not set here; use{@link #setMessage(int,byte[],int) setMessage}to set them subsequently.
 */
  public MetaMessage(){
    this(defaultMessage);
  }
  /** 
 * Constructs a new {@code MetaMessage} and sets the message parameters.
 * The contents of the message can be changed by using
 * the {@code setMessage} method.
 * @param type   meta-message type (must be less than 128)
 * @param data   the data bytes in the MIDI message
 * @param length an amount of bytes in the {@code data} byte array;
 * it should be non-negative and less than or equal to{@code data.length}
 * @throws InvalidMidiDataException if the parameter values do not specify
 * a valid MIDI meta message
 * @see #setMessage(int,byte[],int)
 * @see #getType()
 * @see #getData()
 * @since 1.7
 */
  public MetaMessage(  int type,  byte[] data,  int length) throws InvalidMidiDataException {
    super(null);
    setMessage(type,data,length);
  }
  /** 
 * Constructs a new <code>MetaMessage</code>.
 * @param data an array of bytes containing the complete message.
 * The message data may be changed using the <code>setMessage</code>
 * method.
 * @see #setMessage
 */
  protected MetaMessage(  byte[] data){
    super(data);
    if (data.length >= 3) {
      dataLength=data.length - 3;
      int pos=2;
      while (pos < data.length && (data[pos] & 0x80) != 0) {
        dataLength--;
        pos++;
      }
    }
  }
  /** 
 * Sets the message parameters for a <code>MetaMessage</code>.
 * Since only one status byte value, <code>0xFF</code>, is allowed for meta-messages,
 * it does not need to be specified here.  Calls to <code>{@link MidiMessage#getStatus getStatus}</code> return
 * <code>0xFF</code> for all meta-messages.
 * <p>
 * The <code>type</code> argument should be a valid value for the byte that
 * follows the status byte in the <code>MetaMessage</code>.  The <code>data</code> argument
 * should contain all the subsequent bytes of the <code>MetaMessage</code>.  In other words,
 * the byte that specifies the type of <code>MetaMessage</code> is not considered a data byte.
 * @param type              meta-message type (must be less than 128)
 * @param data              the data bytes in the MIDI message
 * @param length    the number of bytes in the <code>data</code>
 * byte array
 * @throws                  <code>InvalidMidiDataException</code>  if the
 * parameter values do not specify a valid MIDI meta message
 */
  public void setMessage(  int type,  byte[] data,  int length) throws InvalidMidiDataException {
    if (type >= 128 || type < 0) {
      throw new InvalidMidiDataException("Invalid meta event with type " + type);
    }
    if ((length > 0 && length > data.length) || length < 0) {
      throw new InvalidMidiDataException("length out of bounds: " + length);
    }
    this.length=2 + getVarIntLength(length) + length;
    this.dataLength=length;
    this.data=new byte[this.length];
    this.data[0]=(byte)META;
    this.data[1]=(byte)type;
    writeVarInt(this.data,2,length);
    if (length > 0) {
      System.arraycopy(data,0,this.data,this.length - this.dataLength,this.dataLength);
    }
  }
  /** 
 * Obtains the type of the <code>MetaMessage</code>.
 * @return an integer representing the <code>MetaMessage</code> type
 */
  public int getType(){
    if (length >= 2) {
      return data[1] & 0xFF;
    }
    return 0;
  }
  /** 
 * Obtains a copy of the data for the meta message.  The returned
 * array of bytes does not include the status byte or the message
 * length data.  The length of the data for the meta message is
 * the length of the array.  Note that the length of the entire
 * message includes the status byte and the meta message type
 * byte, and therefore may be longer than the returned array.
 * @return array containing the meta message data.
 * @see MidiMessage#getLength
 */
  public byte[] getData(){
    byte[] returnedArray=new byte[dataLength];
    System.arraycopy(data,(length - dataLength),returnedArray,0,dataLength);
    return returnedArray;
  }
  /** 
 * Creates a new object of the same class and with the same contents
 * as this object.
 * @return a clone of this instance
 */
  public Object clone(){
    byte[] newData=new byte[length];
    System.arraycopy(data,0,newData,0,newData.length);
    MetaMessage event=new MetaMessage(newData);
    return event;
  }
  private int getVarIntLength(  long value){
    int length=0;
    do {
      value=value >> 7;
      length++;
    }
 while (value > 0);
    return length;
  }
  private final static long mask=0x7F;
  private void writeVarInt(  byte[] data,  int off,  long value){
    int shift=63;
    while ((shift > 0) && ((value & (mask << shift)) == 0))     shift-=7;
    while (shift > 0) {
      data[off++]=(byte)(((value & (mask << shift)) >> shift) | 0x80);
      shift-=7;
    }
    data[off]=(byte)(value & mask);
  }
}
