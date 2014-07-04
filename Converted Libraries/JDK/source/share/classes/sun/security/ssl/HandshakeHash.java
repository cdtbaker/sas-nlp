package sun.security.ssl;
import java.io.ByteArrayOutputStream;
import java.security.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
/** 
 * Abstraction for the SSL/TLS hash of all handshake messages that is
 * maintained to verify the integrity of the negotiation. Internally,
 * it consists of an MD5 and an SHA1 digest. They are used in the client
 * and server finished messages and in certificate verify messages (if sent).
 * This class transparently deals with cloneable and non-cloneable digests.
 * This class now supports TLS 1.2 also. The key difference for TLS 1.2
 * is that you cannot determine the hash algorithms for CertificateVerify
 * at a early stage. On the other hand, it's simpler than TLS 1.1 (and earlier)
 * that there is no messy MD5+SHA1 digests.
 * You need to obey these conventions when using this class:
 * 1. protocolDetermined(version) should be called when the negotiated
 * protocol version is determined.
 * 2. Before protocolDetermined() is called, only update(), reset(),
 * restrictCertificateVerifyAlgs(), setFinishedAlg(), and
 * setCertificateVerifyAlg() can be called.
 * 3. After protocolDetermined() is called, reset() cannot be called.
 * 4. After protocolDetermined() is called, if the version is pre-TLS 1.2,
 * getFinishedHash() and getCertificateVerifyHash() cannot be called. Otherwise,
 * getMD5Clone() and getSHAClone() cannot be called.
 * 5. getMD5Clone() and getSHAClone() can only be called after
 * protocolDetermined() is called and version is pre-TLS 1.2.
 * 6. getFinishedHash() and getCertificateVerifyHash() can only be called after
 * all protocolDetermined(), setCertificateVerifyAlg() and setFinishedAlg()
 * have been called and the version is TLS 1.2. If a CertificateVerify message
 * is to be used, call setCertificateVerifyAlg() with the hash algorithm as the
 * argument. Otherwise, you still must call setCertificateVerifyAlg(null) before
 * calculating any hash value.
 * Suggestions: Call protocolDetermined(), restrictCertificateVerifyAlgs(),
 * setFinishedAlg(), and setCertificateVerifyAlg() as early as possible.
 * Example:
 * <pre>
 * HandshakeHash hh = new HandshakeHash(...)
 * hh.protocolDetermined(ProtocolVersion.TLS12);
 * hh.update(clientHelloBytes);
 * hh.setFinishedAlg("SHA-256");
 * hh.update(serverHelloBytes);
 * ...
 * hh.setCertificateVerifyAlg("SHA-384");
 * hh.update(CertificateVerifyBytes);
 * byte[] cvDigest = hh.getCertificateVerifyHash();
 * ...
 * hh.update(finished1);
 * byte[] finDigest1 = hh.getFinishedHash();
 * hh.update(finished2);
 * byte[] finDigest2 = hh.getFinishedHash();
 * </pre>
 * If no CertificateVerify message is to be used, call
 * <pre>
 * hh.setCertificateVerifyAlg(null);
 * </pre>
 * This call can be made once you are certain that this message
 * will never be used.
 */
