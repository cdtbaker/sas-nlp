package org.ojalgo.random.process;
import java.util.List;
import org.ojalgo.access.Access1D;
import org.ojalgo.access.Access2D;
import org.ojalgo.array.Array1D;
import org.ojalgo.array.SimpleArray;
import org.ojalgo.random.ContinuousDistribution;
import org.ojalgo.random.Random1D;
import org.ojalgo.random.process.RandomProcess.SimulationResults;
abstract class Process1D<D extends ContinuousDistribution,P extends AbstractProcess<D>> {
  private final Random1D myGenerator;
  private final AbstractProcess<? extends D>[] myProcesses;
  @SuppressWarnings("unused") private Process1D(){
    this(null,null);
  }
  @SuppressWarnings("unchecked") protected Process1D(  final Access2D<?> aCorrelationsMatrix,  final List<? extends P> someProcs){
    super();
    myGenerator=new Random1D(aCorrelationsMatrix);
    myProcesses=someProcs.toArray(new AbstractProcess[someProcs.size()]);
  }
  @SuppressWarnings("unchecked") protected Process1D(  final List<? extends P> someProcs){
    super();
    final int tmpSize=someProcs.size();
    myGenerator=new Random1D(tmpSize);
    myProcesses=someProcs.toArray(new AbstractProcess[tmpSize]);
  }
  public double getValue(  final int index){
    return myProcesses[index].getValue();
  }
  public SimpleArray.Primitive getValues(){
    final int tmpLength=myProcesses.length;
    final SimpleArray.Primitive retVal=SimpleArray.makePrimitive(tmpLength);
    for (int p=0; p < tmpLength; p++) {
      retVal.set(p,myProcesses[p].getValue());
    }
    return retVal;
  }
  public void setValue(  final int index,  final double newValue){
    myProcesses[index].setValue(newValue);
  }
  public void setValues(  final Access1D<?> aValue){
    for (int p=0; p < myProcesses.length; p++) {
      myProcesses[p].setValue(aValue.doubleValue(p));
    }
  }
  public int size(){
    return myProcesses.length;
  }
  public Array1D<Double> step(  final double aStepSize){
    final Array1D<Double> retVal=myGenerator.nextGaussian();
    for (int p=0; p < myProcesses.length; p++) {
      retVal.set(p,myProcesses[p].step(this.getValue(p),aStepSize,retVal.doubleValue(p)));
    }
    return retVal;
  }
  protected AbstractProcess<?> getProcess(  final int index){
    return myProcesses[index];
  }
  D getDistribution(  final int index,  final double aStepSize){
    return myProcesses[index].getDistribution(aStepSize);
  }
  double getExpected(  final int index,  final double aStepSize){
    return myProcesses[index].getExpected(aStepSize);
  }
  double getLowerConfidenceQuantile(  final int index,  final double aStepSize,  final double aConfidence){
    return myProcesses[index].getLowerConfidenceQuantile(aStepSize,aConfidence);
  }
  double getStandardDeviation(  final int index,  final double aStepSize){
    return myProcesses[index].getStandardDeviation(aStepSize);
  }
  double getUpperConfidenceQuantile(  final int index,  final double aStepSize,  final double aConfidence){
    return myProcesses[index].getUpperConfidenceQuantile(aStepSize,aConfidence);
  }
  double getVariance(  final int index,  final double aStepSize){
    return myProcesses[index].getVariance(aStepSize);
  }
  SimulationResults simulate(  final int index,  final int aNumberOfRealisations,  final int aNumberOfSteps,  final double aStepSize){
    return myProcesses[index].simulate(aNumberOfRealisations,aNumberOfSteps,aStepSize);
  }
  double step(  final int index,  final double aStepSize){
    return myProcesses[index].step(aStepSize);
  }
}
