/** 
 * Generic univariate summary statistic objects.
 * <h3>UnivariateStatistic API Usage Examples:</h3>
 * <h4>UnivariateStatistic:</h4>
 * <code>/&lowast; evaluation approach &lowast;/<br/>
 * double[] values = new double[] { 1, 2, 3, 4, 5 };<br/>
 * <span style="font-weight: bold;">UnivariateStatistic stat = new Mean();</span><br/>
 * out.println("mean = " + <span style="font-weight: bold;">stat.evaluate(values)</span>);<br/>
 * </code>
 * <h4>StorelessUnivariateStatistic:</h4>
 * <code>/&lowast; incremental approach &lowast;/<br/>
 * double[] values = new double[] { 1, 2, 3, 4, 5 };<br/>
 * <span style="font-weight: bold;">StorelessUnivariateStatistic stat = new Mean();</span><br/>
 * out.println("mean before adding a value is NaN = " + <span style="font-weight: bold;">stat.getResult()</span>);<br/>
 * for (int i = 0; i &lt; values.length; i++) {<br/>
 * &nbsp;&nbsp;&nbsp; <span style="font-weight: bold;">stat.increment(values[i]);</span><br/>
 * &nbsp;&nbsp;&nbsp; out.println("current mean = " + <span style="font-weight: bold;">stat2.getResult()</span>);<br/>
 * }<br/>
 * <span style="font-weight: bold;"> stat.clear();</span><br/>
 * out.println("mean after clear is NaN = " + <span style="font-weight: bold;">stat.getResult()</span>);
 * </code>
 */
package org.apache.commons.math3.stat.descriptive;
