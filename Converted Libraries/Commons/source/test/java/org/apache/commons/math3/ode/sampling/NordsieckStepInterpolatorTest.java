package org.apache.commons.math3.ode.sampling;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Random;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.NoBracketingException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.ode.ContinuousOutputModel;
import org.apache.commons.math3.ode.TestProblem1;
import org.apache.commons.math3.ode.TestProblem3;
import org.apache.commons.math3.ode.nonstiff.AdamsBashforthIntegrator;
import org.junit.Assert;
import org.junit.Test;
public class NordsieckStepInterpolatorTest {
  @Test public void derivativesConsistency() throws NumberIsTooSmallException, DimensionMismatchException, MaxCountExceededException, NoBracketingException {
    TestProblem3 pb=new TestProblem3();
    AdamsBashforthIntegrator integ=new AdamsBashforthIntegrator(4,0.0,1.0,1.0e-10,1.0e-10);
    StepInterpolatorTestUtils.checkDerivativesConsistency(integ,pb,5e-9);
  }
  @Test public void serialization() throws IOException, ClassNotFoundException, NumberIsTooSmallException, DimensionMismatchException, MaxCountExceededException, NoBracketingException {
    TestProblem1 pb=new TestProblem1();
    AdamsBashforthIntegrator integ=new AdamsBashforthIntegrator(4,0.0,1.0,1.0e-10,1.0e-10);
    integ.addStepHandler(new ContinuousOutputModel());
    integ.integrate(pb,pb.getInitialTime(),pb.getInitialState(),pb.getFinalTime(),new double[pb.getDimension()]);
    ByteArrayOutputStream bos=new ByteArrayOutputStream();
    ObjectOutputStream oos=new ObjectOutputStream(bos);
    for (    StepHandler handler : integ.getStepHandlers()) {
      oos.writeObject(handler);
    }
    Assert.assertTrue(bos.size() > 25500);
    Assert.assertTrue(bos.size() < 26500);
    ByteArrayInputStream bis=new ByteArrayInputStream(bos.toByteArray());
    ObjectInputStream ois=new ObjectInputStream(bis);
    ContinuousOutputModel cm=(ContinuousOutputModel)ois.readObject();
    Random random=new Random(347588535632l);
    double maxError=0.0;
    for (int i=0; i < 1000; ++i) {
      double r=random.nextDouble();
      double time=r * pb.getInitialTime() + (1.0 - r) * pb.getFinalTime();
      cm.setInterpolatedTime(time);
      double[] interpolatedY=cm.getInterpolatedState();
      double[] theoreticalY=pb.computeTheoreticalState(time);
      double dx=interpolatedY[0] - theoreticalY[0];
      double dy=interpolatedY[1] - theoreticalY[1];
      double error=dx * dx + dy * dy;
      if (error > maxError) {
        maxError=error;
      }
    }
    Assert.assertTrue(maxError < 1.0e-6);
  }
}
