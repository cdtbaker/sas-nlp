package cern.jet.random;
/** 
 * Abstract base class for all continous distributions.
 * @author wolfgang.hoschek@cern.ch
 * @version 1.0, 09/24/99
 */
public abstract class AbstractContinousDistribution extends AbstractDistribution {
  /** 
 * Makes this class non instantiable, but still let's others inherit from it.
 */
  protected AbstractContinousDistribution(){
  }
}
