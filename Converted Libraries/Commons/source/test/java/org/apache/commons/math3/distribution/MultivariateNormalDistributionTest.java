package org.apache.commons.math3.distribution;
import org.apache.commons.math3.stat.correlation.Covariance;
import org.apache.commons.math3.linear.RealMatrix;
import java.util.Random;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
/** 
 * Test cases for {@link MultivariateNormalDistribution}.
 */
public class MultivariateNormalDistributionTest {
  /** 
 * Test the ability of the distribution to report its mean value parameter.
 */
  @Test public void testGetMean(){
    final double[] mu={-1.5,2};
    final double[][] sigma={{2,-1.1},{-1.1,2}};
    final MultivariateNormalDistribution d=new MultivariateNormalDistribution(mu,sigma);
    final double[] m=d.getMeans();
    for (int i=0; i < m.length; i++) {
      Assert.assertEquals(mu[i],m[i],0);
    }
  }
  /** 
 * Test the ability of the distribution to report its covariance matrix parameter.
 */
  @Test public void testGetCovarianceMatrix(){
    final double[] mu={-1.5,2};
    final double[][] sigma={{2,-1.1},{-1.1,2}};
    final MultivariateNormalDistribution d=new MultivariateNormalDistribution(mu,sigma);
    final RealMatrix s=d.getCovariances();
    final int dim=d.getDimension();
    for (int i=0; i < dim; i++) {
      for (int j=0; j < dim; j++) {
        Assert.assertEquals(sigma[i][j],s.getEntry(i,j),0);
      }
    }
  }
  /** 
 * Test the accuracy of sampling from the distribution.
 */
  @Test public void testSampling(){
    final double[] mu={-1.5,2};
    final double[][] sigma={{2,-1.1},{-1.1,2}};
    final MultivariateNormalDistribution d=new MultivariateNormalDistribution(mu,sigma);
    d.reseedRandomGenerator(50);
    final int n=500000;
    final double[][] samples=d.sample(n);
    final int dim=d.getDimension();
    final double[] sampleMeans=new double[dim];
    for (int i=0; i < samples.length; i++) {
      for (int j=0; j < dim; j++) {
        sampleMeans[j]+=samples[i][j];
      }
    }
    final double sampledValueTolerance=1e-2;
    for (int j=0; j < dim; j++) {
      sampleMeans[j]/=samples.length;
      Assert.assertEquals(mu[j],sampleMeans[j],sampledValueTolerance);
    }
    final double[][] sampleSigma=new Covariance(samples).getCovarianceMatrix().getData();
    for (int i=0; i < dim; i++) {
      for (int j=0; j < dim; j++) {
        Assert.assertEquals(sigma[i][j],sampleSigma[i][j],sampledValueTolerance);
      }
    }
  }
  /** 
 * Test the accuracy of the distribution when calculating densities.
 */
  @Test public void testDensities(){
    final double[] mu={-1.5,2};
    final double[][] sigma={{2,-1.1},{-1.1,2}};
    final MultivariateNormalDistribution d=new MultivariateNormalDistribution(mu,sigma);
    final double[][] testValues={{-1.5,2},{4,4},{1.5,-2},{0,0}};
    final double[] densities=new double[testValues.length];
    for (int i=0; i < densities.length; i++) {
      densities[i]=d.density(testValues[i]);
    }
    final double[] correctDensities={0.09528357207691344,5.80932710124009e-09,0.001387448895173267,0.03309922090210541};
    for (int i=0; i < testValues.length; i++) {
      Assert.assertEquals(correctDensities[i],densities[i],1e-16);
    }
  }
  /** 
 * Test the accuracy of the distribution when calculating densities.
 */
  @Test public void testUnivariateDistribution(){
    final double[] mu={-1.5};
    final double[][] sigma={{1}};
    final MultivariateNormalDistribution multi=new MultivariateNormalDistribution(mu,sigma);
    final NormalDistribution uni=new NormalDistribution(mu[0],sigma[0][0]);
    final Random rng=new Random();
    final int numCases=100;
    final double tol=Math.ulp(1d);
    for (int i=0; i < numCases; i++) {
      final double v=rng.nextDouble() * 10 - 5;
      Assert.assertEquals(uni.density(v),multi.density(new double[]{v}),tol);
    }
  }
}