package sun.security.ssl;
import java.io.*;
import java.nio.*;
import javax.net.ssl.SSLException;
import sun.misc.HexDumpEncoder;
/** 
 * A OutputRecord class extension which uses external ByteBuffers
 * or the internal ByteArrayOutputStream for data manipulations.
 * <P>
 * Instead of rewriting this entire class
 * to use ByteBuffers, we leave things intact, so handshake, CCS,
 * and alerts will continue to use the internal buffers, but application
 * data will use external buffers.
 * @author Brad Wetmore
 */
final class EngineOutputRecord extends OutputRecord {
  private EngineWriter writer;
  private boolean finishedMsg=false;
  EngineOutputRecord(  byte type,  SSLEngineImpl engine){
    super(type,recordSize(type));
    writer=engine.writer;
  }
  /** 
 * Get the size of the buffer we need for records of the specified
 * type.
 * <P>
 * Application data buffers will provide their own byte buffers,
 * and will not use the internal byte caching.
 */
  private static int recordSize(  byte type){
switch (type) {
case ct_change_cipher_spec:
case ct_alert:
      return maxAlertRecordSize;
case ct_handshake:
    return maxRecordSize;
case ct_application_data:
  return 0;
}
throw new RuntimeException("Unknown record type: " + type);
}
void setFinishedMsg(){
finishedMsg=true;
}
public void flush() throws IOException {
finishedMsg=false;
}
boolean isFinishedMsg(){
return finishedMsg;
}
/** 
 * Calculate the MAC value, storing the result either in
 * the internal buffer, or at the end of the destination
 * ByteBuffer.
 * <P>
 * We assume that the higher levels have assured us enough
 * room, otherwise we'll indirectly throw a
 * BufferOverFlowException runtime exception.
 * position should equal limit, and points to the next
 * free spot.
 */
private void addMAC(MAC signer,ByteBuffer bb) throws IOException {
if (signer.MAClen() != 0) {
byte[] hash=signer.compute(contentType(),bb);
bb.limit(bb.limit() + hash.length);
bb.put(hash);
}
}
void encrypt(CipherBox box,ByteBuffer bb){
box.encrypt(bb);
}
void writeBuffer(OutputStream s,byte[] buf,int off,int len) throws IOException {
ByteBuffer netBB=(ByteBuffer)ByteBuffer.allocate(len).put(buf,0,len).flip();
writer.putOutboundData(netBB);
}
void write(MAC writeMAC,CipherBox writeCipher) throws IOException {
switch (contentType()) {
case ct_change_cipher_spec:
case ct_alert:
case ct_handshake:
break;
default :
throw new RuntimeException("unexpected byte buffers");
}
if (!isEmpty()) {
addMAC(writeMAC);
encrypt(writeCipher);
write((OutputStream)null);
}
return;
}
/** 
 * Main wrap/write driver.
 */
void write(EngineArgs ea,MAC writeMAC,CipherBox writeCipher) throws IOException {
assert (contentType() == ct_application_data);
if (writeMAC == MAC.NULL) {
return;
}
int length=Math.min(ea.getAppRemaining(),maxDataSize);
if (length == 0) {
return;
}
ByteBuffer dstBB=ea.netData;
int dstPos=dstBB.position();
int dstLim=dstBB.limit();
int dstData=dstPos + headerSize;
dstBB.position(dstData);
ea.gather(length);
dstBB.limit(dstBB.position());
dstBB.position(dstData);
addMAC(writeMAC,dstBB);
dstBB.limit(dstBB.position());
dstBB.position(dstData);
encrypt(writeCipher,dstBB);
if (debug != null && (Debug.isOn("record") || Debug.isOn("handshake"))) {
if ((debug != null && Debug.isOn("record")) || contentType() == ct_change_cipher_spec) System.out.println(Thread.currentThread().getName() + ", WRITE: " + protocolVersion+ " "+ InputRecord.contentName(contentType())+ ", length = "+ length);
}
int packetLength=dstBB.limit() - dstData;
dstBB.put(dstPos,contentType());
dstBB.put(dstPos + 1,protocolVersion.major);
dstBB.put(dstPos + 2,protocolVersion.minor);
dstBB.put(dstPos + 3,(byte)(packetLength >> 8));
dstBB.put(dstPos + 4,(byte)packetLength);
dstBB.limit(dstLim);
return;
}
}
