/** 
 * <p>
 * This package holds the main interfaces and basic building block classes
 * dealing with differentiation.
 * The core class is {@link org.apache.commons.math3.analysis.differentiation.DerivativeStructureDerivativeStructure} which holds the value and the differentials of a function. This class
 * handles some arbitrary number of free parameters and arbitrary differentiation order. It is used
 * both as the input and the output type for the {@link org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunctionUnivariateDifferentiableFunction} interface. Any differentiable function should implement this
 * interface.
 * </p>
 * <p>
 * The {@link org.apache.commons.math3.analysis.differentiation.UnivariateFunctionDifferentiatorUnivariateFunctionDifferentiator} interface defines a way to differentiate a simple {@link org.apache.commons.math3.analysis.UnivariateFunction UnivariateFunction} and get a {@link org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunctionUnivariateDifferentiableFunction}.
 * </p>
 * <p>
 * Similar interfaces also exist for multivariate functions and for vector or matrix valued functions.
 * </p>
 */
package org.apache.commons.math3.analysis.differentiation;
