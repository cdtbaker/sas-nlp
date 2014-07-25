package org.apache.commons.math3.transform;
/** 
 * This enumeration defines the various types of normalizations that can be
 * applied to discrete cosine transforms (DCT). The exact definition of these
 * normalizations is detailed below.
 * @see FastCosineTransformer
 * @version $Id: DctNormalization.java 1385310 2012-09-16 16:32:10Z tn $
 * @since 3.0
 */
public enum DctNormalization {/** 
 * Should be passed to the constructor of {@link FastCosineTransformer}to use the <em>standard</em> normalization convention.  The standard
 * DCT-I normalization convention is defined as follows
 * <ul>
 * <li>forward transform:
 * y<sub>n</sub> = (1/2) [x<sub>0</sub> + (-1)<sup>n</sup>x<sub>N-1</sub>]
 * + &sum;<sub>k=1</sub><sup>N-2</sup>
 * x<sub>k</sub> cos[&pi; nk / (N - 1)],</li>
 * <li>inverse transform:
 * x<sub>k</sub> = [1 / (N - 1)] [y<sub>0</sub>
 * + (-1)<sup>k</sup>y<sub>N-1</sub>]
 * + [2 / (N - 1)] &sum;<sub>n=1</sub><sup>N-2</sup>
 * y<sub>n</sub> cos[&pi; nk / (N - 1)],</li>
 * </ul>
 * where N is the size of the data sample.
 */
STANDARD_DCT_I, /** 
 * Should be passed to the constructor of {@link FastCosineTransformer}to use the <em>orthogonal</em> normalization convention. The orthogonal
 * DCT-I normalization convention is defined as follows
 * <ul>
 * <li>forward transform:
 * y<sub>n</sub> = [2(N - 1)]<sup>-1/2</sup> [x<sub>0</sub>
 * + (-1)<sup>n</sup>x<sub>N-1</sub>]
 * + [2 / (N - 1)]<sup>1/2</sup> &sum;<sub>k=1</sub><sup>N-2</sup>
 * x<sub>k</sub> cos[&pi; nk / (N - 1)],</li>
 * <li>inverse transform:
 * x<sub>k</sub> = [2(N - 1)]<sup>-1/2</sup> [y<sub>0</sub>
 * + (-1)<sup>k</sup>y<sub>N-1</sub>]
 * + [2 / (N - 1)]<sup>1/2</sup> &sum;<sub>n=1</sub><sup>N-2</sup>
 * y<sub>n</sub> cos[&pi; nk / (N - 1)],</li>
 * </ul>
 * which makes the transform orthogonal. N is the size of the data sample.
 */
ORTHOGONAL_DCT_I}
