package org.apache.commons.math3.transform;
/** 
 * This enumeration defines the various types of normalizations that can be
 * applied to discrete Fourier transforms (DFT). The exact definition of these
 * normalizations is detailed below.
 * @see FastFourierTransformer
 * @version $Id: DftNormalization.java 1385310 2012-09-16 16:32:10Z tn $
 * @since 3.0
 */
public enum DftNormalization {/** 
 * Should be passed to the constructor of {@link FastFourierTransformer}to use the <em>standard</em> normalization convention. This normalization
 * convention is defined as follows
 * <ul>
 * <li>forward transform: y<sub>n</sub> = &sum;<sub>k=0</sub><sup>N-1</sup>
 * x<sub>k</sub> exp(-2&pi;i n k / N),</li>
 * <li>inverse transform: x<sub>k</sub> = N<sup>-1</sup>
 * &sum;<sub>n=0</sub><sup>N-1</sup> y<sub>n</sub> exp(2&pi;i n k / N),</li>
 * </ul>
 * where N is the size of the data sample.
 */
STANDARD, /** 
 * Should be passed to the constructor of {@link FastFourierTransformer}to use the <em>unitary</em> normalization convention. This normalization
 * convention is defined as follows
 * <ul>
 * <li>forward transform: y<sub>n</sub> = (1 / &radic;N)
 * &sum;<sub>k=0</sub><sup>N-1</sup> x<sub>k</sub>
 * exp(-2&pi;i n k / N),</li>
 * <li>inverse transform: x<sub>k</sub> = (1 / &radic;N)
 * &sum;<sub>n=0</sub><sup>N-1</sup> y<sub>n</sub> exp(2&pi;i n k / N),</li>
 * </ul>
 * which makes the transform unitary. N is the size of the data sample.
 */
UNITARY}
