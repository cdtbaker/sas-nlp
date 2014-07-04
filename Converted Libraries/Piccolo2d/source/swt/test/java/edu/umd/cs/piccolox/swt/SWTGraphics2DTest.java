package edu.umd.cs.piccolox.swt;
import junit.framework.TestCase;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
/** 
 * Unit test for SWTGraphics2D.
 */
public class SWTGraphics2DTest extends TestCase {
  private Shell shell;
  /** 
 * {@inheritDoc} 
 */
  public void setUp(){
    shell=new Shell(Display.getDefault());
    shell.setLayout(new FillLayout());
  }
  public void testCacheCleanup(){
    PSWTCanvas canvas=new PSWTCanvas(shell,0);
    PSWTText text=new PSWTText("test");
    canvas.getLayer().addChild(text);
    canvas.dispose();
    canvas=new PSWTCanvas(shell,0);
    text=new PSWTText("test");
  }
}
