package org.ojalgo.finance.portfolio;
import static org.ojalgo.constant.BigMath.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import org.ojalgo.ProgrammingError;
import org.ojalgo.constant.BigMath;
import org.ojalgo.optimisation.Expression;
import org.ojalgo.optimisation.ExpressionsBasedModel;
import org.ojalgo.optimisation.Variable;
import org.ojalgo.type.TypeUtils;
public final class PortfolioMixer {
  private static final String ACTIVE="_Active";
  private static final String B="B";
  private static final String C="C";
  private static final String DIMENSION_MISMATCH="The target and component portfolios must all have the same number of contained assets!";
  private static final String QUADRATIC_OBJECTIVE_PART="Quadratic Objective Part";
  private static final String STRATEGY_COUNT="Strategy Count";
  private final ArrayList<FinancePortfolio> myComponents;
  private final FinancePortfolio myTarget;
  private final HashMap<int[],LowerUpper> myAssetConstraints=new HashMap<int[],LowerUpper>();
  private final HashMap<int[],LowerUpper> myComponentConstraints=new HashMap<int[],LowerUpper>();
  public PortfolioMixer(  final FinancePortfolio target,  final Collection<? extends FinancePortfolio> components){
    this(target,components.toArray(new FinancePortfolio[components.size()]));
  }
  public PortfolioMixer(  final FinancePortfolio target,  final FinancePortfolio... components){
    super();
    myTarget=target;
    final int tmpSize=myTarget.getWeights().size();
    myComponents=new ArrayList<FinancePortfolio>();
    for (    final FinancePortfolio tmpCompPortf : components) {
      if (tmpCompPortf.getWeights().size() != tmpSize) {
        throw new IllegalArgumentException(DIMENSION_MISMATCH);
      }
 else {
        myComponents.add(tmpCompPortf);
      }
    }
  }
  @SuppressWarnings("unused") private PortfolioMixer(){
    this(null,new ArrayList<FinancePortfolio>());
    ProgrammingError.throwForIllegalInvocation();
  }
  public LowerUpper addAssetConstraint(  final Number lowerLimit,  final Number upperLimit,  final int... assetIndeces){
    return myAssetConstraints.put(assetIndeces,new LowerUpper(lowerLimit,upperLimit));
  }
  public LowerUpper addComponentConstraint(  final Number lowerLimit,  final Number upperLimit,  final int... assetIndeces){
    return myComponentConstraints.put(assetIndeces,new LowerUpper(lowerLimit,upperLimit));
  }
  public List<BigDecimal> mix(  final int aNumber){
    final int tmpNumberOfAssets=myTarget.getWeights().size();
    final int tmpNumberOfComponents=myComponents.size();
    final Variable[] tmpVariables=new Variable[2 * tmpNumberOfComponents];
    for (int c=0; c < tmpNumberOfComponents; c++) {
      final Variable tmpVariable=new Variable(C + c);
      BigDecimal tmpVal=ZERO;
      for (int i=0; i < tmpNumberOfAssets; i++) {
        tmpVal=tmpVal.add(myTarget.getWeights().get(i).multiply(myComponents.get(c).getWeights().get(i)));
      }
      tmpVal=tmpVal.multiply(TWO).negate();
      tmpVariable.weight(tmpVal);
      tmpVariable.lower(ZERO);
      tmpVariable.upper(ONE);
      tmpVariables[c]=tmpVariable;
      tmpVariables[tmpNumberOfComponents + c]=Variable.makeBinary(B + c);
    }
    final ExpressionsBasedModel tmpModel=new ExpressionsBasedModel(tmpVariables);
    final Expression tmpQuadObj=tmpModel.addExpression(QUADRATIC_OBJECTIVE_PART);
    tmpQuadObj.weight(ONE);
    for (int row=0; row < tmpNumberOfComponents; row++) {
      for (int col=0; col < tmpNumberOfComponents; col++) {
        BigDecimal tmpVal=ZERO;
        for (int i=0; i < tmpNumberOfAssets; i++) {
          tmpVal=tmpVal.add(myComponents.get(row).getWeights().get(i).multiply(myComponents.get(col).getWeights().get(i)));
        }
        tmpQuadObj.setQuadraticFactor(row,col,tmpVal);
      }
      final Expression tmpActive=tmpModel.addExpression(tmpVariables[row].getName() + ACTIVE);
      tmpActive.setLinearFactor(row,NEG);
      tmpActive.setLinearFactor(tmpNumberOfComponents + row,ONE);
      tmpActive.lower(ZERO);
    }
    final Expression tmpHundredPercent=tmpModel.addExpression("100%");
    tmpHundredPercent.level(ONE);
    for (int c=0; c < tmpNumberOfComponents; c++) {
      tmpHundredPercent.setLinearFactor(c,ONE);
    }
    final Expression tmpStrategyCount=tmpModel.addExpression(STRATEGY_COUNT);
    tmpStrategyCount.upper(TypeUtils.toBigDecimal(aNumber));
    for (int c=0; c < tmpNumberOfComponents; c++) {
      tmpStrategyCount.setLinearFactor(tmpNumberOfComponents + c,ONE);
    }
    for (    final Entry<int[],LowerUpper> tmpEntry : myAssetConstraints.entrySet()) {
      final int tmpIndex=tmpEntry.getKey()[0];
      final BigDecimal tmpLower=tmpEntry.getValue().lower;
      final BigDecimal tmpUpper=tmpEntry.getValue().upper;
      final Expression tmpExpr=tmpModel.addExpression("AC" + Arrays.toString(tmpEntry.getKey()));
      for (int c=0; c < tmpNumberOfComponents; c++) {
        tmpExpr.setLinearFactor(c,myComponents.get(c).getWeights().get(tmpIndex));
      }
      if (tmpLower != null) {
        tmpExpr.lower(tmpLower);
      }
      if (tmpUpper != null) {
        tmpExpr.upper(tmpUpper);
      }
    }
    for (    final Entry<int[],LowerUpper> tmpEntry : myComponentConstraints.entrySet()) {
      final int tmpIndex=tmpEntry.getKey()[0];
      final BigDecimal tmpLower=tmpEntry.getValue().lower;
      final BigDecimal tmpUpper=tmpEntry.getValue().upper;
      final Expression tmpExpr=tmpModel.addExpression("CC" + Arrays.toString(tmpEntry.getKey()));
      tmpExpr.setLinearFactor(tmpIndex,BigMath.ONE);
      for (int c=0; c < tmpNumberOfComponents; c++) {
      }
      if (tmpLower != null) {
        tmpExpr.lower(tmpLower);
      }
      if (tmpUpper != null) {
        tmpExpr.upper(tmpUpper);
      }
    }
    tmpModel.minimise();
    final ArrayList<BigDecimal> retVal=new ArrayList<BigDecimal>(tmpNumberOfComponents);
    for (int v=0; v < tmpNumberOfComponents; v++) {
      retVal.add(tmpVariables[v].getValue());
    }
    return retVal;
  }
}
