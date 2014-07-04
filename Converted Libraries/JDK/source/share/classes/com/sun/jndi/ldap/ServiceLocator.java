package com.sun.jndi.ldap;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.List;
import javax.naming.*;
import javax.naming.directory.*;
import javax.naming.spi.NamingManager;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import com.sun.jndi.ldap.LdapURL;
/** 
 * This class discovers the location of LDAP services by querying DNS.
 * See http://www.ietf.org/internet-drafts/draft-ietf-ldapext-locate-07.txt
 */
class ServiceLocator {
  private static final String SRV_RR="SRV";
  private static final String[] SRV_RR_ATTR=new String[]{SRV_RR};
  private static final Random random=new Random();
  private ServiceLocator(){
  }
  /** 
 * Maps a distinguished name (RFC 2253) to a fully qualified domain name.
 * Processes a sequence of RDNs having a DC attribute.
 * The special RDN "DC=." denotes the root of the domain tree.
 * Multi-valued RDNs, non-DC attributes, binary-valued attributes and the
 * RDN "DC=." all reset the domain name and processing continues.
 * @param dn A string distinguished name (RFC 2253).
 * @return A domain name or null if none can be derived.
 * @throw InvalidNameException If the distinugished name is invalid.
 */
  static String mapDnToDomainName(  String dn) throws InvalidNameException {
    if (dn == null) {
      return null;
    }
    StringBuffer domain=new StringBuffer();
    LdapName ldapName=new LdapName(dn);
    List rdnList=ldapName.getRdns();
    for (int i=rdnList.size() - 1; i >= 0; i--) {
      Rdn rdn=(Rdn)rdnList.get(i);
      if ((rdn.size() == 1) && ("dc".equalsIgnoreCase(rdn.getType()))) {
        Object attrval=rdn.getValue();
        if (attrval instanceof String) {
          if (attrval.equals(".") || (domain.length() == 1 && domain.charAt(0) == '.')) {
            domain.setLength(0);
          }
          if (domain.length() > 0) {
            domain.append('.');
          }
          domain.append(attrval);
        }
 else {
          domain.setLength(0);
        }
      }
 else {
        domain.setLength(0);
      }
    }
    return (domain.length() != 0) ? domain.toString() : null;
  }
  /** 
 * Locates the LDAP service for a given domain.
 * Queries DNS for a list of LDAP Service Location Records (SRV) for a
 * given domain name.
 * @param domainName A string domain name.
 * @param environment The possibly null environment of the context.
 * @return An ordered list of hostports for the LDAP service or null if
 * the service has not been located.
 */
  static String[] getLdapService(  String domainName,  Hashtable environment){
    if (domainName == null || domainName.length() == 0) {
      return null;
    }
    String dnsUrl="dns:///_ldap._tcp." + domainName;
    String[] hostports=null;
    try {
      Context ctx=NamingManager.getURLContext("dns",environment);
      if (!(ctx instanceof DirContext)) {
        return null;
      }
      Attributes attrs=((DirContext)ctx).getAttributes(dnsUrl,SRV_RR_ATTR);
      Attribute attr;
      if (attrs != null && ((attr=attrs.get(SRV_RR)) != null)) {
        int numValues=attr.size();
        int numRecords=0;
        SrvRecord[] srvRecords=new SrvRecord[numValues];
        int i=0;
        int j=0;
        while (i < numValues) {
          try {
            srvRecords[j]=new SrvRecord((String)attr.get(i));
            j++;
          }
 catch (          Exception e) {
          }
          i++;
        }
        numRecords=j;
        if (numRecords < numValues) {
          SrvRecord[] trimmed=new SrvRecord[numRecords];
          System.arraycopy(srvRecords,0,trimmed,0,numRecords);
          srvRecords=trimmed;
        }
        if (numRecords > 1) {
          Arrays.sort(srvRecords);
        }
        hostports=extractHostports(srvRecords);
      }
    }
 catch (    NamingException e) {
    }
    return hostports;
  }
  /** 
 * Extract hosts and port numbers from a list of SRV records.
 * An array of hostports is returned or null if none were found.
 */
  private static String[] extractHostports(  SrvRecord[] srvRecords){
    String[] hostports=null;
    int head=0;
    int tail=0;
    int sublistLength=0;
    int k=0;
    for (int i=0; i < srvRecords.length; i++) {
      if (hostports == null) {
        hostports=new String[srvRecords.length];
      }
      head=i;
      while (i < srvRecords.length - 1 && srvRecords[i].priority == srvRecords[i + 1].priority) {
        i++;
      }
      tail=i;
      sublistLength=(tail - head) + 1;
      for (int j=0; j < sublistLength; j++) {
        hostports[k++]=selectHostport(srvRecords,head,tail);
      }
    }
    return hostports;
  }
  private static String selectHostport(  SrvRecord[] srvRecords,  int head,  int tail){
    if (head == tail) {
      return srvRecords[head].hostport;
    }
    int sum=0;
    for (int i=head; i <= tail; i++) {
      if (srvRecords[i] != null) {
        sum+=srvRecords[i].weight;
        srvRecords[i].sum=sum;
      }
    }
    String hostport=null;
    int target=(sum == 0 ? 0 : random.nextInt(sum + 1));
    for (int i=head; i <= tail; i++) {
      if (srvRecords[i] != null && srvRecords[i].sum >= target) {
        hostport=srvRecords[i].hostport;
        srvRecords[i]=null;
        break;
      }
    }
    return hostport;
  }
  /** 
 * This class holds a DNS service (SRV) record.
 * See http://www.ietf.org/rfc/rfc2782.txt
 */
static class SrvRecord implements Comparable {
    int priority;
    int weight;
    int sum;
    String hostport;
    /** 
 * Creates a service record object from a string record.
 * DNS supplies the string record in the following format:
 * <pre>
 * <Priority> " " <Weight> " " <Port> " " <Host>
 * </pre>
 */
    SrvRecord(    String srvRecord) throws Exception {
      StringTokenizer tokenizer=new StringTokenizer(srvRecord," ");
      String port;
      if (tokenizer.countTokens() == 4) {
        priority=Integer.parseInt(tokenizer.nextToken());
        weight=Integer.parseInt(tokenizer.nextToken());
        port=tokenizer.nextToken();
        hostport=tokenizer.nextToken() + ":" + port;
      }
 else {
        throw new IllegalArgumentException();
      }
    }
    public int compareTo(    Object o){
      SrvRecord that=(SrvRecord)o;
      if (priority > that.priority) {
        return 1;
      }
 else       if (priority < that.priority) {
        return -1;
      }
 else       if (weight == 0 && that.weight != 0) {
        return -1;
      }
 else       if (weight != 0 && that.weight == 0) {
        return 1;
      }
 else {
        return 0;
      }
    }
  }
}
