package sun.security.provider;
import java.io.*;
import java.lang.RuntimePermission;
import java.net.SocketPermission;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Vector;
import java.util.StringTokenizer;
import java.text.MessageFormat;
import javax.security.auth.x500.X500Principal;
import java.security.GeneralSecurityException;
import sun.security.util.Debug;
import sun.security.util.PropertyExpander;
import sun.security.util.ResourcesMgr;
/** 
 * The policy for a Java runtime (specifying
 * which permissions are available for code from various principals)
 * is represented as a separate
 * persistent configuration.  The configuration may be stored as a
 * flat ASCII file, as a serialized binary file of
 * the Policy class, or as a database. <p>
 * <p>The Java runtime creates one global Policy object, which is used to
 * represent the static policy configuration file.  It is consulted by
 * a ProtectionDomain when the protection domain initializes its set of
 * permissions. <p>
 * <p>The Policy <code>init</code> method parses the policy
 * configuration file, and then
 * populates the Policy object.  The Policy object is agnostic in that
 * it is not involved in making policy decisions.  It is merely the
 * Java runtime representation of the persistent policy configuration
 * file. <p>
 * <p>When a protection domain needs to initialize its set of
 * permissions, it executes code such as the following
 * to ask the global Policy object to populate a
 * Permissions object with the appropriate permissions:
 * <pre>
 * policy = Policy.getPolicy();
 * Permissions perms = policy.getPermissions(protectiondomain)
 * </pre>
 * <p>The protection domain contains CodeSource
 * object, which encapsulates its codebase (URL) and public key attributes.
 * It also contains the principals associated with the domain.
 * The Policy object evaluates the global policy in light of who the
 * principal is and what the code source is and returns an appropriate
 * Permissions object.
 * @author Roland Schemers
 * @author Ram Marti
 * @since 1.2
 */
