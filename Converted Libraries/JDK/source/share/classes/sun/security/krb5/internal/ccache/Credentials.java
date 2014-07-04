package sun.security.krb5.internal.ccache;
import sun.security.krb5.*;
import sun.security.krb5.internal.*;
public class Credentials {
  PrincipalName cname;
  Realm crealm;
  PrincipalName sname;
  Realm srealm;
  EncryptionKey key;
  KerberosTime authtime;
  KerberosTime starttime;
  KerberosTime endtime;
  KerberosTime renewTill;
  HostAddresses caddr;
  AuthorizationData authorizationData;
  public boolean isEncInSKey;
  TicketFlags flags;
  Ticket ticket;
  Ticket secondTicket;
  private boolean DEBUG=Krb5.DEBUG;
  public Credentials(  PrincipalName new_cname,  PrincipalName new_sname,  EncryptionKey new_key,  KerberosTime new_authtime,  KerberosTime new_starttime,  KerberosTime new_endtime,  KerberosTime new_renewTill,  boolean new_isEncInSKey,  TicketFlags new_flags,  HostAddresses new_caddr,  AuthorizationData new_authData,  Ticket new_ticket,  Ticket new_secondTicket){
    cname=(PrincipalName)new_cname.clone();
    if (new_cname.getRealm() != null) {
      crealm=(Realm)new_cname.getRealm().clone();
    }
    sname=(PrincipalName)new_sname.clone();
    if (new_sname.getRealm() != null) {
      srealm=(Realm)new_sname.getRealm().clone();
    }
    key=(EncryptionKey)new_key.clone();
    authtime=(KerberosTime)new_authtime.clone();
    if (new_starttime != null) {
      starttime=(KerberosTime)new_starttime.clone();
    }
    endtime=(KerberosTime)new_endtime.clone();
    if (new_renewTill != null) {
      renewTill=(KerberosTime)new_renewTill.clone();
    }
    if (new_caddr != null) {
      caddr=(HostAddresses)new_caddr.clone();
    }
    if (new_authData != null) {
      authorizationData=(AuthorizationData)new_authData.clone();
    }
    isEncInSKey=new_isEncInSKey;
    flags=(TicketFlags)new_flags.clone();
    ticket=(Ticket)(new_ticket.clone());
    if (new_secondTicket != null) {
      secondTicket=(Ticket)new_secondTicket.clone();
    }
  }
  public Credentials(  KDCRep kdcRep,  Ticket new_secondTicket,  AuthorizationData new_authorizationData,  boolean new_isEncInSKey){
    if (kdcRep.encKDCRepPart == null) {
      return;
    }
    crealm=(Realm)kdcRep.crealm.clone();
    cname=(PrincipalName)kdcRep.cname.clone();
    ticket=(Ticket)kdcRep.ticket.clone();
    key=(EncryptionKey)kdcRep.encKDCRepPart.key.clone();
    flags=(TicketFlags)kdcRep.encKDCRepPart.flags.clone();
    authtime=(KerberosTime)kdcRep.encKDCRepPart.authtime.clone();
    if (kdcRep.encKDCRepPart.starttime != null) {
      starttime=(KerberosTime)kdcRep.encKDCRepPart.starttime.clone();
    }
    endtime=(KerberosTime)kdcRep.encKDCRepPart.endtime.clone();
    if (kdcRep.encKDCRepPart.renewTill != null) {
      renewTill=(KerberosTime)kdcRep.encKDCRepPart.renewTill.clone();
    }
    srealm=(Realm)kdcRep.encKDCRepPart.srealm.clone();
    sname=(PrincipalName)kdcRep.encKDCRepPart.sname.clone();
    caddr=(HostAddresses)kdcRep.encKDCRepPart.caddr.clone();
    secondTicket=(Ticket)new_secondTicket.clone();
    authorizationData=(AuthorizationData)new_authorizationData.clone();
    isEncInSKey=new_isEncInSKey;
  }
  public Credentials(  KDCRep kdcRep){
    this(kdcRep,null);
  }
  public Credentials(  KDCRep kdcRep,  Ticket new_ticket){
    sname=(PrincipalName)kdcRep.encKDCRepPart.sname.clone();
    srealm=(Realm)kdcRep.encKDCRepPart.srealm.clone();
    try {
      sname.setRealm(srealm);
    }
 catch (    RealmException e) {
    }
    cname=(PrincipalName)kdcRep.cname.clone();
    crealm=(Realm)kdcRep.crealm.clone();
    try {
      cname.setRealm(crealm);
    }
 catch (    RealmException e) {
    }
    key=(EncryptionKey)kdcRep.encKDCRepPart.key.clone();
    authtime=(KerberosTime)kdcRep.encKDCRepPart.authtime.clone();
    if (kdcRep.encKDCRepPart.starttime != null) {
      starttime=(KerberosTime)kdcRep.encKDCRepPart.starttime.clone();
    }
 else {
      starttime=null;
    }
    endtime=(KerberosTime)kdcRep.encKDCRepPart.endtime.clone();
    if (kdcRep.encKDCRepPart.renewTill != null) {
      renewTill=(KerberosTime)kdcRep.encKDCRepPart.renewTill.clone();
    }
 else {
      renewTill=null;
    }
    flags=kdcRep.encKDCRepPart.flags;
    if (kdcRep.encKDCRepPart.caddr != null) {
      caddr=(HostAddresses)kdcRep.encKDCRepPart.caddr.clone();
    }
 else {
      caddr=null;
    }
    ticket=(Ticket)kdcRep.ticket.clone();
    if (new_ticket != null) {
      secondTicket=(Ticket)new_ticket.clone();
      isEncInSKey=true;
    }
 else {
      secondTicket=null;
      isEncInSKey=false;
    }
  }
  /** 
 * Checks if this credential is expired
 */
  public boolean isValid(){
    boolean valid=true;
    if (endtime.getTime() < System.currentTimeMillis()) {
      valid=false;
    }
 else     if (starttime != null) {
      if (starttime.getTime() > System.currentTimeMillis()) {
        valid=false;
      }
    }
 else {
      if (authtime.getTime() > System.currentTimeMillis()) {
        valid=false;
      }
    }
    return valid;
  }
  public PrincipalName getServicePrincipal() throws RealmException {
    if (sname.getRealm() == null) {
      sname.setRealm(srealm);
    }
    return sname;
  }
  public sun.security.krb5.Credentials setKrbCreds(){
    return new sun.security.krb5.Credentials(ticket,cname,sname,key,flags,authtime,starttime,endtime,renewTill,caddr);
  }
  public KerberosTime getAuthTime(){
    return authtime;
  }
  public KerberosTime getEndTime(){
    return endtime;
  }
  public TicketFlags getTicketFlags(){
    return flags;
  }
  public int getEType(){
    return key.getEType();
  }
}
