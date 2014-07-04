package sun.reflect;
/** 
 * <P> Java serialization (in java.io) expects to be able to
 * instantiate a class and invoke a no-arg constructor of that
 * class's first non-Serializable superclass. This is not a valid
 * operation according to the VM specification; one can not (for
 * classes A and B, where B is a subclass of A) write "new B;
 * invokespecial A()" without getting a verification error. </P>
 * <P> In all other respects, the bytecode-based reflection framework
 * can be reused for this purpose. This marker class was originally
 * known to the VM and verification disabled for it and all
 * subclasses, but the bug fix for 4486457 necessitated disabling
 * verification for all of the dynamically-generated bytecodes
 * associated with reflection. This class has been left in place to
 * make future debugging easier. </P> 
 */
abstract class SerializationConstructorAccessorImpl extends ConstructorAccessorImpl {
}
