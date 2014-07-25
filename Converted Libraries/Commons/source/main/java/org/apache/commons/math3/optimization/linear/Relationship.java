package org.apache.commons.math3.optimization.linear;
/** 
 * Types of relationships between two cells in a Solver {@link LinearConstraint}.
 * @version $Id: Relationship.java 1422230 2012-12-15 12:11:13Z erans $
 * @deprecated As of 3.1 (to be removed in 4.0).
 * @since 2.0
 */
@Deprecated public enum Relationship {/** 
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
 * @param stringValue display string for the relationship
 */
private Relationship(String stringValue){
  this.stringValue=stringValue;
}
@Override public String toString(){
  return stringValue;
}
/** 
 * Get the relationship obtained when multiplying all coefficients by -1.
 * @return relationship obtained when multiplying all coefficients by -1
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
