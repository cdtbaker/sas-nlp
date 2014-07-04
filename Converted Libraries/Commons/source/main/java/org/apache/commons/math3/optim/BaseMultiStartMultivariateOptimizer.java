package org.apache.commons.math3.optim;
import org.apache.commons.math3.exception.MathIllegalStateException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.random.RandomVectorGenerator;
/** 
 * Base class multi-start optimizer for a multivariate function.
 * <br/>
 * This class wraps an optimizer in order to use it several times in
 * turn with different starting points (trying to avoid being trapped
 * in a local extremum when looking for a global one).
 * <em>It is not a "user" class.</em>
 * @param<PAIR>
 *  Type of the point/value pair returned by the optimization
 * algorithm.
 * @version $Id: BaseMultiStartMultivariateOptimizer.java 1454746 2013-03-09 17:37:30Z luc $
 * @since 3.0
 */
public abstract class BaseMultiStartMultivariateOptimizer<PAIR> extends BaseMultivariateOptimizer<PAIR> {
  /** 
 * Underlying classical optimizer. 
 */
  private final BaseMultivariateOptimizer<PAIR> optimizer;
  /** 
 * Number of evaluations already performed for all starts. 
 */
  private int totalEvaluations;
  /** 
 * Number of starts to go. 
 */
  private int starts;
  /** 
 * Random generator for multi-start. 
 */
  private RandomVectorGenerator generator;
  /** 
 * Optimization data. 
 */
  private OptimizationData[] optimData;
  /** 
 * Location in {@link #optimData} where the updated maximum
 * number of evaluations will be stored.
 */
  private int maxEvalIndex=-1;
  /** 
 * Location in {@link #optimData} where the updated start value
 * will be stored.
 */
  private int initialGuessIndex=-1;
  /** 
 * Create a multi-start optimizer from a single-start optimizer.
 * <p>
 * Note that if there are bounds constraints (see {@link #getLowerBound()}and {@link #getUpperBound()}), then a simple rejection algorithm is used
 * at each restart. This implies that the random vector generator should have
 * a good probability to generate vectors in the bounded domain, otherwise the
 * rejection algorithm will hit the {@link #getMaxEvaluations()} count without
 * generating a proper restart point. Users must be take great care of the <a
 * href="http://en.wikipedia.org/wiki/Curse_of_dimensionality">curse of dimensionality</a>.
 * </p>
 * @param optimizer Single-start optimizer to wrap.
 * @param starts Number of starts to perform. If {@code starts == 1},
 * the {@link #optimize(OptimizationData[]) optimize} will return the
 * same solution as the given {@code optimizer} would return.
 * @param generator Random vector generator to use for restarts.
 * @throws NotStrictlyPositiveException if {@code starts < 1}.
 */
  public BaseMultiStartMultivariateOptimizer(  final BaseMultivariateOptimizer<PAIR> optimizer,  final int starts,  final RandomVectorGenerator generator){
    super(optimizer.getConvergenceChecker());
    if (starts < 1) {
      throw new NotStrictlyPositiveException(starts);
    }
    this.optimizer=optimizer;
    this.starts=starts;
    this.generator=generator;
  }
  /** 
 * {@inheritDoc} 
 */
  @Override public int getEvaluations(){
    return totalEvaluations;
  }
  /** 
 * Gets all the optima found during the last call to {@code optimize}.
 * The optimizer stores all the optima found during a set of
 * restarts. The {@code optimize} method returns the best point only.
 * This method returns all the points found at the end of each starts,
 * including the best one already returned by the {@code optimize} method.
 * <br/>
 * The returned array as one element for each start as specified
 * in the constructor. It is ordered with the results from the
 * runs that did converge first, sorted from best to worst
 * objective value (i.e in ascending order if minimizing and in
 * descending order if maximizing), followed by {@code null} elements
 * corresponding to the runs that did not converge. This means all
 * elements will be {@code null} if the {@code optimize} method did throw
 * an exception.
 * This also means that if the first element is not {@code null}, it is
 * the best point found across all starts.
 * <br/>
 * The behaviour is undefined if this method is called before{@code optimize}; it will likely throw {@code NullPointerException}.
 * @return an array containing the optima sorted from best to worst.
 */
  public abstract PAIR[] getOptima();
  /** 
 * {@inheritDoc}
 * @throws MathIllegalStateException if {@code optData} does not contain an
 * instance of {@link MaxEval} or {@link InitialGuess}.
 */
  @Override public PAIR optimize(  OptimizationData... optData){
    optimData=optData;
    return super.optimize(optData);
  }
  /** 
 * {@inheritDoc} 
 */
  @Override protected PAIR doOptimize(){
    for (int i=0; i < optimData.length; i++) {
      if (optimData[i] instanceof MaxEval) {
        optimData[i]=null;
        maxEvalIndex=i;
      }
      if (optimData[i] instanceof InitialGuess) {
        optimData[i]=null;
        initialGuessIndex=i;
        continue;
      }
    }
    if (maxEvalIndex == -1) {
      throw new MathIllegalStateException();
    }
    if (initialGuessIndex == -1) {
      throw new MathIllegalStateException();
    }
    RuntimeException lastException=null;
    totalEvaluations=0;
    clear();
    final int maxEval=getMaxEvaluations();
    final double[] min=getLowerBound();
    final double[] max=getUpperBound();
    final double[] startPoint=getStartPoint();
    for (int i=0; i < starts; i++) {
      try {
        optimData[maxEvalIndex]=new MaxEval(maxEval - totalEvaluations);
        double[] s=null;
        if (i == 0) {
          s=startPoint;
        }
 else {
          int attempts=0;
          while (s == null) {
            if (attempts++ >= getMaxEvaluations()) {
              throw new TooManyEvaluationsException(getMaxEvaluations());
            }
            s=generator.nextVector();
            for (int k=0; s != null && k < s.length; ++k) {
              if ((min != null && s[k] < min[k]) || (max != null && s[k] > max[k])) {
                s=null;
              }
            }
          }
        }
        optimData[initialGuessIndex]=new InitialGuess(s);
        final PAIR result=optimizer.optimize(optimData);
        store(result);
      }
 catch (      RuntimeException mue) {
        lastException=mue;
      }
      totalEvaluations+=optimizer.getEvaluations();
    }
    final PAIR[] optima=getOptima();
    if (optima.length == 0) {
      throw lastException;
    }
    return optima[0];
  }
  /** 
 * Method that will be called in order to store each found optimum.
 * @param optimum Result of an optimization run.
 */
  protected abstract void store(  PAIR optimum);
  /** 
 * Method that will called in order to clear all stored optima.
 */
  protected abstract void clear();
}
