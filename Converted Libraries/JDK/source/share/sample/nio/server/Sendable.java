import java.io.*;
/** 
 * Method definitions used for preparing, sending, and release
 * content.
 * @author Mark Reinhold
 * @author Brad R. Wetmore
 */
interface Sendable {
  void prepare() throws IOException ;
  boolean send(  ChannelIO cio) throws IOException ;
  void release() throws IOException ;
}
