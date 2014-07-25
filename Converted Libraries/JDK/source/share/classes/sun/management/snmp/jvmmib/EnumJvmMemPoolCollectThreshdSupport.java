package sun.management.snmp.jvmmib;
import java.io.Serializable;
import java.util.Hashtable;
import com.sun.jmx.snmp.Enumerated;
/** 
 * The class is used for representing "JvmMemPoolCollectThreshdSupport".
 */
public class EnumJvmMemPoolCollectThreshdSupport extends Enumerated implements Serializable {
  protected static Hashtable<Integer,String> intTable=new Hashtable<Integer,String>();
  protected static Hashtable<String,Integer> stringTable=new Hashtable<String,Integer>();
static {
    intTable.put(new Integer(2),"supported");
    intTable.put(new Integer(1),"unsupported");
    stringTable.put("supported",new Integer(2));
    stringTable.put("unsupported",new Integer(1));
  }
  public EnumJvmMemPoolCollectThreshdSupport(  int valueIndex) throws IllegalArgumentException {
    super(valueIndex);
  }
  public EnumJvmMemPoolCollectThreshdSupport(  Integer valueIndex) throws IllegalArgumentException {
    super(valueIndex);
  }
  public EnumJvmMemPoolCollectThreshdSupport() throws IllegalArgumentException {
    super();
  }
  public EnumJvmMemPoolCollectThreshdSupport(  String x) throws IllegalArgumentException {
    super(x);
  }
  protected Hashtable getIntTable(){
    return intTable;
  }
  protected Hashtable getStringTable(){
    return stringTable;
  }
}
