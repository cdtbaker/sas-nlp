package org.ojalgo.type.context;
import java.text.Format;
/** 
 * A type context provides two basic services:
 * <ol>
 * <li>It enforces some sort of rule/limit regarding size, accuracy or similar. This feature is useful when
 * writing/reading data to/from a database where attributes are often very specifically typed. "enforcing" is typically
 * a one-way operation that cannot be undone.</li>
 * <li>It translates back and forth between some specific type and {@linkplain String} - essentially a{@linkplain Format}.</li>
 * </ol>
 * @author apete
 */
public interface TypeContext<T> {
  /** 
 * Will force the object to conform to the context's specification.
 * @param object
 * @return
 */
  public abstract T enforce(  T object);
  public abstract String format(  Object object);
  public abstract T parse(  String string);
}
