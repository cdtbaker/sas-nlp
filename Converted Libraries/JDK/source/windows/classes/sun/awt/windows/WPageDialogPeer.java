package sun.awt.windows;
public class WPageDialogPeer extends WPrintDialogPeer {
  WPageDialogPeer(  WPageDialog target){
    super(target);
  }
  /** 
 * Displays the page setup dialog placing the user's
 * settings into target's 'page'.
 */
  private native boolean _show();
  public void show(){
    new Thread(new Runnable(){
      public void run(){
        try {
          ((WPrintDialog)target).setRetVal(_show());
        }
 catch (        Exception e) {
        }
        ((WPrintDialog)target).hide();
      }
    }
).start();
  }
}
