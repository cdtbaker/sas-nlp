/*
 * Copyright 1997-2014 Optimatika (www.optimatika.se)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.ojalgo.scalar;

import java.math.BigDecimal;
import java.math.MathContext;

import org.ojalgo.constant.BigMath;
import org.ojalgo.function.BigFunction;
import org.ojalgo.type.TypeUtils;
import org.ojalgo.type.context.NumberContext;
import org.ojalgo.type.context.NumberContext.Enforceable;

public final class BigScalar extends AbstractScalar<BigDecimal> implements Enforceable<BigScalar> {

    public static final Scalar.Factory<BigDecimal> FACTORY = new Scalar.Factory<BigDecimal>() {

        public BigDecimal cast(final double value) {
            return new BigDecimal(value);
        }

        public BigDecimal cast(final Number number) {
            return TypeUtils.toBigDecimal(number);
        }

        public BigScalar convert(final double value) {
            return new BigScalar(value);
        }

        public BigScalar convert(final Number number) {
            return new BigScalar(TypeUtils.toBigDecimal(number));
        }

        public BigScalar one() {
            return ONE;
        }

        public BigScalar zero() {
            return ZERO;
        }

    };

    public static final boolean IS_INFINITE = false;
    public static final boolean IS_NOT_A_NUMBER = false;
    public static final boolean IS_REAL = true;
    public static final BigScalar ONE = new BigScalar(BigMath.ONE);
    public static final NumberContext PRECISION = NumberContext.getMath(MathContext.DECIMAL128).newScale(32);
    public static final BigScalar ZERO = new BigScalar();

    public static boolean isAbsolute(final BigDecimal value) {
        return value.signum() >= 0;
    }

    public static boolean isPositive(final BigDecimal value) {
        return (value.signum() > 0) && !BigScalar.isZero(value);
    }

    public static boolean isZero(final BigDecimal value) {
        return PRECISION.enforce(value).signum() == 0;
    }

    private final BigDecimal myNumber;

    public BigScalar(final BigDecimal aNmbr) {

        super();

        myNumber = aNmbr;
    }

    public BigScalar(final Number aNmbr) {

        super();

        myNumber = TypeUtils.toBigDecimal(aNmbr);
    }

    private BigScalar() {

        super();

        myNumber = BigMath.ZERO;
    }

    public BigScalar add(final BigDecimal arg) {
        return new BigScalar(myNumber.add(arg));
    }

    public BigScalar add(final double arg) {
        return this.add(new BigDecimal(arg));
    }

    public int compareTo(final BigDecimal reference) {
        return myNumber.compareTo(reference);
    }

    public BigScalar conjugate() {
        return this;
    }

    public BigScalar divide(final BigDecimal arg) {
        return new BigScalar(myNumber.divide(arg, PRECISION.getMathContext()));
    }

    public Scalar<BigDecimal> divide(final double arg) {
        return this.divide(new BigDecimal(arg));
    }

    @Override
    public double doubleValue() {
        return myNumber.doubleValue();
    }

    public BigScalar enforce(final NumberContext context) {
        return new BigScalar(context.enforce(myNumber));
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Scalar<?>)) {
            return false;
        }
        final BigDecimal other = ((Scalar<?>) obj).toBigDecimal();
        if (myNumber == null) {
            if (other != null) {
                return false;
            }
        } else if (!myNumber.equals(other)) {
            return false;
        }
        return true;
    }

    @Override
    public float floatValue() {
        return myNumber.floatValue();
    }

    public BigDecimal getNumber() {
        return myNumber;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((myNumber == null) ? 0 : myNumber.hashCode());
        return result;
    }

    @Override
    public int intValue() {
        return myNumber.intValueExact();
    }

    public BigScalar invert() {
        return ONE.divide(myNumber);
    }

    public boolean isAbsolute() {
        return BigScalar.isAbsolute(myNumber);
    }

    public boolean isInfinite() {
        return BigScalar.IS_INFINITE;
    }

    public boolean isNaN() {
        return BigScalar.IS_NOT_A_NUMBER;
    }

    public boolean isPositive() {
        return BigScalar.isPositive(myNumber);
    }

    public boolean isReal() {
        return BigScalar.IS_REAL;
    }

    public boolean isZero() {
        return BigScalar.isZero(myNumber);
    }

    @Override
    public long longValue() {
        return myNumber.longValueExact();
    }

    public BigScalar multiply(final BigDecimal arg) {
        return new BigScalar(myNumber.multiply(arg));
    }

    public Scalar<BigDecimal> multiply(final double arg) {
        return this.multiply(new BigDecimal(arg));
    }

    public BigScalar negate() {
        return new BigScalar(myNumber.negate());
    }

    public double norm() {
        return Math.abs(myNumber.doubleValue());
    }

    public BigScalar signum() {
        return new BigScalar(BigFunction.SIGNUM.invoke(myNumber));
    }

    public BigScalar subtract(final BigDecimal arg) {
        return new BigScalar(myNumber.subtract(arg));
    }

    public Scalar<BigDecimal> subtract(final double arg) {
        return this.subtract(new BigDecimal(arg));
    }

    public BigDecimal toBigDecimal() {
        return myNumber;
    }

    @Override
    public String toString() {
        return myNumber.toString();
    }

    public String toString(final NumberContext context) {
        return context.enforce(myNumber).toString();
    }
}
