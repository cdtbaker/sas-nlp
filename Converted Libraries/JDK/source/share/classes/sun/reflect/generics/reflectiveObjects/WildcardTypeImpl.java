package sun.reflect.generics.reflectiveObjects;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import sun.reflect.generics.factory.GenericsFactory;
import sun.reflect.generics.tree.FieldTypeSignature;
import sun.reflect.generics.visitor.Reifier;
import java.util.Arrays;
/** 
 * Implementation of WildcardType interface for core reflection.
 */
public class WildcardTypeImpl extends LazyReflectiveObjectGenerator implements WildcardType {
  private Type[] upperBounds;
  private Type[] lowerBounds;
  private FieldTypeSignature[] upperBoundASTs;
  private FieldTypeSignature[] lowerBoundASTs;
  private WildcardTypeImpl(  FieldTypeSignature[] ubs,  FieldTypeSignature[] lbs,  GenericsFactory f){
    super(f);
    upperBoundASTs=ubs;
    lowerBoundASTs=lbs;
  }
  /** 
 * Factory method.
 * @param ubs - an array of ASTs representing the upper bounds for the type
 * variable to be created
 * @param lbs - an array of ASTs representing the lower bounds for the type
 * variable to be created
 * @param f - a factory that can be used to manufacture reflective
 * objects that represent the bounds of this wildcard type
 * @return a wild card type with the requested bounds and factory
 */
  public static WildcardTypeImpl make(  FieldTypeSignature[] ubs,  FieldTypeSignature[] lbs,  GenericsFactory f){
    return new WildcardTypeImpl(ubs,lbs,f);
  }
  private FieldTypeSignature[] getUpperBoundASTs(){
    assert (upperBounds == null);
    return upperBoundASTs;
  }
  private FieldTypeSignature[] getLowerBoundASTs(){
    assert (lowerBounds == null);
    return lowerBoundASTs;
  }
  /** 
 * Returns an array of <tt>Type</tt> objects representing the  upper
 * bound(s) of this type variable.  Note that if no upper bound is
 * explicitly declared, the upper bound is <tt>Object</tt>.
 * <p>For each upper bound B :
 * <ul>
 * <li>if B is a parameterized type or a type variable, it is created,
 * (see {@link #ParameterizedType} for the details of the creation
 * process for parameterized types).
 * <li>Otherwise, B is resolved.
 * </ul>
 * @return an array of Types representing the upper bound(s) of this
 * type variable
 * @throws <tt>TypeNotPresentException</tt> if any of the
 * bounds refers to a non-existent type declaration
 * @throws <tt>MalformedParameterizedTypeException</tt> if any of the
 * bounds refer to a parameterized type that cannot be instantiated
 * for any reason
 */
  public Type[] getUpperBounds(){
    if (upperBounds == null) {
      FieldTypeSignature[] fts=getUpperBoundASTs();
      Type[] ts=new Type[fts.length];
      for (int j=0; j < fts.length; j++) {
        Reifier r=getReifier();
        fts[j].accept(r);
        ts[j]=r.getResult();
      }
      upperBounds=ts;
    }
    return upperBounds.clone();
  }
  /** 
 * Returns an array of <tt>Type</tt> objects representing the
 * lower bound(s) of this type variable.  Note that if no lower bound is
 * explicitly declared, the lower bound is the type of <tt>null</tt>.
 * In this case, a zero length array is returned.
 * <p>For each lower bound B :
 * <ul>
 * <li>if B is a parameterized type or a type variable, it is created,
 * (see {@link #ParameterizedType} for the details of the creation
 * process for parameterized types).
 * <li>Otherwise, B is resolved.
 * </ul>
 * @return an array of Types representing the lower bound(s) of this
 * type variable
 * @throws <tt>TypeNotPresentException</tt> if any of the
 * bounds refers to a non-existent type declaration
 * @throws <tt>MalformedParameterizedTypeException</tt> if any of the
 * bounds refer to a parameterized type that cannot be instantiated
 * for any reason
 */
  public Type[] getLowerBounds(){
    if (lowerBounds == null) {
      FieldTypeSignature[] fts=getLowerBoundASTs();
      Type[] ts=new Type[fts.length];
      for (int j=0; j < fts.length; j++) {
        Reifier r=getReifier();
        fts[j].accept(r);
        ts[j]=r.getResult();
      }
      lowerBounds=ts;
    }
    return lowerBounds.clone();
  }
  public String toString(){
    Type[] lowerBounds=getLowerBounds();
    Type[] bounds=lowerBounds;
    StringBuilder sb=new StringBuilder();
    if (lowerBounds.length > 0)     sb.append("? super ");
 else {
      Type[] upperBounds=getUpperBounds();
      if (upperBounds.length > 0 && !upperBounds[0].equals(Object.class)) {
        bounds=upperBounds;
        sb.append("? extends ");
      }
 else       return "?";
    }
    assert bounds.length > 0;
    boolean first=true;
    for (    Type bound : bounds) {
      if (!first)       sb.append(" & ");
      first=false;
      if (bound instanceof Class)       sb.append(((Class)bound).getName());
 else       sb.append(bound.toString());
    }
    return sb.toString();
  }
  @Override public boolean equals(  Object o){
    if (o instanceof WildcardType) {
      WildcardType that=(WildcardType)o;
      return Arrays.equals(this.getLowerBounds(),that.getLowerBounds()) && Arrays.equals(this.getUpperBounds(),that.getUpperBounds());
    }
 else     return false;
  }
  @Override public int hashCode(){
    Type[] lowerBounds=getLowerBounds();
    Type[] upperBounds=getUpperBounds();
    return Arrays.hashCode(lowerBounds) ^ Arrays.hashCode(upperBounds);
  }
}
