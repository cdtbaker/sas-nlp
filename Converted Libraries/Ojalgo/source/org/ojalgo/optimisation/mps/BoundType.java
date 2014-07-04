package org.ojalgo.optimisation.mps;
/** 
 * BoundType used with the BOUNDS section.
 * type            meaning
 * ---------------------------------------------------
 * LO    lower bound        b <= x (< +inf)
 * UP    upper bound        (0 <=) x <= b
 * FX    fixed variable     x = b
 * FR    free variable      -inf < x < +inf
 * MI    lower bound -inf   -inf < x (<= 0)
 * PL    upper bound +inf   (0 <=) x < +inf
 * BV    binary variable    x = 0 or 1
 * LI    integer variable   b <= x (< +inf)
 * UI    integer variable   (0 <=) x <= b
 * SC    semi-cont variable x = 0 or l <= x <= b
 * l is the lower bound on the variable
 * If none set then defaults to 1
 * @author apete
 */
public enum BoundType {BV, FR, FX, LI, LO, MI, PL, SC, UI, UP}
