package no.uib.cipr.matrix.sparse;
import no.uib.cipr.matrix.Vector;
/** 
 * An iteration reporter which does nothing.
 */
public class NoIterationReporter implements IterationReporter {
  public void monitor(  double r,  int i){
  }
  public void monitor(  double r,  Vector x,  int i){
  }
}
