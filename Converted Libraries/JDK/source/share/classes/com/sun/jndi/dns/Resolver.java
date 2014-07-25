package com.sun.jndi.dns;
import javax.naming.*;
/** 
 * The Resolver class performs DNS client operations in support of DnsContext.
 * <p> Every DnsName instance passed to or returned from a method of
 * this class should be fully-qualified and contain a root label (an
 * empty component at position 0).
 * @author Scott Seligman
 */
class Resolver {
  private DnsClient dnsClient;
  private int timeout;
  private int retries;
  Resolver(  String[] servers,  int timeout,  int retries) throws NamingException {
    this.timeout=timeout;
    this.retries=retries;
    dnsClient=new DnsClient(servers,timeout,retries);
  }
  public void close(){
    dnsClient.close();
    dnsClient=null;
  }
  ResourceRecords query(  DnsName fqdn,  int rrclass,  int rrtype,  boolean recursion,  boolean auth) throws NamingException {
    return dnsClient.query(fqdn,rrclass,rrtype,recursion,auth);
  }
  ResourceRecords queryZone(  DnsName zone,  int rrclass,  boolean recursion) throws NamingException {
    DnsClient cl=new DnsClient(findNameServers(zone,recursion),timeout,retries);
    try {
      return cl.queryZone(zone,rrclass,recursion);
    }
  finally {
      cl.close();
    }
  }
  DnsName findZoneName(  DnsName fqdn,  int rrclass,  boolean recursion) throws NamingException {
    fqdn=(DnsName)fqdn.clone();
    while (fqdn.size() > 1) {
      ResourceRecords rrs=null;
      try {
        rrs=query(fqdn,rrclass,ResourceRecord.TYPE_SOA,recursion,false);
      }
 catch (      NameNotFoundException e) {
        throw e;
      }
catch (      NamingException e) {
      }
      if (rrs != null) {
        if (rrs.answer.size() > 0) {
          return fqdn;
        }
        for (int i=0; i < rrs.authority.size(); i++) {
          ResourceRecord rr=(ResourceRecord)rrs.authority.elementAt(i);
          if (rr.getType() == ResourceRecord.TYPE_SOA) {
            DnsName zone=rr.getName();
            if (fqdn.endsWith(zone)) {
              return zone;
            }
          }
        }
      }
      fqdn.remove(fqdn.size() - 1);
    }
    return fqdn;
  }
  ResourceRecord findSoa(  DnsName zone,  int rrclass,  boolean recursion) throws NamingException {
    ResourceRecords rrs=query(zone,rrclass,ResourceRecord.TYPE_SOA,recursion,false);
    for (int i=0; i < rrs.answer.size(); i++) {
      ResourceRecord rr=(ResourceRecord)rrs.answer.elementAt(i);
      if (rr.getType() == ResourceRecord.TYPE_SOA) {
        return rr;
      }
    }
    return null;
  }
  private String[] findNameServers(  DnsName zone,  boolean recursion) throws NamingException {
    ResourceRecords rrs=query(zone,ResourceRecord.CLASS_INTERNET,ResourceRecord.TYPE_NS,recursion,false);
    String[] ns=new String[rrs.answer.size()];
    for (int i=0; i < ns.length; i++) {
      ResourceRecord rr=(ResourceRecord)rrs.answer.elementAt(i);
      if (rr.getType() != ResourceRecord.TYPE_NS) {
        throw new CommunicationException("Corrupted DNS message");
      }
      ns[i]=(String)rr.getRdata();
      ns[i]=ns[i].substring(0,ns[i].length() - 1);
    }
    return ns;
  }
}
