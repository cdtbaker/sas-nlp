package edu.umd.cs.piccolo.examples;
public class FullScreenNodeExample extends NodeExample {
  /** 
 */
  private static final long serialVersionUID=1L;
  public void initialize(){
    super.initialize();
    setFullScreenMode(true);
  }
  public static void main(  final String[] args){
    new FullScreenNodeExample();
  }
}
