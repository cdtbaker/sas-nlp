package no.uib.cipr.matrix.sparse;
import no.uib.cipr.matrix.Vector;
/** 
 * Reports on the progress of an iterative solver
 */
public interface IterationReporter {
  /** 
 * Registers current information
 * @param rCurrent residual norm
 * @param xCurrent state vector
 * @param iCurrent iteration number
 */
  void monitor(  double r,  Vector x,  int i);
  /** 
 * Registers current information
 * @param rCurrent residual norm
 * @param iCurrent iteration number
 */
  void monitor(  double r,  int i);
}
