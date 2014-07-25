package sun.security.timestamp;
import java.io.IOException;
/** 
 * A timestamping service which conforms to the Time-Stamp Protocol (TSP)
 * defined in:
 * <a href="http://www.ietf.org/rfc/rfc3161.txt">RFC 3161</a>.
 * Individual timestampers may communicate with a Timestamping Authority (TSA)
 * over different transport machanisms. TSP permits at least the following
 * transports: HTTP, Internet mail, file-based and socket-based.
 * @author Vincent Ryan
 * @see HttpTimestamper
 */
public interface Timestamper {
  public TSResponse generateTimestamp(  TSRequest tsQuery) throws IOException ;
}
