package sun.font;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.ref.SoftReference;
import java.util.concurrent.ConcurrentHashMap;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.swing.plaf.FontUIResource;
import sun.util.logging.PlatformLogger;
/** 
 * A collection of utility methods.
 */
public final class FontUtilities {
  public static boolean isSolaris;
  public static boolean isLinux;
  public static boolean isSolaris8;
  public static boolean isSolaris9;
  public static boolean isOpenSolaris;
  public static boolean useT2K;
  public static boolean isWindows;
  public static boolean isOpenJDK;
  static final String LUCIDA_FILE_NAME="LucidaSansRegular.ttf";
  private static boolean debugFonts=false;
  private static PlatformLogger logger=null;
  private static boolean logging;
static {
    AccessController.doPrivileged(new PrivilegedAction(){
      public Object run(){
        String osName=System.getProperty("os.name","unknownOS");
        isSolaris=osName.startsWith("SunOS");
        isLinux=osName.startsWith("Linux");
        String t2kStr=System.getProperty("sun.java2d.font.scaler");
        if (t2kStr != null) {
          useT2K="t2k".equals(t2kStr);
        }
 else {
          useT2K=false;
        }
        if (isSolaris) {
          String version=System.getProperty("os.version","0.0");
          isSolaris8=version.startsWith("5.8");
          isSolaris9=version.startsWith("5.9");
          float ver=Float.parseFloat(version);
          if (ver > 5.10f) {
            File f=new File("/etc/release");
            String line=null;
            try {
              FileInputStream fis=new FileInputStream(f);
              InputStreamReader isr=new InputStreamReader(fis,"ISO-8859-1");
              BufferedReader br=new BufferedReader(isr);
              line=br.readLine();
              fis.close();
            }
 catch (            Exception ex) {
            }
            if (line != null && line.indexOf("OpenSolaris") >= 0) {
              isOpenSolaris=true;
            }
 else {
              isOpenSolaris=false;
            }
          }
 else {
            isOpenSolaris=false;
          }
        }
 else {
          isSolaris8=false;
          isSolaris9=false;
          isOpenSolaris=false;
        }
        isWindows=osName.startsWith("Windows");
        String jreLibDirName=System.getProperty("java.home","") + File.separator + "lib";
        String jreFontDirName=jreLibDirName + File.separator + "fonts";
        File lucidaFile=new File(jreFontDirName + File.separator + LUCIDA_FILE_NAME);
        isOpenJDK=!lucidaFile.exists();
        String debugLevel=System.getProperty("sun.java2d.debugfonts");
        if (debugLevel != null && !debugLevel.equals("false")) {
          debugFonts=true;
          logger=PlatformLogger.getLogger("sun.java2d");
          if (debugLevel.equals("warning")) {
            logger.setLevel(PlatformLogger.WARNING);
          }
 else           if (debugLevel.equals("severe")) {
            logger.setLevel(PlatformLogger.SEVERE);
          }
        }
        if (debugFonts) {
          logger=PlatformLogger.getLogger("sun.java2d");
          logging=logger.isEnabled();
        }
        return null;
      }
    }
);
  }
  /** 
 * Referenced by code in the JDK which wants to test for the
 * minimum char code for which layout may be required.
 * Note that even basic latin text can benefit from ligatures,
 * eg "ffi" but we presently apply those only if explicitly
 * requested with TextAttribute.LIGATURES_ON.
 * The value here indicates the lowest char code for which failing
 * to invoke layout would prevent acceptable rendering.
 */
  public static final int MIN_LAYOUT_CHARCODE=0x0300;
  /** 
 * Referenced by code in the JDK which wants to test for the
 * maximum char code for which layout may be required.
 * Note this does not account for supplementary characters
 * where the caller interprets 'layout' to mean any case where
 * one 'char' (ie the java type char) does not map to one glyph
 */
  public static final int MAX_LAYOUT_CHARCODE=0x206F;
  /** 
 * Calls the private getFont2D() method in java.awt.Font objects.
 * @param font the font object to call
 * @return the Font2D object returned by Font.getFont2D()
 */
  public static Font2D getFont2D(  Font font){
    return FontAccess.getFontAccess().getFont2D(font);
  }
  /** 
 * If there is anything in the text which triggers a case
 * where char->glyph does not map 1:1 in straightforward
 * left->right ordering, then this method returns true.
 * Scripts which might require it but are not treated as such
 * due to JDK implementations will not return true.
 * ie a 'true' return is an indication of the treatment by
 * the implementation.
 * Whether supplementary characters should be considered is dependent
 * on the needs of the caller. Since this method accepts the 'char' type
 * then such chars are always represented by a pair. From a rendering
 * perspective these will all (in the cases I know of) still be one
 * unicode character -> one glyph. But if a caller is using this to
 * discover any case where it cannot make naive assumptions about
 * the number of chars, and how to index through them, then it may
 * need the option to have a 'true' return in such a case.
 */
  public static boolean isComplexText(  char[] chs,  int start,  int limit){
    for (int i=start; i < limit; i++) {
      if (chs[i] < MIN_LAYOUT_CHARCODE) {
        continue;
      }
 else       if (isNonSimpleChar(chs[i])) {
        return true;
      }
    }
    return false;
  }
  public static boolean isNonSimpleChar(  char ch){
    return isComplexCharCode(ch) || (ch >= CharToGlyphMapper.HI_SURROGATE_START && ch <= CharToGlyphMapper.LO_SURROGATE_END);
  }
  public static boolean isComplexCharCode(  int code){
    if (code < MIN_LAYOUT_CHARCODE || code > MAX_LAYOUT_CHARCODE) {
      return false;
    }
 else     if (code <= 0x036f) {
      return true;
    }
 else     if (code < 0x0590) {
      return false;
    }
 else     if (code <= 0x06ff) {
      return true;
    }
 else     if (code < 0x0900) {
      return false;
    }
 else     if (code <= 0x0e7f) {
      return true;
    }
 else     if (code < 0x0f00) {
      return false;
    }
 else     if (code <= 0x0fff) {
      return true;
    }
 else     if (code < 0x1100) {
      return false;
    }
 else     if (code < 0x11ff) {
      return true;
    }
 else     if (code < 0x1780) {
      return false;
    }
 else     if (code <= 0x17ff) {
      return true;
    }
 else     if (code < 0x200c) {
      return false;
    }
 else     if (code <= 0x200d) {
      return true;
    }
 else     if (code >= 0x202a && code <= 0x202e) {
      return true;
    }
 else     if (code >= 0x206a && code <= 0x206f) {
      return true;
    }
    return false;
  }
  public static PlatformLogger getLogger(){
    return logger;
  }
  public static boolean isLogging(){
    return logging;
  }
  public static boolean debugFonts(){
    return debugFonts;
  }
  public static boolean fontSupportsDefaultEncoding(  Font font){
    return getFont2D(font) instanceof CompositeFont;
  }
  /** 
 * This method is provided for internal and exclusive use by Swing.
 * It may be used in conjunction with fontSupportsDefaultEncoding(Font)
 * In the event that a desktop properties font doesn't directly
 * support the default encoding, (ie because the host OS supports
 * adding support for the current locale automatically for native apps),
 * then Swing calls this method to get a font which  uses the specified
 * font for the code points it covers, but also supports this locale
 * just as the standard composite fonts do.
 * Note: this will over-ride any setting where an application
 * specifies it prefers locale specific composite fonts.
 * The logic for this, is that this method is used only where the user or
 * application has specified that the native L&F be used, and that
 * we should honour that request to use the same font as native apps use.
 * The behaviour of this method is to construct a new composite
 * Font object that uses the specified physical font as its first
 * component, and adds all the components of "dialog" as fall back
 * components.
 * The method currently assumes that only the size and style attributes
 * are set on the specified font. It doesn't copy the font transform or
 * other attributes because they aren't set on a font created from
 * the desktop. This will need to be fixed if use is broadened.
 * Operations such as Font.deriveFont will work properly on the
 * font returned by this method for deriving a different point size.
 * Additionally it tries to support a different style by calling
 * getNewComposite() below. That also supports replacing slot zero
 * with a different physical font but that is expected to be "rare".
 * Deriving with a different style is needed because its been shown
 * that some applications try to do this for Swing FontUIResources.
 * Also operations such as new Font(font.getFontName(..), Font.PLAIN, 14);
 * will NOT yield the same result, as the new underlying CompositeFont
 * cannot be "looked up" in the font registry.
 * This returns a FontUIResource as that is the Font sub-class needed
 * by Swing.
 * Suggested usage is something like :
 * FontUIResource fuir;
 * Font desktopFont = getDesktopFont(..);
 * // NOTE even if fontSupportsDefaultEncoding returns true because
 * // you get Tahoma and are running in an English locale, you may
 * // still want to just call getCompositeFontUIResource() anyway
 * // as only then will you get fallback fonts - eg for CJK.
 * if (FontManager.fontSupportsDefaultEncoding(desktopFont)) {
 * fuir = new FontUIResource(..);
 * } else {
 * fuir = FontManager.getCompositeFontUIResource(desktopFont);
 * }
 * return fuir;
 */
  private static volatile SoftReference<ConcurrentHashMap<PhysicalFont,CompositeFont>> compMapRef=new SoftReference(null);
  public static FontUIResource getCompositeFontUIResource(  Font font){
    FontUIResource fuir=new FontUIResource(font);
    Font2D font2D=FontUtilities.getFont2D(font);
    if (!(font2D instanceof PhysicalFont)) {
      return fuir;
    }
    FontManager fm=FontManagerFactory.getInstance();
    CompositeFont dialog2D=(CompositeFont)fm.findFont2D("dialog",font.getStyle(),FontManager.NO_FALLBACK);
    if (dialog2D == null) {
      return fuir;
    }
    PhysicalFont physicalFont=(PhysicalFont)font2D;
    ConcurrentHashMap<PhysicalFont,CompositeFont> compMap=compMapRef.get();
    if (compMap == null) {
      compMap=new ConcurrentHashMap<PhysicalFont,CompositeFont>();
      compMapRef=new SoftReference(compMap);
    }
    CompositeFont compFont=compMap.get(physicalFont);
    if (compFont == null) {
      compFont=new CompositeFont(physicalFont,dialog2D);
      compMap.put(physicalFont,compFont);
    }
    FontAccess.getFontAccess().setFont2D(fuir,compFont.handle);
    FontAccess.getFontAccess().setCreatedFont(fuir);
    return fuir;
  }
  private static final String[][] nameMap={{"sans","sansserif"},{"sans-serif","sansserif"},{"serif","serif"},{"monospace","monospaced"}};
  public static String mapFcName(  String name){
    for (int i=0; i < nameMap.length; i++) {
      if (name.equals(nameMap[i][0])) {
        return nameMap[i][1];
      }
    }
    return null;
  }
  public static FontUIResource getFontConfigFUIR(  String fcFamily,  int style,  int size){
    String mapped=mapFcName(fcFamily);
    if (mapped == null) {
      mapped="sansserif";
    }
    FontUIResource fuir;
    FontManager fm=FontManagerFactory.getInstance();
    if (fm instanceof SunFontManager) {
      SunFontManager sfm=(SunFontManager)fm;
      fuir=sfm.getFontConfigFUIR(mapped,style,size);
    }
 else {
      fuir=new FontUIResource(mapped,style,size);
    }
    return fuir;
  }
  /** 
 * Used by windows printing to assess if a font is likely to
 * be layout compatible with JDK
 * TrueType fonts should be, but if they have no GPOS table,
 * but do have a GSUB table, then they are probably older
 * fonts GDI handles differently.
 */
  public static boolean textLayoutIsCompatible(  Font font){
    Font2D font2D=getFont2D(font);
    if (font2D instanceof TrueTypeFont) {
      TrueTypeFont ttf=(TrueTypeFont)font2D;
      return ttf.getDirectoryEntry(TrueTypeFont.GSUBTag) == null || ttf.getDirectoryEntry(TrueTypeFont.GPOSTag) != null;
    }
 else {
      return false;
    }
  }
}