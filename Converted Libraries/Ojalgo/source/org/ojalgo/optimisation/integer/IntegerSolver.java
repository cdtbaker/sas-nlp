package org.ojalgo.optimisation.integer;
import static org.ojalgo.constant.PrimitiveMath.*;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.atomic.AtomicInteger;
import org.ojalgo.RecoverableCondition;
import org.ojalgo.array.SimpleArray;
import org.ojalgo.array.SimpleArray.Primitive;
import org.ojalgo.concurrent.DaemonPoolExecutor;
import org.ojalgo.constant.PrimitiveMath;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.matrix.store.PrimitiveDenseStore;
import org.ojalgo.optimisation.ExpressionsBasedModel;
import org.ojalgo.optimisation.GenericSolver;
import org.ojalgo.optimisation.Optimisation;
import org.ojalgo.optimisation.Variable;
import org.ojalgo.type.TypeUtils;
/** 
 * IntegerSolver
 * @author apete
 */
public final class IntegerSolver extends GenericSolver {
final class BranchAndBoundNodeTask extends RecursiveTask<Boolean> {
    private final NodeKey myKey;
    private BranchAndBoundNodeTask(    final NodeKey key){
      super();
      myKey=key;
    }
    BranchAndBoundNodeTask(){
      super();
      myKey=new NodeKey(IntegerSolver.this.getModel());
    }
    @Override public String toString(){
      return myKey.toString();
    }
    @Override protected Boolean compute(){
      if (IntegerSolver.this.isDebug()) {
        IntegerSolver.this.logDebug("\nBranch&Bound Node");
        IntegerSolver.this.logDebug(myKey.toString());
        IntegerSolver.this.logDebug(IntegerSolver.this.toString());
      }
      if (!IntegerSolver.this.isIterationAllowed() || !IntegerSolver.this.isIterationNecessary()) {
        if (IntegerSolver.this.isDebug()) {
          IntegerSolver.this.logDebug("Reached iterations or time limit - stop!");
        }
        return false;
      }
      if (IntegerSolver.this.isExplored(this)) {
        if (IntegerSolver.this.isDebug()) {
          IntegerSolver.this.logDebug("Node previously explored!");
        }
        return true;
      }
 else {
        IntegerSolver.this.markAsExplored(this);
      }
      if (!IntegerSolver.this.isGoodEnoughToContinueBranching(myKey.getParentValue())) {
        if (IntegerSolver.this.isDebug()) {
          IntegerSolver.this.logDebug("No longer a relevant node!");
        }
        return true;
      }
      ExpressionsBasedModel tmpModel=this.getModel();
      final Optimisation.Result tmpResult=tmpModel.solve();
      try {
        IntegerSolver.this.incrementIterationsCount();
      }
 catch (      final RecoverableCondition exception) {
        return false;
      }
      if (tmpResult.getState().isOptimal()) {
        if (IntegerSolver.this.isDebug()) {
          IntegerSolver.this.logDebug("Node solved to optimality!");
        }
        if (IntegerSolver.this.options.validate && !tmpModel.validate(tmpResult)) {
          IntegerSolver.this.logDebug("Node solution marked as OPTIMAL, but is actually INVALID/INFEASIBLE/FAILED. Stop this branch!");
          return false;
        }
        final int tmpBranchIndex=IntegerSolver.this.identifyNonIntegerVariable(tmpResult,myKey);
        final double tmpSolutionValue=IntegerSolver.this.evaluateFunction(tmpResult);
        if (tmpBranchIndex == -1) {
          if (IntegerSolver.this.isDebug()) {
            IntegerSolver.this.logDebug("Integer solution! Store it among the others, and stop this branch!");
          }
          final Optimisation.Result tmpIntegerSolutionResult=new Optimisation.Result(Optimisation.State.FEASIBLE,tmpSolutionValue,tmpResult);
          IntegerSolver.this.storeResult(tmpIntegerSolutionResult);
          if (IntegerSolver.this.isDebug()) {
            IntegerSolver.this.logDebug(IntegerSolver.this.getBestResultSoFar().toString());
          }
        }
 else {
          if (IntegerSolver.this.isDebug()) {
            IntegerSolver.this.logDebug("Not an Integer Solution: " + tmpSolutionValue);
          }
          final double tmpVariableValue=tmpResult.doubleValue(IntegerSolver.this.getGlobalIndex(tmpBranchIndex));
          if (IntegerSolver.this.isGoodEnoughToContinueBranching(tmpSolutionValue)) {
            if (IntegerSolver.this.isDebug()) {
              IntegerSolver.this.logDebug("Still hope, branching on {} @ {} >>> {}",tmpBranchIndex,tmpVariableValue,tmpModel.getVariable(IntegerSolver.this.getGlobalIndex(tmpBranchIndex)));
            }
            tmpModel.destroy();
            tmpModel=null;
            final BranchAndBoundNodeTask tmpLowerBranchTask=this.createLowerBranch(tmpBranchIndex,tmpVariableValue,tmpResult);
            final BranchAndBoundNodeTask tmpUpperBranchTask=this.createUpperBranch(tmpBranchIndex,tmpVariableValue,tmpResult);
            tmpUpperBranchTask.fork();
            final boolean tmpLowerBranchValue=tmpLowerBranchTask.compute();
            if (tmpLowerBranchValue) {
              return tmpUpperBranchTask.join();
            }
 else {
              tmpUpperBranchTask.tryUnfork();
              tmpUpperBranchTask.cancel(true);
              return false;
            }
          }
 else {
            if (IntegerSolver.this.isDebug()) {
              IntegerSolver.this.logDebug("Can't find better integer solutions - stop this branch!");
            }
          }
        }
      }
 else {
        if (IntegerSolver.this.isDebug()) {
          IntegerSolver.this.logDebug("Failed to solve problem - stop this branch!");
        }
      }
      return true;
    }
    BranchAndBoundNodeTask createLowerBranch(    final int branchIndex,    final double nonIntegerValue,    final Optimisation.Result nodeResult){
      final double tmpParentValue=nodeResult.getValue();
      final NodeKey tmpKey=myKey.createLowerBranch(branchIndex,nonIntegerValue,tmpParentValue);
      return new BranchAndBoundNodeTask(tmpKey);
    }
    BranchAndBoundNodeTask createUpperBranch(    final int branchIndex,    final double nonIntegerValue,    final Optimisation.Result nodeResult){
      final double tmpParentValue=nodeResult.getValue();
      final NodeKey tmpKey=myKey.createUpperBranch(branchIndex,nonIntegerValue,tmpParentValue);
      return new BranchAndBoundNodeTask(tmpKey);
    }
    NodeKey getKey(){
      return myKey;
    }
    ExpressionsBasedModel getModel(){
      final ExpressionsBasedModel retVal=IntegerSolver.this.getModel().relax(false);
      final int[] tmpIntegerIndeces=IntegerSolver.this.getIntegerIndeces();
      for (int i=0; i < tmpIntegerIndeces.length; i++) {
        final BigDecimal tmpLowerBound=myKey.getLowerBound(i);
        final BigDecimal tmpUpperBound=myKey.getUpperBound(i);
        final Variable tmpVariable=retVal.getVariable(tmpIntegerIndeces[i]);
        tmpVariable.lower(tmpLowerBound);
        tmpVariable.upper(tmpUpperBound);
        final BigDecimal tmpValue=tmpVariable.getValue();
        if (tmpValue != null) {
          tmpVariable.setValue(tmpValue.max(tmpLowerBound).min(tmpUpperBound));
        }
      }
      if (IntegerSolver.this.isIntegerSolutionFound()) {
        final double tmpBestValue=IntegerSolver.this.getBestResultSoFar().getValue();
        final double tmpGap=Math.abs(tmpBestValue * IntegerSolver.this.options.mip_gap);
        if (retVal.isMinimisation()) {
          retVal.limitObjective(null,TypeUtils.toBigDecimal(tmpBestValue - tmpGap,IntegerSolver.this.options.problem));
        }
 else {
          retVal.limitObjective(TypeUtils.toBigDecimal(tmpBestValue + tmpGap,IntegerSolver.this.options.problem),null);
        }
      }
      return retVal;
    }
  }
final class NodeStatistics {
    private final AtomicInteger myTruncated=new AtomicInteger();
    private final AtomicInteger myAbandoned=new AtomicInteger();
    private final AtomicInteger myInfeasible=new AtomicInteger();
    private final AtomicInteger myFailed=new AtomicInteger();
    private final AtomicInteger myExhausted=new AtomicInteger();
    private final AtomicInteger myBranched=new AtomicInteger();
    /** 
 * Node never evaluated (sub/node problem never solved)
 */
    public final boolean abandoned(){
      myAbandoned.incrementAndGet();
      return true;
    }
    /** 
 * Node evaluated, but solution not integer. Estimate still possible to find better integer solution. Created 2
 * new branches.
 */
    public final boolean branched(){
      myBranched.incrementAndGet();
      return true;
    }
    /** 
 * Node evaluated, but solution not integer. Estimate NOT possible to find better integer solution.
 */
    public final boolean exhausted(){
      myExhausted.incrementAndGet();
      return true;
    }
    public final boolean failed(    final boolean state){
      myFailed.incrementAndGet();
      return state;
    }
    public final boolean feasible(){
      myInfeasible.incrementAndGet();
      return true;
    }
    public final boolean infeasible(    final boolean state){
      myInfeasible.incrementAndGet();
      return state;
    }
    public final boolean truncated(    final boolean state){
      myTruncated.incrementAndGet();
      return state;
    }
    int getCreated(){
      return myTruncated.get() + myAbandoned.get() + this.getEvaluated();
    }
    int getEvaluated(){
      return myInfeasible.get() + myFailed.get() + myExhausted.get()+ myBranched.get();
    }
  }
final class RootTask extends RecursiveTask<Boolean> {
    @Override protected Boolean compute(){
      final ExpressionsBasedModel tmpIntegerModel=IntegerSolver.this.getModel();
      final NodeKey tmpRootKey=new NodeKey(tmpIntegerModel);
      final ExpressionsBasedModel tmpRelaxedModel=tmpIntegerModel.relax(false);
      final List<Variable> tmpVariables=tmpIntegerModel.getIntegerVariables();
      for (int i=0; i < tmpVariables.size(); i++) {
        final Variable tmpVariable=tmpVariables.get(i);
        tmpVariable.lower(tmpRootKey.getLowerBound(i)).upper(tmpRootKey.getUpperBound(i));
      }
      for (      final Variable tmpVariable : tmpVariables) {
      }
      return Boolean.TRUE;
    }
  }
final class Subtask extends RecursiveTask<Boolean> {
    @Override protected Boolean compute(){
      return null;
    }
  }
  public static IntegerSolver make(  final ExpressionsBasedModel model){
    return new IntegerSolver(model,null);
  }
  private volatile Optimisation.Result myBestResultSoFar=null;
  private final Set<NodeKey> myExploredNodes=Collections.synchronizedSet(new HashSet<NodeKey>());
  private final int[] myIntegerIndeces;
  private final AtomicInteger myIntegerSolutionsCount=new AtomicInteger();
  private final boolean myMinimisation;
  IntegerSolver(  final ExpressionsBasedModel model,  final Options solverOptions){
    super(model,solverOptions);
    myMinimisation=model.isMinimisation();
    final List<Variable> tmpIntegerVariables=model.getIntegerVariables();
    myIntegerIndeces=new int[tmpIntegerVariables.size()];
    for (int i=0; i < myIntegerIndeces.length; i++) {
      final Variable tmpVariable=tmpIntegerVariables.get(i);
      myIntegerIndeces[i]=model.indexOf(tmpVariable);
    }
  }
  public Result solve(  final Result kickStart){
    if (kickStart != null) {
      this.storeResult(kickStart);
    }
    this.resetIterationsCount();
    final BranchAndBoundNodeTask tmpNodeTask=new BranchAndBoundNodeTask();
    final boolean tmpNormalExit=DaemonPoolExecutor.INSTANCE.invoke(tmpNodeTask);
    Optimisation.Result retVal=this.getBestResultSoFar();
    if (retVal.getState().isFeasible()) {
      if (tmpNormalExit) {
        retVal=new Optimisation.Result(State.OPTIMAL,retVal);
      }
 else {
        retVal=new Optimisation.Result(State.FEASIBLE,retVal);
      }
    }
 else {
      if (tmpNormalExit) {
        retVal=new Optimisation.Result(State.INFEASIBLE,retVal);
      }
 else {
        retVal=new Optimisation.Result(State.FAILED,retVal);
      }
    }
    return retVal;
  }
  @Override public String toString(){
    return TypeUtils.format("Solutions={} Nodes/Iterations={} {}",this.countIntegerSolutions(),this.countExploredNodes(),this.getBestResultSoFar());
  }
  @Override protected MatrixStore<Double> extractSolution(){
    return PrimitiveDenseStore.FACTORY.columns(this.getBestResultSoFar());
  }
  @Override protected boolean initialise(  final Result kickStart){
    return true;
  }
  @Override protected boolean needsAnotherIteration(){
    return !this.getState().isOptimal();
  }
  @Override protected boolean validate(){
    boolean retVal=true;
    this.setState(State.VALID);
    try {
      if (!(retVal=this.getModel().validate())) {
        retVal=false;
        this.setState(State.INVALID);
      }
    }
 catch (    final Exception ex) {
      retVal=false;
      this.setState(State.FAILED);
    }
    return retVal;
  }
  int countExploredNodes(){
    return myExploredNodes.size();
  }
  int countIntegerSolutions(){
    return myIntegerSolutionsCount.intValue();
  }
  Optimisation.Result getBestResultSoFar(){
    if (myBestResultSoFar != null) {
      return myBestResultSoFar;
    }
 else {
      final State tmpSate=State.INVALID;
      final double tmpValue=myMinimisation ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;
      final Primitive tmpMakePrimitive=SimpleArray.makePrimitive(this.getModel().countVariables());
      return new Optimisation.Result(tmpSate,tmpValue,tmpMakePrimitive);
    }
  }
  int getGlobalIndex(  final int integerIndex){
    return myIntegerIndeces[integerIndex];
  }
  final int[] getIntegerIndeces(){
    return myIntegerIndeces;
  }
  int identifyNonIntegerVariable(  final Optimisation.Result nodeResult,  final NodeKey nodeKey){
    final MatrixStore<Double> tmpGradient=this.getGradient(nodeResult);
    int retVal=-1;
    double tmpFraction, tmpWeightedFraction;
    double tmpMaxFraction=ZERO;
    for (int i=0; i < myIntegerIndeces.length; i++) {
      tmpFraction=nodeKey.getIntegerFraction(i,nodeResult.doubleValue(myIntegerIndeces[i]));
      tmpWeightedFraction=tmpFraction * (PrimitiveMath.ONE + Math.abs(tmpGradient.doubleValue(myIntegerIndeces[i])));
      if ((tmpWeightedFraction > tmpMaxFraction) && !options.integer.isZero(tmpWeightedFraction)) {
        retVal=i;
        tmpMaxFraction=tmpWeightedFraction;
      }
    }
    return retVal;
  }
  boolean isExplored(  final BranchAndBoundNodeTask aNodeTask){
    return myExploredNodes.contains(aNodeTask.getKey());
  }
  boolean isGoodEnoughToContinueBranching(  final double nonIntegerValue){
    if (myBestResultSoFar == null) {
      return true;
    }
 else {
      final double tmpBestIntegerValue=this.getBestResultSoFar().getValue();
      final double tmpMipGap=Math.abs(tmpBestIntegerValue - nonIntegerValue) / Math.abs(tmpBestIntegerValue);
      if (myMinimisation) {
        return (nonIntegerValue < tmpBestIntegerValue) && (tmpMipGap > options.mip_gap);
      }
 else {
        return (nonIntegerValue > tmpBestIntegerValue) && (tmpMipGap > options.mip_gap);
      }
    }
  }
  boolean isIntegerSolutionFound(){
    return myBestResultSoFar != null;
  }
  boolean isIterationNecessary(){
    if (myBestResultSoFar == null) {
      return true;
    }
 else {
      final int tmpIterations=this.countIterations();
      final long tmpTime=this.countTime();
      return (tmpTime < options.time_suffice) && (tmpIterations < options.iterations_suffice);
    }
  }
  void markAsExplored(  final BranchAndBoundNodeTask aNodeTask){
    myExploredNodes.add(aNodeTask.getKey());
  }
  synchronized void storeResult(  final Optimisation.Result result){
    if (myBestResultSoFar == null) {
      myBestResultSoFar=result;
    }
 else     if (myMinimisation && (result.getValue() < myBestResultSoFar.getValue())) {
      myBestResultSoFar=result;
    }
 else     if (!myMinimisation && (result.getValue() > myBestResultSoFar.getValue())) {
      myBestResultSoFar=result;
    }
    myIntegerSolutionsCount.incrementAndGet();
  }
}
