package sun.print;
import javax.print.attribute.PrintRequestAttribute;
import javax.print.attribute.standard.Media;
public class SunAlternateMedia implements PrintRequestAttribute {
  private static final long serialVersionUID=-8878868345472850201L;
  private Media media;
  public SunAlternateMedia(  Media altMedia){
    media=altMedia;
  }
  public Media getMedia(){
    return media;
  }
  public final Class getCategory(){
    return SunAlternateMedia.class;
  }
  public final String getName(){
    return "sun-alternate-media";
  }
  public String toString(){
    return "alternate-media: " + media.toString();
  }
  /** 
 * Returns a hash code value for this enumeration value. The hash code is
 * just this enumeration value's integer value.
 */
  public int hashCode(){
    return media.hashCode();
  }
}
