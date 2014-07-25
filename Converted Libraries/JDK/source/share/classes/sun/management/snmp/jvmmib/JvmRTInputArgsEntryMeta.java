package sun.management.snmp.jvmmib;
import java.io.Serializable;
import javax.management.MBeanServer;
import com.sun.jmx.snmp.SnmpCounter;
import com.sun.jmx.snmp.SnmpCounter64;
import com.sun.jmx.snmp.SnmpGauge;
import com.sun.jmx.snmp.SnmpInt;
import com.sun.jmx.snmp.SnmpUnsignedInt;
import com.sun.jmx.snmp.SnmpIpAddress;
import com.sun.jmx.snmp.SnmpTimeticks;
import com.sun.jmx.snmp.SnmpOpaque;
import com.sun.jmx.snmp.SnmpString;
import com.sun.jmx.snmp.SnmpStringFixed;
import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.SnmpNull;
import com.sun.jmx.snmp.SnmpValue;
import com.sun.jmx.snmp.SnmpVarBind;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.agent.SnmpMibNode;
import com.sun.jmx.snmp.agent.SnmpMib;
import com.sun.jmx.snmp.agent.SnmpMibEntry;
import com.sun.jmx.snmp.agent.SnmpStandardObjectServer;
import com.sun.jmx.snmp.agent.SnmpStandardMetaServer;
import com.sun.jmx.snmp.agent.SnmpMibSubRequest;
import com.sun.jmx.snmp.agent.SnmpMibTable;
import com.sun.jmx.snmp.EnumRowStatus;
import com.sun.jmx.snmp.SnmpDefinitions;
/** 
 * The class is used for representing SNMP metadata for the "JvmRTInputArgsEntry" group.
 * The group is defined with the following oid: 1.3.6.1.4.1.42.2.145.3.163.1.1.4.20.1.
 */
public class JvmRTInputArgsEntryMeta extends SnmpMibEntry implements Serializable, SnmpStandardMetaServer {
  /** 
 * Constructor for the metadata associated to "JvmRTInputArgsEntry".
 */
  public JvmRTInputArgsEntryMeta(  SnmpMib myMib,  SnmpStandardObjectServer objserv){
    objectserver=objserv;
    varList=new int[1];
    varList[0]=2;
    SnmpMibNode.sort(varList);
  }
  /** 
 * Get the value of a scalar variable
 */
  public SnmpValue get(  long var,  Object data) throws SnmpStatusException {
switch ((int)var) {
case 2:
      return new SnmpString(node.getJvmRTInputArgsItem());
case 1:
    throw new SnmpStatusException(SnmpStatusException.noSuchInstance);
default :
  break;
}
throw new SnmpStatusException(SnmpStatusException.noSuchObject);
}
/** 
 * Set the value of a scalar variable
 */
public SnmpValue set(SnmpValue x,long var,Object data) throws SnmpStatusException {
switch ((int)var) {
case 2:
throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);
case 1:
throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);
default :
break;
}
throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);
}
/** 
 * Check the value of a scalar variable
 */
public void check(SnmpValue x,long var,Object data) throws SnmpStatusException {
switch ((int)var) {
case 2:
throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);
case 1:
throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);
default :
throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);
}
}
/** 
 * Allow to bind the metadata description to a specific object.
 */
protected void setInstance(JvmRTInputArgsEntryMBean var){
node=var;
}
public void get(SnmpMibSubRequest req,int depth) throws SnmpStatusException {
objectserver.get(this,req,depth);
}
public void set(SnmpMibSubRequest req,int depth) throws SnmpStatusException {
objectserver.set(this,req,depth);
}
public void check(SnmpMibSubRequest req,int depth) throws SnmpStatusException {
objectserver.check(this,req,depth);
}
/** 
 * Returns true if "arc" identifies a scalar object.
 */
public boolean isVariable(long arc){
switch ((int)arc) {
case 2:
case 1:
return true;
default :
break;
}
return false;
}
/** 
 * Returns true if "arc" identifies a readable scalar object.
 */
public boolean isReadable(long arc){
switch ((int)arc) {
case 2:
return true;
default :
break;
}
return false;
}
public boolean skipVariable(long var,Object data,int pduVersion){
switch ((int)var) {
case 1:
return true;
default :
break;
}
return super.skipVariable(var,data,pduVersion);
}
/** 
 * Return the name of the attribute corresponding to the SNMP variable identified by "id".
 */
public String getAttributeName(long id) throws SnmpStatusException {
switch ((int)id) {
case 2:
return "JvmRTInputArgsItem";
case 1:
return "JvmRTInputArgsIndex";
default :
break;
}
throw new SnmpStatusException(SnmpStatusException.noSuchObject);
}
protected JvmRTInputArgsEntryMBean node;
protected SnmpStandardObjectServer objectserver=null;
}
