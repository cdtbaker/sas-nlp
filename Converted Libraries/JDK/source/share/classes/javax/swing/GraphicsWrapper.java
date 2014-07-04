package javax.swing;
import java.awt.*;
interface GraphicsWrapper {
  Graphics subGraphics();
  boolean isClipIntersecting(  Rectangle r);
  int getClipX();
  int getClipY();
  int getClipWidth();
  int getClipHeight();
}
