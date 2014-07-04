package org.ojalgo.function.multiary;
import org.ojalgo.access.Access1D;
import org.ojalgo.function.Function;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.matrix.store.PhysicalStore;
public interface MultiaryFunction<N extends Number> extends Function<N> {
public static interface Constant<N extends Number,F extends Constant<N,?>> extends MultiaryFunction<N> {
    F constant(    Number constant);
    N getConstant();
    void setConstant(    Number constant);
  }
public static interface Linear<N extends Number> extends MultiaryFunction<N> {
    PhysicalStore<N> linear();
  }
public static interface Quadratic<N extends Number> extends MultiaryFunction<N> {
    PhysicalStore<N> quadratic();
  }
  int arity();
  /** 
 * @deprecated Since v35. Use {@link #arity()} instead
 */
  @Deprecated int dim();
  /** 
 * The gradient of a scalar field is a vector field that points in the
 * direction of the greatest rate of increase of the scalar field, and whose
 * magnitude is that rate of increase. In simple terms, the variation in
 * space of any quantity can be represented (e.g. graphically) by a slope.
 * The gradient represents the steepness and direction of that slope.
 * The Jacobian is a generalization of the gradient. Gradients are only
 * defined on scalar-valued functions, but Jacobians are defined on vector-
 * valued functions.
 * When f is real-valued (i.e., f : Rn → R) the derivative Df(x) is a 1 × n
 * matrix, i.e., it is a row vector. Its transpose is called the gradient of
 * the function: ∇f(x) = Df(x)T , which is a (column) vector, i.e., in Rn.
 * Its components are the partial derivatives of f: The first-order
 * approximation of f at a point x ∈ int dom f can be expressed as (the affine
 * function of z) f(x) + ∇f(x)T (z − x).
 */
  MatrixStore<N> getGradient(  Access1D<?> arg);
  /** 
 * The Hessian matrix or Hessian is a square matrix of second-order partial
 * derivatives of a function. It describes the local curvature of a function
 * of many variables.
 * The Hessian is the Jacobian of the gradient.
 * The second-order approximation of f, at or near x, is the quadratic
 * function of z defined by
 * f(z) = f(x) + ∇f(x)T (z − x) + (1/2)(z − x)T ∇2f(x)(z − x)
 */
  MatrixStore<N> getHessian(  Access1D<?> arg);
  N invoke(  Access1D<?> arg);
}
