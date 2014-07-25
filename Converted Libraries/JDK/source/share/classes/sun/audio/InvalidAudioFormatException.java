package sun.audio;
import java.io.IOException;
/** 
 * Signals an invalid audio stream for the stream handler.
 */
class InvalidAudioFormatException extends IOException {
  /** 
 * Constructor.
 */
  public InvalidAudioFormatException(){
    super();
  }
  /** 
 * Constructor with a detail message.
 */
  public InvalidAudioFormatException(  String s){
    super(s);
  }
}
