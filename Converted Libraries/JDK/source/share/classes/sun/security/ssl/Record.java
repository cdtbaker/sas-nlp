package sun.security.ssl;
/** 
 * SSL/TLS records, as pulled off (and put onto) a TCP stream.  This is
 * the base interface, which defines common information and interfaces
 * used by both Input and Output records.
 * @author David Brownell
 */
interface Record {
  static final byte ct_change_cipher_spec=20;
  static final byte ct_alert=21;
  static final byte ct_handshake=22;
  static final byte ct_application_data=23;
  static final int headerSize=5;
  static final int maxExpansion=1024;
  static final int trailerSize=20;
  static final int maxDataSize=16384;
  static final int maxPadding=256;
  static final int maxIVLength=256;
  static final int maxRecordSize=headerSize + maxIVLength + maxDataSize+ maxPadding+ trailerSize;
  static final int maxLargeRecordSize=maxRecordSize + maxDataSize;
  static final int maxAlertRecordSize=headerSize + maxIVLength + 2+ maxPadding+ trailerSize;
}
