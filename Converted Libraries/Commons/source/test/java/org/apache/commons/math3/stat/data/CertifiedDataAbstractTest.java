package org.apache.commons.math3.stat.data;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.math3.TestUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
/** 
 * @version $Id: CertifiedDataAbstractTest.java 1364030 2012-07-21 01:10:04Z erans $
 */
public abstract class CertifiedDataAbstractTest {
  private DescriptiveStatistics descriptives;
  private SummaryStatistics summaries;
  private Map<String,Double> certifiedValues;
  @Before public void setUp() throws IOException {
    descriptives=new DescriptiveStatistics();
    summaries=new SummaryStatistics();
    certifiedValues=new HashMap<String,Double>();
    loadData();
  }
  private void loadData() throws IOException {
    BufferedReader in=null;
    try {
      URL resourceURL=getClass().getClassLoader().getResource(getResourceName());
      in=new BufferedReader(new InputStreamReader(resourceURL.openStream()));
      String line=in.readLine();
      while (line != null) {
        line=line.trim();
        if (!("".equals(line) || line.startsWith("#"))) {
          int n=line.indexOf('=');
          if (n == -1) {
            double value=Double.parseDouble(line);
            descriptives.addValue(value);
            summaries.addValue(value);
          }
 else {
            String name=line.substring(0,n).trim();
            String valueString=line.substring(n + 1).trim();
            Double value=Double.valueOf(valueString);
            certifiedValues.put(name,value);
          }
        }
        line=in.readLine();
      }
    }
  finally {
      if (in != null) {
        in.close();
      }
    }
  }
  protected abstract String getResourceName();
  protected double getMaximumAbsoluteError(){
    return 1.0e-5;
  }
  @After public void tearDown(){
    descriptives.clear();
    descriptives=null;
    summaries.clear();
    summaries=null;
    certifiedValues.clear();
    certifiedValues=null;
  }
  @Test public void testCertifiedValues(){
    for (    String name : certifiedValues.keySet()) {
      Double expectedValue=certifiedValues.get(name);
      Double summariesValue=getProperty(summaries,name);
      if (summariesValue != null) {
        TestUtils.assertEquals("summary value for " + name + " is incorrect.",summariesValue.doubleValue(),expectedValue.doubleValue(),getMaximumAbsoluteError());
      }
      Double descriptivesValue=getProperty(descriptives,name);
      if (descriptivesValue != null) {
        TestUtils.assertEquals("descriptive value for " + name + " is incorrect.",descriptivesValue.doubleValue(),expectedValue.doubleValue(),getMaximumAbsoluteError());
      }
    }
  }
  protected Double getProperty(  Object bean,  String name){
    try {
      String prop="get" + name.substring(0,1).toUpperCase() + name.substring(1);
      Method meth=bean.getClass().getMethod(prop,new Class[0]);
      Object property=meth.invoke(bean,new Object[0]);
      if (meth.getReturnType().equals(Double.TYPE)) {
        return (Double)property;
      }
 else       if (meth.getReturnType().equals(Long.TYPE)) {
        return Double.valueOf(((Long)property).doubleValue());
      }
 else {
        Assert.fail("wrong type: " + meth.getReturnType().getName());
      }
    }
 catch (    NoSuchMethodException nsme) {
    }
catch (    InvocationTargetException ite) {
      Assert.fail(ite.getMessage());
    }
catch (    IllegalAccessException iae) {
      Assert.fail(iae.getMessage());
    }
    return null;
  }
}
