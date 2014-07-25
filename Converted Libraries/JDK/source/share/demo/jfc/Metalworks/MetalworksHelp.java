import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.*;
import javax.swing.text.*;
import javax.swing.event.*;
/** 
 * @author Steve Wilson
 * @author Alexander Kouznetsov
 */
@SuppressWarnings("serial") public class MetalworksHelp extends JInternalFrame {
  public MetalworksHelp(){
    super("Help",true,true,true,true);
    setFrameIcon((Icon)UIManager.get("Tree.openIcon"));
    setBounds(200,25,400,400);
    HtmlPane html=new HtmlPane();
    setContentPane(html);
  }
}
@SuppressWarnings("serial") class HtmlPane extends JScrollPane implements HyperlinkListener {
  JEditorPane html;
  @SuppressWarnings("LeakingThisInConstructor") public HtmlPane(){
    try {
      URL url=getClass().getResource("/resources/HelpFiles/toc.html");
      html=new JEditorPane(url);
      html.setEditable(false);
      html.addHyperlinkListener(this);
      html.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES,Boolean.TRUE);
      JViewport vp=getViewport();
      vp.add(html);
    }
 catch (    MalformedURLException e) {
      System.out.println("Malformed URL: " + e);
    }
catch (    IOException e) {
      System.out.println("IOException: " + e);
    }
  }
  /** 
 * Notification of a change relative to a
 * hyperlink.
 */
  public void hyperlinkUpdate(  HyperlinkEvent e){
    if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
      linkActivated(e.getURL());
    }
  }
  /** 
 * Follows the reference in an
 * link.  The given url is the requested reference.
 * By default this calls <a href="#setPage">setPage</a>,
 * and if an exception is thrown the original previous
 * document is restored and a beep sounded.  If an
 * attempt was made to follow a link, but it represented
 * a malformed url, this method will be called with a
 * null argument.
 * @param u the URL to follow
 */
  protected void linkActivated(  URL u){
    Cursor c=html.getCursor();
    Cursor waitCursor=Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
    html.setCursor(waitCursor);
    SwingUtilities.invokeLater(new PageLoader(u,c));
  }
  /** 
 * temporary class that loads synchronously (although
 * later than the request so that a cursor change
 * can be done).
 */
class PageLoader implements Runnable {
    PageLoader(    URL u,    Cursor c){
      url=u;
      cursor=c;
    }
    public void run(){
      if (url == null) {
        html.setCursor(cursor);
        Container parent=html.getParent();
        parent.repaint();
      }
 else {
        Document doc=html.getDocument();
        try {
          html.setPage(url);
        }
 catch (        IOException ioe) {
          html.setDocument(doc);
          getToolkit().beep();
        }
 finally {
          url=null;
          SwingUtilities.invokeLater(this);
        }
      }
    }
    URL url;
    Cursor cursor;
  }
}
