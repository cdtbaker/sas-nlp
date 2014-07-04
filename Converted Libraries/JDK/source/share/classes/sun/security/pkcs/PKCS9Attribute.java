package sun.security.pkcs;
import java.io.IOException;
import java.io.OutputStream;
import java.security.cert.CertificateException;
import java.util.Locale;
import java.util.Date;
import java.util.Hashtable;
import sun.security.x509.CertificateExtensions;
import sun.security.util.Debug;
import sun.security.util.DerEncoder;
import sun.security.util.DerValue;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.ObjectIdentifier;
import sun.misc.HexDumpEncoder;
/** 
 * Class supporting any PKCS9 attributes.
 * Supports DER decoding and access to attribute values, but not
 * DER encoding or setting of values.
 * <a name="classTable"><h3>Type/Class Table</h3></a>
 * The following table shows the correspondence between
 * PKCS9 attribute types and value component classes.
 * <P>
 * <TABLE BORDER CELLPADDING=8 ALIGN=CENTER>
 * <TR>
 * <TH>Object Identifier</TH>
 * <TH>Attribute Name</TH>
 * <TH>Type</TH>
 * <TH>Value Class</TH>
 * </TR>
 * <TR>
 * <TD>1.2.840.113549.1.9.1</TD>
 * <TD>EmailAddress</TD>
 * <TD>Multi-valued</TD>
 * <TD><code>String[]</code></TD>
 * </TR>
 * <TR>
 * <TD>1.2.840.113549.1.9.2</TD>
 * <TD>UnstructuredName</TD>
 * <TD>Multi-valued</TD>
 * <TD><code>String[]</code></TD>
 * </TR>
 * <TR>
 * <TD>1.2.840.113549.1.9.3</TD>
 * <TD>ContentType</TD>
 * <TD>Single-valued</TD>
 * <TD><code>ObjectIdentifier</code></TD>
 * </TR>
 * <TR>
 * <TD>1.2.840.113549.1.9.4</TD>
 * <TD>MessageDigest</TD>
 * <TD>Single-valued</TD>
 * <TD><code>byte[]</code></TD>
 * </TR>
 * <TR>
 * <TD>1.2.840.113549.1.9.5</TD>
 * <TD>SigningTime</TD>
 * <TD>Single-valued</TD>
 * <TD><code>Date</code></TD>
 * </TR>
 * <TR>
 * <TD>1.2.840.113549.1.9.6</TD>
 * <TD>Countersignature</TD>
 * <TD>Multi-valued</TD>
 * <TD><code>SignerInfo[]</code></TD>
 * </TR>
 * <TR>
 * <TD>1.2.840.113549.1.9.7</TD>
 * <TD>ChallengePassword</TD>
 * <TD>Single-valued</TD>
 * <TD><code>String</code></TD>
 * </TR>
 * <TR>
 * <TD>1.2.840.113549.1.9.8</TD>
 * <TD>UnstructuredAddress</TD>
 * <TD>Single-valued</TD>
 * <TD><code>String</code></TD>
 * </TR>
 * <TR>
 * <TD>1.2.840.113549.1.9.9</TD>
 * <TD>ExtendedCertificateAttributes</TD>
 * <TD>Multi-valued</TD>
 * <TD>(not supported)</TD>
 * </TR>
 * <TR>
 * <TD>1.2.840.113549.1.9.10</TD>
 * <TD>IssuerAndSerialNumber</TD>
 * <TD>Single-valued</TD>
 * <TD>(not supported)</TD>
 * </TR>
 * <TR>
 * <TD>1.2.840.113549.1.9.{11,12}</TD>
 * <TD>RSA DSI proprietary</TD>
 * <TD>Single-valued</TD>
 * <TD>(not supported)</TD>
 * </TR>
 * <TR>
 * <TD>1.2.840.113549.1.9.13</TD>
 * <TD>S/MIME unused assignment</TD>
 * <TD>Single-valued</TD>
 * <TD>(not supported)</TD>
 * </TR>
 * <TR>
 * <TD>1.2.840.113549.1.9.14</TD>
 * <TD>ExtensionRequest</TD>
 * <TD>Single-valued</TD>
 * <TD>CertificateExtensions</TD>
 * </TR>
 * <TR>
 * <TD>1.2.840.113549.1.9.15</TD>
 * <TD>SMIMECapability</TD>
 * <TD>Single-valued</TD>
 * <TD>(not supported)</TD>
 * </TR>
 * <TR>
 * <TD>1.2.840.113549.1.9.16.2.12</TD>
 * <TD>SigningCertificate</TD>
 * <TD>Single-valued</TD>
 * <TD>SigningCertificateInfo</TD>
 * </TR>
 * <TR>
 * <TD>1.2.840.113549.1.9.16.2.14</TD>
 * <TD>SignatureTimestampToken</TD>
 * <TD>Single-valued</TD>
 * <TD>byte[]</TD>
 * </TR>
 * </TABLE>
 * @author Douglas Hoover
 */
