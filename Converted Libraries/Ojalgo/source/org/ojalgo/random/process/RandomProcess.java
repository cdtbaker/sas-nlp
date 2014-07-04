package org.ojalgo.random.process;
import org.ojalgo.array.Array1D;
import org.ojalgo.array.Array2D;
import org.ojalgo.random.Distribution;
import org.ojalgo.random.SampleSet;
import org.ojalgo.series.primitive.PrimitiveSeries;
/** 
 * A random/stochastic process is a collection of random variables representing
 * the evolution of some random value over "time".
 * @author apete
 */
public interface RandomProcess<D extends Distribution> {
public static final class SimulationResults {
    private final double myInitialValue;
    private final Array2D<Double> myResults;
    /** 
 * @param initialValue
 * @param results (Random values) scenarios/realisations/series in rows, and sample sets in columns.
 */
    public SimulationResults(    final double initialValue,    final Array2D<Double> results){
      super();
      myInitialValue=initialValue;
      myResults=results;
    }
    @SuppressWarnings("unused") private SimulationResults(){
      super();
      myInitialValue=0.0;
      myResults=null;
    }
    public int countSampleSets(){
      return myResults.getColDim();
    }
    public int countScenarios(){
      return myResults.getRowDim();
    }
    public double getInitialValue(){
      return myInitialValue;
    }
    public SampleSet getSampleSet(    final int index){
      return SampleSet.wrap(myResults.sliceColumn(0,index));
    }
    /** 
 * A series representing one scenario. Each series has length
 * "number of simulation steps" + 1 as the series includes the initial value.
 */
    public PrimitiveSeries getScenario(    final int index){
      final Array1D<Double> tmpSlicedRow=myResults.sliceRow(index,0);
      return new PrimitiveSeries(){
        public int size(){
          return tmpSlicedRow.size() + 1;
        }
        @Override public double value(        final int index){
          if (index == 0) {
            return myInitialValue;
          }
 else {
            return tmpSlicedRow.doubleValue(index - 1);
          }
        }
      }
;
    }
  }
  /** 
 * Calling this method repeatedly gives the same ressult, unless
 * you call {@linkplain #step(double)} inbetween.
 * @param evaluationPoint How far into the future?
 * @return The distribution for the process value at that future time.
 */
  D getDistribution(  double evaluationPoint);
  /** 
 * @return An array of sample sets. The array has aNumberOfSteps
 * elements, and each sample set has aNumberOfRealisations samples.
 */
  RandomProcess.SimulationResults simulate(  final int numberOfRealisations,  final int numberOfSteps,  final double stepSize);
}
