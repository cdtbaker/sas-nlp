package org.apache.commons.math3.ode.nonstiff;
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
import org.apache.commons.math3.ode.TestProblem3;
import org.apache.commons.math3.ode.sampling.StepHandler;
import org.apache.commons.math3.ode.sampling.StepInterpolatorTestUtils;
import org.junit.Assert;
import org.junit.Test;
public class GillStepInterpolatorTest {
  @Test public void testDerivativesConsistency() throws DimensionMismatchException, NumberIsTooSmallException, MaxCountExceededException, NoBracketingException {
    TestProblem3 pb=new TestProblem3();
    double step=(pb.getFinalTime() - pb.getInitialTime()) * 0.001;
    GillIntegrator integ=new GillIntegrator(step);
    StepInterpolatorTestUtils.checkDerivativesConsistency(integ,pb,1.0e-10);
  }
  @Test public void serialization() throws IOException, ClassNotFoundException, DimensionMismatchException, NumberIsTooSmallException, MaxCountExceededException, NoBracketingException {
    TestProblem3 pb=new TestProblem3(0.9);
    double step=(pb.getFinalTime() - pb.getInitialTime()) * 0.0003;
    GillIntegrator integ=new GillIntegrator(step);
    integ.addStepHandler(new ContinuousOutputModel());
    integ.integrate(pb,pb.getInitialTime(),pb.getInitialState(),pb.getFinalTime(),new double[pb.getDimension()]);
    ByteArrayOutputStream bos=new ByteArrayOutputStream();
    ObjectOutputStream oos=new ObjectOutputStream(bos);
    for (    StepHandler handler : integ.getStepHandlers()) {
      oos.writeObject(handler);
    }
    Assert.assertTrue(bos.size() > 880000);
    Assert.assertTrue(bos.size() < 900000);
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
    Assert.assertTrue(maxError < 0.003);
  }
}