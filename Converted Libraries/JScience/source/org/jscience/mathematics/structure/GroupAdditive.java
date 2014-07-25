package org.jscience.mathematics.structure;
/** 
 * This interface represents a structure with a binary additive 
 * operation (+), satisfying the group axioms (associativity, neutral element,
 * inverse element and closure).
 * @author <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 3.0, February 13, 2006
 * @see <a href="http://en.wikipedia.org/wiki/Mathematical_Group">
 *      Wikipedia: Mathematical Group</a>
 */
public interface GroupAdditive<G> extends Structure<G> {
  /** 
 * Returns the sum of this object with the one specified.
 * @param that the object to be added.
 * @return <code>this + that</code>.
 */
  G plus(  G that);
  /** 
 * Returns the additive inverse of this object. It is the object such as
 * <code>this.plus(this.opposite()) == ZERO</code>,
 * with <code>ZERO</code> being the additive identity.
 * @return <code>-this</code>.
 */
  G opposite();
}
