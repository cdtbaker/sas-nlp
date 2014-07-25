package java.awt;
/** 
 * The {@code GridBagLayoutInfo} is an utility class for{@code GridBagLayout} layout manager.
 * It stores align, size and baseline parameters for every component within a container.
 * <p>
 * @see java.awt.GridBagLayout
 * @see java.awt.GridBagConstraints
 * @since 1.6
 */
public class GridBagLayoutInfo implements java.io.Serializable {
  private static final long serialVersionUID=-4899416460737170217L;
  int width, height;
  int startx, starty;
  int minWidth[];
  int minHeight[];
  double weightX[];
  double weightY[];
  boolean hasBaseline;
  short baselineType[];
  int maxAscent[];
  int maxDescent[];
  /** 
 * Creates an instance of GridBagLayoutInfo representing {@code GridBagLayout}grid cells with it's own parameters.
 * @param width the columns
 * @param height the rows
 * @since 6.0
 */
  GridBagLayoutInfo(  int width,  int height){
    this.width=width;
    this.height=height;
  }
  /** 
 * Returns true if the specified row has any component aligned on the
 * baseline with a baseline resize behavior of CONSTANT_DESCENT.
 */
  boolean hasConstantDescent(  int row){
    return ((baselineType[row] & (1 << Component.BaselineResizeBehavior.CONSTANT_DESCENT.ordinal())) != 0);
  }
  /** 
 * Returns true if there is a baseline for the specified row.
 */
  boolean hasBaseline(  int row){
    return (hasBaseline && baselineType[row] != 0);
  }
}