public class PolicyParser {
  public static final String REPLACE_NAME="PolicyParser.REPLACE_NAME";
  private static final String EXTDIRS_PROPERTY="java.ext.dirs";
  private static final String OLD_EXTDIRS_EXPANSION="${" + EXTDIRS_PROPERTY + "}";
  static final String EXTDIRS_EXPANSION="${{" + EXTDIRS_PROPERTY + "}}";
  private Vector<GrantEntry> grantEntries;
  private static final Debug debug=Debug.getInstance("parser","\t[Policy Parser]");
  private StreamTokenizer st;
  private int lookahead;
  private boolean expandProp=false;
  private String keyStoreUrlString=null;
  private String keyStoreType=null;
  private String keyStoreProvider=null;
  private String storePassURL=null;
  private String expand(  String value) throws PropertyExpander.ExpandException {
    return expand(value,false);
  }
  private String expand(  String value,  boolean encodeURL) throws PropertyExpander.ExpandException {
    if (!expandProp) {
      return value;
    }
 else {
      return PropertyExpander.expand(value,encodeURL);
    }
  }
  /** 
 * Creates a PolicyParser object.
 */
  public PolicyParser(){
    grantEntries=new Vector<GrantEntry>();
  }
  public PolicyParser(  boolean expandProp){
    this();
    this.expandProp=expandProp;
  }
  /** 
 * Reads a policy configuration into the Policy object using a
 * Reader object. <p>
 * @param policy the policy Reader object.
 * @exception ParsingException if the policy configuration contains
 * a syntax error.
 * @exception IOException if an error occurs while reading the policy
 * configuration.
 */
  public void read(  Reader policy) throws ParsingException, IOException {
    if (!(policy instanceof BufferedReader)) {
      policy=new BufferedReader(policy);
    }
    st=new StreamTokenizer(policy);
    st.resetSyntax();
    st.wordChars('a','z');
    st.wordChars('A','Z');
    st.wordChars('.','.');
    st.wordChars('0','9');
    st.wordChars('_','_');
    st.wordChars('$','$');
    st.wordChars(128 + 32,255);
    st.whitespaceChars(0,' ');
    st.commentChar('/');
    st.quoteChar('\'');
    st.quoteChar('"');
    st.lowerCaseMode(false);
    st.ordinaryChar('/');
    st.slashSlashComments(true);
    st.slashStarComments(true);
    lookahead=st.nextToken();
    while (lookahead != StreamTokenizer.TT_EOF) {
      if (peek("grant")) {
        GrantEntry ge=parseGrantEntry();
        if (ge != null)         add(ge);
      }
 else       if (peek("keystore") && keyStoreUrlString == null) {
        parseKeyStoreEntry();
      }
 else       if (peek("keystorePasswordURL") && storePassURL == null) {
        parseStorePassURL();
      }
 else {
      }
      match(";");
    }
    if (keyStoreUrlString == null && storePassURL != null) {
      throw new ParsingException(ResourcesMgr.getString("keystorePasswordURL.can.not.be.specified.without.also.specifying.keystore"));
    }
  }
  public void add(  GrantEntry ge){
    grantEntries.addElement(ge);
  }
  public void replace(  GrantEntry origGe,  GrantEntry newGe){
    grantEntries.setElementAt(newGe,grantEntries.indexOf(origGe));
  }
  public boolean remove(  GrantEntry ge){
    return grantEntries.removeElement(ge);
  }
  /** 
 * Returns the (possibly expanded) keystore location, or null if the
 * expansion fails.
 */
  public String getKeyStoreUrl(){
    try {
      if (keyStoreUrlString != null && keyStoreUrlString.length() != 0) {
        return expand(keyStoreUrlString,true).replace(File.separatorChar,'/');
      }
    }
 catch (    PropertyExpander.ExpandException peee) {
      if (debug != null) {
        debug.println(peee.toString());
      }
      return null;
    }
    return null;
  }
  public void setKeyStoreUrl(  String url){
    keyStoreUrlString=url;
  }
  public String getKeyStoreType(){
    return keyStoreType;
  }
  public void setKeyStoreType(  String type){
    keyStoreType=type;
  }
  public String getKeyStoreProvider(){
    return keyStoreProvider;
  }
  public void setKeyStoreProvider(  String provider){
    keyStoreProvider=provider;
  }
  public String getStorePassURL(){
    try {
      if (storePassURL != null && storePassURL.length() != 0) {
        return expand(storePassURL,true).replace(File.separatorChar,'/');
      }
    }
 catch (    PropertyExpander.ExpandException peee) {
      if (debug != null) {
        debug.println(peee.toString());
      }
      return null;
    }
    return null;
  }
  public void setStorePassURL(  String storePassURL){
    this.storePassURL=storePassURL;
  }
  /** 
 * Enumerate all the entries in the global policy object.
 * This method is used by policy admin tools.   The tools
 * should use the Enumeration methods on the returned object
 * to fetch the elements sequentially.
 */
  public Enumeration<GrantEntry> grantElements(){
    return grantEntries.elements();
  }
  /** 
 * write out the policy
 */
  public void write(  Writer policy){
    PrintWriter out=new PrintWriter(new BufferedWriter(policy));
    Enumeration<GrantEntry> enum_=grantElements();
    out.println("/* AUTOMATICALLY GENERATED ON " + (new java.util.Date()) + "*/");
    out.println("/* DO NOT EDIT */");
    out.println();
    if (keyStoreUrlString != null) {
      writeKeyStoreEntry(out);
    }
    if (storePassURL != null) {
      writeStorePassURL(out);
    }
    while (enum_.hasMoreElements()) {
      GrantEntry ge=enum_.nextElement();
      ge.write(out);
      out.println();
    }
    out.flush();
  }
  /** 
 * parses a keystore entry
 */
  private void parseKeyStoreEntry() throws ParsingException, IOException {
    match("keystore");
    keyStoreUrlString=match("quoted string");
    if (!peek(",")) {
      return;
    }
    match(",");
    if (peek("\"")) {
      keyStoreType=match("quoted string");
    }
 else {
      throw new ParsingException(st.lineno(),ResourcesMgr.getString("expected.keystore.type"));
    }
    if (!peek(",")) {
      return;
    }
    match(",");
    if (peek("\"")) {
      keyStoreProvider=match("quoted string");
    }
 else {
      throw new ParsingException(st.lineno(),ResourcesMgr.getString("expected.keystore.provider"));
    }
  }
  private void parseStorePassURL() throws ParsingException, IOException {
    match("keyStorePasswordURL");
    storePassURL=match("quoted string");
  }
  /** 
 * writes the (unexpanded) keystore entry
 */
  private void writeKeyStoreEntry(  PrintWriter out){
    out.print("keystore \"");
    out.print(keyStoreUrlString);
    out.print('"');
    if (keyStoreType != null && keyStoreType.length() > 0)     out.print(", \"" + keyStoreType + "\"");
    if (keyStoreProvider != null && keyStoreProvider.length() > 0)     out.print(", \"" + keyStoreProvider + "\"");
    out.println(";");
    out.println();
  }
  private void writeStorePassURL(  PrintWriter out){
    out.print("keystorePasswordURL \"");
    out.print(storePassURL);
    out.print('"');
    out.println(";");
    out.println();
  }
  /** 
 * parse a Grant entry
 */
  private GrantEntry parseGrantEntry() throws ParsingException, IOException {
    GrantEntry e=new GrantEntry();
    LinkedList<PrincipalEntry> principals=null;
    boolean ignoreEntry=false;
    match("grant");
    while (!peek("{")) {
      if (peekAndMatch("Codebase")) {
        if (e.codeBase != null)         throw new ParsingException(st.lineno(),ResourcesMgr.getString("multiple.Codebase.expressions"));
        e.codeBase=match("quoted string");
        peekAndMatch(",");
      }
 else       if (peekAndMatch("SignedBy")) {
        if (e.signedBy != null)         throw new ParsingException(st.lineno(),ResourcesMgr.getString("multiple.SignedBy.expressions"));
        e.signedBy=match("quoted string");
        StringTokenizer aliases=new StringTokenizer(e.signedBy,",",true);
        int actr=0;
        int cctr=0;
        while (aliases.hasMoreTokens()) {
          String alias=aliases.nextToken().trim();
          if (alias.equals(","))           cctr++;
 else           if (alias.length() > 0)           actr++;
        }
        if (actr <= cctr)         throw new ParsingException(st.lineno(),ResourcesMgr.getString("SignedBy.has.empty.alias"));
        peekAndMatch(",");
      }
 else       if (peekAndMatch("Principal")) {
        if (principals == null) {
          principals=new LinkedList<PrincipalEntry>();
        }
        String principalClass;
        String principalName;
        if (peek("\"")) {
          principalClass=REPLACE_NAME;
          principalName=match("principal type");
        }
 else {
          if (peek("*")) {
            match("*");
            principalClass=PrincipalEntry.WILDCARD_CLASS;
          }
 else {
            principalClass=match("principal type");
          }
          if (peek("*")) {
            match("*");
            principalName=PrincipalEntry.WILDCARD_NAME;
          }
 else {
            principalName=match("quoted string");
          }
          if (principalClass.equals(PrincipalEntry.WILDCARD_CLASS) && !principalName.equals(PrincipalEntry.WILDCARD_NAME)) {
            if (debug != null) {
              debug.println("disallowing principal that " + "has WILDCARD class but no WILDCARD name");
            }
            throw new ParsingException(st.lineno(),ResourcesMgr.getString("can.not.specify.Principal.with.a.wildcard.class.without.a.wildcard.name"));
          }
        }
        try {
          principalName=expand(principalName);
          if (principalClass.equals("javax.security.auth.x500.X500Principal") && !principalName.equals(PrincipalEntry.WILDCARD_NAME)) {
            X500Principal p=new X500Principal((new X500Principal(principalName)).toString());
            principalName=p.getName();
          }
          principals.add(new PrincipalEntry(principalClass,principalName));
        }
 catch (        PropertyExpander.ExpandException peee) {
          if (debug != null) {
            debug.println("principal name expansion failed: " + principalName);
          }
          ignoreEntry=true;
        }
        peekAndMatch(",");
      }
 else {
        throw new ParsingException(st.lineno(),ResourcesMgr.getString("expected.codeBase.or.SignedBy.or.Principal"));
      }
    }
    if (principals != null)     e.principals=principals;
    match("{");
    while (!peek("}")) {
      if (peek("Permission")) {
        try {
          PermissionEntry pe=parsePermissionEntry();
          e.add(pe);
        }
 catch (        PropertyExpander.ExpandException peee) {
          if (debug != null) {
            debug.println(peee.toString());
          }
          skipEntry();
        }
        match(";");
      }
 else {
        throw new ParsingException(st.lineno(),ResourcesMgr.getString("expected.permission.entry"));
      }
    }
    match("}");
    try {
      if (e.signedBy != null)       e.signedBy=expand(e.signedBy);
      if (e.codeBase != null) {
        if (e.codeBase.equals(OLD_EXTDIRS_EXPANSION)) {
          e.codeBase=EXTDIRS_EXPANSION;
        }
        int es;
        if ((es=e.codeBase.indexOf(EXTDIRS_EXPANSION)) < 0) {
          e.codeBase=expand(e.codeBase,true).replace(File.separatorChar,'/');
        }
 else {
          String[] extDirs=parseExtDirs(e.codeBase,es);
          if (extDirs != null && extDirs.length > 0) {
            for (int i=0; i < extDirs.length; i++) {
              GrantEntry newGe=(GrantEntry)e.clone();
              newGe.codeBase=extDirs[i];
              add(newGe);
              if (debug != null) {
                debug.println("creating policy entry for " + "expanded java.ext.dirs path:\n\t\t" + extDirs[i]);
              }
            }
          }
          ignoreEntry=true;
        }
      }
    }
 catch (    PropertyExpander.ExpandException peee) {
      if (debug != null) {
        debug.println(peee.toString());
      }
      return null;
    }
    return (ignoreEntry == true) ? null : e;
  }
  /** 
 * parse a Permission entry
 */
  private PermissionEntry parsePermissionEntry() throws ParsingException, IOException, PropertyExpander.ExpandException {
    PermissionEntry e=new PermissionEntry();
    match("Permission");
    e.permission=match("permission type");
    if (peek("\"")) {
      e.name=expand(match("quoted string"));
    }
    if (!peek(",")) {
      return e;
    }
    match(",");
    if (peek("\"")) {
      e.action=expand(match("quoted string"));
      if (!peek(",")) {
        return e;
      }
      match(",");
    }
    if (peekAndMatch("SignedBy")) {
      e.signedBy=expand(match("quoted string"));
    }
    return e;
  }
  static String[] parseExtDirs(  String codebase,  int start){
    String s=System.getProperty(EXTDIRS_PROPERTY);
    String globalPrefix=(start > 0 ? codebase.substring(0,start) : "file:");
    int end=start + EXTDIRS_EXPANSION.length();
    String globalSuffix=(end < codebase.length() ? codebase.substring(end) : (String)null);
    String[] dirs=null;
    String localSuffix;
    if (s != null) {
      StringTokenizer st=new StringTokenizer(s,File.pathSeparator);
      int count=st.countTokens();
      dirs=new String[count];
      for (int i=0; i < count; i++) {
        File file=new File(st.nextToken());
        dirs[i]=sun.net.www.ParseUtil.encodePath(file.getAbsolutePath());
        if (!dirs[i].startsWith("/")) {
          dirs[i]="/" + dirs[i];
        }
        localSuffix=(globalSuffix == null ? (dirs[i].endsWith("/") ? "*" : "/*") : globalSuffix);
        dirs[i]=globalPrefix + dirs[i] + localSuffix;
      }
    }
    return dirs;
  }
  private boolean peekAndMatch(  String expect) throws ParsingException, IOException {
    if (peek(expect)) {
      match(expect);
      return true;
    }
 else {
      return false;
    }
  }
  private boolean peek(  String expect){
    boolean found=false;
switch (lookahead) {
case StreamTokenizer.TT_WORD:
      if (expect.equalsIgnoreCase(st.sval))       found=true;
    break;
case ',':
  if (expect.equalsIgnoreCase(","))   found=true;
break;
case '{':
if (expect.equalsIgnoreCase("{")) found=true;
break;
case '}':
if (expect.equalsIgnoreCase("}")) found=true;
break;
case '"':
if (expect.equalsIgnoreCase("\"")) found=true;
break;
case '*':
if (expect.equalsIgnoreCase("*")) found=true;
break;
default :
}
return found;
}
private String match(String expect) throws ParsingException, IOException {
String value=null;
switch (lookahead) {
case StreamTokenizer.TT_NUMBER:
throw new ParsingException(st.lineno(),expect,ResourcesMgr.getString("number.") + String.valueOf(st.nval));
case StreamTokenizer.TT_EOF:
MessageFormat form=new MessageFormat(ResourcesMgr.getString("expected.expect.read.end.of.file."));
Object[] source={expect};
throw new ParsingException(form.format(source));
case StreamTokenizer.TT_WORD:
if (expect.equalsIgnoreCase(st.sval)) {
lookahead=st.nextToken();
}
 else if (expect.equalsIgnoreCase("permission type")) {
value=st.sval;
lookahead=st.nextToken();
}
 else if (expect.equalsIgnoreCase("principal type")) {
value=st.sval;
lookahead=st.nextToken();
}
 else {
throw new ParsingException(st.lineno(),expect,st.sval);
}
break;
case '"':
if (expect.equalsIgnoreCase("quoted string")) {
value=st.sval;
lookahead=st.nextToken();
}
 else if (expect.equalsIgnoreCase("permission type")) {
value=st.sval;
lookahead=st.nextToken();
}
 else if (expect.equalsIgnoreCase("principal type")) {
value=st.sval;
lookahead=st.nextToken();
}
 else {
throw new ParsingException(st.lineno(),expect,st.sval);
}
break;
case ',':
if (expect.equalsIgnoreCase(",")) lookahead=st.nextToken();
 else throw new ParsingException(st.lineno(),expect,",");
break;
case '{':
if (expect.equalsIgnoreCase("{")) lookahead=st.nextToken();
 else throw new ParsingException(st.lineno(),expect,"{");
break;
case '}':
if (expect.equalsIgnoreCase("}")) lookahead=st.nextToken();
 else throw new ParsingException(st.lineno(),expect,"}");
break;
case ';':
if (expect.equalsIgnoreCase(";")) lookahead=st.nextToken();
 else throw new ParsingException(st.lineno(),expect,";");
break;
case '*':
if (expect.equalsIgnoreCase("*")) lookahead=st.nextToken();
 else throw new ParsingException(st.lineno(),expect,"*");
break;
default :
throw new ParsingException(st.lineno(),expect,new String(new char[]{(char)lookahead}));
}
return value;
}
/** 
 * skip all tokens for this entry leaving the delimiter ";"
 * in the stream.
 */
private void skipEntry() throws ParsingException, IOException {
while (lookahead != ';') {
switch (lookahead) {
case StreamTokenizer.TT_NUMBER:
throw new ParsingException(st.lineno(),";",ResourcesMgr.getString("number.") + String.valueOf(st.nval));
case StreamTokenizer.TT_EOF:
throw new ParsingException(ResourcesMgr.getString("expected.read.end.of.file."));
default :
lookahead=st.nextToken();
}
}
}
/** 
 * Each grant entry in the policy configuration file is
 * represented by a
 * GrantEntry object.  <p>
 * <p>
 * For example, the entry
 * <pre>
 * grant signedBy "Duke" {
 * permission java.io.FilePermission "/tmp", "read,write";
 * };
 * </pre>
 * is represented internally
 * <pre>
 * pe = new PermissionEntry("java.io.FilePermission",
 * "/tmp", "read,write");
 * ge = new GrantEntry("Duke", null);
 * ge.add(pe);
 * </pre>
 * @author Roland Schemers
 * version 1.19, 05/21/98
 */
public static class GrantEntry {
public String signedBy;
public String codeBase;
public LinkedList<PrincipalEntry> principals;
public Vector<PermissionEntry> permissionEntries;
public GrantEntry(){
principals=new LinkedList<PrincipalEntry>();
permissionEntries=new Vector<PermissionEntry>();
}
public GrantEntry(String signedBy,String codeBase){
this.codeBase=codeBase;
this.signedBy=signedBy;
principals=new LinkedList<PrincipalEntry>();
permissionEntries=new Vector<PermissionEntry>();
}
public void add(PermissionEntry pe){
permissionEntries.addElement(pe);
}
public boolean remove(PrincipalEntry pe){
return principals.remove(pe);
}
public boolean remove(PermissionEntry pe){
return permissionEntries.removeElement(pe);
}
public boolean contains(PrincipalEntry pe){
return principals.contains(pe);
}
public boolean contains(PermissionEntry pe){
return permissionEntries.contains(pe);
}
/** 
 * Enumerate all the permission entries in this GrantEntry.
 */
public Enumeration<PermissionEntry> permissionElements(){
return permissionEntries.elements();
}
public void write(PrintWriter out){
out.print("grant");
if (signedBy != null) {
out.print(" signedBy \"");
out.print(signedBy);
out.print('"');
if (codeBase != null) out.print(", ");
}
if (codeBase != null) {
out.print(" codeBase \"");
out.print(codeBase);
out.print('"');
if (principals != null && principals.size() > 0) out.print(",\n");
}
if (principals != null && principals.size() > 0) {
ListIterator<PrincipalEntry> pli=principals.listIterator();
while (pli.hasNext()) {
out.print("      ");
PrincipalEntry pe=pli.next();
pe.write(out);
if (pli.hasNext()) out.print(",\n");
}
}
out.println(" {");
Enumeration<PermissionEntry> enum_=permissionEntries.elements();
while (enum_.hasMoreElements()) {
PermissionEntry pe=enum_.nextElement();
out.write("  ");
pe.write(out);
}
out.println("};");
}
public Object clone(){
GrantEntry ge=new GrantEntry();
ge.codeBase=this.codeBase;
ge.signedBy=this.signedBy;
ge.principals=new LinkedList<PrincipalEntry>(this.principals);
ge.permissionEntries=new Vector<PermissionEntry>(this.permissionEntries);
return ge;
}
}
/** 
 * Principal info (class and name) in a grant entry
 */
public static class PrincipalEntry {
public static final String WILDCARD_CLASS="WILDCARD_PRINCIPAL_CLASS";
public static final String WILDCARD_NAME="WILDCARD_PRINCIPAL_NAME";
String principalClass;
String principalName;
/** 
 * A PrincipalEntry consists of the <code>Principal</code>
 * class and <code>Principal</code> name.
 * <p>
 * @param principalClass the <code>Principal</code> class. <p>
 * @param principalName the <code>Principal</code> name. <p>
 */
public PrincipalEntry(String principalClass,String principalName){
if (principalClass == null || principalName == null) throw new NullPointerException(ResourcesMgr.getString("null.principalClass.or.principalName"));
this.principalClass=principalClass;
this.principalName=principalName;
}
public String getPrincipalClass(){
return principalClass;
}
public String getPrincipalName(){
return principalName;
}
public String getDisplayClass(){
if (principalClass.equals(WILDCARD_CLASS)) {
return "*";
}
 else if (principalClass.equals(REPLACE_NAME)) {
return "";
}
 else return principalClass;
}
public String getDisplayName(){
return getDisplayName(false);
}
public String getDisplayName(boolean addQuote){
if (principalName.equals(WILDCARD_NAME)) {
return "*";
}
 else {
if (addQuote) return "\"" + principalName + "\"";
 else return principalName;
}
}
public String toString(){
if (!principalClass.equals(REPLACE_NAME)) {
return getDisplayClass() + "/" + getDisplayName();
}
 else {
return getDisplayName();
}
}
/** 
 * Test for equality between the specified object and this object.
 * Two PrincipalEntries are equal if their PrincipalClass and
 * PrincipalName values are equal.
 * <p>
 * @param obj the object to test for equality with this object.
 * @return true if the objects are equal, false otherwise.
 */
public boolean equals(Object obj){
if (this == obj) return true;
if (!(obj instanceof PrincipalEntry)) return false;
PrincipalEntry that=(PrincipalEntry)obj;
if (this.principalClass.equals(that.principalClass) && this.principalName.equals(that.principalName)) {
return true;
}
return false;
}
/** 
 * Return a hashcode for this <code>PrincipalEntry</code>.
 * <p>
 * @return a hashcode for this <code>PrincipalEntry</code>.
 */
public int hashCode(){
return principalClass.hashCode();
}
public void write(PrintWriter out){
out.print("principal " + getDisplayClass() + " "+ getDisplayName(true));
}
}
/** 
 * Each permission entry in the policy configuration file is
 * represented by a
 * PermissionEntry object.  <p>
 * <p>
 * For example, the entry
 * <pre>
 * permission java.io.FilePermission "/tmp", "read,write";
 * </pre>
 * is represented internally
 * <pre>
 * pe = new PermissionEntry("java.io.FilePermission",
 * "/tmp", "read,write");
 * </pre>
 * @author Roland Schemers
 * version 1.19, 05/21/98
 */
public static class PermissionEntry {
public String permission;
public String name;
public String action;
public String signedBy;
public PermissionEntry(){
}
public PermissionEntry(String permission,String name,String action){
this.permission=permission;
this.name=name;
this.action=action;
}
/** 
 * Calculates a hash code value for the object.  Objects
 * which are equal will also have the same hashcode.
 */
public int hashCode(){
int retval=permission.hashCode();
if (name != null) retval^=name.hashCode();
if (action != null) retval^=action.hashCode();
return retval;
}
public boolean equals(Object obj){
if (obj == this) return true;
if (!(obj instanceof PermissionEntry)) return false;
PermissionEntry that=(PermissionEntry)obj;
if (this.permission == null) {
if (that.permission != null) return false;
}
 else {
if (!this.permission.equals(that.permission)) return false;
}
if (this.name == null) {
if (that.name != null) return false;
}
 else {
if (!this.name.equals(that.name)) return false;
}
if (this.action == null) {
if (that.action != null) return false;
}
 else {
if (!this.action.equals(that.action)) return false;
}
if (this.signedBy == null) {
if (that.signedBy != null) return false;
}
 else {
if (!this.signedBy.equals(that.signedBy)) return false;
}
return true;
}
public void write(PrintWriter out){
out.print("permission ");
out.print(permission);
if (name != null) {
out.print(" \"");
out.print(name.replaceAll("\\\\","\\\\\\\\").replaceAll("\\\"","\\\\\\\""));
out.print('"');
}
if (action != null) {
out.print(", \"");
out.print(action);
out.print('"');
}
if (signedBy != null) {
out.print(", signedBy \"");
out.print(signedBy);
out.print('"');
}
out.println(";");
}
}
public static class ParsingException extends GeneralSecurityException {
private static final long serialVersionUID=-4330692689482574072L;
private String i18nMessage;
/** 
 * Constructs a ParsingException with the specified
 * detail message. A detail message is a String that describes
 * this particular exception, which may, for example, specify which
 * algorithm is not available.
 * @param msg the detail message.
 */
public ParsingException(String msg){
super(msg);
i18nMessage=msg;
}
public ParsingException(int line,String msg){
super("line " + line + ": "+ msg);
MessageFormat form=new MessageFormat(ResourcesMgr.getString("line.number.msg"));
Object[] source={new Integer(line),msg};
i18nMessage=form.format(source);
}
public ParsingException(int line,String expect,String actual){
super("line " + line + ": expected ["+ expect+ "], found ["+ actual+ "]");
MessageFormat form=new MessageFormat(ResourcesMgr.getString("line.number.expected.expect.found.actual."));
Object[] source={new Integer(line),expect,actual};
i18nMessage=form.format(source);
}
public String getLocalizedMessage(){
return i18nMessage;
}
}
public static void main(String arg[]) throws Exception {
FileReader fr=null;
FileWriter fw=null;
try {
PolicyParser pp=new PolicyParser(true);
fr=new FileReader(arg[0]);
pp.read(fr);
fw=new FileWriter(arg[1]);
pp.write(fw);
}
  finally {
if (fr != null) {
fr.close();
}
if (fw != null) {
fw.close();
}
}
}
}
