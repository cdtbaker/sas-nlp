package sun.security.krb5.internal.ktab;
import sun.security.krb5.*;
import sun.security.krb5.internal.*;
import sun.security.krb5.internal.crypto.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;
/** 
 * This class represents key table. The key table functions deal with storing
 * and retrieving service keys for use in authentication exchanges.
 * A KeyTab object is always constructed, if the file specified does not
 * exist, it's still valid but empty. If there is an I/O error or file format
 * error, it's invalid.
 * The class is immutable on the read side (the write side is only used by
 * the ktab tool).
 * @author Yanni Zhang
 */
public class KeyTab implements KeyTabConstants {
  private static final boolean DEBUG=Krb5.DEBUG;
  private static String defaultTabName=null;
  private static Map<String,KeyTab> map=new HashMap<>();
  private boolean isMissing=false;
  private boolean isValid=true;
  private final String tabName;
  private long lastModified;
  private int kt_vno;
  private Vector<KeyTabEntry> entries=new Vector<>();
  /** 
 * Constructs a KeyTab object.
 * If there is any I/O error or format errot during the loading, the
 * isValid flag is set to false, and all half-read entries are dismissed.
 * @param filename path name for the keytab file, must not be null
 */
  private KeyTab(  String filename){
    tabName=filename;
    try {
      lastModified=new File(tabName).lastModified();
      try {
        load(kis);
      }
     }
 catch (    FileNotFoundException e) {
      entries.clear();
      isMissing=true;
    }
catch (    Exception ioe) {
      entries.clear();
      isValid=false;
    }
  }
  /** 
 * Read a keytab file. Returns a new object and save it into cache when
 * new content (modified since last read) is available. If keytab file is
 * invalid, the old object will be returned. This is a safeguard for
 * partial-written keytab files or non-stable network. Please note that
 * a missing keytab is valid, which is equivalent to an empty keytab.
 * @param s file name of keytab, must not be null
 * @return the keytab object, can be invalid, but never null.
 */
  private synchronized static KeyTab getInstance0(  String s){
    long lm=new File(s).lastModified();
    KeyTab old=map.get(s);
    if (old != null && old.isValid() && old.lastModified == lm) {
      return old;
    }
    KeyTab ktab=new KeyTab(s);
    if (ktab.isValid()) {
      map.put(s,ktab);
      return ktab;
    }
 else     if (old != null) {
      return old;
    }
 else {
      return ktab;
    }
  }
  /** 
 * Gets a KeyTab object.
 * @param s the key tab file name.
 * @return the KeyTab object, never null.
 */
  public static KeyTab getInstance(  String s){
    if (s == null) {
      return getInstance();
    }
 else {
      return getInstance0(s);
    }
  }
  /** 
 * Gets a KeyTab object.
 * @param file the key tab file.
 * @return the KeyTab object, never null.
 */
  public static KeyTab getInstance(  File file){
    if (file == null) {
      return getInstance();
    }
 else {
      return getInstance0(file.getPath());
    }
  }
  /** 
 * Gets the default KeyTab object.
 * @return the KeyTab object, never null.
 */
  public static KeyTab getInstance(){
    return getInstance(getDefaultTabName());
  }
  public boolean isMissing(){
    return isMissing;
  }
  public boolean isValid(){
    return isValid;
  }
  /** 
 * The location of keytab file will be read from the configuration file
 * If it is not specified, consider user.home as the keytab file's
 * default location.
 * @return never null
 */
  private static String getDefaultTabName(){
    if (defaultTabName != null) {
      return defaultTabName;
    }
 else {
      String kname=null;
      try {
        String keytab_names=Config.getInstance().getDefault("default_keytab_name","libdefaults");
        if (keytab_names != null) {
          StringTokenizer st=new StringTokenizer(keytab_names," ");
          while (st.hasMoreTokens()) {
            kname=parse(st.nextToken());
            if (new File(kname).exists()) {
              break;
            }
          }
        }
      }
 catch (      KrbException e) {
        kname=null;
      }
      if (kname == null) {
        String user_home=java.security.AccessController.doPrivileged(new sun.security.action.GetPropertyAction("user.home"));
        if (user_home == null) {
          user_home=java.security.AccessController.doPrivileged(new sun.security.action.GetPropertyAction("user.dir"));
        }
        kname=user_home + File.separator + "krb5.keytab";
      }
      defaultTabName=kname;
      return kname;
    }
  }
  /** 
 * Parses some common keytab name formats
 * @param name never null
 * @return never null
 */
  private static String parse(  String name){
    String kname;
    if ((name.length() >= 5) && (name.substring(0,5).equalsIgnoreCase("FILE:"))) {
      kname=name.substring(5);
    }
 else     if ((name.length() >= 9) && (name.substring(0,9).equalsIgnoreCase("ANY:FILE:"))) {
      kname=name.substring(9);
    }
 else     if ((name.length() >= 7) && (name.substring(0,7).equalsIgnoreCase("SRVTAB:"))) {
      kname=name.substring(7);
    }
 else     kname=name;
    return kname;
  }
  private void load(  KeyTabInputStream kis) throws IOException, RealmException {
    entries.clear();
    kt_vno=kis.readVersion();
    if (kt_vno == KRB5_KT_VNO_1) {
      kis.setNativeByteOrder();
    }
    int entryLength=0;
    KeyTabEntry entry;
    while (kis.available() > 0) {
      entryLength=kis.readEntryLength();
      entry=kis.readEntry(entryLength,kt_vno);
      if (DEBUG) {
        System.out.println(">>> KeyTab: load() entry length: " + entryLength + "; type: "+ (entry != null ? entry.keyType : 0));
      }
      if (entry != null)       entries.addElement(entry);
    }
  }
  /** 
 * Reads all keys for a service from the keytab file that have
 * etypes that have been configured for use. If there are multiple
 * keys with same etype, the one with the highest kvno is returned.
 * @param service the PrincipalName of the requested service
 * @return an array containing all the service keys, never null
 */
  public EncryptionKey[] readServiceKeys(  PrincipalName service){
    KeyTabEntry entry;
    EncryptionKey key;
    int size=entries.size();
    ArrayList<EncryptionKey> keys=new ArrayList<>(size);
    for (int i=size - 1; i >= 0; i--) {
      entry=entries.elementAt(i);
      if (entry.service.match(service)) {
        if (EType.isSupported(entry.keyType)) {
          key=new EncryptionKey(entry.keyblock,entry.keyType,new Integer(entry.keyVersion));
          keys.add(key);
          if (DEBUG) {
            System.out.println("Added key: " + entry.keyType + "version: "+ entry.keyVersion);
          }
        }
 else         if (DEBUG) {
          System.out.println("Found unsupported keytype (" + entry.keyType + ") for "+ service);
        }
      }
    }
    size=keys.size();
    EncryptionKey[] retVal=keys.toArray(new EncryptionKey[size]);
    if (DEBUG) {
      System.out.println("Ordering keys wrt default_tkt_enctypes list");
    }
    final int[] etypes=EType.getDefaults("default_tkt_enctypes");
    Arrays.sort(retVal,new Comparator<EncryptionKey>(){
      @Override public int compare(      EncryptionKey o1,      EncryptionKey o2){
        if (etypes != null) {
          int o1EType=o1.getEType();
          int o2EType=o2.getEType();
          if (o1EType != o2EType) {
            for (int i=0; i < etypes.length; i++) {
              if (etypes[i] == o1EType) {
                return -1;
              }
 else               if (etypes[i] == o2EType) {
                return 1;
              }
            }
          }
        }
        return o2.getKeyVersionNumber().intValue() - o1.getKeyVersionNumber().intValue();
      }
    }
);
    return retVal;
  }
  /** 
 * Searches for the service entry in the keytab file.
 * The etype of the key must be one that has been configured
 * to be used.
 * @param service the PrincipalName of the requested service.
 * @return true if the entry is found, otherwise, return false.
 */
  public boolean findServiceEntry(  PrincipalName service){
    KeyTabEntry entry;
    for (int i=0; i < entries.size(); i++) {
      entry=entries.elementAt(i);
      if (entry.service.match(service)) {
        if (EType.isSupported(entry.keyType)) {
          return true;
        }
 else         if (DEBUG) {
          System.out.println("Found unsupported keytype (" + entry.keyType + ") for "+ service);
        }
      }
    }
    return false;
  }
  public String tabName(){
    return tabName;
  }
  /** 
 * Adds a new entry in the key table.
 * @param service the service which will have a new entry in the key table.
 * @param psswd the password which generates the key.
 * @param kvno the kvno to use, -1 means automatic increasing
 * @param append false if entries with old kvno would be removed.
 * Note: if kvno is not -1, entries with the same kvno are always removed
 */
  public void addEntry(  PrincipalName service,  char[] psswd,  int kvno,  boolean append) throws KrbException {
    EncryptionKey[] encKeys=EncryptionKey.acquireSecretKeys(psswd,service.getSalt());
    int maxKvno=0;
    for (int i=entries.size() - 1; i >= 0; i--) {
      KeyTabEntry e=entries.get(i);
      if (e.service.match(service)) {
        if (e.keyVersion > maxKvno) {
          maxKvno=e.keyVersion;
        }
        if (!append || e.keyVersion == kvno) {
          entries.removeElementAt(i);
        }
      }
    }
    if (kvno == -1) {
      kvno=maxKvno + 1;
    }
    for (int i=0; encKeys != null && i < encKeys.length; i++) {
      int keyType=encKeys[i].getEType();
      byte[] keyValue=encKeys[i].getBytes();
      KeyTabEntry newEntry=new KeyTabEntry(service,service.getRealm(),new KerberosTime(System.currentTimeMillis()),kvno,keyType,keyValue);
      entries.addElement(newEntry);
    }
  }
  /** 
 * Gets the list of service entries in key table.
 * @return array of <code>KeyTabEntry</code>.
 */
  public KeyTabEntry[] getEntries(){
    KeyTabEntry[] kentries=new KeyTabEntry[entries.size()];
    for (int i=0; i < kentries.length; i++) {
      kentries[i]=entries.elementAt(i);
    }
    return kentries;
  }
  /** 
 * Creates a new default key table.
 */
  public synchronized static KeyTab create() throws IOException, RealmException {
    String dname=getDefaultTabName();
    return create(dname);
  }
  /** 
 * Creates a new default key table.
 */
  public synchronized static KeyTab create(  String name) throws IOException, RealmException {
    try {
      kos.writeVersion(KRB5_KT_VNO);
    }
     return new KeyTab(name);
  }
  /** 
 * Saves the file at the directory.
 */
  public synchronized void save() throws IOException {
    try {
      kos.writeVersion(kt_vno);
      for (int i=0; i < entries.size(); i++) {
        kos.writeEntry(entries.elementAt(i));
      }
    }
   }
  /** 
 * Removes entries from the key table.
 * @param service the service <code>PrincipalName</code>.
 * @param etype the etype to match, remove all if -1
 * @param kvno what kvno to remove, -1 for all, -2 for old
 * @return the number of entries deleted
 */
  public int deleteEntries(  PrincipalName service,  int etype,  int kvno){
    int count=0;
    Map<Integer,Integer> highest=new HashMap<>();
    for (int i=entries.size() - 1; i >= 0; i--) {
      KeyTabEntry e=entries.get(i);
      if (service.match(e.getService())) {
        if (etype == -1 || e.keyType == etype) {
          if (kvno == -2) {
            if (highest.containsKey(e.keyType)) {
              int n=highest.get(e.keyType);
              if (e.keyVersion > n) {
                highest.put(e.keyType,e.keyVersion);
              }
            }
 else {
              highest.put(e.keyType,e.keyVersion);
            }
          }
 else           if (kvno == -1 || e.keyVersion == kvno) {
            entries.removeElementAt(i);
            count++;
          }
        }
      }
    }
    if (kvno == -2) {
      for (int i=entries.size() - 1; i >= 0; i--) {
        KeyTabEntry e=entries.get(i);
        if (service.match(e.getService())) {
          if (etype == -1 || e.keyType == etype) {
            int n=highest.get(e.keyType);
            if (e.keyVersion != n) {
              entries.removeElementAt(i);
              count++;
            }
          }
        }
      }
    }
    return count;
  }
  /** 
 * Creates key table file version.
 * @param file the key table file.
 * @exception IOException.
 */
  public synchronized void createVersion(  File file) throws IOException {
    try {
      kos.write16(KRB5_KT_VNO);
    }
   }
}