public class PKCS9Attribute implements DerEncoder {
  private static final Debug debug=Debug.getInstance("jar");
  /** 
 * Array of attribute OIDs defined in PKCS9, by number.
 */
  static final ObjectIdentifier[] PKCS9_OIDS=new ObjectIdentifier[18];
static {
    for (int i=1; i < PKCS9_OIDS.length - 2; i++) {
      PKCS9_OIDS[i]=ObjectIdentifier.newInternal(new int[]{1,2,840,113549,1,9,i});
    }
    PKCS9_OIDS[PKCS9_OIDS.length - 2]=ObjectIdentifier.newInternal(new int[]{1,2,840,113549,1,9,16,2,12});
    PKCS9_OIDS[PKCS9_OIDS.length - 1]=ObjectIdentifier.newInternal(new int[]{1,2,840,113549,1,9,16,2,14});
  }
  public static final ObjectIdentifier EMAIL_ADDRESS_OID=PKCS9_OIDS[1];
  public static final ObjectIdentifier UNSTRUCTURED_NAME_OID=PKCS9_OIDS[2];
  public static final ObjectIdentifier CONTENT_TYPE_OID=PKCS9_OIDS[3];
  public static final ObjectIdentifier MESSAGE_DIGEST_OID=PKCS9_OIDS[4];
  public static final ObjectIdentifier SIGNING_TIME_OID=PKCS9_OIDS[5];
  public static final ObjectIdentifier COUNTERSIGNATURE_OID=PKCS9_OIDS[6];
  public static final ObjectIdentifier CHALLENGE_PASSWORD_OID=PKCS9_OIDS[7];
  public static final ObjectIdentifier UNSTRUCTURED_ADDRESS_OID=PKCS9_OIDS[8];
  public static final ObjectIdentifier EXTENDED_CERTIFICATE_ATTRIBUTES_OID=PKCS9_OIDS[9];
  public static final ObjectIdentifier ISSUER_SERIALNUMBER_OID=PKCS9_OIDS[10];
  public static final ObjectIdentifier EXTENSION_REQUEST_OID=PKCS9_OIDS[14];
  public static final ObjectIdentifier SMIME_CAPABILITY_OID=PKCS9_OIDS[15];
  public static final ObjectIdentifier SIGNING_CERTIFICATE_OID=PKCS9_OIDS[16];
  public static final ObjectIdentifier SIGNATURE_TIMESTAMP_TOKEN_OID=PKCS9_OIDS[17];
  public static final String EMAIL_ADDRESS_STR="EmailAddress";
  public static final String UNSTRUCTURED_NAME_STR="UnstructuredName";
  public static final String CONTENT_TYPE_STR="ContentType";
  public static final String MESSAGE_DIGEST_STR="MessageDigest";
  public static final String SIGNING_TIME_STR="SigningTime";
  public static final String COUNTERSIGNATURE_STR="Countersignature";
  public static final String CHALLENGE_PASSWORD_STR="ChallengePassword";
  public static final String UNSTRUCTURED_ADDRESS_STR="UnstructuredAddress";
  public static final String EXTENDED_CERTIFICATE_ATTRIBUTES_STR="ExtendedCertificateAttributes";
  public static final String ISSUER_SERIALNUMBER_STR="IssuerAndSerialNumber";
  private static final String RSA_PROPRIETARY_STR="RSAProprietary";
  private static final String SMIME_SIGNING_DESC_STR="SMIMESigningDesc";
  public static final String EXTENSION_REQUEST_STR="ExtensionRequest";
  public static final String SMIME_CAPABILITY_STR="SMIMECapability";
  public static final String SIGNING_CERTIFICATE_STR="SigningCertificate";
  public static final String SIGNATURE_TIMESTAMP_TOKEN_STR="SignatureTimestampToken";
  /** 
 * Hashtable mapping names and variant names of supported
 * attributes to their OIDs. This table contains all name forms
 * that occur in PKCS9, in lower case.
 */
  private static final Hashtable<String,ObjectIdentifier> NAME_OID_TABLE=new Hashtable<String,ObjectIdentifier>(18);
static {
    NAME_OID_TABLE.put("emailaddress",PKCS9_OIDS[1]);
    NAME_OID_TABLE.put("unstructuredname",PKCS9_OIDS[2]);
    NAME_OID_TABLE.put("contenttype",PKCS9_OIDS[3]);
    NAME_OID_TABLE.put("messagedigest",PKCS9_OIDS[4]);
    NAME_OID_TABLE.put("signingtime",PKCS9_OIDS[5]);
    NAME_OID_TABLE.put("countersignature",PKCS9_OIDS[6]);
    NAME_OID_TABLE.put("challengepassword",PKCS9_OIDS[7]);
    NAME_OID_TABLE.put("unstructuredaddress",PKCS9_OIDS[8]);
    NAME_OID_TABLE.put("extendedcertificateattributes",PKCS9_OIDS[9]);
    NAME_OID_TABLE.put("issuerandserialnumber",PKCS9_OIDS[10]);
    NAME_OID_TABLE.put("rsaproprietary",PKCS9_OIDS[11]);
    NAME_OID_TABLE.put("rsaproprietary",PKCS9_OIDS[12]);
    NAME_OID_TABLE.put("signingdescription",PKCS9_OIDS[13]);
    NAME_OID_TABLE.put("extensionrequest",PKCS9_OIDS[14]);
    NAME_OID_TABLE.put("smimecapability",PKCS9_OIDS[15]);
    NAME_OID_TABLE.put("signingcertificate",PKCS9_OIDS[16]);
    NAME_OID_TABLE.put("signaturetimestamptoken",PKCS9_OIDS[17]);
  }
  /** 
 * Hashtable mapping attribute OIDs defined in PKCS9 to the
 * corresponding attribute value type.
 */
  private static final Hashtable<ObjectIdentifier,String> OID_NAME_TABLE=new Hashtable<ObjectIdentifier,String>(16);
static {
    OID_NAME_TABLE.put(PKCS9_OIDS[1],EMAIL_ADDRESS_STR);
    OID_NAME_TABLE.put(PKCS9_OIDS[2],UNSTRUCTURED_NAME_STR);
    OID_NAME_TABLE.put(PKCS9_OIDS[3],CONTENT_TYPE_STR);
    OID_NAME_TABLE.put(PKCS9_OIDS[4],MESSAGE_DIGEST_STR);
    OID_NAME_TABLE.put(PKCS9_OIDS[5],SIGNING_TIME_STR);
    OID_NAME_TABLE.put(PKCS9_OIDS[6],COUNTERSIGNATURE_STR);
    OID_NAME_TABLE.put(PKCS9_OIDS[7],CHALLENGE_PASSWORD_STR);
    OID_NAME_TABLE.put(PKCS9_OIDS[8],UNSTRUCTURED_ADDRESS_STR);
    OID_NAME_TABLE.put(PKCS9_OIDS[9],EXTENDED_CERTIFICATE_ATTRIBUTES_STR);
    OID_NAME_TABLE.put(PKCS9_OIDS[10],ISSUER_SERIALNUMBER_STR);
    OID_NAME_TABLE.put(PKCS9_OIDS[11],RSA_PROPRIETARY_STR);
    OID_NAME_TABLE.put(PKCS9_OIDS[12],RSA_PROPRIETARY_STR);
    OID_NAME_TABLE.put(PKCS9_OIDS[13],SMIME_SIGNING_DESC_STR);
    OID_NAME_TABLE.put(PKCS9_OIDS[14],EXTENSION_REQUEST_STR);
    OID_NAME_TABLE.put(PKCS9_OIDS[15],SMIME_CAPABILITY_STR);
    OID_NAME_TABLE.put(PKCS9_OIDS[16],SIGNING_CERTIFICATE_STR);
    OID_NAME_TABLE.put(PKCS9_OIDS[17],SIGNATURE_TIMESTAMP_TOKEN_STR);
  }
  /** 
 * Acceptable ASN.1 tags for DER encodings of values of PKCS9
 * attributes, by index in <code>PKCS9_OIDS</code>.
 * Sets of acceptable tags are represented as arrays.
 */
  private static final Byte[][] PKCS9_VALUE_TAGS={null,{new Byte(DerValue.tag_IA5String)},{new Byte(DerValue.tag_IA5String)},{new Byte(DerValue.tag_ObjectId)},{new Byte(DerValue.tag_OctetString)},{new Byte(DerValue.tag_UtcTime)},{new Byte(DerValue.tag_Sequence)},{new Byte(DerValue.tag_PrintableString),new Byte(DerValue.tag_T61String)},{new Byte(DerValue.tag_PrintableString),new Byte(DerValue.tag_T61String)},{new Byte(DerValue.tag_SetOf)},{new Byte(DerValue.tag_Sequence)},null,null,null,{new Byte(DerValue.tag_Sequence)},{new Byte(DerValue.tag_Sequence)},{new Byte(DerValue.tag_Sequence)},{new Byte(DerValue.tag_Sequence)}};
  private static final Class[] VALUE_CLASSES=new Class[18];
static {
    try {
      Class str=Class.forName("[Ljava.lang.String;");
      VALUE_CLASSES[0]=null;
      VALUE_CLASSES[1]=str;
      VALUE_CLASSES[2]=str;
      VALUE_CLASSES[3]=Class.forName("sun.security.util.ObjectIdentifier");
      VALUE_CLASSES[4]=Class.forName("[B");
      VALUE_CLASSES[5]=Class.forName("java.util.Date");
      VALUE_CLASSES[6]=Class.forName("[Lsun.security.pkcs.SignerInfo;");
      VALUE_CLASSES[7]=Class.forName("java.lang.String");
      VALUE_CLASSES[8]=str;
      VALUE_CLASSES[9]=null;
      VALUE_CLASSES[10]=null;
      VALUE_CLASSES[11]=null;
      VALUE_CLASSES[12]=null;
      VALUE_CLASSES[13]=null;
      VALUE_CLASSES[14]=Class.forName("sun.security.x509.CertificateExtensions");
      VALUE_CLASSES[15]=null;
      VALUE_CLASSES[16]=null;
      VALUE_CLASSES[17]=Class.forName("[B");
    }
 catch (    ClassNotFoundException e) {
      throw new ExceptionInInitializerError(e.toString());
    }
  }
  /** 
 * Array indicating which PKCS9 attributes are single-valued,
 * by index in <code>PKCS9_OIDS</code>.
 */
  private static final boolean[] SINGLE_VALUED={false,false,false,true,true,true,false,true,false,false,true,false,false,false,true,true,true,true};
  /** 
 * The OID of this attribute is <code>PKCS9_OIDS[index]</code>.
 */
  private int index;
  /** 
 * Value set of this attribute.  Its class is given by
 * <code>VALUE_CLASSES[index]</code>.
 */
  private Object value;
  /** 
 * Construct an attribute object from the attribute's OID and
 * value.  If the attribute is single-valued, provide only one
 * value.  If the attribute is multi-valued, provide an array
 * containing all the values.
 * Arrays of length zero are accepted, though probably useless.
 * <P> The
 * <a href=#classTable>table</a> gives the class that <code>value</code>
 * must have for a given attribute.
 */
  public PKCS9Attribute(  ObjectIdentifier oid,  Object value) throws IllegalArgumentException {
    init(oid,value);
  }
  /** 
 * Construct an attribute object from the attribute's name and
 * value.  If the attribute is single-valued, provide only one
 * value.  If the attribute is multi-valued, provide an array
 * containing all the values.
 * Arrays of length zero are accepted, though probably useless.
 * <P> The
 * <a href=#classTable>table</a> gives the class that <code>value</code>
 * must have for a given attribute. Reasonable variants of these
 * attributes are accepted; in particular, case does not matter.
 * @exception IllegalArgumentExceptionif the <code>name</code> is not recognized of the
 * <code>value</code> has the wrong type.
 */
  public PKCS9Attribute(  String name,  Object value) throws IllegalArgumentException {
    ObjectIdentifier oid=getOID(name);
    if (oid == null)     throw new IllegalArgumentException("Unrecognized attribute name " + name + " constructing PKCS9Attribute.");
    init(oid,value);
  }
  private void init(  ObjectIdentifier oid,  Object value) throws IllegalArgumentException {
    index=indexOf(oid,PKCS9_OIDS,1);
    if (index == -1)     throw new IllegalArgumentException("Unsupported OID " + oid + " constructing PKCS9Attribute.");
    if (!VALUE_CLASSES[index].isInstance(value))     throw new IllegalArgumentException("Wrong value class " + " for attribute " + oid + " constructing PKCS9Attribute; was "+ value.getClass().toString()+ ", should be "+ VALUE_CLASSES[index].toString());
    this.value=value;
  }
  /** 
 * Construct a PKCS9Attribute from its encoding on an input
 * stream.
 * @param val the DerValue representing the DER encoding of the attribute.
 * @exception IOException on parsing error.
 */
  public PKCS9Attribute(  DerValue derVal) throws IOException {
    DerInputStream derIn=new DerInputStream(derVal.toByteArray());
    DerValue[] val=derIn.getSequence(2);
    if (derIn.available() != 0)     throw new IOException("Excess data parsing PKCS9Attribute");
    if (val.length != 2)     throw new IOException("PKCS9Attribute doesn't have two components");
    ObjectIdentifier oid=val[0].getOID();
    index=indexOf(oid,PKCS9_OIDS,1);
    if (index == -1) {
      if (debug != null) {
        debug.println("ignoring unsupported signer attribute: " + oid);
      }
      throw new ParsingException("Unsupported PKCS9 attribute: " + oid);
    }
    DerValue[] elems=new DerInputStream(val[1].toByteArray()).getSet(1);
    if (SINGLE_VALUED[index] && elems.length > 1)     throwSingleValuedException();
    Byte tag;
    for (int i=0; i < elems.length; i++) {
      tag=new Byte(elems[i].tag);
      if (indexOf(tag,PKCS9_VALUE_TAGS[index],0) == -1)       throwTagException(tag);
    }
switch (index) {
case 1:
case 2:
case 8:
{
        String[] values=new String[elems.length];
        for (int i=0; i < elems.length; i++)         values[i]=elems[i].getAsString();
        value=values;
      }
    break;
case 3:
  value=elems[0].getOID();
break;
case 4:
value=elems[0].getOctetString();
break;
case 5:
value=(new DerInputStream(elems[0].toByteArray())).getUTCTime();
break;
case 6:
{
SignerInfo[] values=new SignerInfo[elems.length];
for (int i=0; i < elems.length; i++) values[i]=new SignerInfo(elems[i].toDerInputStream());
value=values;
}
break;
case 7:
value=elems[0].getAsString();
break;
case 9:
throw new IOException("PKCS9 extended-certificate " + "attribute not supported.");
case 10:
throw new IOException("PKCS9 IssuerAndSerialNumber" + "attribute not supported.");
case 11:
case 12:
throw new IOException("PKCS9 RSA DSI attributes" + "11 and 12, not supported.");
case 13:
throw new IOException("PKCS9 attribute #13 not supported.");
case 14:
value=new CertificateExtensions(new DerInputStream(elems[0].toByteArray()));
break;
case 15:
throw new IOException("PKCS9 SMIMECapability " + "attribute not supported.");
case 16:
value=new SigningCertificateInfo(elems[0].toByteArray());
break;
case 17:
value=elems[0].toByteArray();
break;
default :
}
}
/** 
 * Write the DER encoding of this attribute to an output stream.
 * <P> N.B.: This method always encodes values of
 * ChallengePassword and UnstructuredAddress attributes as ASN.1
 * <code>PrintableString</code>s, without checking whether they
 * should be encoded as <code>T61String</code>s.
 */
public void derEncode(OutputStream out) throws IOException {
DerOutputStream temp=new DerOutputStream();
temp.putOID(getOID());
switch (index) {
case 1:
case 2:
{
String[] values=(String[])value;
DerOutputStream[] temps=new DerOutputStream[values.length];
for (int i=0; i < values.length; i++) {
temps[i]=new DerOutputStream();
temps[i].putIA5String(values[i]);
}
temp.putOrderedSetOf(DerValue.tag_Set,temps);
}
break;
case 3:
{
DerOutputStream temp2=new DerOutputStream();
temp2.putOID((ObjectIdentifier)value);
temp.write(DerValue.tag_Set,temp2.toByteArray());
}
break;
case 4:
{
DerOutputStream temp2=new DerOutputStream();
temp2.putOctetString((byte[])value);
temp.write(DerValue.tag_Set,temp2.toByteArray());
}
break;
case 5:
{
DerOutputStream temp2=new DerOutputStream();
temp2.putUTCTime((Date)value);
temp.write(DerValue.tag_Set,temp2.toByteArray());
}
break;
case 6:
temp.putOrderedSetOf(DerValue.tag_Set,(DerEncoder[])value);
break;
case 7:
{
DerOutputStream temp2=new DerOutputStream();
temp2.putPrintableString((String)value);
temp.write(DerValue.tag_Set,temp2.toByteArray());
}
break;
case 8:
{
String[] values=(String[])value;
DerOutputStream[] temps=new DerOutputStream[values.length];
for (int i=0; i < values.length; i++) {
temps[i]=new DerOutputStream();
temps[i].putPrintableString(values[i]);
}
temp.putOrderedSetOf(DerValue.tag_Set,temps);
}
break;
case 9:
throw new IOException("PKCS9 extended-certificate " + "attribute not supported.");
case 10:
throw new IOException("PKCS9 IssuerAndSerialNumber" + "attribute not supported.");
case 11:
case 12:
throw new IOException("PKCS9 RSA DSI attributes" + "11 and 12, not supported.");
case 13:
throw new IOException("PKCS9 attribute #13 not supported.");
case 14:
{
DerOutputStream temp2=new DerOutputStream();
CertificateExtensions exts=(CertificateExtensions)value;
try {
exts.encode(temp2,true);
}
 catch (CertificateException ex) {
throw new IOException(ex.toString());
}
temp.write(DerValue.tag_Set,temp2.toByteArray());
}
break;
case 15:
throw new IOException("PKCS9 attribute #15 not supported.");
case 16:
throw new IOException("PKCS9 SigningCertificate attribute not supported.");
case 17:
temp.write(DerValue.tag_Set,(byte[])value);
break;
default :
}
DerOutputStream derOut=new DerOutputStream();
derOut.write(DerValue.tag_Sequence,temp.toByteArray());
out.write(derOut.toByteArray());
}
/** 
 * Get the value of this attribute.  If the attribute is
 * single-valued, return just the one value.  If the attribute is
 * multi-valued, return an array containing all the values.
 * It is possible for this array to be of length 0.
 * <P> The
 * <a href=#classTable>table</a> gives the class of the value returned,
 * depending on the type of this attribute.
 */
public Object getValue(){
return value;
}
/** 
 * Show whether this attribute is single-valued.
 */
public boolean isSingleValued(){
return SINGLE_VALUED[index];
}
/** 
 * Return the OID of this attribute.
 */
public ObjectIdentifier getOID(){
return PKCS9_OIDS[index];
}
/** 
 * Return the name of this attribute.
 */
public String getName(){
return OID_NAME_TABLE.get(PKCS9_OIDS[index]);
}
/** 
 * Return the OID for a given attribute name or null if we don't recognize
 * the name.
 */
public static ObjectIdentifier getOID(String name){
return NAME_OID_TABLE.get(name.toLowerCase(Locale.ENGLISH));
}
/** 
 * Return the attribute name for a given OID or null if we don't recognize
 * the oid.
 */
public static String getName(ObjectIdentifier oid){
return OID_NAME_TABLE.get(oid);
}
/** 
 * Returns a string representation of this attribute.
 */
public String toString(){
StringBuffer buf=new StringBuffer(100);
buf.append("[");
buf.append(OID_NAME_TABLE.get(PKCS9_OIDS[index]));
buf.append(": ");
if (SINGLE_VALUED[index]) {
if (value instanceof byte[]) {
HexDumpEncoder hexDump=new HexDumpEncoder();
buf.append(hexDump.encodeBuffer((byte[])value));
}
 else {
buf.append(value.toString());
}
buf.append("]");
return buf.toString();
}
 else {
boolean first=true;
Object[] values=(Object[])value;
for (int j=0; j < values.length; j++) {
if (first) first=false;
 else buf.append(", ");
buf.append(values[j].toString());
}
return buf.toString();
}
}
/** 
 * Beginning the search at <code>start</code>, find the first
 * index <code>i</code> such that <code>a[i] = obj</code>.
 * @return the index, if found, and -1 otherwise.
 */
static int indexOf(Object obj,Object[] a,int start){
for (int i=start; i < a.length; i++) {
if (obj.equals(a[i])) return i;
}
return -1;
}
/** 
 * Throw an exception when there are multiple values for
 * a single-valued attribute.
 */
private void throwSingleValuedException() throws IOException {
throw new IOException("Single-value attribute " + getOID() + " ("+ getName()+ ")"+ " has multiple values.");
}
/** 
 * Throw an exception when the tag on a value encoding is
 * wrong for the attribute whose value it is.
 */
private void throwTagException(Byte tag) throws IOException {
Byte[] expectedTags=PKCS9_VALUE_TAGS[index];
StringBuffer msg=new StringBuffer(100);
msg.append("Value of attribute ");
msg.append(getOID().toString());
msg.append(" (");
msg.append(getName());
msg.append(") has wrong tag: ");
msg.append(tag.toString());
msg.append(".  Expected tags: ");
msg.append(expectedTags[0].toString());
for (int i=1; i < expectedTags.length; i++) {
msg.append(", ");
msg.append(expectedTags[i].toString());
}
msg.append(".");
throw new IOException(msg.toString());
}
}