final class HandshakeHash {
  private int version=-1;
  private ByteArrayOutputStream data=new ByteArrayOutputStream();
  private final boolean isServer;
  private MessageDigest md5, sha;
  private final int clonesNeeded;
  private boolean cvAlgDetermined=false;
  private String cvAlg;
  private MessageDigest finMD;
  /** 
 * Create a new HandshakeHash. needCertificateVerify indicates whether
 * a hash for the certificate verify message is required. The argument
 * algs is a set of all possible hash algorithms that might be used in
 * TLS 1.2. If the caller is sure that TLS 1.2 won't be used or no
 * CertificateVerify message will be used, leave it null or empty.
 */
  HandshakeHash(  boolean isServer,  boolean needCertificateVerify,  Set<String> algs){
    this.isServer=isServer;
    clonesNeeded=needCertificateVerify ? 3 : 2;
  }
  void update(  byte[] b,  int offset,  int len){
switch (version) {
case 1:
      md5.update(b,offset,len);
    sha.update(b,offset,len);
  break;
default :
if (finMD != null) {
  finMD.update(b,offset,len);
}
data.write(b,offset,len);
break;
}
}
/** 
 * Reset the remaining digests. Note this does *not* reset the number of
 * digest clones that can be obtained. Digests that have already been
 * cloned and are gone remain gone.
 */
void reset(){
if (version != -1) {
throw new RuntimeException("reset() can be only be called before protocolDetermined");
}
data.reset();
}
void protocolDetermined(ProtocolVersion pv){
if (version != -1) return;
version=pv.compareTo(ProtocolVersion.TLS12) >= 0 ? 2 : 1;
switch (version) {
case 1:
try {
md5=CloneableDigest.getDigest("MD5",clonesNeeded);
sha=CloneableDigest.getDigest("SHA",clonesNeeded);
}
 catch (NoSuchAlgorithmException e) {
throw new RuntimeException("Algorithm MD5 or SHA not available",e);
}
byte[] bytes=data.toByteArray();
update(bytes,0,bytes.length);
break;
case 2:
break;
}
}
/** 
 * Return a new MD5 digest updated with all data hashed so far.
 */
MessageDigest getMD5Clone(){
if (version != 1) {
throw new RuntimeException("getMD5Clone() can be only be called for TLS 1.1");
}
return cloneDigest(md5);
}
/** 
 * Return a new SHA digest updated with all data hashed so far.
 */
MessageDigest getSHAClone(){
if (version != 1) {
throw new RuntimeException("getSHAClone() can be only be called for TLS 1.1");
}
return cloneDigest(sha);
}
private static MessageDigest cloneDigest(MessageDigest digest){
try {
return (MessageDigest)digest.clone();
}
 catch (CloneNotSupportedException e) {
throw new RuntimeException("Could not clone digest",e);
}
}
private static String normalizeAlgName(String alg){
alg=alg.toUpperCase(Locale.US);
if (alg.startsWith("SHA")) {
if (alg.length() == 3) {
return "SHA-1";
}
if (alg.charAt(3) != '-') {
return "SHA-" + alg.substring(3);
}
}
return alg;
}
/** 
 * Specifies the hash algorithm used in Finished. This should be called
 * based in info in ServerHello.
 * Can be called multiple times.
 */
void setFinishedAlg(String s){
if (s == null) {
throw new RuntimeException("setFinishedAlg's argument cannot be null");
}
if (finMD != null) return;
try {
finMD=CloneableDigest.getDigest(normalizeAlgName(s),2);
}
 catch (NoSuchAlgorithmException e) {
throw new Error(e);
}
finMD.update(data.toByteArray());
}
/** 
 * Restricts the possible algorithms for the CertificateVerify. Called by
 * the server based on info in CertRequest. The argument must be a subset
 * of the argument with the same name in the constructor. The method can be
 * called multiple times. If the caller is sure that no CertificateVerify
 * message will be used, leave this argument null or empty.
 */
void restrictCertificateVerifyAlgs(Set<String> algs){
if (version == 1) {
throw new RuntimeException("setCertificateVerifyAlg() cannot be called for TLS 1.1");
}
}
/** 
 * Specifies the hash algorithm used in CertificateVerify.
 * Can be called multiple times.
 */
void setCertificateVerifyAlg(String s){
if (cvAlgDetermined) return;
cvAlg=s == null ? null : normalizeAlgName(s);
cvAlgDetermined=true;
}
byte[] getAllHandshakeMessages(){
return data.toByteArray();
}
/** 
 * Calculates the hash in Finished. Must be called after setFinishedAlg().
 * This method can be called twice, for Finished messages of the server
 * side and client side respectively.
 */
byte[] getFinishedHash(){
try {
return cloneDigest(finMD).digest();
}
 catch (Exception e) {
throw new Error("BAD");
}
}
}
/** 
 * A wrapper for MessageDigests that simulates cloning of non-cloneable
 * digests. It uses the standard MessageDigest API and therefore can be used
 * transparently in place of a regular digest.
 * Note that we extend the MessageDigest class directly rather than
 * MessageDigestSpi. This works because MessageDigest was originally designed
 * this way in the JDK 1.1 days which allows us to avoid creating an internal
 * provider.
 * It can be "cloned" a limited number of times, which is specified at
 * construction time. This is achieved by internally maintaining n digests
 * in parallel. Consequently, it is only 1/n-th times as fast as the original
 * digest.
 * Example:
 * MessageDigest md = CloneableDigest.getDigest("SHA", 2);
 * md.update(data1);
 * MessageDigest md2 = (MessageDigest)md.clone();
 * md2.update(data2);
 * byte[] d1 = md2.digest(); // digest of data1 || data2
 * md.update(data3);
 * byte[] d2 = md.digest();  // digest of data1 || data3
 * This class is not thread safe.
 */
