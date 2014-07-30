package org.ejml.data;
import org.ejml.ops.MatrixIO;
/** 
 * Fixed sized vector with 6 elements.  Can represent a a 6 x 1 or 1 x 6 matrix, context dependent.
 * @author Peter Abeles
 */
public class FixedMatrix6_64F implements FixedMatrix64F {
  public double a1, a2, a3, a4, a5, a6;
  public FixedMatrix6_64F(){
  }
  public FixedMatrix6_64F(  double a1,  double a2,  double a3,  double a4,  double a5,  double a6){
    this.a1=a1;
    this.a2=a2;
    this.a3=a3;
    this.a4=a4;
    this.a5=a5;
    this.a6=a6;
  }
  public FixedMatrix6_64F(  FixedMatrix6_64F o){
    this.a1=o.a1;
    this.a2=o.a2;
    this.a3=o.a3;
    this.a4=o.a4;
    this.a5=o.a5;
    this.a6=o.a6;
  }
  @Override public double get(  int row,  int col){
    return unsafe_get(row,col);
  }
  @Override public double unsafe_get(  int row,  int col){
    if (row != 0 && col != 0)     throw new IllegalArgumentException("Row or column must be zero since this is a vector");
    int w=Math.max(row,col);
    if (w == 0) {
      return a1;
    }
 else     if (w == 1) {
      return a2;
    }
 else     if (w == 2) {
      return a3;
    }
 else     if (w == 3) {
      return a4;
    }
 else     if (w == 4) {
      return a5;
    }
 else     if (w == 5) {
      return a6;
    }
 else {
      throw new IllegalArgumentException("Out of range.  " + w);
    }
  }
  @Override public void set(  int row,  int col,  double val){
    unsafe_set(row,col,val);
  }
  @Override public void unsafe_set(  int row,  int col,  double val){
    if (row != 0 && col != 0)     throw new IllegalArgumentException("Row or column must be zero since this is a vector");
    int w=Math.max(row,col);
    if (w == 0) {
      a1=val;
    }
 else     if (w == 1) {
      a2=val;
    }
 else     if (w == 2) {
      a3=val;
    }
 else     if (w == 3) {
      a4=val;
    }
 else     if (w == 4) {
      a5=val;
    }
 else     if (w == 5) {
      a6=val;
    }
 else {
      throw new IllegalArgumentException("Out of range.  " + w);
    }
  }
  @Override public int getNumRows(){
    return 6;
  }
  @Override public int getNumCols(){
    return 1;
  }
  @Override public int getNumElements(){
    return 6;
  }
  @Override public <T extends Matrix64F>T copy(){
    return (T)new FixedMatrix6_64F(this);
  }
  @Override public void print(){
    MatrixIO.print(System.out,this);
  }
}