package org.ojalgo.optimisation.quadratic;
import java.util.List;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.optimisation.Expression;
import org.ojalgo.optimisation.ExpressionsBasedModel;
import org.ojalgo.optimisation.GenericSolver;
public class SequentailQuadraticSolver extends GenericSolver {
  List<Expression> myEqualityConstraints;
  List<Expression> myLowerConstraints;
  List<Expression> myUpperConstraints;
  public SequentailQuadraticSolver(  final ExpressionsBasedModel aModel,  final Options solverOptions){
    super(aModel,solverOptions);
    myEqualityConstraints=aModel.selectExpressionsQuadraticEquality();
    myLowerConstraints=aModel.selectExpressionsQuadraticLower();
    myUpperConstraints=aModel.selectExpressionsQuadraticUpper();
  }
  public Result solve(  final Result kickStart){
    return null;
  }
  @Override protected MatrixStore<Double> extractSolution(){
    return null;
  }
  @Override protected boolean initialise(  final Result kickStart){
    return false;
  }
  @Override protected boolean needsAnotherIteration(){
    return false;
  }
  @Override protected boolean validate(){
    final boolean retVal=true;
    this.setState(State.VALID);
    return retVal;
  }
}
