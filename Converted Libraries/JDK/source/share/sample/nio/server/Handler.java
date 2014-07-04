import java.io.*;
import java.nio.channels.*;
/** 
 * Base class for the Handlers.
 * @author Mark Reinhold
 * @author Brad R. Wetmore
 */
interface Handler {
  void handle(  SelectionKey sk) throws IOException ;
}
