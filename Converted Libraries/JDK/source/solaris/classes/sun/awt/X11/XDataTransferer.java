package sun.awt.X11;
import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import javax.imageio.spi.ImageWriterSpi;
import sun.awt.datatransfer.DataTransferer;
import sun.awt.datatransfer.ToolkitThreadBlockedHandler;
import java.io.ByteArrayOutputStream;
/** 
 * Platform-specific support for the data transfer subsystem.
 */
public class XDataTransferer extends DataTransferer {
  static final XAtom FILE_NAME_ATOM=XAtom.get("FILE_NAME");
  static final XAtom DT_NET_FILE_ATOM=XAtom.get("_DT_NETFILE");
  static final XAtom PNG_ATOM=XAtom.get("PNG");
  static final XAtom JFIF_ATOM=XAtom.get("JFIF");
  static final XAtom TARGETS_ATOM=XAtom.get("TARGETS");
  static final XAtom INCR_ATOM=XAtom.get("INCR");
  static final XAtom MULTIPLE_ATOM=XAtom.get("MULTIPLE");
  /** 
 * Singleton constructor
 */
  private XDataTransferer(){
  }
  private static XDataTransferer transferer;
  static XDataTransferer getInstanceImpl(){
synchronized (XDataTransferer.class) {
      if (transferer == null) {
        transferer=new XDataTransferer();
      }
    }
    return transferer;
  }
  public String getDefaultUnicodeEncoding(){
    return "iso-10646-ucs-2";
  }
  public boolean isLocaleDependentTextFormat(  long format){
    return false;
  }
  public boolean isTextFormat(  long format){
    return super.isTextFormat(format) || isMimeFormat(format,"text");
  }
  protected String getCharsetForTextFormat(  Long lFormat){
    long format=lFormat.longValue();
    if (isMimeFormat(format,"text")) {
      String nat=getNativeForFormat(format);
      DataFlavor df=new DataFlavor(nat,null);
      if (!DataTransferer.doesSubtypeSupportCharset(df)) {
        return null;
      }
      String charset=df.getParameter("charset");
      if (charset != null) {
        return charset;
      }
    }
    return super.getCharsetForTextFormat(lFormat);
  }
  protected boolean isURIListFormat(  long format){
    String nat=getNativeForFormat(format);
    if (nat == null) {
      return false;
    }
    try {
      DataFlavor df=new DataFlavor(nat);
      if (df.getPrimaryType().equals("text") && df.getSubType().equals("uri-list")) {
        return true;
      }
    }
 catch (    Exception e) {
    }
    return false;
  }
  public boolean isFileFormat(  long format){
    return format == FILE_NAME_ATOM.getAtom() || format == DT_NET_FILE_ATOM.getAtom();
  }
  public boolean isImageFormat(  long format){
    return format == PNG_ATOM.getAtom() || format == JFIF_ATOM.getAtom() || isMimeFormat(format,"image");
  }
  protected Long getFormatForNativeAsLong(  String str){
    long atom=XAtom.get(str).getAtom();
    return Long.valueOf(atom);
  }
  protected String getNativeForFormat(  long format){
    return getTargetNameForAtom(format);
  }
  public ToolkitThreadBlockedHandler getToolkitThreadBlockedHandler(){
    return XToolkitThreadBlockedHandler.getToolkitThreadBlockedHandler();
  }
  /** 
 * Gets an format name for a given format (atom)
 */
  private String getTargetNameForAtom(  long atom){
    return XAtom.get(atom).getName();
  }
  protected byte[] imageToPlatformBytes(  Image image,  long format) throws IOException {
    String mimeType=null;
    if (format == PNG_ATOM.getAtom()) {
      mimeType="image/png";
    }
 else     if (format == JFIF_ATOM.getAtom()) {
      mimeType="image/jpeg";
    }
 else {
      try {
        String nat=getNativeForFormat(format);
        DataFlavor df=new DataFlavor(nat);
        String primaryType=df.getPrimaryType();
        if ("image".equals(primaryType)) {
          mimeType=df.getPrimaryType() + "/" + df.getSubType();
        }
      }
 catch (      Exception e) {
      }
    }
    if (mimeType != null) {
      return imageToStandardBytes(image,mimeType);
    }
 else {
      String nativeFormat=getNativeForFormat(format);
      throw new IOException("Translation to " + nativeFormat + " is not supported.");
    }
  }
  protected ByteArrayOutputStream convertFileListToBytes(  ArrayList<String> fileList) throws IOException {
    ByteArrayOutputStream bos=new ByteArrayOutputStream();
    for (int i=0; i < fileList.size(); i++) {
      byte[] bytes=fileList.get(i).getBytes();
      if (i != 0)       bos.write(0);
      bos.write(bytes,0,bytes.length);
    }
    return bos;
  }
  /** 
 * Translates either a byte array or an input stream which contain
 * platform-specific image data in the given format into an Image.
 */
  protected Image platformImageBytesOrStreamToImage(  InputStream inputStream,  byte[] bytes,  long format) throws IOException {
    String mimeType=null;
    if (format == PNG_ATOM.getAtom()) {
      mimeType="image/png";
    }
 else     if (format == JFIF_ATOM.getAtom()) {
      mimeType="image/jpeg";
    }
 else {
      try {
        String nat=getNativeForFormat(format);
        DataFlavor df=new DataFlavor(nat);
        String primaryType=df.getPrimaryType();
        if ("image".equals(primaryType)) {
          mimeType=df.getPrimaryType() + "/" + df.getSubType();
        }
      }
 catch (      Exception e) {
      }
    }
    if (mimeType != null) {
      return standardImageBytesOrStreamToImage(inputStream,bytes,mimeType);
    }
 else {
      String nativeFormat=getNativeForFormat(format);
      throw new IOException("Translation from " + nativeFormat + " is not supported.");
    }
  }
  protected String[] dragQueryFile(  byte[] bytes){
    XToolkit.awtLock();
    try {
      return XlibWrapper.XTextPropertyToStringList(bytes,XAtom.get("STRING").getAtom());
    }
  finally {
      XToolkit.awtUnlock();
    }
  }
  protected URI[] dragQueryURIs(  InputStream stream,  byte[] bytes,  long format,  Transferable localeTransferable) throws IOException {
    String charset=null;
    if (localeTransferable != null && isLocaleDependentTextFormat(format) && localeTransferable.isDataFlavorSupported(javaTextEncodingFlavor)) {
      try {
        charset=new String((byte[])localeTransferable.getTransferData(javaTextEncodingFlavor),"UTF-8");
      }
 catch (      UnsupportedFlavorException cannotHappen) {
      }
    }
 else {
      charset=getCharsetForTextFormat(format);
    }
    if (charset == null) {
      charset=getDefaultTextCharset();
    }
    BufferedReader reader=null;
    try {
      reader=new BufferedReader(new InputStreamReader(stream,charset));
      String line;
      ArrayList<URI> uriList=new ArrayList<URI>();
      URI uri;
      while ((line=reader.readLine()) != null) {
        try {
          uri=new URI(line);
        }
 catch (        URISyntaxException uriSyntaxException) {
          throw new IOException(uriSyntaxException);
        }
        uriList.add(uri);
      }
      return uriList.toArray(new URI[uriList.size()]);
    }
  finally {
      if (reader != null)       reader.close();
    }
  }
  /** 
 * Returns true if and only if the name of the specified format Atom
 * constitutes a valid MIME type with the specified primary type.
 */
  private boolean isMimeFormat(  long format,  String primaryType){
    String nat=getNativeForFormat(format);
    if (nat == null) {
      return false;
    }
    try {
      DataFlavor df=new DataFlavor(nat);
      if (primaryType.equals(df.getPrimaryType())) {
        return true;
      }
    }
 catch (    Exception e) {
    }
    return false;
  }
  public List getPlatformMappingsForNative(  String nat){
    List flavors=new ArrayList();
    if (nat == null) {
      return flavors;
    }
    DataFlavor df=null;
    try {
      df=new DataFlavor(nat);
    }
 catch (    Exception e) {
      return flavors;
    }
    Object value=df;
    final String primaryType=df.getPrimaryType();
    final String baseType=primaryType + "/" + df.getSubType();
    if ("text".equals(primaryType)) {
      value=primaryType + "/" + df.getSubType();
    }
 else     if ("image".equals(primaryType)) {
      Iterator readers=ImageIO.getImageReadersByMIMEType(baseType);
      if (readers.hasNext()) {
        flavors.add(DataFlavor.imageFlavor);
      }
    }
    flavors.add(value);
    return flavors;
  }
  private static ImageTypeSpecifier defaultSpecifier=null;
  private ImageTypeSpecifier getDefaultImageTypeSpecifier(){
    if (defaultSpecifier == null) {
      ColorModel model=ColorModel.getRGBdefault();
      WritableRaster raster=model.createCompatibleWritableRaster(10,10);
      BufferedImage bufferedImage=new BufferedImage(model,raster,model.isAlphaPremultiplied(),null);
      defaultSpecifier=new ImageTypeSpecifier(bufferedImage);
    }
    return defaultSpecifier;
  }
  public List getPlatformMappingsForFlavor(  DataFlavor df){
    List natives=new ArrayList(1);
    if (df == null) {
      return natives;
    }
    String charset=df.getParameter("charset");
    String baseType=df.getPrimaryType() + "/" + df.getSubType();
    String mimeType=baseType;
    if (charset != null && DataTransferer.isFlavorCharsetTextType(df)) {
      mimeType+=";charset=" + charset;
    }
    if (df.getRepresentationClass() != null && (df.isRepresentationClassInputStream() || df.isRepresentationClassByteBuffer() || byteArrayClass.equals(df.getRepresentationClass()))) {
      natives.add(mimeType);
    }
    if (DataFlavor.imageFlavor.equals(df)) {
      String[] mimeTypes=ImageIO.getWriterMIMETypes();
      if (mimeTypes != null) {
        for (int i=0; i < mimeTypes.length; i++) {
          Iterator writers=ImageIO.getImageWritersByMIMEType(mimeTypes[i]);
          while (writers.hasNext()) {
            ImageWriter imageWriter=(ImageWriter)writers.next();
            ImageWriterSpi writerSpi=imageWriter.getOriginatingProvider();
            if (writerSpi != null && writerSpi.canEncodeImage(getDefaultImageTypeSpecifier())) {
              natives.add(mimeTypes[i]);
              break;
            }
          }
        }
      }
    }
 else     if (DataTransferer.isFlavorCharsetTextType(df)) {
      final Iterator iter=DataTransferer.standardEncodings();
      if (DataFlavor.stringFlavor.equals(df)) {
        baseType="text/plain";
      }
      while (iter.hasNext()) {
        String encoding=(String)iter.next();
        if (!encoding.equals(charset)) {
          natives.add(baseType + ";charset=" + encoding);
        }
      }
      if (!natives.contains(baseType)) {
        natives.add(baseType);
      }
    }
    return natives;
  }
}