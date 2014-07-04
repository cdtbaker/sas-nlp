package org.apache.commons.math3.util;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
/** 
 * Test to compare FastMath results against StrictMath results for boundary values.
 * <p>
 * Running all tests independently: <br/>{@code mvn test -Dtest=FastMathStrictComparisonTest}<br/>
 * or just run tests against a single method (e.g. scalb):<br/>{@code mvn test -Dtest=FastMathStrictComparisonTest -DargLine="-DtestMethod=scalb"}
 */
@RunWith(Parameterized.class) public class FastMathStrictComparisonTest {
  private static final Double[] DOUBLE_SPECIAL_VALUES={-0.0,+0.0,Double.NaN,Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY,-Double.MAX_VALUE,Double.MAX_VALUE,-Precision.EPSILON,Precision.EPSILON,-Precision.SAFE_MIN,Precision.SAFE_MIN,-Double.MIN_VALUE,Double.MIN_VALUE};
  private static final Float[] FLOAT_SPECIAL_VALUES={-0.0f,+0.0f,Float.NaN,Float.NEGATIVE_INFINITY,Float.POSITIVE_INFINITY,Float.MIN_VALUE,Float.MAX_VALUE,-Float.MIN_VALUE,-Float.MAX_VALUE};
  private static final Object[] LONG_SPECIAL_VALUES={-1,0,1,Long.MIN_VALUE,Long.MAX_VALUE};
  private static final Object[] INT_SPECIAL_VALUES={-1,0,1,Integer.MIN_VALUE,Integer.MAX_VALUE};
  private final Method mathMethod;
  private final Method fastMethod;
  private final Type[] types;
  private final Object[][] valueArrays;
  public FastMathStrictComparisonTest(  Method m,  Method f,  Type[] types,  Object[][] data) throws Exception {
    this.mathMethod=m;
    this.fastMethod=f;
    this.types=types;
    this.valueArrays=data;
  }
  @Test public void test1() throws Exception {
    setupMethodCall(mathMethod,fastMethod,types,valueArrays);
  }
  private static boolean isNumber(  Double d){
    return !(d.isInfinite() || d.isNaN());
  }
  private static boolean isNumber(  Float f){
    return !(f.isInfinite() || f.isNaN());
  }
  private static void reportFailedResults(  Method mathMethod,  Object[] params,  Object expected,  Object actual,  int[] entries){
    final String methodName=mathMethod.getName();
    String format=null;
    long actL=0;
    long expL=0;
    if (expected instanceof Double) {
      Double exp=(Double)expected;
      Double act=(Double)actual;
      if (isNumber(exp) && isNumber(act) && exp != 0) {
        actL=Double.doubleToLongBits(act);
        expL=Double.doubleToLongBits(exp);
        if (Math.abs(actL - expL) == 1) {
          if (methodName.equals("toRadians") || methodName.equals("atan2")) {
            return;
          }
        }
        format="%016x";
      }
    }
 else     if (expected instanceof Float) {
      Float exp=(Float)expected;
      Float act=(Float)actual;
      if (isNumber(exp) && isNumber(act) && exp != 0) {
        actL=Float.floatToIntBits(act);
        expL=Float.floatToIntBits(exp);
        format="%08x";
      }
    }
    StringBuilder sb=new StringBuilder();
    sb.append(mathMethod.getReturnType().getSimpleName());
    sb.append(" ");
    sb.append(methodName);
    sb.append("(");
    String sep="";
    for (    Object o : params) {
      sb.append(sep);
      sb.append(o);
      sep=", ";
    }
    sb.append(") expected ");
    if (format != null) {
      sb.append(String.format(format,expL));
    }
 else {
      sb.append(expected);
    }
    sb.append(" actual ");
    if (format != null) {
      sb.append(String.format(format,actL));
    }
 else {
      sb.append(actual);
    }
    sb.append(" entries ");
    sb.append(Arrays.toString(entries));
    String message=sb.toString();
    final boolean fatal=true;
    if (fatal) {
      Assert.fail(message);
    }
 else {
      System.out.println(message);
    }
  }
  private static void callMethods(  Method mathMethod,  Method fastMethod,  Object[] params,  int[] entries) throws IllegalAccessException, InvocationTargetException {
    try {
      Object expected=mathMethod.invoke(mathMethod,params);
      Object actual=fastMethod.invoke(mathMethod,params);
      if (!expected.equals(actual)) {
        reportFailedResults(mathMethod,params,expected,actual,entries);
      }
    }
 catch (    IllegalArgumentException e) {
      Assert.fail(mathMethod + " " + e);
    }
  }
  private static void setupMethodCall(  Method mathMethod,  Method fastMethod,  Type[] types,  Object[][] valueArrays) throws Exception {
    Object[] params=new Object[types.length];
    int entry1=0;
    int[] entries=new int[types.length];
    for (    Object d : valueArrays[0]) {
      entry1++;
      params[0]=d;
      entries[0]=entry1;
      if (params.length > 1) {
        int entry2=0;
        for (        Object d1 : valueArrays[1]) {
          entry2++;
          params[1]=d1;
          entries[1]=entry2;
          callMethods(mathMethod,fastMethod,params,entries);
        }
      }
 else {
        callMethods(mathMethod,fastMethod,params,entries);
      }
    }
  }
  @Parameters public static List<Object[]> data() throws Exception {
    String singleMethod=System.getProperty("testMethod");
    List<Object[]> list=new ArrayList<Object[]>();
    for (    Method mathMethod : StrictMath.class.getDeclaredMethods()) {
      method:       if (Modifier.isPublic(mathMethod.getModifiers())) {
        Type[] types=mathMethod.getGenericParameterTypes();
        if (types.length >= 1) {
          try {
            Method fastMethod=FastMath.class.getDeclaredMethod(mathMethod.getName(),(Class[])types);
            if (Modifier.isPublic(fastMethod.getModifiers())) {
              if (singleMethod != null && !fastMethod.getName().equals(singleMethod)) {
                break method;
              }
              Object[][] values=new Object[types.length][];
              int index=0;
              for (              Type t : types) {
                if (t.equals(double.class)) {
                  values[index]=DOUBLE_SPECIAL_VALUES;
                }
 else                 if (t.equals(float.class)) {
                  values[index]=FLOAT_SPECIAL_VALUES;
                }
 else                 if (t.equals(long.class)) {
                  values[index]=LONG_SPECIAL_VALUES;
                }
 else                 if (t.equals(int.class)) {
                  values[index]=INT_SPECIAL_VALUES;
                }
 else {
                  System.out.println("Cannot handle class " + t + " for "+ mathMethod);
                  break method;
                }
                index++;
              }
              list.add(new Object[]{mathMethod,fastMethod,types,values});
            }
 else {
              System.out.println("Cannot find public FastMath method corresponding to: " + mathMethod);
            }
          }
 catch (          NoSuchMethodException e) {
            System.out.println("Cannot find FastMath method corresponding to: " + mathMethod);
          }
        }
      }
    }
    return list;
  }
}