final class CloneableDigest extends MessageDigest implements Cloneable {
/** 
 * The individual MessageDigests. Initially, all elements are non-null.
 * When clone() is called, the non-null element with the maximum index is
 * returned and the array element set to null.
 * All non-null element are always in the same state.
 */
private final MessageDigest[] digests;
private CloneableDigest(MessageDigest digest,int n,String algorithm) throws NoSuchAlgorithmException {
super(algorithm);
digests=new MessageDigest[n];
digests[0]=digest;
for (int i=1; i < n; i++) {
digests[i]=JsseJce.getMessageDigest(algorithm);
}
}
/** 
 * Return a MessageDigest for the given algorithm that can be cloned the
 * specified number of times. If the default implementation supports
 * cloning, it is returned. Otherwise, an instance of this class is
 * returned.
 */
static MessageDigest getDigest(String algorithm,int n) throws NoSuchAlgorithmException {
MessageDigest digest=JsseJce.getMessageDigest(algorithm);
try {
digest.clone();
return digest;
}
 catch (CloneNotSupportedException e) {
return new CloneableDigest(digest,n,algorithm);
}
}
/** 
 * Check if this object is still usable. If it has already been cloned the
 * maximum number of times, there are no digests left and this object can no
 * longer be used.
 */
private void checkState(){
}
protected int engineGetDigestLength(){
checkState();
return digests[0].getDigestLength();
}
protected void engineUpdate(byte b){
checkState();
for (int i=0; (i < digests.length) && (digests[i] != null); i++) {
digests[i].update(b);
}
}
protected void engineUpdate(byte[] b,int offset,int len){
checkState();
for (int i=0; (i < digests.length) && (digests[i] != null); i++) {
digests[i].update(b,offset,len);
}
}
protected byte[] engineDigest(){
checkState();
byte[] digest=digests[0].digest();
digestReset();
return digest;
}
protected int engineDigest(byte[] buf,int offset,int len) throws DigestException {
checkState();
int n=digests[0].digest(buf,offset,len);
digestReset();
return n;
}
/** 
 * Reset all digests after a digest() call. digests[0] has already been
 * implicitly reset by the digest() call and does not need to be reset
 * again.
 */
private void digestReset(){
for (int i=1; (i < digests.length) && (digests[i] != null); i++) {
digests[i].reset();
}
}
protected void engineReset(){
checkState();
for (int i=0; (i < digests.length) && (digests[i] != null); i++) {
digests[i].reset();
}
}
public Object clone(){
checkState();
for (int i=digests.length - 1; i >= 0; i--) {
if (digests[i] != null) {
MessageDigest digest=digests[i];
digests[i]=null;
return digest;
}
}
throw new InternalError();
}
}
