package org.jscience.mathematics.structure;
/** 
 * This interface represents an algebraic structure in which the operations of 
 * addition, subtraction, multiplication and division (except division by zero)
 * may be performed. It is not required for the multiplication to be 
 * commutative (non commutative fields are also called division rings
 * or skew fields). 
 * @author <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 3.0, February 13, 2006
 * @see <a href="http://en.wikipedia.org/wiki/Field_mathematics">
 *      Wikipedia: Field (mathematics)</a>
 */
public interface Field<F> extends Ring<F>, GroupMultiplicative<F> {
}
