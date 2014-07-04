package org.ojalgo.finance.portfolio.simulator;
import java.math.BigDecimal;
import java.util.List;
import org.ojalgo.access.Access2D;
import org.ojalgo.array.Array1D;
import org.ojalgo.array.Array2D;
import org.ojalgo.array.SimpleArray;
import org.ojalgo.finance.portfolio.SimplePortfolio;
import org.ojalgo.function.aggregator.Aggregator;
import org.ojalgo.function.aggregator.AggregatorFunction;
import org.ojalgo.random.process.GeometricBrownian1D;
import org.ojalgo.random.process.GeometricBrownianMotion;
import org.ojalgo.random.process.RandomProcess;
public class PortfolioSimulator {
  private GeometricBrownian1D myProcess;
  public PortfolioSimulator(  final Access2D<?> correlations,  final List<GeometricBrownianMotion> assetProcesses){
    super();
    if ((assetProcesses == null) || (assetProcesses.size() < 1)) {
      throw new IllegalArgumentException();
    }
    if (correlations != null) {
      myProcess=new GeometricBrownian1D(correlations,assetProcesses);
    }
 else {
      myProcess=new GeometricBrownian1D(assetProcesses);
    }
  }
  private PortfolioSimulator(){
    super();
  }
  public RandomProcess.SimulationResults simulate(  final int aNumberOfRealisations,  final int aNumberOfSteps,  final double aStepSize){
    return this.simulate(aNumberOfRealisations,aNumberOfSteps,aStepSize,null);
  }
  public RandomProcess.SimulationResults simulate(  final int aNumberOfRealisations,  final int aNumberOfSteps,  final double aStepSize,  final int rebalancingInterval){
    return this.simulate(aNumberOfRealisations,aNumberOfSteps,aStepSize,Integer.valueOf(rebalancingInterval));
  }
  RandomProcess.SimulationResults simulate(  final int aNumberOfRealisations,  final int aNumberOfSteps,  final double aStepSize,  final Integer rebalancingInterval){
    final int tmpProcDim=myProcess.size();
    final SimpleArray.Primitive tmpInitialValues=myProcess.getValues();
    final Number[] tmpValues=new Number[tmpProcDim];
    for (int p=0; p < tmpProcDim; p++) {
      tmpValues[p]=tmpInitialValues.get(p);
    }
    final List<BigDecimal> tmpWeights=new SimplePortfolio(tmpValues).normalise().getWeights();
    final Array2D<Double> tmpRealisationValues=Array2D.PRIMITIVE.makeZero(aNumberOfRealisations,aNumberOfSteps);
    for (int r=0; r < aNumberOfRealisations; r++) {
      for (int s=0; s < aNumberOfSteps; s++) {
        if ((rebalancingInterval != null) && (s != 0) && ((s % rebalancingInterval) == 0)) {
          final double tmpPortfolioValue=tmpRealisationValues.doubleValue(r,s - 1);
          for (int p=0; p < tmpProcDim; p++) {
            myProcess.setValue(p,tmpPortfolioValue * tmpWeights.get(p).doubleValue());
          }
        }
        final Array1D<Double> tmpRealisation=myProcess.step(aStepSize);
        final AggregatorFunction<Double> tmpAggregator=Aggregator.SUM.getPrimitiveFunction();
        tmpRealisation.visitAll(tmpAggregator);
        tmpRealisationValues.set(r,s,tmpAggregator.doubleValue());
      }
      myProcess.setValues(tmpInitialValues);
    }
    final AggregatorFunction<Double> tmpAggregator=Aggregator.SUM.getPrimitiveFunction();
    for (int i=0; i < tmpInitialValues.length; i++) {
      tmpAggregator.invoke(tmpInitialValues.doubleValue(i));
    }
    return new RandomProcess.SimulationResults(tmpAggregator.doubleValue(),tmpRealisationValues);
  }
}
