package org.jscience.mathematics.structure;
/** 
 * This interface represents an algebraic structure with two binary operations
 * addition and multiplication (+ and ·), such that (R, +) is an abelian group, 
 * (R, ·) is a monoid and the multiplication distributes over the addition.
 * @author <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 3.0, February 13, 2006
 * @see <a href="http://en.wikipedia.org/wiki/Mathematical_ring">
 *      Wikipedia: Mathematical Ring</a>
 */
public interface Ring<R> extends GroupAdditive<R> {
  /** 
 * Returns the product of this object with the one specified.
 * @param that the object multiplier.
 * @return <code>this · that</code>.
 */
  R times(  R that);
}
