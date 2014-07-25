package sun.nio.ch;
import sun.misc.Cleaner;
public interface DirectBuffer {
  public long address();
  public Object viewedBuffer();
  public Cleaner cleaner();
}
