package sun.awt.windows;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import sun.awt.datatransfer.SunClipboard;
import sun.awt.datatransfer.DataTransferer;
/** 
 * A class which interfaces with the Windows clipboard in order to support
 * data transfer via Clipboard operations. Most of the work is provided by
 * sun.awt.datatransfer.DataTransferer.
 * @author Tom Ball
 * @author David Mendenhall
 * @author Danila Sinopalnikov
 * @author Alexander Gerasimov
 * @since JDK1.1
 */
public class WClipboard extends SunClipboard {
  private boolean isClipboardViewerRegistered;
  public WClipboard(){
    super("System");
  }
  public long getID(){
    return 0;
  }
  protected void setContentsNative(  Transferable contents){
    Map formatMap=WDataTransferer.getInstance().getFormatsForTransferable(contents,flavorMap);
    openClipboard(this);
    try {
      for (Iterator iter=formatMap.keySet().iterator(); iter.hasNext(); ) {
        Long lFormat=(Long)iter.next();
        long format=lFormat.longValue();
        DataFlavor flavor=(DataFlavor)formatMap.get(lFormat);
        try {
          byte[] bytes=WDataTransferer.getInstance().translateTransferable(contents,flavor,format);
          publishClipboardData(format,bytes);
        }
 catch (        IOException e) {
          if (!(flavor.isMimeTypeEqual(DataFlavor.javaJVMLocalObjectMimeType) && e instanceof java.io.NotSerializableException)) {
            e.printStackTrace();
          }
        }
      }
    }
  finally {
      closeClipboard();
    }
  }
  private void lostSelectionOwnershipImpl(){
    lostOwnershipImpl();
  }
  /** 
 * Currently delayed data rendering is not used for the Windows clipboard,
 * so there is no native context to clear.
 */
  protected void clearNativeContext(){
  }
  /** 
 * Call the Win32 OpenClipboard function. If newOwner is non-null,
 * we also call EmptyClipboard and take ownership.
 * @throws IllegalStateException if the clipboard has not been opened
 */
  public native void openClipboard(  SunClipboard newOwner) throws IllegalStateException ;
  /** 
 * Call the Win32 CloseClipboard function if we have clipboard ownership,
 * does nothing if we have not ownership.
 */
  public native void closeClipboard();
  /** 
 * Call the Win32 SetClipboardData function.
 */
  private native void publishClipboardData(  long format,  byte[] bytes);
  private static native void init();
static {
    init();
  }
  protected native long[] getClipboardFormats();
  protected native byte[] getClipboardData(  long format) throws IOException ;
  protected void registerClipboardViewerChecked(){
    if (!isClipboardViewerRegistered) {
      registerClipboardViewer();
      isClipboardViewerRegistered=true;
    }
  }
  private native void registerClipboardViewer();
  /** 
 * The clipboard viewer (it's the toolkit window) is not unregistered
 * until the toolkit window disposing since MSDN suggests removing
 * the window from the clipboard viewer chain just before it is destroyed.
 */
  protected void unregisterClipboardViewerChecked(){
  }
  /** 
 * Upcall from native code.
 */
  private void handleContentsChanged(){
    if (!areFlavorListenersRegistered()) {
      return;
    }
    long[] formats=null;
    try {
      openClipboard(null);
      formats=getClipboardFormats();
    }
 catch (    IllegalStateException exc) {
    }
 finally {
      closeClipboard();
    }
    checkChange(formats);
  }
  /** 
 * The clipboard must be opened.
 * @since 1.5
 */
  protected Transferable createLocaleTransferable(  long[] formats) throws IOException {
    boolean found=false;
    for (int i=0; i < formats.length; i++) {
      if (formats[i] == WDataTransferer.CF_LOCALE) {
        found=true;
        break;
      }
    }
    if (!found) {
      return null;
    }
    byte[] localeData=null;
    try {
      localeData=getClipboardData(WDataTransferer.CF_LOCALE);
    }
 catch (    IOException ioexc) {
      return null;
    }
    final byte[] localeDataFinal=localeData;
    return new Transferable(){
      public DataFlavor[] getTransferDataFlavors(){
        return new DataFlavor[]{DataTransferer.javaTextEncodingFlavor};
      }
      public boolean isDataFlavorSupported(      DataFlavor flavor){
        return flavor.equals(DataTransferer.javaTextEncodingFlavor);
      }
      public Object getTransferData(      DataFlavor flavor) throws UnsupportedFlavorException {
        if (isDataFlavorSupported(flavor)) {
          return localeDataFinal;
        }
        throw new UnsupportedFlavorException(flavor);
      }
    }
;
  }
}
