package com.sun.jmx.snmp.daemon;
import java.util.logging.Level;
import java.util.Vector;
import com.sun.jmx.snmp.SnmpEngine;
import com.sun.jmx.snmp.SnmpPdu;
import com.sun.jmx.snmp.SnmpValue;
import com.sun.jmx.snmp.SnmpVarBind;
import com.sun.jmx.snmp.SnmpVarBindList;
import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.SnmpDefinitions;
import com.sun.jmx.snmp.SnmpStatusException;
import static com.sun.jmx.defaults.JmxProperties.SNMP_ADAPTOR_LOGGER;
import com.sun.jmx.snmp.agent.SnmpMibAgent;
import com.sun.jmx.snmp.agent.SnmpMibRequest;
import com.sun.jmx.snmp.daemon.SnmpAdaptorServer;
import com.sun.jmx.snmp.internal.SnmpIncomingRequest;
import com.sun.jmx.snmp.ThreadContext;
class SnmpSubNextRequestHandler extends SnmpSubRequestHandler {
  private SnmpAdaptorServer server=null;
  /** 
 * The constuctor initialize the subrequest with the whole varbind
 * list contained in the original request.
 */
  protected SnmpSubNextRequestHandler(  SnmpAdaptorServer server,  SnmpMibAgent agent,  SnmpPdu req){
    super(agent,req);
    init(req,server);
  }
  protected SnmpSubNextRequestHandler(  SnmpEngine engine,  SnmpAdaptorServer server,  SnmpIncomingRequest incRequest,  SnmpMibAgent agent,  SnmpPdu req){
    super(engine,incRequest,agent,req);
    init(req,server);
    if (SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
      SNMP_ADAPTOR_LOGGER.logp(Level.FINEST,SnmpSubNextRequestHandler.class.getName(),"SnmpSubNextRequestHandler","Constructor : " + this);
    }
  }
  private void init(  SnmpPdu req,  SnmpAdaptorServer server){
    this.server=server;
    final int max=translation.length;
    final SnmpVarBind[] list=req.varBindList;
    final NonSyncVector<SnmpVarBind> nonSyncVarBind=((NonSyncVector<SnmpVarBind>)varBind);
    for (int i=0; i < max; i++) {
      translation[i]=i;
      final SnmpVarBind newVarBind=new SnmpVarBind(list[i].oid,list[i].value);
      nonSyncVarBind.addNonSyncElement(newVarBind);
    }
  }
  public void run(){
    try {
      final ThreadContext oldContext=ThreadContext.push("SnmpUserData",data);
      try {
        if (SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
          SNMP_ADAPTOR_LOGGER.logp(Level.FINER,SnmpSubRequestHandler.class.getName(),"run","[" + Thread.currentThread() + "]:getNext operation on "+ agent.getMibName());
        }
        agent.getNext(createMibRequest(varBind,snmpVersionTwo,data));
      }
  finally {
        ThreadContext.restore(oldContext);
      }
    }
 catch (    SnmpStatusException x) {
      errorStatus=x.getStatus();
      errorIndex=x.getErrorIndex();
      if (SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
        SNMP_ADAPTOR_LOGGER.logp(Level.FINEST,SnmpSubRequestHandler.class.getName(),"run","[" + Thread.currentThread() + "]:an Snmp error occured during the operation",x);
      }
    }
catch (    Exception x) {
      errorStatus=SnmpDefinitions.snmpRspGenErr;
      if (SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
        SNMP_ADAPTOR_LOGGER.logp(Level.FINEST,SnmpSubRequestHandler.class.getName(),"run","[" + Thread.currentThread() + "]:a generic error occured during the operation",x);
      }
    }
    if (SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
      SNMP_ADAPTOR_LOGGER.logp(Level.FINER,SnmpSubRequestHandler.class.getName(),"run","[" + Thread.currentThread() + "]:operation completed");
    }
  }
  /** 
 * The method updates the varbind list of the subrequest.
 */
  protected void updateRequest(  SnmpVarBind var,  int pos){
    if (SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
      SNMP_ADAPTOR_LOGGER.logp(Level.FINEST,SnmpSubRequestHandler.class.getName(),"updateRequest","Copy :" + var);
    }
    int size=varBind.size();
    translation[size]=pos;
    final SnmpVarBind newVarBind=new SnmpVarBind(var.oid,var.value);
    if (SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
      SNMP_ADAPTOR_LOGGER.logp(Level.FINEST,SnmpSubRequestHandler.class.getName(),"updateRequest","Copied :" + newVarBind);
    }
    varBind.addElement(newVarBind);
  }
  /** 
 * The method updates a given var bind list with the result of a
 * previsouly invoked operation.
 * Prior to calling the method, one must make sure that the operation was
 * successful. As such the method getErrorIndex or getErrorStatus should be
 * called.
 */
  protected void updateResult(  SnmpVarBind[] result){
    final int max=varBind.size();
    for (int i=0; i < max; i++) {
      final int index=translation[i];
      final SnmpVarBind elmt=(SnmpVarBind)((NonSyncVector)varBind).elementAtNonSync(i);
      final SnmpVarBind vb=result[index];
      if (vb == null) {
        result[index]=elmt;
        continue;
      }
      final SnmpValue val=vb.value;
      if ((val == null) || (val == SnmpVarBind.endOfMibView)) {
        if ((elmt != null) && (elmt.value != SnmpVarBind.endOfMibView))         result[index]=elmt;
        continue;
      }
      if (elmt == null)       continue;
      if (elmt.value == SnmpVarBind.endOfMibView)       continue;
      int comp=elmt.oid.compareTo(vb.oid);
      if (comp < 0) {
        result[index]=elmt;
      }
 else {
        if (comp == 0) {
          if (SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
            SNMP_ADAPTOR_LOGGER.logp(Level.FINER,SnmpSubRequestHandler.class.getName(),"updateResult"," oid overlapping. Oid : " + elmt.oid + "value :"+ elmt.value);
            SNMP_ADAPTOR_LOGGER.logp(Level.FINER,SnmpSubRequestHandler.class.getName(),"updateResult","Already present varBind : " + vb);
          }
          SnmpOid oid=vb.oid;
          SnmpMibAgent deeperAgent=server.getAgentMib(oid);
          if (SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
            SNMP_ADAPTOR_LOGGER.logp(Level.FINER,SnmpSubRequestHandler.class.getName(),"updateResult","Deeper agent : " + deeperAgent);
          }
          if (deeperAgent == agent) {
            if (SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
              SNMP_ADAPTOR_LOGGER.logp(Level.FINER,SnmpSubRequestHandler.class.getName(),"updateResult","The current agent is the deeper one. Update the value with the current one");
            }
            result[index].value=elmt.value;
          }
        }
      }
    }
  }
}
