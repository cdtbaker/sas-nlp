package org.ojalgo.optimisation.mps;
/** 
 * RowType used with the ROWS and RANGES sections.
 * type      meaning
 * ---------------------------
 * E    equality
 * L    less than or equal
 * G    greater than or equal
 * N    objective
 * N    no restriction
 * row type       sign of r       h          u
 * ----------------------------------------------
 * G            + or -         b        b + |r|
 * L            + or -       b - |r|      b
 * E              +            b        b + |r|
 * E              -          b - |r|      b
 * @author apete
 */
public enum RowType {E, G, L, N}
