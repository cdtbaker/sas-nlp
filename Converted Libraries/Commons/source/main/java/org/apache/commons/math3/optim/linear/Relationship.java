package org.apache.commons.math3.optim.linear;
/** 
 * Types of relationships between two cells in a Solver {@link LinearConstraint}.
 * @version $Id: Relationship.java 1435539 2013-01-19 13:27:24Z tn $
 * @since 2.0
 */
public enum Relationship {/** 
 * Equality relationship. 
 */
EQ("="), /** 
 * Lesser than or equal relationship. 
 */
LEQ("<="), /** 
 * Greater than or equal relationship. 
 */
GEQ(">="); /** 
 * Display string for the relationship. 
 */
private final String stringValue;
/** 
 * Simple constructor.
 * @param stringValue Display string for the relationship.
 */
private Relationship(String stringValue){
  this.stringValue=stringValue;
}
@Override public String toString(){
  return stringValue;
}
/** 
 * Gets the relationship obtained when multiplying all coefficients by -1.
 * @return the opposite relationship.
 */
public Relationship oppositeRelationship(){
switch (this) {
case LEQ:
    return GEQ;
case GEQ:
  return LEQ;
default :
return EQ;
}
}
}
