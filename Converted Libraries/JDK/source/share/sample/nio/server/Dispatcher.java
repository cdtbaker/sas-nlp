import java.io.*;
import java.nio.channels.*;
/** 
 * Base class for the Dispatchers.
 * <P>
 * Servers use these to obtain ready status, and then to dispatch jobs.
 * @author Mark Reinhold
 * @author Brad R. Wetmore
 */
interface Dispatcher extends Runnable {
  void register(  SelectableChannel ch,  int ops,  Handler h) throws IOException ;
}
