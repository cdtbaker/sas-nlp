package org.ejml.factory;
/** 
 * This exception is thrown if an operation can not be finished because the matrix is singular.
 * It is a RuntimeException to allow the code to be written cleaner and also because singular
 * matrices are not always detected.  Forcing an exception to be caught provides a false sense
 * of security.
 * @author Peter Abeles
 */
public class SingularMatrixException extends RuntimeException {
}
