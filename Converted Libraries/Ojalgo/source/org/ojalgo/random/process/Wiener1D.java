package org.ojalgo.random.process;
import java.util.List;
import org.ojalgo.access.Access2D;
import org.ojalgo.random.Normal;
public class Wiener1D extends Process1D<Normal,WienerProcess> {
  public Wiener1D(  final List<? extends WienerProcess> someProcs){
    super(someProcs);
  }
  public Wiener1D(  final Access2D<?> aCorrelationsMatrix,  final List<? extends WienerProcess> someProcs){
    super(aCorrelationsMatrix,someProcs);
  }
}
