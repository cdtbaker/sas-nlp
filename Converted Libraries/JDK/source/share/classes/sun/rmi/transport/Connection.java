package sun.rmi.transport;
import java.io.*;
public interface Connection {
  /** 
 * Gets the input stream for this connection.
 */
  public InputStream getInputStream() throws IOException ;
  public void releaseInputStream() throws IOException ;
  /** 
 * Gets the output stream for this connection
 */
  public OutputStream getOutputStream() throws IOException ;
  public void releaseOutputStream() throws IOException ;
  /** 
 * Return true if channel can be used for multiple operations.
 */
  public boolean isReusable();
  /** 
 * Close connection.
 */
  public void close() throws IOException ;
  /** 
 * Returns the channel for this connection.
 */
  public Channel getChannel();
}
