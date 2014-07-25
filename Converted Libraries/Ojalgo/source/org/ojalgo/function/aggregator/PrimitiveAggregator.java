package org.ojalgo.function.aggregator;
import static org.ojalgo.constant.PrimitiveMath.*;
import org.ojalgo.ProgrammingError;
import org.ojalgo.scalar.PrimitiveScalar;
import org.ojalgo.scalar.Scalar;
import org.ojalgo.type.TypeUtils;
public abstract class PrimitiveAggregator {
  public static final ThreadLocal<AggregatorFunction<Double>> CARDINALITY=new ThreadLocal<AggregatorFunction<Double>>(){
    @Override protected AggregatorFunction<Double> initialValue(){
      return new AggregatorFunction<Double>(){
        private int myCount=0;
        public double doubleValue(){
          return myCount;
        }
        public Double getNumber(){
          return Double.valueOf(this.doubleValue());
        }
        public int intValue(){
          return myCount;
        }
        public void invoke(        final double anArg){
          if (!TypeUtils.isZero(anArg)) {
            myCount++;
          }
        }
        public void invoke(        final Double anArg){
          this.invoke(anArg.doubleValue());
        }
        public void merge(        final Double anArg){
          myCount+=anArg.intValue();
        }
        public Double merge(        final Double aResult1,        final Double aResult2){
          return (double)(aResult1.intValue() + aResult2.intValue());
        }
        public AggregatorFunction<Double> reset(){
          myCount=0;
          return this;
        }
        public Scalar<Double> toScalar(){
          return new PrimitiveScalar(this.doubleValue());
        }
      }
;
    }
  }
;
  public static final ThreadLocal<AggregatorFunction<Double>> LARGEST=new ThreadLocal<AggregatorFunction<Double>>(){
    @Override protected AggregatorFunction<Double> initialValue(){
      return new AggregatorFunction<Double>(){
        private double myValue=ZERO;
        public double doubleValue(){
          return myValue;
        }
        public Double getNumber(){
          return Double.valueOf(this.doubleValue());
        }
        public int intValue(){
          return (int)this.doubleValue();
        }
        public void invoke(        final double anArg){
          myValue=Math.max(myValue,Math.abs(anArg));
        }
        public void invoke(        final Double anArg){
          this.invoke(anArg.doubleValue());
        }
        public void merge(        final Double anArg){
          this.invoke(anArg.doubleValue());
        }
        public Double merge(        final Double aResult1,        final Double aResult2){
          return Math.max(aResult1,aResult2);
        }
        public AggregatorFunction<Double> reset(){
          myValue=ZERO;
          return this;
        }
        public Scalar<Double> toScalar(){
          return new PrimitiveScalar(this.doubleValue());
        }
      }
;
    }
  }
;
  public static final ThreadLocal<AggregatorFunction<Double>> MAX=new ThreadLocal<AggregatorFunction<Double>>(){
    @Override protected AggregatorFunction<Double> initialValue(){
      return new AggregatorFunction<Double>(){
        private double myValue=ZERO;
        public double doubleValue(){
          return myValue;
        }
        public Double getNumber(){
          return Double.valueOf(this.doubleValue());
        }
        public int intValue(){
          return (int)this.doubleValue();
        }
        public void invoke(        final double anArg){
          myValue=Math.max(myValue,anArg);
        }
        public void invoke(        final Double anArg){
          this.invoke(anArg.doubleValue());
        }
        public void merge(        final Double anArg){
          this.invoke(anArg.doubleValue());
        }
        public Double merge(        final Double aResult1,        final Double aResult2){
          return Math.max(aResult1,aResult2);
        }
        public AggregatorFunction<Double> reset(){
          myValue=ZERO;
          return this;
        }
        public Scalar<Double> toScalar(){
          return new PrimitiveScalar(this.doubleValue());
        }
      }
;
    }
  }
;
  public static final ThreadLocal<AggregatorFunction<Double>> NORM1=new ThreadLocal<AggregatorFunction<Double>>(){
    @Override protected AggregatorFunction<Double> initialValue(){
      return new AggregatorFunction<Double>(){
        private double myValue=ZERO;
        public double doubleValue(){
          return myValue;
        }
        public Double getNumber(){
          return Double.valueOf(this.doubleValue());
        }
        public int intValue(){
          return (int)this.doubleValue();
        }
        public void invoke(        final double anArg){
          myValue+=Math.abs(anArg);
        }
        public void invoke(        final Double anArg){
          this.invoke(anArg.doubleValue());
        }
        public void merge(        final Double anArg){
          this.invoke(anArg.doubleValue());
        }
        public Double merge(        final Double aResult1,        final Double aResult2){
          return Math.abs(aResult1) + Math.abs(aResult2);
        }
        public AggregatorFunction<Double> reset(){
          myValue=ZERO;
          return this;
        }
        public Scalar<Double> toScalar(){
          return new PrimitiveScalar(this.doubleValue());
        }
      }
;
    }
  }
;
  public static final ThreadLocal<AggregatorFunction<Double>> NORM2=new ThreadLocal<AggregatorFunction<Double>>(){
    @Override protected AggregatorFunction<Double> initialValue(){
      return new AggregatorFunction<Double>(){
        private double myValue=ZERO;
        public double doubleValue(){
          return Math.sqrt(myValue);
        }
        public Double getNumber(){
          return Double.valueOf(this.doubleValue());
        }
        public int intValue(){
          return (int)this.doubleValue();
        }
        public void invoke(        final double anArg){
          myValue+=anArg * anArg;
        }
        public void invoke(        final Double anArg){
          this.invoke(anArg.doubleValue());
        }
        public void merge(        final Double anArg){
          this.invoke(anArg.doubleValue());
        }
        public Double merge(        final Double aResult1,        final Double aResult2){
          return Math.hypot(aResult1,aResult2);
        }
        public AggregatorFunction<Double> reset(){
          myValue=ZERO;
          return this;
        }
        public Scalar<Double> toScalar(){
          return new PrimitiveScalar(this.doubleValue());
        }
      }
;
    }
  }
;
  public static final ThreadLocal<AggregatorFunction<Double>> PRODUCT=new ThreadLocal<AggregatorFunction<Double>>(){
    @Override protected AggregatorFunction<Double> initialValue(){
      return new AggregatorFunction<Double>(){
        private double myValue=ONE;
        public double doubleValue(){
          return myValue;
        }
        public Double getNumber(){
          return Double.valueOf(this.doubleValue());
        }
        public int intValue(){
          return (int)this.doubleValue();
        }
        public void invoke(        final double anArg){
          myValue*=anArg;
        }
        public void invoke(        final Double anArg){
          this.invoke(anArg.doubleValue());
        }
        public void merge(        final Double anArg){
          this.invoke(anArg.doubleValue());
        }
        public Double merge(        final Double aResult1,        final Double aResult2){
          return aResult1 * aResult2;
        }
        public AggregatorFunction<Double> reset(){
          myValue=ONE;
          return this;
        }
        public Scalar<Double> toScalar(){
          return new PrimitiveScalar(this.doubleValue());
        }
      }
;
    }
  }
;
  public static final ThreadLocal<AggregatorFunction<Double>> PRODUCT2=new ThreadLocal<AggregatorFunction<Double>>(){
    @Override protected AggregatorFunction<Double> initialValue(){
      return new AggregatorFunction<Double>(){
        private double myValue=ONE;
        public double doubleValue(){
          return myValue;
        }
        public Double getNumber(){
          return Double.valueOf(this.doubleValue());
        }
        public int intValue(){
          return (int)this.doubleValue();
        }
        public void invoke(        final double anArg){
          myValue*=anArg * anArg;
        }
        public void invoke(        final Double anArg){
          this.invoke(anArg.doubleValue());
        }
        public void merge(        final Double anArg){
          myValue*=anArg;
        }
        public Double merge(        final Double aResult1,        final Double aResult2){
          return aResult1 * aResult2;
        }
        public AggregatorFunction<Double> reset(){
          myValue=ONE;
          return this;
        }
        public Scalar<Double> toScalar(){
          return new PrimitiveScalar(this.doubleValue());
        }
      }
;
    }
  }
;
  public static final ThreadLocal<AggregatorFunction<Double>> SMALLEST=new ThreadLocal<AggregatorFunction<Double>>(){
    @Override protected AggregatorFunction<Double> initialValue(){
      return new AggregatorFunction<Double>(){
        private double myValue=POSITIVE_INFINITY;
        public double doubleValue(){
          if (Double.isInfinite(myValue)) {
            return ZERO;
          }
 else {
            return myValue;
          }
        }
        public Double getNumber(){
          return Double.valueOf(this.doubleValue());
        }
        public int intValue(){
          return (int)this.doubleValue();
        }
        public void invoke(        final double anArg){
          final double tmpArg=Math.abs(anArg);
          if (tmpArg >= IS_ZERO) {
            myValue=Math.min(myValue,tmpArg);
          }
        }
        public void invoke(        final Double anArg){
          this.invoke(anArg.doubleValue());
        }
        public void merge(        final Double anArg){
          this.invoke(anArg.doubleValue());
        }
        public Double merge(        final Double aResult1,        final Double aResult2){
          return Math.min(aResult1,aResult2);
        }
        public AggregatorFunction<Double> reset(){
          myValue=POSITIVE_INFINITY;
          return this;
        }
        public Scalar<Double> toScalar(){
          return new PrimitiveScalar(this.doubleValue());
        }
      }
;
    }
  }
;
  public static final ThreadLocal<AggregatorFunction<Double>> MIN=new ThreadLocal<AggregatorFunction<Double>>(){
    @Override protected AggregatorFunction<Double> initialValue(){
      return new AggregatorFunction<Double>(){
        private double myValue=POSITIVE_INFINITY;
        public double doubleValue(){
          if (Double.isInfinite(myValue)) {
            return ZERO;
          }
 else {
            return myValue;
          }
        }
        public Double getNumber(){
          return Double.valueOf(this.doubleValue());
        }
        public int intValue(){
          return (int)this.doubleValue();
        }
        public void invoke(        final double anArg){
          myValue=Math.min(myValue,anArg);
        }
        public void invoke(        final Double anArg){
          this.invoke(anArg.doubleValue());
        }
        public void merge(        final Double anArg){
          this.invoke(anArg.doubleValue());
        }
        public Double merge(        final Double aResult1,        final Double aResult2){
          return Math.min(aResult1,aResult2);
        }
        public AggregatorFunction<Double> reset(){
          myValue=POSITIVE_INFINITY;
          return this;
        }
        public Scalar<Double> toScalar(){
          return new PrimitiveScalar(this.doubleValue());
        }
      }
;
    }
  }
;
  public static final ThreadLocal<AggregatorFunction<Double>> SUM=new ThreadLocal<AggregatorFunction<Double>>(){
    @Override protected AggregatorFunction<Double> initialValue(){
      return new AggregatorFunction<Double>(){
        private double myValue=ZERO;
        public double doubleValue(){
          return myValue;
        }
        public Double getNumber(){
          return Double.valueOf(this.doubleValue());
        }
        public int intValue(){
          return (int)this.doubleValue();
        }
        public void invoke(        final double anArg){
          myValue+=anArg;
        }
        public void invoke(        final Double anArg){
          this.invoke(anArg.doubleValue());
        }
        public void merge(        final Double anArg){
          this.invoke(anArg.doubleValue());
        }
        public Double merge(        final Double aResult1,        final Double aResult2){
          return aResult1 + aResult2;
        }
        public AggregatorFunction<Double> reset(){
          myValue=ZERO;
          return this;
        }
        public Scalar<Double> toScalar(){
          return new PrimitiveScalar(this.doubleValue());
        }
      }
;
    }
  }
;
  public static final ThreadLocal<AggregatorFunction<Double>> SUM2=new ThreadLocal<AggregatorFunction<Double>>(){
    @Override protected AggregatorFunction<Double> initialValue(){
      return new AggregatorFunction<Double>(){
        private double myValue=ZERO;
        public double doubleValue(){
          return myValue;
        }
        public Double getNumber(){
          return Double.valueOf(this.doubleValue());
        }
        public int intValue(){
          return (int)this.doubleValue();
        }
        public void invoke(        final double anArg){
          myValue+=anArg * anArg;
        }
        public void invoke(        final Double anArg){
          this.invoke(anArg.doubleValue());
        }
        public void merge(        final Double anArg){
          myValue+=anArg;
        }
        public Double merge(        final Double aResult1,        final Double aResult2){
          return aResult1 + aResult2;
        }
        public AggregatorFunction<Double> reset(){
          myValue=ZERO;
          return this;
        }
        public Scalar<Double> toScalar(){
          return new PrimitiveScalar(this.doubleValue());
        }
      }
;
    }
  }
;
  private static final AggregatorCollection<Double> COLLECTION=new AggregatorCollection<Double>(){
    @Override public AggregatorFunction<Double> cardinality(){
      return CARDINALITY.get().reset();
    }
    @Override public AggregatorFunction<Double> largest(){
      return LARGEST.get().reset();
    }
    @Override public AggregatorFunction<Double> maximum(){
      return MAX.get().reset();
    }
    @Override public AggregatorFunction<Double> minimum(){
      return MIN.get().reset();
    }
    @Override public AggregatorFunction<Double> norm1(){
      return NORM1.get().reset();
    }
    @Override public AggregatorFunction<Double> norm2(){
      return NORM2.get().reset();
    }
    @Override public AggregatorFunction<Double> product(){
      return PRODUCT.get().reset();
    }
    @Override public AggregatorFunction<Double> product2(){
      return PRODUCT2.get().reset();
    }
    @Override public AggregatorFunction<Double> smallest(){
      return SMALLEST.get().reset();
    }
    @Override public AggregatorFunction<Double> sum(){
      return SUM.get().reset();
    }
    @Override public AggregatorFunction<Double> sum2(){
      return SUM2.get().reset();
    }
  }
;
  public static AggregatorCollection<Double> getCollection(){
    return COLLECTION;
  }
  private PrimitiveAggregator(){
    super();
    ProgrammingError.throwForIllegalInvocation();
  }
}
