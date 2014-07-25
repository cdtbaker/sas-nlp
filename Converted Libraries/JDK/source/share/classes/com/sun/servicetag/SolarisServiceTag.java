package com.sun.servicetag;
import java.io.IOException;
import java.util.Set;
/** 
 * Utility class to obtain the service tag for the Solaris Operating System.
 */
class SolarisServiceTag {
  private final static String[] SolarisProductURNs=new String[]{"urn:uuid:a7a38948-2bd5-11d6-98ce-9d3ac1c0cfd7","urn:uuid:4f82caac-36f3-11d6-866b-85f428ef944e","urn:uuid:a19de03b-48bc-11d9-9607-080020a9ed93","urn:uuid:4c35c45b-4955-11d9-9607-080020a9ed93","urn:uuid:5005588c-36f3-11d6-9cec-fc96f718e113","urn:uuid:6df19e63-7ef5-11db-a4bd-080020a9ed93"};
  /** 
 * Returns null if not found.
 * There is only one service tag for the operating system.
 */
  static ServiceTag getServiceTag() throws IOException {
    if (Registry.isSupported()) {
      Registry streg=Registry.getSystemRegistry();
      for (      String parentURN : SolarisProductURNs) {
        Set<ServiceTag> instances=streg.findServiceTags(parentURN);
        for (        ServiceTag st : instances) {
          return st;
        }
      }
    }
    return null;
  }
}
