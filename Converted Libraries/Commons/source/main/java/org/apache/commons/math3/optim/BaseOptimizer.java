package org.apache.commons.math3.optim;
import org.apache.commons.math3.util.Incrementor;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.exception.TooManyIterationsException;
/** 
 * Base class for implementing optimizers.
 * It contains the boiler-plate code for counting the number of evaluations
 * of the objective function and the number of iterations of the algorithm,
 * and storing the convergence checker.
 * <em>It is not a "user" class.</em>
 * @param<PAIR>
 *  Type of the point/value pair returned by the optimization
 * algorithm.
 * @version $Id: BaseOptimizer.java 1458323 2013-03-19 14:51:30Z erans $
 * @since 3.1
 */
public abstract class BaseOptimizer<PAIR> {
  /** 
 * Evaluations counter. 
 */
  protected final Incrementor evaluations;
  /** 
 * Iterations counter. 
 */
  protected final Incrementor iterations;
  /** 
 * Convergence checker. 
 */
  private ConvergenceChecker<PAIR> checker;
  /** 
 * @param checker Convergence checker.
 */
  protected BaseOptimizer(  ConvergenceChecker<PAIR> checker){
    this.checker=checker;
    evaluations=new Incrementor(0,new MaxEvalCallback());
    iterations=new Incrementor(Integer.MAX_VALUE,new MaxIterCallback());
  }
  /** 
 * Gets the maximal number of function evaluations.
 * @return the maximal number of function evaluations.
 */
  public int getMaxEvaluations(){
    return evaluations.getMaximalCount();
  }
  /** 
 * Gets the number of evaluations of the objective function.
 * The number of evaluations corresponds to the last call to the{@code optimize} method. It is 0 if the method has not been
 * called yet.
 * @return the number of evaluations of the objective function.
 */
  public int getEvaluations(){
    return evaluations.getCount();
  }
  /** 
 * Gets the maximal number of iterations.
 * @return the maximal number of iterations.
 */
  public int getMaxIterations(){
    return iterations.getMaximalCount();
  }
  /** 
 * Gets the number of iterations performed by the algorithm.
 * The number iterations corresponds to the last call to the{@code optimize} method. It is 0 if the method has not been
 * called yet.
 * @return the number of evaluations of the objective function.
 */
  public int getIterations(){
    return iterations.getCount();
  }
  /** 
 * Gets the convergence checker.
 * @return the object used to check for convergence.
 */
  public ConvergenceChecker<PAIR> getConvergenceChecker(){
    return checker;
  }
  /** 
 * Stores data and performs the optimization.
 * <br/>
 * The list of parameters is open-ended so that sub-classes can extend it
 * with arguments specific to their concrete implementations.
 * <br/>
 * When the method is called multiple times, instance data is overwritten
 * only when actually present in the list of arguments: when not specified,
 * data set in a previous call is retained (and thus is optional in
 * subsequent calls).
 * <br/>
 * Important note: Subclasses <em>must</em> override{@link #parseOptimizationData(OptimizationData[])} if they need to register
 * their own options; but then, they <em>must</em> also call{@code super.parseOptimizationData(optData)} within that method.
 * @param optData Optimization data.
 * This method will register the following data:
 * <ul>
 * <li>{@link MaxEval}</li>
 * <li>{@link MaxIter}</li>
 * </ul>
 * @return a point/value pair that satifies the convergence criteria.
 * @throws TooManyEvaluationsException if the maximal number of
 * evaluations is exceeded.
 * @throws TooManyIterationsException if the maximal number of
 * iterations is exceeded.
 */
  public PAIR optimize(  OptimizationData... optData) throws TooManyEvaluationsException, TooManyIterationsException {
    parseOptimizationData(optData);
    evaluations.resetCount();
    iterations.resetCount();
    return doOptimize();
  }
  /** 
 * Performs the bulk of the optimization algorithm.
 * @return the point/value pair giving the optimal value of the
 * objective function.
 */
  protected abstract PAIR doOptimize();
  /** 
 * Increment the evaluation count.
 * @throws TooManyEvaluationsException if the allowed evaluations
 * have been exhausted.
 */
  protected void incrementEvaluationCount() throws TooManyEvaluationsException {
    evaluations.incrementCount();
  }
  /** 
 * Increment the iteration count.
 * @throws TooManyIterationsException if the allowed iterations
 * have been exhausted.
 */
  protected void incrementIterationCount() throws TooManyIterationsException {
    iterations.incrementCount();
  }
  /** 
 * Scans the list of (required and optional) optimization data that
 * characterize the problem.
 * @param optData Optimization data.
 * The following data will be looked for:
 * <ul>
 * <li>{@link MaxEval}</li>
 * <li>{@link MaxIter}</li>
 * </ul>
 */
  protected void parseOptimizationData(  OptimizationData... optData){
    for (    OptimizationData data : optData) {
      if (data instanceof MaxEval) {
        evaluations.setMaximalCount(((MaxEval)data).getMaxEval());
        continue;
      }
      if (data instanceof MaxIter) {
        iterations.setMaximalCount(((MaxIter)data).getMaxIter());
        continue;
      }
    }
  }
  /** 
 * Defines the action to perform when reaching the maximum number
 * of evaluations.
 */
private static class MaxEvalCallback implements Incrementor.MaxCountExceededCallback {
    /** 
 * {@inheritDoc}
 * @throws TooManyEvaluationsException.
 */
    public void trigger(    int max){
      throw new TooManyEvaluationsException(max);
    }
  }
  /** 
 * Defines the action to perform when reaching the maximum number
 * of evaluations.
 */
private static class MaxIterCallback implements Incrementor.MaxCountExceededCallback {
    /** 
 * {@inheritDoc}
 * @throws TooManyIterationsException.
 */
    public void trigger(    int max){
      throw new TooManyIterationsException(max);
    }
  }
}
