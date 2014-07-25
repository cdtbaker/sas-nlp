package com.sun.jndi.ldap;
import javax.naming.NamingEnumeration;
interface ReferralEnumeration extends NamingEnumeration {
  void appendUnprocessedReferrals(  LdapReferralException ex);
}
