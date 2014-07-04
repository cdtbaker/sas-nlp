package org.ojalgo.type;
import java.awt.Paint;
public abstract class ChartBuilder<C extends ChartBuilder.ChartResource<?>,B extends ChartBuilder<C,B>> {
public static interface ChartResource<T> {
    Paint getBackground();
    T getDelegate();
    int getHeight();
    String getMimeType();
    int getWidth();
    void setBackground(    Paint aPaint);
    void setHeight(    int aHeight);
    void setWidth(    int aWidth);
    byte[] toByteArray();
  }
  public static enum Orientation {  HORISONTAL,   VERTICAL}
  protected ChartBuilder(){
    super();
  }
  public abstract C build();
}
