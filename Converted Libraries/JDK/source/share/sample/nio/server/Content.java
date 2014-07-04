/** 
 * An Sendable interface extension that adds additional
 * methods for additional information, such as Files
 * or Strings.
 * @author Mark Reinhold
 * @author Brad R. Wetmore
 */
interface Content extends Sendable {
  String type();
  long length();
}
