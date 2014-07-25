package sun.print;
public interface SunPrinterJobService {
  /** 
 * This returns true if this service is implemented using the
 * platform's built-in subclass of PrinterJob.
 * ie the same class as the caller.
 */
  public boolean usesClass(  Class c);
}
