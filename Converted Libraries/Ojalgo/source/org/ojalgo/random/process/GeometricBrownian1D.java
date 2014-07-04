package org.ojalgo.random.process;
import java.util.List;
import org.ojalgo.access.Access2D;
import org.ojalgo.random.LogNormal;
public class GeometricBrownian1D extends Process1D<LogNormal,GeometricBrownianMotion> {
  public GeometricBrownian1D(  final Access2D<?> aCorrelationsMatrix,  final List<? extends GeometricBrownianMotion> someProcs){
    super(aCorrelationsMatrix,someProcs);
  }
  public GeometricBrownian1D(  final List<? extends GeometricBrownianMotion> someProcs){
    super(someProcs);
  }
}
